package br.com.auster.billcheckout.caches;

public class DataNotFoundException extends RuntimeException {

	public DataNotFoundException() {
		// empty constructor
	}

	public DataNotFoundException(String message) {
		super(message);
	}

	public DataNotFoundException(Throwable cause) {
		super(cause);
	}

	public DataNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
