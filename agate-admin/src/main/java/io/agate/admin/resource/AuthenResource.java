
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
            JsonObject authInfo = rc.getBodyAsJson();
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
