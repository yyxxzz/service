package com.yoho.gateway.model.response;

import com.yoho.service.model.BaseBO;

/**
 * @author xinfei
 *
 */
@SuppressWarnings("serial")
public class MyIndexRspVO extends BaseBO {

	/**
	 * 待付款数量
	 */
	private int wait_pay_num;

	/**
	 * 待发货
	 */
	private int wait_cargo_num;

	/**
	 * 待收货
	 */
	private int send_cargo_num;

	/**
	 * 收藏的品牌数量
	 */
	private int brand_favorite_total;

	/**
	 * 收藏的商品数量
	 */
	private int product_favorite_total;

	/**
	 * 未读消息总数
	 */
	private int inbox_total;

	/**
	 * 待评价数量
	 */
	private int comment_total;

	/**
	 * 退/换货数量
	 */
	private int refund_exchange_num;

	/**
	 * 有货币数量
	 */
	private int yoho_coin_num;

	/**
	 * 优惠券数量
	 */
	private int coupon_num;

	/**
	 * 浏览记录
	 */
	private int product_browse;
	
	/**
	 * 已评价记录
	 */
	private int showOrderNum;
	
	/**
	 * 待评价记录
	 */
	private int toShareOrderNum;
	
	/**
	 * 限购码记录
	 */
	private int limitCodeNum;
	
	public MyIndexRspVO(){
		
	}
	
	public MyIndexRspVO(int wait_pay_num, int wait_cargo_num,
			int send_cargo_num, int brand_favorite_total,
			int product_favorite_total, int inbox_total, int comment_total,
			int refund_exchange_num, int yoho_coin_num, int coupon_num,
			int product_browse, int showOrderNum, int toShareOrderNum, int limitCodeNum) {
		this.wait_pay_num = wait_pay_num;
		this.wait_cargo_num = wait_cargo_num;
		this.send_cargo_num = send_cargo_num;
		this.brand_favorite_total = brand_favorite_total;
		this.product_favorite_total = product_favorite_total;
		this.inbox_total = inbox_total;
		this.comment_total = comment_total;
		this.refund_exchange_num = refund_exchange_num;
		this.yoho_coin_num = yoho_coin_num;
		this.coupon_num = coupon_num;
		this.product_browse = product_browse;
		this.showOrderNum = showOrderNum;
		this.toShareOrderNum = toShareOrderNum;
		this.limitCodeNum = limitCodeNum;
	}

	public int getWait_pay_num() {
		return wait_pay_num;
	}

	public void setWait_pay_num(int wait_pay_num) {
		this.wait_pay_num = wait_pay_num;
	}

	public int getWait_cargo_num() {
		return wait_cargo_num;
	}

	public void setWait_cargo_num(int wait_cargo_num) {
		this.wait_cargo_num = wait_cargo_num;
	}

	public int getSend_cargo_num() {
		return send_cargo_num;
	}

	public void setSend_cargo_num(int send_cargo_num) {
		this.send_cargo_num = send_cargo_num;
	}

	public int getBrand_favorite_total() {
		return brand_favorite_total;
	}

	public void setBrand_favorite_total(int brand_favorite_total) {
		this.brand_favorite_total = brand_favorite_total;
	}

	public int getProduct_favorite_total() {
		return product_favorite_total;
	}

	public void setProduct_favorite_total(int product_favorite_total) {
		this.product_favorite_total = product_favorite_total;
	}

	public int getInbox_total() {
		return inbox_total;
	}

	public void setInbox_total(int inbox_total) {
		this.inbox_total = inbox_total;
	}

	public int getComment_total() {
		return comment_total;
	}

	public void setComment_total(int comment_total) {
		this.comment_total = comment_total;
	}

	public int getRefund_exchange_num() {
		return refund_exchange_num;
	}

	public void setRefund_exchange_num(int refund_exchange_num) {
		this.refund_exchange_num = refund_exchange_num;
	}

	public int getYoho_coin_num() {
		return yoho_coin_num;
	}

	public void setYoho_coin_num(int yoho_coin_num) {
		this.yoho_coin_num = yoho_coin_num;
	}

	public int getCoupon_num() {
		return coupon_num;
	}

	public void setCoupon_num(int coupon_num) {
		this.coupon_num = coupon_num;
	}

	public int getProduct_browse() {
		return product_browse;
	}

	public void setProduct_browse(int product_browse) {
		this.product_browse = product_browse;
	}

	public int getShowOrderNum() {
		return showOrderNum;
	}

	public void setShowOrderNum(int showOrderNum) {
		this.showOrderNum = showOrderNum;
	}

	public int getToShareOrderNum() {
		return toShareOrderNum;
	}

	public void setToShareOrderNum(int toShareOrderNum) {
		this.toShareOrderNum = toShareOrderNum;
	}

	public int getLimitCodeNum() {
		return limitCodeNum;
	}

	public void setLimitCodeNum(int limitCodeNum) {
		this.limitCodeNum = limitCodeNum;
	}

}
