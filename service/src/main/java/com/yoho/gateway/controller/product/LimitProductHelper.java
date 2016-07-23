package com.yoho.gateway.controller.product;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.model.product.LimitProductVo;
import com.yoho.gateway.model.product.ProductVo;
import com.yoho.gateway.model.product.SaleStatusVo;
import com.yoho.product.model.LimitProductBo;
import com.yoho.product.model.ProductBo;
import com.yoho.product.model.ReminderWrapper;
import com.yoho.product.model.StorageBo;
import com.yoho.product.request.BaseRequest;
import com.yoho.product.request.BatchBaseRequest;
import com.yoho.service.model.promotion.LimitCodeUserBo;
import com.yoho.service.model.promotion.request.ProductLimitCodeReq;

@Service
public class LimitProductHelper {

	private final Logger logger = LoggerFactory.getLogger(LimitProductController.class);

	@Autowired
	private ServiceCaller serviceCaller;
	/**
	 * 处理提醒相关信息包括提醒人数总数和是否提醒
	 * @param uid
	 * @param limitProductVo
	 * @return
	 */
	public LimitProductHelper afterProcessReminder(Integer uid,LimitProductVo limitProductVo) {
		if(null==limitProductVo)
		{
			return this;
		}
		try
		{
			BaseRequest<String> request=new BaseRequest<String>();
			request.setParam(limitProductVo.getLimitProductCode());
			request.setUserId(uid);
			//超过1s就不要再掉了
			ReminderWrapper reminderWrapper =serviceCaller.asyncCall("product.queryReminderCountAndHasReminder", request, ReminderWrapper.class).get(1);
			limitProductVo.setReminderNum(reminderWrapper.getReminderCount());
			limitProductVo.setAlertFlag(reminderWrapper.isReminder());
		}catch(Exception e)
		{
			logger.warn("invoke product.queryReminderCountAndHasReminder failed!!", e);
			limitProductVo.setReminderNum(getRandomInt(200, 1000));
			limitProductVo.setAlertFlag(false);
		}
		return this;
	}


	public LimitProductHelper afterProcessShareUrl(LimitProductVo limitProductVo) {
		if(null==limitProductVo)
		{
			return this;
		}
		//http://m.yohobuy.com
		//http://m.dev.yohobuy.com/product/detail/limit?code=2016030711453248
		limitProductVo.setShareUrl("http://m.yohobuy.com/product/detail/limit?code="+limitProductVo.getLimitProductCode());
		return this;
	}
	/**
	 * 1.立即分享限购码
	 * 2.已经抢光
	 * 3.已经售罄
	 * 4.立即购买
	 * 5.限购码已抢光
	 * 6.即将开售
	 * 处理开售前后的状态
	 * @param uid
	 * @param limitProductVo
	 */
	public LimitProductHelper afterProcessStatus(Integer uid, LimitProductVo limitProductVo) {
		if(null==limitProductVo)
		{
			return this;
		}
	
		//1.首先判断是开售前还是开售后
		// 展示状态
		// 1.开售前 立即分享获得限购码(如果已经抢光显示限购码已经被抢光,获取限购码成功之后按钮变成即将开售，如果有限购码就直接显示即将开售)
		// 2.开售后 如果售罄所有按钮均不展示，如果限购码被抢光显示立即购买不可点，如果有限购码，直接显示立即购买
		boolean isSale= isHasSale(limitProductVo.getProductSkn(),limitProductVo.getOldSaleTime());
		//如果用户不登录就不要执行下面的操作,直接回获取限购码的状态
		if(null==uid||0==uid)
		{
			limitProductVo.setSaleStatus(isSale?1:0);
			//返回获取限购码的状态
			limitProductVo.setShowStatus(1);
			return this;
		}
		//开售后的逻辑
		if(isSale)
		{
			buildHasSalestatus(uid,limitProductVo);

		}else
		{
			buildNotSalestatus(uid,limitProductVo);
		}
		return this;
	}


	public LimitProductHelper afterProcessProductStatus(Integer uid, ProductVo productVo) {
		//不是限量商品就不需要执行下面的逻辑
		if(null==productVo||!productVo.isLimitBuy())
		{
			return this;
		}
		if(StringUtils.isEmpty(productVo.getLimitProductCode()))
		{
			logger.warn("productVo limitProductCode is null by productVo skn:{}",productVo.getProductSkn());
			productVo.setLimitBuy(false);
			return this;
		}
		//首先要根据limitProductCode去查询限量商品信息
		LimitProductBo limitProductBo=serviceCaller.asyncCall("product.getLimitProductByCode", productVo.getLimitProductCode(), LimitProductBo.class).get(1);
		if(null==limitProductBo)
		{
			logger.warn("limitProductBo is null by limitProductCode:{}",productVo.getLimitProductCode());
			productVo.setLimitBuy(false);
			return this;
		}
		// 如果此时limitProductBo的skn和productVo的skn不同，表示原来限购码批次已经切换过skn，原来skn已经变为普通商品
		if(productVo.getProductSkn()!=null && limitProductBo.getProductSkn()!=null && !productVo.getProductSkn().equals(limitProductBo.getProductSkn())){
			logger.info("product has be not limitBuy product skn:{},limitProduct skn:{}",productVo.getProductSkn(),limitProductBo.getProductSkn());
			productVo.setLimitBuy(false);
			return this;
		}

		// 1.首先判断是开售前还是开售后
		// 展示状态
		// 1.开售前 立即分享获得限购码(如果已经抢光显示限购码已经被抢光,获取限购码成功之后按钮变成即将开售，如果有限购码就直接显示即将开售)
		// 2.开售后 如果售罄所有按钮均不展示，如果限购码被抢光显示立即购买不可点，如果有限购码，直接显示立即购买
		boolean isSale= isHasSale(productVo.getProductSkn(),limitProductBo.getSaleTime());
		//开售后的逻辑
		if(isSale)
		{
			buildHasSalestatus(uid,limitProductBo,productVo);

		}else
		{
			buildNotSalestatus(uid,limitProductBo,productVo);
		}
		return this;
	}

	/**
	 * 判断是否已经发售
	 * @return
	 */
	private boolean isHasSale(Integer productSkn,Integer saleTime) {
		boolean isSale=false;
		//如果没关联skn，那么一定是没开售的,如果开售时间大于当前时间也是开售了
		int unixTime = getUnixTime();
		logger.info("isHasSale saleTime:{},productSkn:{},unixTime:{}",saleTime,productSkn, unixTime);
		if(null==productSkn||saleTime>unixTime)
		{
			isSale=false;
			//如果开售时间小于和等于当前时间也是开售了
		}else if(saleTime<=getUnixTime())
		{
			isSale=true;
		}
		logger.info("isHasSale isSale:{},productSkn:{},unixTime:{}",isSale,productSkn, unixTime);
		return isSale;
	}

	/**
	 * 根据SKN获取库存总数
	 * @param productSkn
	 * @return
	 */
	private int getStorageNum(Integer productSkn)
	{
		try
		{
			if(productSkn == 0){
				return 0;
			}
			BaseRequest<Integer> request=new BaseRequest<Integer>();
			request.setParam(productSkn);
			StorageBo[] storageBoList=serviceCaller.call("product.queryStorageByProductSkn", request, StorageBo[].class);
			int storageSumNum=0;
			for (StorageBo storageBo : storageBoList) {
				storageSumNum+=storageBo.getStorageNum();
			}
			return storageSumNum;
		}catch(Exception e)
		{
			logger.warn("invoke product.queryStorageByProductSkn failed!!!", e);
			return 0;
		}
	}

	/**
	 * 构造没开售前的销售按钮状态(限量商品详情页)
	 * @param uid
	 * @param limitProductVo
	 */
	private void buildNotSalestatus(Integer uid, LimitProductVo limitProductVo) {
		// 0.开售前
		limitProductVo.setSaleStatus(0);
		limitProductVo.setShowStatus(getNotSalestatus(uid,
				limitProductVo.getLimitProductCode(),
				limitProductVo.getBatchNo(),
				limitProductVo.getLimitProductType()).getSaleStatus());
	}

	/**
	 * 普通商品详情页
	 * @param uid 用户ID
	 * @param limitProductBo 批次号信息
	 * @param productVo
	 */
	private void buildNotSalestatus(Integer uid,LimitProductBo limitProductBo, ProductVo productVo) {
		// 0.开售前
		productVo.setSaleStatus(0);
		//用户不登录的情况下直接返回（可领取限购码）和是否开售
		if(null==uid||0==uid)
		{
			logger.info("uid is null may be use not login limitProductCode is:{}",limitProductBo.getLimitProductCode());
			//返回获取限购码的状态
			productVo.setShowStatus(1);
			return;
		}
		SaleStatusVo saleStatusVo = getNotSalestatus(uid,
				productVo.getLimitProductCode(), limitProductBo.getBatchNo(),
				limitProductBo.getLimitProductType());
		productVo.setShowStatus(saleStatusVo.getSaleStatus());
		productVo.setLimitProductSku(null == saleStatusVo.getLimitProdutSku() ? "" : saleStatusVo.getLimitProdutSku());
	}



	/**
	 * 构造没开售后的销售按钮状态
	 * @param uid
	 * @param limitProductVo
	 */
	private void buildHasSalestatus(Integer uid, LimitProductVo limitProductVo) {
		//1.首先要看有没有库存,没库存就直接按钮不展示
		//2.判断是否有限购码,如果有限购码再去判断用户是个有库存，如果没有限购码要去看限购码是否已经抢光
		//3.判断限购码是否已经被抢光
		//4.判断是否有库存
		//5.判断商品是否下架
		// 1.开售后
		limitProductVo.setSaleStatus(1);
		
		//获取库存总数
		int storageNum=getStorageNum(limitProductVo.getProductSkn());

		limitProductVo.setShowStatus(getHasSalestatus(uid, storageNum,
				limitProductVo.getLimitProductCode(),
				limitProductVo.getBatchNo(),
				limitProductVo.getLimitProductType()).getSaleStatus());
		
		//获取商品基本信息，判断该商品是否下架
		BatchBaseRequest<Integer> baseRequest=new BatchBaseRequest<Integer>();
		baseRequest.setParams(Lists.newArrayList(limitProductVo.getProductSkn()));
		ProductBo[] productBos = serviceCaller.call("product.batchQueryProductBasicInfo", baseRequest, ProductBo[].class);
			if(null != productBos && productBos.length > 0){
				ProductBo productBo = productBos[0];
					//0表示下架
					if(0 == productBo.getStatus()){
						limitProductVo.setShowStatus(3);
					}
			}
	}


	/**
	 *
	 * @param uid 用户ID
	 * @param limitProductBo 批次号信息
	 * @param productVo
	 */
	private void buildHasSalestatus(Integer uid,LimitProductBo limitProductBo,ProductVo productVo) {

		//1.首先要看有没有库存,没库存就直接按钮不展示
		//2.判断是否有限购码,如果有限购码再去判断用户是个有库存，如果没有限购码要去看限购码是否已经抢光
		//3.判断限购码是否已经被抢光
		//4.判断是否有库存
		// 设置为开售后
		productVo.setSaleStatus(1);
		//用户不登录的情况下直接返回（可领取限购码）和是否开售
		if(null==uid||0==uid)
		{
			logger.info("uid is null may be use not login limitProductCode is:{}",limitProductBo.getLimitProductCode());
			//返回获取限购码的状态
			productVo.setShowStatus(1);
			return;
		}
		//获取库存总数
		int storageNum=productVo.getStorageSum();
		SaleStatusVo saleStatusVo = getHasSalestatus(uid, storageNum,productVo.getLimitProductCode(), limitProductBo.getBatchNo(),
				limitProductBo.getLimitProductType());
		productVo.setShowStatus(saleStatusVo.getSaleStatus());
		productVo.setLimitProductSku(null == saleStatusVo.getLimitProdutSku() ? "" : saleStatusVo.getLimitProdutSku());
	}


	/**
	 * 未开售状态逻辑处理
	 * @param uid 用户ID
	 * @param limitProductCode 限量商品code
	 * @param batchNo 批次号
	 * @param limitProductType
	 * @return
	 */
	private SaleStatusVo getNotSalestatus(Integer uid,String limitProductCode, String batchNo, int limitProductType)
	{
		try
		{
			//根据用户ID和限购商品的code去查询用户是否有这个商品的码
			ProductLimitCodeReq productLimitCodeReq=new ProductLimitCodeReq();
			productLimitCodeReq.setLimitProductCode(limitProductCode);
			productLimitCodeReq.setUid(uid);
			productLimitCodeReq.setBatchNo(batchNo);
			//0:没有可用的限购码，1：有可用的限购码，2：限购码已经作废，或者过期
			int isAvailable=serviceCaller.call("promotion.checkAvailableLimitCode", productLimitCodeReq, Integer.class);
			logger.info("getNotSalestatus.checkAvailableLimitCode result is {}",isAvailable);
			if(isAvailable == 2){
				//限购码已经过期或者作废，设置成3，让APP在限定商品详情页不展示
				return new SaleStatusVo(3);
			}
			LimitCodeUserBo limitCodeUserBo=serviceCaller.call("promotion.getLimitCode", productLimitCodeReq, LimitCodeUserBo.class);
			if(null==limitCodeUserBo)
			{
				//如果是排队的活动直接返回分享获取限购码
				if(limitProductType==2)
				{
					//分享获取限购码
					return new SaleStatusVo(1);
				}
				if(isAvailable == 1)
				{
					//立即分享获取限购码
					return new SaleStatusVo(1);
				}
				if(isAvailable == 0){
					//没有可用的限购码，限购码已经抢光
					return new SaleStatusVo(5);
				}else
				{
					//已经售罄
					return new SaleStatusVo(3);
				}
			}else
			{
				//即将开售
				SaleStatusVo saleStatusVo = new SaleStatusVo(6);
				String limitProductSku = limitCodeUserBo.getProductSku();
				if(StringUtils.isNotEmpty(limitProductSku)){
					saleStatusVo.setLimitProdutSku(limitProductSku);
				}
				return saleStatusVo;
			}

		}catch(Exception e)
		{
			logger.warn("invoke promotion getLimitCode or checkAvailableLimitCode failed!!",e);
			//如果抛异常，全部设置为即将开售,主要是不让页面出不来
			return new SaleStatusVo(6);
		}
	}

	/**
	 *  已经发售后的状态处理逻辑
	 * @param uid 用户Id
	 * @param storageNum 库存总数
	 * @param limitProductCode 限量商品code
	 * @param batchNo 批次信息
	 * @return
	 */
	private SaleStatusVo getHasSalestatus(Integer uid,Integer storageNum,String limitProductCode, String batchNo,int limitProductType)
	{
		if(storageNum<=0)
		{
			//已经售罄,去掉按钮
			return new SaleStatusVo(3);
		}
		try
		{
			//根据用户ID和限购商品的code去查询用户是否有这个商品的码
			ProductLimitCodeReq productLimitCodeReq=new ProductLimitCodeReq();
			productLimitCodeReq.setLimitProductCode(limitProductCode);
			productLimitCodeReq.setUid(uid);
			productLimitCodeReq.setBatchNo(batchNo);
			//0:没有可用的限购码，1：有可用的限购码，2：限购码已经作废，或者过期
			int isAvailable=serviceCaller.call("promotion.checkAvailableLimitCode", productLimitCodeReq, Integer.class);
			logger.info("getHasSalestatus.checkAvailableLimitCode result is {}",isAvailable);
			if(isAvailable == 2){
				//限购码作废
				return new SaleStatusVo(3);
			}
			LimitCodeUserBo limitCodeUserBo=serviceCaller.call("promotion.getLimitCode", productLimitCodeReq, LimitCodeUserBo.class);

			//说明用户没有这个限购码
			if(null==limitCodeUserBo)
			{
				//如果是排队的活动直接返回分享获取限购码
				if(limitProductType==2)
				{
					//分享获取限购码
					return new SaleStatusVo(1);
				}
				if(isAvailable==1)
				{
					//分享获取限购码
					return new SaleStatusVo(1);
				}
				if(isAvailable == 0){
					//限购码被抢光，立即购买按钮不能点击
					return new SaleStatusVo(2);
				}else
				{
					//限购码作废 ---已经售罄
					return new SaleStatusVo(3);

				}
			}else
			{
				//0.不可以使用 1.可使用
				if(0==limitCodeUserBo.getUseFlag())
				{
					//已获取限购码，立即购买不可点击
					return new SaleStatusVo(7);
				}else
				{
					//立即购买
					SaleStatusVo saleStatusVo = new SaleStatusVo(4);
					String limitProductSku = limitCodeUserBo.getProductSku();
					if(StringUtils.isNotEmpty(limitProductSku)){
						saleStatusVo.setLimitProdutSku(limitProductSku);
					}
					return saleStatusVo;
				}
			}
		}catch(Exception e)
		{
			logger.warn("invoke promotion getLimitCode or checkAvailableLimitCode failed!!",e);
			//如果抛异常，全部设置为已经抢光(立即购买灰色不可点击),主要是不让页面出不来
			return new SaleStatusVo(2);
		}
	}

	/**
	 * 获取指定范围的随机数
	 * @param a
	 * @param b
	 * @return
	 */
	private int getRandomInt(int a, int b)
	{
		if (a > b || a < 0)
			return -1;
		// 下面两种形式等价
		// return a + (int) (new Random().nextDouble() * (b - a + 1));
		return a + (int) (Math.random() * (b - a + 1));
	}

	/**
	 * 获取unix的秒时间
	 * @return
	 */
	public int getUnixTime()
	{
		return (int)(new Date().getTime() / 1000);
	}
}
