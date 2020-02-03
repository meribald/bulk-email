package com.frontech.bulkemail.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UpdateEmailInfoRequest extends BaseRequest {

	@NotBlank(message = "{email.notblank}")
	@Pattern(regexp = "^(.+)@(comeon.com|cherry.se)$", message = "{email-domain.notvalid}")
	private String email;

	@NotNull(message = "{count.notnull}")
	@Min(value = 0, message = "{count.invalid}")
	private Long count;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}
}
