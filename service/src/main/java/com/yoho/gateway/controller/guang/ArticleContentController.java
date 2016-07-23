package com.yoho.gateway.controller.guang;

import java.util.List;

import javax.annotation.Resource;

import com.yoho.service.model.sns.SnsServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.sns.ArticleContentReqVO;
import com.yoho.service.model.sns.SnsServices;
import com.yoho.service.model.sns.request.ArticleContentReqBO;
import com.yoho.service.model.sns.response.ArticleBlockRspBO;
import com.yoho.service.model.sns.response.ArticleContentRspBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 内容： 根据文章id和客户端类型获得逛得详情内容,并判断用户是否收藏逛的文章
 * 
 * 场景：进入guang,点击列表，进入具体列表区域点击文章获取详情时触发
 * 
 */

/**
 * 内容： 根据文章id和客户端类型获得逛得详情内容,并判断用户是否收藏逛的文章
 *
 * 场景：进入guang,点击列表，进入具体列表区域点击文章获取详情时触发
 *
 */

@Controller
@RequestMapping(value = "/guang/service/*/article")
public class ArticleContentController {

	private static Logger logger = LoggerFactory.getLogger(ArticleController.class);

	@Resource
	private ServiceCaller serviceCaller;

//	private static final String  ARTICLE_CONTENT_URL = "sns.getArticleContent";
	
//	private static final String  CHECKARTICLEFAV_URL = "sns.checkArticleFav";
//	private static final String ARTICLE_CONTENT_URL = "sns.getArticleContent";

//	private static final String CHECKARTICLEFAV_URL = "sns.checkArticleFav";

	/**
	 * 缓存5秒
	 * 功能描述：根据文章id和客户端类型获取逛的详情内容；场景：进入guang,点击列表，进入具体列表区域点击文章获取详情时触发
	 * @param article_id
	 * @param client_type
	 * @return 
	 * @throws GatewayException
	 */
	@RequestMapping(value = "/getArticleContent")
	@ResponseBody
	@Cachable(expire = 5)
	public ApiResponse getArticleContent(@RequestParam(defaultValue = "0") String article_id, @RequestParam(defaultValue = "h5") String client_type) throws GatewayException {
		logger.debug("Begin call ArticleContentController.getArticleContent gateway. with param article_id is {},client_type is {}", article_id, client_type);
		ArticleContentReqBO reqBO = new ArticleContentReqBO();
		reqBO.setArticleId(article_id);
		reqBO.setClientType(client_type);
		ArticleContentRspBO result = serviceCaller.call(SnsServices.getArticleContent, reqBO, ArticleContentRspBO.class);
		//ArticleContentRspBO result = serviceCaller.call("sns.getArticleContent", reqBO, ArticleContentRspBO.class);
		logger.debug("call guang.getArticleContent with param is {}, with result is {}", reqBO, result);
		JSONArray response = this.genResponse(result);
		return new ApiResponse.ApiResponseBuilder().code(200).message("get article content success").data(response).build();
	}

	private JSONArray genResponse(ArticleContentRspBO response) {
		if (response == null) {
			return new JSONArray();
		}
		List<ArticleBlockRspBO> articleBlocks = response.getBlocks();
		if (articleBlocks == null || articleBlocks.isEmpty()) {
			return new JSONArray();
		}
		JSONArray result = new JSONArray();
		for (ArticleBlockRspBO articleBlock : articleBlocks) {
			JSONObject contentData = articleBlock.getContentData();
			if (contentData == null || contentData.isEmpty()) {
				continue;
			}
			JSONObject json = new JSONObject();
			json.put(articleBlock.getTemplateKey(), contentData);
			result.add(json);
		}
		return result;
	}

	// 判断用户是否收藏逛的文章
	/**
	 * 功能描述：根据用户id和文章id判断用户是否收藏逛的文章
	 */
	@RequestMapping(value = "/checkArticleFav")
	@ResponseBody
	public ApiResponse checkArticleFav(ArticleContentReqVO vo) throws GatewayException {
		logger.debug("Begin call ArticleContentController.checkArticleFav gateway. with param ", vo);

		ArticleContentReqBO bo = new ArticleContentReqBO();
		bo.setArticleId(vo.getArticle_id());
		bo.setUid(vo.getUid());
		boolean result = serviceCaller.call(SnsServices.checkArticleFav, bo , boolean.class);

		return new ApiResponse.ApiResponseBuilder().data(result).build();
	}
}
