package com.yoho.gateway.controller.users;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.user.request.activity.AddShareRequestVO;
import com.yoho.gateway.model.user.request.activity.BindAccountRequestVO;
import com.yoho.gateway.model.user.request.activity.GetShareRequestVO;
import com.yoho.service.model.response.CommonRspBO;
import com.yoho.service.model.users.request.activity.AddShareRequestBO;
import com.yoho.service.model.users.request.activity.BindAccountRequestBO;
import com.yoho.service.model.users.request.activity.GetShareRequestBO;
import com.yoho.service.model.users.response.activity.GetListResponseBO;
import com.yoho.service.model.users.response.activity.GetShareResponseBO;

@Controller
public class ActivityController {

	private static final String BIND_ACCOUNT_SUCCESS_MSG = "绑定成功";
	private static final String GET_SHARE_ACCOUNT_SUCCESS_MSG = "分享信息";
	private static final String GET_LIST_SUCCESS_MSG = "活动信息";
	
	static Logger log = LoggerFactory.getLogger(ActivityController.class);
	
	@Resource
	ServiceCaller serviceCaller;
	
	/**
	 * 获取活动列表
	 * 场景：
	 * 	邀请好友赢福利
	 * 操作逻辑
	 * 	1、查询所有活动
	 * 	2、拼装参数，批量查询数据库
	 * 	3、批量查询
	 * 	4、组装返回对象
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=app.activity.getlist")
	@ResponseBody
	public ApiResponse getlist() throws GatewayException {
		log.debug("enter getlist");
		log.info("Begin call getlist gateway.");
		GetListResponseBO[] result = serviceCaller.call("users.getList", null, GetListResponseBO[].class);
		log.info("call users.getList with result is {}", result == null ? 0 : result.length);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(GET_LIST_SUCCESS_MSG).data(result).build();
		return response;
	}
	
	/**
	 * 绑定用户uid与第三方openid
	 * 场景：
	 * 	邀请好友赢福利里面，用户绑定微信账号
	 * 
	 * 操作逻辑：
	 * 	1、根据uid，查询profile信息
	 * 	2、根据openId，查询绑定信息
	 * 	3、如果没有绑定，则插入数据，绑定成功
	 * 	4、如果查询出来的用户id为0，则更新用户id
	 * 	5、如果查询出来的用户id等于用户传入的uid，则直接绑定成功
	 * 	6、如果查询出来的用户id不等于用户传入的uid，则绑定失败
	 * @param req
	 * @throws Exception
	 */
	@RequestMapping(params = "method=app.activity.bindaccount")
	@ResponseBody
	public ApiResponse bindaccount(BindAccountRequestVO vo) throws GatewayException {
		log.debug("enter bindaccount");
		log.info("Begin call bindaccount gateway. with param is {}", vo);
		BindAccountRequestBO bo = new BindAccountRequestBO();
		BeanUtils.copyProperties(vo, bo);
		CommonRspBO result = serviceCaller.call("users.bindAccount", bo, CommonRspBO.class);
		log.info("call users.bindaccount with param is {} with result is {}", vo, result);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(BIND_ACCOUNT_SUCCESS_MSG).build();
		return response;
	}
	
	/**
	 * 获取分享信息
	 * 场景：
	 * 	邀请好友赢福利页面
	 * 操作逻辑：
	 * 	1、根据活动id，查询活动信息
	 * 	2、根据ActivityType，获得share_url的信息
	 * 	3、组装分享的图片地址信息
	 * @param req
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=app.activity.getshare")
	@ResponseBody
	public ApiResponse getshare(GetShareRequestVO vo) throws GatewayException {
		log.debug("enter getshare");
		log.info("Begin call getshare gateway. with param is {}", vo);
		GetShareRequestBO bo = new GetShareRequestBO();
		BeanUtils.copyProperties(vo, bo);
		GetShareResponseBO result = serviceCaller.call("users.getShare", bo, GetShareResponseBO.class);
		log.info("call users.getshare with param is {} with result is {}", vo, result);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(GET_SHARE_ACCOUNT_SUCCESS_MSG).data(result).build();
		return response;
	}
	
	/**
	 * 添加成功分享记录
	 * 场景：
	 * 	邀请好友赢福利
	 * 操作逻辑：
	 * 	1、根据uid，查询用户信息
	 * 	2、根据活动id，查询活动信息
	 * 	3、根据uid，activity_id,url 查询获得信息
	 * 	4、插入活动分享记录
	 * 	5、双十一活动送红包
	 * @param req
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=app.activity.addshare")
	@ResponseBody
	public ApiResponse addshare(AddShareRequestVO vo) throws GatewayException {
		log.debug("enter addshare");
		log.info("Begin call addshare gateway. with param is {}", vo);
		AddShareRequestBO bo = new AddShareRequestBO();
		BeanUtils.copyProperties(vo, bo);
		CommonRspBO result = serviceCaller.call("users.addShare", bo, CommonRspBO.class);
		log.info("call users.addshare with param is {} with result is {}", vo, result);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message("add Share").build();
		return response;
	}
}
