package com.app.sangleng.evento;

public class User {
    public String username;
    public String user_email;
    public String user_address;
    public String user_phone;
    public String user_image;

    public User(){

    }

    public User(String name, String mail){
        this.username = name;
        this.user_email = mail;
        this.user_address = "";
        this.user_phone = "";
        this.user_image = "";
    }
}
