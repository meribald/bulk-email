package com.frontech.bulkemail.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class GetEmailCountRequest extends BaseRequest {

	@NotBlank(message = "{email.notblank}")
	@Pattern(regexp = "^(.+)@(comeon.com|cherry.se)$", message = "{email-domain.notvalid}")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
