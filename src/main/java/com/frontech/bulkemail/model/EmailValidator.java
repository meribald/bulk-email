package com.frontech.bulkemail.model;

import java.util.regex.Pattern;

public class EmailValidator {

	private static final String REGEX = "^(.+)@(comeon.com|cherry.se)$";
	private static final Pattern PATTERN = Pattern.compile(REGEX);

	private static final EmailValidator INSTANCE = new EmailValidator();

	private EmailValidator() {
	}

	public static EmailValidator instance() {
		return INSTANCE;
	}

	public boolean isInvalid(String email) {
		return !PATTERN.matcher(email).matches();
	}
}
