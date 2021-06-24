package com.shopme.admin.user;

public class UserNotFoundException extends Exception {

	private static final long serialVersionUID = 1436920884636886633L;

	public UserNotFoundException(String message) {
		super(message);
	}

}
