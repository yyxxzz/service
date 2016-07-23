package com.yoho.gateway.model.bigdata;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回对象设置
 * @author mali
 *
 * @param <T>
 * @param <K>
 */
public class PageResponseVO<T, K>  implements Serializable {

	private static final long serialVersionUID = 3575333669955032388L;
	private int page;
	
	private int size;
	
	private int total;
	
	private int totalPage;
	
	private List<T> list;
	
	private K additionInfo;

	public PageResponseVO() {
	}

	public PageResponseVO(int page, int size, int total, List<T> list) {
		this.page = page;
		this.size = size;
		this.total = total;
		this.list = list;
		this.totalPage = 0 == this.total % this.size ? this.total / this.size : this.total / this.size + 1;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
		if (0 != this.total && 0 != this.size) {
			this.totalPage = 0 == this.total % this.size ? this.total / this.size : this.total / this.size + 1;
		}
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
		if (0 != this.size) {
			this.totalPage = 0 == this.total % this.size ? this.total / this.size : this.total / this.size + 1;
		}

	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public K getAdditionInfo() {
		return additionInfo;
	}

	public void setAdditionInfo(K additionInfo) {
		this.additionInfo = additionInfo;
	}

	@Override
	public String toString() {
		return "PageResponseVO [page=" + page + ", size=" + size + ", total="
				+ total + ", totalPage=" + totalPage + ", list=" + list
				+ ", additionInfo=" + additionInfo + "]";
	}

}
