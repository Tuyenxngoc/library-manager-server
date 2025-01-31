package com.example.librarymanager.constant;

public class SuccessMessage {

    public static final String CREATE = "success.create";
    public static final String UPDATE = "success.update";
    public static final String DELETE = "success.delete";

    public static class User {
        public static final String CHANGE_PASSWORD = "success.user.change-password";
        public static final String FORGET_PASSWORD = "success.user.send.password";
    }

    public static class Auth {
        public static final String LOGOUT = "success.user.logout";
    }
}
