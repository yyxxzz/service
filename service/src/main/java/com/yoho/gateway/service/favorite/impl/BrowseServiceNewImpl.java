package com.yoho.gateway.service.favorite.impl;

import com.netflix.config.DynamicPropertyFactory;
import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.core.redis.YHValueOperations;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.client.hystrix.AsyncFuture;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.browse.BrowseReqVO;
import com.yoho.gateway.model.browse.CategoryVO;
import com.yoho.gateway.model.browse.ProductBrowseRespVO;
import com.yoho.gateway.model.browse.ProductVO;
import com.yoho.gateway.service.favorite.IBrowseServiceNew;
import com.yoho.gateway.utils.constants.CacheKeyConstants;
import com.yoho.product.model.BrowseRespBO;
import com.yoho.product.model.CategoryBo;
import com.yoho.product.request.search.BaseSearchRequest;
import com.yoho.service.model.brower.BrowseOperation;
import com.yoho.service.model.brower.response.PageBrowerSKNRespBO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class BrowseServiceNewImpl implements IBrowseServiceNew {
    private static Logger logger = LoggerFactory.getLogger(BrowseServiceNewImpl.class);

    @Autowired
    private ServiceCaller serviceCaller;

    @Resource(name = "yhRedisTemplate")
    private YHRedisTemplate<String, String> myRedisTemplate;

    @Resource(name = "yhValueOperations")
    private YHValueOperations<String, String> valueOperations;

    // 商品分类服务
    private static final String PRODUCT_CATEGORY_SERVICE = "product.querymiddleCategoryList";

    // 浏览商品信息服务
    private static final String PRODUCT_BROWSE_SERVICE = "product.queryBrowserProduct";

    /**
     * 获取浏览记录的列表
     *
     * @param browseReqVO
     * @return
     * @throws GatewayException
     */
    @Override
    public ProductBrowseRespVO listBrowse(BrowseReqVO browseReqVO) throws GatewayException {
        logger.info("Enter listBrowse. browseReqVO is {}", browseReqVO);

        // (1)UID的初始化, 并校验UID的值
        int uid = null == browseReqVO ? 0 : browseReqVO.getUid();
        if (1 > uid) {
            logger.warn("listBrowse: uid is null.");
            throw new GatewayException(500, "请先登录");
        }

        // (2)请求服务, 请求商品的分类信息, 异步调用, 提高请求的效率
        AsyncFuture<CategoryBo[]> categoryBoAsync = serviceCaller.asyncCall(PRODUCT_CATEGORY_SERVICE, "", CategoryBo[].class);

        // (3)调用浏览记录微服务，获取skn列表的分页信息
        PageBrowerSKNRespBO pageBrowerSKNRespBO = serviceCaller.post("browse.listBrowse", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "?method=app.brower.product", browseReqVO, PageBrowerSKNRespBO.class, null).get();

        // (4)组织skn数据，查询商品信息
        List<BrowseOperation> list = (null == pageBrowerSKNRespBO || null == pageBrowerSKNRespBO.getList() || pageBrowerSKNRespBO.getList().isEmpty()) ? new ArrayList<>() : pageBrowerSKNRespBO.getList();
        List<Integer> sknList = new ArrayList<Integer>();
        for (BrowseOperation browseOperation : list) {
            if (null == browseOperation || StringUtils.isEmpty(browseOperation.getProduct_skn())) {
                continue;
            }
            sknList.add(new Integer(browseOperation.getProduct_skn()));
        }

        // (5)如果浏览记录为空, 直接返回空记录.
        int page_total = null == pageBrowerSKNRespBO ? 0 : pageBrowerSKNRespBO.getPage_total();
        int total = null == pageBrowerSKNRespBO ? 0 : pageBrowerSKNRespBO.getTotal();
        int page = null == pageBrowerSKNRespBO ? 0 : pageBrowerSKNRespBO.getPage();
        if (sknList.size() == 0) {
            ProductBrowseRespVO productBrowseRespVO = new ProductBrowseRespVO(new ArrayList<ProductVO>(), new ArrayList<CategoryVO>(), page_total, total, 0, page);
            return productBrowseRespVO;
        }

        // (6)根据商品的SKN列表,调用商品接口, 查询商品详情的信息
        BaseSearchRequest baseSearchRequest = new BaseSearchRequest();
        baseSearchRequest.setProductSkns(sknList);
        AsyncFuture<BrowseRespBO[]> browseRespBOAsync = serviceCaller.asyncCall(PRODUCT_BROWSE_SERVICE, baseSearchRequest, BrowseRespBO[].class);

        // (7)获取商品分类信息, 并将分类信息组装成MAP, KEY: 分类ID, Value: 分类信息对象CategoryBo
        Map<Integer, CategoryBo> categoryBoMap = getCategoryBoMap(categoryBoAsync);
        logger.debug("listBrowse: browseReqVO is {}, categoryBoMap is {}", browseReqVO, categoryBoMap);

        // (8)获取商品列表信息, 并将商品列表组装成MAP, KEY: SKN, Value: 商品的信息对象BrowseRespBO
        BrowseRespBO[] browseRespBOArr = (null != browseRespBOAsync) ? browseRespBOAsync.get() : null;
        Map<String, BrowseRespBO> browseRespBOMap = getBrowseRespBOMap(browseRespBOArr);

        // (10)遍历浏览记录中的所有的商品, 组装商品的信息, 以及商品的分类信息(除去不存在的商品的分类)
        List<ProductVO> productList = new ArrayList<ProductVO>();
        List<CategoryVO> categoryList = new ArrayList<CategoryVO>();
        for (BrowseOperation browseOperation : list) {
            if (null == browseOperation) {
                continue;
            }
            // (10.1)根据SKN获取商品的信息
            BrowseRespBO browseRespBO = browseRespBOMap.get(browseOperation.getProduct_skn());
            if (null == browseRespBO) {
                continue;
            }
            // (10.2)获取商品二级分类信息
            CategoryBo categoryBo = getCategoryBo(categoryBoMap, browseRespBO); // 获取商品的二级分类
            if (null != categoryBo) {
                CategoryVO categoryVO = new CategoryVO(categoryBo.getCategoryId(), categoryBo.getCategoryName());
                if (!categoryList.contains(categoryVO)) {
                    categoryList.add(categoryVO);
                }
            }
            // (4.2)组织商品信息
            String time = DateUtils.formatDate(new Date(browseOperation.getTime()), "yyyy-MM-dd HH:mm:ss");
            int category_id = null == categoryBo ? 0 : categoryBo.getCategoryId(); // 获取单个商品的二级分类,
            // 如果每页找到分类信息,
            // 则分类ID为O,
            // 只能展示在全部分类里面
            ProductVO productVO = new ProductVO(browseRespBO.getProductName(), browseRespBO.getProductId(), browseRespBO.getProductSkn(), browseRespBO.getImage(),
                    null == browseRespBO.getSalesPrice() ? 0 : browseRespBO.getSalesPrice(), null == browseRespBO.getMarketPrice() ? 0 : browseRespBO.getMarketPrice(), browseRespBO.getStatus(),
                    browseRespBO.getStorage(), category_id, time);
            productList.add(productVO);
        }
        logger.info("listBrowse: browseReqVO is {}, page_total is {}, total is {}, page is {}, productList size is {}, categoryList size is {}", browseReqVO, page_total, total, page,
                (null == productList) ? 0 : productList.size(), (null == categoryList) ? 0 : categoryList.size());

        // (11)组装返回参数给APP
        ProductBrowseRespVO productBrowseRespVO = new ProductBrowseRespVO(productList, categoryList, page_total, total, productList.size(), page);
        return productBrowseRespVO;
    }

    /**
     * 删除浏览记录
     *
     * @param browseReqVO
     * @throws GatewayException
     */
    @Override
    public void deleteBrowse(BrowseReqVO browseReqVO) throws GatewayException {
        logger.info("Enter deleteBrowse. browseReqVO is {}", browseReqVO);

        // (1)UID的初始化, 并校验UID的值
        int uid = null == browseReqVO ? 0 : browseReqVO.getUid();
        if (1 > uid) {
            logger.warn("deleteBrowse: param uid is null");
            throw new GatewayException(400, "用户ID错误");
        }

        // (2)调用浏览记录微服务，删除用户浏览记录
        serviceCaller.post("browse.deleteBrowse", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "?method=app.brower.delete", browseReqVO, Void.class, null).get();

        // 清理用户浏览数量缓存
        try {
            myRedisTemplate.delete(CacheKeyConstants.YHGW_PRODUCTBROWSENUM_PRE + uid);
        } catch (Exception e) {
            logger.warn("addBrowseBatch: delete browse num cache failed. error message is {}", e.getMessage());
        }
    }

    /**
     * 描述：批量新增浏览记录<br>
     * 业务流程：<br>
     * &nbsp;&nbsp;1.初始化参数：uid， browseList<br>
     * &nbsp;&nbsp;2.参数校验：uid， browseList不能为空<br>
     * &nbsp;&nbsp;3.获取用户浏览记录列表<br>
     * &nbsp;&nbsp;4.合并浏览记录<br>
     * &nbsp;&nbsp;5.清空用户浏览记录<br>
     * &nbsp;&nbsp;6.重新push用户浏览记录<br>
     * &nbsp;&nbsp;7.返回<br>
     *
     * @param browseReqVO {uid:"",browseList:[{product_skn:"",time:""}]}
     * @return
     * @throws GatewayException 400-用户id错误; 400-浏览记录列表为空
     */
    public Long addBrowseBatch(BrowseReqVO browseReqVO) throws GatewayException {
        logger.info("Enter addBrowseBatch. browseReqVO is {}", browseReqVO);

        // (1)参数初始化：uid， browseJSONStr
        int uid = 0;
        String browseJSONStr = "";
        if (null != browseReqVO) {
            uid = browseReqVO.getUid();
            browseJSONStr = browseReqVO.getBrowseList();
        }

        // (2)校验uid， browseList
        if (1 > uid) {
            logger.warn("addBrowseBatch: param uid is null");
            throw new GatewayException(400, "用户ID错误");
        }
        if (StringUtils.isEmpty(browseJSONStr)) {
            logger.warn("addBrowseBatch: param browseList is null");
            throw new GatewayException(400, "浏览记录列表为空");
        }

        // (3)调用浏览记录微服务，批量新增流浪记录
        Long size = serviceCaller.post("browse.addBrowseBatch", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "?method=app.brower.addBrowseBatch", browseReqVO, Long.class, null).get();

        // 清理用户浏览数量缓存
        try {
            myRedisTemplate.delete(CacheKeyConstants.YHGW_PRODUCTBROWSENUM_PRE + uid);
        } catch (Exception e) {
            logger.warn("addBrowseBatch: delete browse num cache failed. error message is {}", e.getMessage());
        }

        // (4)返回
        return size;
    }

    /**
     * 获取浏览记录的数量
     *
     * @param browseReqVO
     * @return
     * @throws GatewayException
     */
    @Override
    public int totalBrowse(BrowseReqVO browseReqVO) throws GatewayException {
        logger.info("Enter totalBrowse. browseReqVO is {}", browseReqVO);

        // (1)参数初始化：uid
        int uid = null == browseReqVO ? 0 : browseReqVO.getUid();

        // (2)缓存中取用户浏览记录数量
        String productBrowseNumStr = null;
        try {
            productBrowseNumStr = valueOperations.get(CacheKeyConstants.YHGW_PRODUCTBROWSENUM_PRE + uid);
        } catch (Exception e) {
            logger.warn("totalBrowse: get browse num from redis failed. uid is {}, error message is {}", uid, e.getMessage());
        }
        if (StringUtils.isNotBlank(productBrowseNumStr)) {
            return Integer.parseInt(productBrowseNumStr);
        }

        // (3)缓存未命中，调用浏览记录微服务获取用户浏览记录数量
        Integer productBrowseNum = serviceCaller.post("browse.totalBrowse", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "?method=app.brower.total", browseReqVO, Integer.class, null).get(1);

        // (4)反写redis
        try {
            valueOperations.set(CacheKeyConstants.YHGW_PRODUCTBROWSENUM_PRE + uid, String.valueOf(productBrowseNum));
            myRedisTemplate.longExpire(CacheKeyConstants.YHGW_PRODUCTBROWSENUM_PRE + uid, 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("totalBrowse: get browse num from redis failed. uid is {}, error message is {}", uid, e.getMessage());
        }

        // (5)返回
        return productBrowseNum;
    }

    // 组织CategoryBo list为Map
    private Map<Integer, CategoryBo> getCategoryBoMap(AsyncFuture<CategoryBo[]> categoryBoAsync) {
        Map<Integer, CategoryBo> categoryBoMap = new HashMap<Integer, CategoryBo>();
        if (null == categoryBoAsync) {
            return categoryBoMap;
        }
        CategoryBo[] categoryBoArr = categoryBoAsync.get();
        if (null == categoryBoArr || 0 == categoryBoArr.length) {
            return categoryBoMap;
        }
        for (CategoryBo categoryBo : categoryBoArr) {
            if (null == categoryBo) {
                continue;
            }
            categoryBoMap.put(categoryBo.getCategoryId(), categoryBo);
        }
        return categoryBoMap;
    }

    // 组织BrowseRespBO array为Map
    private Map<String, BrowseRespBO> getBrowseRespBOMap(BrowseRespBO[] browseRespBOArr) {
        Map<String, BrowseRespBO> browseRespBOMap = new HashMap<String, BrowseRespBO>();
        if (null == browseRespBOArr || 0 == browseRespBOArr.length) {
            return browseRespBOMap;
        }
        for (BrowseRespBO browseRespBO : browseRespBOArr) {
            if (null == browseRespBO) {
                continue;
            }
            browseRespBOMap.put(String.valueOf(browseRespBO.getProductSkn()), browseRespBO);
        }
        return browseRespBOMap;
    }

    // 获取商品二级分类
    private CategoryBo getCategoryBo(Map<Integer, CategoryBo> categoryBoMap, BrowseRespBO browseRespBO) {
        CategoryBo categoryBo = categoryBoMap.get(new Integer(browseRespBO.getMaxSortId()));
        if (null != categoryBo) {
            return categoryBo;
        }
        categoryBo = categoryBoMap.get(new Integer(browseRespBO.getMiddleSortId()));
        if (null != categoryBo) {
            return categoryBo;
        }
        categoryBo = categoryBoMap.get(new Integer(browseRespBO.getSmallSortId()));
        return categoryBo;
    }

}
