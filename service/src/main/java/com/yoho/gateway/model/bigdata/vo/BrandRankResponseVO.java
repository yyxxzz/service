package com.yoho.gateway.model.bigdata.vo;

/**
 * Created by yoho on 2016/5/27.
 */
public class BrandRankResponseVO {

    private String dateId;

    private String brandId;

    private String brandMainCategory;

    private String rankNow;

    private String rankChange;

    private String rankLevel;

    private boolean rankLevelFlag=false ;

    public String getDateId() {
        return dateId;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandMainCategory() {
        return brandMainCategory;
    }

    public void setBrandMainCategory(String brandMainCategory) {
        this.brandMainCategory = brandMainCategory;
    }

    public String getRankNow() {
        return rankNow;
    }

    public void setRankNow(String rankNow) {
        this.rankNow = rankNow;
    }

    public String getRankChange() {
        return rankChange;
    }

    public void setRankChange(String rankChange) {
        this.rankChange = rankChange;
    }

    public String getRankLevel() {
        return rankLevel;
    }

    public void setRankLevel(String rankLevel) {
        this.rankLevel = rankLevel;
    }

    public boolean isRankLevelFlag() {
        return rankLevelFlag;
    }

    public void setRankLevelFlag(boolean rankLevelFlag) {
        this.rankLevelFlag = rankLevelFlag;
    }

    @Override
    public String toString() {
        return "BrandRankResponseVO{" +
                "dateId='" + dateId + '\'' +
                ", brandId='" + brandId + '\'' +
                ", brandMainCategory='" + brandMainCategory + '\'' +
                ", rankNow='" + rankNow + '\'' +
                ", rankChange='" + rankChange + '\'' +
                ", rankLevel='" + rankLevel + '\'' +
                ", rankLevelFlag=" + rankLevelFlag +
                '}';
    }
}
