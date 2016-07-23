package com.yoho.gateway.controller.order.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import com.yoho.error.event.PaymentEvent;
import com.yoho.gateway.controller.order.payment.common.PayEventEnum;

@Service
public class PaymentEventService implements ApplicationEventPublisherAware{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private ApplicationEventPublisher publisher;
	
	@Override
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;		
	}

	/**
	 * 发布支付事件
	 * @param event
	 */
	public void publishEnvent(PaymentEvent event, PayEventEnum eventType){
		if(null == event){
			return;
		}
		event.setStatus(eventType.getName());
		this.publisher.publishEvent(event);
		
		logger.info("payment event published, {}", event);
	}
	
	
}
