package com.yoho.gateway.model.resources;

import com.alibaba.fastjson.annotation.JSONField;
import com.yoho.service.model.resource.BaseBO;

/**
 * qianjun 2016/2/18
 */
public class CoverStartVO {

    private String img;
    private String delay;
    @JSONField(name="start_time")
    private int startTime;
    @JSONField(name="end_time")
    private int endTime;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
