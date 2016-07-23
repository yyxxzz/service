package com.yoho.gateway.controller.search;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.search.ProductSearchService;

/**
 * @author caoyan
 *
 */
@Controller
public class LifeOrKidsProductController {

	private final Logger logger = LoggerFactory.getLogger(LifeOrKidsProductController.class);
	
	@Autowired
	private ProductSearchService productSearchService;
	
	/**
     * 创意生活频道商品搜索
     * @return
     */
    @RequestMapping(params = "method=app.search.lifeStyle")
    @ResponseBody
    @Cachable(expire= ExpireTime.app_search_lifeStyle)
    public ApiResponse queryLifeStyleProduct(@RequestParam(value = "client_type", required = false) String clientType) {
    	logger.info("come into method=app.search.lifeStyle");
    	//人气单品
    	ProductSearchReq topReq = new ProductSearchReq().setPage(1).setLimit(50).setOutlets(2).setOrder("shelve_time:desc")
    			.setShelveTime(getShelveTime()).setMisort("266,103,280,101,259,285,278").setStatus(1).setStocknumber(1).setClientType(clientType)
    			.setAttributeNot(2).setSearchFrom("search.lifeStyle");
    	JSONArray topData = productSearchService.searchLifeStyleProductList(topReq);
    	
    	//新品到着
    	JSONArray newData = new JSONArray();
    	ProductSearchReq newReq = new ProductSearchReq().setPage(1).setLimit(16).setOutlets(2).setOrder("shelve_time:desc")
    			.setStatus(1).setStocknumber(1).setAttributeNot(2).setSearchFrom("search.lifeStyle");
    	//需要从这些分类下每个分类下取16个商品
    	String misortStr = "266,103,280,101,259,285,278";
    	String[] misortArr = misortStr.split(",");
    	for(int index=0; index<misortArr.length; index++){
    		newReq.setMisort(misortArr[index]);
    		JSONArray result = productSearchService.searchLifeStyleProductList(newReq);
    		newData.addAll(result);
    	}
    	 
    	JSONObject jsonObj = new JSONObject();
    	jsonObj.put("top", topData);
    	jsonObj.put("new", newData);
    	JSONObject resultJson = new JSONObject();
    	resultJson.put("product_list", jsonObj);
    	
        return new ApiResponse.ApiResponseBuilder().code(200).data(resultJson).message("Life Style List.").build();
    }
    
    /**
     * 潮童频道商品搜索
     * @return
     */
    @RequestMapping(params = "method=app.search.kids")
    @ResponseBody
    @Cachable(expire= ExpireTime.app_search_kids)
    public ApiResponse queryKidsProduct(@RequestParam(value = "limit", required = false) Integer limit, 
    		@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "app_version", required = false) String appVersion,
                                        @RequestParam(value = "client_type", required = false) String clientType) {
    	logger.info("come into method=app.search.kids");
		ProductSearchReq req = new ProductSearchReq()
				.setLimit(null == limit ? 20 : limit)
				.setPage(null == page ? 1 : page)
				.setFirstShelveTime(getFirstShelveTime())
				.setMisort("430,423,453,406,371,396,367,400,402,404,370,388,390,369,384,392,368,448,410,394,408,414,421,441,425,427")
                .setAppVersion(appVersion).setClientType(clientType)
				.setSearchFrom("search.kids");
    	
    	JSONObject data = productSearchService.searchKidsProductList(req);
    	
        return new ApiResponse.ApiResponseBuilder().code(200).data(data).message("Last Search List.").build();
    }
    
    /**
     * 获取上架时间
     * @return
     */
    private String getShelveTime() {
		Date now = new Date();
		return now.getTime() / 1000 - (86400 * 60)+ "," + now.getTime()/ 1000;
	}
    
    /**
     * 获取第一次上架时间
     * @return
     */
    private String getFirstShelveTime() {
		Date now = new Date();
		return now.getTime() / 1000 - (86400 * 30)+ "," + now.getTime()/ 1000;
	}
}
