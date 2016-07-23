package com.yoho.gateway.cache.expire.product;

/**
 * Created by chenchao on 2016/4/27.
 */
public interface ExpireTime {
    /**
     *
     */
    int h5_preference_search = 300;
    /**
     *
     */
    int h5_sortPreference_search = 300;
    /**
     *线下店所有品牌
     */
    int cnstore_brandlist_get = 300;
    /**
     *
     */
    int cnstore_product_get = 300;
    /**
     *
     */
    int cnstore_product_duoban = 300;
    /**
     * 线下店商品介绍
     */
    int cnstore_product_info = 300;
    /**
     * 查询店铺对应的装修元素
     */
    int app_shopsdecorator_getList = 300;
    /**
     * 根据店铺id查询店铺介绍
     */
    int app_shops_getIntro = 300;
    /**
     * 根据店铺id查询该店铺下面的品牌
     */
    int app_shops_getShopsBrands = 300;
    /**
     * 根据skn查询商品下半页详情描述信息
     */
    int h5_product_intro = 300;

    /**
     * 获取产品中分类的列表
     */
    int app_category_getMax = 300;

    /**
     * 获取产品大分类的列表
     */
    int app_category_queryMax= 300;
    /**
     * 获取产品小分类的列表
     */
    int app_category_queryMin = 300;
    /**
     *
     */
    int app_comment_li = 300;
    /**
     * 咨询列表
     */
    int app_consult_li = 300;
    /**
     * 查询最新的两条咨询
     */
    int app_consult_lastTwo = 300;
    /**
     * 商品详情页 下半部分
     */
    int app_product_intro = 300;
    /**
     * 商品详情页-为你优选
     */
    int app_product_preference = 600;
    /**
     * 买了再买
     */
    int app_product_purchased = 300;
    /**
     *
     */
    int web_html_content = 300;
    /**
     * 根据品牌域名获取信息
     */
    int web_brand_byDomain = 300;
    /**
     * 获取品牌分类
     */
    int web_brand_folder = 300;
    /**
     * 获取品牌系列
     */
    int web_brand_series= 300;
    /**
     * 根据产品ID查询banner
     */
    int web_productBanner_data = 300;
    /**
     * 根据产品ID查询搭配
     */
    int web_productCollocation_list = 300;
    /**
     * 根据产品ID查询模特卡
     */
    int web_productModelcard_list = 300;
    /**
     *
     */
    int web_queryProductInfoBySkuId = 300;
    /**
     *模特试穿
     */
    int web_productModelTry_data = 300;
    /**
     * 根据产品ID查询舒适度
     */
    int web_productComfort_data = 300;
    /**
     * 根据活动模板id获取商品
     */
    int operations_activitytemp_getProduct = 300;
    /**
     *
     */
    int web_brand_banner = 300;
    /**
     *
     */
    int app_brand_banner = 300;
    /**
    *
    */
    int app_shop_banner = 300;
    /**
     * 创意生活频道商品搜索
     */
    int app_search_lifeStyle = 300;
    /**
     * 潮童频道商品搜索
     */
    int app_search_kids = 300;
    /**
     * 根据dayLimit查询某一区间的商品列表
     */
    int app_search_newProduct = 300;
    /**
     * 根据频道查询其下所有品牌
     */
    int app_brand_brandlist = 300;
    /**
     *
     */
    int app_search_brand = 300;
    /**
    *
    */
   int app_search_shop = 300;
    /**
     * 模糊搜索商品
     */
    int app_search_li = 300;
    /**
     * 根据分类的信息查询当前分类下的商品列表
     */
    int app_search_category = 300;

    /**
     * 奥莱潮品速递的商品列表
     */
    int app_search_trend = 300;
    /**
     * 折扣精品
     */
    int app_search_sales= 300;
    /**
     * 查询所有的分类列表
     */
    int app_sort_get = 300;
    /**
     * 搜索建议
     */
    int app_search_fuzzy = 300;

    /**
     * 热搜品牌
     */
    int app_search_hotBrands = 300;

    /**
     * 热搜品牌记录
     */
    int app_search_hotBrandRecords = 300;
}
