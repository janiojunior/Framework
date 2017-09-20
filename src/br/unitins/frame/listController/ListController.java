package br.unitins.frame.listController;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import br.unitins.frame.application.ApplicationException;
import br.unitins.frame.application.SelectionListListener;
import br.unitins.frame.application.SelectionListener;
import br.unitins.frame.application.Session;
import br.unitins.frame.model.Model;
import br.unitins.frame.repository.Repository;


public abstract class ListController<T extends Model<? super T>> implements Serializable {

	private static final long serialVersionUID = -1361765128106196122L;
	
	protected EntityManager em;	
	protected T entity;
	protected List<T> listEntity;
		
	private Boolean modal;
	private Boolean draggable;
	private Boolean resizable;
	private Integer contentHeight;
	private Integer contentWidth;
	private String listName;
	private SelectionListener<T> listener;
	private SelectionListListener<T> listListener;
	private Map<String, Object> sessionScope;
	private Repository<T> repository;
	private String chave = "";

	public ListController(Repository<T> repository, Boolean modal, Boolean draggable, Boolean resizable, Integer contentHeight, Integer contentWidth, String xhtmlPageName) {
		this.modal = modal;
		this.draggable = draggable;
		this.resizable = resizable;
		this.contentHeight = contentHeight;
		this.contentWidth = contentWidth;
		this.listName = xhtmlPageName;
		this.repository = repository;
//		String beanName = this.getClass().getSimpleName();
//		getSessionScope().put(beanName.substring(0, 1).toLowerCase() + beanName.substring(1), this);
	}

	public Map<String, Object> getSessionScope() {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		sessionScope = externalContext.getSessionMap();
		return sessionScope;
	}

	public void onLoad(){
		// Obtendo a listagem da sessao
		Object obj =  Session.getSessionScope().get(getChave());

		if(obj == null)
			return;

		// Obtendo todos os atributos (inclusive herança) da listagem (this)
		List<Field> fields = new ArrayList<>();
		fields = getAllFields(fields, this.getClass());

		// Setando os dados da listagem da sessão na listagem this
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				//Ignora atributos com o modificador FINAL
				if(!Modifier.isFinal(field.getModifiers()))
					field.set(this, field.get(obj));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				System.out.println(e.toString());
			}
		}

		// Removendo a listagem da sessao
		Session.getSessionScope().remove(getChave());
	}

	public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
		fields.addAll(Arrays.asList(type.getDeclaredFields()));
		if (type.getSuperclass() != null)
			fields = getAllFields(fields, type.getSuperclass());
		return fields;
	}

	public void openList(SelectionListener<T> listener){
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", modal);
		options.put("draggable", draggable);
		options.put("resizable", resizable);
		options.put("contentHeight", contentHeight);
		options.put("contentWidth", "100%");
		options.put("closable", true);
		options.put("closeOnEscape", true);
		options.put("sensível", true);
		options.put("width", "95%");
		options.put("height", contentHeight);

		Map<String, List<String>> params = new HashMap<String, List<String>>();
		List<String> values = new ArrayList<String>();
		String hashCode = String.valueOf(this.hashCode());
		values.add(hashCode);
		params.put("chave", values);

		RequestContext.getCurrentInstance().openDialog(listName, options, params);
		this.listener = listener;
		Session.getSessionScope().put(hashCode, this);
	}

	public void openList(SelectionListListener<T> listListener){
		Map<String, Object> options = new HashMap<String, Object>();
		options.put("modal", modal);
		options.put("draggable", draggable);
		options.put("resizable", resizable);
		options.put("contentHeight", contentHeight);
		options.put("contentWidth", contentWidth);

		Map<String, List<String>> params = new HashMap<String, List<String>>();
		List<String> values = new ArrayList<String>();
		String hashCode = String.valueOf(this.hashCode());
		values.add(hashCode);
		params.put("chave", values);

		RequestContext.getCurrentInstance().openDialog(listName, options, params);
		this.listListener = listListener;
		Session.getSessionScope().put(hashCode, this);

	}

	@SuppressWarnings("unchecked")
	public void onSelect(T entity) {
		RequestContext.getCurrentInstance().closeDialog(entity);
		if (this.listener == null)
			return;
		if (this.listener instanceof SelectionListener)
			((SelectionListener<T>)this.listener).select((T)entity);
		else
			((SelectionListListener<T>)this.listListener).select((List<T>)entity);
	}

	public void closeDialog(ActionEvent actionEvent) {
		RequestContext.getCurrentInstance().closeDialog(entity);
	}

	public void onSelectById(Integer id) {
		T entity = null;
		try {
			entity = repository.find(id);
		} catch (ApplicationException e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
		onSelect(entity);
	}

	public void search(ActionEvent actionEvent) {
		listEntity = null;
	}
	
	public abstract T getEntity();

	public void setEntity(T entity) {
		this.entity = entity;
	}

	public Repository<T> getRepository() {
		return repository;
	}

	public void setRepository(Repository<T> repository) {
		this.repository = repository;
	}

	public void onSelectList(List<T> entity) {
		RequestContext.getCurrentInstance().closeDialog(entity);
		((SelectionListListener<T>) this.listListener).select((List<T>) entity);
	}
	
	public abstract List<T> getListEntity();

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}
	
	public void onRowSelect(SelectEvent event) {
		onSelectById((Integer) event.getObject());
	}

}