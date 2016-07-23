package com.yoho.gateway.controller.resources;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.service.resources.ICategoryService;
import com.yoho.service.model.resource.ResourcesServices;
import com.yoho.service.model.resource.request.CategoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 菜单分类
 * Created by sunjiexiang on 2016/1/25.
 */
@Controller
public class CategoryResourceController {

    @Autowired
    private ServiceCaller serviceCaller;

    @Autowired
    private ICategoryService categoryService;

    /**
     * 获得分类
     *
     * @param clientType
     * @param parentId
     * @return
     */
    @RequestMapping("/operations/api/{v}/category/getCategory")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.CATEGORY_TREE)
    public ApiResponse getCategory(@RequestParam(value = "client_type", required = false, defaultValue = "h5") String clientType,
                                   @RequestParam(value = "parent_id", required = false, defaultValue = "all") String parentId,
                                   @RequestParam(value = "app_version", required = false, defaultValue = "4.6.0") String app_version) {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setClientType(clientType);
        categoryRequest.setParentId(parentId);
        List<Map<String, Object>> categoryList = serviceCaller.call(ResourcesServices.getCategory, categoryRequest, List.class);
        //判断客户端版本号是否低于4.6.0
        if (app_version.compareTo("4.6.0") < 0) {
            for (Map<String, Object> categoryMap : categoryList) {
                //去除id=1107对应的数据（奥莱）
                if (categoryMap.get("id") != null && categoryMap.get("id").equals("1107")) {
                    categoryList.remove(categoryMap);
                    break;
                }
            }
        }
        return new ApiResponse.ApiResponseBuilder().code(200).message("Operation Category List").data(categoryList).build();
    }

    /**
     * 根据ID获得分类
     *
     * @param id
     * @return
     */
    @RequestMapping("/operations/api/{v}/category/getOneCategory")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.CATEGORY_DETAIL)
    public ApiResponse getOneCategory(@RequestParam(value = "id") Integer id,
                                      @RequestParam(value = "fields", required = false, defaultValue = "*") String fields) {
        //从缓存中获取分类对象
        Map<String, String> categoryOfCache = getOneCategoryById(id);
        //将分类对象根据请求的fields字段返回
        Map<String, String> category = categoryService.getOneCategoryById(categoryOfCache, fields);
        return new ApiResponse.ApiResponseBuilder().code(200).message("CategoryRequest categoryRequest").data(category).build();
    }

    /**
     * 如果该接口频繁调用，则考虑加缓存，可以根据对象编号缓存全量对象内容
     *
     * @param id
     * @return
     */
    public Map<String, String> getOneCategoryById(Integer id) {
        //获取对象全量Fields内容
        String fields = "id, sort_name, sort_name_en, sort_ico, sort_ico_big, sort_url, parent_id, " +
                "platform,sort_level, sort_code, content_code, is_hot, is_new, separative_sign, order_by, " +
                "status, create_time, update_time, sort_name_color";
        //获取对象所有内容
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setId(id);
        categoryRequest.setFields(fields);
        Map<String, String> category = serviceCaller.call(ResourcesServices.getOneCategory, categoryRequest, Map.class);
        return category;
    }
}
