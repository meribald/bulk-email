package com.frontech.bulkemail.model.converter;

import com.frontech.bulkemail.model.EmailInfo;
import com.frontech.bulkemail.response.EmailInfoResponse;

public class EmailInfoConverter {

	private static final EmailInfoConverter INSTANCE = new EmailInfoConverter();

	private EmailInfoConverter() {
	}

	public static final EmailInfoConverter instance() {
		return INSTANCE;
	}

	public EmailInfoResponse convert(EmailInfo emailInfo) {
		EmailInfoResponse response = new EmailInfoResponse();
		response.setEmail(emailInfo.getEmail());
		response.setCount(emailInfo.getCount());
		return response;
	}

}
