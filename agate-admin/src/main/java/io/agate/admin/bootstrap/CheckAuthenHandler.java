
package io.agate.admin.bootstrap;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.HttpException;
import io.vertx.ext.web.handler.impl.AuthenticationHandlerImpl;

public class CheckAuthenHandler extends AuthenticationHandlerImpl<AuthenticationProvider> {

    private CheckAuthenHandler() {
        super(null);
    }

    public static CheckAuthenHandler create() {
        return new CheckAuthenHandler();
    }

    @Override
    public void authenticate(RoutingContext context, Handler<AsyncResult<User>> handler) {
        Session session = context.session();
        if (session == null) {
            handler.handle(Future.failedFuture("No session - did you forget to include a SessionHandler?"));
        } else {
            handler.handle(Future.failedFuture(new HttpException(401)));
        }
    }

}
