package com.yoho.gateway.model.sns;

/**
 * ShareUserInfoVo
 * 晒单用户信息列表
 *
 * @author zhangyonghui
 * @date 2015/11/14
 */
public class ShareAuthorVo {

    private String nickName;
    private Integer uid;
    private String headIco;
    private Integer vipLevel;


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getHeadIco() {
        return headIco;
    }

    public void setHeadIco(String headIco) {
        this.headIco = headIco;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }
}
