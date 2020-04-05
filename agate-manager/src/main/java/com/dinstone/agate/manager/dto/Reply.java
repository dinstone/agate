package com.dinstone.agate.manager.dto;

public class Reply {

	private static final int OK = 0;

	private int code;

	private String message;

	private Object result;

	public Reply() {
	}

	public Reply(int code, Object result) {
		super();
		this.code = code;
		this.result = result;
	}

	public Reply(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public static Reply success() {
		return new Reply();
	}

	public static Reply success(Object result) {
		return new Reply(OK, result);
	}

	public static Reply failed(int code, String message) {
		if (code == OK) {
			throw new IllegalArgumentException("failure code is equal 0");
		}
		return new Reply(code, message);
	}

}
