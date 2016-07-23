package com.yoho.gateway.controller.product;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.MemecacheClientHolder;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.product.builder.LimitProductModelBuilder;
import com.yoho.gateway.controller.product.convert.LimitProductConvert;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.product.LimitProductVo;
import com.yoho.gateway.model.product.ReminderVo;
import com.yoho.gateway.service.PushTokenService;
import com.yoho.product.model.LimitProductBo;
import com.yoho.product.model.ReminderBo;
import com.yoho.product.request.BaseRequest;
import com.yoho.product.request.LimitProductQueryRequest;
import com.yoho.product.request.LimitProductSkuReqBo;
import com.yoho.product.response.LimitProductSkuRspBo;
import com.yoho.product.response.VoidResponse;
import com.yoho.service.model.promotion.LimitCodeBo;
import com.yoho.service.model.promotion.request.ActivityStatusReqBO;
import com.yoho.service.model.promotion.request.LimitCodeReq;

/**
 * 限定商品的相关服务
 * 
 * @author xieyong
 *
 */
@Controller
public class LimitProductController {

	private final Logger logger = LoggerFactory.getLogger(LimitProductController.class);

	@Autowired
	private ServiceCaller serviceCaller;

	@Autowired
	private MemecacheClientHolder memecacheClientHolder;

	@Autowired
	private LimitProductConvert limitProductConvert;

	@Autowired
	private LimitProductHelper helper;

	@Autowired
	private LimitProductModelBuilder limitProductModelBuilder;

	@Autowired
	private PushTokenService pushTokenService;

	// 一级缓存的key
	private final static String LIMITPRODUCT_DETAIL_LEVEL1_KEYPRE = "yh:gw1:limitproductDetail:";

	// 二级缓存的key
	private final static String LIMITPRODUCT_DETAIL_LEVEL2_KEYPRE = "yh:gw2:limitproductDetail:";

	/**
	 * 查询已经发售的限量商品(在gw缓存1分钟)
	 * 
	 * @param limit
	 * @param pageNum
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.limitProduct.alreadySaleLimitProduct")
	@ResponseBody
	@Cachable(expire = 60)
	public ApiResponse queryAlreadySaleLimitProduct(@RequestParam(value = "limit", required = false, defaultValue = "20") int limit,
			@RequestParam(value = "page", required = false, defaultValue = "1") int pageNum) throws GatewayException {
		logger.info("begin queryAlreadySaleLimitProduct limit:{},page:{}", limit, pageNum);
		LimitProductQueryRequest limitProductQueryRequest = new LimitProductQueryRequest();
		limitProductQueryRequest.setPageNum(pageNum);
		limitProductQueryRequest.setPageSize(limit);
		LimitProductBo[] limitProductBoList = serviceCaller.call("product.queryAlreadySaleLimitProduct", limitProductQueryRequest, LimitProductBo[].class);
		List<LimitProductVo> limitProductVoList = limitProductConvert.convert(limitProductBoList);
		// 商品列表不为空才需要去查总数
		int limitProdcutCount = 0;
		if (CollectionUtils.isNotEmpty(limitProductVoList)) {
			// 如果2s钟不返回就直接取limitProductVoList的size
			limitProdcutCount = getCount(limitProductVoList.size(), 2, "product.getAlreadySaleLimitProductCount");
		}
		LimitProductRspPageVo limitProductRspPageVo = new LimitProductRspPageVoBuilder().buildCurrentPage(pageNum)
				.buildPageTotal(limitProdcutCount % limit == 0 ? limitProdcutCount / limit : limitProdcutCount / limit + 1).buildTotal(limitProdcutCount)
				.buildLimitProductVoList(limitProductVoList).toBuilder();

		return new ApiResponse.ApiResponseBuilder().data(limitProductRspPageVo).code(200).message("alreadySale limitproduct info").build();

	}

	/**
	 * 
	 * @param defaultCount
	 *            默认值
	 * @param waitSeconds
	 *            等待超时时间
	 * @param method
	 *            调用方法
	 * @return
	 */
	private int getCount(int defaultCount, int waitSeconds, String method) {
		int consultCount = 0;
		try {
			consultCount = serviceCaller.asyncCall(method, null, Integer.class).get(waitSeconds);
		} catch (Exception e) {
			logger.warn("invoke method{} failed", method, e);
			consultCount = defaultCount;
		}
		return consultCount;
	}

	/**
	 * 查询热门发售的限量商品(客户端根据客户端保存的MD5值去判断是否需要显示小红点,判断MD5只根据第一页最新的数据)(在gw缓存1分钟)
	 * 
	 * @param limit
	 * @param pageNum
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.limitProduct.hotLimitProduct")
	@ResponseBody
	@Cachable(expire = 60)
	public ApiResponse queryHotLimitProduct(@RequestParam(value = "limit", required = false, defaultValue = "20") int limit,
			@RequestParam(value = "page", required = false, defaultValue = "1") int pageNum) throws GatewayException {
		logger.info("begin queryHotLimitProduct limit:{},page:{}", limit, pageNum);
		LimitProductQueryRequest limitProductQueryRequest = new LimitProductQueryRequest();
		limitProductQueryRequest.setPageNum(pageNum);
		limitProductQueryRequest.setPageSize(limit);
		LimitProductBo[] limitProductBoList = serviceCaller.call("product.queryHotLimitProduct", limitProductQueryRequest, LimitProductBo[].class);
		List<LimitProductVo> limitProductVoList = limitProductConvert.convert(limitProductBoList);
		// 商品列表不为空才需要去查总数
		int limitProdcutCount = 0;
		if (CollectionUtils.isNotEmpty(limitProductVoList)) {
			// 如果2s钟不返回就直接取limitProductVoList的size
			limitProdcutCount = getCount(limitProductVoList.size(), 2, "product.getHotLimitProductCount");
		}
		LimitProductRspPageVo limitProductRspPageVo = new LimitProductRspPageVoBuilder().buildCurrentPage(pageNum)
				.buildPageTotal(limitProdcutCount % limit == 0 ? limitProdcutCount / limit : limitProdcutCount / limit + 1).buildTotal(limitProdcutCount)
				.buildLimitProductVoList(limitProductVoList).toBuilder();
		return new ApiResponse.ApiResponseBuilder().data(limitProductRspPageVo).code(200).message("hot limitproduct info").build();

	}

	/**
	 * 查询即将发售的限量商品(客户端根据客户端保存的MD5值去判断是否需要显示小红点)(在gw缓存1分钟)
	 * 
	 * @param limit
	 * @param pageNum
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.limitProduct.soonToSaleLimitProduct")
	@ResponseBody
	@Cachable(expire = 60)
	public ApiResponse querySoonToSaleLimitProduct(@RequestParam(value = "limit", required = false, defaultValue = "20") int limit,
			@RequestParam(value = "page", required = false, defaultValue = "1") int pageNum) throws GatewayException {
		logger.info("begin querySoonToSaleLimitProduct limit:{},page:{}", limit, pageNum);
		LimitProductQueryRequest limitProductQueryRequest = new LimitProductQueryRequest();
		limitProductQueryRequest.setPageNum(pageNum);
		limitProductQueryRequest.setPageSize(limit);
		LimitProductBo[] limitProductBoList = serviceCaller.call("product.querySoonToSaleLimitProduct", limitProductQueryRequest, LimitProductBo[].class);
		List<LimitProductVo> limitProductVoList = limitProductConvert.convert(limitProductBoList);
		// 商品列表不为空才需要去查总数
		int limitProdcutCount = 0;
		if (CollectionUtils.isNotEmpty(limitProductVoList)) {
			// 如果2s钟不返回就直接取limitProductVoList的size
			limitProdcutCount = getCount(limitProductVoList.size(), 2, "product.getSoonToSaleLimitProductCount");
		}
		LimitProductRspPageVo limitProductRspPageVo = new LimitProductRspPageVoBuilder().buildCurrentPage(pageNum)
				.buildPageTotal(limitProdcutCount % limit == 0 ? limitProdcutCount / limit : limitProdcutCount / limit + 1).buildTotal(limitProdcutCount)
				.buildLimitProductVoList(limitProductVoList).toBuilder();
		return new ApiResponse.ApiResponseBuilder().data(limitProductRspPageVo).code(200).message("soonToSale limitproduct info").build();
	}

	/**
	 * 限量商品详情h5页面
	 * 
	 * @param limitProductCode
	 * @return
	 */
	@RequestMapping(params = "method=app.limitProduct.limitProductHtml")
	@Cachable
	public ModelAndView queryLimitProductHtml(@RequestParam(value = "limitProductCode") String limitProductCode) throws GatewayException {
		if (StringUtils.isBlank(limitProductCode)) {
			throw new GatewayException(500, "limitProductCode can't be empty");
		}
		LimitProductBo limitProductBo = serviceCaller.call("product.getLimitProductByCode", limitProductCode, LimitProductBo.class);
		ModelMap model = limitProductModelBuilder.buildLimitPrdModelMap(limitProductBo);
		return new ModelAndView("limitproduct-desc-app", model);
	}

	/**
	 * 限定商品详情(根据限量商品code获取限量商品详情)(在gw缓存1分钟,这里涉及到一个回填实时数据的问题) 因为带上udid这边缓存不能带udid
	 * 
	 * @param limitProductCode
	 *            限定商品的唯一标识code
	 * @param uid
	 *            用户在进限量商品详情的时候必须是登陆的状态
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.limitProduct.limitProductDetail")
	@ResponseBody
	public ApiResponse getLimitProductBoByCode(@RequestParam(value = "limitProductCode", required = true) String limitProductCode,
			@RequestParam(value = "uid", required = false, defaultValue = "0") String uid) throws GatewayException {
		logger.info("begin getLimitProductByCode limitProductCode:{},uid:{}", limitProductCode, uid);
		int activityStatus = -1;
		LimitCodeBo limitCode = new LimitCodeBo();
		if (StringUtils.isBlank(limitProductCode)) {
			throw new GatewayException(500, "limitProductCode can't be empty");
		}
		try {
			final String key = LIMITPRODUCT_DETAIL_LEVEL1_KEYPRE + limitProductCode;
			LimitProductVo limitProductVo = memecacheClientHolder.getLevel1Cache().get(key, LimitProductVo.class);
			LimitProductBo limitProductBo = null;
			if (null == limitProductVo) {
				limitProductBo = serviceCaller.call("product.getLimitProductByCode", limitProductCode, LimitProductBo.class);
				limitProductVo = limitProductConvert.convert(limitProductBo);

			}
			if (null != limitProductVo) {
				logger.info("call product.getLimitProductByCode with limitProdtctType is {}", limitProductVo.getLimitProductType());
				LimitCodeReq limitCodeReq = new LimitCodeReq();
				limitCodeReq.setBatchNo(limitProductVo.getBatchNo());
				limitCode = serviceCaller.call("promotion.getLimitSKNByBatchNO", limitCodeReq, LimitCodeBo.class);
				if (limitProductVo.getLimitProductType() == 2) {
					logger.info("getLimitProductByCode activityId is {}", limitProductVo.getActivityId());

					// 获取排队活动状态,如果showFlag为0，表示这个限定商品已经在后台关闭，那么排队状态就置为3，已结束
					if (limitProductVo.getShowFlag() == 0) {
						limitProductVo.setQueueType(3);
					} else {
						ActivityStatusReqBO activityStatusBO = new ActivityStatusReqBO();
						activityStatusBO.setActivityId(String.valueOf(limitProductVo.getActivityId()));
						activityStatusBO.setUid(uid);
						activityStatus = serviceCaller.call("promotion.getActivityStatus", activityStatusBO, Integer.class);
						logger.info("call promotion.getActivityStatus with activityStatus is {}", activityStatus);
						// 活动状态
						limitProductVo.setQueueType(activityStatus);
					}
					// 是否关联了SKU
					boolean relatedSKU = false;

					if (limitCode != null && ("U").equals(limitCode.getLimitCodeType())) {
						relatedSKU = true;
					}
					// 利用SKN查询颜色，尺码信息
					LimitProductSkuReqBo limitProductSku = new LimitProductSkuReqBo();
					// productSkn可能为空，需要判断
					Integer productSkn = limitProductVo.getProductSkn();
					if (null == productSkn) {
						limitProductSku.setProductSkn(0);
					} else {
						limitProductSku.setProductSkn(limitProductVo.getProductSkn());
					}
					limitProductSku.setUid(Integer.valueOf(uid));
					LimitProductSkuRspBo limitProductSkuRsp = null;
					limitProductSkuRsp = serviceCaller.call("product.getUserSkuByUidAndSknId", limitProductSku, LimitProductSkuRspBo.class);
					logger.info("getUserSkuByUidAndSknId result is {}", limitProductSkuRsp);
					if (limitProductSkuRsp != null) {
						logger.info("getLimitProductByCode colorName is {}, sizeName is {},selectSKU is {},uid is {}", limitProductSkuRsp.getColorName(),
								limitProductSkuRsp.getSizeName(), limitProductSkuRsp.getProductSku(), limitProductSkuRsp.getUid());
						if (uid.equals(String.valueOf(limitProductSkuRsp.getUid()))) {
							String colorName = limitProductSkuRsp.getColorName();
							String sizeName = limitProductSkuRsp.getSizeName();
							int selectSKU = limitProductSkuRsp.getProductSku();
							limitProductVo.setColorName(colorName);
							limitProductVo.setSizeName(sizeName);
							limitProductVo.setSelectSKU(String.valueOf(selectSKU));
							// 如果选了SKU则表示肯定关联了SKU
							relatedSKU = true;
						}
					}// 有一级缓存，如果用户没有选择对应的SKU则把colorName，sizeName，selectSKU强制置为null
					else {
						limitProductVo.setColorName(null);
						limitProductVo.setSizeName(null);
						limitProductVo.setSelectSKU(null);
					}
					limitProductVo.setRelatedSKU(relatedSKU);
					// 1分钟
					memecacheClientHolder.getLevel1Cache().set(key, 60, limitProductVo);
					memecacheClientHolder.getLevel2Cache().set(LIMITPRODUCT_DETAIL_LEVEL2_KEYPRE + limitProductCode, memecacheClientHolder.getLevel2Expire(), limitProductVo);
				}
			}
			// 对限定商品的后处理
			helper.afterProcessReminder(NumberUtils.toInt(uid), limitProductVo).afterProcessShareUrl(limitProductVo).afterProcessStatus(NumberUtils.toInt(uid), limitProductVo);
			logger.debug("after getLimitProductBoByCode limitProductVo:{},uid:{}", limitProductVo, uid);

			// 如果showFlag为0，表示这个限定商品已经在后台关闭,则showstatus状态置为3，已经售罄,productSKN置为0，不让APP显示查看商品详情
			if (limitProductVo.getShowFlag() == 0) {
				limitProductVo.setShowStatus(3);
				limitProductVo.setProductSkn(0);
			}
			return new ApiResponse.ApiResponseBuilder().data(limitProductVo).code(200).message("detail limitproduct info").build();

		} catch (Throwable e) {
			logger.warn("invoke product.getLimitProductByCode failed", e);
			// 服务不可用时走二级缓存,并回填到一级缓存中
			if (shouldGetFromLevel2(e)) {
				LimitProductVo limitProductVo = getFromLevel2Cache(limitProductCode);
				return new ApiResponse.ApiResponseBuilder().data(limitProductVo).code(200).message("detail limitproduct info").build();
			}
			throw e;
		}
	}

	/**
	 * 从二级缓存获取
	 * 
	 * @return
	 */
	private LimitProductVo getFromLevel2Cache(final String limitProductCode) {
		logger.info("begin getLimitProductBoByCode from Level2Cache limitProductCode is:{}", limitProductCode);
		LimitProductVo level2LimitProductVo = memecacheClientHolder.getLevel2Cache().get(LIMITPRODUCT_DETAIL_LEVEL2_KEYPRE + limitProductCode, LimitProductVo.class);
		if (null != level2LimitProductVo) {
			logger.info("getLimitProductBoByCode from Level2Cache limitProductCode is:{}", limitProductCode);
			// 回填到一级缓存2分钟
			memecacheClientHolder.getLevel1Cache().set(LIMITPRODUCT_DETAIL_LEVEL1_KEYPRE + limitProductCode, 120, level2LimitProductVo);
			return level2LimitProductVo;
		}
		logger.info("end getLimitProductBoByCode from Level2Cache is empty limitProductCode is:{}", limitProductCode);
		return level2LimitProductVo;
	}

	/**
	 * 添加提醒
	 * 
	 * @param uid
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.reminder.addUserReminder")
	@ResponseBody
	public ApiResponse addUserReminder(@RequestParam(value = "limitProductCode", required = true) String limitProductCode,
			@RequestParam(value = "uid", required = false, defaultValue = "") String uid, @RequestParam(value = "token", required = false, defaultValue = "") String token)
			throws GatewayException {
		logger.info("beign addUserReminder limitProductCode is:{},uid is:{},token is:{}", limitProductCode, uid, token);
		if (StringUtils.isEmpty(limitProductCode)) {
			throw new GatewayException(500, "limitProductCode can't be empty");
		}
		if (StringUtils.isBlank(uid) || !NumberUtils.isNumber(uid)) {
			throw new GatewayException(500, "uid can't be empty or uid is not number");
		}
		BaseRequest<String> request = new BaseRequest<String>();
		request.setParam(limitProductCode);
		request.setUserId(NumberUtils.toInt(uid));
		VoidResponse voidResponse = serviceCaller.call("product.addUserReminder", request, VoidResponse.class);
		if (voidResponse.isSuccess()) {
			// 强制打开这个token的状态为1
			pushTokenService.updateUserTokenStatusOpen(uid, token);
			return new ApiResponse.ApiResponseBuilder().data("success").code(200).message("add reminder success").build();
		} else {
			return new ApiResponse.ApiResponseBuilder().data("fail").code(200).message("add reminder fail").build();
		}
	}

	/**
	 * 根据用户ID查询用户的所有提醒
	 * 
	 * @param uid
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.reminder.getUserReminder")
	@ResponseBody
	public ApiResponse queryUserReminder(@RequestParam(value = "uid", required = false, defaultValue = "") String uid) throws GatewayException {
		logger.info("beign queryUserReminder uid is:{}", uid);
		if (StringUtils.isBlank(uid)) {
			throw new GatewayException(500, "uid can't be empty");
		}
		BaseRequest<Integer> request = new BaseRequest<Integer>();
		request.setUserId(NumberUtils.toInt(uid));
		ReminderBo[] reminderBoList = serviceCaller.call("product.queryUserReminder", request, ReminderBo[].class);
		List<ReminderVo> reminderVoList = limitProductConvert.convert(reminderBoList);
		return new ApiResponse.ApiResponseBuilder().data(reminderVoList).code(200).message("getUserReminder success").build();
	}

	/**
	 * 只有不是ServiceException 和 GatewayException 的时候，才需要从二级缓存中获取数据
	 *
	 * @param ex
	 * @return
	 */
	private boolean shouldGetFromLevel2(Throwable ex) {
		if (ex instanceof ServiceException) {
			return false;
		}
		return true;
	}

	/**
	 *
	 * @author xieyong
	 *
	 */
	public static class LimitProductRspPageVoBuilder {
		private LimitProductRspPageVo limitProductRspPageVo;

		public LimitProductRspPageVoBuilder() {
			limitProductRspPageVo = new LimitProductRspPageVo();
		}

		public LimitProductRspPageVoBuilder buildLimitProductVoList(List<LimitProductVo> limitProductVoList) {
			this.limitProductRspPageVo.setLimitProductVoList(limitProductVoList);
			return this;
		}

		public LimitProductRspPageVoBuilder buildCurrentPage(int page) {
			this.limitProductRspPageVo.setPage(page);
			return this;
		}

		public LimitProductRspPageVoBuilder buildPageTotal(int pageTotal) {
			this.limitProductRspPageVo.setPage_total(pageTotal);
			return this;
		}

		public LimitProductRspPageVoBuilder buildTotal(int total) {
			this.limitProductRspPageVo.setTotal(total);
			return this;
		}

		public LimitProductRspPageVo toBuilder() {
			return limitProductRspPageVo;
		}
	}

	/**
	 * @author xieyong
	 *
	 */
	public static class LimitProductRspPageVo {

		private int page;

		private int page_total;

		private int total;

		private List<LimitProductVo> limitProductVoList;

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			this.page = page;
		}

		public int getPage_total() {
			return page_total;
		}

		public void setPage_total(int page_total) {
			this.page_total = page_total;
		}

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}

		public List<LimitProductVo> getLimitProductVoList() {
			return limitProductVoList;
		}

		public void setLimitProductVoList(List<LimitProductVo> limitProductVoList) {
			this.limitProductVoList = limitProductVoList;
		}
	}
}
