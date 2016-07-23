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
import com.yoho.service.model.sns.request.ArticleReqBO;
import com.yoho.service.model.sns.response.BrandRspBo;
import com.yoho.service.model.sns.response.OtherArticleRspBo;

/**
 * module：guang 文章相关的品牌信息以及 文章内容相关的其他文章
 *
 * @author maelk-liu
 */
@Controller
public class ArticleBrandRelatedController {

	private Logger logger = LoggerFactory.getLogger(ArticleBrandRelatedController.class);

	// 获取文章相关品牌信息
//	private final static String GUANG_GETBRAND_SERVICE_URL = "sns.getBrandRelatedByArticle";

	// 获取文章内容相关的其他内容
//	public final static String GUANG_GETOTHERARTICLE_SERVICE_URL = "sns.getOtherArticle";

	@Autowired
	private ServiceCaller serviceCaller;

	// 获取文章相关品牌列表的成功响应的响应码
	private final static int GETBRAND_RELATE_LIST_CODE_SUCCESS = 200;
	private final static String GETBRAND_RELATE_LIST_CODE_SUCCESS_MSG = "文章相关品牌列表";

	// 获取文章内容相关的其他内容的响应码
	private final static int GET_OTHER_ARTICLE_LIST_SUCCESS_CODE = 200;
	private final static String GET_OTHER_ARTICLE_LIST_SUCCESS_MSG = "文章相关内容列表";
    
	/**
	 * 
	 * 功能描述: 文章相关的品牌信息
	 * 场景：进入文章详情页时，用于展示"相关品牌"。
	 * @author maelk-liu
	 * 
	 * @param article_id
	 * @param client_type
	 * @return
	 */
	@RequestMapping("/guang/service/*/article/getBrand")
	@ResponseBody
	public ApiResponse getBrandRelatedByArticle(@RequestParam("article_id") Integer article_id, @RequestParam(value = "client_type", required = false) String client_type) {
		logger.debug("Enter getBrandRelatedByArticle. param article_id is {} and client_type is {}", article_id, client_type);
		ArticleReqBO req = new ArticleReqBO();
		if (null == client_type || "".equals(client_type)) {
			client_type = "h5";
		}
		req.setClient_type(client_type);
		req.setId(article_id);
		BrandRspBo[] list = serviceCaller.call(SnsServices.getBrandRelatedByArticle, req, BrandRspBo[].class);
		logger.debug("Leave getBrandRelatedByArticle. param article_id is {} and client_type is {}", article_id, client_type);
		return new ApiResponse.ApiResponseBuilder().code(GETBRAND_RELATE_LIST_CODE_SUCCESS).message(GETBRAND_RELATE_LIST_CODE_SUCCESS_MSG).data(list).build();
	}
    
	/**
	 * 功能描述: 文章内容的其他推荐内容
     * 场景：进入文章详情页时，用于展示"相关文章"。
     * @author maelk-liu
	 *
	 * @param tags
	 * @param article_id
	 * @param offset
	 * @param limit
	 * @param client_type
	 */
	@RequestMapping("/guang/service/*/article/getOtherArticle")
	@ResponseBody
	public ApiResponse getOtherArticle(@RequestParam("tags") String tags, @RequestParam("article_id") Integer article_id, @RequestParam(value = "offset", required = false) Integer offset,
			@RequestParam(value = "limit", required = false) Integer limit, @RequestParam(value = "client_type") String client_type) {
		logger.debug("Enter getOtherArticle controller. tags is {}, article_id is {}, offset is {}, limit is {}, client_type is {}", tags, article_id, offset, limit, client_type);
		// (1)组装请求参数
		ArticleReqBO articleReqBO = new ArticleReqBO();
		if (null == client_type || "".equals(client_type)) {
			client_type = "h5";
		}
		if (null == offset) {
			offset = 0;
		}
		if (null == limit) {
			limit = 10;
		}
		articleReqBO.setId(article_id);
		articleReqBO.setClient_type(client_type);
		articleReqBO.setLimit(limit);
		articleReqBO.setOffset(offset);
		articleReqBO.setTag(tags);
		OtherArticleRspBo[] list = serviceCaller.call(SnsServices.getOtherArticle, articleReqBO, OtherArticleRspBo[].class);

		// (2)组装返回
		ApiResponse response = new ApiResponse.ApiResponseBuilder().code(GET_OTHER_ARTICLE_LIST_SUCCESS_CODE).message(GET_OTHER_ARTICLE_LIST_SUCCESS_MSG).data(list).build();

		logger.debug("leave getOtherArticle controller. tags is {}, article_id is {}, offset is {}, limit is {}, client_type is {}", tags, article_id, offset, limit, client_type);
		return response;
	}

}
