package com.yoho.gateway.service.order;

import com.yoho.gateway.model.order.HistoryOrderVO;
import com.yoho.service.model.order.request.OrderListRequest;
import com.yoho.service.model.order.response.PageResponse;
import org.springframework.stereotype.Service;

/**
 * sunjiexiang
 */
@Service
public interface OrderService {

    /**
     * 获取历史订单
     * @param request
     */
    public PageResponse<HistoryOrderVO> getHistoryOrderList(OrderListRequest request);
}
