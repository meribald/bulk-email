package com.frontech.bulkemail.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.frontech.bulkemail.dao.EmailInfoRepository;
import com.frontech.bulkemail.model.EmailInfo;
import com.frontech.bulkemail.model.EmailValidator;
import com.frontech.bulkemail.model.converter.EmailInfoConverter;
import com.frontech.bulkemail.request.CreateEmailInfoRequest;
import com.frontech.bulkemail.request.EmailBatchRequest;
import com.frontech.bulkemail.request.GetEmailCountRequest;
import com.frontech.bulkemail.request.GetPaginatedEmailRequest;
import com.frontech.bulkemail.request.UpdateEmailInfoRequest;
import com.frontech.bulkemail.request.converter.EmailBatchRequestConverter;
import com.frontech.bulkemail.response.CreateEmailResponse;
import com.frontech.bulkemail.response.DeleteEmailInfoResponse;
import com.frontech.bulkemail.response.EmailBatchResponse;
import com.frontech.bulkemail.response.EmailCountResponse;
import com.frontech.bulkemail.response.EmailInfoPagingResponse;
import com.frontech.bulkemail.response.GetEmailResponse;
import com.frontech.bulkemail.response.UpdateEmailResponse;
import com.frontech.bulkemail.service.delegate.ExternalEmailResponseBuilder;
import com.frontech.bulkemail.service.delegate.ExternalEmailServiceDelegate;
import com.frontech.bulkemail.service.delegate.dto.EmailBatchDto;

@Service
@Transactional
public class EmailInfoService {

	private final Map<String, Long> emailCountMap = new HashMap<>();
	private final Lock emailCountMapLock = new ReentrantLock();
	private final EmailInfoRepository emailRepository;
	private final ExternalEmailServiceDelegate externalEmailServiceDelegate;
	private Logger logger = LoggerFactory.getLogger(getClass());

	public EmailInfoService(EmailInfoRepository emailRepository,
			ExternalEmailServiceDelegate externalEmailServiceDelegate) {
		this.emailRepository = emailRepository;
		this.externalEmailServiceDelegate = externalEmailServiceDelegate;
	}

	public EmailBatchResponse saveEmails(EmailBatchRequest request) {

		return validateEmailBatchRequest(request).map(message -> {
			EmailBatchResponse emailBatchResponse = new EmailBatchResponse();
			emailBatchResponse.setErrorMessage(message, HttpStatus.BAD_REQUEST);
			return emailBatchResponse;
		}).orElseGet(() -> {
			addExternalResourceEmailsToCache(EmailBatchRequestConverter.instance().convert(request));
			addEmailsToCache(request.getEmails());
			return new EmailBatchResponse("done");
		});

	}

	public EmailInfoPagingResponse getPaginated(GetPaginatedEmailRequest request) {
		Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by("email"));
		Page<EmailInfo> emailInfoPage = emailRepository.findAll(pageable);
		return new EmailInfoPagingResponse(emailInfoPage.map(EmailInfoConverter.instance()::convert));
	}

	public EmailCountResponse getCount(GetEmailCountRequest request) {
		EmailInfo emailInfo = emailRepository.findByEmail(request.getEmail());

		if (emailInfo == null) {
			return new EmailCountResponse(EmailServiceMessages.EMAIL_NOT_NULL, HttpStatus.NOT_FOUND);
		}

		return new EmailCountResponse(emailInfo.getCount());
	}

	public CreateEmailResponse create(CreateEmailInfoRequest request) {
		EmailInfo existingEmailInfo = emailRepository.findByEmail(request.getEmail());

		if (existingEmailInfo != null) {
			return new CreateEmailResponse(EmailServiceMessages.EMAIL_EXISTS, HttpStatus.BAD_REQUEST);
		}

		EmailInfo emailInfo = new EmailInfo(request.getEmail(), 1L);
		emailRepository.save(emailInfo);

		return new CreateEmailResponse();
	}

	public GetEmailResponse get(String email) {
		EmailInfo emailInfo = emailRepository.findByEmail(email);

		if (emailInfo == null) {
			return new GetEmailResponse(EmailServiceMessages.EMAIL_NOT_NULL, HttpStatus.NOT_FOUND);
		}

		GetEmailResponse getEmailResponse = new GetEmailResponse();
		getEmailResponse.setEmail(emailInfo.getEmail());
		getEmailResponse.setCount(emailInfo.getCount());

		return getEmailResponse;
	}

	public UpdateEmailResponse update(UpdateEmailInfoRequest request) {
		EmailInfo existingEmailInfo = emailRepository.findByEmail(request.getEmail());

		if (existingEmailInfo == null) {
			return new UpdateEmailResponse(EmailServiceMessages.EMAIL_NOT_EXISTS, HttpStatus.BAD_REQUEST);
		}

		existingEmailInfo.setCount(existingEmailInfo.getCount() + 1);
		emailRepository.save(existingEmailInfo);

		return new UpdateEmailResponse();
	}

	public DeleteEmailInfoResponse delete(String email) {
		EmailInfo existingEmailInfo = emailRepository.findByEmail(email);

		if (existingEmailInfo == null) {
			return new DeleteEmailInfoResponse(EmailServiceMessages.EMAIL_NOT_EXISTS, HttpStatus.BAD_REQUEST);
		}

		emailRepository.delete(existingEmailInfo);

		return new DeleteEmailInfoResponse();
	}

	@Scheduled(fixedDelayString = "${email-batch-period}")
	protected void scheduleFixedDelayTask() {

		logger.trace("scheduleFixedDelayTask: {}", emailCountMap);

		emailCountMapLock.lock();

		if (emailCountMap.isEmpty()) {
			emailCountMapLock.unlock();
			return;
		}

		List<EmailInfo> existingEmailInfos = emailRepository.findByEmailIn(emailCountMap.keySet());

		List<EmailInfo> cachedEmailInfos = createEmailInfosFromCache();

		List<EmailInfo> matchingEmailInfos = new ArrayList<>();
		List<EmailInfo> newEmailInfos = new ArrayList<>();

		fillMatchingAndNewEmailInfos(existingEmailInfos, cachedEmailInfos, matchingEmailInfos, newEmailInfos);

		emailRepository.createBatch(newEmailInfos);
		emailRepository.updateBatch(matchingEmailInfos);
		emailCountMap.clear();
		emailCountMapLock.unlock();

	}

	private Optional<String> validateEmailBatchRequest(EmailBatchRequest request) {
		List<String> emails = request.getEmails();
		List<String> resources = request.getResources();

		if (CollectionUtils.isEmpty(emails) && CollectionUtils.isEmpty(resources)) {
			return Optional.of(EmailServiceMessages.EMAILS_OR_RESOURCES_NOT_NULL);
		}

		return Optional.empty();
	}

	private void addExternalResourceEmailsToCache(EmailBatchDto emailBatch) {

		logger.trace("Inside addExternalResourceEmailsToCache: {}", emailBatch);

		if (CollectionUtils.isEmpty(emailBatch.getResources())) {
			return;
		}

		List<String> resourceUrls = emailBatch.getResources().stream()
				.filter(resource -> !StringUtils.isEmpty(resource)).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(resourceUrls)) {
			return;
		}

		AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();

		ConcurrentLinkedQueue<EmailBatchDto> responseEmails = new ConcurrentLinkedQueue<>();

		logger.trace("Before calling external service: {}", resourceUrls);

		for (String resourceUrl : resourceUrls) {
			ExternalEmailResponseBuilder responseBuilder = new ExternalEmailResponseBuilder(
					externalEmailServiceDelegate, resourceUrl, responseEmails);
			asyncTaskRunner.addTask(responseBuilder);
		}

		try {
			asyncTaskRunner.run();

			logger.trace("After calling external service: {}", responseEmails);

			addEmailsToCache(fillResponseEmailsFromResponseBatches(responseEmails));
			responseEmails.forEach(this::addExternalResourceEmailsToCache);
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Exeption caught while adding external emails to cache: {}", e);
			e.printStackTrace();
		}
	}

	private void addEmailsToCache(List<String> emails) {

		if (CollectionUtils.isEmpty(emails)) {
			return;
		}

		emails.removeIf(EmailValidator.instance()::isInvalid);
		Map<String, Long> localEmailCountMap = emails.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		emailCountMapLock.lock();
		localEmailCountMap.forEach((email, count) -> {
			Long previousCount = emailCountMap.get(email);

			if (previousCount == null) {
				emailCountMap.put(email, count);
			} else {
				emailCountMap.put(email, count + previousCount);
			}
		});
		emailCountMapLock.unlock();
	}

	private void fillMatchingAndNewEmailInfos(List<EmailInfo> existingEmailInfos, List<EmailInfo> cachedEmailInfos,
			List<EmailInfo> matchingEmailInfos, List<EmailInfo> newEmailInfos) {
		for (EmailInfo cachedEmailInfo : cachedEmailInfos) {

			Optional<EmailInfo> matchedEmailInfoOptional = existingEmailInfos.stream().filter(cachedEmailInfo::equals)
					.findFirst();

			if (matchedEmailInfoOptional.isPresent()) {
				EmailInfo matchedEmailInfo = matchedEmailInfoOptional.get();
				matchedEmailInfo.setCount(matchedEmailInfo.getCount() + cachedEmailInfo.getCount());
				matchingEmailInfos.add(matchedEmailInfoOptional.get());
			} else {
				newEmailInfos.add(cachedEmailInfo);
			}

		}
	}

	private List<EmailInfo> createEmailInfosFromCache() {
		return emailCountMap.entrySet().stream().map(entry -> new EmailInfo(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	private List<String> fillResponseEmailsFromResponseBatches(ConcurrentLinkedQueue<EmailBatchDto> responseEmails) {
		List<String> emails = new ArrayList<>();
		List<List<String>> emailListOfList = responseEmails.stream()
				.filter(emailBatch -> emailBatch.getEmails() != null).map(emailBatch -> emailBatch.getEmails())
				.collect(Collectors.toList());

		emailListOfList.forEach(emails::addAll);
		return emails;
	}
}
