/**
 *
 */
package com.yoho.gateway.controller.activity;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.service.model.user.other.response.GetFloatResponseBO;

/**
 * 描述：
 *
 * @author ping.huang
 * 2016年4月6日
 */
@Controller
public class ADVController {

	static Logger log = LoggerFactory.getLogger(ADVController.class);

	@Autowired
	private ServiceCaller serviceCaller;

	@RequestMapping(params = "method=app.cover.getfloat")
	@ResponseBody
	public ApiResponse getfloat(@RequestParam(value = "app_version", required = false, defaultValue = "4.0.0") String app_version,
								@RequestParam(value = "client_type", required = false, defaultValue = "H5") String clientType) throws GatewayException {
		log.info("enter getfloat");

		if(app_version.compareTo("4.0.0")<0 && "iphone".equals(clientType.toLowerCase())){
			log.info("app.cover.getfloat,app_version is under 4.0.0");
			String retJSONdata="{\"template_id\":6426,\"template_intro\":\"一张图片\",\"template_name\":\"single_image\",\"ad_action\":\"go.url\",\"act_title\":\"升级提示\",\"ad_images\":\"http://cdn.yoho.cn/yohobuy-tip-686_1234.png\",\"islogin\":\"N\",\"ad_option\":\"link=http://a.app.qq.com/o/simple.jsp?pkgname=com.yoho\"}";
			return new ApiResponse.ApiResponseBuilder().data(JSON.parse(retJSONdata)).build();
		}
		GetFloatResponseBO resp = serviceCaller.call("users.getfloat", null, GetFloatResponseBO.class);
		log.info("getfloat success");
		return new ApiResponse.ApiResponseBuilder().data(resp).build();
	}
}
