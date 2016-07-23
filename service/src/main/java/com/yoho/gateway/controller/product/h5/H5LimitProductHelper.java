package com.yoho.gateway.controller.product.h5;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.product.model.GoodsBo;
import com.yoho.product.model.GoodsSizeBo;
import com.yoho.product.model.LimitProductBo;
import com.yoho.product.model.ProductBo;
import com.yoho.service.model.promotion.LimitCodeUserBo;
import com.yoho.service.model.promotion.request.ProductLimitCodeReq;

//待h5重构后这段代码要废弃掉
@Service
@Deprecated
public class H5LimitProductHelper {
	
	private final Logger logger = LoggerFactory.getLogger(H5LimitProductHelper.class);
	
	@Autowired
	private ServiceCaller serviceCaller;
	
	
	public H5LimitProductHelper afterProcessProductStatus(Integer uid, ProductBo productBo) {
		//不是限量商品就不需要执行下面的逻辑
		if(null==productBo||!"Y".equals(productBo.getIsLimitBuy()))
		{
			return this;
		}
		// 1.首先判断是开售前还是开售后
		// 展示状态 
        // 1.开售前 立即分享获得限购码(如果已经抢光显示限购码已经被抢光,获取限购码成功之后按钮变成即将开售，如果有限购码就直接显示即将开售)
        // 2.开售后 如果售罄所有按钮均不展示，如果限购码被抢光显示立即购买不可点，如果有限购码，直接显示立即购买
		if(StringUtils.isEmpty(productBo.getLimitProductCode()))
		{	
			logger.warn("productBo limitProductCode is null by productBo skn:{}",productBo.getErpProductId());
			productBo.setIsLimitBuy("N");
			return this;
		}
		//首先要根据limitProductCode去查询限量商品信息
		LimitProductBo limitProductBo=serviceCaller.asyncCall("product.getLimitProductByCode", productBo.getLimitProductCode(), LimitProductBo.class).get(1);
		if(null==limitProductBo)
		{	
			logger.warn("limitProductBo is null by limitProductCode:{}",productBo.getLimitProductCode());
			productBo.setIsLimitBuy("N");
			return this;
		}
		// 如果此时limitProductBo的skn和productVo的skn不同，表示原来限购码批次已经切换过skn，原来skn已经变为普通商品
		if(productBo.getErpProductId()!=null && limitProductBo.getProductSkn()!=null && !productBo.getErpProductId().equals(limitProductBo.getProductSkn())){
			logger.info("product has be not limitBuy product skn:{},limitProduct skn:{}",productBo.getErpProductId(),limitProductBo.getProductSkn());
			productBo.setIsLimitBuy("N");
			return this;
		}
		boolean isSale= isHasSale(limitProductBo.getSaleTime());
		//开售后的逻辑
		if(isSale)
		{	
			buildHasSalestatus(uid,limitProductBo,productBo);
			
		}else
		{
			buildNotSalestatus(uid,limitProductBo,productBo);
		}
		return this;
	}
	
	/**
	 * 判断是否已经发售
	 * @param limitProductBo
	 * @return
	 */
	private boolean isHasSale(Integer saleTime) {
		boolean isSale=false;
		//如果没关联skn，那么一定是没开售的,如果开售时间大于当前时间也是开售了
		if(saleTime>getUnixTime())
		{
			isSale=false;
			//如果开售时间小于和等于当前时间也是开售了
		}else if(saleTime<=getUnixTime())
		{
			isSale=true;
		}
		return isSale;
	}
	
	/**
	 * 普通商品详情页
	 * @param uid 用户ID
	 * @param limitProductBo 限量商品信息
	 * @param productBo 普通商品信息
	 */
	private void buildNotSalestatus(Integer uid,LimitProductBo limitProductBo, ProductBo productBo) {
		// 0.开售前
		productBo.setSaleStatus(0);
		//用户不登录的情况下直接返回（可领取限购码）和是否开售
		if(null==uid)
		{	
			logger.info("uid is null may be use not login");
			//返回获取限购码的状态
			productBo.setShowStatus(1);
			return;
		}
		productBo.setShowStatus(getNotSalestatus(uid, productBo.getLimitProductCode(), limitProductBo.getBatchNo(),limitProductBo.getLimitProductType()));
	}
	
	
	/**
	 * 
	 * @param uid 用户ID
	 * @param limitProductBo 限量商品信息
	 * @param productBo 商品信息
	 */
	private void buildHasSalestatus(Integer uid,LimitProductBo limitProductBo, ProductBo productBo) {
		
		//1.首先要看有没有库存,没库存就直接按钮不展示
		//2.判断是否有限购码,如果有限购码再去判断用户是个有库存，如果没有限购码要去看限购码是否已经抢光
		//3.判断限购码是否已经被抢光
		//4.判断是否有库存
		//根据用户ID和限购商品的code去查询用户是否有这个商品的码
		// 设置为开售后
		productBo.setSaleStatus(1);
		//用户不登录的情况下直接返回（可领取限购码）和是否开售
		if(null==uid)
		{	
			logger.info("uid is null may be use not login");
			//返回获取限购码的状态
			productBo.setShowStatus(1);
			return;
		}
		//获取库存总数
		int storageNum=caculteStorageNum(productBo);
		productBo.setShowStatus(getHasSalestatus(uid, storageNum, productBo.getLimitProductCode(),limitProductBo.getBatchNo(),limitProductBo.getLimitProductType()));
	}
	
	/**
	 * 计算库存总数
	 * @param productBo
	 * @return
	 */
	private int caculteStorageNum(ProductBo productBo)
	{
		List<GoodsBo> goodsBoList=productBo.getGoodsList();
		if(CollectionUtils.isEmpty(goodsBoList))
		{
			logger.warn("ProductBo goodsBoList is empty productBo id:{}",productBo.getId());
			return 0;
		}
		int storageSumNum=0;
		for (GoodsBo goodsBo : goodsBoList) {
			List<GoodsSizeBo> sizeBoList=goodsBo.getGoodsSizeBoList();
			for (GoodsSizeBo goodsSizeBo : sizeBoList) {
				storageSumNum+=goodsSizeBo.getGoodsSizeStorageNum();
			}
		}
		return storageSumNum;
	}
	
	
	/**
	 * 未开售状态逻辑处理
	 * @param uid 用户ID
	 * @param limitProductCode 限量商品code
	 * @param batchNo 批次号
	 * @param limitProductType 限量商品类型（1.分享(默认是分享) 2.排队）
	 * @return
	 */
	private int getNotSalestatus(Integer uid,String limitProductCode, String batchNo,int limitProductType)
	{
		try
		{
			//1.先看用户有没有码
			ProductLimitCodeReq productLimitCodeReq=new ProductLimitCodeReq();
			productLimitCodeReq.setLimitProductCode(limitProductCode);
			productLimitCodeReq.setUid(uid);
			productLimitCodeReq.setBatchNo(batchNo);
			LimitCodeUserBo limitCodeUserBo=serviceCaller.call("promotion.getLimitCode", productLimitCodeReq, LimitCodeUserBo.class);
			if(null==limitCodeUserBo)
			{	
				//如果是排队的活动直接返回分享获取限购码
				if(limitProductType==2)
				{
					//分享获取限购码
					return 1;
				}
				int isAvailable=serviceCaller.call("promotion.checkAvailableLimitCode", productLimitCodeReq, Integer.class);
				if(isAvailable == 1)
				{	
					//立即分享获取限购码
					return 1;
				}else
				{	
					//限购码已经抢光
					return 5;
				}
			}else
			{	
				//即将开售
				return 6;
			}
			
		}catch(Exception e)
		{	
			logger.warn("invoke promotion getLimitCode or checkAvailableLimitCode failed!!",e);
			//如果抛异常，全部设置为即将开售,主要是不让页面出不来
			return 6;
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
	private int getHasSalestatus(Integer uid,Integer storageNum,String limitProductCode, String batchNo,int limitProductType)
	{
		if(storageNum<=0)
		{
			//已经售罄,去掉按钮
			return 3;
		}
		try
		{
			ProductLimitCodeReq productLimitCodeReq=new ProductLimitCodeReq();
			productLimitCodeReq.setLimitProductCode(limitProductCode);
			productLimitCodeReq.setUid(uid);
			productLimitCodeReq.setBatchNo(batchNo);
			LimitCodeUserBo limitCodeUserBo=serviceCaller.call("promotion.getLimitCode", productLimitCodeReq, LimitCodeUserBo.class);
			logger.debug("promotion get limitCodeUserBo is:{}",limitCodeUserBo);
			//说明用户没有这个限购码
			if(null==limitCodeUserBo)
			{	
				//如果是排队的活动直接返回分享获取限购码
				if(limitProductType==2)
				{
					//分享获取限购码
					return 1;
				}
				//0:没有可用的限购码，1：有限购码，2：限购码作废
				int isAvailable=serviceCaller.call("promotion.checkAvailableLimitCode", productLimitCodeReq, Integer.class);
				if(isAvailable ==1)
				{	
					//立即分享获取限购码
					return 1;
				}else {
					//已经抢光(立即购买灰色不可点)
					return 2;
				}
			}else
			{	
				//0.不可以使用 1.可使用  
				if(0==limitCodeUserBo.getUseFlag())
				{
					//已获取限购码，立即购买不可点击
					return 7;
				}else
				{	
					//立即购买
					return 4;
				}
			}
		}catch(Exception e)
		{
			logger.warn("invoke promotion getLimitCode or checkAvailableLimitCode failed!!",e);
			//如果抛异常，全部设置为已经抢光(立即购买灰色不可点击),主要是不让页面出不来
			return 2;
		}
	}
	
	/**
	 * 获取unix的秒时间
	 * @return
	 */
	private int getUnixTime()
	{
		return (int)(new Date().getTime() / 1000);
	}
}
