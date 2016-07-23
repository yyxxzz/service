package com.yoho.gateway.controller.guang;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.sns.ArticleReqVO;
import com.yoho.service.model.sns.SnsServices;
import com.yoho.service.model.sns.request.ArticleReqBO;
import com.yoho.service.model.sns.request.CategoryReqBO;
import com.yoho.service.model.sns.response.ArticleInfoRspBO;
import com.yoho.service.model.sns.response.CategoryRspBO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 分类下的资讯列表
 *
 * Created by DengXinFei on 2016/3/9.
 */
@Controller
@RequestMapping("/guang/api")
public class ArticleInfoController {

	private Logger logger = LoggerFactory.getLogger(ArticleInfoController.class);

	// 获取文章分类列表的URL
//	private final static String CATEGORY_LOAD_URL = "sns.getList";

	// 获取分类下的资讯列表URL
//	public final static String CATEGORY_ARTICLE_LIST_URL = "sns.getCategory";

	// 获取‘星潮教室’一级分类中，标签开启状态下的文章
//	public final static  String STARCLASSROOM_ARTICLE_LIST_LOAD_URL = "sns.getStarClassroomArticleList" ;

	@Autowired
	private ServiceCaller serviceCaller;

	// 获取分类的成功响应的响应码
	private final static int GET_CATEGORY_LIST_CODE_SUCCESS = 200;
	private final static String GET_CATEGORY_LIST_CODE_SUCCESS_MSG = "分类列表";

	// 获取文章的分类列表的响应码
	private final static int GET_ARTICLE_LIST_SUCCESS_CODE = 200;
	private final static String GET_ARTICLE_LIST_SUCCESS_MSG = "资讯列表";

	// 获取"星潮教室"一级分类中，标签开启状态下的文章的响应码
	private final static int GET_STARCLASSROOM_ARTICLE_LIST_SUCCESS_CODE = 200;
	private final static String GET_STARCLASSROOM_ARTICLE_LIST_SUCCESS_MSG = "星潮教室文章列表";

	/**
	 * 功能描述：获取逛下的分类列表
	 * 场景：点击“逛”后触发
	 * @param client_type
	 * @param gender
	 * @return 分类列表
	 */
	@RequestMapping("/*/category/get")
	@ResponseBody
	public ApiResponse getCategoryList(@RequestParam("client_type") String client_type, @RequestParam(value = "gender", required = false) String gender) {
		logger.debug("Enter getCategoryList. param client_type is {} and gender is {}", client_type, gender);
		// (1)请求获取逛的文章分类的列表
		CategoryReqBO categoryReqBO = new CategoryReqBO(gender, client_type);
		CategoryRspBO[] categoryList = serviceCaller.call(SnsServices.getCategory, categoryReqBO, CategoryRspBO[].class);
		logger.debug("Leave getCategoryList. param client_type is {} and gender is {}, category length is {}", client_type, gender, (null != categoryList) ? categoryList.length : 0);
		return new ApiResponse.ApiResponseBuilder().code(GET_CATEGORY_LIST_CODE_SUCCESS).message(GET_CATEGORY_LIST_CODE_SUCCESS_MSG).data(categoryList).build();
	}

	/**
	 * 功能描述: 获取分类下的文章的列表, 缓存10秒
	 * 场景：点击某一分类查看分类下的文章时触发
	 * @param articleReqVO
	 * @return 分类下的文章列表
	 */
	@RequestMapping("/*/article/getList")
	@ResponseBody
	@Cachable(expire=5, needMD5 = true)
	public ApiResponse getArticleList(ArticleReqVO articleReqVO) {
		logger.info("Enter getArticleList controller. param articleReqVO is {}", articleReqVO);

		// (1)组装请求参数
		int sort_id = 0, author_id = 0, uid = 0, page = 1, limit = 10;
		String gender = "", tag = "", client_type = "h5", udid = "";
		if (null != articleReqVO) {
			sort_id = StringUtils.isEmpty(articleReqVO.getSort_id()) ? 0 : Integer.parseInt(articleReqVO.getSort_id());
			author_id = StringUtils.isEmpty(articleReqVO.getAuthor_id()) ? 0 : Integer.parseInt(articleReqVO.getAuthor_id());
			uid = StringUtils.isEmpty(articleReqVO.getUid()) ? 0 : Integer.parseInt(articleReqVO.getUid());
			page = StringUtils.isEmpty(articleReqVO.getPage()) ? 1 : Integer.parseInt(articleReqVO.getPage());
			limit = StringUtils.isEmpty(articleReqVO.getLimit()) ? 10 : Integer.parseInt(articleReqVO.getLimit());
			gender = articleReqVO.getGender();
			tag = articleReqVO.getTag();
			client_type = articleReqVO.getClient_type();
			udid = articleReqVO.getUdid();
		}
		ArticleReqBO articleReqBO = new ArticleReqBO(sort_id, gender, author_id, tag, page, uid, udid, limit, client_type);
		ArticleInfoRspBO articleInfoRspBO = serviceCaller.call(SnsServices.getList, articleReqBO, ArticleInfoRspBO.class);

		// (2)组装返回
		ApiResponse response = new ApiResponse.ApiResponseBuilder().code(GET_ARTICLE_LIST_SUCCESS_CODE).message(GET_ARTICLE_LIST_SUCCESS_MSG).data(articleInfoRspBO).build();

		logger.debug("Leave getArticleList controller. gender is {}, sort_id is {}, udid is {}, uid is {}", gender, sort_id, udid, uid);
		return response;
	}


	/**
	 * @dese	获得‘星潮教室’一级分类中，标签开启状态下的文章
	 * @param articleReqVO
	 * 		请求对象，请求URL示例：http://ip:port/guang/api/v2/article/getList?app_version=4.1.0&client_secret=1a51fc368cd2e494ef9c8344e21dd803&client_type=android&gender=1%2C3&os_version=android4.4.4%3AMX4_Pro&page=2&screen_size=1536x2560&sort_id=2&udid=866002023631624&v=7&yh_channel=1 HTTP/1.1
	 * @return	ApiResponse
	 */
	@RequestMapping("/*/article/getStarClassroomArticleList")
	@ResponseBody
	@Cachable(expire=10, needMD5 = true)
	public  ApiResponse getStarClassroomArticleList(ArticleReqVO articleReqVO){
		logger.debug("Enter getStarClassroomArticleList controller. param articleReqVO is {}", articleReqVO);

		//1:组装对象
		int sort_id = 0, author_id = 0, uid = 0, page = 1, limit = 10;
		String gender = "", tag = "", udid = "";
		if(null != articleReqVO){
			sort_id = StringUtils.isEmpty(articleReqVO.getSort_id()) ? 0 : Integer.parseInt(articleReqVO.getSort_id());
			author_id = StringUtils.isEmpty(articleReqVO.getAuthor_id()) ? 0 : Integer.parseInt(articleReqVO.getAuthor_id());
			uid = StringUtils.isEmpty(articleReqVO.getUid()) ? 0 : Integer.parseInt(articleReqVO.getUid());
			page = StringUtils.isEmpty(articleReqVO.getPage()) ? 1 : Integer.parseInt(articleReqVO.getPage());
			limit = StringUtils.isEmpty(articleReqVO.getLimit()) ? 10 : Integer.parseInt(articleReqVO.getLimit());
			gender = articleReqVO.getGender();
			tag = articleReqVO.getTag();
			//client_type = articleReqVO.getClient_type();
			udid = articleReqVO.getUdid();
		}
		//因为星潮教室在APP中运行，需要URL中openbuy这些数据，所以把client_type伪装成adroid
		String client_type = "android";
		ArticleReqBO articleReqBO = new ArticleReqBO(sort_id, gender, author_id, tag, page, uid, udid, limit, client_type);
		ArticleInfoRspBO starClassRoomArticleInfoRspBO = serviceCaller.call(SnsServices.getStarClassroomArticleList, articleReqBO, ArticleInfoRspBO.class);

		//2: 组装返回
		ApiResponse response=new ApiResponse.ApiResponseBuilder().code(GET_STARCLASSROOM_ARTICLE_LIST_SUCCESS_CODE).message(GET_STARCLASSROOM_ARTICLE_LIST_SUCCESS_MSG).data(starClassRoomArticleInfoRspBO).build();

		logger.debug("Leave getStarClassroomArticleList controller. gender is {}, sort_id is {}, udid is {}, uid is {}", gender, sort_id, udid, uid);
		return response ;
	}
}
