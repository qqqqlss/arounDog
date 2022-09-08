package com.example.aroundog.dto;

import com.example.aroundog.Model.Gender;

public class UserDto {
    private Long id;
    private String password;
    private Integer age;
    private int image;
    private String userName;
    private String phone;
    private String email;
    private Gender gender;

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public Integer getAge() {
        return age;
    }

    public int getImage() {
        return image;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Gender getGender() {
        return gender;
    }

    public UserDto() {

    }

    public UserDto(String password, Integer age, int image, String userName, String phone, String email, Gender gender) {
        this.password = password;
        this.age = age;
        this.image = image;
        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
    }
}
