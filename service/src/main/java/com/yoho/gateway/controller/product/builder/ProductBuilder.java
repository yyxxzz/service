package com.yoho.gateway.controller.product.builder;

import java.util.List;

import com.yoho.gateway.model.product.ProductVo;
import com.yoho.product.model.GoodsBo;
import com.yoho.product.model.ProductBo;

/**
 * @author xieyong
 *
 */
public interface ProductBuilder {
	
	/**
	 * 构造商品基本信息
	 * @param productVo
	 * @param productBo
	 * @return
	 */
	ProductBuilder buildBasicProductVo(ProductVo productVo, ProductBo productBo);
	
	/**
	 * 构造商品评论和咨询
	 * @param productVo
	 * @param productBo
	 * @return
	 */
	ProductBuilder buildCommentAndConsultVo(ProductVo productVo, ProductBo productBo);
	
	/**
	 * 构造商品品牌信息
	 * @param productVo
	 * @param productBo
	 * @return
	 */
	ProductBuilder buildProductBrandVo(ProductVo productVo, ProductBo productBo);
	/**
	 * 构造商品vip价格信息
	 * @param productVo
	 * @param productBo
	 * @return
	 */
	ProductBuilder buildProductVipInfo(ProductVo productVo, ProductBo productBo);
	/**
	 * 构造商品促销信息
	 * @param productVo
	 * @param productBo
	 * @return
	 */
	ProductBuilder buildProductPromotion(ProductVo productVo, ProductBo productBo);
	/**
	 * 构造商品skc的信息
	 * @param productVo
	 * @param productBo
	 * @return
	 */
	ProductBuilder buildProductGoodsVo(ProductVo productVo, ProductBo productBo);
	/**
	 * 构造商品分类信息
	 * @param productVo
	 * @param productBo
	 * @return
	 */
	ProductBuilder buildProductCategoryVo(ProductVo productVo, ProductBo productBo);
	
	/**
	 * 构造商品标签信息
	 * @param productVo
	 * @param productBo
	 * @return
	 */
	ProductBuilder buildProductTagVo(ProductVo productVo, ProductBo productBo);

	/**
	 * 过滤掉库存状态为0的
	 * @param goodsList
	 */
	void filterGoodSizeBo(List<GoodsBo> goodsList);
}
