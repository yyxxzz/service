package com.yoho.gateway.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * 登陆的响应
 * <pre>
 * uid": "10216497",
 * "profile": "18751986615",
 * "session_key": "fa31d3a5d069c6c98cd8c38c3a5f89e6",
 * "vip": 0
 * </pre>
 *
 * Created by chang@yoho.cn on 2015/11/3.
 */
public class SigninResponse {
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

    private String uid = "00111";
    private String profile;
    private String session_key;
    private int vip;




}
