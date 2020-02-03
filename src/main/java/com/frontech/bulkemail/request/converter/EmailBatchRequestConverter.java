package com.frontech.bulkemail.request.converter;

import com.frontech.bulkemail.request.EmailBatchRequest;
import com.frontech.bulkemail.service.delegate.dto.EmailBatchDto;

public class EmailBatchRequestConverter {

	private final static EmailBatchRequestConverter INSTANCE = new EmailBatchRequestConverter();

	private EmailBatchRequestConverter() {
	}

	public final static EmailBatchRequestConverter instance() {
		return INSTANCE;
	}

	public EmailBatchDto convert(EmailBatchRequest request) {
		EmailBatchDto emailBatchDto = new EmailBatchDto();
		emailBatchDto.setEmails(request.getEmails());
		emailBatchDto.setResources(request.getResources());

		return emailBatchDto;
	}
}
