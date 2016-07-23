package com.yoho.gateway.controller.order.payment.tenpay.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class JsonUtil {

	public static String getJsonValue(String rescontent, String key) {
		JSONObject jsonObject;
		String v = null;
		try {
			jsonObject = JSON.parseObject(rescontent);  // new JSONObject(rescontent);
			v = jsonObject.getString(key);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return v;
	}
}
