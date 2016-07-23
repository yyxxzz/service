package com.yoho.gateway.controller.users;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.yoho.gateway.controller.message.InboxController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.core.redis.YHValueOperations;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.client.hystrix.AsyncFuture;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.browse.BrowseReqVO;
import com.yoho.gateway.model.response.MyIndexRspVO;
import com.yoho.gateway.service.favorite.IBrowseServiceNew;
import com.yoho.gateway.utils.constants.CacheKeyConstants;
import com.yoho.gateway.utils.constants.Constants;
import com.yoho.product.request.FavoriteReqBo;
import com.yoho.service.model.inbox.request.InboxReqBO;
import com.yoho.service.model.order.request.OrdersStatusStatisticsRequest;
import com.yoho.service.model.order.response.OrdersStatusStatistics;
import com.yoho.service.model.promotion.request.ProductLimitCodeReq;
import com.yoho.service.model.promotion.request.UserCouponListReq;
import com.yoho.service.model.request.FavoriteCountReqBO;
import com.yoho.service.model.request.YohoCoinLogReqBO;
import com.yoho.service.model.sns.request.BaseSnsRequest;

@Controller
public class MyIndexController {

	@Resource
	ServiceCaller service;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private IBrowseServiceNew browseServiceNew;

	@Resource(name = "yhValueOperations")
	private YHValueOperations<String, String> valueOperations;

	@Resource(name = "yhRedisTemplate")
	private YHRedisTemplate<String, String> redisTemplate;

	// uid为返回参数和返回码
	private final static int UID_NULL_CODE = 500;
	private final static String UID_NULL_MSG = "uid is null";

	// 获取首页成功返回
	private final static int SUCCESS_CODE = 200;
	private final static String SUCCESS_MSG = "Get info num is success!";

	// 收藏的品牌 URL
	private final static String BRAND_FAVORITE_TOTAL_URL = "users.getFavoriteBrandCount";
	// 收藏的商品 URL
	private final static String PRODUCT_FAVORITE_TOTAL_URL = "product.getFavoriteCount";
	// 未读消息 URL
	private final static String INBOX_TOTAL_URL = "message.getInboxCount";
	// 有货币总数 URL
	private final static String YOHO_COIN_NUM_URL = "users.getYohoCoinNum";
	// 优惠券总数 URL
	private final static String COUPON_NUM_URL = "promotion.queryUserCouponsCnt";
	// 订单统计的 URL
	private final static String ORDER_STATISTICS_URL = "order.getOrdersStatusStatisticsByUid";
	// 已晒单的 URL
	private final static String COMMENT_STATISTICS_URL = "sns.queryCommentNum";
	// 待晒单的 URL
	private final static String TOSHAREORDERNUM_STATISTICS_URL = "sns.queryToShareOrderNum";
	// 限购码总数的 URL
	private final static String LIMITCODENUM_STATISTICS_URL = "promotion.queryLimitCodeRecordNum";

	private Logger logger = LoggerFactory.getLogger(MyIndexController.class);

	@RequestMapping(params = "method=app.home.getInfoNum")
	@ResponseBody
	public ApiResponse getMyIndex(@RequestParam(value = "uid") int uid, @RequestParam(value = "udid", required = false) String udid) throws GatewayException {
		logger.info("Enter getMyIndex controller. uid is {}, udid is {}", uid, udid);
		// (1)进行参数校验，首先判断用户uid是否为空
		if (uid < 1) {
			logger.warn("request param uid is null or 0.");
			throw new GatewayException(UID_NULL_CODE, UID_NULL_MSG);
		}

		// (2.1)获取待付款,待发货,待收货,退换货的数量. 首先从缓存中取订单相关的数量, 如果缓存的记录为空, 异步发送请求,
		// 调用服务,取出记录
		AsyncFuture<OrdersStatusStatistics> ordersStatusStatisticsAsync = null;
		String waitPayNumStr = getValueFromRedis(CacheKeyConstants.YHGW_WAITPAYNUM_PRE + uid);// 待付款
		String waitCargoNumStr = getValueFromRedis(CacheKeyConstants.YHGW_WAITCARGONUM_PRE + uid);// 待发货
		String sendCargoNumStr = getValueFromRedis(CacheKeyConstants.YHGW_SENDCARGONUM_PRE + uid);// 待收获
		String refundExchangeNumStr = getValueFromRedis(CacheKeyConstants.YHGW_REFUNDEXCHANGENUM_PRE + uid);// 退换货
		try {
			if (StringUtils.isEmpty(waitPayNumStr) || StringUtils.isEmpty(waitCargoNumStr) || StringUtils.isEmpty(sendCargoNumStr) || StringUtils.isEmpty(refundExchangeNumStr)) {
				logger.debug("getMyIndex: Begin request order.getOrdersStatusStatisticsByUid. uid is {}", uid);
				OrdersStatusStatisticsRequest orderRequest = new OrdersStatusStatisticsRequest();
				orderRequest.setUid(uid);
				ordersStatusStatisticsAsync = service.asyncCall(ORDER_STATISTICS_URL, orderRequest, OrdersStatusStatistics.class);
				logger.debug("getMyIndex: End request order.getOrdersStatusStatisticsByUid.");
			}
		} catch (Exception e) {
			logger.warn("getMyIndex error: request order.getOrdersStatusStatisticsByUid failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}

		// (2.2)获取收藏的品牌的数量, 先从REDIS中取数据, REDIS记录为空, 异步发送请求, 调用服务取数据
		AsyncFuture<Integer> brandFavoriteTotalAsync = null;
		String brandFavNumStr = getValueFromRedis(CacheKeyConstants.YHGW_BRANDFAVNUM_PRE + uid); // REDIS中获取收藏的品牌的数量
		try {
			if (StringUtils.isEmpty(brandFavNumStr)) {
				logger.debug("getMyIndex: Begin request users.getFavoriteBrandCount. uid is {}", uid);
				FavoriteCountReqBO favoriteReqBO = new FavoriteCountReqBO(uid);
				brandFavoriteTotalAsync = service.asyncCall(BRAND_FAVORITE_TOTAL_URL, favoriteReqBO, Integer.class);
				logger.debug("getMyIndex: End request users.getFavoriteBrandCount.");
			}
		} catch (Exception e) {
			logger.warn("getMyIndex error: request users.getFavoriteBrandCount failed. uid is {} , errorMsg is {}", uid, e.getMessage());
		}

		// (2.3)获取收藏的商品的数量, 先从REDIS中取数据, REDIS记录为空, 异步发送请求, 调用服务取数据
		AsyncFuture<Integer> productFavoriteTotalAsync = null;
		String productFavNumStr = getValueFromRedis(CacheKeyConstants.YHGW_PRODUCTFAVNUM_PRE + uid);
		try {
			if (StringUtils.isEmpty(productFavNumStr)) {
				logger.debug("getMyIndex: Begin request users.getFavoriteProductCount. uid is {}", uid);
				FavoriteReqBo favoriteReqBO = new FavoriteReqBo();
				favoriteReqBO.setType("product");
				favoriteReqBO.setUid(uid);
				productFavoriteTotalAsync = service.asyncCall(PRODUCT_FAVORITE_TOTAL_URL, favoriteReqBO, Integer.class);
				logger.debug("getMyIndex: End request users.getFavoriteProductCount.");
			}
		} catch (Exception e) {
			logger.warn("getMyIndex error: request users.getFavoriteProductCount failed. uid is {} , errorMsg is {}", uid, e.getMessage());
		}

		// (2.4)获取消息盒子的数量, 先从REDIS中取数据, REDIS记录为空, 异步发送请求, 调用服务取数据
		AsyncFuture<Integer> inboxTotalAsync = null;
		String inboxNumStr = getValueFromRedis(CacheKeyConstants.YHGW_INBOXNUM_PRE + uid);
		try {
			if (StringUtils.isEmpty(inboxNumStr)) {
				logger.debug("getMyIndex: Begin request message.getInboxCount. uid is {}", uid);
				InboxReqBO InboxReqBO = new InboxReqBO(uid, "N");
				logger.info("inbox switch is {}",InboxController.flag);
				if(InboxController.flag){
					inboxTotalAsync = service.post("brower.getInboxCount", DynamicPropertyFactory.getInstance().getStringProperty("browse.service.url", "http://localhost:8092/brower/").get() + "/InBoxRest/getInboxCount", InboxReqBO, Integer.class, null);
				}else {
					inboxTotalAsync = service.asyncCall(INBOX_TOTAL_URL, InboxReqBO, Integer.class);
				}
				logger.debug("getMyIndex: End request message.getInboxCount.");
			}
		} catch (Exception e) {
			logger.warn("getMyIndex error: request message.getInboxCount. failed. uid is {} , errorMsg is {}", uid, e.getMessage());
		}

		// (2.5)获取有货币的数量, 先从REDIS中取数据, REDIS没有数据, 异步发送请求, 调用服务,获取有货币的数量.
		AsyncFuture<Integer> yohoCoinNumAsync = null;
		String yohoCoinNumStr = getValueFromRedis(CacheKeyConstants.YHGW_YOHOCOINNUM_PRE + uid);
		try {
			if (StringUtils.isEmpty(yohoCoinNumStr)) {
				logger.debug("getMyIndex: Begin request users.getYohoCoinNum. uid is {}", uid);
				YohoCoinLogReqBO yohoCoinLogReqBO = new YohoCoinLogReqBO(uid);
				yohoCoinNumAsync = service.asyncCall(YOHO_COIN_NUM_URL, yohoCoinLogReqBO, Integer.class);
				logger.debug("getMyIndex: End request users.getYohoCoinNum.");
			}
		} catch (Exception e) {
			logger.warn("getMyIndex error: request users.getYohoCoinNum. failed. uid is {} , errorMsg is {}", uid, e.getMessage());
		}

		// (2.6)获取优惠券的数量, 先从REDIS中取数据, REDIS没有数据, 异步发送请求, 调用服务,获取优惠券的数量.
		AsyncFuture<Integer> couponNumAsync = null;
		String couponNumStr = getValueFromRedis(CacheKeyConstants.YHGW_COUPONNUM_PRE + uid);
		try {
			if (StringUtils.isEmpty(couponNumStr)) {
				logger.debug("getMyIndex: Begin request users.getCouponsCount. uid is {}", uid);
				// 默认未使用
				UserCouponListReq userCouponListReq = new UserCouponListReq();
				userCouponListReq.setUid(uid);
				couponNumAsync = service.asyncCall(COUPON_NUM_URL, userCouponListReq, Integer.class);
				logger.debug("getMyIndex: End request users.getCouponsCount.");
			}
		} catch (Exception e) {
			logger.warn("getMyIndex error: request users.getCouponsCount. failed. uid is {} , errorMsg is {}", uid, e.getMessage());
		}

		// (2.7)获取评论的数量, 先从REDIS中取数据, REDIS没有数据, 异步发送请求, 调用服务,获取用户评论的数量.
		AsyncFuture<Integer> commentNumAsync = null;
		String commentNumStr = getValueFromRedis(CacheKeyConstants.YHGW_COMMENTNUM_PRE + uid);
		try {
			if (StringUtils.isEmpty(commentNumStr)) {
				logger.debug("getMyIndex: Begin request comments.queryCommentNum. uid is {}", uid);
				BaseSnsRequest<String> snsRequest = new BaseSnsRequest<String>();
				snsRequest.setUid(String.valueOf(uid));
				commentNumAsync = service.asyncCall(COMMENT_STATISTICS_URL, snsRequest, Integer.class);
				logger.debug("getMyIndex: End request comments.queryCommentNum.");
			}
		} catch (Exception e) {
			logger.warn("getMyIndex error: request comments.queryCommentNum failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}

		// (2.8)获取待晒单的数量, 先从REDIS中取数据, REDIS没有数据, 异步发送请求, 调用服务,获取用户待晒单的数量.
		AsyncFuture<Integer> toShareOrderNumAsync = null;
		String toShareOrderNumStr = getValueFromRedis(CacheKeyConstants.YHGW_TOSHAREORDERNUM_PRE + uid);
		try {
			if (StringUtils.isEmpty(toShareOrderNumStr)) {
				logger.debug("getMyIndex: Begin request show.queryToShareOrderNum. uid is {}", uid);
				BaseSnsRequest<String> snsRequest = new BaseSnsRequest<String>();
				snsRequest.setUid(String.valueOf(uid));
				toShareOrderNumAsync = service.asyncCall(TOSHAREORDERNUM_STATISTICS_URL, snsRequest, Integer.class);
				logger.debug("getMyIndex: End request show.queryToShareOrderNum.");
			}
		} catch (Exception e) {
			logger.warn("getMyIndex error: request show.queryToShareOrderNum failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}

		// (2.9)获取限购码的数量, 先从REDIS中取数据, REDIS没有数据, 异步发送请求, 调用服务, 调用获取限购码的数量
		AsyncFuture<Integer> limitCodeRecordNumAsync = null;
		String limitCodeRecordNumStr = getValueFromRedis(CacheKeyConstants.YHGW_LIMITCODERECORDNUM_PRE + uid);
		try {
			if (null == limitCodeRecordNumStr) {
				logger.debug("getMyIndex: Begin request promotion.queryLimitCodeRecordNum. uid is {}", uid);
				ProductLimitCodeReq limitCodeReq = new ProductLimitCodeReq();
				limitCodeReq.setUid(uid);
				limitCodeRecordNumAsync = service.asyncCall(LIMITCODENUM_STATISTICS_URL, limitCodeReq, Integer.class);
				logger.debug("getMyIndex: End request promotion.queryLimitCodeRecordNum.");
			}
		} catch (Exception e) {
			logger.warn("getMyIndex error: request promotion.queryLimitCodeRecordNum failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}

		// (2.10)获取浏览记录的数量, 待付款,待发货,待收货,退换货的数量
		int productBrowse = 0;
		try {
			logger.debug("getMyIndex: get browse count. uid is {}", uid);
			udid = StringUtils.isEmpty(udid) ? "" : udid;
			if (StringUtils.isNotEmpty(udid)) {
				BrowseReqVO browseReqVO = new BrowseReqVO(uid, udid);
				productBrowse = browseServiceNew.totalBrowse(browseReqVO);
			}
			logger.debug("getMyIndex: End request users.getBrowseCount");
		} catch (Exception e) {
			logger.warn("getMyIndex error: request users.getBrowseCount failed. uid is {} , errorMsg is {}", uid, e.getMessage());
		}

		// (3.1 )待付款,待发货,待收货,退换货的数量
		int waitPayNum = 0, waitCargoNum = 0, sendCargoNum = 0, brandFavoriteTotal = 0, productFavoriteTotal = 0, inboxTotal = 0, commentTotal = 0, refundExchangeNum = 0, yohoCoinNum = 0, couponNum = 0, showOrderNum = 1, toShareOrderNum = 1, limitCodeNum = 0;
		try {
			if (!StringUtils.isEmpty(waitPayNumStr) && !StringUtils.isEmpty(waitCargoNumStr) && !StringUtils.isEmpty(sendCargoNumStr) && !StringUtils.isEmpty(refundExchangeNumStr)) {
				waitPayNum = Integer.parseInt(waitPayNumStr);
				waitCargoNum = Integer.parseInt(waitCargoNumStr);
				sendCargoNum = Integer.parseInt(sendCargoNumStr);
				refundExchangeNum = Integer.parseInt(refundExchangeNumStr);
			} else if (null != ordersStatusStatisticsAsync) {
				OrdersStatusStatistics ordersStatusStatistics = ordersStatusStatisticsAsync.get();
				waitPayNum = ordersStatusStatistics.getPendingPaymentCount();
				waitCargoNum = ordersStatusStatistics.getDueOutGoodsCount();
				sendCargoNum = ordersStatusStatistics.getDueInGoodsCount();
				refundExchangeNum = ordersStatusStatistics.getRefundGoodsCount();

				setToRedis(CacheKeyConstants.YHGW_WAITPAYNUM_PRE + uid, String.valueOf(waitPayNum), 10, TimeUnit.SECONDS);
				setToRedis(CacheKeyConstants.YHGW_WAITCARGONUM_PRE + uid, String.valueOf(waitCargoNum), 10, TimeUnit.SECONDS);
				setToRedis(CacheKeyConstants.YHGW_SENDCARGONUM_PRE + uid, String.valueOf(sendCargoNum), 10, TimeUnit.SECONDS);
				setToRedis(CacheKeyConstants.YHGW_REFUNDEXCHANGENUM_PRE + uid, String.valueOf(refundExchangeNum), 10, TimeUnit.SECONDS);
			}
			logger.debug("getMyIndex: End get order.getOrdersStatusStatisticsByUid.waitPayNum is {}, waitCargoNum is {}, sendCargoNum is {}, refundExchangeNum is {}", waitPayNum, waitCargoNum,
					sendCargoNum, refundExchangeNum);
		} catch (Exception e) {
			logger.warn("getMyIndex error: get order.getOrdersStatusStatisticsByUid failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}
		// (3.2)获取收藏品牌数量
		try {
			logger.debug("getMyIndex: Begin get users.getFavoriteBrandCount. brandFavoriteTotalAsync is {}", brandFavoriteTotalAsync);
			if (!StringUtils.isEmpty(brandFavNumStr)) {
				brandFavoriteTotal = Integer.parseInt(brandFavNumStr);
			} else if (null != brandFavoriteTotalAsync) {
				brandFavoriteTotal = brandFavoriteTotalAsync.get();
				setToRedis(CacheKeyConstants.YHGW_BRANDFAVNUM_PRE + uid, String.valueOf(brandFavoriteTotal), 10, TimeUnit.SECONDS);
			}
			logger.debug("getMyIndex: End get users.getFavoriteBrandCount. brandFavoriteTotal is {}", brandFavoriteTotal);
		} catch (Exception e) {
			logger.warn("getMyIndex error: get users.getFavoriteBrandCount failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}

		// (3.3)获取收藏商品数量, 缓存时间5秒
		try {
			logger.debug("getMyIndex: Begin get users.getFavoriteProductCount. productFavoriteTotalAsync is {}", productFavoriteTotalAsync);
			if (!StringUtils.isEmpty(productFavNumStr)) {
				productFavoriteTotal = Integer.parseInt(productFavNumStr);
			} else if (null != productFavoriteTotalAsync) {
				productFavoriteTotal = productFavoriteTotalAsync.get();
				setToRedis(CacheKeyConstants.YHGW_PRODUCTFAVNUM_PRE + uid, String.valueOf(productFavoriteTotal), 5, TimeUnit.SECONDS);
			}
			logger.debug("getMyIndex: End get users.getFavoriteProductCount. productFavoriteTotal is {}", productFavoriteTotal);
		} catch (Exception e) {
			logger.warn("getMyIndex error: get users.getFavoriteProductCount failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}

		// (3.4)获取未读消息数量, 缓存时间60秒
		try {
			logger.debug("getMyIndex: Begin get message.getInboxCount. inboxTotalAsync is {}", inboxTotalAsync);
			if (!StringUtils.isEmpty(inboxNumStr)) {
				inboxTotal = Integer.parseInt(inboxNumStr);
			} else if (null != inboxTotalAsync) {
				DynamicLongProperty inboxNumExpireTime = DynamicPropertyFactory.getInstance().getLongProperty(Constants.INBOX_NUM_EXPIRETIME_NAME, 120);
                inboxTotal = inboxTotalAsync.get();
                setToRedis(CacheKeyConstants.YHGW_INBOXNUM_PRE + uid, String.valueOf(inboxTotal), inboxNumExpireTime.get(), TimeUnit.SECONDS);
			}
			logger.debug("getMyIndex: End get message.getInboxCount. inboxTotal is {}", inboxTotal);
		} catch (Exception e) {
			logger.warn("getMyIndex error: get message.getInboxCount failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}

		// (3.5)获取有货币数量, 缓存时间30秒
		try {
			logger.debug("getMyIndex: Begin get users.getYohoCoinNum. yohoCoinNumAsync is {}", yohoCoinNumAsync);
			if (!StringUtils.isEmpty(yohoCoinNumStr)) {
				yohoCoinNum = Integer.parseInt(yohoCoinNumStr);
			} else if (null != yohoCoinNumAsync) {
				yohoCoinNum = yohoCoinNumAsync.get();
				setToRedis(CacheKeyConstants.YHGW_YOHOCOINNUM_PRE + uid, String.valueOf(yohoCoinNum), 10, TimeUnit.SECONDS);
			}
			logger.debug("getMyIndex: End get users.getYohoCoinNum. yohoCoinNum is {}", yohoCoinNum);
		} catch (Exception e) {
			logger.warn("getMyIndex error: get users.getYohoCoinNum failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}

		// (3.6)获取优惠券数量, 缓存时间30秒
		try {
			logger.debug("getMyIndex: Begin get users.getCouponsCount. couponNumAsync is {}", couponNumAsync);
			if (!StringUtils.isEmpty(couponNumStr)) {
				couponNum = Integer.parseInt(couponNumStr);
			} else if (null != couponNumAsync) {
				couponNum = couponNumAsync.get();
				setToRedis(CacheKeyConstants.YHGW_COUPONNUM_PRE + uid, String.valueOf(couponNum), 10, TimeUnit.SECONDS);
			}
			logger.debug("getMyIndex: End get users.getCouponsCount. couponNum is {}", couponNum);
		} catch (Exception e) {
			logger.warn("getMyIndex error: get users.getCouponsCount failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}

		// (3.7)已评价数量,缓存时间30秒
		try {
			logger.debug("getMyIndex: Begin get comments.queryCommentNum. commentNumAsync is {}", commentNumAsync);
			if (!StringUtils.isEmpty(commentNumStr)) {
				showOrderNum = Integer.parseInt(commentNumStr);
			} else if (null != commentNumAsync) {
				showOrderNum = commentNumAsync.get();
				setToRedis(CacheKeyConstants.YHGW_COMMENTNUM_PRE + uid, String.valueOf(showOrderNum), 30, TimeUnit.SECONDS);
			}
			logger.debug("getMyIndex: End get comments.queryCommentNum. showOrderNum is {}", showOrderNum);
		} catch (Exception e) {
			logger.warn("getMyIndex error: get comments.queryCommentNum failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}

		// (3.8)待晒单数量, 缓存时间30秒
		try {
			logger.debug("getMyIndex: Begin get show.queryToShareOrderNum. toShareOrderNumAsync is {}", toShareOrderNumAsync);
			if (!StringUtils.isEmpty(toShareOrderNumStr)) {
				toShareOrderNum = Integer.parseInt(toShareOrderNumStr);
			} else if (null != toShareOrderNumAsync) {
				toShareOrderNum = toShareOrderNumAsync.get();
				setToRedis(CacheKeyConstants.YHGW_TOSHAREORDERNUM_PRE + uid, String.valueOf(toShareOrderNum), 30, TimeUnit.SECONDS);
			}
			logger.debug("getMyIndex: End get show.queryToShareOrderNum. toShareOrderNum is {}", toShareOrderNum);
		} catch (Exception e) {
			logger.warn("getMyIndex error: get show.queryToShareOrderNum failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}

		// (3.10)限购码数量, 缓存时间30秒
		try {
			logger.debug("getMyIndex: Begin get promotion.queryLimitCodeRecordNum. limitCodeRecordNumAsync is {}", limitCodeRecordNumAsync);
			if (!StringUtils.isEmpty(limitCodeRecordNumStr)) {
				limitCodeNum = Integer.parseInt(limitCodeRecordNumStr);
			} else if (null != limitCodeRecordNumAsync) {
				limitCodeNum = limitCodeRecordNumAsync.get();
				setToRedis(CacheKeyConstants.YHGW_LIMITCODERECORDNUM_PRE + uid, String.valueOf(limitCodeNum), 10, TimeUnit.SECONDS);
			}
			logger.debug("getMyIndex: End get promotion.queryLimitCodeRecordNum. limitCodeNum is {}", limitCodeNum);
		} catch (Exception e) {
			logger.warn("getMyIndex error: get promotion.queryLimitCodeRecordNum failed. uid is {}, errorMsg is {}", uid, e.getMessage());
		}
		// (12)组装返回app参数
		MyIndexRspVO indexRspVO = new MyIndexRspVO(waitPayNum, waitCargoNum, sendCargoNum, brandFavoriteTotal, productFavoriteTotal, inboxTotal, commentTotal, refundExchangeNum, yohoCoinNum,
				couponNum, productBrowse, showOrderNum, toShareOrderNum, limitCodeNum);
		ApiResponse apiResponse = new ApiResponse.ApiResponseBuilder().code(SUCCESS_CODE).message(SUCCESS_MSG).data(indexRspVO).build();
		return apiResponse;
	}

	private String getValueFromRedis(String key) {
		logger.info("Redis. get value in myIndex. key is {}", key);
		try {
			if (redisTemplate.hasKey(key)) {
				return valueOperations.get(key);
			}
		} catch (Exception e) {
			logger.warn("getValueFromRedis error: key is {}, error message is {}", key, e.getMessage());
		}
		return null;
	}

	private void setToRedis(String key, String value, long expire, TimeUnit timeUnit) {
		try {
			valueOperations.setIfAbsent(key, value);
			redisTemplate.longExpire(key, expire, timeUnit);
		} catch (Exception e) {
			logger.warn("setToRedis error: key is {}, value is {}, expire is {}, error message is {}", key, value, expire, e.getMessage());
		}
	}

	@RequestMapping(params = "method=app.home.getinfonum")
	@ResponseBody
	public ApiResponse getinfonum(@RequestParam(value = "uid") int uid, @RequestParam(value = "udid", required = false) String udid) throws GatewayException {
		return this.getMyIndex(uid, udid);
	}
}
