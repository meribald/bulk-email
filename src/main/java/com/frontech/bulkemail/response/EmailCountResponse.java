package com.frontech.bulkemail.response;

import org.springframework.http.HttpStatus;

public class EmailCountResponse extends BaseResponse {

	private long count;

	public EmailCountResponse(long count) {
		this.count = count;
	}

	public EmailCountResponse(String errorMessage) {
		super(errorMessage);
	}

	public EmailCountResponse(String errorMessage, HttpStatus httpStatus) {
		super(errorMessage, httpStatus);
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
