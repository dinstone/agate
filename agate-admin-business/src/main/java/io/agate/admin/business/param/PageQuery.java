package io.agate.admin.business.param;

public class PageQuery {

	protected Integer pageSize;
	protected Integer pageIndex;

	public PageQuery() {
		super();
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

}