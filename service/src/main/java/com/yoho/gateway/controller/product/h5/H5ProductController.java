package com.yoho.gateway.controller.product.h5;

import java.util.ArrayList;

import com.yoho.gateway.cache.expire.product.ExpireTime;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.product.ProductCacheFinder;
import com.yoho.gateway.controller.product.builder.DefaultProductBuilder;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.product.model.CommentBo;
import com.yoho.product.model.ProductBo;
import com.yoho.product.model.ProductInfoBo;
import com.yoho.product.model.wrapper.CommentBoWrapper;
import com.yoho.product.request.BaseRequest;

/**
 * @author xieyong
 * H5网络不通，通过nginx代理到gw，然后掉服务
 */
@Controller
public class H5ProductController {
	
    private final Logger logger = LoggerFactory.getLogger(H5ProductController.class);

	@Autowired
    private ServiceCaller serviceCaller;
	
	@Autowired
	private ProductCacheFinder productCacheFinder;
	
	@Autowired
	private H5LimitProductHelper limitProductHelper;
	
	/**
	 * 根据productskn唯一确定一个商品
	 * 获取商品详细信息
	 * @param productId 根据productId获得一个产品信息
	 * @param productskn 根据SKN获得一个产品信息
	 * @param userId
	 * @return
	 * @throws GatewayException 
	 */
	@RequestMapping(params = "method=h5.product.data")
	@ResponseBody
	public ProductBo queryProductDetailByProductId(
			@RequestParam(value = "productId", required = false) Integer productId,
			@RequestParam(value = "product_skn", required = false) Integer productskn,
			@RequestParam(value = "uid", required = false) Integer userId) throws GatewayException {
		
		if(null==productskn && null==productId)
		{
			throw new GatewayException(404, "product_skn or product_id Is Null");
		}
		logger.info("enter queryProductDetailByProductId method=h5.product.data productId is:{},productskn is:{},userId is:{}",productId,productskn,userId);
		
		ProductBo productBo = productCacheFinder.fetchProductBo(productskn, productId, userId,null);
		//暂时兼容1.0.0版本,升级到1.0.1之后就不需要了,只是为了出现一个commentTotal的节点
		CommentBoWrapper commentBoWrapper=productBo.getCommentBoWrapper();
		if(null==commentBoWrapper||null==commentBoWrapper.getCommentTotal()||CollectionUtils.isEmpty(commentBoWrapper.getCommentBoList()))
		{
			commentBoWrapper=new CommentBoWrapper();
			commentBoWrapper.setCommentTotal(0);
			commentBoWrapper.setCommentBoList(new ArrayList<CommentBo>());
			productBo.setCommentBoWrapper(commentBoWrapper);
		}
		limitProductHelper.afterProcessProductStatus(userId, productBo);
		
		new DefaultProductBuilder().filterGoodSizeBo(productBo.getGoodsList());
		return productBo;
	}
	
	/**
	 * 根据skn查询商品下半页详情描述信息
	 * @param productskn
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=h5.product.intro")
	@ResponseBody
	@Cachable(expire = ExpireTime.h5_product_intro)
	public ProductInfoBo queryProductIntro(@RequestParam(value = "productskn", required = true) Integer productskn) throws GatewayException {
		
		if(null==productskn)
		{
			throw new GatewayException(404, "product_skn or product_id Is Null");
		}
		logger.info("enter queryProductIntro method=h5.product.intro productskn is:{}",productskn);
		BaseRequest<Integer> baseRequest=new BaseRequest<Integer>();
		baseRequest.setParam(productskn);
		ProductInfoBo productInfoBo = null;
		try {
			productInfoBo = serviceCaller.call("product.queryProductIntroBySkn", baseRequest, ProductInfoBo.class);
		} catch (ServiceException e) {
			logger.warn("invoke product.queryProductIntroBySkn failed",e);
			throw e;
		}
		if(null==productInfoBo)
		{	
			logger.warn("queryProductIntroBySkn productInfoBo is null");
			throw new GatewayException(404, "productInfoBo is null");
		}
		logger.debug("exit queryProductIntro method=h5.product.intro productInfoBo is:{} ",productInfoBo);
		return productInfoBo;
	}
}
