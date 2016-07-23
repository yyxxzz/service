package com.yoho.gateway.model.sns;

/**
 * 待晒单商品数据
 */
public class ShareOrderGoodsVo {

    private String orderId;
    private String productId;
    private String productSkn;
    private String goodsId;
    private String erpSkuId;
    private String imageUrl;
    private String subImageUrl;
    private int rewardStatus;
    private String goodsName;
    private String productName;
    private String cnAlphabet;
    private String comment;
    private BrandVo brand;
    //判断商品品类是否显示身高体重
    private boolean shouldShowWeighInfo;


    public boolean isShouldShowWeighInfo() {
        return shouldShowWeighInfo;
    }

    public void setShouldShowWeighInfo(boolean shouldShowWeighInfo) {
        this.shouldShowWeighInfo = shouldShowWeighInfo;
    }

    public String getProductSkn() {
        return productSkn;
    }

    public void setProductSkn(String productSkn) {
        this.productSkn = productSkn;
    }

    public BrandVo getBrand() {
        return brand;
    }

    public void setBrand(BrandVo brand) {
        this.brand = brand;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getErpSkuId() {
        return erpSkuId;
    }

    public void setErpSkuId(String erpSkuId) {
        this.erpSkuId = erpSkuId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getRewardStatus() {
        return rewardStatus;
    }

    public void setRewardStatus(int rewardStatus) {
        this.rewardStatus = rewardStatus;
    }

	public String getSubImageUrl() {
		return subImageUrl;
	}

	public void setSubImageUrl(String subImageUrl) {
		this.subImageUrl = subImageUrl;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCnAlphabet() {
		return cnAlphabet;
	}

	public void setCnAlphabet(String cnAlphabet) {
		this.cnAlphabet = cnAlphabet;
	}
	
}
