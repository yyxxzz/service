package com.yoho.gateway.controller.resources;

import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.resources.NoticeVO;
import com.yoho.gateway.service.resources.NoticeService;
import com.yoho.service.model.resource.request.NoticeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 文字公告
 * Created by sunjiexiang on 2015/12/24.
 */
@Controller
public class NoticeController {

    private static final Logger logger = LoggerFactory.getLogger(NoticeController.class);

    @Autowired
    private NoticeService noticeService;

    /**
     * 获取文字公告
     */
    @RequestMapping(params = "method=app.resources.getNotices")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.NOTICES)
    public ApiResponse getNotices(@RequestParam(value = "position", required = false, defaultValue = "2") int position,
                                  @RequestParam(value = "client_type", required = false, defaultValue = "H5") String clientType) {
        logger.info("getNotices enter position {}, clientType {}.", position, clientType);
        NoticeRequest noticeRequest = new NoticeRequest();
        noticeRequest.setPosition(position);
        noticeRequest.setClientType(clientType);
        NoticeVO data = noticeService.getNotices(noticeRequest);
        logger.info("getNotices exit position {}, clientType {}.", position, clientType);
        return new ApiResponse.ApiResponseBuilder().code(200).message("文字公告信息").data(data).build();
    }
}
