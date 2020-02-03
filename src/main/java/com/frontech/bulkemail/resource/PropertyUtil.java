package com.frontech.bulkemail.resource;

import java.util.ResourceBundle;

public class PropertyUtil {

	private PropertyUtil() {
	}

	public static String getProperty(String property) {
		ResourceBundle messages = ResourceBundle.getBundle("validation/messages");
		return messages.getString(property);
	}
}
