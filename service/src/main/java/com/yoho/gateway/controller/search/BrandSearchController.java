package com.yoho.gateway.controller.search;

import com.google.common.collect.Lists;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.product.model.SearchRecordBo;
import com.yoho.product.model.search.HotSearchBrandBo;
import com.yoho.product.request.SearchRecordRequest;
import com.yoho.product.response.VoidResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.product.BrandIntroVo;
import com.yoho.gateway.service.assist.SearchConstants;
import com.yoho.gateway.service.search.ProductBrandSearchService;
import com.yoho.product.model.BrandBo;

import java.util.List;

/**
 * Created by caoyan on 2015/12/2.
 */
@Controller
public class BrandSearchController {

    private final Logger logger = LoggerFactory.getLogger(BrandSearchController.class);

    private static final String BRAND_NOT_EXIST = "品牌不存在";

    private static final String BRAND_BANNER_NOT_EXIST = "品牌banner不存在";

    @Autowired
    private ProductBrandSearchService productBrandSearchService;

    @Autowired
    private ServiceCaller serviceCaller;

    /**
     * 根据品牌ID获取品牌信息
     * @param brand_id
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.brand.getBrandIntro")
    @ResponseBody
    public ApiResponse queryBrandInfoById(@RequestParam(value = "brand_id", required = true)String brandId,
    		@RequestParam(value = "uid", required = false)Integer uid) {
        logger.info("[method=app.brand.getBrandIntro] param brand_id is {}, uid is {}",brandId, uid);
        
        if(null == brandId){
        	return new ApiResponse(500, "Brand Id IS Null", null);
        }
        Integer brandIdInt = null;
        if(brandId.split(SearchConstants.IndexNameConstant.SEPERATOR_COMMA).length>1){
        	brandIdInt = Integer.valueOf(brandId.split(SearchConstants.IndexNameConstant.SEPERATOR_COMMA)[0]);
        }else{
        	brandIdInt = Integer.valueOf(brandId);
        }
        BrandIntroVo responseEntity = productBrandSearchService.queryBrandIntroById(brandIdInt, uid);
        if (responseEntity==null){
            return new ApiResponse(500, "Brand Intro IS Null", null);
        }
        return new ApiResponse.ApiResponseBuilder().code(200).message("Brand Intro").data(responseEntity).build();
    }

    /**
     * 根据品牌ID获取品牌的banner
     * @param brand_id
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.brand.banner")
    @ResponseBody
    @Cachable(expire = ExpireTime.app_brand_banner)
    public ApiResponse queryBrandBanner(@RequestParam(value = "brand_id", required = true)Integer brandId) {
        logger.info("[method=app.brand.getBrandIntro] param brand_id is {}", brandId);

        if(null == brandId){
            return new ApiResponse(500, BRAND_NOT_EXIST, null);
        }
        BrandBo brandBo = productBrandSearchService.queryBrandById(brandId);

        if (null == brandBo || StringUtils.isEmpty(brandBo.getBrandBanner())){
            return new ApiResponse(500, BRAND_BANNER_NOT_EXIST, null);
        }
        String bannerImage = ImagesHelper.template2(brandBo.getBrandBanner(), "brandBanner");
        bannerImage = bannerImage.split("\\?")[0] + "?imageMogr2/auto-orient/strip/thumbnail/x{height}/crop/{width}x{height}";
        JSONObject obj = new JSONObject();
        obj.put("banner", bannerImage);

        return new ApiResponse.ApiResponseBuilder().code(200).message("banner").data(obj).build();
    }

    /***
     * 品牌热搜词
     * @return
     */
    @RequestMapping(params = "method=app.search.hotBrands")
    @ResponseBody
    @Cachable(expire = ExpireTime.app_search_hotBrands)
    public ApiResponse queryHotSearchBrands(){
        logger.info("method=app.search.hotBrands is invoked");
        String serviceName = "product.queryHotsearchBrands";
        HotSearchBrandBo[]  hotSearchBrandList = serviceCaller.call(serviceName,null,HotSearchBrandBo[].class );
        logger.info("call {} get result {}",serviceName, hotSearchBrandList);
        return new ApiResponse.ApiResponseBuilder().code(200).message("HotSearchBrand List").data(hotSearchBrandList).build();
    }


    @RequestMapping(params = "method=app.search.hotBrandRecords")
    @ResponseBody
    public ApiResponse compareBrandSearchRecords(@RequestParam(value = "uid", required = false,defaultValue="0") Integer uid,
                                                 @RequestParam(value = "records", required = false) String[] records){
        logger.info("method=app.search.hotBrandRecords is invoked");
        String serviceName = "product.compareBrandSearchRecords";
        SearchRecordRequest req = new SearchRecordRequest();
        req.setUid(uid);
        if (records != null){
            req.setList(buildSearchRecordBo(records));
        }
        SearchRecordBo[] hotSearchBrandRecordList = serviceCaller.call(serviceName,req,SearchRecordBo[].class );
        return new ApiResponse.ApiResponseBuilder()
                .code(200)
                .message("HotSearchBrandRecord List")
                .data(hotSearchBrandRecordList)
                .build();
    }

    /**
     * clear Hot Brand Records
     * @param uid
     * @return
     */
    @RequestMapping(params = "method=app.search.clearHotBrandRecords")
    @ResponseBody
    public ApiResponse clearBrandSearchRecords(@RequestParam(value = "uid") Integer uid){

        logger.info("method=app.search.hotBrandRecords is invoked");
        SearchRecordRequest req = new SearchRecordRequest();
        req.setUid(uid);
        serviceCaller.call("product.clearBrandSearchRecords",req,VoidResponse.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("clear BrandSearchRecords successfully")
                .build();
    }

    private List<SearchRecordBo> buildSearchRecordBo(String[] recordes){
        List<SearchRecordBo> list= Lists.newArrayList();
        SearchRecordBo searchRecordBo;
        for(int i=0;i<recordes.length;i++){
            //时间戳_品牌_brandDomain_brandId
            if (StringUtils.isBlank(recordes[i]) || !recordes[i].contains("_")){
                continue;
            }
            String[] array = recordes[i].split("\\_");
            if (array.length !=2 ){
                continue;
            }
            searchRecordBo = new SearchRecordBo();
            if(StringUtils.isNotBlank(array[0]) && NumberUtils.isNumber(array[0])){
                searchRecordBo.setSearchTime(Integer.parseInt(array[0]));
            }
            searchRecordBo.setSearchTerms(array[1]);
            list.add(searchRecordBo);
        }
        return list;
    }
}
