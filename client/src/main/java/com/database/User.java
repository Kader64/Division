package com.database;

public class User {
    private String nick;
    private String password;
    private String email;
    private String icon;

    public User(String nick, String password, String email, String text) {
        this.nick = nick;
        this.password = password;
        this.email = email;
        this.icon = text;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
