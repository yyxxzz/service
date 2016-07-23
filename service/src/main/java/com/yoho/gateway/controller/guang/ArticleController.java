package com.yoho.gateway.controller.guang;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.ArticleReqVO;
import com.yoho.service.model.sns.SnsServices;
import com.yoho.service.model.sns.request.ArticleReqBO;
import com.yoho.service.model.sns.response.ArticleBrowseRspBO;
import com.yoho.service.model.sns.response.ArticleNoticeRspBO;
import com.yoho.service.model.sns.response.ArticleRspBO;
import com.yoho.service.model.sns.response.TagsViewsRspBO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import java.util.List;

@Controller
@RequestMapping(value = "/guang/api/*/")
public class ArticleController {

	private static Logger logger = LoggerFactory.getLogger(ArticleController.class);

	// 获取指定时间内发布文章数
//	private static final String ARTICLE_NOTICLE_SERVICE = "sns.getArticleNotice";

	// 获取文章内容服务
//	private static final String ARTICLE_GET_SERVICE = "sns.getArticle";

	// 获取文章内容服务
//	private static final String ARTICLE_GETBASEINFO_SERVICE = "sns.getArticleBaseInfo";

	// 获取48小时内浏览最多的文章
//	private static final String ARTICLE_VIEWSNUM_SERVICE = "sns.getArticleByViewsNum";

	// 获取48小时内浏览最多的文章
//	private static final String ARTICLE_TAGTOP_SERVICE = "sns.getTagTop";

	// 获取资讯总数的code和message
	private static final int ARTICLE_NOTICLE_CODE = 200;
	private static final String ARTICLE_NOTICLE_MSG = "更新数量";

	// 参数异常
	private static final int PARAM_NOT_REASONABLE_CODE = 402;
	private static final String PARAM_NOT_REASONABLE_MSG = "参数不合理";

	@Resource
	ServiceCaller serviceCaller;

	/**
	 * 获取指定时间内发布文章数
	 *
	 * @param ArticleReqVO
	 *            资讯请求信息
	 * @return ApiResponse 返回信息
	 */
	@RequestMapping(value = "/article/getArticleNotice")
	@ResponseBody
	public ApiResponse getArticleNotice(ArticleReqVO vo) throws GatewayException {
		logger.debug("Begin call ArticleController.getArticleNotice gateway. with param ArticleReqVO is {}", vo);

		ArticleReqBO bo = new ArticleReqBO();
		BeanUtils.copyProperties(vo, bo);
		if (StringUtils.isNotEmpty(vo.getDatetime())) {
			if (-1 == vo.getDatetime().indexOf(".")) {
				bo.setDateTime(new Integer(vo.getDatetime()));
			} else {
				bo.setDateTime(new Integer(vo.getDatetime().substring(0, vo.getDatetime().indexOf("."))));
			}
		}
		ArticleNoticeRspBO result = serviceCaller.call(SnsServices.getArticleNotice, bo, ArticleNoticeRspBO.class);
		logger.debug("call sns.getArticleNotice with param is {}, with result is {}", vo, result);
		return new ApiResponse.ApiResponseBuilder().code(ARTICLE_NOTICLE_CODE).message(ARTICLE_NOTICLE_MSG).data(result).build();
	}

	/**
	 * 获取文章内容
	 */
	@RequestMapping(value = "/share/guang")
	@ResponseBody
	public ApiResponse getArticle(@RequestParam(value = "id", required = false) Integer id) throws GatewayException {
		logger.debug("Enter ArticleController.getArticle. param id is {}", id);
		// (1)判断请求参数是否合法
		if (null == id || 0 == id) {
			throw new GatewayException(PARAM_NOT_REASONABLE_CODE, PARAM_NOT_REASONABLE_MSG);
		}

		// (2)组装请求参数, 发送获取文章信息请求
		ArticleReqBO articleReqBO = new ArticleReqBO();
		articleReqBO.setId(id);
		ArticleRspBO articleRspBO = serviceCaller.call(SnsServices.getArticle, articleReqBO, ArticleRspBO.class);

		// (3)组装返回json
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("title", articleRspBO.getArticleTitle());
		jsonObject.put("content", articleRspBO.getArticleTitle());
		jsonObject.put("pic", articleRspBO.getImageUrl());
		jsonObject.put("url", articleRspBO.getUrl());
		jsonObject.put("praise_num", articleRspBO.getPraise());

		return new ApiResponse.ApiResponseBuilder().code(200).message("文章分享").data(jsonObject).build();
	}

	/**
	 * 获取文章内容详情
	 */
	@RequestMapping(value = "/article/getArticleBaseInfo")
	@ResponseBody
	public ApiResponse getArticleBaseInfo(int id, @RequestParam(required = false) String uid, @RequestParam(required = false) String udid) throws GatewayException {
		logger.debug("Enter ArticleController.getArticleBaseInfo. param id is {}", id);

		// (1)组装请求参数
		ArticleReqBO articleReqBO = new ArticleReqBO();
		articleReqBO.setId(id);
		if (StringUtils.isNotEmpty(uid)) {
			articleReqBO.setUid(Integer.parseInt(uid));
		}
		articleReqBO.setUdid(udid);

		// (2)请求
		ArticleRspBO articleRspBO = serviceCaller.call(SnsServices.getArticleBaseInfo, articleReqBO, ArticleRspBO.class);

		// (3)组装返回json
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", articleRspBO.getId());
		jsonObject.put("title", articleRspBO.getArticleTitle());
		jsonObject.put("intro", articleRspBO.getArticleSummary());
		jsonObject.put("src", articleRspBO.getCoverImage());
		jsonObject.put("praise_num", articleRspBO.getPraise());
		jsonObject.put("view_num", articleRspBO.getBrowse());
		jsonObject.put("category_id", articleRspBO.getMaxSortId());
		jsonObject.put("publish_time", StringUtils.isEmpty(articleRspBO.getPublishTime()) ? articleRspBO.getCreateTime() : articleRspBO.getPublishTime());
		jsonObject.put("isFavor", articleRspBO.getIsFavor());
		jsonObject.put("is_recommended", articleRspBO.getIsRecommend());
		jsonObject.put("isPraise", articleRspBO.getIsPraise());

		return new ApiResponse.ApiResponseBuilder().code(200).message("文章分享").data(jsonObject).build();
	}

	/**
	 *
	 * 功能描述: <br>
	 * 获取48小时内浏览最多的文章 author ：zhouxiang
	 *
	 * @param page
	 * @param limit
	 * @param gender
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(value = "/article/getArticleByViewsNum")
	@ResponseBody
	@Cachable(expire = 300)
	public ApiResponse getArticleByViewsNum(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "3") int limit, String gender, String client_type) throws GatewayException {
		logger.debug("Begin call ArticleController.getArticleByViewsNum gateway. with param page is {},limit is {},gender is {}", page, limit, gender);
		ArticleReqBO articleReqBO = new ArticleReqBO();
		articleReqBO.setPage(page);
		articleReqBO.setLimit(limit);
		articleReqBO.setGender(gender);
		articleReqBO.setClient_type(client_type);
		List<ArticleBrowseRspBO> articleBrowseRspBOs = serviceCaller.call(SnsServices.getArticleByViewsNum, articleReqBO, List.class);
		return new ApiResponse.ApiResponseBuilder().code(ARTICLE_NOTICLE_CODE).message("获取48小时内浏览最多的文章成功！").data(articleBrowseRspBOs).build();
	}

	/**
	 *
	 * 功能描述: <br>
	 * 获取逛的热门标签 author ：zhouxiang
	 *
	 * @param page
	 * @param limit
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(value = "/article/getTagTop")
	@ResponseBody
	public ApiResponse getTagTop(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit) throws GatewayException {
		logger.debug("Begin call ArticleController.getTagTop gateway. with param page is {},limit is {}", page, limit);
		ArticleReqBO articleReqBO = new ArticleReqBO();
		articleReqBO.setPage(page);
		articleReqBO.setLimit(limit);
		List<TagsViewsRspBO> tagsViewsRspBOs = serviceCaller.call(SnsServices.getTagTop, articleReqBO, List.class);
		return new ApiResponse.ApiResponseBuilder().code(ARTICLE_NOTICLE_CODE).message("获取逛的热门标签成功！").data(tagsViewsRspBOs).build();
	}

}
