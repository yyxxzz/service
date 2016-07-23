package com.yoho.gateway.controller.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.MemecacheClientHolder;
import com.yoho.product.model.ProductBo;
import com.yoho.product.request.BaseRequest;

@Service
public class ProductCacheFinder {
	
    private final Logger logger = LoggerFactory.getLogger(ProductCacheFinder.class);
    
	@Autowired
    private ServiceCaller serviceCaller;
	
	@Autowired
	private MemecacheClientHolder memecacheClientHolder;
	
	@Autowired
	private CallBackChain callBackChain;
	
	private final static String LEVEL1_CACHEKEY="yh:gw:product:level1:";
	
	private final static String LEVEL2_CACHEKEY="yh:gw:product:level2:";
	/**
	 * 查询ProductBo
	 * @param productskn
	 * @param productId
	 * @param userId
	 * @param appversion  app版本(为了兼容评论功能，根据app版本去判断，这里主要是出于性能考虑)
	 */
	public ProductBo fetchProductBo(Integer productskn, Integer productId,
			Integer userId, String appversion) {
		//skn不为空就根据skn查询
		if(null!=productskn)
		{	
			return fetchProductBySkn(productskn,userId,appversion);
			
		}//商品ID不为空就根据ID查询
		else if(null!=productId)
		{	
			return fetchProductById(productId,userId,appversion);
		}
		return null;
	}
	
	/**
	 * 
	 * @param productId 商品ID
	 * @param userId 用户ID
	 * @param appversion  app版本
	 * @return
	 */
	private ProductBo fetchProductById(Integer productId, Integer userId,String appversion) {
		//fetch cache
		ProductBo cacheProductBo=fetchProductFromCache(LEVEL1_CACHEKEY+productId,userId,appversion);
		if(null!=cacheProductBo)
		{
			return cacheProductBo;
		}
		BaseRequest<Integer> baseRequest=new BaseRequest<Integer>();
		//构造查询ProductBo请求
		baseRequest.setUserId(userId);
		baseRequest.setParam(productId);
		try
		{	
			//3秒超时
			cacheProductBo=serviceCaller.asyncCall("product.queryProductDetailByProductId", baseRequest, ProductBo.class).get(3);
			
		}catch(Throwable e)
		{	
			logger.warn("queryProductDetailByProductId failed productId is:{}",productId,e);
			//服务不可用时走二级缓存,并回填到一级缓存中
			if(shouldGetFromLevel2(e))
			{	
				ProductBo productBo=getFromLevel2Cache(productId);
				return productBo;
			}
			throw e;
		}
		
		//20分钟
		if(null!=cacheProductBo)
		{
			memecacheClientHolder.getLevel1Cache().set(LEVEL1_CACHEKEY+productId, 300, new ProductCacheWrapper(cacheProductBo, ProductCacheWrapper.CACHE_LEVEL1));
			memecacheClientHolder.getLevel2Cache().set(LEVEL2_CACHEKEY+productId, memecacheClientHolder.getLevel2Expire(), new ProductCacheWrapper(cacheProductBo, ProductCacheWrapper.CACHE_LEVEL2));
		}
		//回填评论,兼容老版本商品详情页需要返回评论的情况，在3.9之后的版本就不回调评论服务了
		//回填全局促销活动信息
		callBackChain.onCallBackComment(cacheProductBo, appversion)
				.onCallBackUserFavorite(cacheProductBo, userId)
				.onCallBackConsult(cacheProductBo, appversion)
				.onCallBackPromotionActity(cacheProductBo, appversion);
		return cacheProductBo;
	}
	
	/**
	 * 
	 * @param productskn 商品SKN
	 * @param userId 用户ID
	 * @param appversion app版本号
	 * @return
	 */
	private ProductBo fetchProductBySkn(Integer productskn, Integer userId,String appversion) {
		//fetch cache
		ProductBo cacheProductBo=fetchProductFromCache(LEVEL1_CACHEKEY+productskn,userId,appversion);
		if(null!=cacheProductBo)
		{
			return cacheProductBo;
		}
		BaseRequest<Integer> baseRequest=new BaseRequest<Integer>();
		//构造查询ProductBo请求
		baseRequest.setUserId(userId);
		baseRequest.setParam(productskn);
		try
		{	
			//3秒超时
			cacheProductBo=serviceCaller.asyncCall("product.queryProductDetailBySKN", baseRequest, ProductBo.class).get(3);
			
		}catch(Throwable e)
		{	
			logger.warn("queryProductDetailBySKN failed productskn is:{}",productskn,e);
			//服务不可用时走二级缓存,并回填到一级缓存中
			if(shouldGetFromLevel2(e))
			{	
				cacheProductBo=getFromLevel2Cache(productskn);
				return cacheProductBo;
			}
			throw e;
		}
		//20分钟
		if(null!=cacheProductBo)
		{
			memecacheClientHolder.getLevel1Cache().set(LEVEL1_CACHEKEY+productskn, 300, new ProductCacheWrapper(cacheProductBo, ProductCacheWrapper.CACHE_LEVEL1));
			memecacheClientHolder.getLevel2Cache().set(LEVEL2_CACHEKEY+productskn, memecacheClientHolder.getLevel2Expire(), new ProductCacheWrapper(cacheProductBo, ProductCacheWrapper.CACHE_LEVEL2));
		}
		//回填评论,兼容老版本商品详情页需要返回评论的情况，在3.9之后的版本就不回调评论服务了
		//回填全局促销活动信息
		callBackChain.onCallBackComment(cacheProductBo, appversion)
				.onCallBackUserFavorite(cacheProductBo, userId)
				.onCallBackConsult(cacheProductBo, appversion)
				.onCallBackPromotionActity(cacheProductBo, appversion);
		return cacheProductBo;
	}

	/**
	 * 
	 * @param key 缓存中的key
	 * @param userId 用户ID
	 * @param appversion app版本号
	 * @return
	 */
	private ProductBo fetchProductFromCache(final String key, final Integer userId, final String appversion) {
		ProductCacheWrapper productCacheWrapper=memecacheClientHolder.getLevel1Cache().get(key, ProductCacheWrapper.class);
		logger.debug("fetchProductFromCache key is:{}, userId:{}, productCacheWrapper:{}", key, userId, productCacheWrapper);
		if(null==productCacheWrapper)
		{
			return null;
		}
		//如果缓存来自于二级缓存就直接返回了,不需要再回填一些实时数据，因为此时灾难可能已经发生了
		if(productCacheWrapper.getCacheFromLevel()==ProductCacheWrapper.CACHE_LEVEL2)
		{
			logger.debug("fetchProductFromCache Level2 key is:{}, userId:{}, productCacheWrapper:{}", key, userId, productCacheWrapper);
			return productCacheWrapper.getProductBo();
			
		}else
		{
			callBackChain.onCallBackRealtimeData(productCacheWrapper.getProductBo())
					.onCallBackUserFavorite(productCacheWrapper.getProductBo(),userId)
					.onCallBackComment(productCacheWrapper.getProductBo(),appversion)
					.onCallBackConsult(productCacheWrapper.getProductBo(), appversion)
					.onCallBackPromotionActity(productCacheWrapper.getProductBo(), appversion);
			return productCacheWrapper.getProductBo();
		}
	}

	/**
     * 只有不是ServiceException 和  GatewayException 的时候，才需要从二级缓存中获取数据
     *
     * @param ex
     * @return
     */
    private boolean shouldGetFromLevel2(Throwable ex) {
        if (ex instanceof ServiceException) {
            return false;
        }
        return true;
    }
	
	/**
	 * 从二级缓存获取
	 * @param key
	 * @return
	 */
	private ProductBo getFromLevel2Cache(final Integer key)
	{
		logger.info("begin queryProductDetailBySKN from Level2Cache key is:{}",key);
		ProductCacheWrapper productCacheWrapper=memecacheClientHolder.getLevel2Cache().get(LEVEL2_CACHEKEY+key, ProductCacheWrapper.class);
		if(null!=productCacheWrapper)
		{	
			logger.info("queryProductDetailBySKN from Level2Cache key is:{}",key);
			//回填到一级缓存2分钟
			memecacheClientHolder.getLevel1Cache().set(LEVEL1_CACHEKEY+key, 120, new ProductCacheWrapper(productCacheWrapper.getProductBo(), ProductCacheWrapper.CACHE_LEVEL2));
			return productCacheWrapper.getProductBo();
		}
		logger.info("end queryProductDetailBySKN from Level2Cache is empty key is:{}",key);
		return null;
	}
	
	/**
	 * 对gateway的缓存做一层Wrapper
	 * @author xieyong
	 *
	 */
	public static class ProductCacheWrapper
	{
		private ProductBo productBo;
		
		/**
		 * 默认来自于第一级的缓存
		 */
		private int cacheFromLevel=1;
		
		public final static int CACHE_LEVEL1=1;
		
		public final static int CACHE_LEVEL2=2;
		
		
		public ProductCacheWrapper(ProductBo productBo, int cacheFromLevel) {
			super();
			this.productBo = productBo;
			this.cacheFromLevel = cacheFromLevel;
		}
		
		public ProductCacheWrapper()
		{
			super();
		}

		public ProductBo getProductBo() {
			return productBo;
		}

		public void setProductBo(ProductBo productBo) {
			this.productBo = productBo;
		}

		public int getCacheFromLevel() {
			return cacheFromLevel;
		}

		public void setCacheFromLevel(int cacheFromLevel) {
			this.cacheFromLevel = cacheFromLevel;
		}

		@Override
		public String toString() {
			return "ProductCacheWrapper [productBo=" + productBo
					+ ", cacheFromLevel=" + cacheFromLevel + "]";
		}

	}
}
