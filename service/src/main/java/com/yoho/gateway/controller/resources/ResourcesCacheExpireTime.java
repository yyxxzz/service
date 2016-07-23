package com.yoho.gateway.controller.resources;

/**
 * Created by LUOXC on 2016/7/1.
 */
public interface ResourcesCacheExpireTime {
    int RESOURCES_GET = 300;


    int RESOURCES_HOME = 300;


    /**
     * 判断是否新客
     */
    int RESOURCES_IS_NEW_USER = 3600;

    /**
     * WEB 分享
     */
    int WEB_SHARE = 300;


    /**
     * WEB 分享
     */
    int APP_VERSION = 300;

    int SPECIAL = 60;

    int NOTICES = 60;

    int ADS_LIST = 60;

    int SEARCH_BANNER = 60;

    int CATEGORY_TREE = 60;

    int CATEGORY_DETAIL = 60;


    int CONFIG_CLIENT_INIT_CONFIG = 60;

    int CONFIG_ICON_CONFIG = 60;

    int HTML_STATIC_CONTENT = 60;

    int HOT_RANK_TAG = 60;

    int ENTRANCE = 60;

    int COVER_START = 60;

}
