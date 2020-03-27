package com.dinstone.agate.manager.authen;

public interface AuthenProvider {

    AuthenUser authenticate(String un, String pw);

}
