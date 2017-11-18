package br.unitins.frame.application;

import java.io.IOException;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class Util {

	public static void message(Severity severity, String msg) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, msg, null));
	}

	public static void infoMessage(String msg) {
		message(FacesMessage.SEVERITY_INFO, msg);
	}

	public static void warningMessage(String msg) {
		message(FacesMessage.SEVERITY_WARN, msg);
	}

	public static void errorMessage(String msg) {
		message(FacesMessage.SEVERITY_ERROR, msg);
	}

	public static void showMessagesInfo(List<String> listMessages) {
		for (String message : listMessages) {
			infoMessage(message);
		}
	}

	public static void showMessagesWarning(List<String> listMessages) {
		for (String message : listMessages) {
			warningMessage(message);
		}
	}

	public static void showMessagesError(List<String> listMessages) {
		for (String message : listMessages) {
			errorMessage(message);
		}
	}
	
	public static boolean redirect(String page) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			System.out.println("\\n\\n"+ec.getRequestContextPath());
			ec.redirect(ec.getRequestContextPath() +"/faces/"+ page);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
