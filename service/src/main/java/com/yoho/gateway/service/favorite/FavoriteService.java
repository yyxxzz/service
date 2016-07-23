package com.yoho.gateway.service.favorite;

/*import java.util.List;*/
import java.util.List;
import java.util.Map;






import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.model.product.ShowProductFavoriteVo;
/*import com.yoho.gateway.model.product.ProductFavoriteSortVo;*/
import com.yoho.product.request.FavoriteReqBo;
import com.yoho.service.model.response.BrandFavoriteRespBO;

/**
 * 收藏接口
 * @author mali
 *
 */
public interface FavoriteService {
	/**
	 * 查询收藏的商品
	 * @param favoriteReqBO
	 * @return
	 */
	Map<String, Object> queryFavoriteProductList(FavoriteReqBo favoriteReqBO);
/*	
	*//**
	 * 查询收藏商品的分类列表
	 * @param uid
	 * @return
	 *//*
	List<ProductFavoriteSortVo> queryFavoriteProductSortList(String uid);*/
	
	/**
	 * 查询收藏的商品(包含促销信息)
	 * @param favoriteReqBO
	 * @return
	 */
	Map<String, Object> queryWebFavoriteProductList(FavoriteReqBo favoriteReqBO);
	
	/**
	 * show频道查询收藏的商品(只包含基本信息)
	 * @param favoriteReqBO
	 * @return
	 */
	List<ShowProductFavoriteVo> queryShowFavoriteProductList(FavoriteReqBo favoriteReqBO);
	
	/**
	 * 处理返回结果，对品牌或店铺的跳转规则做处理
	 * 无店铺：0--->品牌页 
     * 无单品店有多品店：1--->搜索页 
     * 有单品店：2--->店铺页面
	 * @param result
	 */
	void getBrandOrShopForwardType(List<JSONObject> list);
}
