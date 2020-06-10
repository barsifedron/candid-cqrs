package com.barsifedron.candid.cqrs.happy.utils.cqrs.command.middleware;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.CommandResultToLog;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.CommandToLog;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.domainevents.DomainEventToLog;

import java.util.List;
import java.util.logging.Logger;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class DetailedLoggingCommandBusMiddleware implements CommandBusMiddleware {

    private final static Logger LOGGER = Logger.getLogger(
            DetailedLoggingCommandBusMiddleware.class.getName());

    @Override
    public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {

        logCommand(command);
        CommandResponse<T> commandResponse = next.dispatch(command);
        logCommandResponse(command, commandResponse);
        logDomainEvents(commandResponse);

        return commandResponse;
    }

    private <T> void logCommand(Command<T> command) {
        boolean logCommandDetail = CommandToLog.class.isAssignableFrom(command.getClass());
        if (logCommandDetail) {
            LOGGER.info("\n\nProcessing command :\n" + command.toString());
        }
        if (!logCommandDetail) {
            LOGGER.info("\n\nProcessing  command of type :\n" + command.getClass().getName());
        }
    }

    private <T> void logCommandResponse(Command<T> command, CommandResponse<T> commandResponse) {
        boolean logCommandResult = CommandResultToLog.class.isAssignableFrom(command.getClass());
        if (logCommandResult) {
            LOGGER.info("Command response was : " + commandResponse.result);
        }
        if (!logCommandResult) {
            LOGGER.info("Command response was of type : " + commandResponse.result.getClass().getName());
        }
    }

    private <T> void logDomainEvents(CommandResponse<T> commandResponse) {

        List<DomainEvent> eventsToLog = commandResponse.domainEvents
                .stream()
                .filter(evt -> DomainEventToLog.class.isAssignableFrom(evt.getClass()))
                .collect(toList());

        LOGGER.info("Command generated "
                + commandResponse.domainEvents.size()
                + " domain events (local) of types :\n "
                + commandResponse.domainEvents.stream().map(evt -> evt.getClass().getName()).collect(joining("\n")));

        if (eventsToLog.isEmpty()) {
            return;
        }

        LOGGER.info("" +
                "Safely loggable domain events (local) are :\n  "
                + eventsToLog
                .stream()
                .map(evt -> evt.toString())
                .collect(joining("\n")));

    }
}
