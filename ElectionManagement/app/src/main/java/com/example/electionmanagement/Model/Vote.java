package com.example.electionmanagement.Model;

public class Vote {
    public int id;
    public int voterId;
    public int candidateId;
    public int electionId;

    public Vote(int id, int voterId, int candidateId, int electionId) {
        this.id = id;
        this.voterId = voterId;
        this.candidateId = candidateId;
        this.electionId = electionId;
    }
}
