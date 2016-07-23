package com.yoho.gateway.controller.order.payment.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	public static final int EXPIRE_INTERVAL = 7200;  // 默认支付超时时间, 2小时
	
	/**
	 * 时间戳格式转换, "20160120163217" => "2016-01-20 16:32:17"
	 * @param time
	 * @return
	 */
	public static String formatTime(String time) {
	    if (null == time) {
	        return "";
	    }
	
	    DateFormat formatFrom = new SimpleDateFormat("yyyyMMddHHmmss");	    
        DateFormat formatTo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    try {
	        Date date = formatFrom.parse(time);
	        String result = formatTo.format(date);
	        return result;
	    } catch (ParseException e) {
	        return "";
	    }
	}

	/**
	 * 计算支付超时时间, 订单生成时间加上超时间隔 "20160120163217"
	 * 传入订单生成时间, 返回字符串
	 */
	public static String getExpireTime(int createTime) {
	    int expire = createTime + EXPIRE_INTERVAL;
	    Date date = new Date(expire * 1000L);
	
	    DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
	
	    return format.format(date);
	}

	/**
	 * 获取当前时间,格式为 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getCurrentTime() {
	    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	    Date curr = new Date();
	    String res = format.format(curr);
	    return res;
	}

	/**
	 *
	 */
	public static int getExpireTimeInSec(int createTime) {
	    int expire = createTime + EXPIRE_INTERVAL;
	    return expire;
	}
	
}


