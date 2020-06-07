package com.barsifedron.candid.cqrs.springboot.app.library.domain;

import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.Id;

@Builder
@Entity
public class Student {

    @Id
    private long id;
    private String name;


    public Student() {
    }

    public Student(long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}