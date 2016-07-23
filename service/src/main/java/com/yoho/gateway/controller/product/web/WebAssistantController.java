package com.yoho.gateway.controller.product.web;


import com.yoho.gateway.cache.expire.product.ExpireTime;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.service.favorite.FavoriteService;
import com.yoho.product.model.HtmlContentBo;
import com.yoho.product.request.BaseRequest;

/**
 * Created by caoyan
 */
@Controller
public class WebAssistantController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebAssistantController.class);

    private static final String HTML_CONTENT = "html content";
    
    @Autowired
    private ServiceCaller serviceCaller;
    
    @Autowired
	FavoriteService favoriteService;
    
    @RequestMapping(params = "method=web.html.content")
	@ResponseBody
	@Cachable(expire= ExpireTime.web_html_content)
	public ApiResponse getHtmlContent(@RequestParam(value = "node")String node, 
			@RequestParam(value = "mode", defaultValue = "release")String mode) throws ServiceException {
    	LOGGER.info("Begin call web.html.content. with param node is {}, mode is {}", node, mode);
    	
    	if(StringUtils.isEmpty(node)){
    		return new ApiResponse(404, "node is empty", null);
    	}
    	
    	BaseRequest<String> req = new BaseRequest<String>();
    	req.setParam(node);
        HtmlContentBo bo = serviceCaller.call("product.getContentByNodeCode", req, HtmlContentBo.class);
        if(null == bo){
        	return new ApiResponse.ApiResponseBuilder().message(HTML_CONTENT).data(null).build();
        }
        String htmlContent = bo.getEditContent();
        if("release".equals(mode)){
        	htmlContent = bo.getInuseContent();
        }
        
		LOGGER.debug("call product.getContentByNodeCode with param is {}, with result is {}", req, htmlContent);
		
		LOGGER.info("getHtmlContent call success.");
		
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(HTML_CONTENT).data(htmlContent).build();
		return response;
	}
    
}
