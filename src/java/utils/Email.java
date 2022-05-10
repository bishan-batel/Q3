package utils;

import java.util.regex.Pattern;

public final class Email {
	/**
	 * https://www.baeldung.com/java-email-validation-regex
	 * baeldung goat
	 */
	private static final Pattern EMAIL_PATTERN =
					Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

	public static boolean isValid(String email) {
		return EMAIL_PATTERN.matcher(email).matches();
	}
}
