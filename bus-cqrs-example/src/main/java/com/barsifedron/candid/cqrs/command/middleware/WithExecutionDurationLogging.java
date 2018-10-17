package com.barsifedron.candid.cqrs.command.middleware;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.CommandBusMiddlewareChain;
import com.barsifedron.candid.cqrs.command.CommandResponse;

import java.util.logging.Logger;


/**
 * For the sake of providing an example of a decorating function:
 * A decorator calculating the execution time of our commands handling.
 * <p>
 * Now it is up to you to create others! Some possible things here : Validating your command dtos,
 * wrapping the command execution within a database transaction, etc...
 * The sky is your limit
 */
public class WithExecutionDurationLogging implements CommandBusMiddleware {

    private final static Logger LOGGER = Logger.getLogger(WithExecutionDurationLogging.class.getName());

    public <T> CommandResponse<T> dispatch(Command<T> command, CommandBusMiddlewareChain next) {

        LOGGER.info("Processing simple command of type :" + command.getClass().getName());

        long timeBefore = System.nanoTime();
        CommandResponse<T> result = next.dispatch(command);
        long timeAfter = System.nanoTime();

        LOGGER.info("" +
                "Done processing command of type" + command.getClass().getName() +
                "\nExecution time was :" + ((timeAfter - timeBefore) / 1000000) + " ms");
        return result;
    }

}