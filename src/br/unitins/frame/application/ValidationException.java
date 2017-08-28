package br.unitins.frame.application;

import java.util.ArrayList;
import java.util.List;


public class ValidationException extends Exception {

	private static final long serialVersionUID = -3200033271654036323L;
	
	private List<String> listMessages;
	
	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(List<String> listMessages) {
		getListMessages().addAll(listMessages);
	}
	
	public List<String> getListMessages() {
		if(listMessages == null)
			listMessages = new ArrayList<String>();
		return listMessages;
	}

	public void setListMessages(List<String> listMessages) {
		this.listMessages = listMessages;
	}
	
	public void addMessage(String message){
		getListMessages().add(message);
	}
}
