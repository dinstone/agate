/*
 * Copyright (C) 2020~2022 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
