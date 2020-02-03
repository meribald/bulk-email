package com.frontech.bulkemail.response;

public class RestResponse {

	private BaseResponse response;

	public RestResponse() {
	}

	public RestResponse(BaseResponse response) {
		this.setResponse(response);
	}

	public BaseResponse getResponse() {
		return response;
	}

	public void setResponse(BaseResponse response) {
		this.response = response;
	}
}
