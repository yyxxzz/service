package com.yoho.gateway.service.search.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.exception.ServiceNotAvaibleException;
import com.yoho.core.rest.exception.ServiceNotFoundException;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.search.convert.SalesCategoryConvert;
import com.yoho.gateway.model.search.SalesCategoryVo;
import com.yoho.gateway.service.search.SortService;
import com.yoho.gateway.service.search.wrapper.SearchRestTemplateWrapper;
import com.yoho.gateway.utils.StringUtils;
import com.yoho.product.model.QuerySalesCategoryReq;
import com.yoho.product.model.SalesCategoryBo;
import com.yoho.product.model.SalesCategoryRspBo;

/**
 * 查询分组分类信息列表接口
 * @author mali
 *
 */
@Service
public class SortServiceImpl implements SortService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SortServiceImpl.class);

    // 搜索推荐URL的链接
    @Value("${ip.port.search.server}")
    private String searchServerIpAndPort;

    @Autowired
    private SearchRestTemplateWrapper searchRestTemplateWrapper;

    /**
     * 分类转换器
     */
    @Autowired
    private SalesCategoryConvert salesCategoryConvert;

    /**
     * http请求工具
     */
    @Autowired
    private ServiceCaller serviceCaller;

    /**
     * 查询分类信息
     * @param paramMap
     * @return  分类集合的JSON对象
     */
    @Override
    public Object getSortList(String searchFrom,String params) {
        LOGGER.info("search sort list begin. params : {}", params);

        String sortStr = null;

        // 发送HTTP请求
        String url = getUrl(params);
        try {
            sortStr = searchRestTemplateWrapper.getForObject(searchFrom,url, String.class, Maps.newHashMap());
        } catch (Exception e) {
            LOGGER.warn("The Result search sort list. url :{} " , url, e);
        }
        LOGGER.info("Method of get sort. params is：{}" , params);

        LOGGER.debug("The result of search sort list. url : {}, params : {}", url, params);

        Object filterSort = null;
        try {
            filterSort = getGroupSort(params, sortStr);
        } catch (Exception e) {
            LOGGER.warn("Resolve of result of search sort list find  .", e); // 此外为次要步骤，有异常无需抛出，不影响主查询流程
        }
        return filterSort;
    }

    private Object getGroupSort(String params, String sortStr) {
        JSONObject sortJson = JSONObject.parseObject(sortStr);
        if (null == sortJson) {
            LOGGER.warn("The Result of yohosearch/sortgroup.json is wrong. params is {}, sortStr is {}",params, sortStr);
            return null;
        }
        JSONObject data = sortJson.getJSONObject("data");
        if (null == data) {
            LOGGER.warn("The Result of yohosearch/sortgroup.json is wrong. params is {}, sortStr is {}", params, sortStr);
            return null;
        }

        return filterSort(data.getJSONArray("sort"));
    }

    /**
     * 物理分类转运营分类
     * @param sort 物理分类
     * @return 转换成运营分类列表，包含子分类结构
     */
    public Object filterSort(JSONArray sort) {
        if (null == sort) {
            LOGGER.warn("The Result of data.sort is null.");
            return null;
        }
        // 获取三级物理分类
        Map<String,Integer> physicalSort = getPhysicalSortMap(sort);

        // 获取所有的运营分类
        SalesCategoryRspBo responseBean = getAllSalesCategory();

        // 如果三级物理分类或者所有的运营分类都为空，则无需再进行处理，直接返回null
        if (physicalSort.isEmpty() || null == responseBean
                || responseBean.getData() == null || responseBean.getData().isEmpty()) {
            return null;
        }

        // 获取三级物理分类对应的三级运营分类
        LinkedHashSet<SalesCategoryBo> result = getIntersection(physicalSort, responseBean.getData());

        // 查询上述三级运营分类的二级分类，合并结构，即三级分类塞到其二级分类的sub下面
//        LOGGER.info("in filterSort. result : {}", JSON.toJSONString(result));
        return processResult(result, responseBean.getData());
    }

    // 查询上述三级运营分类的二级分类，合并结构，即三级分类塞到其二级分类的sub下面
    private Object processResult(LinkedHashSet<SalesCategoryBo> threeCategoryList, Map<Integer, SalesCategoryBo> data) {
        Map<String, SalesCategoryVo> result = new LinkedHashMap<String, SalesCategoryVo>();
        for (SalesCategoryBo threeCategory : threeCategoryList) {
            SalesCategoryBo secondSalesCategoryBo = data.get(threeCategory.getParentId());
            //可能二级分类已经关闭
            if(null==secondSalesCategoryBo)
            {
                LOGGER.warn("secondSalesCategoryBo is empty by threeCategory:{}",threeCategory);
                continue;
            }
            //我是没看懂的，直接暴力不允许重复就好了
            //如果名字相同，且指向的物理分类都一样，只需要展示一个出来了 通过hashCode来做
            //对于运营分类，需要把相同名字的分类来做一次合并
            if(secondSalesCategoryBo!=null){
                if (result.containsKey(secondSalesCategoryBo.getCategoryName())) {
                    SalesCategoryVo salesCategoryVo = result.get(secondSalesCategoryBo.getCategoryName());
                    Set<SalesCategoryVo> voSub = salesCategoryVo.getVoSub();
                    if (CollectionUtils.isEmpty(voSub)) {
                        voSub = new LinkedHashSet<SalesCategoryVo>();
                        salesCategoryVo.setVoSub(voSub);
                    }
                    merageSubCategoryByName(threeCategory, voSub);
                }else {
                    SalesCategoryVo convertToVO = salesCategoryConvert.convertToVO(secondSalesCategoryBo);
                    Set<SalesCategoryVo> voSub = new LinkedHashSet<SalesCategoryVo>();
                    //首次需要把三级分类往二级分类挂
                    voSub.add(salesCategoryConvert.convertToVO(threeCategory));
                    convertToVO.setVoSub(voSub);
                    result.put(secondSalesCategoryBo.getCategoryName(), convertToVO);
                }
            }
        }
        // 二级运营分类的物理分类不正确，把三级运营分类的物理分类聚合
        merageSubCategoryRelationParameter(result);
        return result.values();
    }

    /**
     * 二级分类的物理分类不正确，把三级分类的物理分类聚合
     * @param result
     */
    private void merageSubCategoryRelationParameter(Map<String, SalesCategoryVo> result) {
		if(result.isEmpty()){
			return;
		}
		
		for (SalesCategoryVo subSalesCategoryVo : result.values()) {
			HashSet<String> newHashSet = Sets.newHashSet();
			for(SalesCategoryVo threeSalesCategoryVo : subSalesCategoryVo.getVoSub()){
				Map<String, String> threeRelationParameterMap = threeSalesCategoryVo.getRelationParameterMap();
				if (MapUtils.isNotEmpty(threeRelationParameterMap)) {
                    String sort = threeRelationParameterMap.get("sort");
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(sort)) {
                        newHashSet.addAll(Sets.newHashSet(StringUtils.split(sort,",")));
                    }
                }
			}
			Map<String, String> subRelationParameterMap = subSalesCategoryVo.getRelationParameterMap();
			if (MapUtils.isNotEmpty(subRelationParameterMap)) {
				String sort = subRelationParameterMap.get("sort");
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(sort)) {
                    newHashSet.addAll(Sets.newHashSet(StringUtils.split(sort,",")));
                }
			}
			subSalesCategoryVo.getRelationParameterMap().put("sort", StringUtils.join(newHashSet.toArray(), ","));
		}
	}

	/**
     * //对于运营分类，需要把相同名字的分类来做一次合并
     * @param threeCategory
     * @param voSub
     */
    private void merageSubCategoryByName(SalesCategoryBo threeCategory,
                                         Set<SalesCategoryVo> voSub) {
        boolean isExsit=false;
        //如果三级销售分类名字和当前要往这个set里面加的名称一致，需要合并里面的relationParameterMap
        for (SalesCategoryVo salesCategoryVo2 : voSub) {
            if(org.apache.commons.lang3.StringUtils.isNotEmpty(salesCategoryVo2.getCategoryName()) && salesCategoryVo2.getCategoryName().equals(threeCategory.getCategoryName()))
            {
                Map<String, String> relationParameterMap = salesCategoryVo2.getRelationParameterMap();
                if (MapUtils.isNotEmpty(relationParameterMap) && org.apache.commons.lang3.StringUtils.isNotEmpty(threeCategory.getRelationParameter())) {
                    String sort = relationParameterMap.get("sort");
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(sort)) {
                        HashSet<String> newHashSet = Sets.newHashSet(StringUtils.split(sort,","));
                        newHashSet.addAll(Sets.newHashSet(threeCategory.getRelationParameter().split(",")));
                        salesCategoryVo2.getRelationParameterMap().put("sort", StringUtils.join(newHashSet.toArray(), ","));
                    }
                }
                isExsit = true;
                break;
            }
        }
        if (!isExsit) {
            voSub.add(salesCategoryConvert.convertToVO(threeCategory));
        }
    }

    private LinkedHashSet<SalesCategoryBo> getIntersection(Map<String, Integer> physicalSort, Map<Integer, SalesCategoryBo> data) {
        LinkedHashSet<SalesCategoryBo> result = new LinkedHashSet<SalesCategoryBo>();
        for (String physicalSortId : physicalSort.keySet()) {
            for (SalesCategoryBo salesCategoryBo : data.values()) {
                if (null == salesCategoryBo || salesCategoryBo.getLevelNumber() != 3
                        || org.apache.commons.lang3.StringUtils.isEmpty(salesCategoryBo.getRelationParameter())) {
                    continue;
                }
                List<String> asList = Arrays.asList(salesCategoryBo.getRelationParameter().split(","));
                if (asList.contains(physicalSortId)) {
                    result.add(salesCategoryBo);
                }
            }
        }
        return result;
    }

    // 获取所有的运营分类
    private SalesCategoryRspBo getAllSalesCategory() {
        SalesCategoryRspBo responseBean = null;
        try {
            responseBean = serviceCaller.call("product.queryOriginalSalesCategoryList", new QuerySalesCategoryReq(), SalesCategoryRspBo.class);
        } catch (ServiceException e) {
            LOGGER.warn("queryOriginalSalesCategoryList find wrong.", e);
        } catch (ServiceNotAvaibleException e) {
            LOGGER.warn("queryOriginalSalesCategoryList Service not avaible.", e);
        } catch (ServiceNotFoundException e) {
            LOGGER.warn("queryOriginalSalesCategoryList Service not found.", e);
        }
        return responseBean;
    }

    private Map<String,Integer> getPhysicalSortMap(JSONArray sort) {
        Map<String,Integer> physicalSort = new LinkedHashMap<String, Integer>();
        // 取三级菜单下所有的的sort_id 和count 集合
        int size = sort.size();
        for (int i = 0; i < size; i++) {
            JSONObject oneLeverSort = sort.getJSONObject(i);
            if (null == oneLeverSort) {
                continue;
            }
            JSONArray twoLeverSort = oneLeverSort.getJSONArray("sub");
            if (null == twoLeverSort) {
                continue;
            }
            int size2 = twoLeverSort.size();
            for (int j = 0; j < size2; j++) {
                JSONObject threeLeverSort = twoLeverSort.getJSONObject(j);
                if (null == threeLeverSort) {
                    continue;
                }
                JSONArray minSortArr = threeLeverSort.getJSONArray("sub");
                if (null == minSortArr) {
                    continue;
                }
                int size3 = minSortArr.size();
                for (int k = 0; k < size3; k++) {
                    JSONObject leafSort = minSortArr.getJSONObject(k);
                    physicalSort.put(leafSort.getString("sort_id"), leafSort.getInteger("count"));
                }
            }
        }
        return physicalSort;
    }

    private String getUrl(String dynamicParam) {
        return "http://" + searchServerIpAndPort + "/yohosearch/sortgroup.json?" + dynamicParam;
    }
}