package com.yoho.gateway.controller.users;

import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.browse.BrowseReqVO;
import com.yoho.gateway.model.browse.ProductBrowseRespVO;
import com.yoho.gateway.service.favorite.IBrowseServiceNew;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BrowseController {
    private Logger logger = LoggerFactory.getLogger(BrowseController.class);

    @Autowired
    private IBrowseServiceNew browseServiceNew;

    /**
     * 描述：获取用户浏览记录商品列表 场景：1. 进入个人中心, 点击浏览记录, 显示个人浏览记录
     *
     * @param uid   uid用户ID
     * @param page  页数
     * @param limit 每页大小
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.browse.product")
    @ResponseBody
    public ApiResponse product(@RequestParam(value = "uid") int uid, @RequestParam(value = "page") int page, @RequestParam(value = "limit") int limit) throws GatewayException {
        logger.info("Enter BrowseController.product. uid is {}, page is {}, limit is {}", uid, page, limit);

        // (1)组装请求参数对象
        BrowseReqVO browseReqVO = new BrowseReqVO(uid, page, limit);

        // (2)调用service获取返回
        ProductBrowseRespVO productBrowseRespVO = browseServiceNew.listBrowse(browseReqVO);

        // (3)返回
        return new ApiResponse.ApiResponseBuilder().code(200).message("Sales Product List").data(productBrowseRespVO).build();
    }

    /**
     * 描述：删除/清空用户浏览记录<br>
     * 场景：<br>
     * &nbsp;&nbsp;1.会员中心用户浏览记录列表页面，在“全部”分类下点击“清空”(清空用户所有浏览记录)<br>
     * &nbsp;&nbsp;2.会员中心用户浏览记录列表页面，在某一具体分类下点击“清空”(清空用户该分类的浏览记录)<br>
     * &nbsp;&nbsp;3.会员中心用户浏览记录列表页面，删除某一具体商品<br>
     *
     * @param uid         uid
     * @param skn         商品SKN，可不传
     * @param category_id 商品二级分类ID，可不传
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.browse.delete")
    @ResponseBody
    public ApiResponse delete(@RequestParam(value = "uid") int uid, @RequestParam(value = "skn", required = false) String skn, @RequestParam(value = "category_id", required = false) String category_id)
            throws GatewayException {
        logger.debug("Enter BrowseController.delete. uid is {}, skn is {}, category_id is {}", uid, skn, category_id);

        // (1)组装请求参数对象
        BrowseReqVO browseReqVO = new BrowseReqVO(uid, skn, category_id);

        // (2)调用service获取返回
        browseServiceNew.deleteBrowse(browseReqVO);

        // (3)返回
        return new ApiResponse.ApiResponseBuilder().code(200).message("delete success").data(new JSONObject()).build();
    }

    /**
     * 描述：批量新增浏览记录<br>
     * 场景：<br>
     * &nbsp;&nbsp;1.客户端缓存的访客浏览记录，用户登录后提交到服务端保存<br>
     *
     * @param browseReqVO {uid:"",browseList:[{product_skn:"",time:""}]}
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.browse.addBrowseBatch")
    @ResponseBody
    public ApiResponse addBrowseBatch(BrowseReqVO browseReqVO) throws GatewayException {
        logger.debug("Enter addBrowseBatch. browseReqVO", browseReqVO);

        // (1)调用服务
        Long size = browseServiceNew.addBrowseBatch(browseReqVO);

        // (2)返回
        return new ApiResponse.ApiResponseBuilder().code(200).message("addBrowseBatch success").data(size).build();
    }

}
