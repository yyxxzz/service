package com.yoho.gateway.service.sns;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.model.sns.*;
import com.yoho.gateway.utils.CalendarUtils;
import com.yoho.product.model.BrandBo;
import com.yoho.service.model.order.response.OrdersGoods;
import com.yoho.service.model.request.BatchReqBO;
import com.yoho.service.model.response.UserBaseRspBO;
import com.yoho.service.model.sns.model.CommentBo;
import com.yoho.service.model.sns.model.ShareOrderGoodsBo;
import com.yoho.service.model.sns.response.PageResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * ShareOrderService
 *
 * @author zhangyonghui
 * @date 2015/11/9
 */
@Service
public class ShareOrderService {

    private static String productUrl = "http://m.yohobuy.com/product/pro_{0}_{1}";
    private final Logger logger = LoggerFactory.getLogger(ShareOrderService.class);
    @Autowired
    private ServiceCaller serviceCaller;

    /**
     * 品牌信息的Bo转换为Vo
     *
     * @param brandBo
     * @return
     */
    private BrandVo getBrandVo(BrandBo brandBo) {
        BrandVo brandVo = new BrandVo();
        brandVo.setId(brandBo.getId().intValue());
        brandVo.setBrand_alif(brandBo.getBrandAlif());
        brandVo.setBrand_doamin(brandBo.getBrandDomain());
        brandVo.setBrand_ico(brandBo.getBrandIco());
        brandVo.setBrand_name(brandBo.getBrandName());
        //brandVo.setBrand_name_substr(brandBo.getbrand);
        brandVo.setIs_hot(brandBo.getIsHot());


        return brandVo;
    }


    /**
     * 订单商品的Bo转换Vo
     *
     * @param ordergoodsBo
     * @return
     */
    private OrderGoodsVo getOrderGoodsVo(OrdersGoods ordergoodsBo) {
        OrderGoodsVo orderGoodsVo = new OrderGoodsVo();
        orderGoodsVo.setBuy_number(ordergoodsBo.getNum());
        orderGoodsVo.setColor_name(ordergoodsBo.getColorName());
        orderGoodsVo.setGoods_amount(ordergoodsBo.getGoodsAmount());
        if (ordergoodsBo.getGoodsImg() != null && ordergoodsBo.getGoodsImg().size()>0) {
            orderGoodsVo.setGoods_image(ordergoodsBo.getGoodsImg().get(0).getImageUrl());
        }
        orderGoodsVo.setGoods_price(ordergoodsBo.getGoodsPrice());
        if (ordergoodsBo.getGoodsType() != null) {
            orderGoodsVo.setGoods_type(String.valueOf(ordergoodsBo.getGoodsType()));
        }
        orderGoodsVo.setGoodsId(ordergoodsBo.getGoodsId());
        orderGoodsVo.setPruduct_name(ordergoodsBo.getProductName());
        orderGoodsVo.setProduct_url(ordergoodsBo.getProductUrl());
        orderGoodsVo.setProductId(ordergoodsBo.getProductId());
        orderGoodsVo.setSize_name(ordergoodsBo.getSizeName());

        return orderGoodsVo;
    }

    /**
     * 晒单用户信息的Bo转换Vo
     *
     * @param userBaseBO
     * @return
     */
    public ShareAuthorVo getShareAuthorVo(UserBaseRspBO userBaseBO) {

        ShareAuthorVo shareAuthorVo = new ShareAuthorVo();
        shareAuthorVo.setUid(userBaseBO.getUid());
        shareAuthorVo.setNickName(userBaseBO.getNickname());
        shareAuthorVo.setHeadIco(userBaseBO.getHeadIco());
        shareAuthorVo.setVipLevel(userBaseBO.getVipLevel());

        return shareAuthorVo;
    }


    public String createShareProductUrl(String productId, String goodsId) {

        String proUrl = "";
        if (StringUtils.isBlank(productId) || StringUtils.isBlank(goodsId)) {
            return proUrl;
        }

        String url_index = MessageFormat.format(productUrl, productId.toString(), goodsId.toString());

        StringBuffer sb = new StringBuffer();
        sb.append(url_index);
        sb.append("/");
        sb.append("product.html");

        return sb.toString();
    }

    /**
     * 将待待晒单列表消息转换为vo
     *
     * @param pageResponse
     * @param isPage       是否分页
     * @return
     */
    public PageResponse getShareOrderGoodsVoList(PageResponse pageResponse, boolean isPage) {

        List<ShareOrderGoodsVo> returnList = new ArrayList<>();

        List<ShareOrderGoodsBo> list = null;

        //是否分页处理
        if (!isPage) {
            list = pageResponse.getList();
        } else {
            if (CollectionUtils.isEmpty(pageResponse.getList())) {
                return pageResponse;
            }
            list = JSON.parseArray(pageResponse.getList().toString(), ShareOrderGoodsBo.class);
        }

        if (CollectionUtils.isNotEmpty(list)) {
            //循环封装BrandVo
            for (ShareOrderGoodsBo shareOrderGoodsBo : list) {
                if (StringUtils.isNotEmpty(shareOrderGoodsBo.getImageUrl()) && !shareOrderGoodsBo.getImageUrl().startsWith("http:")) {
                    shareOrderGoodsBo.setImageUrl(ImagesHelper.template2(shareOrderGoodsBo.getImageUrl(), ImagesHelper.SYS_BUCKET.get(ImagesHelper.SYS_GOODS_NAME)));
                }

                ShareOrderGoodsVo shareOrderGoodsVo = new ShareOrderGoodsVo();
                BeanUtils.copyProperties(shareOrderGoodsBo, shareOrderGoodsVo);
                // 满足客户端需求
                shareOrderGoodsVo.setSubImageUrl(shareOrderGoodsBo.getSubImageUrl());
                //获取brandVo
                BrandVo brandVo = null;
                if (shareOrderGoodsBo.getBrandBo() != null) {
                    brandVo = getBrandVo(shareOrderGoodsBo.getBrandBo());
                }

                shareOrderGoodsVo.setBrand(brandVo);
                returnList.add(shareOrderGoodsVo);
            }
        }

        pageResponse.setList(returnList);

        return pageResponse;
    }


    /**
     * 将晒单列表消息转换为vo
     *
     * @return
     */
    public PageResponse getShareOrderVoList(PageResponse pageResponse) {

        List<ShareOrderVo> returnList = new ArrayList<>();

        logger.debug("json parse: {} ", pageResponse.getList().toString());

        List<CommentBo> list = JSON.parseArray(pageResponse.getList().toString(), CommentBo.class);


        if (CollectionUtils.isNotEmpty(list)) {

            List<Integer> uidList = new ArrayList<Integer>();
            /**
             *   循环封装ShareOrderVo
             *   将shareOrder Bo转为VO
             */
            for (CommentBo commentBo : list) {
                if (StringUtils.isNotEmpty(commentBo.getUrl()) && !commentBo.getUrl().startsWith("http:")) {
                    commentBo.setUrl(ImagesHelper.template(commentBo.getUrl(), ImagesHelper.SYS_BUCKET.get(ImagesHelper.SYS_SNS_NAME), 1));
                }
                ShareOrderVo shareOrderVo = new ShareOrderVo();
                BeanUtils.copyProperties(commentBo, shareOrderVo);

                if (commentBo.getGoods() == null) {
                    uidList.add(Integer.parseInt(commentBo.getUid()));
                    returnList.add(shareOrderVo);
                    continue;
                }

                //获取brandVo
                BrandVo brandVo = null;
                if (commentBo.getGoods().getBrand() != null) {
                    brandVo = getBrandVo(commentBo.getGoods().getBrand());
                }

                //获取orderGoodsVo
                OrderGoodsVo orderGoodsVo = getOrderGoodsVo(commentBo.getGoods());
                //设置 brandVo
                orderGoodsVo.setBrand(brandVo);
                //设置 orderGoodsVo
                shareOrderVo.setGoods(orderGoodsVo);

                uidList.add(Integer.parseInt(commentBo.getUid()));

                returnList.add(shareOrderVo);
            }

            /**
             * 查询设置晒单用户信息
             */
            setShareOrderUserInfo(returnList, uidList);

        }
        pageResponse.setList(returnList);
        return pageResponse;
    }


    /**
     * 设置晒单的用户信息
     *
     * @param shareOrderVoList
     * @param uidList
     */
    private void setShareOrderUserInfo(List<ShareOrderVo> shareOrderVoList, List<Integer> uidList) {

        UserBaseRspBO[] defaultBo = null;
        try {
            // 批量查询晒单用户的信息
            defaultBo = getDefaultUserInfo(shareOrderVoList);
            BatchReqBO<Integer> reqList = new BatchReqBO(uidList);
            defaultBo = serviceCaller.call("users.selectUserBaseList", reqList, UserBaseRspBO[].class, defaultBo);
        } catch (Exception e) {
            logger.error("invoke users.selectUserBaseList failed, reqList:{}, exception: {} ", uidList, e);

        }

        // 循环遍
        for (ShareOrderVo shareOrderVo : shareOrderVoList) {
            for (UserBaseRspBO userBaseBO : defaultBo) {
                if (userBaseBO.getUid().toString().equals(shareOrderVo.getUid())) {
                    shareOrderVo.setUserInfo(getShareAuthorVo(userBaseBO));
                    continue;
                }
            }
        }
    }


    // default
    private UserBaseRspBO[] getDefaultUserInfo(List<ShareOrderVo> shareOrderVoList) {

        if (CollectionUtils.isEmpty(shareOrderVoList)) {
            return null;
        }

        UserBaseRspBO[] bo = new UserBaseRspBO[shareOrderVoList.size()];
        for (int i = 0; i < shareOrderVoList.size(); i++) {

            ShareOrderVo shareOrderVo = shareOrderVoList.get(i);
            UserBaseRspBO defaultUser = new UserBaseRspBO();
            defaultUser.setUid(Integer.parseInt(shareOrderVo.getUid()));
            defaultUser.setNickname(String.valueOf(defaultUser.getUid()));
            defaultUser.setVipLevel(0);
            bo[i] = defaultUser;
        }

        return bo;
    }
    
    /**
     * 将晒单评价列表信息转换为Vo
     *
     * @param pageResponse
     * @param isPage   是否分页
     * @return
     */
    public List<ShareOrderCommentVO> getShareOrderCommentVOList(ShareOrderGoodsBo[] shareOrderGoodsBoList) {
    	logger.info("Enter getShareOrderCommentVOList");
        if (shareOrderGoodsBoList==null || shareOrderGoodsBoList.length==0) {
            return Lists.newArrayList();
        }
        
        // 根据orderCode进行分组
        Map<String, List<ShareOrderGoodsBo>> map = new LinkedHashMap<String, List<ShareOrderGoodsBo>>();
        for(ShareOrderGoodsBo shareOrderGoodsBo : shareOrderGoodsBoList){
        	if(map.get(shareOrderGoodsBo.getOrderCode())!=null){
        		List<ShareOrderGoodsBo> shareOrderGoodTemp = map.get(shareOrderGoodsBo.getOrderCode());
        		shareOrderGoodTemp.add(shareOrderGoodsBo);
        		map.put(shareOrderGoodsBo.getOrderCode(), shareOrderGoodTemp);
        	}else{
        		map.put(shareOrderGoodsBo.getOrderCode(), Lists.newArrayList(shareOrderGoodsBo));
        	}
        }
        
        List<ShareOrderCommentVO> returnList = new ArrayList<ShareOrderCommentVO>();
        for (Entry<String, List<ShareOrderGoodsBo>> entry : map.entrySet()) {
        	returnList.add(covertToShareOrderCommentVO(entry));
        }
        
        return returnList;
    }
    
    
    private ShareOrderCommentVO covertToShareOrderCommentVO(Entry<String, List<ShareOrderGoodsBo>> entry){
    	List<ShareOrderGoodsBo> shareOrderGoodBoList = entry.getValue();
    	ShareOrderCommentVO shareOrderCommentVO = new ShareOrderCommentVO();
    	shareOrderCommentVO.setOrderCode(entry.getKey());
    	List<ShareOrderGoodsVo> orderGoodsVoList = new ArrayList<ShareOrderGoodsVo>();
    	for (ShareOrderGoodsBo shareOrderGoodsBo : shareOrderGoodBoList) {
    		ShareOrderGoodsVo orderGoodVo = new ShareOrderGoodsVo();
    		shareOrderCommentVO.setOrderId(shareOrderGoodsBo.getOrderId());
    		shareOrderCommentVO.setCreateTime(CalendarUtils.parseformatSeconds(shareOrderGoodsBo.getCreateTime(), CalendarUtils.LONG_FORMAT_LINE));
    		orderGoodVo.setCnAlphabet(shareOrderGoodsBo.getCnAlphabet());
    		orderGoodVo.setComment(shareOrderGoodsBo.getComment());
    		orderGoodVo.setErpSkuId(shareOrderGoodsBo.getErpSkuId());
    		orderGoodVo.setGoodsId(shareOrderGoodsBo.getGoodsId());
    		orderGoodVo.setImageUrl(shareOrderGoodsBo.getImageUrl());
    		orderGoodVo.setProductId(shareOrderGoodsBo.getProductId());
    		orderGoodVo.setProductName(shareOrderGoodsBo.getProductName());
    		orderGoodVo.setProductSkn(shareOrderGoodsBo.getProductSkn());
    		orderGoodsVoList.add(orderGoodVo);
		}
    	shareOrderCommentVO.setOrderGoods(orderGoodsVoList);
    	return shareOrderCommentVO;
    }


	public Integer getNotCommentCount(ShareOrderGoodsBo[] shareOrderGoodsBoList) {
		logger.info("Enter getShareOrderCommentVOList");
        if (shareOrderGoodsBoList==null || shareOrderGoodsBoList.length==0) {
            return 0;
        }
        Integer count = 0;
        for (ShareOrderGoodsBo shareOrderGoodsBo : shareOrderGoodsBoList) {
			if(StringUtils.isEmpty(shareOrderGoodsBo.getComment())){
				count++;
			}
		}
		return count;
	}
}
