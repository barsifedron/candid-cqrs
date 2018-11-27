package com.barsifedron.candid.app.members.command;

import com.barsifedron.candid.app.members.domain.MemberId;
import com.barsifedron.candid.cqrs.command.Command;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class RegisterNewMember implements Command<MemberId> {

    @Email(message = "Needs a valid email")
    @NotBlank(message = "Needs a valid email")
    public String email;

    @NotBlank(message = "Needs a valid first name")
    public String firstName;

    @NotBlank(message = "Needs a valid family name")
    public String familyName;

    public RegisterNewMember(
            String email,
            String firstName,
            String familyName) {
        this.email = email;
        this.firstName = firstName;
        this.familyName = familyName;
    }
}
