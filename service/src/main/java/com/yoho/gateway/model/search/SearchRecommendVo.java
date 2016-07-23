package com.yoho.gateway.model.search;

/**
 * 搜索推荐结果Vo对象
 * @author mali
 *
 */
public class SearchRecommendVo {
	/**
	 * 关键词
	 */
	private String keyword;
	
	/**
	 * 关键词对应商品的数量
	 */
	private Integer count;

	public SearchRecommendVo(String keyword, Integer count) {
		this.keyword = keyword;
		this.count = count;
	}

	public SearchRecommendVo() {
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "SearchRecommendVo [keyword=" + keyword + ", count=" + count
				+ "]";
	}
}
