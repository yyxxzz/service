package com.yoho.gateway.service.resources.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.model.resources.HotRankTagVO;
import com.yoho.gateway.service.resources.IHotRankTagService;
import com.yoho.service.model.resource.HotRankTagBO;
import com.yoho.service.model.resource.ResourcesServices;
import com.yoho.service.model.resource.request.HotRankTagRequest;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LiQZ on 2016/3/15.
 */
@Service
public class HotRankTagServiceImpl implements IHotRankTagService {

    private static final Logger logger = LoggerFactory.getLogger(HotRankTagServiceImpl.class);

    @Autowired
    private ServiceCaller serviceCaller;

    private static final Function<HotRankTagBO, HotRankTagVO> BO_TO_VO = hotRankTagBO -> {
        HotRankTagVO hotRankTagVO = new HotRankTagVO();
        BeanUtils.copyProperties(hotRankTagBO, hotRankTagVO);
        return hotRankTagVO;
    };

    @Override
    public List<HotRankTagVO> getEnableHotRankTag(String platform) {
        logger.info("Begin getEnableHotRankTag platform {}.", platform);
        HotRankTagRequest request = new HotRankTagRequest();
        request.setPlatform(platform);
        List<HotRankTagBO> hotRankTagBOList = serviceCaller.call(ResourcesServices.getHotRankTag, request, List.class);

        List<HotRankTagVO> hotRankTagVOList  = Lists.transform(hotRankTagBOList, BO_TO_VO);
        logger.info("End getEnableHotRankTag hotRankTagBOList size {}.", CollectionUtils.size(hotRankTagBOList));
        return hotRankTagVOList;
    }

    @Override
    public HotRankTagVO getOneHotRankTag(Integer id) {
        logger.info("Begin getOneHotRankTag id {}.", id);
        HotRankTagRequest request = new HotRankTagRequest();
        request.setId(id);
        HotRankTagBO hotRankTagBO = serviceCaller.call("resources.getOneHotRankTag", request, HotRankTagBO.class);
        return BO_TO_VO.apply(hotRankTagBO);
    }
}
