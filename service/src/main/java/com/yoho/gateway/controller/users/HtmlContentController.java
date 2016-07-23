package com.yoho.gateway.controller.users;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.user.help.HtmlContentReqVO;
import com.yoho.service.model.request.HtmlContentDetailReqBO;
import com.yoho.service.model.request.HtmlContentReqBO;
import com.yoho.service.model.response.HtmlContentDetailRspBO;
import com.yoho.service.model.response.HtmlContentRepBO;

@Controller
public class HtmlContentController {

    @Resource
    ServiceCaller serviceCaller;

    private static final int HELP_HELPCENTER_CODE = 200;
    private static final String HELP_HELPHELPCENTER_MSG = "帮助列表";

    private Logger logger = LoggerFactory.getLogger(HtmlContentController.class);
    /**
     * 帮助中心
     */
    @RequestMapping(params = "method=app.help.li")
    @ResponseBody
    public ApiResponse helpCenter()throws GatewayException{

        logger.info("enter helpCenter");

        HtmlContentReqBO bo = new HtmlContentReqBO();
        bo.setCategoryId(14);
        //请求服务
        HtmlContentRepBO[] htmlContentRepBO = serviceCaller.call("users.getHtmlContentByCategoryId", bo, HtmlContentRepBO[].class);

        //返回
        return new ApiResponse.ApiResponseBuilder().code(HELP_HELPCENTER_CODE).message(HELP_HELPHELPCENTER_MSG).data(htmlContentRepBO).build();
    }

	/**
	 * 根据nodecode，获取具体的帮助内容
	 */
	@RequestMapping(params = "method=app.help.detail")
	@ResponseBody
	public String helpContent(HtmlContentReqVO vo)throws GatewayException{
		
		logger.info("enter helpContent param is{}",vo);
		if(StringUtils.isEmpty(vo.getCode())){
			throw new ServiceException(ServiceError.HELP_ID_ERROR);
		}
		HtmlContentDetailReqBO bo = new HtmlContentDetailReqBO();
		bo.setCode(vo.getCode());
		//请求服务
		HtmlContentDetailRspBO htmlContentDetailRspBo = 
				serviceCaller.call("users.getHtmlContentDetailByNodeCode", bo, HtmlContentDetailRspBO.class);
		
		//返回
		return htmlContentDetailRspBo.getInuseContent();
	}
}
