package com.example.myapplication.Helpers;

public class LoginDataHolder {
    private static LoginDataHolder instance;
    private String idUser;
    private String authType;
    private String apikey;
    private String username;
    private boolean isConnected = false;

    private LoginDataHolder() {}

    public static synchronized LoginDataHolder getInstance() {
        if (instance == null) {
            instance = new LoginDataHolder();
        }
        return instance;
    }

    public void setLoginData(String idUser, String authType, String apikey, String username) {
        this.idUser = idUser;
        this.authType = authType;
        this.apikey = apikey;
        this.username = username;
        this.isConnected = true;
    }

    public void logout() {
        this.idUser = null;
        this.authType = null;
        this.apikey = null;
        this.username = null;
        this.isConnected = false;
    }
    public String getIdUser() {
        return idUser;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthType() {
        return authType;
    }

    public String getApikey() {
        return apikey;
    }
}
