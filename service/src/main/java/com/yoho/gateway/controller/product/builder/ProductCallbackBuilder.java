package com.yoho.gateway.controller.product.builder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.yoho.product.model.GoodsBo;
import com.yoho.product.model.GoodsSizeBo;
import com.yoho.product.model.ProductBo;
import com.yoho.product.model.ProductCallBackDataBo;
import com.yoho.product.model.ProductTagBo;
import com.yoho.product.model.PromotionBo;
import com.yoho.product.model.StorageBo;
import com.yoho.service.model.promotion.PointActivityInfoBo;

public final class ProductCallbackBuilder {
	
	 private final static Logger logger = LoggerFactory.getLogger(ProductCallbackBuilder.class);
	/**
	 * 即将售罄的库存阀值
	 */
	private final static Integer STORAGE_THRESHOLD=2;
	
	
	/**
	 * 回填实时的库存和标签信息
	 * @param productBo 
	 * @param productCallBackDataBo
	 */
	public static void fillProductBo(ProductBo productBo, ProductCallBackDataBo productCallBackDataBo) 
	{
		if(null==productCallBackDataBo)
		{	
			logger.warn("productCallBackDataBo is empty");
			return;
		}
		//回填goodsSizeBo中的库存信息
		List<GoodsSizeBo> goodsSizeBoList = getGoodsSizeBo(productBo);
		
		//将库存写到GoodsSizeBo中
		buildGoodsSizeBo(productCallBackDataBo.getStorageBoList(),goodsSizeBoList);
		
		//回填标签
		buildProductTags(productBo,productCallBackDataBo);
		
	}
	
	public static List<PromotionBo> buildPromotion(PromotionBo[] promotionBoList,PointActivityInfoBo[] pointActivityInfoBoList) 
	{
		List<PromotionBo> allPromotionBoList=Lists.newArrayList();
		
		List<PromotionBo> pointActivityPromotionBoList=Lists.newArrayList();
		if(ArrayUtils.isNotEmpty(pointActivityInfoBoList)){
			for (PointActivityInfoBo pointActivityInfoBo : pointActivityInfoBoList) {
				PromotionBo promotionBo=new PromotionBo();
				promotionBo.setPromotionTitle(pointActivityInfoBo.getTitle());
				promotionBo.setPromotionType(pointActivityInfoBo.getType()+"");
				pointActivityPromotionBoList.add(promotionBo);
			}
		}
		if(CollectionUtils.isNotEmpty(pointActivityPromotionBoList))
		{
			allPromotionBoList.addAll(pointActivityPromotionBoList);
		}
		if(ArrayUtils.isNotEmpty(promotionBoList))
		{
			allPromotionBoList.addAll(Lists.newArrayList(promotionBoList));
		}
		return allPromotionBoList;
	}
	
	
	private static void buildProductTags(ProductBo productBo,
			ProductCallBackDataBo productCallBackDataBo) {
		
		List<ProductTagBo> productTagBoList=productBo.getProductTagBoList();
		if(CollectionUtils.isEmpty(productTagBoList))
		{
			return;
		}
		List<ProductTagBo> productTagBos=Lists.newArrayList(productTagBoList);
		for (ProductTagBo productTagBo : productTagBoList) 
		{
			if("is_new".equals(productTagBo.getTagValue())&&!isNewsTag(productBo))
			{
				productTagBos.remove(productTagBo);
			}
			if("is_soon_sold_out".equals(productTagBo.getTagValue())&&!isSoonSoldoutTag(productCallBackDataBo.getStorageBoList()))
			{
				productTagBos.remove(productTagBo);
			}
		}
		productBo.setProductTagBoList(productTagBos);
	}
	

	private static List<GoodsSizeBo> getGoodsSizeBo(ProductBo productBo) {
		List<GoodsBo> goodsBoList=productBo.getGoodsList();
		List<GoodsSizeBo> goodsSizeBoList=Lists.newArrayList();
		if(CollectionUtils.isEmpty(goodsBoList))
		{
			return goodsSizeBoList;
		}
		for (GoodsBo goodsBo : goodsBoList) {
			if(CollectionUtils.isNotEmpty(goodsBo.getGoodsSizeBoList()))
			{
				goodsSizeBoList.addAll(goodsBo.getGoodsSizeBoList());
			}
		}
		return goodsSizeBoList;
	}
	
	
	private static void buildGoodsSizeBo(List<StorageBo> storages,
			List<GoodsSizeBo> goodsSizeBoList) {
		for (GoodsSizeBo goodsSizeBo : goodsSizeBoList) {
			for (StorageBo storageBo : storages) {
				//skuID才是唯一对应的
				if(goodsSizeBo.getGoodsSizeSkuId().equals(storageBo.getErpSkuId()))
				{
					goodsSizeBo.setGoodsSizeStorageNum(storageBo.getStorageNum());
					break;
				}
			}
		}
	}
	
	private static boolean isNewsTag(ProductBo productBo) 
	{
		//7天之内的是新品上架
		if(null!=productBo.getShelveTime() && productBo.getShelveTime()>calculateDayOfTime(-7))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param day
	 * @return
	 */
	private static long calculateDayOfTime(Integer day)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, day);  //设置时间
		return calendar.getTimeInMillis()/1000;
	}
	
	
	private static boolean isSoonSoldoutTag(List<StorageBo> storages) 
	{
		if(CollectionUtils.isNotEmpty(storages))
		{	 
			int storageSumNum=0;
			for (StorageBo storage : storages) {
				storageSumNum+=storage.getStorageNum();
			}
			if(storageSumNum<=STORAGE_THRESHOLD)
			{
				return true;
			}
		}
		return false;
	}
}
