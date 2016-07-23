package com.yoho.gateway.service.assist;

import org.springframework.stereotype.Service;

import com.yoho.gateway.model.search.SimpleBrandInfoVo;
import com.yoho.product.model.SimpleBrandInfo;

/**
 * 品牌的Bo到Vo的转换器
 * @author mali
 *
 */
@Service
public class DefaultBrandInfoConvert implements BrandInfoConvert {
	/**
	 * 品牌的Bo到Vo的转换
	 * @param info Bo 为空则返回NULL
	 * @return Vo
	 */
	public SimpleBrandInfoVo convertVo(SimpleBrandInfo info) {
		if (null == info) {
			return null;
		}
		SimpleBrandInfoVo vo = new SimpleBrandInfoVo();
		
		// 完善图标信息的完整URL
		vo.setBrandBanner(ImageUrlAssist.getAllProductPicUrl(info.getBrandBanner(), "brandBanner", "center", "d2hpdGU="));
		vo.setBrandDomain(info.getBrandDomain());
		
		// 完善品牌LOGO的完整URL
		vo.setBrandIco(info.getBrandIco());
		vo.setBrandName(info.getBrandName());
		vo.setId(info.getId());
		return vo;
	}
}
