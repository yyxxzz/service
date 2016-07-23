package com.yoho.gateway.service.sns;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by yoho on 2015/10/30.
 */
@Service
public class MakeUrlServiceImpl implements IMakeUrlService {

	private static final Logger logger = LoggerFactory.getLogger(MakeUrlServiceImpl.class);

	private static final String H5 = "h5";

	private static final String WEB = "web";

	private static final String YOHOBUY_WEBSITE = "http://www.yohobuy.com";

	private static final List<String> SIMPLE_ACTIONS;

	static {
		SIMPLE_ACTIONS = new ArrayList<>();
		SIMPLE_ACTIONS.add("go.mine");
		SIMPLE_ACTIONS.add("go.attention");
		SIMPLE_ACTIONS.add("go.plus");
		SIMPLE_ACTIONS.add("go.star");
		SIMPLE_ACTIONS.add("go.new");
		SIMPLE_ACTIONS.add("go.sale");
		SIMPLE_ACTIONS.add(GUANG_CHANNEL_ACTION);
		SIMPLE_ACTIONS.add("go.weblogin");
		SIMPLE_ACTIONS.add("go.yohood");
		SIMPLE_ACTIONS.add("go.top100");
		SIMPLE_ACTIONS.add("go.activitytemplate");
		SIMPLE_ACTIONS.add("go.globalpurchase");
		SIMPLE_ACTIONS.add("go.subchannel");
		SIMPLE_ACTIONS.add("go.showgoods");
		SIMPLE_ACTIONS.add("go.limitpurchase");

	}

	// @Resource
	// private IBrandDao brandDao;
	//
	// @javax.annotation.Resource
	// private CacheService cacheService;

	/**
	 * 处理url
	 */
	public String url(String action, String url, String clientType) {
		if (H5.equalsIgnoreCase(clientType) || WEB.equalsIgnoreCase(clientType)) {
			return url;
		}
		if (StringUtils.isEmpty(action) || StringUtils.isEmpty(url)) {
			return "";
		}
		url = decoderURL(decoderURL(url));
		JSONObject urlObj = parseUrl(url);
		JSONObject query = urlObj.getJSONObject("query");
		return makeUrl(action, url, query, clientType);

	}

	/**
	 * 将url转化成json格式对象 em: http://localhost/resources/pro?k1=v1&k2=v2 转化成
	 * {"scheme"
	 * :"http","port":"80","host":"localhost","path":"/resources/pro","query"
	 * :{"k1":"v1","k2":"v2"}}
	 *
	 * @param url
	 * @return
	 */
	private JSONObject parseUrl(String url) {
		URL parseUrl;
		try {
			if (url.contains("://")) {
				parseUrl = new URL(url);
			} else {
				parseUrl = new URL("http://" + url);
			}
		} catch (MalformedURLException e) {
			logger.warn("malformed url:{}", url);
			return new JSONObject();
		}
		JSONObject urlObj = new JSONObject();
		urlObj.put("scheme", parseUrl.getProtocol());
		// 如果是默认端口，则设置默认端口。
		urlObj.put("port", parseUrl.getPort() == -1 ? parseUrl.getDefaultPort() : parseUrl.getPort());
		urlObj.put("host", parseUrl.getHost());
		urlObj.put("path", parseUrl.getPath());
		JSONObject query = new JSONObject();
		if (StringUtils.isNotEmpty(parseUrl.getQuery())) {
			String[] queryConds = parseUrl.getQuery().split("&");
			for (String queryCond : queryConds) {
				String[] ent = queryCond.split("=");
				if (ent.length > 1) {
					query.put(ent[0], ent[1]);
				}
			}
		}
		urlObj.put("query", query);
		return urlObj;
	}

	public String makeUrl(JSONObject urlData, String clientType) {
		if (urlData == null || urlData.isEmpty()) {
			return "";
		}
		String url = urlData.getString("url");
		JSONObject params = urlData.getJSONObject("params");
		if (null != params) {
			params = new JSONObject();
		}
		if (StringUtils.isNotEmpty(url)) {
			return url(urlData.getString("action"), url, clientType);
		} else {
			return makeUrl(urlData.getString("action"), YOHOBUY_WEBSITE, params, clientType);
		}
	}

	/**
	 * 处理链接
	 */
	public String makeUrl(String action, String url, JSONObject params, String clientType) {
		if (H5.equalsIgnoreCase(clientType) || WEB.equalsIgnoreCase(clientType)) {
			return url;
		}
		if (StringUtils.isEmpty(action) || StringUtils.isEmpty(url)) {
			return "";
		}
		if (params == null) {
			params = new JSONObject();
		}
		if (SIMPLE_ACTIONS.contains(action)) {
			return markUrl(action, url, params);
		}
		// else if (BRAND_ACTION.equals(action)) {
		// return brandUrl(action, url, params);
		// }
		else if (PRODUCT_DETAIL_ACTION.equals(action)) {
			return productDetailUrl(action, url, params);
		} else if (COUPON_ACTION.equals(action)) {
			return markUrl(action, url, params);
		} else if (FAV_ACTION.equals(action)) {
			return favoriteUrl(action, url, params);
		} else if (LIST_ACTION.equals(action)) {
			return listUrl(action, url, params);
		} else if (SHARE_ACTION.equals(action)) {
			return shareUrl(action, url, params);
		} else if (H5_ACTION.equals(action)) {
			return h5(action, url, params);
		} else if (ACTIVITY_ACTION.equals(action)) {
			return activityUrl(action, url, params);
		} else if (GENDER_ACTION.equals(action)) {
			return genderUrl(action, url, params);
		} else if (HOME_ACTION.equals(action)) {
			return homeUrl(action, url, params);
		} else {
			logger.warn("unknown action {} parse the url {}.", action, url);
			return markUrl(action, url, params);
		}
	}

	private String activityUrl(String action, String url, JSONObject params) {
		if (StringUtils.isEmpty(params.getString("link"))) {
			params.put("link", url);
		}
		return markUrl(action, url, params);
	}

	private String shareUrl(String action, String url, JSONObject params) {
		if (StringUtils.isEmpty(params.getString("url"))) {
			params.put("url", url);
		}
		return markUrl(action, url, params);
	}

	private String markUrl(String action, String url, JSONObject params) {
		JSONObject tmp = new JSONObject();
		tmp.put("action", action);
		if (params != null && !params.isEmpty()) {
			tmp.put("params", params);
		}
		return make(url, tmp);
	}

	// private String brandUrl(String action, String url, JSONObject params) {
	// String[] urlArr = url.split("\\.");
	// if (urlArr == null || urlArr.length == 0) {
	// return "";
	// }
	// String brandDomain = urlArr[0].replaceAll("http://", "");
	// if (StringUtils.isEmpty(brandDomain) || "www".equals(brandDomain)) {
	// return "";
	// }
	// if (StringUtils.isEmpty(params.getString("brand_id"))) {
	// String key = "/brands/domains/" + brandDomain;
	// Object obj = cacheService.get(key);
	// Brand brand;
	// if (obj == null) {
	// brand = brandDao.selectByBrandDomain(brandDomain);
	// if (brand != null) {
	// cacheService.put(key, brand);
	// }
	// } else {
	// brand = (Brand) obj;
	// }
	// if (brand == null) {
	// return "";
	// }
	// params.put("brand_id", brand.getId().toString());
	// }
	// return markUrl(action, url, params);
	// }

	private String productDetailUrl(String action, String url, JSONObject params) {
		if (StringUtils.isEmpty(params.getString("product_skn"))) {
			String productId = getProductId(url);
			if (StringUtils.isNotEmpty(productId)) {
				params.put("product_id", productId);
			}
		}
		return markUrl(action, url, params);
	}

	private String getProductId(String url) {
		Pattern pattern = Pattern.compile("/pro_([\\d]+)_[\\d]+/.*\\.html");
		Matcher matcher = pattern.matcher(url);
		List<String> matchers = new ArrayList<String>();
		while (matcher.find()) {
			matchers.add(matcher.group(1));
		}
		if (!matchers.isEmpty() && StringUtils.isNotEmpty(matchers.get(0))) {
			return matchers.get(0);
		}
		return null;
	}

	private String favoriteUrl(String action, String url, JSONObject params) {
		int favType = url.contains("/home/favorite/brand") ? 1 : 0;
		params.put("action", action);
		if (null == params.getJSONObject("params")) {
			params.put("params", new JSONObject());
		}
		params.getJSONObject("params").put("favType", favType);
		return make(url, params);
	}

	/**
	 * 品类列表和搜索列表
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	private String listUrl(String action, String url, JSONObject params) {
		if (StringUtils.isNotEmpty(params.getString("query"))) {
			params.put("query", decoderURL(decoderURL(params.getString("query"))));
		}
		if (StringUtils.isNotEmpty(params.getString("title"))) {
			params.put("title", decoderURL(decoderURL(params.getString("title"))));
		}
		params.put("actiontype", url.contains("http://list") ? 1 : 0);
		return markUrl(action, url, params);
	}

	private String h5(String action, String url, JSONObject params) {
		params.put("url", url.replaceAll("\\?.*", ""));
		if (StringUtils.isNotEmpty(params.getString("share_id"))) {
			handlerH5ParamsWhenHasShareId(params);
		} else {
			handlerH5ParamsWhenNotShareId(url, params);
		}
		return markUrl(action, url, params);
	}

	private void handlerH5ParamsWhenHasShareId(JSONObject params) {
		JSONObject param = JSONObject.parseObject(params.toJSONString());
		if (StringUtils.isNotEmpty(param.getString("url"))) {
			param.remove("url");
		}
		params.put("share", "/operations/api/v5/webshare/getShare");
		JSONObject shareparam = new JSONObject();
		shareparam.put("share_id", params.getString("share_id"));
		params.put("shareparam", shareparam);
		params.put("param", param);
		params.remove("share_id");
	}

	private void handlerH5ParamsWhenNotShareId(String url, JSONObject params) {
		JSONObject param = params.getJSONObject("param");
		if (param == null) {
			param = new JSONObject();
		}
		if (StringUtils.isNotEmpty(params.getString("id"))) {
			param.put("id", params.getString("id"));
			if (StringUtils.isEmpty(params.getString("share")) && url.contains("info/index")) {
				params.put("share", "/guang/api/v1/share/guang");
				JSONObject shareparam = new JSONObject();
				shareparam.put("id", params.getString("id"));
				params.put("shareparam", shareparam);
			}
			if (!params.containsKey("type") && params.getString("url").contains("/info/index")) {
				params.put("type", 1);
			}
			params.put("param", param);
		} else if (StringUtils.isNotEmpty(params.getString("content_code"))) {
			param.put("content_code", params.getString("content_code"));
			if (StringUtils.isEmpty(params.getString("share"))) {
				params.put("share", "/guang/api/v1/share/activityInfo");
				JSONObject shareparam = new JSONObject();
				shareparam.put("content_code", params.getString("content_code"));
				params.put("shareparam", shareparam);
			}
			if (!params.containsKey("type")) {
				params.put("type", 1);
			}
			params.put("param", param);
		} else if (StringUtils.isNotEmpty(params.getString("query"))) {
			param.put("query", params.get("query"));
			params.put("param", param);
		}
	}

	private String genderUrl(String action, String url, JSONObject params) {

		int channel = getChannel(url);
		if (null == params.getJSONObject("params")) {
			params.put("params", new JSONObject());
		}
		params.getJSONObject("params").put("gender", channel);
		params.put("channel", channel);

		params.put("action", action);
		return make(url, params);
	}

	private String homeUrl(String action, String url, JSONObject params) {
		int channel = getChannel(url);
		params.put("gender", channel);
		params.put("channel", channel);
		JSONObject param = new JSONObject();
		param.put("action", action);
		param.put("params", params);
		return make(url, param);
	}

	private int getChannel(String url) {
		int channel;
		if (url.contains("girls.html")) {
			channel = 2;
		} else if (url.contains("boys.html")) {
			channel = 1;
		} else if (url.contains("kids.html")) {
			channel = 3;
		} else if (url.contains("life.html")) {
			channel = 4;
		} else if (url.contains("yohood.html")) {
			channel = 5;
		} else {
			channel = 1;
		}
		return channel;
	}

	public String make(String url, JSONObject params) {
		if (null == params || params.isEmpty()) {
			return url;
		}
		if (params.containsKey("params") && null == params.get("params")) {
			params.remove("params");
		}
		if (url.contains("?")) {
			url += "&openby:yohobuy=" + params.toJSONString();
		} else {
			url += "?openby:yohobuy=" + params.toJSONString();
		}
		return url;
	}

	private String decoderURL(String url) {
		try {
			if (url == null) {
				return null;
			}
			return URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// never run
			logger.error("unsupported encoding exception :{}", "UTF-8");
			return url;
		}
	}

}
