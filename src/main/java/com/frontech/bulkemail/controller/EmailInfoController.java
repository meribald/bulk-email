package com.frontech.bulkemail.controller;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.frontech.bulkemail.request.CreateEmailInfoRequest;
import com.frontech.bulkemail.request.EmailBatchRequest;
import com.frontech.bulkemail.request.GetEmailCountRequest;
import com.frontech.bulkemail.request.GetPaginatedEmailRequest;
import com.frontech.bulkemail.request.UpdateEmailInfoRequest;
import com.frontech.bulkemail.response.BaseResponse;
import com.frontech.bulkemail.response.CreateEmailResponse;
import com.frontech.bulkemail.response.DeleteEmailInfoResponse;
import com.frontech.bulkemail.response.EmailBatchResponse;
import com.frontech.bulkemail.response.EmailCountResponse;
import com.frontech.bulkemail.response.EmailInfoPagingResponse;
import com.frontech.bulkemail.response.GetEmailResponse;
import com.frontech.bulkemail.response.UpdateEmailResponse;
import com.frontech.bulkemail.service.EmailInfoService;

@RestController
@RequestMapping("/email-info")
public class EmailInfoController {

	private EmailInfoService service;

	public EmailInfoController(EmailInfoService service) {
		this.service = service;
	}

	@PostMapping(value = "/batch-create", consumes = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<EmailBatchResponse> saveEmails(@Valid @RequestBody EmailBatchRequest request) {
		EmailBatchResponse serviceResponse = service.saveEmails(request);
		return createResponseBody(serviceResponse).body(serviceResponse);
	}

	@GetMapping("/page")
	public ResponseEntity<EmailInfoPagingResponse> getPaginated(@Valid GetPaginatedEmailRequest request) {
		EmailInfoPagingResponse serviceResponse = service.getPaginated(request);
		return createResponseBody(serviceResponse).body(serviceResponse);
	}

	@GetMapping("/count")
	public ResponseEntity<EmailCountResponse> getCount(@Valid GetEmailCountRequest request) {
		EmailCountResponse serviceResponse = service.getCount(request);
		return createResponseBody(serviceResponse).body(serviceResponse);
	}

	@PostMapping
	public ResponseEntity<CreateEmailResponse> create(@Valid @RequestBody CreateEmailInfoRequest request) {
		CreateEmailResponse serviceResponse = service.create(request);
		return createResponseBody(serviceResponse).body(serviceResponse);
	}

	@GetMapping("/{email}")
	public ResponseEntity<GetEmailResponse> get(@PathVariable String email) {
		GetEmailResponse serviceResponse = service.get(email);
		return createResponseBody(serviceResponse).body(serviceResponse);
	}

	@PutMapping
	public ResponseEntity<UpdateEmailResponse> update(@Valid @RequestBody UpdateEmailInfoRequest request) {
		UpdateEmailResponse serviceResponse = service.update(request);
		return createResponseBody(serviceResponse).body(serviceResponse);
	}

	@DeleteMapping("/{email}")
	public ResponseEntity<DeleteEmailInfoResponse> delete(@PathVariable String email) {
		DeleteEmailInfoResponse serviceResponse = service.delete(email);
		return createResponseBody(serviceResponse).body(serviceResponse);
	}

	private BodyBuilder createResponseBody(BaseResponse serviceResponse) {
		if (serviceResponse.getErrorResponse() == null) {
			return ResponseEntity.ok();
		}

		return ResponseEntity.status(serviceResponse.getErrorResponse().getHttpsStatus());
	}

}
