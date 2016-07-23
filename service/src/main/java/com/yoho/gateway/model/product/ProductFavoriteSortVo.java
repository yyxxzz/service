package com.yoho.gateway.model.product;

/**
 * 收藏商品分类的vo对象
 * @author mali
 *
 */
public class ProductFavoriteSortVo {
	/**
	 * 收藏商品所属分类的Id
	 */
	private Integer category_id;
	
	/**
	 * 收藏商品所属分类的名称
	 */
	private String category_name;
	
	/**
	 * 某用户收藏某品类下所有商品的数量
	 */
	private Integer num;

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public Integer getCategory_id() {
		return category_id;
	}

	public void setCategory_id(Integer category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	@Override
	public String toString() {
		return "ProductFavoriteSortVo [category_id=" + category_id
				+ ", category_name=" + category_name + ", num=" + num + "]";
	}
}
