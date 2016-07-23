package com.yoho.gateway.service.search;

import com.alibaba.fastjson.JSONObject;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.product.BrandIntroVo;
import com.yoho.product.model.BrandBo;
import com.yoho.product.model.ShopsBo;

import java.util.Map;

/**
 * Created by sailing on 2015/11/21.
 */
public interface ProductBrandSearchService {

    /**
     * 查询品牌列表
     * @param channel
     * @return
     * @throws GatewayException
     */
	JSONObject queryBrandListByChannl(String channel) throws ServiceException;
	
	/**
	 * 查询品牌列表
	 * @param channel
	 * @return
	 * @throws ServiceException
	 */
	Map<String, Object> queryNewBrandListByChannel(String channel)throws ServiceException;

    /**
     * 根据品牌ID获取品牌信息
     * @param brandId
     * @return
     */
    BrandIntroVo queryBrandIntroById(Integer brandId, Integer uid);

    /**
     * 根据品牌ID获取品牌banner
     * @param brandId
     * @return
     */
    BrandBo queryBrandById(Integer brandId);
    /**
     * 根据店铺id查询店铺信息
     * @param shopid
     * @return
     */
    public ShopsBo queryShopsBoById(Integer shopid);
}