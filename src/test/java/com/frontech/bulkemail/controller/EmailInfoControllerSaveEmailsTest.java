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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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

	@SpyBean
	private ExternalEmailServiceDelegate externalEmailServiceDelegate;

	protected static final MediaType CONTENT_TYPE = new MediaType(MediaType.APPLICATION_XML.getType(),
			MediaType.APPLICATION_XML.getSubtype(), Charset.forName("utf8"));

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
				.andExpect(jsonPath("result", is("done")));

		Thread.sleep(8000);

		EmailInfo emailInfo1 = new EmailInfo("deneme1@comeon.com", 1L);
		EmailInfo emailInfo2 = new EmailInfo("deneme2@cherry.se", 1L);
		EmailInfo emailInfo3 = new EmailInfo("deneme3@cherry.se", 1L);
		List<EmailInfo> cachedEmailInfos = Arrays.asList(emailInfo1, emailInfo2, emailInfo3);

		Mockito.verify(emailInfoRepository, times(1)).createBatch(argThat(new UndorderedListMatcher(cachedEmailInfos)));

	}

	private String xml(EmailBatchRequest request) throws HttpMessageNotWritableException, IOException, JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(EmailBatchRequest.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		StringWriter sw = new StringWriter();
		jaxbMarshaller.marshal(request, sw);
		return sw.toString();
	}

}
