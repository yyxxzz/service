package com.yoho.gateway.controller.product.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.yoho.gateway.utils.ProductDetailHtmlUtil;
import com.yoho.gateway.utils.StringUtils;
import com.yoho.product.constants.LimitProductAttachType;
import com.yoho.product.model.LimitProductAttachBo;
import com.yoho.product.model.LimitProductBo;

/**
 * Created by sailing on 2016/3/4.
 */
@Service
public final class LimitProductModelBuilder {

    @Value("${limitProduct.desc.version}")
    private String limitPrdDescVersion;

    public ModelMap buildLimitPrdModelMap(LimitProductBo limitProductBo) {
        ModelMap model = new ModelMap();
        model.addAttribute("version",limitPrdDescVersion);
        if (limitProductBo == null) {
            return model;
        }
        List<LimitProductAttachBo> limitProductAttachBos = limitProductBo.getAttachment();
        if (CollectionUtils.isEmpty(limitProductAttachBos)){
            return model;
        }
        Comparator<LimitProductAttachBo> comparator = (LimitProductAttachBo o1, LimitProductAttachBo o2) ->{
                if (o1 == null){//o1 is null
                    if (o2==null){
                        return 0;
                    }else{
                        return 1;
                    }
                }else{//o1 is not null
                    if (o2 == null){
                        return -1;
                    }else{
                        return o2.getOrderBy() - o1.getOrderBy();
                    }
                }
        };
        //sort order by OrderBy
        Collections.sort(limitProductAttachBos, comparator);
        List<Map<String,LimitProductAttachBo>> goodDescList = new LinkedList<Map<String,LimitProductAttachBo>>();

        Consumer<LimitProductAttachBo> consumer = limitProductAttachBo -> {
            boolean isDefault = limitProductAttachBo.getIsDefault() == 1;
            int attachType = limitProductAttachBo.getAttachType();
            if(attachType == LimitProductAttachType.PICTURE.getIntVal() ){
                Map<String,LimitProductAttachBo> imgMap = new HashMap<>();
                //{width}x{height}
                String imgUrl = reBuildUrl(limitProductAttachBo.getAttachUrl());
                limitProductAttachBo.setAttachUrl(imgUrl);
                imgMap.put("img",limitProductAttachBo);
                if (!isDefault){
                    goodDescList.add(imgMap);
                }
            }else if(attachType == LimitProductAttachType.VIDEO.getIntVal()){
                Map<String,LimitProductAttachBo> videoMap = new HashMap<>();
                String videoUrl = reBuildVideoUrl(limitProductAttachBo.getAttachUrl());
                limitProductAttachBo.setAttachUrl(videoUrl);
                videoMap.put("video", limitProductAttachBo);
                if (!isDefault){
                    goodDescList.add(videoMap);
                }
            }else if (attachType == LimitProductAttachType.TEXT.getIntVal()){
                Map<String,LimitProductAttachBo> textMap = new HashMap<>();
                textMap.put("text", limitProductAttachBo);
                if (!isDefault){
                    goodDescList.add(textMap);
                }
            }
        };
        limitProductAttachBos.stream().forEach(consumer);
        model.addAttribute("goodDescList", goodDescList);
        return model;
    }

    private static final List<String> imgTypes = Arrays.asList(new String[]{"jpg", "png", "gif"});
    private String reBuildUrl(String rawImgUrl ){
        if (StringUtils.isNotBlank(rawImgUrl)){
            return ProductDetailHtmlUtil.addQuickLoadParams(rawImgUrl, imgTypes);
        }
        return rawImgUrl;
    }
    //mov， avi ，rmvb ，mp4
    private static final List<String> vedioTypes = Arrays.asList(new String[]{"mp4", "avi", "mov", "rmvb"});
    private String reBuildVideoUrl(String rawImgUrl){
        if (StringUtils.isNotBlank(rawImgUrl)){
            return ProductDetailHtmlUtil.removeParamsInUrl(rawImgUrl, vedioTypes);
        }
        return rawImgUrl;
    }

    public static void main(String[] args) {
        String url = "www.asfei.com/ww/wee/erwerfsfsf.mp4?eiwooweifmp4";
        System.out.println(ProductDetailHtmlUtil.removeParamsInUrl(url, vedioTypes));
    }

}
