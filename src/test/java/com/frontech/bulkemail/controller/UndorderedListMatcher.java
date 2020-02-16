package com.frontech.bulkemail.controller;

import static java.util.Comparator.comparing;

import java.util.Collections;
import java.util.List;

import org.mockito.ArgumentMatcher;

import com.frontech.bulkemail.model.EmailInfo;

public class UndorderedListMatcher implements ArgumentMatcher<List<EmailInfo>> {

	private List<EmailInfo> expectedEmailInfos;

	public UndorderedListMatcher(List<EmailInfo> expectedEmailInfos) {
		this.expectedEmailInfos = expectedEmailInfos;
	}

	@Override
	public boolean matches(List<EmailInfo> actualEmailInfos) {

		Collections.sort(actualEmailInfos, comparing(EmailInfo::getEmail));
		Collections.sort(expectedEmailInfos, comparing(EmailInfo::getEmail));

		return expectedEmailInfos.equals(actualEmailInfos);
	}

}
