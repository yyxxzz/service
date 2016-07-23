package com.yoho.gateway.model.resources;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author LiQZ on 2016/3/15.
 */
public class HotRankTagVO {

    private String id;

    @JSONField(name = "tag_name")
    private String tagName;

    @JSONField(name = "category_id")
    private String categoryId;

    private String platform;

    private String channel;

    @JSONField(name = "order_by")
    private String orderBy;

    private String status;

    @JSONField(name = "create_time")
    private String createTime;

    @JSONField(name = "update_time")
    private String updateTime;


    public String getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = toStr(id);
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = toStr(orderBy);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = toStr(status);
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = toStr(createTime);
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Integer updateTime) {
        this.updateTime = toStr(updateTime);
    }

    private String toStr(Object o) {
       return  o == null ? "": o.toString();
    }
}
