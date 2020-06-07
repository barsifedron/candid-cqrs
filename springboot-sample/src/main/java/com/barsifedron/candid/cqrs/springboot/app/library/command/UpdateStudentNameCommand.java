package com.barsifedron.candid.cqrs.springboot.app.library.command;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.NoResult;
import lombok.Builder;

@Builder
public class UpdateStudentNameCommand
        implements Command<NoResult> {

    public UpdateStudentNameCommand() {
    }
}
