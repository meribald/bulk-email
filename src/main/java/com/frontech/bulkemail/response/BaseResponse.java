package com.frontech.bulkemail.response;

import org.springframework.http.HttpStatus;

import com.frontech.bulkemail.resource.PropertyUtil;

public abstract class BaseResponse {

	private static final String CODE_SUFFIX = ".code";
	private static final String MESSAGE_SUFFIX = ".message";
	private ErrorResponse errorResponse;

	public BaseResponse() {
	}

	public BaseResponse(String errorMessage) {
		this.errorResponse = new ErrorResponse(PropertyUtil.getProperty(errorMessage + CODE_SUFFIX),
				PropertyUtil.getProperty(errorMessage + MESSAGE_SUFFIX), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public BaseResponse(String errorMessage, HttpStatus httpStatus) {
		this.errorResponse = new ErrorResponse(PropertyUtil.getProperty(errorMessage + CODE_SUFFIX),
				PropertyUtil.getProperty(errorMessage + MESSAGE_SUFFIX), httpStatus);
	}

	public ErrorResponse getErrorResponse() {
		return errorResponse;
	}

	public void setErrorResponse(ErrorResponse errorResponse) {
		this.errorResponse = errorResponse;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorResponse = new ErrorResponse(PropertyUtil.getProperty(errorMessage + CODE_SUFFIX),
				PropertyUtil.getProperty(errorMessage + MESSAGE_SUFFIX), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public void setErrorMessage(String errorMessage, HttpStatus httpStatus) {
		this.errorResponse = new ErrorResponse(PropertyUtil.getProperty(errorMessage + CODE_SUFFIX),
				PropertyUtil.getProperty(errorMessage + MESSAGE_SUFFIX), httpStatus);
	}

	public static class ErrorResponse {

		private String code;
		private String message;
		private HttpStatus httpsStatus;

		public ErrorResponse(String code, String message, HttpStatus httpStatus) {
			this.setCode(code);
			this.setMessage(message);
			this.setHttpsStatus(httpStatus);
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public HttpStatus getHttpsStatus() {
			return httpsStatus;
		}

		public void setHttpsStatus(HttpStatus httpsStatus) {
			this.httpsStatus = httpsStatus;
		}
	}
}
