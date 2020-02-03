package com.frontech.bulkemail.response;

import org.springframework.http.HttpStatus;

public class GetEmailResponse extends BaseResponse {

	private String email;
	private Long count;

	public GetEmailResponse(String error, HttpStatus httpStatus) {
		super(error, httpStatus);
	}

	public GetEmailResponse() {
	}

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
