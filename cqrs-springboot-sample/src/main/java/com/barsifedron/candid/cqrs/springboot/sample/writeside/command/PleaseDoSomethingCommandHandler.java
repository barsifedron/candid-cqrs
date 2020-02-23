package com.barsifedron.candid.cqrs.springboot.sample.writeside.command;

import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.springboot.sample.writeside.domainevent.SomethingWasDoneEvent;
import org.springframework.stereotype.Component;

@Component
public class PleaseDoSomethingCommandHandler implements CommandHandler<String, PleaseDoSomethingCommand> {

    @Override
    public CommandResponse<String> handle(PleaseDoSomethingCommand command) {
        return CommandResponse
                .empty()
                .withResult("DONE!")
                .withAddedDomainEvents(
                        new SomethingWasDoneEvent(
                                "barsifedron@no-spam.com",
                                666)
                );
    }

    @Override
    public Class<PleaseDoSomethingCommand> listenTo() {
        return PleaseDoSomethingCommand.class;
    }

}
