package com.fedex.smartpost.mts.exceptions;

import org.springframework.core.NestedRuntimeException;

public class EvccRuntimeException extends NestedRuntimeException {
	private static final long serialVersionUID = 1L;

	public EvccRuntimeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
