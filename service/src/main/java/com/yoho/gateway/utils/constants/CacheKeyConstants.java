package com.yoho.gateway.utils.constants;

/**
 * Created by yoho on 2016/4/13.
 */
public class CacheKeyConstants {

    /**
     * 资源位，判断是否新客楼层
     */
    public static final String RESOURCES_IS_NEW_USER = "yh:resources:is_new_user:";

    //=====================个人中心请求数量的REDIS的KEY================================
    // 等待支付数量redis缓存key前缀
    public final static String YHGW_WAITPAYNUM_PRE = "yh:gw:waitPayNum:";
    // 代发货数量redis缓存key前缀
    public final static String YHGW_WAITCARGONUM_PRE = "yh:gw:waitCargoNum:";
    // 已发货数量redis缓存key前缀
    public final static String YHGW_SENDCARGONUM_PRE = "yh:gw:sendCargoNum:";
    // 退换货数量redis缓存可以前缀
    public final static String YHGW_REFUNDEXCHANGENUM_PRE = "yh:gw:refundExchangeNum:";
    // 收藏品牌数量redis缓存可以前缀
    public final static String YHGW_BRANDFAVNUM_PRE = "yh:gw:brandFavNum:";
    // 收藏商品数量redis缓存可以前缀
    public final static String YHGW_PRODUCTFAVNUM_PRE = "yh:gw:productFavNum:";
    // 消息盒子数量redis缓存可以前缀
    public final static String YHGW_INBOXNUM_PRE = "yh:gw:inboxNum:";
    // 有货币数量redis缓存可以前缀
    public final static String YHGW_YOHOCOINNUM_PRE = "yh:gw:yohoCoinNum:";
    // 优惠券数量redis缓存可以前缀
    public final static String YHGW_COUPONNUM_PRE = "yh:gw:couponNum:";
    // 评论数量redis缓存可以前缀
    public final static String YHGW_COMMENTNUM_PRE = "yh:gw:commentNum:";
    // 待晒单数量redis缓存可以前缀
    public final static String YHGW_TOSHAREORDERNUM_PRE = "yh:gw:toShareOrderNum:";
    // 限购码数量redis缓存可以前缀
    public final static String YHGW_LIMITCODERECORDNUM_PRE = "yh:gw:limitCodeRecordNum:";
    // 用户浏览记录数量redis缓存可以前缀
    public final static String YHGW_PRODUCTBROWSENUM_PRE = "yh:gw:productBrowseNum:";

    //=====================商品中心请求服务的REDIS的KEY================================
    // 所有中分类的信息集合
    public final static String YHGW_ALLMIDDELSORT_LIST = "yh:gw:allMiddleSort";

}
