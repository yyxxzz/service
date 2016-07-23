package com.yoho.gateway.controller.shops;

import com.alibaba.fastjson.JSON;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.bigdata.*;
import com.yoho.gateway.model.bigdata.vo.*;
import com.yoho.gateway.model.bigdata.vo.StorageStatisticsVO;
import com.yoho.service.model.shops.request.BalanceReqBo;
import com.yoho.service.model.shops.response.ShopAccountRespBo;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Created by lijian
 * on 2016-5-23 14:30:034
 */

@Controller
public class ShopReportController {

    private Logger logger = LoggerFactory.getLogger(ShopReportController.class);

    @Autowired
    protected ServiceCaller serviceCaller;


    /**
     * 销售统计报表　给APP用
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.shops.sales")
    @ResponseBody
    public ApiResponse getOrdersGoodsRptList( OrdersGoodsVo vo) throws GatewayException {
    	logger.info("Begin getOrdersGoodsRptList. vo is {}", vo);
        if (vo == null) {
            throw new GatewayException(401, "参数请求异常");
        }
        JSONObject result = null;
        try{
            result = serviceCaller.call("bigdata.getOrdersGoodsRptForApi", vo, JSONObject.class);
        }catch(Exception e){
            logger.warn("getOrdersGoodsRptList req bigdate is error {}", e);
            return new ApiResponse.ApiResponseBuilder().code(500).message("调用服务失败").build();
        }
        int code = 200;
        String message = "查询销售统计报表成功";
        PageResponseVO<OrdersGoodsLast7DaysVO, NumAmountVO> pageResponseVO = null;
        if (result != null && result.getInt("code") == 200) {
            if("[]".equals(result.get("data").toString())){
                JSONObject rootResult = buildNoResultJSON();
                return new ApiResponse.ApiResponseBuilder().code(result.getInt("code")).data(rootResult).build();
            }
            JSONObject jsonObject = JSONObject.fromObject(result.get("data"));
            pageResponseVO = JSON.parseObject(jsonObject.toString(), PageResponseVO.class);

            //组织返回
            return new ApiResponse.ApiResponseBuilder().code(code).message(message).data(pageResponseVO).build();
        } else {
            code = result.getInt("code");
            return new ApiResponse.ApiResponseBuilder().code(code).message(message).data(result.get("message")).build();
        }

    }

    /**
     * 当返回结果没有时，同样的数据结构返回
     * @return
     */
    private JSONObject buildNoResultJSON() {
        JSONObject rootResult = new JSONObject();
        rootResult.put("additionInfo","");
        JSONObject listResult = new JSONObject();
        rootResult.put("list",listResult.toString());
        rootResult.put("page",0);
        rootResult.put("size",0);
        rootResult.put("total",0);
        rootResult.put("totalPage",0);
        return rootResult;
    }


    /**
     * 查询退货统计报表　给APP用
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.shops.refund")
    @ResponseBody
    public ApiResponse getReturnGoodsRptList( ReturnsGoodsVo vo) throws GatewayException {
        logger.info("Begin getReturnGoodsRptList. vo is {}", vo);
        if (vo == null) {
            throw new GatewayException(401, "参数请求异常");
        }
        JSONObject result = null;
        try {
            result = serviceCaller.call("bigdata.getReturnGoodsRptForApi", vo, JSONObject.class);
        } catch (Exception e) {
            logger.warn("getReturnGoodsRptList req bigdate is error {}", e);
            return new ApiResponse.ApiResponseBuilder().code(500).message("调用服务失败").build();
        }
        int code = 200;
        String message = "查询退货统计报表成功";
        PageResponseVO<ReturnsGoodsVO, NumAmountVO> pageResponseVO = null;

        if (result != null && result.getInt("code") == 200) {
            if("[]".equals(result.get("data").toString())){
                return new ApiResponse.ApiResponseBuilder().code(result.getInt("code")).data(buildNoResultJSON()).build();
            }
            JSONObject jsonObject = JSONObject.fromObject(result.get("data"));
            pageResponseVO = JSON.parseObject(jsonObject.toString(), PageResponseVO.class);

            //组织返回
            return new ApiResponse.ApiResponseBuilder().code(code).message(message).data(pageResponseVO).build();
        } else {
            code = result.getInt("code");
            return new ApiResponse.ApiResponseBuilder().code(code).message(message).data(result.get("message")).build();

        }

    }


    /**
     * 发货入库统计　给APP用
     * @param storageInVo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.shops.storagein")
    @ResponseBody
    public ApiResponse getStorageInRptList( StorageInVo storageInVo) throws GatewayException {
        logger.info("Begin getStorageInRptList. storageInVo is {}", storageInVo);
        if (storageInVo == null) {
            throw new GatewayException(401, "参数请求异常");
        }

        JSONObject result = null;
        try {
            result = serviceCaller.call("bigdata.getStorageInRptForApi", storageInVo, JSONObject.class);
        } catch (Exception e) {
            logger.warn("getStorageInRptList req bigdate is error {}", e);
            return new ApiResponse.ApiResponseBuilder().code(500).message("调用服务失败").build();
        }
        int code = 200;
        String message = "查询进库统计报表成功";
        PageResponseVO<RequisitionInStorageVO, NumAmountVO> pageResponseVO = null;
        if (result != null && result.getInt("code") == 200) {
            if("[]".equals(result.get("data").toString())){
                return new ApiResponse.ApiResponseBuilder().code(result.getInt("code")).data(buildNoResultJSON()).build();
            }
            JSONObject jsonObject = JSONObject.fromObject(result.get("data"));
            pageResponseVO = JSON.parseObject(jsonObject.toString(), PageResponseVO.class);

            //组织返回
            return new ApiResponse.ApiResponseBuilder().code(code).message(message).data(pageResponseVO).build();
        } else {
            code = result.getInt("code");
            return new ApiResponse.ApiResponseBuilder().code(code).message(message).data(result.get("message")).build();
        }

    }


    /**
     * 清退出库统计　给APP用
     *
     * @param vo
     * @return
     */
    @RequestMapping(params = "method=app.shops.storageout")
    @ResponseBody
    public ApiResponse getStorageOutRptList( StorageOutVo vo) throws GatewayException {
        logger.info("Begin getStorageOutRptList. vo is {}", vo);
        if (vo == null) {
            throw new GatewayException(401, "参数请求异常");
        }
        JSONObject result = null;
        try {
            result = serviceCaller.call("bigdata.getStorageOutRptForApi", vo, JSONObject.class);
        } catch (Exception e) {
            logger.warn("getStorageOutRptList req bigdate is error {}", e);
            return new ApiResponse.ApiResponseBuilder().code(500).message("调用服务失败").build();
        }
        int code = 200;
        String message = "查询销售出库统计报表成功";
        PageResponseVO<RequisitionOutStorageVO, NumAmountVO> pageResponseVO = null;

        if (result != null && result.getInt("code") == 200) {
            if("[]".equals(result.get("data").toString())){
                return new ApiResponse.ApiResponseBuilder().code(result.getInt("code")).data(buildNoResultJSON()).build();
            }
            JSONObject jsonObject = JSONObject.fromObject(result.get("data"));
            pageResponseVO = JSON.parseObject(jsonObject.toString(), PageResponseVO.class);

            //组织返回
            return new ApiResponse.ApiResponseBuilder().code(code).message(message).data(pageResponseVO).build();
        } else {
            code = result.getInt("code");
            return new ApiResponse.ApiResponseBuilder().code(code).message(message).data(result.get("message")).build();
        }

    }
    
    /**
     * 获取店铺的品牌排名，当前等级、排名、上升名词
     */
    @RequestMapping(params = "method=app.shops.shopbrandrank")
    @ResponseBody
    public ApiResponse getShopBrandRank ( ShopBrandRankVO vo) throws GatewayException {
        logger.info("Begin getShopBrandRank. vo is {}", vo);
        if (vo == null) {
            throw new GatewayException(401, "参数请求异常");
        }
        JSONObject result = null;
        try {
            result = serviceCaller.call("bigdata.getShopBrandRank", vo, JSONObject.class);
        } catch (Exception e) {
            logger.warn("getShopBrandRank req bigdate is error {}", e);
            return new ApiResponse.ApiResponseBuilder().code(500).message("调用服务失败").build();
        }
        BrandRankResponseVO responseVO = new BrandRankResponseVO();
        if (result != null && result.getInt("code") == 200) {
            if("[]".equals(result.get("data").toString())){
                return new ApiResponse.ApiResponseBuilder().code(result.getInt("code")).data(responseVO).build();
            }
            JSONObject jsonObject = JSONObject.fromObject(result.get("data"));
            responseVO =JSON.parseObject(jsonObject.toString(), BrandRankResponseVO.class);

            //组织返回
            return new ApiResponse.ApiResponseBuilder().data(responseVO).build();
        } else {
            return new ApiResponse.ApiResponseBuilder().code(result.getInt("code")).data(result.get("message")).build();
        }

    }
    
    /**
     * 获取店铺的经营指标：一个自然日内有效订单商品件数、有效订单商品 金额
     */
    @RequestMapping(params = "method=app.shops.shopbusinessoverview")
    @ResponseBody
    public ApiResponse getShopBusinessOverview (ShopBrandRankVO vo) throws GatewayException{
    	 logger.info("Begin getShopBusinessOverview. vo is {}", vo);
         if (vo == null) {
             throw new GatewayException(401, "参数请求异常");
         }
         JSONObject result = null;
         try {
             result = serviceCaller.call("bigdata.getShopBusinessOverview", vo, JSONObject.class);
         } catch (Exception e) {
             logger.warn("getShopBusinessOverview req bigdate is error {}", e);
             return new ApiResponse.ApiResponseBuilder().code(500).message("调用服务失败").build();
         }
        ManagementKpiResponseVO responseVO =  new ManagementKpiResponseVO();
        if (result != null && result.getInt("code") == 200) {

            if("[]".equals(result.get("data").toString())){
                return new ApiResponse.ApiResponseBuilder().code(result.getInt("code")).data(responseVO).build();
            }
            JSONObject jsonObject = JSONObject.fromObject(result.get("data"));

            //组织返回
            responseVO = JSON.parseObject(jsonObject.toString(), ManagementKpiResponseVO.class);

            //组织返回
            return new ApiResponse.ApiResponseBuilder().data(responseVO).build();
         } else {
             return new ApiResponse.ApiResponseBuilder().code(result.getInt("code")).data(result.get("message")).build();
       }

    }
    
    /**
     * 查询库存列表
     */
    @RequestMapping(params = "method=app.shops.storagestatistics")
    @ResponseBody
    public ApiResponse getStorageStatisticsList(StorageStatisticsVO vo) throws GatewayException{
    	 logger.info("Begin getStorageStatisticsList.vo is {}", vo);
         if (vo == null) {
             throw new GatewayException(401, "参数请求异常");
         }
         JSONObject result = null;
         try {
             result = serviceCaller.call("bigdata.getStorageStatisticsList", vo, JSONObject.class);
         } catch (Exception e) {
             logger.warn("getStorageStatisticsList req bigdate is error {}", e);
             return new ApiResponse.ApiResponseBuilder().code(500).message("调用服务失败").build();
         }
         int code = 200;
         String message = "	查询成功！";
         PageResponseVO<StorageStatisticsVO, StorageStatisticsInfoVO> storageStatisticsRep = null;
        if (result != null && result.getInt("code") == 200) {
            if("[]".equals(result.get("data").toString())){
                return new ApiResponse.ApiResponseBuilder().code(result.getInt("code")).data(buildNoResultJSON()).build();
            }
             JSONObject jsonObject = JSONObject.fromObject(result.get("data"));
             storageStatisticsRep = JSON.parseObject(jsonObject.toString(), PageResponseVO.class);

            //组织返回
            return new ApiResponse.ApiResponseBuilder().code(code).message(message).data(storageStatisticsRep).build();
         } else {
            code = result.getInt("code");
             return new ApiResponse.ApiResponseBuilder().code(code).message(message).data(result.get("message")).build();
         }

    }
	
	/**
     * 对账结算　给APP用
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.shops.accountbalance")
    @ResponseBody
    public ApiResponse getOrdersGoodsAccountRptList( OrdersGoodsVo vo) throws GatewayException {
        // 参数检查
        if(vo == null){
            logger.warn("accountbalance: request param is null");
            throw new GatewayException(500, "request param is null");
        }
        logger.info("Begin call ShopReportController.accountbalance gateway. with vo={}", vo);

        BalanceReqBo bo = new BalanceReqBo();
        BeanUtils.copyProperties(vo, bo);

        ShopAccountRespBo result = serviceCaller.call("platform.getAccountBalance", vo, ShopAccountRespBo.class);

        return new ApiResponse.ApiResponseBuilder().message("调用服务成功").data(result).build();
    }

}
