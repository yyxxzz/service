package com.yoho.gateway.cache;

import org.apache.commons.lang3.StringUtils;

/**
 * yh+模块名
 * @author caoyan
 *
 */
public final class KeyBuilder {
	
	// 商品一级缓存信息
	private static final String PRODUCT_LEVEL1_CACHEKEY="yh:gw:product:level1:";
	
	/**
	 * 根据优惠券ID查询已领取数量的key
	 * @param couponId
	 * @return
	 */
	public static String couponIdKeyBuilder(String couponId)
	{	
		if(null==couponId)
		{
			throw new IllegalArgumentException("couponId can't be null");
		}
		StringBuilder builder=new StringBuilder("yh:promotion:couponId:");
		builder.append(couponId);
		return builder.toString();
	}
	
	/**
	 * 根据优惠券ID和用户ID查询领取数量的key
	 * @param couponId
	 * @param uid
	 * @return
	 */
	public static String couponIdAndUidKeyBuilder(String couponId, String uid)
	{	
		if(null == couponId || null == uid)
		{
			throw new IllegalArgumentException("couponId or uid can't be null");
		}
		StringBuilder builder=new StringBuilder("yh:promotion:couponId:uid:");
		builder.append(couponId + ":" + uid);
		return builder.toString();
	}
	
	/**
	 * 根据productSkn查询商品基本信息的key
	 * @param productSkn
	 * @return
	 */
	public static String productDetailLevelOneKeyBuilder(Integer productSkn)
	{	
		if(null == productSkn)
		{
			throw new IllegalArgumentException("productSkn can't be null");
		}
		StringBuilder builder=new StringBuilder(PRODUCT_LEVEL1_CACHEKEY);
		builder.append(productSkn);
		return builder.toString();
	}
	
	/**
	 * 领券中心的key
	 * @param clientType 客户端类型
	 * @param contentCode 内容码
	 * @return
	 */
	public static String couponCenterKeyBuilder(String clientType, String contentCode){
		if(StringUtils.isEmpty(clientType) || StringUtils.isEmpty(contentCode)){
			throw new IllegalArgumentException("clientType or contentCode can't be null");
		}
		StringBuilder builder=new StringBuilder("yh:gw:clientType:contentCode:");
		builder.append(clientType + ":" + contentCode);
		return builder.toString();
	}
	
	public static String getUserCouponsKeyBuilder(Integer uid) {
        StringBuilder builder = new StringBuilder("yh:gw:").append("center:").append("coupons:");
        builder.append(uid);
        return builder.toString();
    }
	
	public static String getCouponCenterStatusKeyBuiler(String clientType, String contentCode){
		if(StringUtils.isEmpty(clientType) || StringUtils.isEmpty(contentCode)){
			throw new IllegalArgumentException("clientType or contentCode can't be null");
		}
		StringBuilder builder=new StringBuilder("yh:gw:clientType:contentCode:status:");
		builder.append(clientType + ":" + contentCode);
		return builder.toString();
	}
	
	public static String getPopupKeyBuilder(String uid, String recPos){
		if(StringUtils.isEmpty(uid) || StringUtils.isEmpty(recPos)){
			throw new IllegalArgumentException("uid or recPos can't be null");
		}
		StringBuilder builder=new StringBuilder("yh:gw:uid:recPos:");
		builder.append(uid + ":" + recPos);
		return builder.toString();
	}
	
}
