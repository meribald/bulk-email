package com.frontech.bulkemail.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class GetPaginatedEmailRequest extends BaseRequest {

	@NotNull(message = "{page.notNull}")
	@Min(value = 0, message = "{page.invalid}")
	private Integer page;
	@NotNull(message = "{size.notNull}")
	@Min(value = 1, message = "{size.invalid}")
	private Integer size;

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
}
