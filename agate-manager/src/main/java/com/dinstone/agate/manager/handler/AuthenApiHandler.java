/*
 * Copyright (C) 2016~2019 dinstone<dinstone@163.com>
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
package com.dinstone.agate.manager.handler;

import com.dinstone.agate.manager.authen.AuthenProvider;
import com.dinstone.agate.manager.authen.AuthenUser;
import com.dinstone.agate.manager.context.ApplicationContext;
import com.dinstone.agate.manager.utils.RestfulUtil;
import com.dinstone.vertx.web.annotation.Context;
import com.dinstone.vertx.web.annotation.Get;
import com.dinstone.vertx.web.annotation.Post;
import com.dinstone.vertx.web.annotation.Produces;
import com.dinstone.vertx.web.annotation.WebHandler;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@WebHandler("/authen")
@Produces({ "application/json" })
public class AuthenApiHandler {

	private AuthenProvider authenProvider;

	public AuthenApiHandler(ApplicationContext context) {
		authenProvider = context.getAuthenProvider();
	}

	@Post
	public void login(@Context RoutingContext ctx) {
		JsonObject params = ctx.getBodyAsJson();
		String un = params.getString("username");
		String pw = params.getString("password");
		if (un == null || un.isEmpty()) {
			RestfulUtil.failed(ctx, "Username is empty");
			return;
		}
		if (pw == null || pw.isEmpty()) {
			RestfulUtil.failed(ctx, "Password is empty");
			return;
		}

		AuthenUser user = authenProvider.authenticate(un, pw);
		if (user != null) {
			ctx.session().put("user", user);
			RestfulUtil.success(ctx, "authen is ok");
		} else {
			RestfulUtil.failed(ctx, "username or password is error");
		}

	}

	@Get
	public void logout(@Context RoutingContext ctx) {
		ctx.session().destroy();
		RestfulUtil.success(ctx);
	}

}