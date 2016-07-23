package com.yoho.gateway.controller.product.convert;

import org.springframework.stereotype.Service;

import com.yoho.gateway.controller.product.builder.DefaultProductBuilder;
import com.yoho.gateway.controller.product.builder.ProductBuilder;
import com.yoho.gateway.model.product.ProductVo;
import com.yoho.product.model.ProductBo;

/**
 * @author xieyong
 *
 */
@Service
public class DefaultProductConvert implements ProductConvert{
	
	/* (non-Javadoc)
	 * @see com.yoho.gateway.controller.product.convert.ProductConvert#convert(com.yoho.product.model.ProductBo)
	 */
	@Override
	public ProductVo convert(ProductBo productBo) {
		
		ProductVo productVo=new ProductVo();
		
		ProductBuilder builder=new DefaultProductBuilder();

		builder.buildBasicProductVo(productVo, productBo)
				.buildProductBrandVo(productVo, productBo)
				.buildProductCategoryVo(productVo, productBo)
				.buildProductGoodsVo(productVo, productBo)
				.buildProductPromotion(productVo, productBo)
				.buildProductTagVo(productVo, productBo)
				.buildProductVipInfo(productVo, productBo)
				.buildCommentAndConsultVo(productVo, productBo);
		
		return productVo;
	}
}
