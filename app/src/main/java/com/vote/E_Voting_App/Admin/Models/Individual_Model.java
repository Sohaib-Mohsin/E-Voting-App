package com.vote.E_Voting_App.Admin.Models;

public class Individual_Model {

    String Candidate, Party;

    public Individual_Model() {

    }

    public Individual_Model(String candidate, String party) {
        Candidate = candidate;
        Party = party;
    }

    public String getCandidate() {
        return Candidate;
    }

    public void setCandidate(String candidate) {
        Candidate = candidate;
    }

    public String getParty() {
        return Party;
    }

    public void setParty(String party) {
        Party = party;
    }
}
