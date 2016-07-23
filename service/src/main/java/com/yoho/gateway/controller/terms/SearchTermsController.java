package com.yoho.gateway.controller.terms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.product.model.SearchRecordBo;
import com.yoho.product.model.SearchTermsBo;
import com.yoho.product.request.SearchRecordRequest;

@Controller
public class SearchTermsController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchTermsController.class);
	private static final int REQUEST_TERMS_ERROR_CODE=500;
	private static final String REQUEST_TERMS_ERROR_MSG="uid must be not null!";

	@Autowired
	private ServiceCaller serviceCaller;
	/**
	 * 查询搜索词
	 * @return
	 * @throws GatewayException 
	 */
	@RequestMapping(params = "method=app.search.getTerms")
	@ResponseBody
	@Cachable(expire=200)
	public ApiResponse queryTerms() throws GatewayException {
		
		SearchTermsBo[] list=serviceCaller.call("product.querySearchTerm", null, SearchTermsBo[].class);
		List<Map<String, Object>>  hotTerm=new ArrayList<Map<String,Object>>();
		List<Map<String, Object>>  defaltTerm=new ArrayList<Map<String,Object>>();
		for(SearchTermsBo bo:list){
			Map<String, Object> dataMap=new HashMap<String, Object>();
			dataMap.put("id", bo.getId());
			dataMap.put("content", bo.getContent());
			dataMap.put("type", bo.getType());
			dataMap.put("status", bo.getStatus());
			dataMap.put("sort", bo.getSort());
			dataMap.put("url",bo.getUrl() );
			if(null!=bo.getType()&&bo.getType()==1){
				defaltTerm.add(dataMap);
			}
			if(null!=bo.getType()&&bo.getType()==3){
				hotTerm.add(dataMap);
			}
		}
		Map<String, Object> data=new HashMap<String, Object>();
		data.put("defaultTerms", defaltTerm);
		data.put("hotTerms", hotTerm);
		return new ApiResponse.ApiResponseBuilder().code(200).message("Terms  List.").data(data).build();
	}
	
	/**
	 * 
	 * @param uid 用户ID
	 * @param records app端传过来的历史搜索记录
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params="method=app.search.compareRecord")
	@ResponseBody
	public ApiResponse compareSearchRecord(@RequestParam(value = "uid", required = false,defaultValue="0") Integer uid,
			@RequestParam(value = "records", required = false) String[] records ) throws GatewayException {
		LOGGER.info("[method=app.search.recorde] in. uid is:{},recordes:{}",new Object[]{uid,records});
		long beginTime = System.currentTimeMillis();
		if(uid==null||uid<1){
			throw new GatewayException(REQUEST_TERMS_ERROR_CODE,REQUEST_TERMS_ERROR_MSG);
		}
		SearchRecordRequest req=new SearchRecordRequest();
		
		req.setList(buildSearchRecordBo(records));
		req.setUid(uid);
		SearchRecordBo[] list=serviceCaller.call("product.compareSearchRecorde", req, SearchRecordBo[].class);
		LOGGER.info("The time consuming of method=app.search.compareRecord is {}", (System.currentTimeMillis() - beginTime));
		return new ApiResponse.ApiResponseBuilder().code(200).message("Last Search List.").data(list).build();
	}
	private List<SearchRecordBo> buildSearchRecordBo(String[] recordes){
		List<SearchRecordBo> list=Lists.newArrayList();
		for(int i=0;i<recordes.length;i++){
			SearchRecordBo searchRecordBo=new SearchRecordBo();
			String[] array=recordes[i].split("\\_");
			if(array[0]!=null&&!"".equals(array[0])){
				searchRecordBo.setSearchTime(Integer.parseInt(array[0]));
			}
			searchRecordBo.setSearchTerms(array[1]);
			list.add(searchRecordBo);
		}
		return list;
	}


}
