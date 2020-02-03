package com.frontech.bulkemail.response;

import org.springframework.http.HttpStatus;

public class CreateEmailResponse extends BaseResponse {

	public CreateEmailResponse(String error, HttpStatus httpStatus) {
		super(error, httpStatus);
	}

	public CreateEmailResponse() {
	}
}
