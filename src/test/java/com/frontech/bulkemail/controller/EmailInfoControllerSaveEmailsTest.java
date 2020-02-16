package com.frontech.bulkemail.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.frontech.bulkemail.dao.EmailInfoRepository;
import com.frontech.bulkemail.model.EmailInfo;
import com.frontech.bulkemail.request.EmailBatchRequest;
import com.frontech.bulkemail.service.EmailInfoService;
import com.frontech.bulkemail.service.delegate.ExternalEmailServiceDelegate;
import com.frontech.bulkemail.service.delegate.dto.EmailBatchDto;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:/test.properties")
public class EmailInfoControllerSaveEmailsTest {

	@Autowired
	private MockMvc mockMvc;

	@SpyBean
	private EmailInfoService emailInfoService;

	@MockBean
	private EmailInfoRepository emailInfoRepository;

	@MockBean
	private ExternalEmailServiceDelegate externalEmailServiceDelegate;

	@Test
	public void shouldSaveEmailsWithoutResources() throws HttpMessageNotWritableException, IOException, Exception {

		// Request

		EmailBatchRequest emailBatchRequest = new EmailBatchRequest();
		List<String> emails = Arrays.asList("deneme1@comeon.com", "deneme2@cherry.se", "deneme3@cherry.se");
		emailBatchRequest.setEmails(emails);

		// Setup

		Set<String> emailKeySet = new HashSet<>();
		emailKeySet.add("deneme1@comeon.com");
		emailKeySet.add("deneme2@cherry.se");
		emailKeySet.add("deneme3@cherry.se");

		when(emailInfoRepository.findByEmailIn(emailKeySet)).thenReturn(new ArrayList<>());

		// Execute and verify

		mockMvc.perform(
				post("/email-info/batch-create").contentType(MediaType.APPLICATION_XML).content(xml(emailBatchRequest)))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("result", is("done"))).andExpect(jsonPath("errorResponse", IsNull.nullValue()));

		Thread.sleep(8000);

		EmailInfo emailInfo1 = new EmailInfo("deneme1@comeon.com", 1L);
		EmailInfo emailInfo2 = new EmailInfo("deneme2@cherry.se", 1L);
		EmailInfo emailInfo3 = new EmailInfo("deneme3@cherry.se", 1L);
		List<EmailInfo> cachedEmailInfos = Arrays.asList(emailInfo1, emailInfo2, emailInfo3);

		Mockito.verify(emailInfoRepository, times(1)).createBatch(argThat(new UndorderedListMatcher(cachedEmailInfos)));
	}

	@Test
	public void shouldSaveEmailsWithResources() throws HttpMessageNotWritableException, IOException, Exception {

		// Request

		EmailBatchRequest emailBatchRequest = new EmailBatchRequest();
		List<String> emails = Arrays.asList("deneme1@comeon.com", "deneme2@cherry.se", "deneme3@cherry.se");
		emailBatchRequest.setEmails(emails);

		List<String> urls = Arrays.asList("http://localhost:8201/emailresource/without-resource");
		emailBatchRequest.setResources(urls);

		// Setup

		EmailBatchDto emailBatchFromExternalResource = new EmailBatchDto();
		List<String> emailsFromExternalResource = Arrays.asList("deneme2@cherry.se", "deneme2@cherry.se",
				"deneme4@cherry.se");
		emailBatchFromExternalResource.setEmails(emailsFromExternalResource);

		when(externalEmailServiceDelegate.getEmailBatch("http://localhost:8201/emailresource/without-resource"))
				.thenReturn(Optional.of(emailBatchFromExternalResource));

		Set<String> emailKeySet = new HashSet<>();
		emailKeySet.add("deneme1@comeon.com");
		emailKeySet.add("deneme2@cherry.se");
		emailKeySet.add("deneme3@cherry.se");
		emailKeySet.add("deneme4@cherry.se");

		when(emailInfoRepository.findByEmailIn(emailKeySet)).thenReturn(new ArrayList<>());

		// Execute and verify

		mockMvc.perform(
				post("/email-info/batch-create").contentType(MediaType.APPLICATION_XML).content(xml(emailBatchRequest)))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("result", is("done"))).andExpect(jsonPath("errorResponse", IsNull.nullValue()));

		Thread.sleep(8000);

		EmailInfo emailInfo1 = new EmailInfo("deneme1@comeon.com", 1L);
		EmailInfo emailInfo2 = new EmailInfo("deneme2@cherry.se", 3L);
		EmailInfo emailInfo3 = new EmailInfo("deneme3@cherry.se", 1L);
		EmailInfo emailInfo4 = new EmailInfo("deneme4@cherry.se", 1L);
		List<EmailInfo> cachedEmailInfos = Arrays.asList(emailInfo1, emailInfo2, emailInfo3, emailInfo4);

		Mockito.verify(emailInfoRepository, times(1)).createBatch(argThat(new UndorderedListMatcher(cachedEmailInfos)));

	}

	@Test
	public void shouldSaveEmailsWithResourcesInResources()
			throws HttpMessageNotWritableException, IOException, Exception {

		// Request

		EmailBatchRequest emailBatchRequest = new EmailBatchRequest();
		List<String> emails = Arrays.asList("deneme1@comeon.com", "deneme2@cherry.se", "deneme3@cherry.se");
		emailBatchRequest.setEmails(emails);

		String firstResourceUrl = "http://localhost:8201/emailresource/without-resource";
		List<String> urls = Arrays.asList(firstResourceUrl);
		emailBatchRequest.setResources(urls);

		// Setup

		EmailBatchDto emailBatchFromExternalResource = new EmailBatchDto();
		List<String> emailsFromExternalResource = Arrays.asList("deneme2@cherry.se", "deneme2@cherry.se",
				"deneme4@cherry.se");
		emailBatchFromExternalResource.setEmails(emailsFromExternalResource);
		String secondResourceUrl = "http://localhost:8202/emailresource/without-resource";
		List<String> urlsFromExternalResource = Arrays.asList(secondResourceUrl);
		emailBatchFromExternalResource.setResources(urlsFromExternalResource);

		when(externalEmailServiceDelegate.getEmailBatch(firstResourceUrl))
				.thenReturn(Optional.of(emailBatchFromExternalResource));

		EmailBatchDto emailBatchFromExternalResource2 = new EmailBatchDto();
		List<String> emailsFromExternalResource2 = Arrays.asList("deneme5@cherry.se", "deneme4@cherry.se");
		emailBatchFromExternalResource2.setEmails(emailsFromExternalResource2);

		when(externalEmailServiceDelegate.getEmailBatch(secondResourceUrl))
				.thenReturn(Optional.of(emailBatchFromExternalResource2));

		Set<String> emailKeySet = new HashSet<>();
		emailKeySet.add("deneme1@comeon.com");
		emailKeySet.add("deneme2@cherry.se");
		emailKeySet.add("deneme3@cherry.se");
		emailKeySet.add("deneme4@cherry.se");
		emailKeySet.add("deneme5@cherry.se");

		when(emailInfoRepository.findByEmailIn(emailKeySet)).thenReturn(new ArrayList<>());

		// Execute and verify

		mockMvc.perform(
				post("/email-info/batch-create").contentType(MediaType.APPLICATION_XML).content(xml(emailBatchRequest)))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("result", is("done"))).andExpect(jsonPath("errorResponse", IsNull.nullValue()));

		Thread.sleep(8000);

		EmailInfo emailInfo1 = new EmailInfo("deneme1@comeon.com", 1L);
		EmailInfo emailInfo2 = new EmailInfo("deneme2@cherry.se", 3L);
		EmailInfo emailInfo3 = new EmailInfo("deneme3@cherry.se", 1L);
		EmailInfo emailInfo4 = new EmailInfo("deneme4@cherry.se", 2L);
		EmailInfo emailInfo5 = new EmailInfo("deneme5@cherry.se", 1L);
		List<EmailInfo> cachedEmailInfos = Arrays.asList(emailInfo1, emailInfo2, emailInfo3, emailInfo4, emailInfo5);

		Mockito.verify(emailInfoRepository, times(1)).createBatch(argThat(new UndorderedListMatcher(cachedEmailInfos)));
	}

	@Test
	public void shouldSaveEmailsWithoutResourcesRemoveEmailsFromUnwantedDomains()
			throws HttpMessageNotWritableException, IOException, Exception {

		// Request

		EmailBatchRequest emailBatchRequest = new EmailBatchRequest();
		List<String> emails = Arrays.asList("deneme1@comeon.com", "deneme2@cherry2.se", "deneme3@comeon3.se");
		emailBatchRequest.setEmails(emails);

		// Setup

		Set<String> emailKeySet = new HashSet<>();
		emailKeySet.add("deneme1@comeon.com");

		when(emailInfoRepository.findByEmailIn(emailKeySet)).thenReturn(new ArrayList<>());

		// Execute and verify

		mockMvc.perform(
				post("/email-info/batch-create").contentType(MediaType.APPLICATION_XML).content(xml(emailBatchRequest)))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("result", is("done"))).andExpect(jsonPath("errorResponse", IsNull.nullValue()));

		Thread.sleep(8000);

		EmailInfo emailInfo1 = new EmailInfo("deneme1@comeon.com", 1L);
		List<EmailInfo> cachedEmailInfos = Arrays.asList(emailInfo1);

		Mockito.verify(emailInfoRepository, times(1)).createBatch(argThat(new UndorderedListMatcher(cachedEmailInfos)));

	}

	@Test
	public void shouldSaveEmailsWithoutResourcesRemoveInvalidEmails()
			throws HttpMessageNotWritableException, IOException, Exception {

		// Request

		EmailBatchRequest emailBatchRequest = new EmailBatchRequest();
		List<String> emails = Arrays.asList("deneme1@comeon.com", "deneme2.cherry.se", "deneme3-comeon.com");
		emailBatchRequest.setEmails(emails);

		// Setup

		Set<String> emailKeySet = new HashSet<>();
		emailKeySet.add("deneme1@comeon.com");

		when(emailInfoRepository.findByEmailIn(emailKeySet)).thenReturn(new ArrayList<>());

		// Execute and verify

		mockMvc.perform(
				post("/email-info/batch-create").contentType(MediaType.APPLICATION_XML).content(xml(emailBatchRequest)))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("result", is("done"))).andExpect(jsonPath("errorResponse", IsNull.nullValue()));

		Thread.sleep(8000);

		EmailInfo emailInfo1 = new EmailInfo("deneme1@comeon.com", 1L);
		List<EmailInfo> cachedEmailInfos = Arrays.asList(emailInfo1);

		Mockito.verify(emailInfoRepository, times(1)).createBatch(argThat(new UndorderedListMatcher(cachedEmailInfos)));
	}

	@Test
	public void shouldSaveEmailsWithoutResourcesCallServiceTwice()
			throws HttpMessageNotWritableException, IOException, Exception {

		// Request

		EmailBatchRequest emailBatchRequest1 = new EmailBatchRequest();
		List<String> emails1 = Arrays.asList("deneme1@comeon.com", "deneme2@cherry.se", "deneme3@cherry.se");
		emailBatchRequest1.setEmails(emails1);

		EmailBatchRequest emailBatchRequest2 = new EmailBatchRequest();
		List<String> emails2 = Arrays.asList("deneme4@comeon.com", "deneme5@cherry.se", "deneme6@cherry.se");
		emailBatchRequest2.setEmails(emails2);

		// Setup

		Set<String> emailKeySet = new HashSet<>();
		emailKeySet.add("deneme1@comeon.com");
		emailKeySet.add("deneme2@cherry.se");
		emailKeySet.add("deneme3@cherry.se");
		emailKeySet.add("deneme4@comeon.com");
		emailKeySet.add("deneme5@cherry.se");
		emailKeySet.add("deneme6@cherry.se");

		when(emailInfoRepository.findByEmailIn(emailKeySet)).thenReturn(new ArrayList<>());

		// Execute and verify

		mockMvc.perform(post("/email-info/batch-create").contentType(MediaType.APPLICATION_XML)
				.content(xml(emailBatchRequest1))).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("result", is("done")))
				.andExpect(jsonPath("errorResponse", IsNull.nullValue()));

		mockMvc.perform(post("/email-info/batch-create").contentType(MediaType.APPLICATION_XML)
				.content(xml(emailBatchRequest2))).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("result", is("done")))
				.andExpect(jsonPath("errorResponse", IsNull.nullValue()));

		Thread.sleep(8000);

		EmailInfo emailInfo1 = new EmailInfo("deneme1@comeon.com", 1L);
		EmailInfo emailInfo2 = new EmailInfo("deneme2@cherry.se", 1L);
		EmailInfo emailInfo3 = new EmailInfo("deneme3@cherry.se", 1L);
		EmailInfo emailInfo4 = new EmailInfo("deneme4@comeon.com", 1L);
		EmailInfo emailInfo5 = new EmailInfo("deneme5@cherry.se", 1L);
		EmailInfo emailInfo6 = new EmailInfo("deneme6@cherry.se", 1L);
		List<EmailInfo> cachedEmailInfos = Arrays.asList(emailInfo1, emailInfo2, emailInfo3, emailInfo4, emailInfo5,
				emailInfo6);

		Mockito.verify(emailInfoRepository, times(1)).createBatch(argThat(new UndorderedListMatcher(cachedEmailInfos)));
	}

	@Test
	public void shouldSaveEmailsWithoutResourcesCallServiceTwiceWithTimeInBetween()
			throws HttpMessageNotWritableException, IOException, Exception {

		// Request

		EmailBatchRequest emailBatchRequest1 = new EmailBatchRequest();
		List<String> emails1 = Arrays.asList("deneme1@comeon.com", "deneme2@cherry.se", "deneme3@cherry.se");
		emailBatchRequest1.setEmails(emails1);

		EmailBatchRequest emailBatchRequest2 = new EmailBatchRequest();
		List<String> emails2 = Arrays.asList("deneme4@comeon.com", "deneme5@cherry.se", "deneme6@cherry.se");
		emailBatchRequest2.setEmails(emails2);

		// Setup

		Set<String> emailKeySet = new HashSet<>();
		emailKeySet.add("deneme1@comeon.com");
		emailKeySet.add("deneme2@cherry.se");
		emailKeySet.add("deneme3@cherry.se");
		emailKeySet.add("deneme4@comeon.com");
		emailKeySet.add("deneme5@cherry.se");
		emailKeySet.add("deneme6@cherry.se");

		when(emailInfoRepository.findByEmailIn(emailKeySet)).thenReturn(new ArrayList<>());

		// Execute and verify

		mockMvc.perform(post("/email-info/batch-create").contentType(MediaType.APPLICATION_XML)
				.content(xml(emailBatchRequest1))).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("result", is("done")))
				.andExpect(jsonPath("errorResponse", IsNull.nullValue()));

		Thread.sleep(8000);

		mockMvc.perform(post("/email-info/batch-create").contentType(MediaType.APPLICATION_XML)
				.content(xml(emailBatchRequest2))).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("result", is("done")))
				.andExpect(jsonPath("errorResponse", IsNull.nullValue()));

		Thread.sleep(8000);

		EmailInfo emailInfo1 = new EmailInfo("deneme1@comeon.com", 1L);
		EmailInfo emailInfo2 = new EmailInfo("deneme2@cherry.se", 1L);
		EmailInfo emailInfo3 = new EmailInfo("deneme3@cherry.se", 1L);
		List<EmailInfo> cachedEmailInfos1 = Arrays.asList(emailInfo1, emailInfo2, emailInfo3);

		Mockito.verify(emailInfoRepository, times(1))
				.createBatch(argThat(new UndorderedListMatcher(cachedEmailInfos1)));

		EmailInfo emailInfo4 = new EmailInfo("deneme4@comeon.com", 1L);
		EmailInfo emailInfo5 = new EmailInfo("deneme5@cherry.se", 1L);
		EmailInfo emailInfo6 = new EmailInfo("deneme6@cherry.se", 1L);
		List<EmailInfo> cachedEmailInfos2 = Arrays.asList(emailInfo4, emailInfo5, emailInfo6);

		Mockito.verify(emailInfoRepository, times(1))
				.createBatch(argThat(new UndorderedListMatcher(cachedEmailInfos2)));
	}

	@Test
	public void shouldUpdateEmailsWithoutResources() throws HttpMessageNotWritableException, IOException, Exception {

		// Request

		EmailBatchRequest emailBatchRequest = new EmailBatchRequest();
		List<String> emails = Arrays.asList("deneme1@comeon.com", "deneme2@cherry.se", "deneme3@cherry.se");
		emailBatchRequest.setEmails(emails);

		// Setup

		Set<String> emailKeySet = new HashSet<>();
		emailKeySet.add("deneme1@comeon.com");
		emailKeySet.add("deneme2@cherry.se");
		emailKeySet.add("deneme3@cherry.se");

		EmailInfo existingEmailInfo1 = new EmailInfo("deneme1@comeon.com", 10L);
		EmailInfo existingEmailInfo2 = new EmailInfo("deneme2@cherry.se", 50L);
		EmailInfo existingEmailInfo3 = new EmailInfo("deneme3@cherry.se", 1L);
		List<EmailInfo> existingEmailInfos = Arrays.asList(existingEmailInfo1, existingEmailInfo2, existingEmailInfo3);

		when(emailInfoRepository.findByEmailIn(emailKeySet)).thenReturn(existingEmailInfos);

		// Execute and verify

		mockMvc.perform(
				post("/email-info/batch-create").contentType(MediaType.APPLICATION_XML).content(xml(emailBatchRequest)))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("result", is("done"))).andExpect(jsonPath("errorResponse", IsNull.nullValue()));

		Thread.sleep(8000);

		EmailInfo emailInfo1 = new EmailInfo("deneme1@comeon.com", 11L);
		EmailInfo emailInfo2 = new EmailInfo("deneme2@cherry.se", 51L);
		EmailInfo emailInfo3 = new EmailInfo("deneme3@cherry.se", 2L);
		List<EmailInfo> cachedEmailInfos = Arrays.asList(emailInfo1, emailInfo2, emailInfo3);

		Mockito.verify(emailInfoRepository, times(1)).updateBatch(argThat(new UndorderedListMatcher(cachedEmailInfos)));
	}

	private String xml(EmailBatchRequest request) throws HttpMessageNotWritableException, IOException, JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(EmailBatchRequest.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		StringWriter sw = new StringWriter();
		jaxbMarshaller.marshal(request, sw);
		return sw.toString();
	}

}
