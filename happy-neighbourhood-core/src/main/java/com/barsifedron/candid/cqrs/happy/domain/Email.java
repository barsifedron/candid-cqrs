package com.barsifedron.candid.cqrs.happy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.annotation.Generated;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Access(AccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@Entity
@Table(name = "email")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "body", length = 1024)
    private String body;
    @Column(name = "email")
    private String email;
    @Column(name = "status")
    private EMAIL_STATUS status;

    public Integer id() {
        return id;
    }

    public static enum EMAIL_STATUS {
        TO_BE_SENT, SENT
    }
}
