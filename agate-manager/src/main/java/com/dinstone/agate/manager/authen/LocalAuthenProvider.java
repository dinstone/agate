package com.dinstone.agate.manager.authen;

import io.vertx.core.json.JsonObject;

public class LocalAuthenProvider implements AuthenProvider {

    private JsonObject users;

    public LocalAuthenProvider(JsonObject users) {
        this.users = users;
    }

    @Override
    public AuthenUser authenticate(String un, String pw) {
        JsonObject userAttr = users.getJsonObject(un);
        if (userAttr != null && pw.equals(userAttr.getString("password"))) {
            AuthenUser user = new AuthenUser(un, pw);
            user.setPrincipal(userAttr);
            return user;
        }
        return null;
    }

}
