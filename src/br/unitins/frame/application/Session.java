package br.unitins.frame.application;

import java.io.Serializable;
import java.util.Map;

import javax.faces.context.FacesContext;

public abstract class Session implements Serializable {

	private static final long serialVersionUID = -3383684974244392459L;
	
	private static Map<String, Object> sessionScope;
	
	public Session(){};

	public static Map<String, Object> getSessionScope() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null)
			return sessionScope;
		sessionScope = facesContext.getExternalContext().getSessionMap();
		return sessionScope;
	}
	
    public static void encerrarSessao(){   
    	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
//    	sessionScope = null;
   }

}
