package com.yoho.gateway.controller.servicescontrol;

import com.yoho.core.rest.client.finder.zookeeper.CuratorXDiscoveryClientWrapper;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedList;
import java.util.List;

/**
 *  服务管理
 * Created by chunhua.zhang@yoho.cn on 2016/1/11.
 */
@RequestMapping("/service_control")
@Controller
public class ServiceController {

    private final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    private CuratorXDiscoveryClientWrapper curatorXDiscoveryClientWrapper;
    /**
     * 注销服务
     * @param ip IP地址
     * @param context context
     */
    @RequestMapping("/unregister")
    public @ResponseBody  ApiResponse unregister(@RequestParam("ip") String ip,@RequestParam("context") String context) throws GatewayException {
        try {
            curatorXDiscoveryClientWrapper.unRegister(ip, context);
        } catch (Exception e) {
            logger.error("can not remove service at:{} context:{}", ip, context);
            throw  new GatewayException(400, "注销服务失败:"+ e.getMessage());
        }

        return new ApiResponse.ApiResponseBuilder().build();
    }

    /**
     * 注销服务
     * @param serviceName IP地址
     */
    @RequestMapping("/list")
    public @ResponseBody  ApiResponse getServices(@RequestParam("name") String serviceName) throws GatewayException {

       List<String> allPath = new LinkedList<>();
        try {
            allPath =   curatorXDiscoveryClientWrapper.getAllRequestUrl(serviceName);
        } catch (Exception e) {
            logger.error("get all path exception.", e);
            throw  new GatewayException(400, "获取所有服务异常:"+ e.getMessage());
        }

        return new ApiResponse.ApiResponseBuilder().data(allPath).build();
    }


}
