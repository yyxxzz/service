package com.yoho.gateway.controller.product;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.client.hystrix.AsyncFuture;
import com.yoho.gateway.cache.MemecacheClientHolder;
import com.yoho.gateway.controller.product.builder.ProductCallbackBuilder;
import com.yoho.product.model.ProductBo;
import com.yoho.product.model.ProductCallBackDataBo;
import com.yoho.product.model.PromotionBo;
import com.yoho.product.model.wrapper.CommentBoWrapper;
import com.yoho.product.model.wrapper.ConsultBoWrapper;
import com.yoho.product.request.BaseRequest;
import com.yoho.product.request.CallBackDataRequest;
import com.yoho.product.request.FavoriteReqBo;
import com.yoho.service.model.promotion.PointActivityInfoBo;
import com.yoho.service.model.promotion.request.PointActivityReq;

@Service
public class CallBackChain {
	
	private final static Logger logger = LoggerFactory.getLogger(CallBackChain.class);
	
	private final static Logger static_logger = LoggerFactory.getLogger("static_log");
	
	@Autowired
    private ServiceCaller serviceCaller;
	
	@Autowired
	private MemecacheClientHolder memecacheClientHolder;
	
	/**
	 * 回填用户是否喜欢
	 * @param cacheProductBo
	 * @param userId
	 * @return
	 */
	public CallBackChain onCallBackUserFavorite(ProductBo cacheProductBo, Integer userId)
	{	
		if(null == userId || 0==userId)
		{
			return this;
		}
		final String favoriteKey="yh:gw:favorite:product:"+userId;
		//先从缓存获取是否收藏
		String[] favoriteCache=memecacheClientHolder.getLevel1Cache().get(favoriteKey, String[].class);
		if(null!=favoriteCache)
		{
			if (ArrayUtils.contains(favoriteCache, String.valueOf(cacheProductBo.getId()))) {
				cacheProductBo.setIsCollect("Y");
			} else {
				cacheProductBo.setIsCollect("N");
			}
			logger.info("call onCallBackUserFavorite from cache useId is:{},isFavoriteCache:{}",userId,cacheProductBo.getIsCollect());
			return this;
		}
        FavoriteReqBo favRequest = new FavoriteReqBo();
		favRequest.setUid(userId);
		favRequest.setType("product");
		boolean isFavorite = false;
		String[] favoriteProductIds=new String[]{};
		try {
			// 查询用户收藏的所有商品
			//1s之内不给请求就直接返回,不能死等
			favoriteProductIds = serviceCaller.asyncCall("product.getFavoriteProductIdsByUid", favRequest,String[].class).get(1);
			if(ArrayUtils.contains(favoriteProductIds, String.valueOf(cacheProductBo.getId())))
			{
				isFavorite = true;
			}
			logger.info("call onCallBackUserFavorite from service useId is:{},isFavoriteCache:{}",userId,isFavorite);
		} catch (Throwable e) {
			// 捕捉异常，让流程继续走下去
			logger.warn("call product.isFavorite fail uid:{},productId:{}",userId, cacheProductBo.getId(), e);
			isFavorite = false;
		}
		//缓存10分钟
		memecacheClientHolder.getLevel1Cache().set(favoriteKey, 600, favoriteProductIds);
		cacheProductBo.setIsCollect(isFavorite ? "Y" : "N");
		return this;
	}
	
	/**
	 * 回填用户评论信息
	 * @param cacheProductBo
	 * @param appversion app版本
	 * @return
	 */
	public CallBackChain onCallBackComment(ProductBo cacheProductBo, String appversion)
	{
		if(null==cacheProductBo)
		{
			return this;
		}
		if(isCallBackComment(appversion))
		{	
			final String commentKey="yh:gw:firstNewComment:"+cacheProductBo.getId();
			//先从缓存获取评论
			CommentBoWrapper commentBoWrapper=memecacheClientHolder.getLevel1Cache().get(commentKey, CommentBoWrapper.class);
			if(null!=commentBoWrapper)
			{	
				cacheProductBo.setCommentBoWrapper(commentBoWrapper);
				return this;
			}
			BaseRequest<Integer> baseRequest=new BaseRequest<Integer>();
			baseRequest.setParam(cacheProductBo.getId());
			//1s之内不给请求就直接返回,不能死等
			try {
				commentBoWrapper = serviceCaller.asyncCall("product.queryFirstNewComment", baseRequest,CommentBoWrapper.class).get(1);
			} catch (Throwable e) {
				// 捕捉异常，让流程继续走下去
				logger.warn("call product.queryFirstNewComment fail productId:{}",cacheProductBo.getId(), e);
				return this;
			}
			if(null!=commentBoWrapper)
			{	
				//缓存10分钟
				memecacheClientHolder.getLevel1Cache().set(commentKey, 600, commentBoWrapper);
			}
			cacheProductBo.setCommentBoWrapper(commentBoWrapper);
		}
		return this;
	}
	
	
	/**
	 * 回填用户咨询信息
	 * @param cacheProductBo
	 * @param appversion app版本
	 * @return
	 */
	public CallBackChain onCallBackConsult(ProductBo cacheProductBo, String appversion)
	{
		if(null==cacheProductBo)
		{
			return this;
		}
		if(isCallBackConsult(appversion))
		{	
			
			final String consultKey="yh:gw:firstNewConsult:"+cacheProductBo.getId();
			//先从缓存获取咨询信息
			ConsultBoWrapper consultBoWrapper=memecacheClientHolder.getLevel1Cache().get(consultKey, ConsultBoWrapper.class);
			if(null!=consultBoWrapper)
			{	
				cacheProductBo.setConsultBoWrapper(consultBoWrapper);
				return this;
			}
			BaseRequest<Integer> baseRequest=new BaseRequest<Integer>();
			baseRequest.setParam(cacheProductBo.getId());
			//1s之内不给请求就直接返回,不能死等
			try {
				consultBoWrapper = serviceCaller.asyncCall("product.queryFirstNewConsult", baseRequest,ConsultBoWrapper.class).get(1);
			} catch (Throwable e) {
				// 捕捉异常，让流程继续走下去
				logger.warn("call product.queryFirstNewConsult fail productId:{}",cacheProductBo.getId(), e);
				return this;
			}
			if(null!=consultBoWrapper)
			{	
				//缓存10分钟
				memecacheClientHolder.getLevel1Cache().set(consultKey, 600, consultBoWrapper);
			}
			cacheProductBo.setConsultBoWrapper(consultBoWrapper);
		}
		return this;
	}
	
	
	
	/**
	 * @param appversion
	 * @return true:回调
	 *         false：不回调
	 */
	private boolean isCallBackConsult(String appversion)
	{
		if(StringUtils.isBlank(appversion))
		{
			return true;
		}
		//大于4.0.1的版本就不回调咨询服务了
		boolean flag=appversion.compareTo("4.0.1")<0;
		if(flag)
		{	
			//统计
			static_logger.info("call back consult appversion is:{}",appversion);
		}
		return flag;
	}
	
	/**
	 * @param appversion
	 * @return true:回调
	 *         false：不回调
	 */
	private boolean isCallBackComment(String appversion)
	{
		if(StringUtils.isBlank(appversion))
		{
			return true;
		}
		//大于3.9的版本就不回调评论服务了
		boolean flag=appversion.compareTo("3.9")<0;
		if(flag)
		{
			static_logger.info("call back comment appversion is:{}",appversion);
		}
		return flag;
				
	}

	/**
	 *
	 * @param appversion
	 * @return true:回调
	 *         false：不回调
     */
	private boolean isCallBackPromotion(String appversion) {
		if(StringUtils.isBlank(appversion))
		{
			return true;
		}
		//大于4.6的版本就不回调促销服务了
		//4.6.0.1606220001
		boolean flag=appversion.compareTo("4.6.0")<0;
		if(flag)
		{
			static_logger.info("call back promotion appversion is:{}",appversion);
		}
		return flag;
	}

	/**
	 * 回填一些实时信息(库存信息，促销信息(促销信息要剥离出来单独给客户端提供接口))
	 * @param cacheProductBo
	 * @return
	 */
	public CallBackChain onCallBackRealtimeData(ProductBo cacheProductBo)
	{
		final String key="yh:gw:product:callbackdata:"+cacheProductBo.getErpProductId();
		//还是先去gateway的缓存中查询
		ProductCallBackDataBo productCallBackDataBo=memecacheClientHolder.getLevel1Cache().get(key, ProductCallBackDataBo.class);
		if(null!=productCallBackDataBo)
		{	
			//回填库存和标签信息
			ProductCallbackBuilder.fillProductBo(cacheProductBo,productCallBackDataBo);
			return this;
		}
		CallBackDataRequest callBackDataRequest=new CallBackDataRequest();
		callBackDataRequest.setProductBo(cacheProductBo);
		//回填失败也不能影响,1s之内不给请求就直接返回,不能死等
		try
		{
			productCallBackDataBo=serviceCaller.asyncCall("product.queryProductCallBackData", callBackDataRequest, ProductCallBackDataBo.class).get(1);
		}catch(Throwable e)
		{
			// 捕捉异常，让流程继续走下去
			logger.warn("call product.queryProductCallBackData fail productId:{}", cacheProductBo.getId(), e);
			return this;
		}
		//回填库存和标签信息
		ProductCallbackBuilder.fillProductBo(cacheProductBo,productCallBackDataBo);
		//2分钟
		if(null!=productCallBackDataBo)
		{
			memecacheClientHolder.getLevel1Cache().set(key, 120, productCallBackDataBo);
		}
		return this;
	}
	
	/**
	 * 回填促销和一些全局的活动信息
	 * @param cacheProductBo
	 * @param appversion
	 * @return
	 */
	public CallBackChain onCallBackPromotionActity(ProductBo cacheProductBo, String appversion)
	{
		if (null == cacheProductBo) {
			return this;
		}
		// 兼容老版本，查询促销信息  新版本不再回填促销信息
		if (isCallBackPromotion(appversion)) {
			final String key="yh:gw:product:promotionBoList:"+cacheProductBo.getErpProductId();
			PromotionActityWrapper promotionActityWrapper=memecacheClientHolder.getLevel1Cache().get(key, PromotionActityWrapper.class);
			if(null!=promotionActityWrapper)
			{	
				cacheProductBo.setPromotionBoList(promotionActityWrapper.getPromotionBoList());
				return this;
			}
			try
			{	
				BaseRequest<Integer> request=new BaseRequest<Integer>();
				request.setParam(cacheProductBo.getErpProductId());
				AsyncFuture<PromotionBo[]> promotionBoFuture=serviceCaller.asyncCall("product.queryProductFitPromotionListBySkn", request, PromotionBo[].class);
				AsyncFuture<PointActivityInfoBo[]> pointActivityInfoBoFuture=serviceCaller.asyncCall("promotion.queryPointActivity", new PointActivityReq(), PointActivityInfoBo[].class);
				List<PromotionBo> promotionBoList=ProductCallbackBuilder.buildPromotion(promotionBoFuture.get(1),pointActivityInfoBoFuture.get(1));
				cacheProductBo.setPromotionBoList(promotionBoList);
				memecacheClientHolder.getLevel1Cache().set(key, 180, new PromotionActityWrapper(promotionBoList));

			}catch(Throwable e)
			{
				// 捕捉异常，让流程继续走下去
				logger.warn("call promotion.queryPointActivity failed!!!!!",e);
				return this;
			}
		}
		logger.debug("out onCallBackPromotionActity fillPromotionActity productSkn is:{}, cacheProductBo:{}", cacheProductBo.getErpProductId(),cacheProductBo);
		return this;
	}
	
	public static class PromotionActityWrapper
	{
		private List<PromotionBo> promotionBoList;
		
		public PromotionActityWrapper(List<PromotionBo> promotionBoList) {
			super();
			this.promotionBoList = promotionBoList;
		}
		
		public PromotionActityWrapper() {
			super();
		}

		public List<PromotionBo> getPromotionBoList() {
			return promotionBoList;
		}

		public void setPromotionBoList(List<PromotionBo> promotionBoList) {
			this.promotionBoList = promotionBoList;
		}
	}
}
