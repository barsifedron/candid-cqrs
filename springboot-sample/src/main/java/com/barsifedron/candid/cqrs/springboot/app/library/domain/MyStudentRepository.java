package com.barsifedron.candid.cqrs.springboot.app.library.domain;

import com.barsifedron.candid.cqrs.happy.domain.Student;

public interface MyStudentRepository {


    void save(Student student);
}
