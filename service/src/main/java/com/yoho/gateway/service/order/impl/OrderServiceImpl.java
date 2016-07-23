package com.yoho.gateway.service.order.impl;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.model.order.HistoryOrderVO;
import com.yoho.gateway.service.order.OrderService;
import com.yoho.service.model.order.OrderServices;
import com.yoho.service.model.order.model.HistoryOrderBO;
import com.yoho.service.model.order.request.OrderListRequest;
import com.yoho.service.model.order.response.CountBO;
import com.yoho.service.model.order.response.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import com.yoho.service.model.order.model.HistoryOrderBO;

/**
 * sunjiexiang
 * Created by yoho on 2016/3/15.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ServiceCaller serviceCaller;

    /**
     * 获取历史订单列表
     * @param request
     * @return
     */
    @Override
    public PageResponse<HistoryOrderVO> getHistoryOrderList(OrderListRequest request) {
        //根据UID查询历史订单总数
        CountBO countBO = serviceCaller.call(OrderServices.getHistoryOrderCount, request, CountBO.class);

        if(countBO.getCount() == 0){
            logger.info("uid {}, start {}, limit {}, the total order history data is 0", request.getUid(), request.getPage(), request.getLimit());
            return setPageResponse(request.getPage(), request.getLimit(), countBO.getCount(), Collections.EMPTY_LIST);
        }

        //根据UID查询分页订单列表
        HistoryOrderBO[] ordeses = serviceCaller.call(OrderServices.getHistoryOrderList, request, HistoryOrderBO[].class);

        //将订单列表BO转换成VO
        List<HistoryOrderVO> voList = new ArrayList<>();
        for (HistoryOrderBO orderBo : ordeses) {
            HistoryOrderVO orderVo = new HistoryOrderVO();
            BeanUtils.copyProperties(orderBo, orderVo);

            orderVo.setOrderTime(String.valueOf(orderBo.getOrderTime()));
            orderVo.setAmount(String.valueOf(orderBo.getAmount()));

            voList.add(orderVo);
        }

        return setPageResponse(request.getPage(), request.getLimit(), countBO.getCount(), voList);
    }

    /**
     * 设置分页返回对象
     * @param pageNo
     * @param limit
     * @param totalCount
     * @param dataList
     * @return
     */
    private PageResponse<HistoryOrderVO> setPageResponse(Integer pageNo, Integer limit, int totalCount, List<HistoryOrderVO> dataList){
        PageResponse<HistoryOrderVO> response = new PageResponse<>();
        response.setPageNo(pageNo);
        response.setPageSize(limit);
        response.setTotalCount(totalCount);
        response.setList(dataList);
        return response;
    }
}
