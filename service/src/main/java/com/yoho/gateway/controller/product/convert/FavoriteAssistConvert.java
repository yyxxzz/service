package com.yoho.gateway.controller.product.convert;

import com.yoho.gateway.model.product.ProductFavoriteSortVo;
import com.yoho.gateway.model.request.ProductFavoriteReqVO;
import com.yoho.product.model.favorite.ProductFavoriteSortBo;
import com.yoho.product.request.FavoriteReqBo;

/**
 * 收藏请求对象Vo和Bo对象的转换器
 * @author mali
 *
 */
public class FavoriteAssistConvert {
	public static final FavoriteReqBo converToBo(ProductFavoriteReqVO vo, String type) {
		FavoriteReqBo bo = new FavoriteReqBo();
		bo.setLimit(vo.getLimit());
		bo.setPage(vo.getPage());
		bo.setSortId(vo.getSortId());
		bo.setUid(vo.getUid());
		
		bo.setType(type);
		return bo;
	}
	
	public static final ProductFavoriteSortVo converToSortVo(ProductFavoriteSortBo bo) {
		ProductFavoriteSortVo vo = new ProductFavoriteSortVo();
		vo.setCategory_id(bo.getSortId());
		vo.setNum(bo.getNum());
		vo.setCategory_name(bo.getSortName());
		return vo;
	}
}
