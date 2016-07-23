package com.yoho.gateway.controller.product;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.product.request.BatchUpdateStorageRequest;
import com.yoho.product.request.UpdateStorageRequest;
import com.yoho.product.response.VoidResponse;

/**
 * 提供库存的增 删  查功能
 * @author xieyong
 *
 */
@Controller
public class StorageController {
	
	private final Logger logger = LoggerFactory.getLogger(StorageController.class);
	
	@Autowired
	private ServiceCaller serviceCaller;
	
	/**
	 * 全量更新库存信息
	 * @param updateStorageRequest
	 * @return
	 */
	@RequestMapping("/erp/sync/storage/full")
	@ResponseBody
	public ApiResponse batchUpdateFullStorageBySkuId(@RequestBody List<JSONObject> updateStorageRequest)
	{
		BatchUpdateStorageRequest batchUpdateStorageRequest=buildBatchUpdateStorageRequest(updateStorageRequest);
		logger.info("begin batchUpdateFullStorageBySkuId invoke updateStorageRequest is:{}",batchUpdateStorageRequest);
		VoidResponse voidResponse=serviceCaller.call("product.batchUpdateStorageBySkuId", batchUpdateStorageRequest, VoidResponse.class);
		if(voidResponse.isSuccess())
		{
			return new ApiResponse.ApiResponseBuilder().data("").code(200).message("batchUpdateFullStorageBySkuId success").build();
		}
		return new ApiResponse.ApiResponseBuilder().data("").code(500).message("batchUpdateFullStorageBySkuId success").build();
	}
	
	/**
	 * 增量更新库存信息
	 * @param updateStorageRequest
	 * @return
	 */
	@RequestMapping("/erp/sync/storage/change")
	@ResponseBody
	public ApiResponse batchChangeStorageBySkuId(@RequestBody List<JSONObject> updateStorageRequest)
	{
		BatchUpdateStorageRequest batchUpdateStorageRequest=buildBatchUpdateStorageRequest(updateStorageRequest);
		logger.info("begin batchChangeStorageBySkuId invoke updateStorageRequest is:{}",batchUpdateStorageRequest);
		VoidResponse voidResponse=serviceCaller.call("product.batchChangeStorageBySkuId", batchUpdateStorageRequest, VoidResponse.class);
		if(voidResponse.isSuccess())
		{
			return new ApiResponse.ApiResponseBuilder().data("").code(200).message("batchChangeStorageBySkuId success").build();
		}
		return new ApiResponse.ApiResponseBuilder().data("").code(500).message("batchChangeStorageBySkuId success").build();
	}
	
	
	private BatchUpdateStorageRequest buildBatchUpdateStorageRequest(List<JSONObject> list) {
		BatchUpdateStorageRequest batchUpdateStorageRequest=new BatchUpdateStorageRequest();
		List<UpdateStorageRequest> request=new ArrayList<UpdateStorageRequest>(list.size());
		UpdateStorageRequest updateStorageRequest=null;
		for (JSONObject jsonObject : list) {
			updateStorageRequest=new UpdateStorageRequest();
			updateStorageRequest.setSkuId(jsonObject.getInteger("sku"));
			updateStorageRequest.setStorageNum(jsonObject.getInteger("storageNum"));
			request.add(updateStorageRequest);
		}
		batchUpdateStorageRequest.setUpdateStorageRequest(request);
		return batchUpdateStorageRequest;
	}
	
}
