package com.yoho.gateway.model.sns;

import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.service.model.sns.model.ShareOrderTagBo;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by zhangyonghui on 2015/11/11
 * 晒单VO对象
 */
public class ShareOrderVo {


    /**
     * 晒单ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private String uid;

    /**
     * 滤镜ID
     */
    private String filterId;

    /**
     * 相框ID
     */
    private String frameId;

    /**
     * 是否是达人
     */
    private String star;

    /**
     * 客户类型
     */
    private String source;

    /**
     * 封面图片
     */
    private String cover;

    /**
     * 带标签的图片
     */
    private String taggedPic;

    /**
     * 图片URL
     */
    private Integer picNum;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 产品id
     */
    private String productId;

    /**
     * 晒单商品ID
     */
    private String goodsId;


    private String erpSkuId;

    /**
     * '0:图片,1:视频,2:音频,3:图片列表,4:城市地理',
     */
    private Integer type;

    /**
     * 内容链接（如图片地址
     */
    private String url;

    /**
     * 晒单文字
     */
    private String content;

    /**
     * 商品满意 1-5星级
     */
    private Integer satisfied;

    /**
     * 尺寸 small:偏小，middle：适中，big：偏大
     */
    private String size;

    /**
     * 身高
     */
    private Integer height;

    /**
     * 体重
     */
    private Integer weight;

    /**
     * 是否匿名
     */
    private Boolean anonymous;

    /**
     * 是否同步第三方系
     */
    private Integer sysPartnerStatus;

    /**
     * 创建时间
     */
    private Integer createTime;

    /**
     * 更新时间
     */
    private Integer updateTime;

    /**
     * 审核状
     */
    private String audStatus;

    /**
     * 晒单通过奖励 0:未发送 1:奖励已发
     */
    private Boolean shareReward;

    /**
     * 推荐奖励 0:未发送 1:已发放
     */
    private Boolean recomReward;

    /**
     * 审核
     */
    private String audPeople;

    /**
     * 审核时间
     */
    private Integer audTime;

    /**
     * 是否隐藏身高 体重
     * 0：不隐藏  1：隐藏
     */
    private Integer isHideHeight;

    private OrderGoodsVo goods;

    private ShareAuthorVo userInfo;

    /**
     * 根据品类是否展示升高体重
     */
    private Boolean shouldShowWeighInfo;

    /**
     * 绑定的标签集
     * */
    private List<ShareOrderTagBo> shareOrderTagList;

    // 是否点赞，N：未点赞 Y：已点赞
    private String isPraise;
    
    // 点赞数目
    private Integer praise_num;

    public Boolean getShareReward() {
        return shareReward;
    }

    public void setShareReward(Boolean shareReward) {
        this.shareReward = shareReward;
    }

    public Boolean getRecomReward() {
        return recomReward;
    }

    public void setRecomReward(Boolean recomReward) {
        this.recomReward = recomReward;
    }

    public Boolean getShouldShowWeighInfo() {
        return shouldShowWeighInfo;
    }

    public void setShouldShowWeighInfo(Boolean shouldShowWeighInfo) {
        this.shouldShowWeighInfo = shouldShowWeighInfo;
    }

    public Integer getIsHideHeight() {
        return isHideHeight;
    }

    public void setIsHideHeight(Integer isHideHeight) {
        this.isHideHeight = isHideHeight;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public ShareAuthorVo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(ShareAuthorVo userInfo) {
        this.userInfo = userInfo;
    }

    public String getErpSkuId() {
        return erpSkuId;
    }

    public void setErpSkuId(String erpSkuId) {
        this.erpSkuId = erpSkuId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }

    public String getFrameId() {
        return frameId;
    }

    public void setFrameId(String frameId) {
        this.frameId = frameId;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTaggedPic() {
        return taggedPic;
    }

    public void setTaggedPic(String taggedPic) {
        this.taggedPic = taggedPic;
    }

    public Integer getPicNum() {
        return picNum;
    }

    public void setPicNum(Integer picNum) {
        this.picNum = picNum;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUrl() {
        if(StringUtils.isBlank(url) || url.startsWith("http:")){
            return url;
        }
        return ImagesHelper.template2(url, ImagesHelper.SYS_BUCKET.get(ImagesHelper.SYS_SNS_NAME));
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getSatisfied() {
        return satisfied;
    }

    public void setSatisfied(Integer satisfied) {
        this.satisfied = satisfied;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Integer getSysPartnerStatus() {
        return sysPartnerStatus;
    }

    public void setSysPartnerStatus(Integer sysPartnerStatus) {
        this.sysPartnerStatus = sysPartnerStatus;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public Integer getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Integer updateTime) {
        this.updateTime = updateTime;
    }

    public String getAudStatus() {
        return audStatus;
    }

    public void setAudStatus(String audStatus) {
        this.audStatus = audStatus;
    }

    public String getAudPeople() {
        return audPeople;
    }

    public void setAudPeople(String audPeople) {
        this.audPeople = audPeople;
    }

    public Integer getAudTime() {
        return audTime;
    }

    public void setAudTime(Integer audTime) {
        this.audTime = audTime;
    }

    public OrderGoodsVo getGoods() {
        return goods;
    }

    public void setGoods(OrderGoodsVo goods) {
        this.goods = goods;
    }

    public List<ShareOrderTagBo> getShareOrderTagList() {
        return shareOrderTagList;
    }

    public void setShareOrderTagList(List<ShareOrderTagBo> shareOrderTagList) {
        this.shareOrderTagList = shareOrderTagList;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPraise_num() {
		return praise_num;
	}

	public void setPraise_num(Integer praise_num) {
		this.praise_num = praise_num;
	}

	public String getIsPraise() {
		return isPraise;
	}

	public void setIsPraise(String isPraise) {
		this.isPraise = isPraise;
	}
}
