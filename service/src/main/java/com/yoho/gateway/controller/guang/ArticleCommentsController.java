package com.yoho.gateway.controller.guang;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.service.model.response.CommonRspBO;
import com.yoho.service.model.sns.SnsServices;
import com.yoho.service.model.sns.request.ArticleCommentsReqBO;
import com.yoho.service.model.sns.response.ArticleCommentsPageRspBO;
import com.yoho.service.model.sns.response.ArticleCommentsRspBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/guang/api/*/comments")
public class ArticleCommentsController {

	private static Logger logger = LoggerFactory.getLogger(ArticleCommentsController.class);

	// 获取文章评论列表服务
//	private static final String ARTICLECOMMENTS_GETLIST_SERVICE = "sns.getArticleCommentsList";

	// 获取文章内容服务
//	private static final String ARTICLECOMMENTS_ADD_SERVICE = "sns.addArticleComments";

	@Resource
	ServiceCaller serviceCaller;


	/**
	 * 获取文章评论列表
	 */
	@RequestMapping(value = "/getList")
	@ResponseBody
	public ApiResponse getArticleCommentsList(@RequestParam(value = "article_id") int article_id, @RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "limit", required = false, defaultValue = "10") int limit) throws GatewayException {
		logger.debug("Enter ArticleCommentsController.getArticleCommentsList. param article_id is {}, page is {}, limit is {}", article_id, page, limit);

		// (1)组装求情参数
		ArticleCommentsReqBO articleCommentsReqBO = new ArticleCommentsReqBO();
		articleCommentsReqBO.setArticleId(article_id);
		articleCommentsReqBO.setPage(page);
		articleCommentsReqBO.setLimit(limit);

		// (2)请求
		ArticleCommentsPageRspBO articleCommentsPageRspBO = serviceCaller.call(SnsServices.getArticleCommentsList, articleCommentsReqBO, ArticleCommentsPageRspBO.class);

		// (3)组装返回json
		JSONArray jsonArray = new JSONArray();
		for (ArticleCommentsRspBO articleCommentsRspBO : articleCommentsPageRspBO.getArticleCommentsRspBOs()) {
			if (null == articleCommentsRspBO) {
				continue;
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", articleCommentsRspBO.getId());
			jsonObject.put("article_id", articleCommentsRspBO.getArticleId());
			jsonObject.put("username", articleCommentsRspBO.getUsername());
			jsonObject.put("avator", articleCommentsRspBO.getHeadIco());
			jsonObject.put("create_time", articleCommentsRspBO.getCreateTime());
			jsonObject.put("content", articleCommentsRspBO.getContent());
			jsonArray.add(jsonObject);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("page", articleCommentsPageRspBO.getPage());
		jsonObject.put("total", articleCommentsPageRspBO.getTotal());
		jsonObject.put("total_page", articleCommentsPageRspBO.getTotalPage());
		jsonObject.put("list", jsonArray);

		return new ApiResponse.ApiResponseBuilder().code(200).message("Comment List!").data(jsonObject).build();
	}


	/**
	 * 增加文章评论
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	public ApiResponse addArticleComments(int article_id, int uid, String content) throws GatewayException {
		logger.debug("Enter ArticleCommentsController.addArticleComments. param article_id is {}, uid is {}, content is {}", article_id, uid, content);

		// (1)组装求情参数
		ArticleCommentsReqBO articleCommentsReqBO = new ArticleCommentsReqBO();
		articleCommentsReqBO.setArticleId(article_id);
		articleCommentsReqBO.setUid(uid);
		articleCommentsReqBO.setContent(content);

		// (2)请求
		serviceCaller.call(SnsServices.addArticleComments, articleCommentsReqBO, CommonRspBO.class);

		return new ApiResponse.ApiResponseBuilder().code(200).message("发表评论成功").data(new JSONObject()).build();
	}

}
