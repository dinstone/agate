package com.dinstone.agate.manager.authen;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;

public class AuthenUser extends AbstractUser {

    private String username;

    private String password;

    private JsonObject attributes = new JsonObject();

    private JsonObject principal;

    public AuthenUser(String username) {
        super();
        this.username = username;
    }

    public AuthenUser(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JsonObject getPrincipal() {
        return principal;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public JsonObject principal() {
        return principal;
    }

    /**
     * 设置用户附加信息
     * 
     * @param principal
     */
    public void setPrincipal(JsonObject principal) {
        this.principal = principal;
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {
    }

    @Override
    protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {
        JsonArray roles = principal.getJsonArray("roles");
        resultHandler.handle(Future.<Boolean>succeededFuture((roles != null && roles.contains(permission))));
    }

    public JsonObject attributes() {
        return attributes;
    }
}
