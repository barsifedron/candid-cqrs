package com.barsifedron.candid.cqrs.command;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * This is in charge of dispatching the command to the right Command Handler.
 * This will only allow for one handler per command type. As it should be.
 */
public class MapCommandBus implements CommandBus {

    private final Map<Class<Command>, Supplier<CommandHandler>> handlers;

    public MapCommandBus(CommandHandler... commandHandlers) {
        this(Stream.of(commandHandlers).collect(toSet()));
    }

    public MapCommandBus(Set<? extends CommandHandler> handlers) {
        this(handlers.stream().collect(toMap(
                handler -> handler.listenTo(),
                handler -> () -> handler))
        );
    }

    /**
     * The set of handlers will usually be injected by your dependency injection tool.
     * Examples for this can be found in the other modules.
     */
    public MapCommandBus(Map<Class<Command>, Supplier<CommandHandler>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public <T> CommandResponse<T> dispatch(Command<T> command) {
        CommandHandler commandHandler = Optional
                .ofNullable(handlers.get(command.getClass()))
                .map(handlerSupplier -> handlerSupplier.get())
                .orElseThrow(() -> new CommandHandlerNotFoundException(command.getClass()));
        return commandHandler.handle(command);
    }

    public static class CommandHandlerNotFoundException extends RuntimeException {
        public CommandHandlerNotFoundException(Class<? extends Command> aClass) {
            super("Could not find Command Handler for command of type " + aClass.getName());
        }
    }
}
