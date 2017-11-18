package br.unitins.frame.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;

import br.unitins.frame.application.ApplicationException;
import br.unitins.frame.application.Config;
import br.unitins.frame.application.Util;
import br.unitins.frame.application.ValidationException;
import br.unitins.frame.model.Model;
import br.unitins.frame.repository.Repository;
import br.unitins.frame.validation.Validation;

public abstract class Controller<T extends Model<? super T>> {
	
	protected EntityManager em;
	protected T entity;
	
	protected abstract EntityManager getEntityManager();
	
	public void insert(ActionEvent actionEvent) {
		try {
			em = getEntityManager();
			Repository<T> repository = getRepository(em);
			em.getTransaction().begin();
			validarEntidade();
			setEntity(repository.save(getEntity()));
			em.getTransaction().commit();
			clean(actionEvent);
			Util.infoMessage(Config.INSERT_SUCCESS_MSG);
		} catch (ValidationException e) {
			em.getTransaction().rollback();
			Util.showMessagesWarning(e.getListMessages());
		} catch (ApplicationException e) {
			em.getTransaction().rollback();
			Util.errorMessage(e.getMessage());
		}
	}
	
	public void update(ActionEvent actionEvent) {
		try {
			em = getEntityManager();
			Repository<T> repository = getRepository(em);
			em.getTransaction().begin();
			validarEntidade();
			setEntity(repository.save(getEntity()));
			em.getTransaction().commit();
			clean(actionEvent);
			Util.infoMessage(Config.UPDATE_SUCCESS_MSG);
		} catch (ValidationException e) {
			em.getTransaction().rollback();
			Util.showMessagesWarning(e.getListMessages());
		} catch (ApplicationException e) {
			em.getTransaction().rollback();
			Util.errorMessage(e.getMessage());
		} catch (OptimisticLockException e) {
			em.getTransaction().rollback();
			Util.warningMessage(Config.VERSION_CHANGE_MSG);
		}
	}

	public void delete(ActionEvent actionEvent) {
		try {
			em = getEntityManager();
			Repository<T> repository = getRepository(em);
			T t = repository.find(getEntity().getId());
			em.getTransaction().begin();
			repository.remove(t);
			em.getTransaction().commit();
			clean(actionEvent);
			Util.infoMessage(Config.DELETE_SUCCESS_MSG);
		} catch (ApplicationException e) {
			em.getTransaction().rollback();
			Util.errorMessage(e.getMessage());
		} catch (OptimisticLockException e) {
			em.getTransaction().rollback();
			System.out.println(e.toString());
			Util.warningMessage(Config.VERSION_CHANGE_MSG);
		} catch (Exception e) {
			em.getTransaction().rollback();
			Util.errorMessage(e.getMessage());
		}
		
	}

	
	public abstract T getEntity();
	
	public void setEntity(T entity) {
		this.entity = entity;
	}
	
	public abstract Validation<T> getValidation();
	
	public void validarEntidade() throws ValidationException {
		if (getValidation() == null) {
			System.out.println("Método validate() da classe '" + getClass().getSimpleName() + "' não foi implementado");
			throw new ValidationException("Método validate() da classe '" + getClass().getSimpleName() + "' não foi implementado");
		}
		getValidation().validate(getEntity());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Repository<T> getRepository(EntityManager em) {
		int f = this.getClass().getPackage().getName().indexOf("controller");
		String pack = this.getClass().getPackage().getName().substring(0, f);

		try {
			Class clazz = Class.forName(pack + "repository." + getEntity().getClass().getSimpleName() + "Repository");
			Constructor constructor = clazz.getConstructor(EntityManager.class);
			
			return (Repository<T>) constructor.newInstance(em);
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException
				| SecurityException 	| IllegalArgumentException | InvocationTargetException e) {
			System.out.println(	"Não existe um repositório (repository) para o modelo " + getEntity().getClass().getName());
			e.printStackTrace();
		}

		return null;
	}
	
	public void clean(ActionEvent actionEvent) {
		setEntity(null);
	}

}
