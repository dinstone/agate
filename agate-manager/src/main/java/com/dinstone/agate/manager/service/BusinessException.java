package com.dinstone.agate.manager.service;

public class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;

	private int code;

	public BusinessException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
