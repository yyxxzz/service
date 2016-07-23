package com.yoho.gateway.utils;


import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public final class ProductDetailHtmlUtil {
	

	public static String fixHtml(String infoWithHtml){
		String result = "";
		if(infoWithHtml == null){
			return result;
		}
		//截取，h5已经放弃
		//String br = "<br />";
		//String noWords = infoWithHtml.contains(br) ? infoWithHtml.substring(infoWithHtml.indexOf(br)) : infoWithHtml;
		Pattern p = Pattern.compile("\\t|\\r|\\n");
		result = p.matcher(infoWithHtml).replaceAll("");
         //'</p>' => '',
		result = result.replaceAll("\\<\\/p\\>", "");
         //       '<br />' => "\r\n",
		String reg_br = "\\<br\\s*\\/\\s*\\>";
		result = result.replaceAll(reg_br, "\r\n");
		//懒加载 放弃
		//result = result.replaceAll("\\<img\\s*src=", imgPrefix);
		result = addQuickLoadParamsBefore(result, imgTypes);
		//result = addQuickLoadParams(result);//原始版本，有bug;
		return result;
	}

	private static final List<String> imgTypes = Arrays.asList(new String[]{"jpg", "png", "gif"});

	/**
	 * 动态添加优化参数
	 * 保留.type后字符
	 * @param text
	 * @param imageTypes
	 * @return
	 */
	public static String addQuickLoadParamsBefore(String text, List<String> imageTypes){
		if(StringUtils.isBlank(text)){ return text;}
		if (CollectionUtils.isEmpty(imageTypes)){
			imageTypes = imgTypes;
		}
		String matchType = "";
		String suffix = "";
		boolean isExist = false;
		for (String type : imageTypes){
			String typeWithSepretor = "." + type;
			if (text.contains(typeWithSepretor)){
				isExist = true;
				matchType = "\\." + type;
				suffix = typeWithSepretor;
				break;
			}
		}
		// not exist matched type
		if (!isExist){
			return text;
		}
		//replace .jpg
		String thumb = suffix + "?imageMogr2/thumbnail/750x/quality/80/interlace/1";
		String result = text.replaceAll(matchType,thumb);
		return result;
	}

	/**
	 * 动态添加优化参数
	 * 不保留.type后字符
	 * 七牛加速
	 * 详见http://developer.qiniu.com/docs/v6/api/reference/fop/image/imagemogr2.html
	 * @param text
	 * @return
	 */
	public static String addQuickLoadParams(String text, List<String> imageTypes){
		if(StringUtils.isBlank(text)){ return text;}
		if (CollectionUtils.isEmpty(imageTypes)){
			imageTypes = imgTypes;
		}
		String matchType = "";
		String suffix = "";
		boolean isExist = false;
		for (String type : imageTypes){
			String typeWithSepretor = "." + type;
			if (text.contains(typeWithSepretor)){
				isExist = true;
				matchType = "\\." + type;
				suffix = typeWithSepretor;
				break;
			}
		}
		// not exist matched type
		if (!isExist){
			return text;
		}
		//replace .jpg
		String thumb = suffix + "?imageMogr2/thumbnail/750x/quality/80/interlace/1";
		String fullUrl = new StringBuilder(StringUtils.substringBeforeLast(text, suffix)).append(suffix).toString();
		String result = fullUrl.replaceAll(matchType,thumb);
		return result;
	}

	/**静态添加优化参数，只支持JPG，且保留.jpg后所有字符，可能导致错误；
	 * 要求.jpg后没有任何字符
	 * 保留
	 * @param text
	 * @return
	 */
	@Deprecated
	public static String addQuickLoadParams(String text){
		//replace .jpg
		String thumb = ".jpg?imageMogr2/thumbnail/750x/quality/80/interlace/1";
		String result = text.replaceAll("\\.jpg",thumb);
		return result;
	}



	private static final List<String> vedioTypes = Arrays.asList(new String[]{"mp4", "avi", "mov", "rmvb"});

	/**
	 * 根据指定类型动态去除.type后所有字符
	 * @param text
	 * @param types
	 * @return
	 */
	public static String removeParamsInUrl(String text, List<String> types){
		if(StringUtils.isBlank(text) || CollectionUtils.isEmpty(types)){ return text;}
		String suffix = "";
		boolean isExist = false;
		for (String type : types){
			String typeWithSepretor = "." + type;
			if (text.contains(typeWithSepretor)){
				isExist = true;
				suffix = typeWithSepretor;
				break;
			}
		}
		// not exist matched type
		if (!isExist){
			return text;
		}
		return new StringBuilder(StringUtils.substringBeforeLast(text, suffix)).append(suffix).toString();
	}
	
	public static void main(String[] args) {
		String br = "<br />" ;
		String reg = "(?i)\\<br\\s*\\/\\s*\\>";
		boolean brReg = br.matches(reg);
		System.out.println("brReg is : " + brReg);
		String info = "<p>\n" +
				"\tSIXVISION机械的生命卡通短袖TEE ，一个机械与生命的结合，畸形的状态，但人们似乎不以为然 ， SIXVISION机械的生命卡通短袖TEE全棉设计，上身穿着舒适，非常适合春夏！与众不同！<br />\n" +
				"\t<br />\n" +
				"\t<br />\n" +
				"\t<img src=\"http://img03.static.yohobuy.com/thumb/2011/06/23/04/02f761cafb1cdf0280c5b6774a095314cf-0750x1500-1-goodsimg.jpg\" /></p>\n" +
				"<p>\n" +
				"\t<img src=\"http://img03.static.yohobuy.com/thumb/2011/06/23/04/02561cf2ef38098325e757dd5fbe5769a3-0750x1500-1-goodsimg.jpg\" /><br />\n" +
				"\t<br />\n" +
				"\t<br />\n" +
				"\t<img src=\"http://img04.static.yohobuy.com/thumb/2011/06/23/04/02425c899faee8c5b183e5678b0720874e-0750x1500-1-goodsimg.jpg\" /><br />\n" +
				"\t<br />\n" +
				"\t<br />\n" +
				"\t<img src=\"http://img04.static.yohobuy.com/thumb/2011/06/23/04/020338b404688a8d447c42a64a2e125339-0750x1500-1-goodsimg.jpg\" /><br />\n" +
				"\t<br />\n" +
				"\t<br />\n" +
				"\t<img src=\"http://img03.static.yohobuy.com/thumb/2011/06/23/04/026761c7702d36eb8bbcc28539b3b92c41-0750x1500-1-goodsimg.jpg\" /><br />\n" +
				"\t<br />\n" +
				"\t<br />\n" +
				"\t<br />\n" +
				"\t<br />\n" +
				"\t<br />\n" +
				"\t<img src=\"http://img04.static.yohobuy.com/thumb/2011/06/23/04/02f093eade1383f1dd003fa61eeb25f1f0-0750x1500-1-goodsimg.jpg\" /><br />\n" +
				"\t<br />\n" +
				"\t&nbsp;</p>";

		System.out.println(fixHtml(info));

		String pngUrl = "http://img12.static.yhbimg.com/goodsimg/2016/03/09/13/02b669dd12917294dea72f1eaf88c13860.png?imageMogr2/thumbnail/{width}x{height}/extent/{width}x{height}/background/d2hpdGU=/position/center/quality/80";
		System.out.println("pngUrl is " + addQuickLoadParams(pngUrl, Arrays.asList(new String[]{"jpg", "png", "gif"})));

	}
}
