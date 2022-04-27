
package io.agate.admin.resource;

import org.springframework.stereotype.Component;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;

@Component
public class LocalAuthenProvider implements AuthenticationProvider {

    @Override
    public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> resultHandler) {
        String username = credentials.getString("username");
        String password = credentials.getString("password");
        if ("agate".equals(username) && "123456".equals(password)) {
            // get result
            User user = User.fromName(username);
            resultHandler.handle(Future.succeededFuture(user));
        } else {
            resultHandler.handle(Future.failedFuture("username or password incorrect"));
        }
    }
}