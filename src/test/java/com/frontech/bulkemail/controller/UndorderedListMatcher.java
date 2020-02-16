package com.frontech.bulkemail.controller;

import static java.util.Comparator.comparing;

import java.util.Collections;
import java.util.List;

import org.mockito.ArgumentMatcher;
import org.springframework.util.CollectionUtils;

import com.frontech.bulkemail.model.EmailInfo;

public class UndorderedListMatcher implements ArgumentMatcher<List<EmailInfo>> {

	private List<EmailInfo> expectedEmailInfos;

	public UndorderedListMatcher(List<EmailInfo> expectedEmailInfos) {
		this.expectedEmailInfos = expectedEmailInfos;
	}

	@Override
	public boolean matches(List<EmailInfo> actualEmailInfos) {

		if ((CollectionUtils.isEmpty(actualEmailInfos) && !CollectionUtils.isEmpty(expectedEmailInfos))
				|| (!CollectionUtils.isEmpty(actualEmailInfos) && CollectionUtils.isEmpty(expectedEmailInfos))) {
			return false;
		}

		if (actualEmailInfos.size() != expectedEmailInfos.size()) {
			return false;
		}

		Collections.sort(actualEmailInfos, comparing(EmailInfo::getEmail));
		Collections.sort(expectedEmailInfos, comparing(EmailInfo::getEmail));

		for (int i = 0; i < actualEmailInfos.size(); ++i) {
			boolean emailEquals = expectedEmailInfos.get(i).getEmail().equals(actualEmailInfos.get(i).getEmail());
			boolean countEquals = expectedEmailInfos.get(i).getCount().equals(actualEmailInfos.get(i).getCount());

			if (!emailEquals || !countEquals) {
				return false;
			}
		}

		return true;
	}

}
