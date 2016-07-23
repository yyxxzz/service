package com.yoho.gateway.controller.order.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.product.model.*;
import com.yoho.product.request.BatchBaseRequest;
import com.yoho.service.model.order.OrderServices;
import com.yoho.service.model.order.model.PromotionBO;
import com.yoho.service.model.order.request.ShoppingCartRequest;
import com.yoho.service.model.order.response.shopping.ShoppingGoods;
import com.yoho.service.model.order.response.shopping.ShoppingQueryResponse;
import com.yoho.service.model.order.response.shopping.ShoppingQueryResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class WebShoppingController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ServiceCaller serviceCaller;


    /**
     * 查询购物车
     *
     * @return
     */
    @RequestMapping(params = "method=web.Shopping.cart")
    @ResponseBody
    public ApiResponse cart(HttpServletRequest httpServletRequest,
                            @RequestParam(value = "sale_channel", required = false) String saleChannel,
                            @RequestParam(value = "uid", required = false) Integer uid,
                            @RequestParam(value = "shopping_key", required = false) String shoppingKey) {
        logger.info("shopping cart begin. uid:{} channel:{} shoppingKey:{}", uid, saleChannel, shoppingKey);
        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setSale_channel(saleChannel);
        request.setUid(uid);
        request.setShopping_key(shoppingKey);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingQueryResponse response = serviceCaller.call(OrderServices.cartShopping, request, ShoppingQueryResponse.class);
        JSONObject cartVO = createCartVO(response);
        return new ApiResponse.ApiResponseBuilder().data(cartVO).code(200).message("cart goods list.").build();
    }

    private JSONObject createCartVO(ShoppingQueryResponse response) {
        if (response == null) {
            return new JSONObject();
        }
        JSONObject cartVO = new JSONObject();
        parseOrdinary_cart_data(response.getOrdinary_cart_data(), cartVO);
        parseAdvance_cart_data(response.getAdvance_cart_data(), cartVO);
        // TODO 符合免费购物车条件
        //cartVO.put("fit_free_shipping", response.getOrdinary_cart_data().getShopping_cart_data().get);
        parsePromotions(response.getOrdinary_cart_data(), cartVO);
        return cartVO;
    }

    private void parseOrdinary_cart_data(ShoppingQueryResult ordinary_cart_data, JSONObject cartVO) {
        if (ordinary_cart_data == null || CollectionUtils.isEmpty(ordinary_cart_data.getGoods_list())) {
            return;
        }
        JSONArray main_goods = new JSONArray();
        JSONArray outlet_goods = new JSONArray();
        JSONArray gift_goods = new JSONArray();
        JSONArray need_pay_gifts = new JSONArray();
        for (ShoppingGoods goods : ordinary_cart_data.getGoods_list()) {
            JSONObject tmp = makeProductInfo(goods);
            if ("gift".equals(goods.getGoods_type())) {
                gift_goods.add(tmp);
            } else if ("price_gift".equals(goods.getGoods_type())) {
                need_pay_gifts.add(tmp);
            } else if ("outlet".equals(goods.getGoods_type())) {
                outlet_goods.add(tmp);
            } else {
                main_goods.add(tmp);
            }
        }
        cartVO.put("main_goods", main_goods);
        cartVO.put("outlet_goods", outlet_goods);
        cartVO.put("gift_goods", gift_goods);
        cartVO.put("need_pay_gifts", need_pay_gifts);
    }

    private void parseAdvance_cart_data(ShoppingQueryResult advance_cart_data, JSONObject cartVO) {
        if (advance_cart_data == null || CollectionUtils.isEmpty(advance_cart_data.getGoods_list())) {
            return;
        }
        JSONArray advance_goods = new JSONArray();
        for (ShoppingGoods goods : advance_cart_data.getGoods_list()) {
            JSONObject tmp = makeProductInfo(goods);
            advance_goods.add(tmp);
        }
        cartVO.put("advance_goods", advance_goods);
    }

    // 创建商品信息
    private JSONObject makeProductInfo(ShoppingGoods goods) {
        JSONObject tmp = new JSONObject();
        if (StringUtils.isNotEmpty(goods.getGoods_id())) {
//            tmp.put("product_url", makeProductUrl(goods.getProduct_id(), Integer.valueOf(goods.getGoods_id()), goods.getCn_alphabet()));
        } else {
            tmp.put("product_url", "");
        }
        tmp.put("is_advance", goods.getIs_advance());
        tmp.put("default_img", goods.getGoods_images());
        tmp.put("product_name", goods.getProduct_name());
        tmp.put("color_name", goods.getColor_name());
        tmp.put("size_name", goods.getSize_name());
        tmp.put("is_cheapest_free", goods.getIs_cheapest_free());
        if (goods.getVip_price() == null) {
            tmp.put("show_price", goods.getSale_price());
        } else {
            tmp.put("show_price", goods.getVip_price());
        }
        tmp.put("buy_number", goods.getBuy_number());
        tmp.put("goods_incart_id", goods.getShopping_cart_goods_id());
        tmp.put("activities_id", goods.getActivities_id());
        return tmp;
    }

    // 创建促销信息
    private void parsePromotions(ShoppingQueryResult ordinary_cart_data, JSONObject cartVO) {
        if (ordinary_cart_data == null || CollectionUtils.isEmpty(ordinary_cart_data.getPromotion_info())) {
            cartVO.put("has_promotion", 0);
            return;
        }
        cartVO.put("has_promotion", 1);
        // TODO 符合OUTLET促销条件
        //cartVO.put("fit_outlet_promotion",response.getOrdinary_cart_data().getPromotion_info());
        // 设置第一个促销信息
        List<PromotionBO> promotionBOs = ordinary_cart_data.getPromotion_info();
        JSONObject first_promotions = new JSONObject();
        PromotionBO firstPromotion = promotionBOs.get(0);
        first_promotions.put("promotion_id", firstPromotion.getPromotion_id());
        first_promotions.put("promotion_title", firstPromotion.getPromotion_title());
        cartVO.put("first_promotions", first_promotions);
        cartVO.put("has_first_promotion", 1);
        // 设置其他促销信息
        JSONArray other_promotions = new JSONArray();
        for (int i = 1, size = promotionBOs.size(); i < size; i++) {
            PromotionBO promotion = promotionBOs.get(i);
            JSONObject tmp = new JSONObject();
            tmp.put("promotion_id", promotion.getPromotion_id());
            tmp.put("promotion_title", promotion.getPromotion_title());
            other_promotions.add(tmp);
        }
        cartVO.put("other_promotions", other_promotions);
        cartVO.put("has_other_promotion", other_promotions.isEmpty() ? 0 : 1);
    }

    /**
     * yohoGift
     */
    @RequestMapping(params = "method=web.Shopping.yohoGift")
    @ResponseBody
    public ApiResponse yohoGift(@RequestParam("skn_list") String sknList) {
        List<Integer> skns = JSONArray.parseArray(sknList, Integer.class);
        BatchBaseRequest<Integer> request = new BatchBaseRequest<>();
        request.setParams(skns);
        ProductGiftBo[] productGiftBos = serviceCaller.call("product.queryYohoGiftBySkns", request, ProductGiftBo[].class);
        JSONArray response = createYohoGiftVO(productGiftBos);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).build();
    }

    /**
     * 创建yohogiftVO数据
     *
     * @param productGiftBos
     * @return
     */
    private JSONArray createYohoGiftVO(ProductGiftBo[] productGiftBos) {
        JSONArray response = new JSONArray();
        for (ProductGiftBo productGiftBo : productGiftBos) {
            // 设置商品信息
            ProductBo productBo = productGiftBo.getProductBo();
            JSONObject productGift = new JSONObject();
            productGift.put("id", productBo.getId());
            productGift.put("product_name", productBo.getProductName());
            // 设置商品价格信息
            ProductPriceBo productPriceBo = productGiftBo.getProductPriceBo();
            if (productPriceBo != null) {
                JSONObject price = new JSONObject();
                price.put("sales_price", productPriceBo.getSalesPrice());
                price.put("yoho_coin_num", productPriceBo.getYohoCoinNum());
                productGift.put("price", price);
            }
            // 设置商品颜色信息
            GoodsBo defaultGoods = getDefaultGoods(productBo.getId(), productBo.getGoodsList());
            if (defaultGoods != null) {
                productGift.put("default_pic", defaultGoods.getColorImage());
                productGift.put("url", makeProductUrl(productBo.getId(), defaultGoods.getId(), productBo.getCnAlphabet()));
            }
            // 设置商品库存信息
            productGift.put("storage", getYohoGiftStorageTotalNum(productGiftBo.getStorageBoList()));
            productGift.put("storage_list", getYohoGiftStorageList(productGiftBo.getStorageBoList(), productBo.getGoodsList()));
            response.add(productGift);
        }
        return response;
    }

    private String makeProductUrl(Integer productId, Integer goodsId, String productCnAlphabet) {
        return String.format("http://item.yohobuy.com/product/pro_%s_%s/%s.html", productId, goodsId, productCnAlphabet);
    }

    /**
     * 获取默认商品
     *
     * @param productId
     * @param goodsBos
     * @return
     */
    private GoodsBo getDefaultGoods(Integer productId, List<GoodsBo> goodsBos) {
        if (CollectionUtils.isEmpty(goodsBos)) {
            return null;
        }
        for (GoodsBo goods : goodsBos) {
            if (ObjectUtils.equals(goods.getStatus(), Integer.valueOf(1))
                    && ObjectUtils.equals(goods.getIsDefault(), "Y")
                    && ObjectUtils.equals(goods.getIsDown(), "N")) {
                return goods;
            }
        }
        return goodsBos.get(0);
    }

    /**
     * 获取商品库存总数
     *
     * @param storageBoList
     * @return
     */
    private int getYohoGiftStorageTotalNum(List<StorageBo> storageBoList) {
        int totalNum = 0;
        if (CollectionUtils.isEmpty(storageBoList)) {
            return totalNum;
        } else {
            for (StorageBo storageBo : storageBoList) {
                totalNum += storageBo.getStorageNum();
            }
            return totalNum;
        }
    }

    /**
     * 获取商品库存信息
     *
     * @param storageBoList
     * @return
     */
    private JSONArray getYohoGiftStorageList(List<StorageBo> storageBoList, List<GoodsBo> goodsList) {
        JSONArray res = new JSONArray();
        if (CollectionUtils.isEmpty(storageBoList)) {
            return res;
        } else {
            for (StorageBo storageBo : storageBoList) {
                JSONObject s = new JSONObject();
                s.put("storage_num", storageBo.getStorageNum());
                s.put("goods_dimension_id", storageBo.getGoodsDimensionId());
                s.put("erp_sku_id", storageBo.getErpSkuId());
                s.put("product_id", storageBo.getProductId());
                s.put("goods_id", storageBo.getGoodsId());
                GoodsSizeBo goodsSizeBo = getGoodsSizeByGoodsIdAndGoodsSizeId(storageBo.getGoodsId(), storageBo.getGoodsDimensionId(), goodsList);
                if (goodsSizeBo != null) {
                    JSONObject size = new JSONObject();
                    size.put("size_name", goodsSizeBo.getSizeName());
                    s.put("size", size);
                }
                res.add(s);
            }
            return res;
        }
    }


    private GoodsSizeBo getGoodsSizeByGoodsIdAndGoodsSizeId(Integer goodsId, Integer goodsSizeId, List<GoodsBo> goodsList) {
        if (CollectionUtils.isEmpty(goodsList)) {
            return null;
        }
        GoodsBo goodsBo = getGoodsById(goodsId, goodsList);
        if (goodsBo != null) {
            return getGoodsSizeByGoodsSizeId(goodsSizeId, goodsBo.getGoodsSizeBoList());
        } else {
            return null;
        }
    }

    private GoodsBo getGoodsById(Integer id, List<GoodsBo> goodsList) {
        if (CollectionUtils.isEmpty(goodsList)) {
            return null;
        }
        for (GoodsBo goods : goodsList) {
            if (ObjectUtils.equals(id, goods.getId())) {
                return goods;
            }
        }
        return null;
    }

    private GoodsSizeBo getGoodsSizeByGoodsSizeId(Integer goodsSizeId, List<GoodsSizeBo> goodsSizeBoList) {
        if (CollectionUtils.isEmpty(goodsSizeBoList)) {
            return null;
        }
        for (GoodsSizeBo goodsSize : goodsSizeBoList) {
            if (ObjectUtils.equals(goodsSizeId, goodsSize.getId())) {
                return goodsSize;
            }
        }
        return null;
    }

}