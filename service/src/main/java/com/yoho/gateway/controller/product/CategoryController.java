package com.yoho.gateway.controller.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.product.model.CategoryBo;


/**
 * 二级分类列表
 * @author 李绪新
 *
 */
@Controller
public class CategoryController {

	@Autowired
	private ServiceCaller serviceCaller;
	
	@RequestMapping(params = "method=app.category.getMax")
	@ResponseBody
	@Cachable(expire = ExpireTime.app_category_getMax)
	public ApiResponse queryMaxCategory() {
		CategoryBo[] list=serviceCaller.call("product.querymiddleCategoryList", null, CategoryBo[].class);
		List<Map<String, Object>>  data=new ArrayList<Map<String,Object>>();
		for(CategoryBo bo:list){
			Map<String, Object> dataMap=new HashMap<String, Object>();
			dataMap.put("category_id", bo.getCategoryId());
			dataMap.put("category_name", bo.getCategoryName());
			data.add(dataMap);
		}
		return new ApiResponse.ApiResponseBuilder().code(200).message("Category Product List.").data(data).build();
	}

	/**
	 * 获取产品大分类的列表
	 * @return
	 * @author zhouxiang
	 */
	@RequestMapping(params = "method=app.category.queryMax")
	@ResponseBody
	@Cachable(expire = ExpireTime.app_category_queryMax)
	public ApiResponse queryMaxCategoryList() {
		CategoryBo[] list=serviceCaller.call("product.queryMaxCategoryList", null, CategoryBo[].class);
		List<Map<String, Object>>  data=new ArrayList<Map<String,Object>>();
		for(CategoryBo bo:list){
			Map<String, Object> dataMap=new HashMap<String, Object>();
			dataMap.put("category_id", bo.getCategoryId());
			dataMap.put("category_name", bo.getCategoryName());
			data.add(dataMap);
		}
		return new ApiResponse.ApiResponseBuilder().code(200).message("Category Product List.").data(data).build();
	}

	/**
	 * 根据parentId获取二级分类的列表
	 * @return
	 * @author zhouxiang
	 */
	@RequestMapping(params = "method=app.category.queryMin")
	@ResponseBody
	@Cachable(expire = ExpireTime.app_category_queryMin)
	public ApiResponse queryMinCategoryListByParentId(@RequestParam(value="parent_id", required=false) Integer parent_id) {
		if(parent_id == null){
			return new ApiResponse.ApiResponseBuilder().code(500).message("parent_id is null").data(null).build();
		}
		CategoryBo[] list=serviceCaller.call("product.queryMinCategoryListByParentId", parent_id, CategoryBo[].class);
		List<Map<String, Object>>  data=new ArrayList<Map<String,Object>>();
		for(CategoryBo bo:list){
			Map<String, Object> dataMap=new HashMap<String, Object>();
			dataMap.put("category_id", bo.getCategoryId());
			dataMap.put("category_name", bo.getCategoryName());
			data.add(dataMap);
		}
		return new ApiResponse.ApiResponseBuilder().code(200).message("Category Product List.").data(data).build();
	}
}
	
