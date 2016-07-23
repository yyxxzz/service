package com.yoho.gateway.helper;

import org.apache.commons.lang3.StringUtils;

/**
 * 手机号码公共处理helper类
 * 
 * @author yoho
 *
 */
public class MobileHelper {

	/**
	 * 覆盖手机号码中4位为*
	 * 
	 * @param mobile
	 * @return
	 */
	public static String coverMobile(String mobile) {
		if (StringUtils.isEmpty(mobile)) {
			return mobile;
		}
		// 11位国内手机号
		if (mobile.matches("\\d{11}")) {
			StringBuffer sb = new StringBuffer();
			sb.append(mobile.substring(0, 3)).append("****").append(mobile.substring(7));
			return sb.toString();
		}

		// 国际号码
		int index = mobile.indexOf("-");
		if (0 < index && mobile.length() > index + 3) {
			StringBuffer sb = new StringBuffer();
			sb.append(mobile.substring(0, index + 4)).append("****");
			if (mobile.length() > index + 7) {
				sb.append(mobile.substring(index + 8));
			}
			return sb.toString();
		}
		return mobile;
	}
	
	public static void main(String[] args) {
		String a = "012-4";
		System.out.println(coverMobile(a));
	}

}
