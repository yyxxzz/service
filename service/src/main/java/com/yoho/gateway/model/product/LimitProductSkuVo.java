package com.yoho.gateway.model.product;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhouxiang on 2016/4/21.
 */
public class LimitProductSkuVo implements Serializable {

    private static final long serialVersionUID = -6912142540604191858L;

    private int id;

    private String uid;

    private String product_skn;

    private String product_sku;

    private String product_skc;

    private String color_name;

    private String size_name;

    private String activity_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProduct_skn() {
        return product_skn;
    }

    public void setProduct_skn(String product_skn) {
        this.product_skn = product_skn;
    }

    public String getProduct_sku() {
        return product_sku;
    }

    public void setProduct_sku(String product_sku) {
        this.product_sku = product_sku;
    }

    public String getProduct_skc() {
        return product_skc;
    }

    public void setProduct_skc(String product_skc) {
        this.product_skc = product_skc;
    }

    public String getColor_name() {
        return color_name;
    }

    public void setColor_name(String color_name) {
        this.color_name = color_name;
    }

    public String getSize_name() {
        return size_name;
    }

    public void setSize_name(String size_name) {
        this.size_name = size_name;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }
}
