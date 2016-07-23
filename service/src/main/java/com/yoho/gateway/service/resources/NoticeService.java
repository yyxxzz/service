package com.yoho.gateway.service.resources;

import com.yoho.gateway.model.resources.NoticeVO;
import com.yoho.service.model.resource.request.NoticeRequest;

import java.util.List;

/**
 * 文字公告
 * Created by sunjiexiang on 2015/12/24.
 */
public interface NoticeService {

    /**
     * 获取文字公告集合
     * @param noticeRequest
     * @return
     */
    NoticeVO getNotices(NoticeRequest noticeRequest);
}
