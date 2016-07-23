package com.yoho.gateway.service.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.model.product.HotRankTagVo;
import com.yoho.gateway.service.search.HotRankTagService;
import com.yoho.service.model.resource.HotRankTagBO;
import com.yoho.service.model.resource.request.HotRankTagRequest;

/**
 * 获取热门标签操作接口
 * @author wangshusheng
 *
 */
@Service(value="hotRankTagService")
public class HotRankTagServiceImpl implements HotRankTagService{
	// LOG
	private static final Logger LOGGER = LoggerFactory.getLogger(HotRankTagServiceImpl.class);
	
	/**
	 * http请求工具
	 */
	@Autowired
    private ServiceCaller serviceCaller;
	
	@Override
	public List<HotRankTagVo> getTagsList(String yh_channel, String client_type) {
		LOGGER.info("Method getTagsList in; yh_channel is:{}, client_type is:{}", new Object[]{yh_channel, client_type});
		
		List<HotRankTagVo> hotRankTagList = new ArrayList<HotRankTagVo>();
		HotRankTagBO[] hotRankTagArray = null;
		HotRankTagRequest req = new HotRankTagRequest();
		if(StringUtils.isEmpty(yh_channel)){
			yh_channel = "1";
		}
		if(StringUtils.isEmpty(client_type)){
			client_type = "iphone";
		}
		req.setChannel(yh_channel);
		req.setClientType(client_type);
		
		try {
			hotRankTagArray = serviceCaller.call("product.getTagsList", req, HotRankTagBO[].class);
		} catch (Exception e) {
			LOGGER.warn("getTagsList find wrong. yh_channel: {},client_type:{}" ,yh_channel ,client_type, e);
		} 
		
		HotRankTagVo hotRankTagVo = new HotRankTagVo();
		hotRankTagVo.setId(1);
		hotRankTagVo.setName("TOP100");
		hotRankTagVo.setParams("");
		hotRankTagList.add(hotRankTagVo);
		
		if(hotRankTagArray!=null && hotRankTagArray.length>0){
			for (HotRankTagBO hotRankTag : hotRankTagArray) {
				hotRankTagVo = new HotRankTagVo();
				hotRankTagVo.setId(hotRankTag.getId());
				hotRankTagVo.setName(hotRankTag.getTagName());
				hotRankTagVo.setParams("sort=" + hotRankTag.getCategoryId());
				hotRankTagList.add(hotRankTagVo);
			}
		}
		
		return hotRankTagList;
	}
    
	
}
