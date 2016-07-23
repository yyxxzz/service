package com.yoho.gateway.service.resources;

import com.yoho.gateway.model.resources.HotRankTagVO;

import java.util.List;

/**
 * 热门标签
 * @author LiQZ on 2016/3/15.
 */
public interface IHotRankTagService {

    /**
     * 加载热门标签列表
     * @param platform 平台 (iphone, android, web等) 默认 H5
     * @return 列表
     */
    List<HotRankTagVO> getEnableHotRankTag(String platform);

    /**
     * 根据 ID 获取热门标签
     * @param id 主键
     * @return 热门标签
     */
    HotRankTagVO getOneHotRankTag(Integer id);

}
