package com.yoho.gateway.model.browse;

import com.yoho.service.model.BaseBO;

public class BrowseReqVO extends BaseBO {
    private static final long serialVersionUID = -2172811209215988075L;

    // uid
    private int uid;
    // udid
    private String udid;
    // 产品skn
    private String skn;
    // 产品二级分类id
    private String category_id;

    // 客户端版本号
    private String app_version;

    // 浏览记录数据
    private String browseList;

    // 分页
    private int page = 1;
    private int limit = 10;

    // 是否记录浏览记录标志
    private int flag = 1;

    public BrowseReqVO() {
    }

    public BrowseReqVO(int uid, String udid) {
        this.uid = uid;
        this.udid = udid;
    }

    public BrowseReqVO(int uid, int page, int limit) {
        this.uid = uid;
        this.page = page;
        this.limit = limit;
    }

    public BrowseReqVO(int uid, String skn, String category_id) {
        this.uid = uid;
        this.skn = skn;
        this.category_id = category_id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getSkn() {
        return skn;
    }

    public void setSkn(String skn) {
        this.skn = skn;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getBrowseList() {
        return browseList;
    }

    public void setBrowseList(String browseList) {
        this.browseList = browseList;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
