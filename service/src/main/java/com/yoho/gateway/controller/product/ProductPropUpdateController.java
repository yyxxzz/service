package com.yoho.gateway.controller.product;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.service.product.ProductCacheClearService;
import com.yoho.product.constants.PrdPropNames;
import com.yoho.product.model.ProductBo;
import com.yoho.product.request.BatchUpdateReq;
import com.yoho.product.request.BatchUpdateStorageRequest;
import com.yoho.product.response.VoidResponse;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchao on 2016/7/11.
 */
@Controller
public class ProductPropUpdateController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ServiceCaller serviceCaller;

    @Autowired
	private ProductCacheClearService productCacheClearService;
    
    /**
     * 更新促销标记信息
     * @param prdListRequest
     * @return
     */
    @RequestMapping("/erp/sync/promotion/update")
    @ResponseBody
    public ApiResponse batchUpdateIsPromotionBySkn(@RequestBody List<JSONObject> prdListRequest){
        if (CollectionUtils.isEmpty(prdListRequest)){
            logger.error("prdListRequest is empty");
            return new ApiResponse.ApiResponseBuilder().code(500).message("batchUpdateIsPromotionBySkn fail").build();
        }
        BatchUpdateReq req = buildBatchRequest(prdListRequest);
        req.setPropName(PrdPropNames.PROP_ISPROMOTION);
        logger.info("begin batchUpdateIsPromotionBySkn invoke updatePrdProp is:{}",req);
        int row = serviceCaller.call("product.updatePrdProp", req, Integer.class);
        
        List<Integer> productSkns = new ArrayList<Integer>();
        for (ProductBo productBo : req.getProductBoList()) {
        	productSkns.add(productBo.getErpProductId());
		}
        // 清理缓存
        productCacheClearService.clearBatchProductCacheBySkn(productSkns);
        
        return new ApiResponse.ApiResponseBuilder().data(row).code(200).message("batchUpdateIsPromotionBySkn success").build();
    }

    private BatchUpdateReq buildBatchRequest(List<JSONObject> prdListRequest){
        BatchUpdateReq req = new BatchUpdateReq();
        List<ProductBo> productBoList = Lists.newArrayList();
        ProductBo bo;
        for(JSONObject json: prdListRequest  ) {
            bo = JSONObject.parseObject(json.toJSONString(), ProductBo.class);
            productBoList.add(bo);
        }
        req.setProductBoList(productBoList);
        return req;
    }

}
