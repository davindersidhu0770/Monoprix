package com.production.monoprix.ui.login;

public interface AuthenticationListener {
    void onTokenReceived(String auth_token);
}
