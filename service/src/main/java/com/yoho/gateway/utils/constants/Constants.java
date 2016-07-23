package com.yoho.gateway.utils.constants;

/**
 * 常量类
 * 
 * @author lijian
 * 
 */
public interface Constants {

	public static final String YOHO_HOME_URL = "http://www.yohobuy.com";

	String TN_STATUS = "TN-Status";

	//登录-同一个IP限制登录次数KEY
	public final static String LOGIN_IP_LIMIT_TIMES = "yh:users:loginIpLimit:";
	
	//登录-商家端-同一个IP限制登录次数KEY
	public final static String LOGIN_SHOPS_ID_LIMIT_TIMES = "yh:shops:loginIpLimit:";

	//登录-同一个手机号码,一分钟之内的登录失败次数KEY
	public final static String LOGIN_FAILED_TIMES = "yh:users:loginFailed:";
	
	//登录-商家端,账号一分钟之内的登录失败次数KEY
	public final static String LOGIN_SHOPS_FAILED_TIMES = "yh:shops:loginFailed:";

	// 手机号码注册，memcache中存入注册验证码的key
	public final static String REGISTER_CODE_MEM_KEY = "yh:users:register_mobile_";

	// 手机注册，将提交注册判断的次数存入cache中，存入次数的key
	public final static String REGISTER_TIME_MEM_KEY = "yh:users:register_time_";

	// 手机找回密码时的，储存手机验证码的KEY
	public final static String BACK_PASSWORD_MEM_KEY = "yh:users:repassword_mobile_";

	// 手机号找回密码的次数
	public final static String BACK_PASSWORD_TIME_MEM_KEY = "yh:users:repassword_times_";

	// 利用邮箱找回密码的次数
	public final static String BACK_PASSWORD_TIME_MEM_KEY_EMAIL = "repassword_times_email_";

	// 绑定手机号，检查验证码次数
	public final static String BIND_MOBILE_TIME_MEM_KEY = REGISTER_TIME_MEM_KEY;

	// 绑定手机号时，验证码保存的key
	public final static String BIND_MOBILE_MEM_KEY = REGISTER_CODE_MEM_KEY;

	// 发送生日送券验证码
	public final static String BIRTHDAY_COUPON_CODE_MEM_KEY = "birthday_code_mobile_";

	// 浏览记录同步开关key
	public static final String SYNCBROWSE_ONOFF_NAME = "users.browse.sync";

	// 浏览记录过期时间key
	public static final String BROWSE_EXPIRETIME_NAME = "users.browse.expire";
	
	//验证手机时的，验证码保存的key
	public final static String VERIFY_MOBILE_MEM_KEY = "yh:users:verify:VERIFY_MOBILE_MEM_KEY_";
	
	//验证手机时，检查验证码次数的key
	public final static String VERIFY_MOBILE_TIME_MEM_KEY = "yh:users:verify:VERIFY_MOBILE_TIME_MEM_KEY_";
	
	//消息盒子数量key
	public static final String INBOX_NUM_EXPIRETIME_NAME = "message.inboxNum.expire";

}
