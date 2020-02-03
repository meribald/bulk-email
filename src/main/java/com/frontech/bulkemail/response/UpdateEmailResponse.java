package com.frontech.bulkemail.response;

import org.springframework.http.HttpStatus;

public class UpdateEmailResponse extends BaseResponse {

	public UpdateEmailResponse(String error, HttpStatus httpStatus) {
		super(error, httpStatus);
	}

	public UpdateEmailResponse() {
	}
}
