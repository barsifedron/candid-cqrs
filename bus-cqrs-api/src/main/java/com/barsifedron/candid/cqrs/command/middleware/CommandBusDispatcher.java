package com.barsifedron.candid.cqrs.command.middleware;

import com.barsifedron.candid.cqrs.command.*;

import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * This is in charge of dispatching the command to the right Command Handler.
 * This will only allow for one handler per command type. As it should be.
 * <p>
 * When you chain many middleware, this should always be the last one in the chain.
 */
public class CommandBusDispatcher implements CommandBusMiddleware {

    private final Map<Class, CommandHandler> handlers;

    /**
     * The set of handlers will usually be injected by your dependency injection tool.
     * Examples for this can be found in the other modules.
     */
    public CommandBusDispatcher(Set<? extends CommandHandler> commandHandlers) {
        handlers = commandHandlers.stream().collect(toMap(
                handler -> handler.listenTo(),
                handler -> handler));
    }

    @Override
    public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus unreachableCommandBus) {
        CommandHandler commandHandler = Optional
                .ofNullable(handlers.get(command.getClass()))
                .orElseThrow(() -> new CommandHandlerNotFoundException(command.getClass()));
        return commandHandler.handle(command);
    }

    public class CommandHandlerNotFoundException extends RuntimeException {
        public CommandHandlerNotFoundException(Class<? extends Command> aClass) {
            super("Could not find Command Handler for command of type " + aClass.getName());
        }
    }
}
