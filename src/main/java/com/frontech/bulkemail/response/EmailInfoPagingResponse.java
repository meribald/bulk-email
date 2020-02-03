package com.frontech.bulkemail.response;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

public class EmailInfoPagingResponse extends BaseResponse {

	private Page<EmailInfoResponse> emailPagingResponse;

	public EmailInfoPagingResponse(Page<EmailInfoResponse> emailPagingResponse) {
		this.emailPagingResponse = emailPagingResponse;
	}

	public EmailInfoPagingResponse(String errorMessage) {
		super(errorMessage);
	}

	public EmailInfoPagingResponse(String errorMessage, HttpStatus httpStatus) {
		super(errorMessage, httpStatus);
	}

	public Page<EmailInfoResponse> getEmailPagingResponse() {
		return emailPagingResponse;
	}

	public void setEmailPagingResponse(Page<EmailInfoResponse> emailPagingResponse) {
		this.emailPagingResponse = emailPagingResponse;
	}

}
