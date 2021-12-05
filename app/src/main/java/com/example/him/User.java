package com.example.him;

public class User {

    private String userId;              // 아이디
    private String password;            // 비밀번호

    private String name;                // 이름

    public User() {}

    public User(String userId, String password, String name) {
        this.userId = userId;
        this.password = password;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
