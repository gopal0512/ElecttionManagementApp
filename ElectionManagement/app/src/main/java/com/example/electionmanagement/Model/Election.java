package com.example.electionmanagement.Model;

public class Election {
    public int id;
    public String title;
    public String state;
    public String startIso;
    public String endIso;

    public Election(int id, String title, String state, String startIso, String endIso) {
        this.id = id; this.title = title; this.state = state; this.startIso = startIso; this.endIso = endIso;
    }
}
