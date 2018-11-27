package com.barsifedron.candid.app.members.domain;

import java.util.Objects;

public class Member {

    private MemberId memberId;
    private String email;
    private String firstName;
    private String familyName;


    public Member(String email, String firstName, String familyName) {
        this(new MemberId(), email, firstName, familyName);
    }

    public Member(MemberId memberId, String email, String firstName, String familyName) {
        this.memberId = memberId;
        this.email = email;
        this.firstName = firstName;
        this.familyName = familyName;
    }

    public MemberId memberId() {
        return memberId;
    }

    public String email() {
        return email;
    }

    public String firstName() {
        return firstName;
    }

    public String familyName() {
        return familyName;
    }

    public boolean hasEmail(String candidateEmail) {
        return Objects.equals(email, candidateEmail);
    }

    public boolean hasFirstName(String candidateFirstName) {
        return Objects.equals(firstName, candidateFirstName);
    }

    public boolean hasFamilyName(String candidateFamilyName) {
        return Objects.equals(familyName, candidateFamilyName);
    }
}
