package com.yoho.gateway.controller.product;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.product.FashioncodeConfig;
import com.yoho.gateway.model.product.FashioncodeConfigVo;

/**
 * 潮流密码配置
 * @author yoho
 *
 */
@Controller
public class FashioncodeConfigController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsultController.class);
	
	//请求成功
	private final static int REQUEST_SUCCESS_CODE = 200;
	
	// 商品的分析潮流密码配置
	@Value("${fashioncode.product}")
	private String productFashioncode;
	
	// 品牌的分析潮流密码配置
	@Value("${fashioncode.brand}")
	private String brandFashioncode;
	
	@RequestMapping(params = "method=app.config.fashioncodeTemplate")
	@ResponseBody
	public ApiResponse getConsultList() throws GatewayException {
		LOGGER.info("Begin call getConsultList gateway.");
		
		FashioncodeConfigVo result = new FashioncodeConfigVo();
		result.setList(getConfigList());
		
		LOGGER.info("call getConsultList gateway out.");
		
		return new ApiResponse.ApiResponseBuilder().code(REQUEST_SUCCESS_CODE).message("fashioncode template").data(result).build();
	}
	
	//复制这条消息,打开Yoho!Buy有货客户端,即可查看商品【StussNo.4 Box Tee DC SPAR HIGH...】 http://m.new.yohobuy.com/   潮流密码 YINGDOEN
	private List<FashioncodeConfig> getConfigList() {
		List<FashioncodeConfig> list = new ArrayList<FashioncodeConfig>();
		list.add(new FashioncodeConfig("product", "潮流密码", productFashioncode+"{name}{url}{fashion_code}"));
		list.add(new FashioncodeConfig("brand", "潮流密码", brandFashioncode+"{name}{url}{fashion_code}"));
		return list;
	}
}
