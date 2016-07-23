/**
 * 
 */
package com.yoho.gateway.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.yoho.core.common.utils.MD5;

/**
 * @author ping.huang 2016年6月8日
 */
public class TestClientSecret {
	
	static Map<String, String> privateKeyMap = new HashMap<String, String>();
	
	static {
		privateKeyMap.put("iphone", "a85bb0674e08986c6b115d5e3a4884fa");
		privateKeyMap.put("ipad", "ad9fcda2e679cf9229e37feae2cdcf80");
		privateKeyMap.put("android", "fd4ad5fcfa0de589ef238c0e7331b585");
		privateKeyMap.put("yoho", "fd4ad5fcsa0de589af23234ks1923ks");
		privateKeyMap.put("h5", "fd4ad5fcfa0de589ef238c0e7331b585");
		privateKeyMap.put("web", "0ed29744ed318fd28d2c07985d3ba633");
	}

	@Test
	public void getClientSecret() {
		// remove some reqParams
		
		Map<String, String> reqParams = new HashMap<String, String>();
		reqParams.put("method", "wap.activity.getActivityInfo");
		
		ImmutableList list = ImmutableList.of("/api", "client_secret", "q", "debug_data");
		SortedMap<String, String> filtedMap = new TreeMap<>();
		for (Map.Entry<String, String> entry : reqParams.entrySet()) {
			String k = entry.getKey();
			if (!list.contains(k)) {
				filtedMap.put(k, entry.getValue());
			}
		}

		// put private
		String clientType = reqParams.get("client_type");
		String privateKey = privateKeyMap.get(clientType);
		filtedMap.put("private_key", privateKey);

		// string: k1=v1&k2=v2
		List<String> array = new LinkedList<>();
		for (Map.Entry<String, String> entry : filtedMap.entrySet()) {
			String pair = entry.getKey() + "=" + entry.getValue();
			array.add(pair.trim());
		}
		String signStr = String.join("&", array);

		// sign md5
		String sign = MD5.md5(signStr);

		System.out.println(sign);
	}
}
