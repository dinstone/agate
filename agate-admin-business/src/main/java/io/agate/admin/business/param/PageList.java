package io.agate.admin.business.param;

import java.util.List;

public class PageList<T> {

	private int total;

	private List<T> list;

	public PageList() {
	}

	public PageList(List<T> list) {
		if (list != null) {
			total = list.size();
		}
		this.list = list;
	}

	public PageList(int total) {
		this.total = total;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getList() {
		return list;
	}

	public PageList<T> setList(List<T> list) {
		this.list = list;
		return this;
	}

}
