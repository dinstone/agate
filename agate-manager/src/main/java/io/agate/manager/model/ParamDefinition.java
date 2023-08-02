/*
 * Copyright (C) 2020~2023 dinstone<dinstone@163.com>
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
package io.agate.manager.model;

public class ParamDefinition {

	private String feParamName;

	/**
	 * Header/Query/Path
	 */
	private ParamType feParamType;

	private String beParamName;

	/**
	 * Header/Query/Path
	 */
	private ParamType beParamType;

	public ParamDefinition() {
		super();
	}

	public String getFeParamName() {
		return feParamName;
	}

	public void setFeParamName(String feParamName) {
		this.feParamName = feParamName;
	}

	public ParamType getFeParamType() {
		return feParamType;
	}

	public void setFeParamType(ParamType feParamType) {
		this.feParamType = feParamType;
	}

	public String getBeParamName() {
		return beParamName;
	}

	public void setBeParamName(String beParamName) {
		this.beParamName = beParamName;
	}

	public ParamType getBeParamType() {
		return beParamType;
	}

	public void setBeParamType(ParamType beParamType) {
		this.beParamType = beParamType;
	}

}
