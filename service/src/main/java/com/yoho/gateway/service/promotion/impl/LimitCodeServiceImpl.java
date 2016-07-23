package com.yoho.gateway.service.promotion.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.model.promotion.LimitCodeProductVo;
import com.yoho.gateway.model.promotion.LimitCodeVo;
import com.yoho.gateway.service.promotion.LimitCodeService;
import com.yoho.product.model.ProductPriceBo;
import com.yoho.product.request.BatchBaseRequest;
import com.yoho.product.request.LimitProductSkuReqBo;
import com.yoho.product.response.LimitProductSkuRspBo;
import com.yoho.service.model.promotion.LimitCodeProductBo;
import com.yoho.service.model.promotion.LimitProductPromotionBo;

/**
 * 组装限购码商品接口
 * @author wangshusheng
 * @Time 2016/2/17
 *
 */
@Service
public class LimitCodeServiceImpl implements LimitCodeService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LimitCodeServiceImpl.class);

	// 根据skn批量查询商品价格
	private final String QUERY_PRICE_LIMITCODE_SERVICE_NAME = "product.queryProductPriceBySkns";

	//根据uid,skn批量查询颜色，尺码
	private final String QUERY_SKU_LIMITCODE_SERVICE_NAME = "product.getUserSkuByUid";
	@Autowired
	private ServiceCaller serviceCaller;

	@Override
	public LimitCodeVo getLimitCodeVo(LimitCodeProductBo[] limitCodeProducts) {
		if (limitCodeProducts==null || limitCodeProducts.length==0){
			return new LimitCodeVo();
		}
		// 根据skn批量查询商品价格
		getLimitProductSknPrice(limitCodeProducts);

		//skn是批量查询颜色和尺码
		getLimitProductSku(limitCodeProducts);

		LimitCodeVo limitCodeVo = getLimitProductStatus(limitCodeProducts);

		return limitCodeVo;
	}

	private void getLimitProductSku(LimitCodeProductBo[] limitCodeProducts){
//		List<Integer> sknList = new ArrayList<Integer>();
		LimitProductSkuReqBo request = new LimitProductSkuReqBo();
		int uid = 0;
		for (LimitCodeProductBo limitCodeProductBo : limitCodeProducts){
			uid= limitCodeProductBo.getUid();
		}
		request.setUid(uid);
		//批量查询limit_product_sku记录
		LimitProductSkuRspBo[] productSkus = null;

		if(request!=null&&request.getUid()>0){
			LOGGER.info("getLimitProductSku.sknList size is {}",request.getUid());
			productSkus=serviceCaller.call(QUERY_SKU_LIMITCODE_SERVICE_NAME, request, LimitProductSkuRspBo[].class);
		}

		if (productSkus==null || productSkus.length==0){
			return;
		}
		for (LimitCodeProductBo limitCodeProductBo : limitCodeProducts) {
			LimitProductPromotionBo limitProductPromotionBo = limitCodeProductBo.getLimitProductBo();
			if(limitProductPromotionBo==null){
				continue;
			}
			for (LimitProductSkuRspBo productSkuBO : productSkus) {
				if(Integer.valueOf(productSkuBO.getProductSkn()).equals(limitProductPromotionBo.getProductSkn())){
					limitProductPromotionBo.setColor_name(productSkuBO.getColorName());
					limitProductPromotionBo.setSize_name(productSkuBO.getSizeName());
					limitProductPromotionBo.setProductSku(productSkuBO.getProductSku());
					break;
				}
			}
		}

	}
	private LimitCodeVo getLimitProductStatus(LimitCodeProductBo[] limitCodeProducts) {
		// 判断限购码列表的正常状态还是失效状态
		LimitCodeVo limitCodeVo = new LimitCodeVo();
		List<LimitCodeProductVo> validLimitCodeProducts = new ArrayList<LimitCodeProductVo>();
		List<LimitCodeProductVo> invalidLimitCodeProducts = new ArrayList<LimitCodeProductVo>();
		List<String> batchList = new ArrayList<String>();
		for (LimitCodeProductBo limitCodeProductBo : limitCodeProducts) {
			if(limitCodeProductBo.getStatus()!=4){//正常状态
				// Bo转化为Vo
				LimitCodeProductVo limitCodeProductVo = convertLimitCodeBoToVo(limitCodeProductBo);
				limitCodeProductVo.setStatus(1);
				validLimitCodeProducts.add(limitCodeProductVo);
			}else{//失效状态
				// Bo转化为Vo
				LimitCodeProductVo limitCodeProductVo = convertLimitCodeBoToVo(limitCodeProductBo);
				limitCodeProductVo.setStatus(0);
				invalidLimitCodeProducts.add(limitCodeProductVo);
			}
		}
		limitCodeVo.setLimitCodeProducts(validLimitCodeProducts);
		limitCodeVo.setInvalidLimitCodeProducts(invalidLimitCodeProducts);
		return limitCodeVo;
	}

	/**
	 * 批量查询skn商品价格
	 * @param limitCodeProducts
	 */
	private void getLimitProductSknPrice(LimitCodeProductBo[] limitCodeProducts) {
		BatchBaseRequest<Integer> request = new BatchBaseRequest<Integer>();
		List<Integer> sknList = new ArrayList<Integer>();
		for (LimitCodeProductBo limitCodeProductBo : limitCodeProducts) {
			if(limitCodeProductBo.getLimitProductBo()==null){
				continue;
			}
			Integer productSkn = limitCodeProductBo.getLimitProductBo().getProductSkn();
			if(productSkn!=null){
				sknList.add(productSkn);
			}
		}
		request.setParams(sknList);

		ProductPriceBo[] productPrices = null;
		if(sknList.size()>0){
			// 批量调价格接口，查询最新价格
			productPrices = serviceCaller.call(QUERY_PRICE_LIMITCODE_SERVICE_NAME, request, ProductPriceBo[].class);
			if (productPrices==null || productPrices.length==0){
				return;
			}
			for (LimitCodeProductBo limitCodeProductBo : limitCodeProducts) {
				LimitProductPromotionBo limitProductPromotionBo = limitCodeProductBo.getLimitProductBo();
				if(limitProductPromotionBo==null){
					continue;
				}
				for (ProductPriceBo productPriceBo : productPrices) {
					if(productPriceBo.getProductSkn().equals(limitProductPromotionBo.getProductSkn())){
						limitProductPromotionBo.setPrice(productPriceBo.getFormatSalesPrice());
						break;
					}else
					{
						limitProductPromotionBo.setPrice("¥售价待定");
					}
				}
			}
		}else{
			// 都没有关联skn
			for (LimitCodeProductBo limitCodeProductBo : limitCodeProducts) {
				LimitProductPromotionBo limitProductPromotionBo = limitCodeProductBo.getLimitProductBo();
				if(limitProductPromotionBo==null){
					continue;
				}
				limitProductPromotionBo.setPrice("¥售价待定");
			}
		}


	}

	private LimitCodeProductVo convertLimitCodeBoToVo(LimitCodeProductBo limitCodeProductBo) {
		LimitCodeProductVo limitCodeProductVo = new LimitCodeProductVo();
		LimitProductPromotionBo limitProductPromotionBo = limitCodeProductBo.getLimitProductBo();
		limitCodeProductVo.setLimitProductCode(limitCodeProductBo.getLimitProductCode());
		limitCodeProductVo.setLimitCode(limitCodeProductBo.getLimitCode());
		limitCodeProductVo.setActivityId(limitCodeProductBo.getActivityId());
		if(limitProductPromotionBo!=null){
			limitCodeProductVo.setProductName(limitProductPromotionBo.getProductName());
			limitCodeProductVo.setPrice(limitProductPromotionBo.getPrice());
			limitCodeProductVo.setDefaultUrl(limitProductPromotionBo.getAttachUrl());
			limitCodeProductVo.setProductSkn(limitProductPromotionBo.getProductSkn());
			limitCodeProductVo.setColor_name(limitProductPromotionBo.getColor_name());
			limitCodeProductVo.setSize_name(limitProductPromotionBo.getSize_name());
			limitCodeProductVo.setProductSku(limitProductPromotionBo.getProductSku());
		}else{
			LOGGER.warn(" warning limitCode has not productInfo,limitCodeProductBo is {}", limitCodeProductBo);
		}
		return limitCodeProductVo;
	}
}
