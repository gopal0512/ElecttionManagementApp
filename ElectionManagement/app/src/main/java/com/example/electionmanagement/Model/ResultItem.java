package com.example.electionmanagement.Model;

public class ResultItem {
    public String name;
    public String party;
    public int votes;
    public ResultItem(String name, String party, int votes) {
        this.name = name;
        this.party = party;
        this.votes = votes;
    }
}
