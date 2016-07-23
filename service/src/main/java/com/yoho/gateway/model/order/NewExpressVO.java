package com.yoho.gateway.model.order;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * qianjun
 * 2015/11/27
 */
public class NewExpressVO {


    private String url;
    private String logo;
    private String caption;

    @JSONField(name="is_support")
    private String isSupport;

    @JSONField(name="express_number")
    private String expressNumber;

    @JSONField(name="express_detail")
    private List<WaybillInfoVO> expressDetail;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getIsSupport() {
        return isSupport;
    }

    public void setIsSupport(String isSupport) {
        this.isSupport = isSupport;
    }

    public String getExpressNumber() {
        return expressNumber;
    }

    public void setExpressNumber(String expressNumber) {
        this.expressNumber = expressNumber;
    }

    public List<WaybillInfoVO> getExpressDetail() {
        return expressDetail;
    }

    public void setExpressDetail(List<WaybillInfoVO> expressDetail) {
        this.expressDetail = expressDetail;
    }
}

















