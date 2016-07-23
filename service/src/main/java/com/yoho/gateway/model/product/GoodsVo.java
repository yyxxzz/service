package com.yoho.gateway.model.product;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * @author xieyong
 *
 */
public class GoodsVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6149254107101362464L;
	
	@JSONField(name="goods_id")
	private Integer id;

    @JSONField(name="color_id")
    private Integer colorId;
    
    @JSONField(name="color_name")
    private String colorName;
    
    /**
     * 对应颜色的图片,全路径
     */
    @JSONField(name="color_image")
    private String colorImage;

    @JSONField(name="status")
    private Integer status;
    
    @JSONField(name="is_default")
    private String isDefault;
    
    @JSONField(name="product_skc")
    private Integer productSkc;

    @JSONField(name="images_list")
    private List<GoodsImagesVo> goodsImagesList;
    
    @JSONField(name="size_list")
    private List<GoodsSizeVo> goodsSizeBoList;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getColorId() {
		return colorId;
	}

	public void setColorId(Integer colorId) {
		this.colorId = colorId;
	}

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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public List<GoodsImagesVo> getGoodsImagesList() {
		return goodsImagesList;
	}

	public void setGoodsImagesList(List<GoodsImagesVo> goodsImagesList) {
		this.goodsImagesList = goodsImagesList;
	}

	public List<GoodsSizeVo> getGoodsSizeBoList() {
		return goodsSizeBoList;
	}

	public void setGoodsSizeBoList(List<GoodsSizeVo> goodsSizeBoList) {
		this.goodsSizeBoList = goodsSizeBoList;
	}
}
