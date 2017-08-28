package br.unitins.frame.application;

public class ApplicationException extends Exception 
{


	private static final long serialVersionUID = 1287595138854546861L;

	public ApplicationException(Throwable cause) 
	{
		super(cause);
	}
	
	public ApplicationException(String message) 
	{
		super(message);
	}
	
	public ApplicationException(String message, Throwable cause)
	{
		super(message, cause);
	}	
}
