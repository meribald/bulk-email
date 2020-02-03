package com.frontech.bulkemail.service.delegate.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dataset")
public class EmailBatchDto {

	private List<String> emails;
	private List<String> resources;

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
