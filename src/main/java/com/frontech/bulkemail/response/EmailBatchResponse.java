package com.frontech.bulkemail.response;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

public class EmailBatchResponse extends BaseResponse implements Serializable {

	private static final long serialVersionUID = 9108180489713565365L;

	private String result;

	public EmailBatchResponse() {
	}

	public EmailBatchResponse(String result) {
		this.result = result;
	}

	public EmailBatchResponse(String errorMessage, HttpStatus httpStatus) {
		super(errorMessage, httpStatus);
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
