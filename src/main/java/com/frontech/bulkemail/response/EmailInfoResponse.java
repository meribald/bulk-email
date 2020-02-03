package com.frontech.bulkemail.response;

public class EmailInfoResponse extends BaseResponse {

	private String email;
	private long count;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
