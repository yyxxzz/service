package com.yoho.gateway.model.order;

import com.alibaba.fastjson.annotation.JSONField;
import com.yoho.product.model.BrandBo;
import com.yoho.product.model.GoodsImagesBo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * OrdersGoods
 * 订单商品类
 *
 * @author zhangyonghui
 * @date 2015/11/3
 */
public class OrdersGoodsVO implements Serializable {

    @JSONField(name = "product_skn")
    private String productSkn;

    @JSONField(name = "product_sku")
    private String productSku;

    @JSONField(name = "product_name")
    private String productName;

    /**
     * '1为正常商品，2为商品的赠品，3为订单赠品，4为商品满足条件加钱送，5为订单满足条件价钱送。
     */
    @JSONField(name = "goods_type")
    private String goodsType;

    /**
     *尺码
     */
    @JSONField(name = "size_name")
    private String sizeName;

    /**
     * 颜色名称
     */
    @JSONField(name = "color_name")
    private String colorName;

    @JSONField(name = "sales_price")
    private String salesPrice;

    @JSONField(name = "goods_image")
    private String goodsImage;

    @JSONField(name = "product_id")
    private String productId;

    @JSONField(name = "goods_id")
    private String goodsId;

    @JSONField(name = "cn_alphabet")
    private String cnAlphabet;

    public String getProductSkn() {
        return productSkn;
    }

    public void setProductSkn(String productSkn) {
        this.productSkn = productSkn;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(String salesPrice) {
        this.salesPrice = salesPrice;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCnAlphabet() {
        return cnAlphabet;
    }

    public void setCnAlphabet(String cnAlphabet) {
        this.cnAlphabet = cnAlphabet;
    }
}