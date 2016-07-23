package com.yoho.gateway.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 有关手机号码相关的工具类
 * 
 * @author xinfei
 *
 */
public class PhoneUtil {
	
	private final static String CHINA_AREA_CODE = "86";
	
	private static Map<String, String> areaPatternMap = new HashMap<String, String>();
	
	static{
		//------区域码，以及区域码对应的号码的校验正则表达式
		//中国
		areaPatternMap.put("86", "^1[3|4|5|8|7][0-9]{9}$");
		//中国香港
		areaPatternMap.put("852", "^[9|6|5][0-9]{7}$");
		//中国澳门
		areaPatternMap.put("853", "^[0-9]{8}$");
		//中国台湾
		areaPatternMap.put("886", "^[0-9]{10}$");
		//新加坡
		areaPatternMap.put("65", "^[9|8][0-9]{7}$");
		//马来西亚
		areaPatternMap.put("60", "^1[1|2|3|4|6|7|9][0-9]{7}$");
		//加拿大&美国
		areaPatternMap.put("1", "^[0-9]{10}$");
		//韩国
		areaPatternMap.put("82", "^01[0-9]{9}$");
		//英国
		areaPatternMap.put("44", "^7[7|8|9][0-9]{8}$");
		//日本
		areaPatternMap.put("81", "^0[9|8|7][0-9]{9}$");
		//澳大利亚
		areaPatternMap.put("61", "^[0-9]{11}$");
	}
	
	
	/**
	 * 根据区域码，对手机号码进行处理，如果不是中国手机号码，在号码之前加区域码
	 * 
	 * @param area 区域码
	 * @param mobile 手机号码
	 * @return String 处理之后的手机号码
	 */
	public static String makePhone(String area, String mobile){
		if(null == mobile || mobile.isEmpty()){
			return null;
		}
		if(null == area || area.isEmpty() || CHINA_AREA_CODE.equals(area)){
			return mobile;
		}
		return area + "-" + mobile;
	}
	
	/**
	 * 根据区域码，校验各个国家和地区的号码格式是否正确
	 *
	 * @param area
	 * @param mobile
	 * @return
	 */
	public static boolean areaMobileVerify(String area, String mobile){
		String[] arr = mobile.split("-");
		if(StringUtils.isEmpty(area) && arr.length == 1) {
			area = "86";
		} else if (arr.length == 2) {
			area = arr[0];
			mobile = arr[1];
		}
		
		if (!"86".equals(area)) {
            return mobile.matches("\\d+");
        }
		
		//根据国家或者地区码返回匹配模式
		String reg = areaPatternMap.get(area);
		if(null == reg){
			return false;
		}
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(mobile);
		boolean ret = m.matches();
		return ret;
	}
	
	/**
	 * 拆分手机号
	 * 
	 * @param mobile
	 * @return
	 */
	public static String[] splitMobile(String mobile) {
		if (StringUtils.isEmpty(mobile) || mobile.indexOf("-") < 0) {
			return null;
		}
		return mobile.split("-");
	}
	
	/**
	 * 验证手机号是否在制定的区域内
	 * 
	 * @param areaMobile
	 * @return
	 */
	public static boolean areaMobile(String areaMobile) {
		if (StringUtils.isEmpty(areaMobile)) {
			return false;
		}
		if (areaMobile.indexOf("-") < 0) {
			return areaMobileVerify("86", areaMobile);
		}
		String[] arr = splitMobile(areaMobile);
		if (arr.length != 2) {
			return false;
		}
		return areaMobileVerify(arr[0], arr[1]);
	}

	/**
	 * 验证是否都是数字
	 */
	public static boolean isMobile(String mobiles) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(mobiles).matches();
	}
	
	/**
	 * 获取四位短信验证码
	 * 
	 * @return String
	 */
	public static String getPhoneVerifyCode(){
		Random random = new Random();
		int result = random.nextInt(10000);
		if(result < 1000){
			result = result + 1000;
		}
		return String.valueOf(result);
	}
	

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
		System.out.println(PhoneUtil.areaMobileVerify("86", "18001582955"));
	}
}
