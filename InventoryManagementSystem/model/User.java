package model;

import java.io.Serializable;

/** Admin user who can log in. */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }

    public boolean matches(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }
}
