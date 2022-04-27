
package io.agate.admin.resource;

import org.springframework.stereotype.Component;

import com.dinstone.vertx.web.annotation.Context;
import com.dinstone.vertx.web.annotation.Get;
import com.dinstone.vertx.web.annotation.WebHandler;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

@Component
@WebHandler("/account")
public class AccountResource {

    @Get
    public void info(@Context RoutingContext rc) {
        // result json
        JsonObject json = new JsonObject();
        // set code
        json.put("code", 1);
        try {
            User user = rc.user();
            json.put("result", user.principal());
            writeJsonResponse(rc, json);
        } catch (Exception e) {
            json.put("code", -1);
            json.put("cause", e.getMessage());
            writeJsonResponse(rc, json);
        }

    }

    private void writeJsonResponse(RoutingContext rc, JsonObject json) {
        HttpServerResponse response = rc.response();
        response.putHeader("Content-Type", "application/json");
        response.end(json.toBuffer());
    }

}
