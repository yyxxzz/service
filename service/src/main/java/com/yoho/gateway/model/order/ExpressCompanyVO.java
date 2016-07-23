package com.yoho.gateway.model.order;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * qianjun
 * 2015/11/27
 */
public class ExpressCompanyVO {
    /**
     * 物流公司ID
     */
    private Integer id;

    /**
     * 物流公司名称
     */
    @JSONField(name="company_name")
    private String companyName;

    /**
     * 物流公司子字符串
     */
    @JSONField(name="company_name_substr")
    private String companyNameSubstr;

    /**
     * 物流公司首字母
     */
    @JSONField(name="company_alif")
    private String companyAlif;

    /**
     * 物流公司编码
     */
    @JSONField(name="company_code")
    private String companyCode;

    /**
     * 是否首次
     */
    @JSONField(name="is_first")
    private String  isFirst;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNameSubstr() {
        return companyNameSubstr;
    }

    public void setCompanyNameSubstr(String companyNameSubstr) {
        this.companyNameSubstr = companyNameSubstr;
    }

    public String getCompanyAlif() {
        return companyAlif;
    }

    public void setCompanyAlif(String companyAlif) {
        this.companyAlif = companyAlif;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(String isFirst) {
        this.isFirst = isFirst;
    }
}







































