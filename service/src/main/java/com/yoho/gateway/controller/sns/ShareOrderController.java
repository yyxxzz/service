package com.yoho.gateway.controller.sns;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.GatewayError;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.sns.ShareOrderCommentVO;
import com.yoho.gateway.model.sns.ShareOrderGoodsVo;
import com.yoho.gateway.service.sns.ShareOrderService;
import com.yoho.service.model.resource.resource.FocusParsedResource;
import com.yoho.service.model.sns.model.CommentBo;
import com.yoho.service.model.sns.model.ShareOrderGoodsBo;
import com.yoho.service.model.sns.request.BaseSnsRequest;
import com.yoho.service.model.sns.request.CommentRecordReq;
import com.yoho.service.model.sns.request.ShareOrderForm;
import com.yoho.service.model.sns.response.PageResponse;
import com.yoho.service.model.sns.response.ProductFilterResponse;
import com.yoho.service.model.sns.response.ResponseBean;

/**
 * gateway 晒单接口
 *
 * @author CaoQi
 * @Time 2015/11/5
 */
@Controller
public class ShareOrderController {

    private final Logger logger = LoggerFactory.getLogger(ShareOrderController.class);

    //保证单例
    @Autowired
    private ServiceCaller serviceCaller;

    @Autowired
    private ShareOrderService shareOrderService;
    
    @Autowired
	private ShareOrderCacheFinder shareOrderCacheFinder;

    private String SHARE_KEY="publishUrl";



    /**
     * 发布晒单
     * @param parameters
     * @return
     */
    @RequestMapping(params = "method=show.saveShareOrder")
    @ResponseBody
    public ApiResponse saveShareOrder(@RequestParam("parameters")String parameters){
    	logger.info("come in ShareOrderController method=show.saveShareOrder parameters is:{}", parameters);
        CommentBo commentBo = JSON.parseObject(parameters, CommentBo.class);
        if (null == commentBo || StringUtils.isEmpty(commentBo.getUid())) {
        	logger.warn("ShareOrderController method=show.saveShareOrder uid Is Null", commentBo);
			return new ApiResponse(404, "uid Is Null", null);
		}
        
        ResponseBean responseBean = serviceCaller.call("sns.saveShareOrder", commentBo, ResponseBean.class);
        ApiResponse apiResponse = new ApiResponse();
        Map<String,String> map = new HashMap<String,String>();

        if("200".equals(responseBean.getCode())){
            String productUrl = shareOrderService.createShareProductUrl(commentBo.getProductId(),commentBo.getGoodsId());
            map.put(SHARE_KEY,productUrl);
        }


        apiResponse.setCode(Integer.parseInt(responseBean.getCode()));
        apiResponse.setMessage(responseBean.getMessage());
        apiResponse.setData(map);
        logger.info("come out ShareOrderController method=show.saveShareOrder parameters is:{}", parameters);
        return apiResponse;
    }


    /**
     * 我的晒单列表
     * 分页查询我的晒单
     * @param shareOrderForm
     * @return
     */
    @RequestMapping(params = "method=show.ownerShareList")
    @ResponseBody
    public ApiResponse ownerShareList(ShareOrderForm shareOrderForm){
        PageResponse pageResponse = serviceCaller.call("sns.ownerShareList", shareOrderForm, PageResponse.class);

        if(pageResponse!=null && pageResponse.getList()!=null){
        	pageResponse = shareOrderService.getShareOrderVoList(pageResponse);
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(pageResponse);
        apiResponse.setCode(GatewayError.CODE_SUCCESS.getCode());
        apiResponse.setMessage(GatewayError.CODE_SUCCESS.getMessage());
        return apiResponse;
    }

    /**
     * 查询本订单内待晒单列表
     * @param shareOrderForm
     * @return
     */
    @RequestMapping(params = "method=show.toShareOrderList")
    @ResponseBody
    public ApiResponse toShareOrderList(ShareOrderForm shareOrderForm){
        ShareOrderGoodsBo[] shareOrderGoodsBoList = serviceCaller.call("sns.toShareOrderList", shareOrderForm, ShareOrderGoodsBo[].class);

        PageResponse pageResponse = new PageResponse();
        pageResponse.setList(Arrays.asList(shareOrderGoodsBoList));


        PageResponse<ShareOrderGoodsVo> result = shareOrderService.getShareOrderGoodsVoList(pageResponse,false);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(result.getList());
        apiResponse.setCode(GatewayError.CODE_SUCCESS.getCode());
        apiResponse.setMessage(GatewayError.CODE_SUCCESS.getMessage());
        return apiResponse;
    }

    /**
     * 查询其他订单待晒单列表
     * 分页查询其他待晒单 （订单入口）
     * @param shareOrderForm
     * @return
     */
    @RequestMapping(params = "method=show.otherToShareOrderList")
    @ResponseBody
    public ApiResponse otherToShareOrderList(ShareOrderForm shareOrderForm){
        PageResponse<ShareOrderGoodsBo> pageResponse = serviceCaller.call("sns.otherToShareOrderList", shareOrderForm, PageResponse.class);

        PageResponse<ShareOrderGoodsVo> result = shareOrderService.getShareOrderGoodsVoList(pageResponse,true);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(result);
        apiResponse.setCode(GatewayError.CODE_SUCCESS.getCode());
        apiResponse.setMessage(GatewayError.CODE_SUCCESS.getMessage());
        return apiResponse;
    }

    /**
     * 查询全部待晒单列表
     * 分页查询 （我的晒单入口）
     * @param shareOrderForm
     * @return
     */
    @RequestMapping(params = "method=show.allToShareOrderList")
    @ResponseBody
    public ApiResponse allToShareOrderList(ShareOrderForm shareOrderForm){
        PageResponse<ShareOrderGoodsBo> responseBean = serviceCaller.call("sns.allToShareOrderList", shareOrderForm, PageResponse.class);
        PageResponse<ShareOrderGoodsVo> result = shareOrderService.getShareOrderGoodsVoList(responseBean,true);

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setData(result);
        apiResponse.setCode(GatewayError.CODE_SUCCESS.getCode());
        apiResponse.setMessage(GatewayError.CODE_SUCCESS.getMessage());
        return apiResponse;
    }

    /**
     * 商品详情中晒单列表
     * 根据商品id查询晒单列表
     * filterId: 7:全部，6：有图，4：四星
     * @return
     */
    @RequestMapping(params = "method=show.productShareOrderList")
    @ResponseBody
    @Cachable
    public ApiResponse productShareOrderList(@RequestParam String productId, @RequestParam(required = false, defaultValue = "15") int limit,
                                               @RequestParam(required = false, defaultValue = "1") int page, @RequestParam(required = false, defaultValue = "0") int filterId){
        ShareOrderForm form = new ShareOrderForm();
        form.setProductId(productId);
        Integer _filterId = filterId <=0 ? null : filterId;
        form.setFilterId(_filterId);
        form.setPage(page);
        form.setLimit(limit);
        ProductFilterResponse fileterResponse = serviceCaller.call("sns.productShareOrderList", form, ProductFilterResponse.class);

        //设置分页中的VO具体信息
        if (fileterResponse.getPageResponse() != null){
            shareOrderService.getShareOrderVoList(fileterResponse.getPageResponse());
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(fileterResponse);
        apiResponse.setCode(GatewayError.CODE_SUCCESS.getCode());
        apiResponse.setMessage(GatewayError.CODE_SUCCESS.getMessage());
        return apiResponse;
    }

    /**
     * 商品详情页展示评论信息，如果该商品有晒图，展示一条最近的晒图评价，如果没有晒图，展示两条最近的评价
     * 根据商品id查询晒单列表
     *
     * @return
     */
    @RequestMapping(params = "method=show.recentShareOrderByProductId")
    @ResponseBody
    @Cachable(expire = 1800)
    public ApiResponse recentShareOrderByProductId(@RequestParam String productId){
    	BaseSnsRequest<String> request = new BaseSnsRequest<String>();
    	request.setParam(productId);
    	PageResponse pageResponse = serviceCaller.call("sns.getRecentShareOrderByProductId", request, PageResponse.class);

        //设置分页中的VO具体信息
        if (pageResponse != null && pageResponse.getList()!=null){
            shareOrderService.getShareOrderVoList(pageResponse);
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(pageResponse);
        apiResponse.setCode(GatewayError.CODE_SUCCESS.getCode());
        apiResponse.setMessage(GatewayError.CODE_SUCCESS.getMessage());
        return apiResponse;
    }
    
    /**
     * show推荐晒单列表，展示点赞个数，不适用默认cache
     * 根据商品id查询晒单列表
     *
     * @return
     */
    @RequestMapping(params = "method=show.showChannel")
    @ResponseBody
    public ApiResponse showChannel( @RequestParam(required = false, defaultValue = "15") int limit,
                                    @RequestParam(required = false, defaultValue = "1") int page,
                                    @RequestParam(value = "udid", required = false) String udid,
                                    @RequestParam("client_type") String clientType){
		logger.info("showChannel method=show.showChannel limit is:{},page is:{},udid is:{},clientType is:{}",
				limit, page, udid, clientType);
		ApiResponse apiResponse = shareOrderCacheFinder.fetchShowChannelCache(limit, page, udid, clientType);
        return apiResponse;
    }

    /**
	 * 用户对晒单点赞
	 * @param commentId：晒单id
	 * @param udid:设备唯一标示
	 * @return
	 */
	@RequestMapping(params = "method=show.addPraise")
	@ResponseBody
	public ApiResponse addPraise(
			@RequestParam(value = "commentId", required = false) String commentId,
			@RequestParam(value = "udid", required = false) String udid) {
		if (null == commentId && null == udid) {
			return new ApiResponse(404, "commentId or udid Is Null", null);
		}
		
		logger.info("addPraise method=sns.addPraise commentId is:{}, udid is:{}", commentId, udid);
		BaseSnsRequest<String> request = new BaseSnsRequest<String>();
		request.setUdid(udid);
		request.setParam(commentId);
		
		ResponseBean responseBean = null;
		try{
			responseBean = serviceCaller.call("sns.addPraise", request, ResponseBean.class);
			logger.debug( "addPraise method=show.addPraise responseBean is:{} ", responseBean);
		}catch(Exception e){
			logger.warn("invoke sns.addPraise failed", e);
			throw e;
		}
		
		if (null == responseBean) {
			logger.warn(
					"addPraise responseBean is null commentId is:{}, udid is:{}", commentId, udid);
			return new ApiResponse.ApiResponseBuilder().code(404).message("add praise fail").build();
		}
		
		ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(Integer.parseInt(responseBean.getCode()));
        apiResponse.setMessage(responseBean.getMessage());
        apiResponse.setData(responseBean.getData());
		return apiResponse;
	}
	
	/**
	 * 用户对晒单取消点赞
	 * @param commentId：晒单id
	 * @param udid:设备唯一标示
	 * @return
	 */
	@RequestMapping(params = "method=show.cancelPraise")
	@ResponseBody
	public ApiResponse cancelPraise(
			@RequestParam(value = "commentId", required = false) String commentId,
			@RequestParam(value = "udid", required = false) String udid) {
		if (null == commentId && null == udid) {
			return new ApiResponse(404, "commentId or udid Is Null", null);
		}
		
		logger.info("cancelPraise method=sns.cancelPraise commentId is:{}, udid is:{}", commentId, udid);
		BaseSnsRequest<String> request = new BaseSnsRequest<String>();
		request.setUdid(udid);
		request.setParam(commentId);
		
		ResponseBean responseBean = null;
		try{
			responseBean = serviceCaller.call("sns.cancelPraise", request, ResponseBean.class);
			logger.debug( "cancelPraise method=sns.cancelPraise responseBean is:{} ", responseBean);
		}catch(Exception e){
			logger.warn("invoke show.cancelPraise failed", e);
			throw e;
		}
		
		if (null == responseBean) {
			logger.warn(
					"cancelPraise responseBean is null commentId is:{}, udid is:{}", commentId, udid);
			return new ApiResponse.ApiResponseBuilder().code(404).message("cancel praise fail").build();
		}
		
		ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(Integer.parseInt(responseBean.getCode()));
        apiResponse.setMessage(responseBean.getMessage());
        apiResponse.setData(responseBean.getData());
		return apiResponse;
	}
	
	/**
     * pc端提供接口（个人中心消息）：查询用户未评价的商品记录总数
     *
     * @return
     */
    @RequestMapping(params = "method=show.notCommentRecordCount")
    @ResponseBody
    @Cachable
    public ApiResponse getNotCommentRecordCount(@RequestParam(value = "uid") String uid){
    	logger.info("getNotCommentRecordCount method=show.notCommentRecordCount, uid is:{}", uid);
    	Integer count = 0;

    	ShareOrderForm shareOrderForm = new ShareOrderForm();
    	shareOrderForm.setUid(uid);
    	ShareOrderGoodsBo[] shareOrderGoodsBoList = serviceCaller.call("sns.queryOrderProductCommentList", shareOrderForm, ShareOrderGoodsBo[].class);
        if(shareOrderGoodsBoList!=null && shareOrderGoodsBoList.length>0){
        	count = shareOrderService.getNotCommentCount(shareOrderGoodsBoList);
        }
        
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(count);
        apiResponse.setCode(GatewayError.CODE_SUCCESS.getCode());
        apiResponse.setMessage(GatewayError.CODE_SUCCESS.getMessage());
        return apiResponse;
    }
    
    /**
     * pc端我的订单商品评价列表
     * @param shareOrderForm
     * @return
     */
    @RequestMapping(params = "method=web.show.queryOrderProductCommentList")
    @ResponseBody
    public ApiResponse queryOrderProductCommentList(ShareOrderForm shareOrderForm){
    	logger.info("Enter queryOrderProductCommentList : shareOrderForm is {}", shareOrderForm);
    	ShareOrderGoodsBo[] shareOrderGoodsBoList = serviceCaller.call("sns.queryOrderProductCommentList", shareOrderForm, ShareOrderGoodsBo[].class);
    	List<ShareOrderCommentVO> shareOrderCommentVOList = null;
        if(shareOrderGoodsBoList!=null && shareOrderGoodsBoList.length>0){
        	shareOrderCommentVOList = shareOrderService.getShareOrderCommentVOList(shareOrderGoodsBoList);
        }

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(shareOrderCommentVOList);
        apiResponse.setCode(GatewayError.CODE_SUCCESS.getCode());
        apiResponse.setMessage(GatewayError.CODE_SUCCESS.getMessage());
        
        return apiResponse;
    }
}