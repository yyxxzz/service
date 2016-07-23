package com.yoho.gateway.controller.product.builder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yoho.gateway.utils.ProductDetailHtmlUtil;
import com.yoho.gateway.utils.StringUtils;
import com.yoho.product.model.FitModelBo;
import com.yoho.product.model.ModelBo;
import com.yoho.product.model.ProductBo;
import com.yoho.product.model.ProductDescBo;
import com.yoho.product.model.ProductExtraBo;
import com.yoho.product.model.ProductInfoBo;
import com.yoho.product.model.ProductIntroBo;
import com.yoho.product.model.ProductMaterialBo;
import com.yoho.product.model.ReferenceSizeBo;
import com.yoho.product.model.SizeAttributeBo;
import com.yoho.product.model.SizeBo;
import com.yoho.product.model.SizeInfoBo;
import com.yoho.product.model.SortAttributeBo;
import com.yoho.product.model.StandardBo;
import com.yoho.product.model.WashTipsBo;

/**
 * Created by sailing on 2016/3/4.
 */
@Service
public class ProductModelBuilder {

    private final Logger logger = LoggerFactory.getLogger(ProductModelBuilder.class);
    private static final String LIST_KEY_PARAM = "param";
    private static final String KEY_GLOAB_PARAMS = "params";
    private static final String KEY_LIST = "list";

    private static final String referSizeDefault = "*";
    @Value("${h5.version}")
    private String h5Version;
    @Value("${gateway.domain.url}")
    private String gateWayDomain;
    /**
     *
     * @param productInfoBo
     * @return
     */
    public ModelMap buildPrdDesModel(final ProductInfoBo productInfoBo,
                                      Integer channel, String gender) {
        ModelMap model = new ModelMap();
        // goodsDescription
        ProductDescBo productDescBo = productInfoBo.getProductDescBo();
        Map<String, Object> goodsDescription = buildProductDesc(productDescBo);
        model.addAttribute("goodsDescription", goodsDescription);
        // 尺码信息
        SizeInfoBo sizeInfoBo = productInfoBo.getSizeInfoBo();
        if (sizeInfoBo != null) {
            Map<String, Object> sizeInfo = buildSizeInfo(sizeInfoBo,
                    productInfoBo.getProductExtra(), productDescBo.getGender());
            model.addAttribute("sizeInfo", sizeInfo);
        }

        // measurementMethod 测量方式
        String sizeImage = productInfoBo.getSizeImage();
        if (StringUtils.isNotBlank(sizeImage)) {
            Map<String, Object> measurementMethod = buildMeasurementMethod(sizeImage);
            model.addAttribute("measurementMethod", measurementMethod);
        }
        // 模特
        List<ModelBo> modelBos = productInfoBo.getModelBos();
        if (CollectionUtils.isNotEmpty(modelBos)) {
            model.addAttribute("reference", buildReference(modelBos));
        }
        // 材质
        List<ProductMaterialBo> productMaterialBos = productInfoBo
                .getProductMaterialList();
        if (CollectionUtils.isNotEmpty(productMaterialBos)) {
            model.addAttribute("materials", buildMaterials(productMaterialBos));
        }
        //
        List<WashTipsBo> washTipsBoList = productInfoBo.getWashTipsBoList();
        if (CollectionUtils.isNotEmpty(washTipsBoList)) {
            model.addAttribute("washTips", buildWashTips(washTipsBoList));
        }
        // 商品详情
        ProductIntroBo productIntroBo = productInfoBo.getProductIntroBo();
        if (null != productIntroBo) {
            model.addAttribute("productDetail",
                    buildProductDetail(productIntroBo.getProductIntro()));
        }
        model.addAttribute("version", h5Version);
        // 为你优选
        ProductBo productBo = productInfoBo.getProductBo();
        if (productBo != null) {
            model.addAttribute("preferenceUrl",
                    buildYourPreferUrl(productBo, channel, gender));
        } else {
            logger.warn("no product info return");
        }
        return model;
    }


    /**
     * 商品信息
     *
     * @param productDescBo
     * @return
     */
    private Map<String, Object> buildProductDesc(ProductDescBo productDescBo) {
        Map<String, Object> goodsDescription = new HashMap<String, Object>();
        goodsDescription.put("title", "商品信息");
        goodsDescription.put("enTitle", "PRODUCT INFO");
        if (productDescBo != null) {
            // nessarry
            Map<String, Object> detail = new HashMap<String, Object>();
            List<Map<String, Object>> detailList = new LinkedList<Map<String, Object>>();
            // 编号
            String prdId = productDescBo.getErpProductId();
            if (StringUtils.isNotBlank(prdId)) {
                Map<String, Object> erpProductId = new HashMap<String, Object>();
                erpProductId.put(LIST_KEY_PARAM, "编号：" + prdId);
                detailList.add(erpProductId);
            }

            // 颜色
            String iColorName = productDescBo.getColorName();
            if (StringUtils.isNotBlank(iColorName)) {
                Map<String, Object> colorName = new HashMap<String, Object>();
                colorName.put(LIST_KEY_PARAM, "颜色：" + iColorName);
                detailList.add(colorName);
            }
            // 性别
            Integer gender = productDescBo.getGender();
            String sex = "通用";
            if (gender != null) {
                switch (gender) {
                    case 1:
                        sex = "男款";
                        break;
                    case 2:
                        sex = "女款";
                        break;
                }
            }
            Map<String, Object> sexName = new HashMap<String, Object>();
            sexName.put(LIST_KEY_PARAM, "性别：" + sex);
            detailList.add(sexName);
            // more
            List<StandardBo> standardBos = productDescBo.getStandardBos();
            if (CollectionUtils.isNotEmpty(standardBos)) {
                for (StandardBo sb : standardBos) {
                    Map<String, Object> standard = new HashMap<String, Object>();
                    String value = sb.getStandardName() + "："
                            + sb.getStandardVal();
                    standard.put(LIST_KEY_PARAM, value);
                    detailList.add(standard);
                }
            }
            // set
            detail.put("list", detailList);
            goodsDescription.put("detail", detail);
        }
        String desc = productDescBo.getPhrase();
        if (StringUtils.isNotBlank(desc)) {
            goodsDescription.put("desc", desc);
        }
        return goodsDescription;
    }


    /**
     * 这里所做的一切工作是组装一个表格； 表格的每一列使用 LinkedList 存储键值对Map，保持行顺序 整张表也使用 LinkedList
     * 保持列的顺序
     *
     * @param sizeInfoBo
     * @return
     */
    private Map<String, Object> buildSizeInfo(SizeInfoBo sizeInfoBo,
                                              ProductExtraBo productExtra, Integer gender) {
        Map<String, Object> sizeInfo = new HashMap<String, Object>();
        sizeInfo.put("title", "尺码信息");
        sizeInfo.put("enTitle", "SIZE INFO");
        String size_list_param = LIST_KEY_PARAM;
        if (null != sizeInfoBo) {
			/* 1.构建表格所需要的每一列 */
            Map<String, Object> detail = new HashMap<String, Object>();
            List<SizeAttributeBo> sizeAttributeBos = sizeInfoBo
                    .getSizeAttributeBos();
            // 1.1 第一列 第一行
            Map<String, Object> defaultMap_1_1 = buildParam(size_list_param,
                    "吊牌尺码");
            // 第一列链表
            List<Map<String, Object>> column_1 = new LinkedList<Map<String, Object>>();
            column_1.add(defaultMap_1_1);
            // 1.2 可选的第二列或第三列
            boolean referenceSizeIsShow = false;
            // 第二列
            List<Map<String, Object>> referenceSizeColumn2 = Lists
                    .newLinkedList();
            // 第三列
            List<Map<String, Object>> referenceSizeColumn3 = Lists
                    .newLinkedList();
            RefSizeFlags refSizeFlags = initRefSizeFlags(productExtra, gender);
            initReferenceSizeColumn(refSizeFlags, size_list_param,
                    referenceSizeColumn2, referenceSizeColumn3);
            if (CollectionUtils.isNotEmpty(referenceSizeColumn2)) {
                referenceSizeIsShow = true;
            }

			/*
			 * 1.3.从第二列或第三列开始 表格第一行
			 */
            Map<Integer, List<Map<String, Object>>> saBoMap = new LinkedHashMap<Integer, List<Map<String, Object>>>();
            initSizeAttributeMap(sizeAttributeBos, size_list_param, saBoMap);
			/*
			 * 1.4 其余部分，依赖1.1 1.2 和 1.3的对象
			 */
            List<SizeBo> sizeBos = sizeInfoBo.getSizeBoList();
            if (CollectionUtils.isEmpty(sizeBos)) {
                logger.warn("sizeInfoBo.sizeBoList is :{}", sizeBos);
                return Maps.newHashMap();
            }
            int referSizeNullCount = 0;
            for (SizeBo sizeBo : sizeBos) {
                // 只是补充第一列的行，从第二行开始，至尾行；数据在数据库里取出来时已经排序了
                String sizeName = sizeBo.getSizeName();
                Map<String, Object> sizeNameMap = new LinkedHashMap<String, Object>();
                sizeNameMap.put(size_list_param, sizeName);
                column_1.add(sizeNameMap);
                // 第二列或第三列开始补充至尾列，每列补充第二行至尾行
                fillOtherColumn(sizeBo, size_list_param, saBoMap);
                if (referenceSizeIsShow) {
                    // 可选的第二列的第二行开始
                    fillReferSizeColumn(sizeBo, referenceSizeColumn2,
                            referenceSizeColumn3, refSizeFlags, size_list_param);
                    if (referSizeIsNull(sizeBo)) {
                        referSizeNullCount++;
                    }
                }
            }
            // 参考尺码开关：关闭
            if (referSizeNullCount == sizeBos.size()) {
                referenceSizeIsShow = false;
            }
            boolean column2IsEmpty = judgeColIsEmpty(referenceSizeColumn2,
                    size_list_param);
            if (column2IsEmpty) {
                referenceSizeIsShow = false;
            }

			/*
			 * 2.用列组装表格
			 */
            // 使用链表保持顺序
            List<Map<String, List>> detailList = new LinkedList<Map<String, List>>();
            // 第一列
            String key_params = "params";
            detailList.add(buildParams(key_params, column_1));
            // 第二列 也许还有第三列
            if (referenceSizeIsShow) {
                // 第二列
                if (CollectionUtils.isNotEmpty(referenceSizeColumn2)) {
                    detailList.add(buildParams(key_params, referenceSizeColumn2));
                }
                boolean col3IsEmpty = judgeColIsEmpty(referenceSizeColumn3,
                        size_list_param);
                // 第3列
                if (CollectionUtils.isNotEmpty(referenceSizeColumn3)
                        && !col3IsEmpty) {
                    detailList.add(buildParams(key_params, referenceSizeColumn3));
                }
            }

            // 从第二列或第三列或第四列 开始的之后所有列
            for (List list : saBoMap.values()) {
                detailList.add(buildParams(key_params, list));
            }
            detail.put("list", detailList);
            sizeInfo.put("detail", detail);
        }
        return sizeInfo;
    }
    /**
     * 商品详情
     *
     * @param productIntro
     * @return
     */
    private Map<String, Object> buildProductDetail(String productIntro) {
        Map<String, Object> productDetail = new HashMap<String, Object>();
        productDetail.put("title", "商品详情");
        productDetail.put("enTitle", "DETAILS");
        productDetail.put("desc", fixHtml(productIntro));
        return productDetail;
    }

    private String fixHtml(String infoWithHtml) {
        String result = "";
        if (StringUtils.isBlank(infoWithHtml)) {
            return result;
        }
        result = ProductDetailHtmlUtil.fixHtml(infoWithHtml);
        return result;
    }

    /**
     * 计算是否全为默认，全部时视为空
     *
     * @param referenceSizeColumn
     * @param size_list_param
     * @return
     */
    private boolean judgeColIsEmpty(
            List<Map<String, Object>> referenceSizeColumn,
            String size_list_param) {
        int referSizeColumn3Size = referenceSizeColumn.size();
        int column3NullNum = countHowMuchDefault(referenceSizeColumn,
                size_list_param);
        return (referSizeColumn3Size - 1 == column3NullNum);
    }

    private int countHowMuchDefault(
            List<Map<String, Object>> referenceSizeColumn, String key) {
        int count = 0;
        if (CollectionUtils.isNotEmpty(referenceSizeColumn)) {
            for (Map<String, Object> map : referenceSizeColumn) {
                Object obj = map.get(key);
                boolean isStr = obj instanceof String;
                if (isStr && referSizeDefault.equals(obj)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * actually just change the key name of map
     *
     * @param washTipsBos
     * @return
     */
    private Map<String, List> buildWashTips(List<WashTipsBo> washTipsBos) {
        // Map<String,Object> washTips = new HashMap<String,Object>();
        List<WashTipsBo> list = new ArrayList<WashTipsBo>(washTipsBos);
        Map<String, List> washTips = new HashMap<String, List>();
        washTips.put(KEY_LIST, list);
        return washTips;
    }

    /**
     * 模特试穿
     *
     * @param modelBos
     * @return
     */
    private Map<String, Object> buildReference(List<ModelBo> modelBos) {
        String key_params = KEY_GLOAB_PARAMS;
        String key_param = LIST_KEY_PARAM;
        Map<String, Object> reference = new HashMap<String, Object>();
        reference.put("title", "模特试穿");
        reference.put("enTitle", "REFERENCE");
        // todo add elements
        List<Map<String, List<Map>>> list = new LinkedList<Map<String, List<Map>>>();
        String[] titles = new String[] { "", "模特", "身高", "体重", "三围", "吊牌尺码",
                "试穿描述" };
        for (int i = 0; i < titles.length; i++) {
            List<Map> paramList = new LinkedList<Map>();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put(key_param, titles[i]);
            paramList.add(paramMap);
            Map<String, List<Map>> paramsMap = new LinkedHashMap<String, List<Map>>();
            paramsMap.put(key_params, paramList);
            list.add(paramsMap);
        }
        boolean fitRemarkIsShow = false;
        List<Map<String, Object>> feelRemarkList = new LinkedList<Map<String, Object>>();
        for (ModelBo modelBo : modelBos) {
            // 'avatar'
            Map<String, Object> paramMap_avatar = new HashMap<>();
            paramMap_avatar.put(key_param, modelBo.getAvatar());
            list.get(0).get(key_params).add(paramMap_avatar);
            // 'modelName'
            Map<String, Object> paramMap_modelName = new HashMap<>();
            paramMap_modelName.put(key_param, modelBo.getModelName());
            list.get(1).get(key_params).add(paramMap_modelName);
            // 'height'
            Map<String, Object> paramMap_height = new HashMap<>();
            paramMap_height.put(key_param, modelBo.getHeight());
            list.get(2).get(key_params).add(paramMap_height);
            // 'weight'
            Map<String, Object> paramMap_weight = new HashMap<>();
            paramMap_weight.put(key_param, modelBo.getWeight());
            list.get(3).get(key_params).add(paramMap_weight);
            // 'vitalStatistics'
            Map<String, Object> paramMap_vitalStatistics = new HashMap<>();
            paramMap_vitalStatistics.put(key_param,
                    modelBo.getVitalStatistics());
            list.get(4).get(key_params).add(paramMap_vitalStatistics);
            FitModelBo fitModelBo = modelBo.getFitModelBo();
            String fitRemark = "";
            if (null != fitModelBo) {
                // 'fitModelBo' 'fit_size'
                Map<String, Object> paramMap_fitModelBo = new HashMap<>();
                paramMap_fitModelBo.put(key_param, fitModelBo.getFit_size());
                list.get(5).get(key_params).add(paramMap_fitModelBo);
                // 'fitModelBo' 'feel'
                Map<String, Object> paramMap_ = new HashMap<>();
                paramMap_.put(key_param, fitModelBo.getFeel());
                list.get(6).get(key_params).add(paramMap_);
                fitRemark = fitModelBo.getFit_remark();
                if (StringUtils.isNotBlank(fitRemark) && !fitRemarkIsShow) {
                    fitRemarkIsShow = true;
                }
            }
            Map<String, Object> paramMap_fitRemark = new HashMap<String, Object>();
            paramMap_fitRemark.put(key_param, fitRemark);
            feelRemarkList.add(paramMap_fitRemark);
        }
        // 补全
        if (fitRemarkIsShow) {
            List<Map> paramList = new LinkedList<Map>();
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put(key_param, "备注");
            paramList.add(paramMap);
            paramList.addAll(1, feelRemarkList);
            Map<String, List<Map>> paramsMap = new LinkedHashMap<String, List<Map>>();
            paramsMap.put(key_params, paramList);
            list.add(7, paramsMap);
        }
        Map<String, List> listMap = new HashMap<String, List>();
        listMap.put("list", list);
        reference.put("detail", listMap);
        return reference;
    }

    /**
     * 测量方式
     *
     * @param sizeImage
     * @return
     */
    private Map<String, Object> buildMeasurementMethod(String sizeImage) {
        Map<String, Object> measurementMethod = new HashMap<String, Object>();
        measurementMethod.put("title", "测量方式");
        measurementMethod.put("enTitle", "MEASUREMENT METHOD");
        measurementMethod.put("img", sizeImage);
        return measurementMethod;
    }

    /**
     *
     * @param productMaterialBos
     * @return
     */
    private Map<String, Object> buildMaterials(
            List<ProductMaterialBo> productMaterialBos) {
        Map<String, Object> materials = new HashMap<String, Object>();
        materials.put("title", "商品材质");
        materials.put("enTitle", "MATERIALS");
        List<Map> list = new LinkedList<Map>();
        for (ProductMaterialBo productMaterialBo : productMaterialBos) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("img", productMaterialBo.getImageUrl());
            map.put("desc", productMaterialBo.getRemark());
            list.add(map);
        }
        materials.put("list", list);
        return materials;
    }

    /**
     *
     * @param sizeAttributeBos
     * @param size_list_param
     * @param saBoMap
     */
    private void initSizeAttributeMap(
            final List<SizeAttributeBo> sizeAttributeBos,
            final String size_list_param,
            final Map<Integer, List<Map<String, Object>>> saBoMap) {
        if (CollectionUtils.isEmpty(sizeAttributeBos)) {
            return;
        }
        for (SizeAttributeBo saBo : sizeAttributeBos) {
            // 非第一列的链表
            List<Map<String, Object>> otherParams = new LinkedList<Map<String, Object>>();
            // 从第二列或第三列开始的第一行，至尾列
            Map<String, Object> other = buildParam(size_list_param,
                    saBo.getAttributeName());
            otherParams.add(other);
            saBoMap.put(saBo.getId(), otherParams);
        }
    }


    /**
     *
     * @param sizeBo
     * @param size_list_param
     * @param saBoMap
     */
    private void fillOtherColumn(final SizeBo sizeBo,
                                 final String size_list_param,
                                 final Map<Integer, List<Map<String, Object>>> saBoMap) {
        if (sizeBo == null
                || CollectionUtils.isEmpty(sizeBo.getSortAttributes())) {
            return;
        }
        for (SortAttributeBo sortAttributeBo : sizeBo.getSortAttributes()) {
            Map<String, Object> map = new HashMap<String, Object>();
            Integer sortAttrId = sortAttributeBo.getId();
            map.put(size_list_param, sortAttributeBo.getSizeValue());
            saBoMap.get(sortAttrId).add(map);
        }
    }

    /**
     *
     * @param sizeBo
     * @return
     */
    private boolean referSizeIsNull(final SizeBo sizeBo) {
        ReferenceSizeBo referenceSizeBo_boy = sizeBo.getBoyReferSize();
        ReferenceSizeBo referenceSizeBo_girl = sizeBo.getGirlReferSize();
        return referenceSizeBo_boy == null && referenceSizeBo_girl == null;
    }

    /**
     *
     * @param sizeBo
     * @param referenceSizeColumn2
     * @param referenceSizeColumn3
     * @param refSizeFlags
     * @param size_list_param
     */
    private void fillReferSizeColumn(final SizeBo sizeBo,
                                     final List<Map<String, Object>> referenceSizeColumn2,
                                     final List<Map<String, Object>> referenceSizeColumn3,
                                     RefSizeFlags refSizeFlags, String size_list_param) {
        ReferenceSizeBo referenceSizeBo_boy = sizeBo.getBoyReferSize();
        if (refSizeFlags.isCommonBoy() || refSizeFlags.isBoy()) {
            String referenceName_boy = getReferSizeName(referenceSizeBo_boy);
            Map<String, Object> referenceSize_2 = buildParam(size_list_param,
                    referenceName_boy);
            referenceSizeColumn2.add(referenceSize_2);
        }
        //
        ReferenceSizeBo referenceSizeBo_girl = sizeBo.getGirlReferSize();
        String referenceName_girl = getReferSizeName(referenceSizeBo_girl);
        Map<String, Object> referenceSize_2 = buildParam(size_list_param,
                referenceName_girl);
        if (refSizeFlags.isCommonGirl()) {
            if (refSizeFlags.isMultColumn()) {
                referenceSizeColumn3.add(referenceSize_2);
            } else {
                referenceSizeColumn2.add(referenceSize_2);
            }

        }
        if (refSizeFlags.isGirl()) {
            referenceSizeColumn2.add(referenceSize_2);
        }

    }


    private RefSizeFlags initRefSizeFlags(final ProductExtraBo productExtra,
                                          final Integer gender) {
        RefSizeFlags refSizeFlags = new RefSizeFlags();
        Integer boyRef = productExtra.getBoyReference();
        boolean boyReference = boyRef == null ? false : boyRef == 1;
        Integer girlRef = productExtra.getGirlReference();
        boolean girlReference = girlRef == null ? false : girlRef == 1;
        if (gender == 1 && boyReference) {
            refSizeFlags.setIsBoy(true);
        } else if (gender == 2 && girlReference) {
            refSizeFlags.setIsGirl(true);
        } else {
            boolean column2IsExist = false;
            if (gender == 3 && boyReference) {
                refSizeFlags.setIsCommonBoy(true);
                column2IsExist = true;
            }
            if (gender == 3 && girlReference) {
                refSizeFlags.setIsCommonGirl(true);
                if (column2IsExist) {
                    refSizeFlags.setIsMultColumn(true);
                }
            }
        }
        return refSizeFlags;
    }

    /**
     *
     * @param refSizeFlags
     * @param size_list_param
     * @param referenceSizeColumn2
     * @param referenceSizeColumn3
     */
    private void initReferenceSizeColumn(final RefSizeFlags refSizeFlags,
                                         final String size_list_param,
                                         final List<Map<String, Object>> referenceSizeColumn2,
                                         final List<Map<String, Object>> referenceSizeColumn3) {
        if (refSizeFlags == null) {
            return;
        }
        String referenceName = "";
        if (refSizeFlags.isBoy() || refSizeFlags.isGirl()) {
            referenceName = "参考尺码";
            // 第二列 第一行
            Map<String, Object> referenceSize_2_1 = buildParam(size_list_param,
                    referenceName);
            referenceSizeColumn2.add(referenceSize_2_1);
        } else {
            boolean showColumn3 = false;
            if (refSizeFlags.isCommonBoy()) {
                String referenceName_boy = "参考尺码(男)";
                Map<String, Object> referenceSize_2_1 = buildParam(
                        size_list_param, referenceName_boy);
                referenceSizeColumn2.add(referenceSize_2_1);
                showColumn3 = true;
            }
            if (refSizeFlags.isCommonGirl()) {
                String referenceName_girl = "参考尺码(女)";
                Map<String, Object> referenceSize_3_1 = buildParam(
                        size_list_param, referenceName_girl);
                if (showColumn3) {
                    referenceSizeColumn3.add(referenceSize_3_1);
                } else {
                    referenceSizeColumn2.add(referenceSize_3_1);
                }

            }
        }
    }

    private String getReferSizeName(ReferenceSizeBo referenceSizeBo) {
        String referenceName = referSizeDefault;
        if (referenceSizeBo != null) {
            if (StringUtils.isNotBlank(referenceSizeBo.getReferenceName())) {
                referenceName = referenceSizeBo.getReferenceName();
            }
        }
        return referenceName;
    }
    /**
     *
     * @param key
     * @param value
     * @return
     */
    private Map<String, Object> buildParam(String key, Object value) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(key, value);
        return param;
    }

    private Map<String, List> buildParams(String key, List value) {
        Map<String, List> params = new LinkedHashMap<String, List>();
        params.put(key, value);
        return params;
    }

    /**
     * 构建为你优选的URL
     *
     * @param productBo
     * @return
     */
    private String buildYourPreferUrl(ProductBo productBo, Integer yhchannel,
                                      String gender) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("method", "app.product.preference");
        paramsMap.put("yhchannel", yhchannel);
        paramsMap.put("gender", gender);
        paramsMap.put("v", 7);
        if (productBo != null) {
            paramsMap.put("brandId", productBo.getBrandId());
        } else {
            logger.warn("no product info return");
        }
        String params = StringUtils.convertUrlParamStrFromMap2(paramsMap);
        String yourPreferUrl = new StringBuilder(gateWayDomain).append("/?")
                .append(params).toString();
        return yourPreferUrl;
    }

    private class RefSizeFlags {
        boolean isCommonGirl;
        boolean isCommonBoy;
        boolean isBoy;
        boolean isGirl;
        boolean isMultColumn;

        public boolean isCommonGirl() {
            return isCommonGirl;
        }

        public void setIsCommonGirl(boolean isCommonGirl) {
            this.isCommonGirl = isCommonGirl;
        }

        public boolean isCommonBoy() {
            return isCommonBoy;
        }

        public void setIsCommonBoy(boolean isCommonBoy) {
            this.isCommonBoy = isCommonBoy;
        }

        public boolean isBoy() {
            return isBoy;
        }

        public void setIsBoy(boolean isBoy) {
            this.isBoy = isBoy;
        }

        public boolean isGirl() {
            return isGirl;
        }

        public void setIsGirl(boolean isGirl) {
            this.isGirl = isGirl;
        }

        public boolean isMultColumn() {
            return isMultColumn;
        }

        public void setIsMultColumn(boolean isMultColumn) {
            this.isMultColumn = isMultColumn;
        }
    }
}
