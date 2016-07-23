package com.yoho.gateway.controller.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.yoho.gateway.cache.expire.product.ExpireTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yoho.core.cache.CacheClient;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.product.CommonVo;
import com.yoho.gateway.model.product.ConsultRspPageVO;
import com.yoho.gateway.model.product.ConsultRspVO;
import com.yoho.product.model.ConsultBo;
import com.yoho.product.model.ConsultLikeBo;
import com.yoho.product.request.BaseRequest;
import com.yoho.product.request.ConsultAddRequest;
import com.yoho.product.request.ConsultLikeRequest;
import com.yoho.product.request.ConsultQueryRequest;
import com.yoho.product.request.LikeRequest;

/**
 * @author xieyong
 *
 */
@Controller
public class ConsultController  implements InitializingBean{

	@Autowired
	private ServiceCaller serviceCaller;
	@Autowired
	private CacheClient cacheClient;
	
	@Resource(name="consultConfigMap")
	private Map<String, Object> consultMap;
	
	private List<CommonVo> commonBolist=Lists.newArrayList();
	
	//请求成功
	private final static int REQUEST_SUCCESS_CODE = 200;
	private final static String REQUEST_SUCCESS_MSG = "获取咨询信息成功.";
	
	//请求成功
	private final static int ADD_REQUEST_SUCCESS_CODE = 200;
	private final static String ADD_REQUEST_SUCCESS_MSG = "提交成功.";
	private final static String QUERY_COMMON_SUCCESS_MSG = "查询常用问题成功.";
	
	//UID不能为空
	private final static int UID_IS_NULL_CODE = 404;
	private final static String UID_IS_NULL_MSG = "Uid Is Null.";
		
	//新增时产品ID不能为空
	private final static int ADD_PRODUCT_ID_IS_NULL_CODE = 404;
	private final static String ADD_PRODUCT_ID_IS_NULL_MSG = "产品id不能为空.";
	
	//新增时内容不能为空
	private final static int CONTENT_IS_NULL_CODE = 404;
	private final static String CONTENT_IS_NULL_MSG = "内容不能为空.";
	
	//查询时产品ID不能为空
	private final static int PRODUCT_IS_NULL_CODE = 500;
	private final static String PRODUCT_IS_NULL_MSG = "产品id不能为空.";
	//点赞时咨询ID不能为空
	private final static int CONSULT_ID_IS_NULL_CODE = 500;
	private final static String CONSULT_ID_IS_NULL_MSG = "咨询id不能为空.";
	private final static int USER_IS_USEFULLED_CODE = 500;
	private final static String USER_IS_USEFULLED_MSG = "用户已点击，不能再次提交.";
	private final static int CONSULT_CACHE_TIME = ExpireTime.app_consult_li;
	
	private Logger logger = LoggerFactory.getLogger(ConsultController.class);

	@RequestMapping(params = "method=app.consult.li")
	@ResponseBody
	public ApiResponse getConsultList(@RequestParam(value="page", required=false, defaultValue="1") int pageNum,
			@RequestParam(value="uid", required=false, defaultValue="0") int uid,
			@RequestParam(value="limit", required=false, defaultValue="10") int limit,
			@RequestParam(value="product_id", required=true) int product_id) throws GatewayException {
		logger.info("Begin call getConsultList gateway. Param page is {}, limit is {}, product_id is {}",
				pageNum, limit, product_id);
		//校验参数product_id不能为空，或者为0
		if(product_id < 1){
			logger.warn("Parameter product_id is {}", product_id);
			throw new GatewayException(PRODUCT_IS_NULL_CODE, PRODUCT_IS_NULL_MSG);
		}
		ConsultQueryRequest consultQueryRequest=new ConsultQueryRequest();
		consultQueryRequest.setPageNum(pageNum);
		consultQueryRequest.setPageSize(limit);
		consultQueryRequest.setProductId(product_id);
		consultQueryRequest.setUid(uid);
		final String key="yh:gw:consultList:"+pageNum+":"+limit+":"+product_id;
		final String countKey="yh:gw:consultCount:"+product_id;
		ConsultBo[] consultBoList=cacheClient.get(key,ConsultBo[].class);
		if(null==consultBoList)
		{
			consultBoList = serviceCaller.call("product.queryConsults", consultQueryRequest, ConsultBo[].class);
			cacheClient.set(key, CONSULT_CACHE_TIME, consultBoList);
		}
			
		//查询总数，前台需要分页
		BaseRequest<Integer> request=new BaseRequest<Integer>();
		request.setParam(product_id);
		Integer consultCount=cacheClient.get(countKey,Integer.class);
		if(null==consultCount)
		{
			consultCount=serviceCaller.call("product.queryConsultCount", request, Integer.class);
			cacheClient.set(countKey, CONSULT_CACHE_TIME, consultCount);
		}
		//TODO (崩溃的保护)
		
		//组装点赞查询参数
		LikeRequest likeRequest=new LikeRequest();
		likeRequest.setUid(uid);
		List<ConsultLikeBo> list=new ArrayList<ConsultLikeBo>();
		 if(null != consultBoList && consultBoList.length > 0){
				for(int index = 0; index < consultBoList.length; index++){
					ConsultLikeBo likebo=new ConsultLikeBo();
					likebo.setId(consultBoList[index].getId());
					list.add(likebo);
				}
		 }
		 likeRequest.setList(list);
		//product.queryLikes---->/consult/queryLikes
		 ConsultBo[] consuList = serviceCaller.call("product.queryLikes", likeRequest, ConsultBo[].class);
		
		List<ConsultRspVO> voList = new ArrayList<ConsultRspVO>();
		
		 if(null != consultBoList && consultBoList.length > 0){
				for(int index = 0; index < consultBoList.length; index++){
					ConsultBo bo = consultBoList[index];
					
					ConsultRspVO vo = new ConsultRspVO(bo.getId(), bo.getAsk(), bo.getAnswer(), bo.getAskTime(), bo.getAnswerTime(),consuList[index].getLike(),consuList[index].getIsLike(),consuList[index].getUseful(),consuList[index].getIsUseful(), consultCount);
					voList.add(vo);
				}
			}
		ConsultRspPageVO pageVo=new ConsultRspPageVO(pageNum,consultCount%limit==0?consultCount/limit:consultCount/limit+1,consultCount,voList);
		return new ApiResponse.ApiResponseBuilder().code(REQUEST_SUCCESS_CODE).message(REQUEST_SUCCESS_MSG).data(pageVo).build();
		
	}
	
	/**
	 * 添加咨询信息
	 * 
	 * @param product_id 产品id
	 * @param uid 用户id
	 * @param content 咨询内容
	 * @return ApiResponse
	 * @throws GatewayException 
	 */
	@RequestMapping(params = "method=app.consult.add")
	@ResponseBody
	public ApiResponse addConsult(@RequestParam("product_id") int product_id,
			@RequestParam("uid") int uid,
			@RequestParam("content") String content) throws GatewayException {
		logger.info("Begin call getCouponsList gateway. Param product_id is {}, uid is {}, content is {}", product_id, uid, content);
		//校验参数uid，product_id以及content不能为空，或者为0
		if(uid < 1){
			logger.warn("Parameter uid is null. uid={}, product_id={}, content={}", uid, product_id, content);
			throw new GatewayException(UID_IS_NULL_CODE, UID_IS_NULL_MSG);
		}
		if(product_id < 1){
			logger.warn("Parameter product_id is null. uid={}, product_id={}, content={}", uid, product_id, content);
			throw new GatewayException(ADD_PRODUCT_ID_IS_NULL_CODE, ADD_PRODUCT_ID_IS_NULL_MSG);
		}
		if(null == content || content.isEmpty()){
			logger.warn("Parameter content is null or empty. uid={}, product_id={}, content={}", uid, product_id, content);
			throw new GatewayException(CONTENT_IS_NULL_CODE, CONTENT_IS_NULL_MSG);
		}
		
		ConsultAddRequest consultAddRequest=new ConsultAddRequest();
		consultAddRequest.setAskContent(content);
		consultAddRequest.setAskUserId(uid);
		consultAddRequest.setProductId(product_id);
		
		//调用新增咨询服务
		//product.addConsult---->/consult/addConsult
		int affectRow=serviceCaller.call("product.addConsult", consultAddRequest, Integer.class);
		if(affectRow>0)
		{
			logger.info("Add consult end and add successed");
		}
		//(返回响应参数
		ApiResponse responseBean = new ApiResponse.ApiResponseBuilder().data("").code(ADD_REQUEST_SUCCESS_CODE).message(ADD_REQUEST_SUCCESS_MSG).build();
		return responseBean;
	}
	/**
	 * 点赞接口 咨询列表对每条咨询信息进行点赞操作
	 * @param id 咨询id
	 * @param uid 用户id
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.consult.like")
	@ResponseBody
	public ApiResponse addLike(@RequestParam("id") int id,
			@RequestParam("uid") int uid )throws GatewayException {
		logger.info("Begin call getCouponsList gateway. Param id is {}, uid is {}, content is {}", id, uid);
		//校验参数uid，id 或者为0
		if(uid < 1){
			logger.warn("Parameter uid is null. uid={}, product_id={}", uid,id );
			throw new GatewayException(UID_IS_NULL_CODE, UID_IS_NULL_MSG);
		}
		if(id < 1){
			logger.warn("Parameter product_id is null. uid={}, id={}", uid,id);
			throw new GatewayException(CONSULT_ID_IS_NULL_CODE, CONSULT_ID_IS_NULL_MSG);
		}
		
		ConsultLikeRequest consultLikeRequest=new ConsultLikeRequest();
		consultLikeRequest.setConsultId(id);
		consultLikeRequest.setUserId(uid);
		
		//调用咨询点赞服务
		int affectRow=serviceCaller.call("product.addLike", consultLikeRequest, Integer.class);
		if(affectRow>0)
		{
			logger.info("Add consult end and add successed");
		}else{
			throw new GatewayException(USER_IS_USEFULLED_CODE, USER_IS_USEFULLED_MSG);
		}
		//(返回响应参数
		ApiResponse responseBean = new ApiResponse.ApiResponseBuilder().data("").code(ADD_REQUEST_SUCCESS_CODE).message(ADD_REQUEST_SUCCESS_MSG).build();
		return responseBean;
	}
	/**
	 * 是否有用  咨询列表页面 对每条咨询信息评价是否有用
	 * @param id 咨询id
	 * @param uid 用户id
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.consult.useful")
	@ResponseBody
	public ApiResponse addUseful(@RequestParam("id") int id,
			@RequestParam("uid") int uid )throws GatewayException {
		logger.info("Begin call getCouponsList gateway. Param id is {}, uid is {}, content is {}", id, uid);
		//校验参数uid，，或者为0
		if(uid < 1){
			logger.warn("Parameter uid is null. uid={}, product_id={}", uid,id );
			throw new GatewayException(UID_IS_NULL_CODE, UID_IS_NULL_MSG);
		}
		if(id < 1){
			logger.warn("Parameter product_id is null. uid={}, id={}", uid,id);
			throw new GatewayException(CONSULT_ID_IS_NULL_CODE, CONSULT_ID_IS_NULL_MSG);
		}
		ConsultLikeRequest consultLikeRequest=new ConsultLikeRequest();
		consultLikeRequest.setConsultId(id);
		consultLikeRequest.setUserId(uid);
		
		//调用新增有用服务
		int affectRow=serviceCaller.call("product.addUseful", consultLikeRequest, Integer.class);
		if(affectRow>0)
		{
			logger.info("Add consult end and add successed");
		}else{
			throw new GatewayException(USER_IS_USEFULLED_CODE, USER_IS_USEFULLED_MSG);
		}
		//(返回响应参数
		ApiResponse responseBean = new ApiResponse.ApiResponseBuilder().data("").code(ADD_REQUEST_SUCCESS_CODE).message(ADD_REQUEST_SUCCESS_MSG).build();
		return responseBean;
	}
	/**
	 * 查询常用问题接口
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.consult.common")
	@ResponseBody
	public ApiResponse queryCommon()throws GatewayException {
		//(返回响应参数
		ApiResponse responseBean = new ApiResponse.ApiResponseBuilder().data(commonBolist).code(ADD_REQUEST_SUCCESS_CODE).message(QUERY_COMMON_SUCCESS_MSG).build();
		return responseBean;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		List<CommonVo> list =new ArrayList<CommonVo>();
		Set<String> set=consultMap.keySet();
		for (String key : set) {
			CommonVo commonBo=new CommonVo();
			commonBo.setId(Integer.valueOf(key.split("-")[1]));
			commonBo.setAnswer(((Map<String, Object>)consultMap.get(key)).get("answer").toString());
			commonBo.setQuestion(((Map<String, Object>)consultMap.get(key)).get("question").toString());
			list.add(commonBo);
		}
		this.commonBolist=list;
	}
	
	
	/**
	 * 查询最新的两条咨询
	 * @param pageNum 
	 * @param limit
	 * @param productId
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.consult.lastTwo")
	@ResponseBody
	@Cachable(expire= ExpireTime.app_consult_lastTwo)
	public ApiResponse getConsultLastTwo(@RequestParam(value="page", required=false, defaultValue="1") int pageNum,
			@RequestParam(value="limit", required=false, defaultValue="2") int limit,
			@RequestParam(value="productId", required=false ,defaultValue="0") int productId) throws GatewayException {
			logger.info("Begin call getConsultList gateway. Param page is {}, limit is {}, productId is {}",pageNum, limit, productId);
			//校验参数product_id不能为空，或者为0
			if(productId < 1){
				logger.warn("Parameter product_id is {}", productId);
				throw new GatewayException(PRODUCT_IS_NULL_CODE, PRODUCT_IS_NULL_MSG);
			}
			ConsultQueryRequest consultQueryRequest=new ConsultQueryRequest();
			consultQueryRequest.setPageNum(pageNum);
			consultQueryRequest.setPageSize(limit);
			consultQueryRequest.setProductId(productId);
			//先查询咨询
			ConsultBo[] consultBoList = serviceCaller.call("product.queryConsults", consultQueryRequest, ConsultBo[].class);
			int consultCount=0;
			//有咨询时，才需要去查询总数
			if(null != consultBoList && consultBoList.length>0)
			{
				BaseRequest<Integer> request=new BaseRequest<Integer>();
				request.setParam(productId);
				consultCount=serviceCaller.call("product.queryConsultCount", request, Integer.class);
			}
			List<ConsultRspVO> voList = new ArrayList<ConsultRspVO>();
			if(null != consultBoList && consultBoList.length > 0){
				for(int index = 0; index < consultBoList.length; index++){
					ConsultBo bo = consultBoList[index];
					ConsultRspVO vo = new ConsultRspVO(bo.getId(), bo.getAsk(), bo.getAnswer(), bo.getAskTime(), bo.getAnswerTime());
					voList.add(vo);
				}
			}
			ConsultRspPageVO pageVo=new ConsultRspPageVO(pageNum,consultCount,voList);
			return new ApiResponse.ApiResponseBuilder().code(REQUEST_SUCCESS_CODE).message(REQUEST_SUCCESS_MSG).data(pageVo).build();
		
	}
	
}
