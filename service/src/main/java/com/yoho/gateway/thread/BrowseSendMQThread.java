package com.yoho.gateway.thread;

import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.gateway.model.browse.BrowseReqVO;
import com.yoho.gateway.mqmessage.YhProducerTemplateCommon;
import com.yoho.gateway.service.favorite.IBrowseServiceNew;
import com.yoho.gateway.utils.AppVersionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bblu on 2016/7/4.
 */
@Component
public class BrowseSendMQThread {
    private static Logger logger = LoggerFactory.getLogger(BrowseSendMQThread.class);

    @Resource
    private YhProducerTemplateCommon producerTemplateCommon;

    @Resource(name = "yhRedisTemplate")
    private YHRedisTemplate<String, String> myRedisTemplate;

    // 发送MQ TOPIC
    private final static String BROWSE_ADDBROWSE_TOPIC = "browse.addBrowse";

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public BrowseSendMQThread() {
        logger.info("BrowseSendMQThread start...");

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    try {
                        Thread.sleep(50);
                        if (0 == count % 100) {
                            logger.info("BrowseSendMQThread: browse blockingqueue size is {}", IBrowseServiceNew.blockingQueue.size());
                            count = 0;
                        }
                        count++;

                        // 从blockingqueue中取出100个对象用于发送MQ消息
                        List<BrowseReqVO> browseReqVOList = new ArrayList<>();
                        IBrowseServiceNew.blockingQueue.drainTo(browseReqVOList, 100);
                        if (0 == browseReqVOList.size()) {
                            continue;
                        }

                        // 校验列表数据合法性
                        Iterator<BrowseReqVO> iterator = browseReqVOList.iterator();
                        while (iterator.hasNext()) {
                            BrowseReqVO browseReqVO = iterator.next();
                            if (null != browseReqVO && (0 < browseReqVO.getUid() || (StringUtils.isNotBlank(browseReqVO.getUdid()) && StringUtils.isNotBlank(browseReqVO.getApp_version()) && AppVersionUtils.EnVersionCompareResult.smaller == AppVersionUtils.compareVersion(browseReqVO.getApp_version(), "4.1.0.***")))) {
                                continue;
                            }
                            iterator.remove();
                        }
                        logger.debug("BrowseSendMQThread: browseReqVOList size is {}", browseReqVOList.size());

                        // 发送MQ消息
                        if (0 == browseReqVOList.size()) {
                            continue;
                        }
                        producerTemplateCommon.send(BROWSE_ADDBROWSE_TOPIC, browseReqVOList);

                    } catch (Exception e) {
                        logger.warn("browser send MQ error. error message is {}", e.getMessage());
                    }
                }
            }
        });
    }
}
