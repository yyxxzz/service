package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 凑单商品VO
 *
 * @author caoyan
 */
public class TogetherProductVo implements Serializable {

    private static final long serialVersionUID = 8467587291317523119L;

    @JSONField(name = "id")
    private Integer id;

    @JSONField(name = "product_name")
    private String productName;

    @JSONField(name = "product_skn")
    private Integer productSkn;

    @JSONField(name = "default_pic")
    private String defaultPic;

    @JSONField(name = "url")
    private String url;

    @JSONField(name = "price")
    private TogetherProductPriceVo price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDefaultPic() {
        return defaultPic;
    }

    public void setDefaultPic(String defaultPic) {
        this.defaultPic = defaultPic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TogetherProductPriceVo getPrice() {
        return price;
    }

    public void setPrice(TogetherProductPriceVo price) {
        this.price = price;
    }

    public Integer getProductSkn() {
        return productSkn;
    }

    public void setProductSkn(Integer productSkn) {
        this.productSkn = productSkn;
    }
}
