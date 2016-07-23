package com.yoho.gateway.controller.guang;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.service.model.sns.SnsServices;
import com.yoho.service.model.sns.request.ArticleByBrandReqBO;
import com.yoho.service.model.sns.response.ArticleByBrandListRspBO;
import com.yoho.service.model.sns.response.ArticleByBrandRspBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/guang/service/*/article")
public class ArticleBrandController {

	private static Logger logger = LoggerFactory.getLogger(ArticleController.class);

	@Resource
	private ServiceCaller serviceCaller;

//	private static final String ARTICLE_BY_BRAND_URL = "sns.getArticleByBrand";

	/**
	 * 获取品牌的文章, 缓存时间5分钟
	 *
	 * @param brand_id
	 * @param udid
	 * @param client_type
	 * @param limit
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(value = "/getArticleByBrand")
	@ResponseBody
	@Cachable(expire = 10)
	public ApiResponse getArticleByBrand(@RequestParam(defaultValue = "0") int brand_id, @RequestParam(defaultValue = "") String udid, @RequestParam(defaultValue = "") String uid,
			@RequestParam(defaultValue = "h5") String client_type, @RequestParam(defaultValue = "10") int limit) throws GatewayException {
		logger.debug("Begin call ArticleBrandController.getArticleByBrand gateway. with param brand_id is {},udid is {},client_type is{},limit is{} ", brand_id, udid, client_type,
				limit);
		ArticleByBrandReqBO reqBO = new ArticleByBrandReqBO();
		reqBO.setBrandId(brand_id);
		reqBO.setUid(uid);
		reqBO.setUdid(udid);
		reqBO.setClientType(client_type);
		reqBO.setLimit(limit);
		ArticleByBrandListRspBO serviceResponse = serviceCaller.call(SnsServices.getArticleByBrand, reqBO, ArticleByBrandListRspBO.class);
		logger.debug("call sns.getArticleByBrand with param is {}, with result is {}", reqBO, serviceResponse);

		JSONArray response = this.genResponse(serviceResponse);
		return new ApiResponse.ApiResponseBuilder().code(200).message("get article by brand success").data(response).build();
	}

	private JSONArray genResponse(ArticleByBrandListRspBO response) {
		if (response == null) {
			return new JSONArray();
		}
		List<ArticleByBrandRspBO> articles = response.getArticles();
		if (articles == null || articles.isEmpty()) {
			return new JSONArray();
		}
		JSONArray result = new JSONArray();
		for (ArticleByBrandRspBO articleRspBO : articles) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", String.valueOf(articleRspBO.getArticleId()));
			jsonObject.put("src", articleRspBO.getCoverImage());
			jsonObject.put("cover_image_type", String.valueOf(articleRspBO.getCoverImageType()));
			jsonObject.put("title", articleRspBO.getArticleTitle());
			jsonObject.put("publish_time", articleRspBO.getPublishTime());
			jsonObject.put("url", articleRspBO.getArticleUrl());
			jsonObject.put("views_num",String.valueOf( articleRspBO.getBrowseNum()));

			Map<String, Object> like = new HashMap<String, Object>();
			like.put("isLiked", articleRspBO.isPraise());
			like.put("count", String.valueOf(articleRspBO.getPraiseNum()));
			jsonObject.put("like", like);

			jsonObject.put("praise_num", String.valueOf(articleRspBO.getPraiseNum()));
			jsonObject.put("isFavor", articleRspBO.getIsFav());
			jsonObject.put("intro", articleRspBO.getArticleSummary());
			jsonObject.put("share_url", articleRspBO.getShareUrl());

			result.add(jsonObject);
		}
		return result;
	}
}
