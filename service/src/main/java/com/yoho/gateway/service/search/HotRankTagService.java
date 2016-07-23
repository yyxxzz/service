package com.yoho.gateway.service.search;

import java.util.List;

import com.yoho.gateway.model.product.HotRankTagVo;

/**
 * 获取热门标签操作接口
 * @author wangshusheng
 *
 */
public interface HotRankTagService {
	/**
	 * 根据yh_channel,client_type查询热门标签信息
	 * @param yh_channel
	 * @param client_type
	 * @return 热门标签信息   查询不到则返回null
	 */
	List<HotRankTagVo> getTagsList(String yh_channel, String client_type);
}
