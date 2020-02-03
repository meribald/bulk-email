package com.frontech.bulkemail.service.delegate;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.frontech.bulkemail.service.delegate.dto.EmailBatchDto;

public class ExternalEmailResponseBuilder implements Callable<Void> {

	private ExternalEmailServiceDelegate externalEmailServiceDelegate;
	private String resourceUrl;
	private ConcurrentLinkedQueue<EmailBatchDto> responseEmailBatches;

	public ExternalEmailResponseBuilder(ExternalEmailServiceDelegate externalEmailServiceDelegate, String resourceUrl,
			ConcurrentLinkedQueue<EmailBatchDto> responseEmailBatches) {
		this.externalEmailServiceDelegate = externalEmailServiceDelegate;
		this.resourceUrl = resourceUrl;
		this.responseEmailBatches = responseEmailBatches;
	}

	@Override
	public Void call() {
		Optional<EmailBatchDto> emailBatchOptional = externalEmailServiceDelegate.getEmailBatch(resourceUrl);

		if (emailBatchOptional.isPresent()) {
			responseEmailBatches.add(emailBatchOptional.get());
		}

		return null;
	}

}
