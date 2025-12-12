package com.browserstack.models;

public class UserData {
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String state;
    private String postalCode;

    public UserData(String userName, String password, String firstName, String lastName, String address, String state, String postalCode) {
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.state = state;
        this.postalCode = postalCode;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }
}
