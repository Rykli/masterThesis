package de.lehrbaum.masterthesis.exceptions;

/**
 * This class describes an exception that can be fixed by the user. For this purpose it
 * contains a method that will convert it to user readable text.
 */
public abstract class UserReadableException extends Exception {
	public abstract String convertForUser();
}
