package com.example.electionmanagement.Model;

public class Voter {
    public int id;
    public String firstName;
    public String lastName;
    public String address;
    public String voterCard;
    public String email;
    public String phone;
    public String password;
    public String state;

    public Voter(int id, String firstName, String lastName, String address,
                 String voterCard, String email, String phone, String password, String state) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.voterCard = voterCard;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.state = state;
    }

    public int getId() {
        return id;
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

    public String getVoterCard() {
        return voterCard;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getState() {
        return state;
    }
}
