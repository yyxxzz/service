package com.yoho.gateway.controller.guang;

import com.yoho.service.model.sns.SnsServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.sns.request.ArticleAuthorReqBO;
import com.yoho.service.model.sns.response.ArticleAuthorRspBO;

/**
 * 获取文章作者信息
 *
 * Created by hugufei on 2016/3/9.
 */
@Controller
@RequestMapping("/guang/service")
public class ArticleAuthorController {

	private Logger logger = LoggerFactory.getLogger(ArticleAuthorController.class);

//	private String ARTICLE_AUTHOR_URL = "sns.getArticeAuthor";

	@Autowired
	private ServiceCaller serviceCaller;

	/**
	 * 根据作者id获取作者信息
	 */
	@RequestMapping("/*/author/getAuthor")
	@ResponseBody
	public ApiResponse getAuthor(@RequestParam(defaultValue="") String author_id, @RequestParam(defaultValue="h5") String client_type) {
		ArticleAuthorReqBO articleAuthorReqBO = new ArticleAuthorReqBO(author_id,client_type);
		ArticleAuthorRspBO articleAuthorRspBO = serviceCaller.call(SnsServices.getArticeAuthor, articleAuthorReqBO, ArticleAuthorRspBO.class);
		logger.debug("Leave getAuthor. param  author_id is {}, client_type is {},articleAuthorRspBO is {}", author_id, client_type, articleAuthorRspBO);
		return new ApiResponse.ApiResponseBuilder().code(200).message("author info").data(articleAuthorRspBO).build();
	}

}
