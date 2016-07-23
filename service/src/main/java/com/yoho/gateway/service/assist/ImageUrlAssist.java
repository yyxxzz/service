package com.yoho.gateway.service.assist;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * 图片URL拼接的辅助接口
 * @author mali
 *
 */
public class ImageUrlAssist {
	private static final String INIT_POSITION = "center";


	private static final String INIT_BACKGROUND = "d2hpdGU=";


	private static final String INIT_SIZE = "source";


	private static final Logger logger = LoggerFactory.getLogger(ImageUrlAssist.class);
	
	
	/**
	 * 图片尺寸字符串的分隔符
	 */
	private static final String SEPRATOR_SIZE  = "x";
	
	/**
	 * 获取图片的完整路径     包含参数
	 * @param imageUrl 相对路径
	 * @param bucket   
	 * @param position
	 * @param background
	 * @return 图片的完整路径
	 */
	public static String getAllProductPicUrl(Object imageUrl, String bucket, String position, String background) {
		if (!(imageUrl instanceof String) || StringUtils.isEmpty((String)imageUrl)) {
			return "";
		}
		if (StringUtils.isEmpty(background)) {
			background = INIT_BACKGROUND;
		}
		if (StringUtils.isEmpty(position)) {
			position = INIT_POSITION;
		}
		return getUrl((String)imageUrl, bucket, null, null) + "?imageMogr2/thumbnail/{width}x{height}/extent/{width}x{height}/background/" 
				+ background + "/position/" + position + "/quality/80";
	}
	
	/**
	 * 获取图片的路径，不含参数
	 * @param imageUrl
	 * @param bucket
	 * @return
	 */
	public static String getUrl(String imageUrl, String bucket, String source, Integer mode) {
		if (StringUtils.isEmpty(imageUrl)) {
			return "";
		}
		source = StringUtils.isEmpty(source) ? INIT_SIZE : source;
		mode = null == mode ? 1 : mode;
		Integer width = null; 
		Integer	height = null;
		if (StringUtils.isEmpty(source) && !INIT_SIZE.equals(source)) {
			String[] split = source.split(SEPRATOR_SIZE);
			if (split.length == 2) {
				try {
					width = Integer.valueOf(split[0]);
					height = Integer.valueOf(split[1]);
				} catch (NumberFormatException e) {
					logger.warn("The source of goodsPic is not number. imageUrl is " + imageUrl + "; source : " + source, e);
				}
			}
        }
		String domain = getDomain(imageUrl);
        if (StringUtils.isEmpty(domain)) {
            return "";
        }
		return getImgPrivateUrl(bucket + imageUrl, width, height, mode, domain);
	}
	
	/**
	 * 获取私有的图片地址
	 * @param fileName
	 * @param width
	 * @param height
	 * @param mode
	 * @param domain
	 * @return
	 */
	private static String getImgPrivateUrl(String fileName, Integer width, Integer height, Integer mode, String domain) {
		if (null == mode) {
			mode = 1;
		}
		
		if (StringUtils.isEmpty(domain)) {
			domain = "yhfair.qiniudn.com";
        }
        
		return makeRequest("http://" + domain + "/" + fileName.replaceAll("%2F", "/"), mode, width, height, domain, null, null);
	}
	
	/**
	 * 拼接图片的URL参数
	 * @param baseUrl
	 * @param mode
	 * @param width
	 * @param height
	 * @param domain
	 * @param quality
	 * @param format
	 * @return
	 */
	private static String makeRequest(String baseUrl, Integer mode, Integer width, Integer height, String domain,
			String quality, String format) {
		StringBuilder sb = new StringBuilder();
		if (null != mode) {
			sb.append(mode);
        }
		if (null != width) {
			sb.append("/w/").append(width);
        }
		if (null != height) {
			sb.append("/h/").append(height);
        }  
		if (null != quality) {
			sb.append("/q/").append(quality);
		}  
		if (null != format) {
			sb.append("/format/").append(format);
		}  
		if (0 == sb.length()) {
			return baseUrl;
		}  
		if (null == width || null == height) {
			return baseUrl;
		}  
	    return baseUrl + "?imageView/" + sb.toString();
	}
	
	
	private static String getDomain(String imageUrl) {
		if (imageUrl.length() < 17) {
			return "";
		}
	    String node = imageUrl.substring(15, 17);
	    List<String> domainList = getDomainList(node);
	    if (domainList.isEmpty()) {
	    	return "";
	    }
	    return domainList.get(Math.random() > 0.5 ? 1 : 0);
	}
	
	/**
	 * 图片的节点服务器列表
	 * @param node
	 * @return
	 */
	private static List<String> getDomainList(String node) {
		if ("01".equals(node)) {
			return Lists.newArrayList("img10.static.yhbimg.com", "img11.static.yhbimg.com");
		}else if("02".equals(node)) {
			return Lists.newArrayList("img12.static.yhbimg.com", "img13.static.yhbimg.com");
		}
		return new ArrayList<String>(0);
	}
}
