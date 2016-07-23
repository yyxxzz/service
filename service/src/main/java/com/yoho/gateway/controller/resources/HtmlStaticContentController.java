package com.yoho.gateway.controller.resources;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.resource.HtmlContentBO;
import com.yoho.service.model.resource.request.HtmlContentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * qianjun 2016/3/17
 */
@Controller
public class HtmlStaticContentController {

    @Autowired
    private ServiceCaller serviceCaller;

    /**
     * 根据节点和运行环境选择静态内容
     */
    @RequestMapping(params = "method=app.resources.getHtmlContentByNodeContent")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.HTML_STATIC_CONTENT)
    public ApiResponse getHtmlContentByNodeContent(@RequestParam(value = "node") String node, @RequestParam(value = "mode", required = false, defaultValue = "release") String mode) {
        HtmlContentRequest htmlContentRequest = new HtmlContentRequest();
        htmlContentRequest.setNode(node);
        htmlContentRequest.setMode(mode);
        HtmlContentBO htmlContentBO = serviceCaller.call("resources.getNodeContent", htmlContentRequest, HtmlContentBO.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("html content").data(htmlContentBO).build();
    }

}
