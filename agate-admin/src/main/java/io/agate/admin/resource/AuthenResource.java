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

package io.agate.admin.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dinstone.vertx.web.annotation.Context;
import com.dinstone.vertx.web.annotation.Path;
import com.dinstone.vertx.web.annotation.Post;
import com.dinstone.vertx.web.annotation.WebHandler;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

@Component
@WebHandler("/authen")
public class AuthenResource {

    @Autowired
    private AuthenticationProvider authProvider;

    @Post
    public void login(@Context RoutingContext rc) {
        // result json
        JsonObject json = new JsonObject();
        // set code
        json.put("code", 1);

        try {
            // get request body
            JsonObject authInfo = rc.body().asJsonObject();
            authProvider.authenticate(authInfo, ar -> {
                if (ar.succeeded()) {
                    rc.setUser(ar.result());
                    // get session
                    Session session = rc.session();
                    if (session != null) {
                        // regenerate id
                        session.regenerateId();
                        // session account
                        session.put("LoginAccount", ar.result());
                    }
                    json.put("result", true);
                } else {
                    json.put("result", false);
                }

                writeJsonResponse(rc, json);
            });

        } catch (Exception e) {
            json.put("result", false);
            json.put("cause", e.getMessage());

            writeJsonResponse(rc, json);
        }
    }

    @Path
    public void logout(@Context RoutingContext rc) {
        // remove session account
        rc.session().remove("LoginAccount");
        // clear session user
        rc.clearUser();
        // go to the login page
        rc.response().putHeader("location", "/").setStatusCode(302).end();
    }

    private void writeJsonResponse(RoutingContext rc, JsonObject json) {
        HttpServerResponse response = rc.response();
        response.putHeader("Content-Type", "application/json");
        response.end(json.toBuffer());
    }

}
