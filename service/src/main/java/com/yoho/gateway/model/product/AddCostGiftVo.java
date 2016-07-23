package com.yoho.gateway.model.product;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class AddCostGiftVo  implements Serializable{
	private static final long serialVersionUID = 8467587291317523112L;
	
	@JSONField(name="color_name")
	private String colorName;

	@JSONField(name="color_image")
	private String colorImage;

	@JSONField(name="is_default")
	private String isDefault;

	@JSONField(name="product_skc")
	private Integer productSkc;

	@JSONField(name="status")
	private Integer status;

	@JSONField(name="color_id")
	private Integer colorId;

	@JSONField(name="images_list")
	private List<AddCostGiftImageVo> imagesList;

	@JSONField(name="size_list")
	private List<AddCostGiftSizeVo> sizeList;

	@JSONField(name="goods_id")
	private Integer goodsId;

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public String getColorImage() {
		return colorImage;
	}

	public void setColorImage(String colorImage) {
		this.colorImage = colorImage;
	}

	public String getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	public Integer getProductSkc() {
		return productSkc;
	}

	public void setProductSkc(Integer productSkc) {
		this.productSkc = productSkc;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getColorId() {
		return colorId;
	}

	public void setColorId(Integer colorId) {
		this.colorId = colorId;
	}

	public List<AddCostGiftImageVo> getImagesList() {
		return imagesList;
	}

	public void setImagesList(List<AddCostGiftImageVo> imagesList) {
		this.imagesList = imagesList;
	}

	public List<AddCostGiftSizeVo> getSizeList() {
		return sizeList;
	}

	public void setSizeList(List<AddCostGiftSizeVo> sizeList) {
		this.sizeList = sizeList;
	}

	public Integer getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}
}
