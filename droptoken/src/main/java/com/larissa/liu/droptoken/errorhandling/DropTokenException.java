package com.larissa.liu.droptoken.errorhandling;

public class DropTokenException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	Integer status;
	
	/** application specific error code */
	int code; 
		
	/** detailed error description for developers*/
	String developerMessage;	
	
	public DropTokenException(int status, int code, String message,
			String developerMessage) {
		super(message);
		this.status = status;
		this.code = code;
		this.developerMessage = developerMessage;
	}

	public DropTokenException() { }

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDeveloperMessage() {
		return developerMessage;
	}

	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}
}
