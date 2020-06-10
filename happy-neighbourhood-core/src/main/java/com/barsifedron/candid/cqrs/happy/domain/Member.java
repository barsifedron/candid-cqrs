package com.barsifedron.candid.cqrs.happy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Access(AccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Entity
@Table(name = "member")
public class Member {

    @Embedded
    @EmbeddedId
    @AttributeOverride(name = "id", column = @Column(updatable = false))
    private MemberId memberId;

    @Column(name = "email")
    private String email;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "surname")
    private String surname;

    @Column(name = "registeredon")
    private LocalDateTime registeredOn;

    public MemberId memberId() {
        return memberId;
    }

    public boolean hasEmail(String candidate) {
        return email.equals(candidate);
    }

    public boolean hasSurname(String candidate) {
        return surname.equals(candidate);
    }

    public boolean hasFirstname(String candidate) {
        return firstname.equals(candidate);
    }

    public LocalDateTime registeredOn() {
        return registeredOn;
    }

    public String firstName() {
        return firstname;
    }

    public String surname() {
        return surname;
    }

    public String email() {
        return email;
    }
}
