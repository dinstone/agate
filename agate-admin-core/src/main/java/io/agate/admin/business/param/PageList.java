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
package io.agate.admin.business.param;

import java.util.List;

public class PageList<T> {

	private int total;

	private List<T> list;

	public PageList() {
	}

	public PageList(List<T> list) {
		if (list != null) {
			this.total = list.size();
		}
		this.list = list;
	}

	public PageList(int total) {
		this.total = total;
	}

	public PageList(List<T> list, int total) {
		this.list = list;
		this.total = total;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getList() {
		return list;
	}

	public PageList<T> setList(List<T> list) {
		this.list = list;
		return this;
	}

}
