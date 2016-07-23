package com.yoho.gateway.service.sns;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by yoho on 2015/11/7.
 */
public interface IMakeUrlService {

	String GUANG_CHANNEL_ACTION = "go.guangchannel";

	String BRAND_ACTION = "go.brand";

	String PRODUCT_DETAIL_ACTION = "go.productDetail";

	String COUPON_ACTION = "go.coupon";

	String FAV_ACTION = "go.fav";

	String LIST_ACTION = "go.list";

	String SHARE_ACTION = "go.share";

	String H5_ACTION = "go.h5";

	String ACTIVITY_ACTION = "go.activity";

	String GENDER_ACTION = "go.gender";

	String HOME_ACTION = "go.home";

	/**
	 * 处理链接
	 *
	 * @param action
	 * @param url
	 * @param clientType
	 * @return
	 */
	String url(String action, String url, String clientType);

	/**
	 * 处理链接
	 *
	 * @param urlData
	 *            { "action":"go.brand", "url":"http://www.yohobuy.com",
	 *            "params":{} }
	 * @param clientType
	 * @return
	 */
	String makeUrl(JSONObject urlData, String clientType);

	/**
	 * 处理链接
	 *
	 * @param action
	 * @param url
	 * @param params
	 * @param clientType
	 * @return
	 */
	String makeUrl(String action, String url, JSONObject params, String clientType);

	/**
	 * 处理链接。在url后面追加openby:yohobuy={params}
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	String make(String url, JSONObject params);
}
