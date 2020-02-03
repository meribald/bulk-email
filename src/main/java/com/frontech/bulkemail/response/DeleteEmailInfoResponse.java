package com.frontech.bulkemail.response;

import org.springframework.http.HttpStatus;

public class DeleteEmailInfoResponse extends BaseResponse {

	public DeleteEmailInfoResponse(String error, HttpStatus httpStatus) {
		super(error, httpStatus);
	}

	public DeleteEmailInfoResponse() {
	}
}
