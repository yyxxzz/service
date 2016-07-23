package com.yoho.gateway.controller.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.resources.SearchBannerVo;
import com.yoho.service.model.resource.AdsBo;
import com.yoho.service.model.resource.SearchBannerBO;
import com.yoho.service.model.resource.request.AdsRequest;

/**
 * @author lixuxin
 */
@Controller
public class AdsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdsController.class);
    private static final int REQUEST_ERROR_CODE = 500;
    private static final String REQUEST_ADS_ERROR_MSG = "positionId must be not null!";
    @Autowired
    private ServiceCaller serviceCaller;

    @RequestMapping(params = "method=app.ads.list")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.ADS_LIST)
    public ApiResponse queryAdsList(@RequestParam(value = "position_id", required = false) Integer position_id
            , @RequestParam(value = "max_sort_id", required = false) Integer max_sort_id
            , @RequestParam(value = "middle_sort_id", required = false) Integer middle_sort_id) throws GatewayException {
        LOGGER.info("app.activity.get param position_id is {},max_sort_id is {},middle_sort_id is {}", position_id, max_sort_id, middle_sort_id);
        if (position_id == null || position_id < 1) {
            throw new GatewayException(REQUEST_ERROR_CODE, REQUEST_ADS_ERROR_MSG);
        }
        AdsRequest request = new AdsRequest();
        request.setPositionId(position_id);
        request.setMaxSortId(max_sort_id);
        request.setMiddleSortId(middle_sort_id);
        AdsBo[] list = serviceCaller.call("resources.queryAdsList", request, AdsBo[].class);
        if (list == null) {
            return new ApiResponse.ApiResponseBuilder().code(200).message("Category Product List.").build();
        }
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (AdsBo bo : list) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("ads_image", bo.getAdsImage());
            dataMap.put("ads_url", bo.getAdsUrl());
            dataMap.put("ads_text", bo.getAdsText());
            data.add(dataMap);
        }
        return new ApiResponse.ApiResponseBuilder().code(200).message("Category Product List.").data(data).build();
    }

    @RequestMapping(params = "method=web.search.banner")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.SEARCH_BANNER)
    public ApiResponse searchBanner(@RequestParam(value = "position_id", defaultValue = "0") int positionId,
                                    @RequestParam(value = "max_sort_id", defaultValue = "0") int maxSortId,
                                    @RequestParam(value = "middle_sort_id", defaultValue = "0") int middleSortId,
                                    @RequestParam(value = "gender", required = false) String gender,
                                    @RequestParam(value = "brand", defaultValue = "0") String brand,
                                    @RequestParam(value = "style", defaultValue = "0") String style,
                                    @RequestParam(value = "color", defaultValue = "0") int color,
                                    @RequestParam(value = "price", defaultValue = "0") String price,
                                    @RequestParam(value = "size_id", defaultValue = "0") int sizeId) throws GatewayException {

        LOGGER.info("app.searh.banner param position_id is {},max_sort_id is {},middle_sort_id is {}, gender is {}, brand is {}, "
                + "style is {}, color is {}, price is {}, size_id is {}", positionId, maxSortId, middleSortId, gender, brand, style, color, price, sizeId);

        if (positionId < 1) {
            return new ApiResponse(400, "Position Id is Null", null);
        }

        AdsRequest req = new AdsRequest();
        req.setPositionId(positionId);
        req.setMaxSortId(maxSortId);
        req.setMiddleSortId(middleSortId);
        req.setStatus("1");
        req.setColor(color);
        req.setPrice(price);
        req.setBrand(getSort(brand));
        req.setSizeId(sizeId);
        req.setStyle(getSort(style));
        req.setGender(getGender(gender));

        SearchBannerBO bo = serviceCaller.call("resources.getSearchBannerByParam", req, SearchBannerBO.class);
        if (null == bo || null == bo.getData()) {
            return new ApiResponse(400, "Data is Null", null);
        }

        SearchBannerVo vo = buildSearchBannerVo(bo);

        return new ApiResponse.ApiResponseBuilder().code(200).message("searchBanner").data(vo).build();
    }

    private SearchBannerVo buildSearchBannerVo(SearchBannerBO bo) {
        SearchBannerVo vo = new SearchBannerVo();
        String data = bo.getData();
        JSONObject json = JSONObject.parseObject(data);
        vo.setLogo(ImagesHelper.template2(json.getString("logo"), "adpic").split("\\?")[0]);
        vo.setTitle(json.getString("title"));
        vo.setUrl(json.getString("url"));
        vo.setSubtitle(json.getString("subtitle"));
        vo.setIntro(json.getString("intro"));
        vo.setKeyword(json.getJSONArray("keyword"));
        vo.setTempletId(String.valueOf(bo.getTempletId()));
        vo.setResult("1");
        return vo;
    }

    private String getSort(String data) {
        String[] arr = data.split(",");
        Arrays.sort(arr);
        return Arrays.asList(arr).toString().replace("[", "").replace("]", "").replace(" ", "");

    }

    private String getGender(String gender) {
        String str = "";
        if (StringUtils.isEmpty(gender)) {
            str = "3";
        } else {
            str = "2,3".equals(gender) ? "2" : "1";
        }

        return str;
    }

}
