package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandResultToLog;
import com.barsifedron.candid.cqrs.command.CommandToLog;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegisterNewMemberCommand implements Command<MemberId> , CommandToLog, CommandResultToLog {

    @Email
    public final String email;
    @NotEmpty
    public final String firstname;
    @NotEmpty
    public final String surname;
    @NotEmpty
    public final String memberId;

}
