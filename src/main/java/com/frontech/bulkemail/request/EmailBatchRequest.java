package com.frontech.bulkemail.request;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dataset")
public class EmailBatchRequest extends BaseRequest {

	private List<String> emails;
	private List<String> resources;

	public EmailBatchRequest() {

	}

	@XmlElementWrapper
	@XmlElement(name = "email")
	public List<String> getEmails() {
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	@XmlElementWrapper
	@XmlElement(name = "url")
	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}
}
