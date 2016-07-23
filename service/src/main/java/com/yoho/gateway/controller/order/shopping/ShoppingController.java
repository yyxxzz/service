package com.yoho.gateway.controller.order.shopping;

import com.alibaba.fastjson.JSON;
import com.yoho.core.common.utils.MD5;
import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.core.redis.YHValueOperations;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.event.LogEvent;
import com.yoho.gateway.cache.MemecacheClientHolder;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.order.cache.UserOrderCache;
import com.yoho.gateway.interceptor.RemoteIPInterceptor;
import com.yoho.gateway.utils.constants.CacheKeyConstants;
import com.yoho.service.model.order.request.*;
import com.yoho.service.model.order.response.CountBO;
import com.yoho.service.model.order.response.shopping.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by JXWU on 2015/11/27.
 */
@Controller
public class ShoppingController implements ApplicationEventPublisherAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final static String SHOPPING_COUNT_CACHE_PRE_KEY = "yh:order:shopping:count:";

    public final static String SHOPPING_ADD_REST_URL = "order.addShopping";
    public final static String SHOPPING_CART_REST_URL = "order.cartShopping";
    public final static String SHOPPING_INCREASE_REST_URL = "order.increaseShopping";
    public final static String SHOPPING_DECREASE_REST_URL = "order.decreaseShopping";
    public final static String SHOPPING_REMOVE_REST_URL = "order.removeShopping";
    public final static String SHOPPING_SWAP_REST_URL = "order.swapShopping";
    public final static String SHOPPING_PAYMENT_REST_URL = "order.paymentShopping";
    public final static String SHOPPING_COUNT_REST_URL = "order.countShopping";
    public final static String SHOPPING_SELECTED_REST_URL = "order.selectedShopping";
    public final static String SHOPPING_COMPUTE_REST_URL = "order.computeShopping";
    public final static String SHOPPING_USECOUPON_REST_URL = "order.useCouponShopping";
    public final static String SHOPPING_SUBMIT_REST_URL = "order.submitShopping";
    public final static String SHOPPING_ERPSUBMITSTAT_REST_URL = "order.erpSubmitStat";
    public final static String SHOPPING_USEPROMOTIONCODE_REST_URL = "order.usePromotionCode";
    public final static String SHOPPING_READD_REST_URL = "order.readd";

    public final static String SHOPPING_ADDFAVORITE_REST_URL = "order.addfavorite";

    public final static String SHOPPING_SELECTEDANDCART_REST_URL = "order.selectedAndCart";
    public final static String SHOPPING_REMOVEANDCART_REST_URL = "order.removeAndCart";
    public final static String SHOPPING_ADDFAVORITEANDCART_REST_URL = "order.addfavoriteAndCart";

    public final static String SHOPPING_COUNTUSABLECOUPON_REST_URL = "order.countUsableCoupon";

    public final static String SHOPPING_LISTCOUPON_REST_URL ="order.listCoupon";

    public final static String FROM_APP = "APP";

    @Autowired
    private ServiceCaller serviceCaller;

    //publisher
    private ApplicationEventPublisher publisher;

    @Autowired
    private YHValueOperations<String, String> valueOperations;

    @Autowired
    private YHRedisTemplate<String, String> redisTemplate;

    @Autowired
    private MemecacheClientHolder holder;

    @Autowired
    private UserOrderCache userOrderCache;
    /**
     * 添加购物车
     *
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.add")
    @ResponseBody
    public ApiResponse add(HttpServletRequest httpServletRequest,
                           @RequestParam(value = "product_sku", required = true) Integer productSku,
                           @RequestParam(value = "uid", required = false) Integer uid,
                           @RequestParam(value = "buy_number", required = false) Integer buy_number,
                           @RequestParam(value = "shopping_key", required = false) String shoppingKey,
                           @RequestParam(value = "promotion_id", required = false) Integer promotion_id,
                           @RequestParam(value = "selected", required = false) String selected) {
        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setProduct_sku(productSku);
        request.setUid(uid);
        request.setShopping_key(shoppingKey);
        request.setBuy_number(buy_number);
        request.setPromotion_id(promotion_id);
        request.setSelected(selected);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingAddResponse response = serviceCaller.call(SHOPPING_ADD_REST_URL, request, ShoppingAddResponse.class);

        //失效缓存
        deleteCountShoppingCache(uid, shoppingKey);

        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("加入成功").build();
    }


    /**
     * method=app.Shopping.cart&uid=2993932&shopping_key=xxxxx
     * 查询购物车
     *
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.cart")
    @ResponseBody
    public ApiResponse cart(HttpServletRequest httpServletRequest,
                            @RequestParam(value = "sale_channel", required = false) String saleChannel,
                            @RequestParam(value = "uid", required = false) Integer uid,
                            @RequestParam(value = "shopping_key", required = false) String shoppingKey) {
        logger.info("shopping cart begin. uid:{} channel:{} shoppingKey:{}", uid, saleChannel, shoppingKey);

        //失效缓存,查询购物车可能会删除商品,如赠品
        deleteCountShoppingCache(uid, shoppingKey);

        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setSale_channel(saleChannel);
        request.setUid(uid);
        request.setShopping_key(shoppingKey);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingQueryResponse response = serviceCaller.call(SHOPPING_CART_REST_URL, request, ShoppingQueryResponse.class);
        logger.info("shopping cart end, uid:{} ordinary:{}",uid, response.getOrdinary_cart_data().getShopping_cart_data());
        logger.debug("shopping cart end: {}", response);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("cart goods list.").build();
    }


    /**
     * method=app.Shopping.cart&uid=2993932&shopping_key=xxxxx
     * 查询购物车 前端发送购物车信息
     * product_sku_list:[{"sku_id":##,"num"：##,"selected":"(Y/N)"},{}...]
     * 例: [{"sku_id":131874,"num":11,"selected":"Y","promotion_id":0,"product_skn":50015491}]
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.cartByLocal")
    @ResponseBody
    public ApiResponse cartByLocal(HttpServletRequest httpServletRequest,
                            @RequestParam(value = "product_sku_list", required = false) String product_sku_list) {
        logger.info("shopping cartNo begin. product_sku_list:{} ", product_sku_list);

        //失效缓存,查询购物车可能会删除商品,如赠品
       //TODO 缓存?
        // deleteCountShoppingCache(uid, shoppingKey);

        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setProduct_sku_list(product_sku_list);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingQueryResponse response = serviceCaller.call(SHOPPING_CART_REST_URL, request, ShoppingQueryResponse.class);
        logger.info("shopping cart end, ordinary:{}", response.getOrdinary_cart_data().getShopping_cart_data());
        logger.debug("shopping cart end: {}", response);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("cart goods list.").build();
    }


    /**
     * 增加数量
     *
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.increase")
    @ResponseBody
    public ApiResponse increase(HttpServletRequest httpServletRequest,
                                @RequestParam(value = "product_sku", required = true) Integer productSku,
                                @RequestParam(value = "increase_number", required = false) Integer increaseNumber,
                                @RequestParam(value = "uid", required = false) Integer uid,
                                @RequestParam(value = "shopping_key", required = false) String shoppingKey,
                                @RequestParam(value = "promotion_id", required = false) Integer promotion_id) {
        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setProduct_sku(productSku);
        request.setIncrease_number(increaseNumber);
        request.setUid(uid);
        request.setShopping_key(shoppingKey);
        request.setPromotion_id(promotion_id);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingAddResponse response = serviceCaller.call(SHOPPING_INCREASE_REST_URL, request, ShoppingAddResponse.class);

        //失效缓存
        deleteCountShoppingCache(uid, shoppingKey);

        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("操作成功").build();
    }

    /**
     * 减少数量
     *
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.decrease")
    @ResponseBody
    public ApiResponse decrease(HttpServletRequest httpServletRequest,
                                @RequestParam(value = "product_sku", required = true) Integer productSku,
                                @RequestParam(value = "decrease_number", required = false) Integer decreaseNumber,
                                @RequestParam(value = "uid", required = false) Integer uid,
                                @RequestParam(value = "shopping_key", required = false) String shoppingKey,
                                @RequestParam(value = "promotion_id", required = false) Integer promotion_id) {
        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setProduct_sku(productSku);
        request.setDecrease_number(decreaseNumber);
        request.setUid(uid);
        request.setShopping_key(shoppingKey);
        request.setPromotion_id(promotion_id);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingAddResponse response = serviceCaller.call(SHOPPING_DECREASE_REST_URL, request, ShoppingAddResponse.class);

        //失效缓存
        deleteCountShoppingCache(uid, shoppingKey);

        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("操作成功").build();
    }


    /**
     * 更换商品
     *
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.swap")
    @ResponseBody
    public ApiResponse swap(HttpServletRequest httpServletRequest,
                            @RequestParam(value = "swap_data", required = true) String swapData,
                            @RequestParam(value = "uid", required = false) Integer uid,
                            @RequestParam(value = "shopping_key", required = false) String shoppingKey) {
        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setSwap_data(swapData);
        request.setUid(uid);
        request.setShopping_key(shoppingKey);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingAddResponse response = serviceCaller.call(SHOPPING_SWAP_REST_URL, request, ShoppingAddResponse.class);

        //失效缓存
        deleteCountShoppingCache(uid, shoppingKey);

        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("加入成功").build();
    }


    /**
     * 移除商品到收藏夹
     *
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.addfavorite")
    @ResponseBody
    public ApiResponse addfavorite(HttpServletRequest httpServletRequest,
                                   @RequestParam(value = "product_sku_list", required = true) String productSkuListStr,
                                   @RequestParam(value = "uid", required = false) Integer uid,
                                   @RequestParam(value = "shopping_key", required = false) String shoppingKey) {

        //失效缓存
        deleteCountShoppingCache(uid, shoppingKey);

        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setProduct_sku_list(productSkuListStr);
        request.setUid(uid);
        request.setShopping_key(shoppingKey);
        ShoppingAddResponse response = serviceCaller.call(SHOPPING_ADDFAVORITE_REST_URL, request, ShoppingAddResponse.class);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("操作成功").build();
    }

    /**
     * 删除购物车商品
     *
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.remove")
    @ResponseBody
    public ApiResponse remove(HttpServletRequest httpServletRequest,
                              @RequestParam(value = "product_sku_list", required = true) String productSkuListStr,
                              @RequestParam(value = "uid", required = false) Integer uid,
                              @RequestParam(value = "shopping_key", required = false) String shoppingKey) {
        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setProduct_sku_list(productSkuListStr);
        request.setUid(uid);
        request.setShopping_key(shoppingKey);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingAddResponse response = serviceCaller.call(SHOPPING_REMOVE_REST_URL, request, ShoppingAddResponse.class);

        //失效缓存
        deleteCountShoppingCache(uid, shoppingKey);

        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("操作成功").build();
    }


    /**
     * cart_type=ordinary&method=app.Shopping.payment&uid=362719&yoho_coin_mode=0&product_sku_list=[{"sku":1363342,"buy_number":1,"type":"limitcode","limitproductcode":"2016061514364720","skn":51287024}]
     * 限购商品立即购买接口
     *
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.easyPayment")
    @ResponseBody
    public ApiResponse easyPayment(HttpServletRequest httpServletRequest,
                                   @RequestParam(value = "uid", required = true) Integer uid,
                                   @RequestParam(value = "cart_type", required = false) String cartType,
                                   @RequestParam(value = "yoho_coin_mode", required = false) Integer yohoCoinMode,
                                   @RequestParam(value = "enable_red_envelopes", required = false, defaultValue = "1") Integer enabledRedEnvelopes,
                                   @RequestParam(value = "product_sku_list", required = false) String productSkuListStr) {
        logger.info("shopping easy payment begin,uid:{},cart_type:{},yoho_coin_mode:{}, enable_red_envelopes:{},product_sku_list:{}",
                uid, cartType, yohoCoinMode, enabledRedEnvelopes, productSkuListStr);

        return doPayment(uid, cartType, yohoCoinMode, enabledRedEnvelopes, productSkuListStr, httpServletRequest.getHeader("User-Agent"));
    }


    /**
     * cart_type=ordinary&client_type=iphone&method=app.Shopping.payment&uid=362719&v=7
     * 获取购物车支付信息接口
     *
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.payment")
    @ResponseBody
    public ApiResponse payment(HttpServletRequest httpServletRequest,
                               @RequestParam(value = "uid", required = true) Integer uid,
                               @RequestParam(value = "cart_type", required = false) String cartType,
                               @RequestParam(value = "yoho_coin_mode", required = false) Integer yohoCoinMode,
                               @RequestParam(value = "enable_red_envelopes", required = false, defaultValue = "1") Integer enabledRedEnvelopes,
                               @RequestParam(value = "product_sku_list", required = false) String productSkuListStr) {

        logger.info("shopping payment begin,uid:{},cart_type:{},yoho_coin_mode:{}, enable_red_envelopes:{},product_sku_list:{}",
                uid, cartType, yohoCoinMode, enabledRedEnvelopes, productSkuListStr);

        return doPayment(uid, cartType, yohoCoinMode, enabledRedEnvelopes, productSkuListStr, httpServletRequest.getHeader("User-Agent"));
    }

    private ApiResponse doPayment(Integer uid,
                                  String cartType,
                                  Integer yohoCoinMode,
                                  Integer enabledRedEnvelopes,
                                  String productSkuListStr,
                                  String userAgent) {
        //失效缓存,
        deleteCountShoppingCache(uid, null);

        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setUid(uid);
        request.setCart_type(cartType);
        //默认使用yoho币
        request.setYoho_coin_mode(yohoCoinMode == null ? 1 : yohoCoinMode);
        request.setUser_agent(userAgent);
        request.setProduct_sku_list(productSkuListStr);
        request.setEnabled_RedEnvelopes(enabledRedEnvelopes);
        logger.info("enter payment shopping in controller,uid is {},request is {}", uid, request);
        ShoppingPaymentResponseWithRedEnvelopes response = serviceCaller.call(SHOPPING_PAYMENT_REST_URL, request, ShoppingPaymentResponseWithRedEnvelopes.class);

        Object realResponse = response;
        if (response.getEnabledRedEnvelopes() > 0) {
            realResponse = response;
        } else {
            //不使用红包,就不返回红包字段(red_envelopes,use_red_envelopes)给前端,前端就不会显示
            realResponse = filterRedEnvelopesAndBuildPaymentResponse(response);
        }

        return new ApiResponse.ApiResponseBuilder().data(realResponse).code(200).message("payment data.").build();
    }

    /**
     * 不返还红包
     * @param response
     * @return
     */
    private ShoppingPaymentResponse filterRedEnvelopesAndBuildPaymentResponse(ShoppingPaymentResponseWithRedEnvelopes response) {
        ShoppingPaymentResponse responseWithoutRedEnvelopes = new ShoppingPaymentResponse();
        responseWithoutRedEnvelopes.setShopping_cart_tag(response.getShopping_cart_tag());
        responseWithoutRedEnvelopes.setDelivery_address(response.getDelivery_address());
        responseWithoutRedEnvelopes.setUid(response.getUid());
        responseWithoutRedEnvelopes.setYoho_coin(response.getYoho_coin());
        responseWithoutRedEnvelopes.setUse_yoho_coin(response.getUse_yoho_coin());
        responseWithoutRedEnvelopes.setShopping_cart_data(response.getShopping_cart_data());
        responseWithoutRedEnvelopes.setGoods_list(response.getGoods_list());
        responseWithoutRedEnvelopes.setInvoices(response.getInvoices());
        responseWithoutRedEnvelopes.setPayment_way(response.getPayment_way());
        responseWithoutRedEnvelopes.setDelivery_way(response.getDelivery_way());
        responseWithoutRedEnvelopes.setDelivery_time(response.getDelivery_time());
        //是否开启优惠码
        responseWithoutRedEnvelopes.setPromo_code_enabled(response.getPromo_code_enabled());

        return  responseWithoutRedEnvelopes;
    }

    /**
     * 获取购物车总数
     *
     * @param uid
     * @param shoppingKey
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.count")
    @ResponseBody
    public ApiResponse count(HttpServletRequest httpServletRequest,
                             @RequestParam(value = "uid", required = false) Integer uid,
                             @RequestParam(value = "shopping_key", required = false) String shoppingKey) {

        logger.info("enter count shopping in controller,uid is {},shoppingKey is {}", uid, shoppingKey);

        ShoppingCountResponse response = new ShoppingCountResponse();
        //uid或shoppingKey有一个不为null
        if ((uid != null && uid > 0) || StringUtils.isNotEmpty(shoppingKey)) {
            //先从缓存中获取
            response = getCachedCountShoppingResponse(uid, shoppingKey);

            if (response == null) {
                logger.info("miss count shopping cache,uid is {},shoppingKey is {}", uid, shoppingKey);
                //缓存miss
                ShoppingCartRequest request = new ShoppingCartRequest();
                request.setUid(uid);
                request.setShopping_key(shoppingKey);
                request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
                response = serviceCaller.call(SHOPPING_COUNT_REST_URL, request, ShoppingCountResponse.class);

                //缓存结果
                cacheCountShoppingResponse(uid, shoppingKey, response);

            }
        }
        logger.info("count shopping success,response is {}", response);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("cart goods count.").build();
    }


    /**
     * 选中购物商品
     *
     * @param productSkuListStr
     * @param uid
     * @param shoppingKey
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.selected")
    @ResponseBody
    public ApiResponse selected(HttpServletRequest httpServletRequest,
                                @RequestParam(value = "product_sku_list", required = true) String productSkuListStr,
                                @RequestParam(value = "uid", required = false) Integer uid,
                                @RequestParam(value = "shopping_key", required = false) String shoppingKey) {
        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setProduct_sku_list(productSkuListStr);
        request.setUid(uid);
        request.setShopping_key(shoppingKey);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        serviceCaller.call(SHOPPING_SELECTED_REST_URL, request, void.class);

        //失效缓存,
        deleteCountShoppingCache(uid, shoppingKey);

        return new ApiResponse.ApiResponseBuilder().data(null).code(200).message("选择成功").build();
    }

    /**
     * @param use_yoho_coin
     * @param use_red_envelopes
     * @param coupon_code
     * @param payment_type
     * @param delivery_way
     * @param cart_type
     * @param uid
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.compute")
    @ResponseBody
    public ApiResponse compute(HttpServletRequest httpServletRequest,
                               @RequestParam(value = "use_yoho_coin", required = false) Double use_yoho_coin,
                               @RequestParam(value = "use_red_envelopes", required = false) Double use_red_envelopes,
                               @RequestParam(value = "coupon_code", required = false) String coupon_code,
                               @RequestParam(value = "payment_type", required = false) Integer payment_type,
                               @RequestParam(value = "delivery_way", required = false) Integer delivery_way,
                               @RequestParam(value = "cart_type", required = false) String cart_type,
                               @RequestParam(value = "uid", required = true) Integer uid,
                               @RequestParam(value = "promotion_code", required = false) String promotionCode,
                               @RequestParam(value = "product_sku_list", required = false) String productSkuListStr,
                               @RequestParam(value = "check_yohocoin_amount", required = false, defaultValue = "N") String check_yohocoin_amount) {

        //失效缓存,
        deleteCountShoppingCache(uid, null);

        ShoppingComputeRequest request = new ShoppingComputeRequest();
        request.setUse_yoho_coin(use_yoho_coin);
        request.setUse_red_envelopes(use_red_envelopes);
        request.setCoupon_code(coupon_code);
        request.setPayment_type(payment_type);
        request.setDelivery_way(delivery_way);
        request.setCart_type(cart_type);
        request.setUid(uid);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        request.setPromotion_code(promotionCode);
        request.setProduct_sku_list(productSkuListStr);
        request.setCheck_yohocoin_amount(check_yohocoin_amount);
        ShoppingComputeResponse response = serviceCaller.call(SHOPPING_COMPUTE_REST_URL, request, ShoppingComputeResponse.class);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("compute list.").build();
    }


    @RequestMapping(params = "method=app.Shopping.useCoupon")
    @ResponseBody
    public ApiResponse useCoupon(HttpServletRequest httpServletRequest,
                                 @RequestParam(value = "coupon_code", required = false) String coupon_code,
                                 @RequestParam(value = "cart_type", required = false) String cart_type,
                                 @RequestParam(value = "uid", required = true) Integer uid) {

        //失效缓存,
        deleteCountShoppingCache(uid, null);

        ShoppingComputeRequest request = new ShoppingComputeRequest();
        request.setCoupon_code(coupon_code);
        request.setCart_type(cart_type);
        request.setUid(uid);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingUseCouponResponse response = serviceCaller.call(SHOPPING_USECOUPON_REST_URL, request, ShoppingUseCouponResponse.class);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("use coupon code.").build();
    }

    /**
     * address_id=137&cart_type=ordinary&client_type=iphone&delivery_time=2&delivery_way=1&method=app.Shopping.submit&payment_id=15&payment_type=1&uid=362719&use_yoho_coin=120
     * 下单
     *
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.submit")
    @ResponseBody
    public ApiResponse submit(HttpServletRequest httpServletRequest,
                              @RequestParam(value = "uid", required = true) Integer uid,
                              @RequestParam(value = "sale_channel", required = false) String sale_channel,
                              @RequestParam(value = "address_id", required = false) Integer address_id,
                              @RequestParam(value = "payment_type", required = true) Integer payment_type,
                              @RequestParam(value = "payment_id", required = false) Integer payment_id,
                              @RequestParam(value = "cart_type", required = false) String cart_type,
                              @RequestParam(value = "use_yoho_coin", required = false) Double use_yoho_coin,
                              @RequestParam(value = "use_red_envelopes", required = false) Double use_red_envelopes,
                              @RequestParam(value = "coupon_code", required = false) String coupon_code,
                              @RequestParam(value = "delivery_way", required = false) Integer delivery_way,
                              @RequestParam(value = "delivery_time", required = false) Integer delivery_time,
                              @RequestParam(value = "shopping_cart_tag", required = false) String shopping_cart_tag,
                              @RequestParam(value = "client_type", required = false) String client_type,
                              @RequestParam(value = "invoices_type_id", required = false) Integer invoices_type_id,
                              @RequestParam(value = "invoices_type", required = false) Integer invoices_type,
                              @RequestParam(value = "invoice_content", required = false) Integer invoice_content,
                              @RequestParam(value = "receiverMobile", required = false) String receiverMobile,
                              @RequestParam(value = "invoices_title", required = false) String invoices_title,
                              @RequestParam(value = "remark", required = false) String remark,
                              @RequestParam(value = "qhy_union", required = false) String qhy_union,
                              @RequestParam(value = "promotion_code", required = false) String promotionCode,
                              @RequestParam(value = "product_sku_list", required = false) String productSkuListStr,
                              @RequestParam(value = "is_print_price", required = false) String isPrintPrice,
                              @RequestParam(value = "is_pre_contact", required = false) String isPreContact,
                              @RequestParam(value = "delivery_address", required = false) ShoppingDeliveryAddress deliveryAddressReq) {

        //失效缓存,
        deleteCountShoppingCache(uid, null);

        ShoppingSubmitRequest request = new ShoppingSubmitRequest();
        request.setUid(uid);
        request.setSale_channel(sale_channel);
        request.setAddress_id(address_id);
        request.setPayment_type(payment_type);
        request.setPayment_id(payment_id);
        request.setCart_type(cart_type);
        request.setUse_yoho_coin(use_yoho_coin);
        request.setUse_red_envelopes(use_red_envelopes);
        request.setCoupon_code(coupon_code);
        request.setDelivery_way(delivery_way);
        request.setDelivery_time(delivery_time);
        request.setShopping_cart_tag(shopping_cart_tag);
        request.setClient_type(client_type);
        request.setInvoices_type_id(invoices_type_id);
        request.setInvoices_title(invoices_title);
        request.setInvoices_type(invoices_type);
        request.setInvoice_content(invoice_content);
        request.setReceiverMobile(receiverMobile);
        request.setRemark(remark);
        request.setQhy_union(qhy_union);
        request.setFrom(FROM_APP);
        request.setPromotion_code(promotionCode);
        request.setClient_ip(RemoteIPInterceptor.getRemoteIP());
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        request.setProduct_sku_list(productSkuListStr);
        request.setIs_print_price(isPrintPrice);
        request.setIs_pre_contact(isPreContact);

        //收获详细地址
        request.setDelivery_address(deliveryAddressReq);

        logger.info("enter submit shopping in controller,uid is {} req is {}", uid,request);
        ShoppingSubmitResponse response = serviceCaller.call(SHOPPING_SUBMIT_REST_URL, request, ShoppingSubmitResponse.class);
        logger.info("submit shopping in controller serviceCaller call rel ,uid is {} rsp is {}", uid,response);
        //下单后，清除各类订单统计缓存
        userOrderCache.clearOrderCountCache(uid);
        
        //publish event
        LogEvent event = new LogEvent.Builder("order.submit").addTag("order_code", response.getOrder_code()).addArg("response", response).build();
        publisher.publishEvent(event);
        clearResourcesHomeIsNewUser(uid);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("submit order .").build();
    }

    /**
     * 清除首页判断是否新用户
     *
     * @param uid
     */
    private void clearResourcesHomeIsNewUser(Integer uid) {
        String cacheKey = CacheKeyConstants.RESOURCES_IS_NEW_USER + uid;
        try {
            holder.getLevel1Cache().delete(cacheKey);
        } catch (Exception e) {
            logger.info("can not clear cache {}", cacheKey);
        }
    }


    /**
     * 获取erp失败订单数量
     *
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.erpSubmitStat")
    @ResponseBody
    public ApiResponse erpSubmitStat(HttpServletRequest httpServletRequest) {
        Map response = serviceCaller.call(SHOPPING_ERPSUBMITSTAT_REST_URL, null, Map.class);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("ok.").build();
    }

    /**
     * 优惠码
     *
     * @param httpServletRequest
     * @param promotion_code
     * @param cart_type
     * @param uid
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.usePromotionCode")
    @ResponseBody
    public ApiResponse usePromotionCode(HttpServletRequest httpServletRequest,
                                        @RequestParam(value = "promotion_code", required = true) String promotion_code,
                                        @RequestParam(value = "cart_type", required = false) String cart_type,
                                        @RequestParam(value = "uid", required = true) Integer uid) {

        //失效缓存,
        deleteCountShoppingCache(uid, null);

        ShoppingComputeRequest request = new ShoppingComputeRequest();
        request.setPromotion_code(promotion_code);
        request.setCart_type(cart_type);
        request.setUid(uid);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingPromotionCodeResponse response = serviceCaller.call(SHOPPING_USEPROMOTIONCODE_REST_URL, request, ShoppingPromotionCodeResponse.class);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("use promotion code.").build();
    }


    /**
     * 再次购买
     *
     * @param httpServletRequest
     * @param order_code
     * @param uid
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.readd")
    @ResponseBody
    public ApiResponse readd(HttpServletRequest httpServletRequest,
                             @RequestParam(value = "order_code", required = true) String order_code,
                             @RequestParam(value = "uid", required = true) Integer uid) {
        ShoppingReAddRequest request = new ShoppingReAddRequest();
        request.setOrder_code(order_code);
        request.setUid(uid);
        ShoppingAddResponse response = serviceCaller.call(SHOPPING_READD_REST_URL, request, ShoppingAddResponse.class);

        //失效缓存,
        deleteCountShoppingCache(uid, null);

        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("use promotion code.").build();
    }


    /**
     * 先执行selectd操作，然后查询购物车
     *
     * @param httpServletRequest
     * @param productSkuListStr  [{"goods_type":"ordinary","buy_number":1,"selected":"Y","product_sku":"1006277","promotion_id":11111}]
     * @param uid
     * @param shoppingKey
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.selectedAndCart")
    @ResponseBody
    public ApiResponse selectedAndCart(HttpServletRequest httpServletRequest,
                                       @RequestParam(value = "product_sku_list", required = true) String productSkuListStr,
                                       @RequestParam(value = "uid", required = false) Integer uid,
                                       @RequestParam(value = "shopping_key", required = false) String shoppingKey) {

        //失效缓存,
        deleteCountShoppingCache(uid, shoppingKey);

        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setProduct_sku_list(productSkuListStr);
        request.setUid(uid);
        request.setShopping_key(shoppingKey);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingQueryResponse response = serviceCaller.call(SHOPPING_SELECTEDANDCART_REST_URL, request, ShoppingQueryResponse.class);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("ok").build();
    }


    /**
     * 先删除购物车商品，然后查询购物
     *
     * @param httpServletRequest
     * @param productSkuListStr  [{"product_sku":"1006277","promotion_id":11111}]
     * @param uid
     * @param shoppingKey
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.removeAndCart")
    @ResponseBody
    public ApiResponse removeAndCart(HttpServletRequest httpServletRequest,
                                     @RequestParam(value = "product_sku_list", required = true) String productSkuListStr,
                                     @RequestParam(value = "uid", required = false) Integer uid,
                                     @RequestParam(value = "shopping_key", required = false) String shoppingKey) {

        //失效缓存,
        deleteCountShoppingCache(uid, shoppingKey);

        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setProduct_sku_list(productSkuListStr);
        request.setUid(uid);
        request.setShopping_key(shoppingKey);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingQueryResponse response = serviceCaller.call(SHOPPING_REMOVEANDCART_REST_URL, request, ShoppingQueryResponse.class);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("ok").build();
    }


    /**
     * 先移入收藏夹商品，然后查询购物
     *
     * @param httpServletRequest
     * @param productSkuListStr  [{"product_sku":"1006277","promotion_id":11111}]
     * @param uid
     * @param shoppingKey
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.addfavoriteAndCart")
    @ResponseBody
    public ApiResponse addfavoriteAndCart(HttpServletRequest httpServletRequest,
                                          @RequestParam(value = "product_sku_list", required = true) String productSkuListStr,
                                          @RequestParam(value = "uid", required = false) Integer uid,
                                          @RequestParam(value = "shopping_key", required = false) String shoppingKey) {

        //失效缓存,
        deleteCountShoppingCache(uid, shoppingKey);

        ShoppingCartRequest request = new ShoppingCartRequest();
        request.setProduct_sku_list(productSkuListStr);
        request.setUid(uid);
        request.setShopping_key(shoppingKey);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        ShoppingQueryResponse response = serviceCaller.call(SHOPPING_ADDFAVORITEANDCART_REST_URL, request, ShoppingQueryResponse.class);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("ok").build();
    }

    /**
     * 统计可用的优惠券张数
     * @param uid {"uid":111}
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.countUsableCoupon")
    @ResponseBody
    public ApiResponse countUsableCoupon(@RequestParam(value = "uid", required = true) int uid) {

        //失效缓存,
        deleteCountShoppingCache(uid, null);

        ShoppingCouponRequest request = new ShoppingCouponRequest();
        request.setUid(uid);
        CountBO countBO = serviceCaller.call(SHOPPING_COUNTUSABLECOUPON_REST_URL, request, CountBO.class);
        return new ApiResponse.ApiResponseBuilder().data(countBO).code(200).message("ok.").build();
    }


    /**
     * 统计可用和不可用的优惠券列表
     * @param uid {"uid":111}
     * @return
     */
    @RequestMapping(params = "method=app.Shopping.listCoupon")
    @ResponseBody
    public ApiResponse listCoupon(@RequestParam(value = "uid", required = true) int uid) {

        //失效缓存,
        deleteCountShoppingCache(uid, null);

        ShoppingCouponRequest request = new ShoppingCouponRequest();
        request.setUid(uid);
        ShoppingCouponListResponse response = serviceCaller.call(SHOPPING_LISTCOUPON_REST_URL, request, ShoppingCouponListResponse.class);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("ok.").build();
    }


    /**
     * 缓存app.Shopping.count接口查询结果
     * 缓存一个小时
     * 缓存key ,uid > 0 缓存key为uid,否则
     *
     * @param uid
     * @param shoppingKey
     * @param response
     */
    private void cacheCountShoppingResponse(Integer uid, String shoppingKey, ShoppingCountResponse response) {
        logger.info("cache shopping count response,uid is {},shoppingKey is {},response is {}", uid, shoppingKey, response);
        if (response == null) {
            return;
        }
        try {
            String suffixKey = getCountShoppingCachedKey(uid, shoppingKey);
            if (suffixKey == null) {
                return;
            }
            String key = SHOPPING_COUNT_CACHE_PRE_KEY + suffixKey;
            logger.info("cache count shopping response,redis key is {}", key);
            valueOperations.setIfAbsent(key, JSON.toJSONString(response));
            redisTemplate.longExpire(key, 1, TimeUnit.HOURS);
            logger.info("cache shopping count response success");
        } catch (Exception ex) {
            logger.warn("cache shopping count response error,uid is {},shoppingkey is {},response is {}",
                    uid, shoppingKey, response, ex);
        }
    }

    /**
     * 从缓存中获取app.Shopping.count结果
     *
     * @param uid
     * @param shoppingKey
     * @return
     */
    private ShoppingCountResponse getCachedCountShoppingResponse(Integer uid, String shoppingKey) {

        String key = null;
        try {
            String suffixKey = getCountShoppingCachedKey(uid, shoppingKey);
            if (suffixKey == null) {
                return null;
            }
            key = SHOPPING_COUNT_CACHE_PRE_KEY + suffixKey;
            String text = valueOperations.get(key);
            return JSON.parseObject(text, ShoppingCountResponse.class);
        } catch (Exception ex) {
            logger.warn("get shopping count redis cache error, redis key:{}  uid is {},shoppingkey is {}",
                    key, uid, shoppingKey, ex);
        }
        return null;
    }

    /**
     * 删除app.Shopping.count缓存结果
     *
     * @param uid
     * @param shoppingKey
     */
    private void deleteCountShoppingCache(Integer uid, String shoppingKey) {
        logger.info("delete shopping count redis cache,uid is {},shoppingKey is {}", uid, shoppingKey);
        try {
            String suffixKey = getCountShoppingCachedKey(uid, shoppingKey);
            if (suffixKey == null) {
                return;
            }
            String key = SHOPPING_COUNT_CACHE_PRE_KEY + suffixKey;
            redisTemplate.delete(key);
            logger.info("delete shopping count redis cache success");
        } catch (Exception ex) {
            logger.warn("delete shopping count redis cache error,uid is {},shoppingkey is {}",
                    uid, shoppingKey, ex);
        }
    }

    private String getCountShoppingCachedKey(Integer uid, String shoppingKey) {
        if (uid != null && uid > 0) {
            return String.valueOf(uid);
        } else if (StringUtils.isNotEmpty(shoppingKey)) {
            return MD5.md5(shoppingKey);
        }
        return null;
    }


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}