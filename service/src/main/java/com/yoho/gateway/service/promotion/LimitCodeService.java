package com.yoho.gateway.service.promotion;

import java.util.List;

import com.yoho.gateway.model.promotion.LimitCodeProductVo;
import com.yoho.gateway.model.promotion.LimitCodeVo;
import com.yoho.service.model.promotion.LimitCodeProductBo;

/**
 * 
 * @author wangshusheng
 * @Time 2016/2/17
 *
 */
public interface LimitCodeService {

	LimitCodeVo getLimitCodeVo(LimitCodeProductBo[] limitCodeProducts);
}
