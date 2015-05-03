package net.chaosserver.facebook;

public class FacebookException extends Exception {

	public FacebookException(String message) {
		super(message);
	}

	public FacebookException(String message, Exception cause) {
		super(message, cause);
	}

}
