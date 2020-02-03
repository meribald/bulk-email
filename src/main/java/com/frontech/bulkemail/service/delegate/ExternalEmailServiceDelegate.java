package com.frontech.bulkemail.service.delegate;

import java.util.Optional;

import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.frontech.bulkemail.service.delegate.dto.EmailBatchDto;

@Component
public class ExternalEmailServiceDelegate {

	private final RestTemplate restTemplate;

	public ExternalEmailServiceDelegate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Retryable(value = ResourceAccessException.class, maxAttemptsExpression = "${max-retry-attempts}")
	public Optional<EmailBatchDto> getEmailBatch(String resourceUrl) {
		EmailBatchDto emailBatch = restTemplate.getForObject(resourceUrl, EmailBatchDto.class);
		return Optional.of(emailBatch);
	}

	@Recover
	public Optional<EmailBatchDto> recover(ResourceAccessException e) {
		return Optional.empty();
	}

}
