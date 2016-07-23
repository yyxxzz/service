package com.yoho.gateway.controller.guang;

import com.yoho.service.model.sns.SnsServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.sns.request.ArticleReqBO;
import com.yoho.service.model.sns.response.ArticleRspBO;

/**
 * 内容： 根据文章id和客户端类型获得文章,并增加浏览记录
 * 
 * 场景：进入guang,获得列表。点击某篇文章进入具体的文章详情时 触发!
 * 
 * @author maelk_liu
 */
@Controller
public class ArticleContentInfoController {

	private Logger logger = LoggerFactory.getLogger(ArticleContentInfoController.class);

	@Autowired
	ServiceCaller serviceCaller;

	/**
	 * 
	 * 功能描述: 根据文章id和客户端类型获得文章,并增加浏览记录 场景：进入guang,获得列表。点击某篇文章进入具体的文章详情时 触发!
	 *
	 * @param article_id
	 * @param client_type
	 * @return ApiResponse
	 */
	@RequestMapping("guang/service/*/article/getArticle")
	@ResponseBody
	public ApiResponse getArticleContentForIdClientType(@RequestParam("article_id") Integer article_id, @RequestParam(value = "client_type", required = false) String client_type) {
		logger.debug("Enter getArticleContentForIdClientType. param article_id is {} and client_type is {}", article_id, client_type);
		ArticleReqBO req = new ArticleReqBO();
		if (null == client_type || "".equals(client_type)) {
			client_type = "h5";
		}
		req.setClient_type(client_type);
		req.setId(article_id);
		ArticleRspBO bo = serviceCaller.call(SnsServices.getArticleContentForIdClientType, req, ArticleRspBO.class);
		logger.debug("Leave getArticleContentForIdClientType. param article_id is {} and client_type is {} and articleTitle is{}", article_id, client_type, bo.getArticleTitle());
		JSONObject a = toJson(bo);
		return new ApiResponse.ApiResponseBuilder().code(200).message("咨询内容").data(a).build();
	}

	private JSONObject toJson(ArticleRspBO bo) {
		JSONObject obj = new JSONObject();
		obj.put("id", bo.getId());
		obj.put("article_title", bo.getArticleTitle());
		obj.put("max_sort_id", bo.getMaxSortId());
		obj.put("min_sort_id", bo.getMinSortId());
		obj.put("author_id", bo.getAuthorId());
		obj.put("cover_image", bo.getCoverImage());
		obj.put("cover_image_type", bo.getCoverImageType());
		obj.put("url", bo.getUrl());
		obj.put("ads_img_size", bo.getAdsImgSize());
		obj.put("article_type", bo.getArticleType());
		obj.put("article_summary", bo.getArticleSummary());
		obj.put("article_gender", bo.getArticleGender());
		obj.put("brand", bo.getBrand());
		obj.put("tag", bo.getTag());
		obj.put("praise", bo.getPraise());
		obj.put("browse", bo.getBrowse());
		obj.put("status", bo.getStatus());
		obj.put("is_recommend", bo.getIsRecommend());
		obj.put("create_time", bo.getCreate_time());
		obj.put("publish_time", bo.getPublish_time());
		obj.put("publish_state", bo.getPublishState());
		obj.put("update_time", bo.getUpdate_time());
		obj.put("publishTime", bo.getPublishString());
		obj.put("pageViews", bo.getPageViews());
		obj.put("tags", bo.getTag_array());
		return obj;
	}

	
	
	
	
}
