package com.example.electionmanagement.Model;

public class Candidate {
    public int id;
    public String name;
    public String party;
    public String symbol;
    public int electionId;

    public Candidate(int id, String name, String party, String symbol, int electionId) {
        this.id = id;
        this.name = name;
        this.party = party;
        this.symbol = symbol;
        this.electionId = electionId;
    }
}
