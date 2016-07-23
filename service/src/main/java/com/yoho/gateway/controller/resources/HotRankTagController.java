package com.yoho.gateway.controller.resources;

import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.resources.HotRankTagVO;
import com.yoho.gateway.service.resources.IHotRankTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 热门标签
 *
 * @author LiQZ on 2016/3/15.
 */
@Controller
public class HotRankTagController {

    public static final Logger logger = LoggerFactory.getLogger(HotRankTagController.class);

    @Autowired
    private IHotRankTagService hotRankTagService;

    /**
     * 查询标签列表
     */
    @RequestMapping(params = "method=app.resources.getHotRankTag")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.HOT_RANK_TAG)
    public ApiResponse getHotRankTag(@RequestParam(value = "client_type", required = false, defaultValue = "H5") String clientType) {
        logger.info("HotRankTagController#getHotRankTag with param is {}", clientType);
        List<HotRankTagVO> hotRankTagVOs = hotRankTagService.getEnableHotRankTag(clientType);
        return new ApiResponse.ApiResponseBuilder().code(200).message("热门标签列表").data(hotRankTagVOs).build();
    }

    @RequestMapping(params = "method=app.resources.getOneHotRankTag")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.HOT_RANK_TAG)
    public ApiResponse getOneHotRankTag(Integer id) {
        logger.info("HotRankTagController#getOneHotRankTag with param is {}", id);
        HotRankTagVO hotRankTagVO = hotRankTagService.getOneHotRankTag(id);
        return new ApiResponse.ApiResponseBuilder().data(hotRankTagVO).build();
    }
}
