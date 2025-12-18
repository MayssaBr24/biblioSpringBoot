package tn.essat.config;

import tn.essat.model.User;

public class JwtResponse {
private String token;
    private User user;

    public JwtResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

}
