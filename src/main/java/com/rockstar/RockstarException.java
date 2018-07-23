package com.rockstar;

public class RockstarException extends RuntimeException {
	private static final long serialVersionUID = -7341141682624126228L;

	public RockstarException(String message)	{
		super(message);
	}
	
	public RockstarException(String message,Throwable cause)	{
		super(message,cause);
	}
}
