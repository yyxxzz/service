package com.yoho.gateway.controller.sns;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.qiniu.util.Json;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.client.hystrix.AsyncFuture;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.MyGuangVO;
import com.yoho.service.model.resource.ResourcesServices;
import com.yoho.service.model.resource.request.ResourcesRequestBody;
import com.yoho.service.model.response.CommonRspBO;
import com.yoho.service.model.sns.SnsServices;
import com.yoho.service.model.sns.request.ArticleReqBO;
import com.yoho.service.model.sns.request.ArticleTagsReqBO;
import com.yoho.service.model.sns.request.MyGuangReqBO;
import com.yoho.service.model.sns.response.PageResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新潮教室接口模块
 * @author LiQZ on 2016/6/1.
 */
@RestController
public class StarClassController {

    private static final Logger logger = LoggerFactory.getLogger(StarClassController.class);

    @Autowired
    private ServiceCaller serviceCaller;

    /**
     * 新潮教室首页
     */
    @Cachable(expire = 5, needMD5 = true)
    @RequestMapping(params = "method=app.starClass.index")
    public ApiResponse index(@RequestParam(value = "client_type") String clientType,
                             @RequestParam(value = "code", defaultValue = "8adc27fcf5676f356602889afcfd2a8e") String code) {

        logger.info("Star class input params clientType {} code {}", clientType, code);

        // 加载资源位
        ResourcesRequestBody request = new ResourcesRequestBody();
        request.setContentCode(code);
        request.setClientType(clientType);
        AsyncFuture<Object[]> adsAsync = serviceCaller.asyncCall(ResourcesServices.get, request, Object[].class);

        // 加载标签与最新文章
        ArticleReqBO articleReqBO = new ArticleReqBO();
        articleReqBO.setClient_type(clientType);
        JSONObject response = serviceCaller.call(SnsServices.getLastArticleAndTags, articleReqBO, JSONObject.class);

        try {
            Object[] ads = adsAsync.get();
            if (null != ads && 0 < ads.length) {
                response.put("ads", ads[0]);
            }
        } catch (Exception e) {
            logger.warn("get starClass focus fail.");
        }

        return new ApiResponse.ApiResponseBuilder().code(200).message("success").data(response).build();
    }

    /**
     * 新潮教室首页
     * Note: Cachable 对直接调用无效
     * [(uid), client_type]
     */
    @RequestMapping(params = "method=app.starClass.tags")
    public ApiResponse tags(@RequestParam(value = "client_type") String client_type) {

        // 获取标签信息
        ArticleTagsReqBO tagsReqBO = new ArticleTagsReqBO();
        tagsReqBO.setClientType(client_type);
        tagsReqBO.setClassifyId(2); // 新潮教室分类为 2, 之前就是这么写的
        List<JSONObject> tagsResponse = serviceCaller.call(SnsServices.getArticleTagList, tagsReqBO, List.class);

        return new ApiResponse.ApiResponseBuilder().code(200).message("success").data(tagsResponse).build();

    }

    /**
     * 最新文章分页
     * [(uid), client_type]
     */
    @RequestMapping(params = "method=app.starClass.lastArticle")
    public ApiResponse lastArticle(@RequestParam(value = "uid", defaultValue = "0") int uid,
                             @RequestParam(value = "client_type") String client_type,
                             @RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "size", defaultValue = "10")int size) {

        ApiResponse tagsResponse = tags(client_type);

        List<JSONObject> tagsData = (List<JSONObject>) tagsResponse.getData();

        if (CollectionUtils.isEmpty(tagsData)) {
            return new ApiResponse.ApiResponseBuilder().code(200).message("tags is empty").data("[]").build();
        }

        Map<String, JSONObject> tagsMap = new HashMap<>();
        StringBuilder tagsBuilder = new StringBuilder();
        for (JSONObject tag :tagsData) {
            String tagName = tag.getString("tagName");
            tagsBuilder.append(tagName).append(",");
            tagsMap.put(tagName, tag);
        }
        tagsBuilder.deleteCharAt(tagsBuilder.length() - 1);

        // 获取文章信息
        ArticleReqBO articleReqBO = new ArticleReqBO();
        articleReqBO.setUid(uid);
        articleReqBO.setClient_type(client_type);
        articleReqBO.setTag(tagsBuilder.toString());
        articleReqBO.setContentImgSize(3); // 需要三张小图
        articleReqBO.setOffset(page);
        articleReqBO.setLimit(size);
        PageResponse<JSONObject> articleResponse = serviceCaller.call(SnsServices.getArticleListByTags, articleReqBO, PageResponse.class);

        // 关联文章与 Tags， 这个需求比较特别，就放这里吧
        List<JSONObject> articleInfoBOs = articleResponse.getList();
        for (JSONObject articleInfoBO :articleInfoBOs) {
            String tag = articleInfoBO.getString("tag");
            JSONObject tagInfo = getFirstTagInfo(tag, tagsMap);
            articleInfoBO.put("tagInfo", tagInfo);
        }

        return new ApiResponse.ApiResponseBuilder().code(200).message("success").data(articleResponse).build();
    }

    /**
     * 根据 tag 获取文章
     * [(uid), client_type]
     */
    @Cachable(expire = 5, needMD5 = true)
    @RequestMapping(params = "method=app.starClass.lastTagArticle")
    public ApiResponse lastTagArticle(@RequestParam(value = "uid", defaultValue = "0") int uid,
                                      @RequestParam(value = "client_type") String client_type,
                                      @RequestParam(value = "tag") String tag,
                                      @RequestParam(value = "page", defaultValue = "1") int page,
                                      @RequestParam(value = "size", defaultValue = "10")int size) {

        logger.info("Star class last tag article params uid {} client_type {} tag {} page {} size {}", uid, client_type, tag, page, size);

        // 获取文章信息
        ArticleReqBO articleReqBO = new ArticleReqBO();
        articleReqBO.setUid(uid);
        articleReqBO.setClient_type(client_type);
        articleReqBO.setTag(tag);
        articleReqBO.setOffset(page);
        articleReqBO.setLimit(size);
        articleReqBO.setNeedIsFavor(true); // 需要判断用户是否收藏
        articleReqBO.setNeedCountFavor(false); // 需要统计用户收藏数量【又不需要了~~】
        articleReqBO.setNeedShare(true); // 需要获取分享信息
        PageResponse<JSONObject> articleResponse = serviceCaller.call(SnsServices.getArticleListByTags, articleReqBO, PageResponse.class);

        return new ApiResponse.ApiResponseBuilder().code(200).message("success").data(articleResponse).build();
    }

    /**
     * 收藏资讯返回收藏数量
     */
    @RequestMapping(params = "method=app.sns.setFavorBackCount")
    public ApiResponse setFavorite(@RequestParam(value = "uid") int uid,
                                   @RequestParam(value = "article_id") String articleId,
                                   @RequestParam(value = "client_type") String clientType) {

        logger.info("Star class set Favorite params uid {} articleId {} clientType {}", uid, articleId, clientType);

        // 收藏
        MyGuangReqBO bo = new MyGuangReqBO();
        bo.setUid(uid);
        bo.setArticle_id(articleId);
        bo.setClient_type(clientType);
        serviceCaller.call("sns.setFavorite", bo, CommonRspBO.class);

        // 统计现在收藏数量
        String count = serviceCaller.call("sns.countFavorite", bo, String.class);

        return new ApiResponse.ApiResponseBuilder().code(200).data(count).build();
    }

    /**
     * 取消用户收藏返回收藏数量
     */
    @RequestMapping(params = "method=app.sns.cancelFavorBackCount")
    public ApiResponse cancelFavorite(@RequestParam(value = "uid") int uid,
                                      @RequestParam(value = "article_id") String articleId,
                                      @RequestParam(value = "client_type") String clientType) {

        logger.info("Star class cancel Favorite params uid {} articleId {} clientType {}", uid, articleId, clientType);

        // 取消收藏
        MyGuangReqBO bo = new MyGuangReqBO();
        bo.setClient_type(clientType);
        bo.setArticle_id(articleId);
        bo.setUid(uid);
        serviceCaller.call("sns.cancelFavorite", bo, CommonRspBO.class);

        // 统计现在收藏数量
        String count = serviceCaller.call("sns.countFavorite", bo, String.class);

        return new ApiResponse.ApiResponseBuilder().code(200).data(count).build();
    }

    /**
     * 获取 Tag 详情
     */
    private JSONObject getFirstTagInfo(String tag, Map<String, JSONObject> tagsMap) {
        if (StringUtils.isBlank(tag) || tagsMap.isEmpty()) { return null; }
        Iterable<String> tagIterable = Splitter.on(",").trimResults().omitEmptyStrings().split(tag);
        for (String t :tagIterable) {
            if (tagsMap.containsKey(t)) {
                return tagsMap.get(t);
            }
        }
        return null;
    }

}
