package com.yoho.gateway.controller.product;

import java.util.List;

import com.yoho.gateway.cache.expire.product.ExpireTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yoho.core.cache.CacheClient;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.product.model.CommentBo;
import com.yoho.product.request.CommentRequest;

@Controller
public class CommentController {
	
	private Logger logger = LoggerFactory.getLogger(CommentController.class);
	
	//查询时产品ID不能为空
	private final static int PRODUCT_IS_NULL_CODE = 500;
		
	private final static String PRODUCT_IS_NULL_MSG = "产品id不能为空.";
	
	@Autowired
	private ServiceCaller serviceCaller;
	
	@Autowired
	private CacheClient cacheClient;
	
	@RequestMapping(params = "method=app.comment.li")
	@ResponseBody
	public ApiResponse getCommentList(@RequestParam(value="page", required=false, defaultValue="1") int pageNum,
			@RequestParam(value="limit", required=false, defaultValue="10") int pageSize,
			@RequestParam(value="product_id", required=true) int product_id) throws GatewayException{
		logger.info("Begin call getCommentList gateway. Param page is {}, pageSize is {}, product_id is {}",
				pageNum, pageSize, product_id);
		//校验参数product_id不能为空，或者为0
		if(product_id < 1){
			logger.warn("Parameter product_id is {}", product_id);
			throw new GatewayException(PRODUCT_IS_NULL_CODE, PRODUCT_IS_NULL_MSG);
		}
		CommentRequest commentRequest=new CommentRequest();
		commentRequest.setElementId(product_id);
		commentRequest.setPageNum(pageNum);
		commentRequest.setPageSize(pageSize);
		final String key="yh:gw:commentList:"+pageNum+":"+pageSize+":"+product_id;
		final String countKey="yh:gw:commentCount:"+product_id;
		
		CommentBo[] commentBoList=cacheClient.get(key,CommentBo[].class);
		if(null==commentBoList)
		{
			commentBoList=serviceCaller.call("product.queryComments", commentRequest, CommentBo[].class);
			//5分钟
			cacheClient.set(key, ExpireTime.app_comment_li, commentBoList);
		}
		//有评论时，才需要去查询总数
		Integer commentCount=0;
		if(null != commentBoList && commentBoList.length>0)
		{
			commentCount=cacheClient.get(countKey,Integer.class);
			if(null==commentCount)
			{
				commentCount=serviceCaller.call("product.queryCommentCount", commentRequest, Integer.class);
				//5分钟
				cacheClient.set(countKey, ExpireTime.app_comment_li, commentCount);
			}
		}
		List<CommentVo> commentVoList=convert(commentBoList,commentCount);
		return new ApiResponse.ApiResponseBuilder().code(200).message("comment list").data(commentVoList).build();
	}
	
	
	private List<CommentVo> convert(CommentBo[] commentBoList,Integer commentCount) {
		if(null == commentBoList || commentBoList.length<=0)
		{
			return Lists.newArrayList();
		}
		List<CommentVo> commentVoList=Lists.newArrayList();
		CommentVo commentVo=null;
		for (CommentBo commentBo : commentBoList) {
			commentVo=new CommentVo();
			commentVo.setColor_name(commentBo.getColorName());
			commentVo.setContent(commentBo.getContent());
			commentVo.setCreate_time(commentBo.getCreateTime());
			commentVo.setHead_ico(commentBo.getHeadIcon());
			commentVo.setId(commentBo.getId()+"");
			commentVo.setNickname(commentBo.getNickName());
			commentVo.setProduct_id(commentBo.getProductId()+"");
			commentVo.setSize_name(commentBo.getSizeName());
			commentVo.setTotal(commentCount+"");
			commentVoList.add(commentVo);
		}
		return commentVoList;
	}


	public static class CommentVo
	{	
		public String id;
		public String uid;
		public String product_id;
		public String content;
		public String create_time;
		public String size_name;
		public String color_name;
		public String head_ico;
		public String nickname;
		public String total;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getUid() {
			return uid;
		}
		public void setUid(String uid) {
			this.uid = uid;
		}
		public String getProduct_id() {
			return product_id;
		}
		public void setProduct_id(String product_id) {
			this.product_id = product_id;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getCreate_time() {
			return create_time;
		}
		public void setCreate_time(String create_time) {
			this.create_time = create_time;
		}
		public String getSize_name() {
			return size_name;
		}
		public void setSize_name(String size_name) {
			this.size_name = size_name;
		}
		public String getColor_name() {
			return color_name;
		}
		public void setColor_name(String color_name) {
			this.color_name = color_name;
		}
		public String getHead_ico() {
			return head_ico;
		}
		public void setHead_ico(String head_ico) {
			this.head_ico = head_ico;
		}
		public String getNickname() {
			return nickname;
		}
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		public String getTotal() {
			return total;
		}
		public void setTotal(String total) {
			this.total = total;
		}
	}
}
