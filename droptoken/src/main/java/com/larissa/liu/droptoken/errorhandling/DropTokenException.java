package com.larissa.liu.droptoken.errorhandling;

public class DropTokenException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	Integer status;
		
	/** detailed error description for developers*/
	String developerMessage;	
	
	public DropTokenException(int status, String message,
			String developerMessage) {
		super(message);
		this.status = status;
		this.developerMessage = developerMessage;
	}

	public DropTokenException() { }

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}
}
