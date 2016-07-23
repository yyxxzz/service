package com.yoho.gateway.service.resources.impl;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.model.resources.NoticeVO;
import com.yoho.gateway.service.resources.NoticeService;
import com.yoho.service.model.resource.NoticeBO;
import com.yoho.service.model.resource.ResourcesServices;
import com.yoho.service.model.resource.request.NoticeRequest;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 文字公告
 * Created by sunjiexiang on 2015/12/24.
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    private static final Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

    @Autowired
    private ServiceCaller serviceCaller;

    @Override
    public NoticeVO getNotices(NoticeRequest noticeRequest) {
        logger.info("Begin getNotices noticeRequest {}.", noticeRequest);
        List<NoticeBO> noticeBOs = serviceCaller.call(ResourcesServices.getNotices, noticeRequest, List.class);
        NoticeVO noticeVO = new NoticeVO();
        noticeVO.setOpen(CollectionUtils.isNotEmpty(noticeBOs) ? "Y" : "N");
        noticeVO.setTime("3");
        List<NoticeVO.Data> list = new ArrayList<>();
        for (NoticeBO notice : noticeBOs) {
            NoticeVO.Data data = new NoticeVO.Data();
            BeanUtils.copyProperties(notice, data);
            list.add(data);
        }
        noticeVO.setList(list);
        logger.info("End getNotices noticeRequest {}.", noticeRequest);
        return noticeVO;
    }
}
