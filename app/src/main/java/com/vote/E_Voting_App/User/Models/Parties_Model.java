package com.vote.E_Voting_App.User.Models;

public class Parties_Model {

    String PartyName, PartyDescription, ImageURL, Candidate;

    public Parties_Model() {
    }

    public Parties_Model(String partyName, String partyDescription, String imageURL) {
        PartyName = partyName;
        PartyDescription = partyDescription;
        ImageURL = imageURL;
    }

    public Parties_Model(String partyName, String partyDescription, String imageURL, String candidate) {
        PartyName = partyName;
        PartyDescription = partyDescription;
        ImageURL = imageURL;
        Candidate = candidate;
    }

    public Parties_Model(String partyName, String partyDescription) {
        PartyName = partyName;
        PartyDescription = partyDescription;
    }

    public String getCandidate() {
        return Candidate;
    }

    public void setCandidate(String candidate) {
        Candidate = candidate;
    }

    public String getPartyName() {
        return PartyName;
    }

    public void setPartyName(String partyName) {
        PartyName = partyName;
    }

    public String getPartyDescription() {
        return PartyDescription;
    }

    public void setPartyDescription(String partyDescription) {
        PartyDescription = partyDescription;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
