package br.unitins.frame.validation;

import br.unitins.frame.application.ValidationException;

public interface Validation<T> {
	public void validate(T t) throws ValidationException;
}
