package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.command.middleware;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.CommandResponse;

import java.util.logging.Logger;

public class WithErrorLogCommandBusMiddleware implements CommandBusMiddleware {

    private final static Logger LOGGER = Logger.getLogger(WithErrorLogCommandBusMiddleware.class.getName());

    @Override
    public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus bus) {
        try {
            CommandResponse<T> response = bus.dispatch(command);
            return response;
        } catch (Exception exception) {
            LOGGER.info("Failed to process command due to error : " + exception.getMessage());
            throw exception;
        }
    }
}
