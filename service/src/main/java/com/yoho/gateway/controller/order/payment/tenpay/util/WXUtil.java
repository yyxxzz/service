package com.yoho.gateway.controller.order.payment.tenpay.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class WXUtil {
	
	public static String getNonceStr() {
		Random random = new Random();
		return MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "GBK");
	}

	public static String getTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}
	
	/**
	 * 生成微信支付应答或请求的xml（新版本API）
	 * @param paramMaps 参数对
	 * @return String 
	 */	
	public static String createWXPayXml(Map<String, String> paramMaps)
	{
		String prePayBody = "";		
		Element root = new Element("xml");  
		Document doc = new Document(root); 
		
		for(Map.Entry<String, String> entry : paramMaps.entrySet()){
			root.addContent(new Element(entry.getKey()).setText(entry.getValue()));
		}

		XMLOutputter xmlOut = new XMLOutputter();  
		prePayBody = xmlOut.outputString(doc);  

		return prePayBody;
	}	
	
	/**
	 * 微信支付请求应答XML解析（新版本API）
	 * @param String 请求或者应答的xml
	 * @return Map 
	 */		
	public static Map<String, String> parseWXPayXml(String responseXml)
	{
		if(responseXml == null)
			return null;
		
		Map<String, String> paramMaps = new HashMap<String, String>();

		try {
			SAXBuilder sax = new SAXBuilder();
			
			InputStream inputStream = new ByteArrayInputStream(responseXml.getBytes(ConstantUtil.WeixinPayConstants.DEFAULT_CHARACTER_SET));
			Document doc = sax.build(inputStream);

			Element root = doc.getRootElement();
			List nodeList = root.getChildren();  
			for(int i = 0; i < nodeList.size(); i++){
				Element node = (Element)nodeList.get(i);
				paramMaps.put(node.getName(), node.getValue());
			}
			
		} catch (Exception e) {
			//loggerErr.error("parse wexin notify response failed: {}, error", responseXml, e);
			return null;
		}
		
		return paramMaps;
	}	
	
	/**
	 * 生成Md5签名（新版本API）
	 * @param Map<String, String> 参数对
	 * @param String 商户私钥
	 * @return String 
	 */	
	public static String signMd5(Map<String, String> paramMap, String privateKey){
		if(null == paramMap){
			return "";
		}
		//使用SortedMap将参数按名称的字典序排队
		SortedMap<String, String> sortMap = new TreeMap<String, String>();
		for(Map.Entry<String, String> entry : paramMap.entrySet()){
			if(ConstantUtil.WeixinPayConstants.SIGN.equals(entry.getKey()))
			{//sign本身不加入签名
				continue;
			}			
			if(StringUtils.isEmpty(entry.getValue()))
			{//值为空的不加入签名
				continue;
			}			
			sortMap.put(entry.getKey(), entry.getValue());
		}
		
		StringBuilder strBuilder = new StringBuilder();
		for(Map.Entry<String, String> entry : sortMap.entrySet()){
			strBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");			
		}
		//字典排序后再拼接上商户密钥
		strBuilder.append(ConstantUtil.WeixinPayConstants.KEY).append("=").append(privateKey);
		
		String md5Sign = MD5Util.MD5Encode(strBuilder.toString(), ConstantUtil.WeixinPayConstants.DEFAULT_CHARACTER_SET).toUpperCase();

		return md5Sign;
	}	
	
	/**
	 * 验证Md5签名（新版本API）
	 * @param Map<String, String> 参数对
	 * @param String 商户私钥
	 * @return String 
	 */	
	public static boolean validSign(Map<String, String> paramMap, String privateKey){
		if(paramMap == null)
			return false;
		
		return signMd5(paramMap, privateKey).equals(paramMap.get(ConstantUtil.WeixinPayConstants.SIGN));
	}
	
	/**
	 * 微信支付从Notify请求中获取Body数据（新版本API）
	 * @param HttpServletRequest 请求
	 * @return Map 
	 */			
	public static String getWXPayRequestBody(HttpServletRequest request) throws Exception{
		BufferedReader br = request.getReader();
		StringBuilder strBuiler = new StringBuilder();
		String strTemp;
		while((strTemp = br.readLine()) != null){
			strBuiler.append(strTemp);
		}
		return strBuiler.toString();
	}
	
	/****

	public static void main(String[] args){
		String reqXml = "<xml>"
						+ "<appid>wx2421b1c4370ec43b</appid>"
						+ "<attach>支付测试</attach>"
						+ "<body>APP支付测试</body>"
						+ "<mch_id>10000100</mch_id>"
						+ "<nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>"
						+ "<notify_url>http://wxpay.weixin.qq.com/pub_v2/pay/notify.v2.php</notify_url>"
						+ "<out_trade_no>1415659990</out_trade_no>"
						+ "<spbill_create_ip>14.23.150.211</spbill_create_ip>"
						+ "<total_fee>1</total_fee>"
						+ "<trade_type>APP</trade_type>"
						+ "<sign>87518E97AE2E5664A2CB98CEFA56682C</sign>"
						+ "</xml>";
		System.out.println("========" + reqXml + "\n");
		
		Map<String, String> params = parseWXPayXml(reqXml);
		System.out.println("========sign: " + signMd5(params, ConstantUtil.PARTNER_KEY));
		
		System.out.println("========valid sign: " + validSign(params, ConstantUtil.PARTNER_KEY));
		
		System.out.println("========");
		for(Map.Entry<String, String> entry : params.entrySet()){
			System.out.println(entry.getKey() + "=" + entry.getValue());
		}
		
		System.out.println("********");
		System.out.println(createWXPayXml(params));
	}
	 ****/
}
