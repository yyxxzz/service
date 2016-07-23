package com.yoho.gateway.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.client.hystrix.AsyncFuture;
import com.yoho.service.model.push.GetuiTokenBO;
import com.yoho.service.model.push.IosTokenBO;

@Service
public class PushTokenService {

	private static final Logger logger = LoggerFactory.getLogger(PushTokenService.class);

	@Resource
	private ServiceCaller serviceCaller;

	public void updateUserTokenStatusOpen(String uid, String token) {
		try {
			logger.info("updateUserTokenStatusOpen ,uid is {},token is {}",uid,token);
			if(StringUtils.isBlank(token)){
				return;
			}
			Integer uidInt = Integer.valueOf(uid);
			this.updateIosTokenStatus(uidInt, token, 1);
			this.updateAndroidTokenStatus(uidInt, token, 1);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	private AsyncFuture<Integer> updateIosTokenStatus(int uid, String token, int status) {
		IosTokenBO iosTokenBO = new IosTokenBO();
		iosTokenBO.setToken(token);
		iosTokenBO.setUid(uid > 0 ? uid : 0);
		iosTokenBO.setStatus((byte) status);
		return serviceCaller.asyncCall("message.publishIosUser", iosTokenBO, Integer.class);
	}
	
	private AsyncFuture<Integer> updateAndroidTokenStatus(int uid, String token, int status) {
		GetuiTokenBO getuiTokenBO = new GetuiTokenBO();
		getuiTokenBO.setGetuiCid(token);
		getuiTokenBO.setUid(uid > 0 ? uid : 0);
		getuiTokenBO.setStatus((byte) status);
		return serviceCaller.asyncCall("message.publishGetuiUser", getuiTokenBO, Integer.class);
	}

}
