package de.lehrbaum.masterthesis.exceptions;

public abstract class UserReadableException extends Exception {
	public abstract String convertForUser();
}
