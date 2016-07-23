package com.yoho.gateway.utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils extends org.apache.commons.lang3.StringUtils{
	/**
	 * URL链接参数分隔符
	 */
	private static final String SEPARATOR_URL_PARAM = "&";

	/**
	 * 数字型字符串转成数值类型
	 * @param str 字符型
	 * @return
	 */
	public static Integer parseInt(String str) {
		if (null == str) {
			return null;
		}
		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * 将一个逗号分隔的字符串，转换为int组成的字符串， 再转成字符串
	 * @param str 字符型
	 * @return
	 */
	public static String converInt(String str, String seprator) {
        if (isBlank(str)) {
        	return "";
        }
        String[] split = str.split(seprator);
        StringBuilder sb =  new StringBuilder();
        Integer parseInt;
        for (String item : split) {
        	parseInt = parseInt(item.trim());
        	if (null != parseInt && parseInt > 0) {
        		sb.append(parseInt).append(seprator);
        	}
        }
        if (sb.length() == 0) {
        	return "";
        }
        return sb.substring(0, sb.length() - 1);
	}
	
	/**
	 * 将一个逗号分隔的字符串，转成字符串数组；每个字符串的前后去掉空格
	 * @param str 字符型
	 * @return
	 */
	public static String[] converArray(String str, String seprator) {
		if (isBlank(str)) {
			return new String[0];
		}
		String[] split = str.split(seprator);
		String[] result = new String[split.length];
		for (int i = 0; i < split.length ; i++) {
			result[i] = split[i].trim();
		}
		return result;
	}
	
	/**
	 * map集合转成URL的参数字符串
	 * @param paramMap 参数集合
	 * @return URL的参数字符串
	 */
	public static String convertUrlParamStrFromMap(Map<String, String> paramMap) {
		if (paramMap == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append(SEPARATOR_URL_PARAM);
		}
		String s = sb.toString();
		if (s.endsWith(SEPARATOR_URL_PARAM)) {
			s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, SEPARATOR_URL_PARAM);
		}
		return s;
	}

	/**
	 * map集合转成URL的参数字符串
	 * @param paramMap
	 * @return
	 */
	public static String convertUrlParamStrFromMap2(Map<String, ? extends Object> paramMap) {
		if (paramMap == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Entry<String, ? extends Object> entry : paramMap.entrySet()) {
			Object value = entry.getValue();
			if (value == null) continue;
			sb.append(entry.getKey() + "=" + entry.getValue());
			sb.append(SEPARATOR_URL_PARAM);
		}
		String s = sb.toString();
		if (s.endsWith(SEPARATOR_URL_PARAM)) {
			s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, SEPARATOR_URL_PARAM);
		}
		return s;
	}
	
	/**
	 * 验证email
	 * 
	 * @param mail
	 * @return
	 */
	public static boolean validateMail(String mail) {
		return validate("^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$", mail);
	}
	
	private static boolean validate(String reg, String value) {
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(value);
		boolean ret = m.matches();
		return ret;
	}
	
	/**
	 * 注册校验密码，密码规则，英文字母或者数字
	 * 
	 * @param password
	 *            string
	 * @return
	 */
	public static boolean registerValidatePassword(String password) {
		if (StringUtils.isBlank(password)) {
			return false;
		}
		String reg = "^[a-zA-Z0-9~!@#$%^&*()-_+=<,>./?;:\"'{\\[}\\]\\|]{6,20}$";
		return password.matches(reg);
	}



}
