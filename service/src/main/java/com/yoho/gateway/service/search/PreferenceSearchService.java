package com.yoho.gateway.service.search;

import com.alibaba.fastjson.JSONArray;
import com.yoho.gateway.model.search.ProductSearchReq;

/**
 * 为您优选商品列表的接口
 * @author wss
 *
 */
public interface PreferenceSearchService {

	/**
 	 * 	商品详情页
		底部增加“为您优选新品”，共显示9个，可左右滑动，商品顺序随机显示，点击进入商品详情页
		商品规则：同品牌同品类30天内上新随机取3个商品，同品牌其他品类30天上新随机取6个商品。如果同品类商品数量不足，用其他品类的补足。
		商品区分男女，童装和家居选取全部。点击进入商品详情页。
 	 * @param request
 	 * @return
 	 */
	JSONArray queryPreference(ProductSearchReq req);

	/**
	  * 购物车
		        购物车无商品时，底部增加“为您优选新品”，随机显示9个商品，可左右滑动，点击进入商品详情页
		商品规则：14天内上新的商品，商品组成：2上衣+2裤装+2鞋靴+1包+1配饰+1创意生活。若某品类无上新，随机取其他品类补足。
		当购物车有商品，全部删除后，先不显示“为您优选”，再次进入购物车时才显示。
		商品区分男女，童装和家居选取全部。点击进入商品详情页。
	          个人中心
		         底部增加“为您优选新品”，随机显示9个商品，可左右滑动，点击进入商品详情页
                     商品规则：14天内上新的商品，商品组成：2上衣+2裤装+2鞋靴+1包+1配饰+1创意生活。若某品类无上新，随机取其他品类补足。
                     商品区分男女，童装和家居选取全部。点击进入商品详情页。
	  * @param request
	  * @return
	  */
	JSONArray querySortPreference(ProductSearchReq req);
	
}
