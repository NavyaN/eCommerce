package com.example.ecommerce.Model;

public class Users {

    private String name, password, phone;

    public Users(){

    }

    public Users(String name, String password, String phone) {
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

}
