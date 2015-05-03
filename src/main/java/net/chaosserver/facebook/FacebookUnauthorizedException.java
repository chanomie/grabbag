package net.chaosserver.facebook;

import com.google.appengine.api.datastore.EntityNotFoundException;

public class FacebookUnauthorizedException extends FacebookException {

	public FacebookUnauthorizedException(String message) {
		super(message);
	}

	public FacebookUnauthorizedException(String message,
			Exception cause) {
		super(message, cause);
	}

}
