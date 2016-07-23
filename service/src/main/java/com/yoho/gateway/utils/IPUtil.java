package com.yoho.gateway.utils;

public class IPUtil {
	
	/**
	 * 将字符串类型的IP转换成 int类型的IP
	 * 生成规则：
	 * ip第一位 x 256的3次方 ＋ ip第二位 x 256的2次方 ＋ ip第三位 x 256 + ip第四位
	 *    
	 * @param ipAddress IP地址，10.1.1.1
	 * @return int
	 */
	public static long ip2Long(String ipAddress){
		long ip = 0;
		if(null == ipAddress || ipAddress.isEmpty()){
			return ip;
		}
		//拆分ip地址，如果长度小于4，则ip异常，直接返回0
		String[] ips = ipAddress.split("\\.");
		if(ips.length != 4){
			return ip;
		}
		long ip0 = Integer.parseInt(ips[0]);
		long ip1 = Integer.parseInt(ips[1]);
		long ip2 = Integer.parseInt(ips[2]);
		long ip3 = Integer.parseInt(ips[3]);
		ip = ip0 * (256 * 256 * 256) + ip1 * (256 * 256) + ip2 * 256 + ip3;
		return ip;
	}

	/**
	 *
	 * 将LONG类型的IP地址,转换成字符串类型
	 *
	 * @param ipLong LONGIP地址
	 *
	 * @return String类型的IP地址
	 */
	public static String long2StrIp(long ipLong){
		return new StringBuilder().append(((ipLong >> 24) & 0xff)).append('.')
					.append((ipLong >> 16) & 0xff).append('.').append(
							(ipLong >> 8) & 0xff).append('.').append((ipLong & 0xff))
					.toString();
	}

	public static void main(String[] args){
		long v = IPUtil.ip2Long("192.168.102.202");
		System.out.print(v);
		System.out.print(long2StrIp(0));

	}

}
