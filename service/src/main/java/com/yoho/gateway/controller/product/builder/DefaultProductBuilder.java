package com.yoho.gateway.controller.product.builder;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.yoho.gateway.model.product.BrandVo;
import com.yoho.gateway.model.product.CategoryVo;
import com.yoho.gateway.model.product.CommentAndConsultVo;
import com.yoho.gateway.model.product.CommentVo;
import com.yoho.gateway.model.product.ConsultVo;
import com.yoho.gateway.model.product.GoodsImagesVo;
import com.yoho.gateway.model.product.GoodsSizeVo;
import com.yoho.gateway.model.product.GoodsVo;
import com.yoho.gateway.model.product.ProductVo;
import com.yoho.gateway.model.product.PromotionVo;
import com.yoho.gateway.model.product.VipPriceVo;
import com.yoho.product.model.CategoryBo;
import com.yoho.product.model.CommentBo;
import com.yoho.product.model.ConsultBo;
import com.yoho.product.model.GoodsBo;
import com.yoho.product.model.GoodsImagesBo;
import com.yoho.product.model.GoodsSizeBo;
import com.yoho.product.model.ProductBo;
import com.yoho.product.model.ProductTagBo;
import com.yoho.product.model.PromotionBo;
import com.yoho.product.model.VipPriceBo;
import com.yoho.product.model.wrapper.CommentBoWrapper;
import com.yoho.product.model.wrapper.ConsultBoWrapper;

/**
 * @author xieyong
 *
 */
public class DefaultProductBuilder implements ProductBuilder{
	
	
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 预计上市时间，需要转换成具体月
	 */
	private final static String EXPECTARRIVAL_MONTH="月";
	
	/* (non-Javadoc)
	 * @see com.yoho.gateway.controller.product.builder.ProductBuilder#buildBasicProductVo(com.yoho.gateway.model.product.ProductVo, com.yoho.product.model.ProductBo)
	 */
	@Override
	public ProductBuilder buildBasicProductVo(ProductVo productVo,
			ProductBo productBo) {
		productVo.setAttribute(productBo.getAttribute());
		productVo.setCnAlphabet(productBo.getCnAlphabet());
		//只有大于0才需要展示
		if ("Y".equals(productBo.getIsAdvance()) && null!=productBo.getExpectArrivalTime()&&productBo.getExpectArrivalTime()>0) 
		{
			productVo.setExpectArrivalTime(productBo.getExpectArrivalTime()+EXPECTARRIVAL_MONTH);
		}
		//注意空指针问题
		if(null!=productBo.getProductPriceBo())
		{
			productVo.setFormatMarketPrice(productBo.getProductPriceBo().getFormatMarketPrice());
			//这里有逻辑，如果销售价和市场价相等，这个FormatSalesPrice要设置为0,其实我想说真是奇葩!!!!!!
			if(productBo.getProductPriceBo().getSalesPrice().equals(productBo.getProductPriceBo().getMarketPrice()))
			{
				productVo.setFormatSalesPrice("0");
			}
			else
			{
				productVo.setFormatSalesPrice(productBo.getProductPriceBo().getFormatSalesPrice());
			}
			productVo.setMarketPrice(productBo.getProductPriceBo().getMarketPrice());
			productVo.setSalesPrice(productBo.getProductPriceBo().getSalesPrice());
			productVo.setVipPrice(productBo.getProductPriceBo().getVipPrice());
		}
		productVo.setIsCollect(productBo.getIsCollect());
		productVo.setIsAdvance(productBo.getIsAdvance());
		productVo.setIsOutlets(productBo.getIsOutlets());
		productVo.setPhrase(productBo.getPhrase());
		productVo.setProductId(productBo.getId());
		productVo.setProductName(productBo.getProductName());
		productVo.setProductSkn(productBo.getErpProductId());
		productVo.setProductUrl(productBo.getProductUrl());
		productVo.setSalesPhrase(productBo.getSalesPhrase());
		//这里是算出来的，根据sku
		productVo.setStorageSum(caculteStorageNum(productBo));
		//限量商品相关,是否限购,限购商品code
		productVo.setLimitBuy("Y".equals(productBo.getIsLimitBuy()));
		productVo.setLimitProductCode(StringUtils.isEmpty(productBo.getLimitProductCode())?"":productBo.getLimitProductCode());
		return this;
	}
	
	/**
	 * 计算库存总数
	 * @param productBo
	 * @return
	 */
	private int caculteStorageNum(ProductBo productBo)
	{
		if(0==productBo.getStatus()){
			logger.info("caculteStorageNum, status=0, productSkn:{}",productBo.getErpProductId());
			return 0;
		}
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

	/* (non-Javadoc)
	 * @see com.yoho.gateway.controller.product.builder.ProductBuilder#buildProductBrandVo(com.yoho.gateway.model.product.ProductVo, com.yoho.product.model.ProductBo)
	 */
	@Override
	public ProductBuilder buildProductBrandVo(ProductVo productVo,
			ProductBo productBo) {
		if(null==productBo.getBrand())
		{
			return this;
		}
		BrandVo brandVo=new BrandVo();
		brandVo.setBrandIco(productBo.getBrand().getBrandIco());
		brandVo.setBrandId(productBo.getBrand().getId());
		brandVo.setBrandName(productBo.getBrand().getBrandName());
		productVo.setBrand(brandVo);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.yoho.gateway.controller.product.builder.ProductBuilder#buildProductVipInfo(com.yoho.gateway.model.product.ProductVo, com.yoho.product.model.ProductBo)
	 */
	@Override
	public ProductBuilder buildProductVipInfo(ProductVo productVo,
			ProductBo productBo) {
		List<VipPriceBo> vipPriceBoList=productBo.getProductPriceBo().getVipPrices();
		
		// 详情页显示有货币
		Integer yohoCoinNum = productBo.getProductPriceBo().getYohoCoinNum();
		productVo.setYohoCoinNum(null == yohoCoinNum ? "" : String.valueOf(yohoCoinNum));
		
		if(CollectionUtils.isEmpty(vipPriceBoList))
		{
			return this;
		}
		
		List<VipPriceVo> vipPriceVoList=Lists.newArrayList();
		
		VipPriceVo vipPriceVo=null;
		for (VipPriceBo vipPriceBo : vipPriceBoList) {
			vipPriceVo=new VipPriceVo();
			vipPriceVo.setVipTitle(vipPriceBo.getVipTitle());
			vipPriceVo.setVipPrice(vipPriceBo.getVipPrice());
			vipPriceVoList.add(vipPriceVo);
		}
		productVo.setVip(vipPriceVoList);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.yoho.gateway.controller.product.builder.ProductBuilder#buildProductPromotion(com.yoho.gateway.model.product.ProductVo, com.yoho.product.model.ProductBo)
	 */
	@Override
	public ProductBuilder buildProductPromotion(ProductVo productVo,
			ProductBo productBo) {
		List<PromotionBo> promotionBoList=productBo.getPromotionBoList();
		if(CollectionUtils.isEmpty(promotionBoList))
		{
			return this;
		}
		List<PromotionVo> promotionVoList=Lists.newArrayList();
		PromotionVo promotionVo=null;
		for (PromotionBo promotionBo : promotionBoList) {
			promotionVo=new PromotionVo();
			promotionVo.setPromotionTitle(promotionBo.getPromotionTitle());
			promotionVo.setPromotionType(promotionBo.getPromotionType());
			promotionVoList.add(promotionVo);
		}
		productVo.setPromotionVoList(promotionVoList);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.yoho.gateway.controller.product.builder.ProductBuilder#buildProductGoodsVo(com.yoho.gateway.model.product.ProductVo, com.yoho.product.model.ProductBo)
	 */
	@Override
	public ProductBuilder buildProductGoodsVo(ProductVo productVo,
			ProductBo productBo) {
		List<GoodsBo> goodsBoList=productBo.getGoodsList();
		
		if(CollectionUtils.isEmpty(goodsBoList))
		{
			return this;
		}
		List<GoodsVo> goodsVoList=Lists.newArrayList();
		
		GoodsVo goodsVo=null;
		for (GoodsBo goodsBo : goodsBoList) {
			goodsVo=new GoodsVo();
			goodsVo.setColorId(goodsBo.getColorId());
			goodsVo.setColorImage(goodsBo.getColorImage());
			goodsVo.setColorName(goodsBo.getColorName());
			goodsVo.setId(goodsBo.getId());
			goodsVo.setIsDefault(goodsBo.getIsDefault());
			goodsVo.setProductSkc(goodsBo.getProductSkc());
			goodsVo.setStatus(goodsBo.getStatus());
			goodsVo.setGoodsSizeBoList(buildGoodsSizeList(goodsBo));
			goodsVo.setGoodsImagesList(buildGoodsImageList(goodsBo));
			goodsVoList.add(goodsVo);
		}
		productVo.setGoodsList(goodsVoList);
		return this;
	}

	/**
	 * 构造skc下sku尺码的信息
	 * @param goodsBo
	 */
	private List<GoodsSizeVo> buildGoodsSizeList(GoodsBo goodsBo) {
		List<GoodsSizeBo> goodsSizeBoList=goodsBo.getGoodsSizeBoList();
		if(CollectionUtils.isEmpty(goodsSizeBoList))
		{
			return Lists.newArrayList();
		}
		List<GoodsSizeVo> goodsSizeVoList=Lists.newArrayList();
		GoodsSizeVo goodsSizeVo=null;
		for (GoodsSizeBo goodsSizeBo : goodsSizeBoList) {
			goodsSizeVo=new GoodsSizeVo();
			goodsSizeVo.setGoodsSizeSkuId(goodsSizeBo.getGoodsSizeSkuId());
			goodsSizeVo.setGoodsSizeStorageNum(goodsSizeBo.getGoodsSizeStorageNum());
			goodsSizeVo.setId(goodsSizeBo.getId());
			goodsSizeVo.setOrderBy(goodsSizeBo.getOrderBy());
			goodsSizeVo.setSizeName(goodsSizeBo.getSizeName());
			goodsSizeVoList.add(goodsSizeVo);
		}
		return goodsSizeVoList;
		
	}

	/**
	 * 构造goods图片信息
	 * @param goodsBo
	 * @return
	 */
	private List<GoodsImagesVo> buildGoodsImageList(GoodsBo goodsBo) {
		List<GoodsImagesBo> goodsImagesBoList=goodsBo.getGoodsImagesList();
		if(CollectionUtils.isEmpty(goodsImagesBoList))
		{
			return Lists.newArrayList();
		}
		List<GoodsImagesVo> goodsImagesVoList=Lists.newArrayList();
		GoodsImagesVo goodsImagesVo=null;
		for (GoodsImagesBo goodsImagesBo : goodsImagesBoList) {
			goodsImagesVo=new GoodsImagesVo();
			//全路径的url
			goodsImagesVo.setImageUrl(goodsImagesBo.getImageUrl());
			goodsImagesVoList.add(goodsImagesVo);
		}
		return goodsImagesVoList;
	}
	/* (non-Javadoc)
	 * @see com.yoho.gateway.controller.product.builder.ProductBuilder#buildProductCategoryVo(com.yoho.gateway.model.product.ProductVo, com.yoho.product.model.ProductBo)
	 */
	@Override
	public ProductBuilder buildProductCategoryVo(ProductVo productVo,
			ProductBo productBo) {
		List<CategoryBo> categoryBoList=productBo.getCategoryBoList();
		if(CollectionUtils.isEmpty(categoryBoList))
		{
			return this;
		}
		List<CategoryVo> categoryVoList=Lists.newArrayList();
		CategoryVo categoryVo=null;
		for (CategoryBo categoryBo : categoryBoList) {
			categoryVo=new CategoryVo();
			categoryVo.setCategoryId(categoryBo.getCategoryId());
			categoryVo.setCategoryName(categoryBo.getCategoryName());
			categoryVoList.add(categoryVo);
		}
		
		productVo.setCategoryVoList(categoryVoList);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.yoho.gateway.controller.product.builder.ProductBuilder#buildProductTagVo(com.yoho.gateway.model.product.ProductVo, com.yoho.product.model.ProductBo)
	 */
	@Override
	public ProductBuilder buildProductTagVo(ProductVo productVo,
			ProductBo productBo) {
		List<ProductTagBo> productTagBoList=productBo.getProductTagBoList();
		if(CollectionUtils.isEmpty(productTagBoList))
		{
			return this;
		}
		List<String> tags=Lists.newArrayList();
		for (ProductTagBo productTagBo : productTagBoList) {
			tags.add(productTagBo.getTagLabel());
		}
		productVo.setTags(tags);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.yoho.gateway.controller.product.builder.ProductBuilder#buildCommentAndConsultVo(com.yoho.gateway.model.product.ProductVo, com.yoho.product.model.ProductBo)
	 */
	@Override
	public ProductBuilder buildCommentAndConsultVo(ProductVo productVo,
			ProductBo productBo) {
		
		CommentAndConsultVo commentAndConsultVo=new CommentAndConsultVo();
		CommentBoWrapper commentBoWrapper=productBo.getCommentBoWrapper();
		if(null!=commentBoWrapper)
		{
			commentAndConsultVo.setCommentTotal(null==commentBoWrapper.getCommentTotal()?0:commentBoWrapper.getCommentTotal());
			if(CollectionUtils.isNotEmpty(commentBoWrapper.getCommentBoList()))
			{	
				CommentBo commentBo=commentBoWrapper.getCommentBoList().get(0);
				commentAndConsultVo.setCommentVo(buildCommentVo(commentBo));
			}
		}
		ConsultBoWrapper consultBoWrapper=productBo.getConsultBoWrapper();
		if(null!=consultBoWrapper)
		{
			commentAndConsultVo.setConsultTotal(null==consultBoWrapper.getConsultTotal()?0:consultBoWrapper.getConsultTotal());
			if(CollectionUtils.isNotEmpty(consultBoWrapper.getConsultBoList()))
			{	
				ConsultBo consultBo=consultBoWrapper.getConsultBoList().get(0);
				commentAndConsultVo.setConsultVo(buildConsultVo(consultBo));
			}
		}
		productVo.setCommentAndConsultVo(commentAndConsultVo);
		return this;
	}
	
	/**
	 * 构造咨询vo
	 * @param consultBo
	 * @return
	 */
	private ConsultVo buildConsultVo(ConsultBo consultBo) {
		ConsultVo consultVo=new ConsultVo();
		consultVo.setAnswer(consultBo.getAnswer());
		consultVo.setAnswerTime(consultBo.getAnswerTime());
		consultVo.setAsk(consultBo.getAsk());
		consultVo.setAskTime(consultBo.getAskTime());
		consultVo.setId(consultBo.getId());
		consultVo.setProductId(consultBo.getProductId());
		return consultVo;
	}
	
	/**
	 * 构造评论vo
	 * @param commentBo
	 * @return
	 */
	private CommentVo buildCommentVo(CommentBo commentBo) {
		CommentVo commentVo=new CommentVo();
		commentVo.setColorName(commentBo.getColorName());
		commentVo.setContent(commentBo.getContent());
		commentVo.setCreateTime(commentBo.getCreateTime());
		commentVo.setHeadIcon(commentBo.getHeadIcon());
		commentVo.setId(commentBo.getId());
		commentVo.setNickName(commentBo.getNickName());
		commentVo.setProductId(commentBo.getProductId());
		commentVo.setSizeName(commentBo.getSizeName());
		commentVo.setUid(commentBo.getUid());
		return commentVo;
	}

	/**
	 * 过滤掉库存状态为0的
	 * @param goodsList
	 */
	@Override
	public void filterGoodSizeBo(List<GoodsBo> goodsList) {
		if (CollectionUtils.isEmpty(goodsList)) {
			return;
		}
		
		for (GoodsBo goodsBo : goodsList) {
			filterGoodSizeBoEx(goodsBo.getGoodsSizeBoList());
		}
	}

	private void filterGoodSizeBoEx(List<GoodsSizeBo> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		
		Iterator<GoodsSizeBo> iterator = list.iterator();
		while(iterator.hasNext()) {
			GoodsSizeBo next = iterator.next();
			if (null == next || Integer.valueOf(0).equals(next.getSkuStatus())) {
				iterator.remove();
			}
		}
	}
}
