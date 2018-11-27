package com.barsifedron.candid.cqrs.command;

import com.barsifedron.candid.cqrs.command.middleware.CommandBusDispatcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;

public class CommandBusMiddlewareChainTest {

    public CommandBusMiddlewareChainTest() {
    }

    @Test(expected = java.lang.RuntimeException.class)
    public void shouldFailToConstructEmptyMiddlewareChain() {
        new CommandBusMiddlewareChain.Factory().chain(new ArrayList<>());
    }

    @Test(expected = java.lang.RuntimeException.class)
    public void shouldFailIfNoDispatcherMiddleware() {
        new CommandBusMiddlewareChain.Factory().chain(new FirstTestMiddleware());
    }

    @Test(expected = java.lang.RuntimeException.class)
    public void shouldFailIfLastMiddlewareInChainIsNotTheDispatcher() {
        new CommandBusMiddlewareChain.Factory().chain(
                new FirstTestMiddleware(),
                new CommandBusDispatcher(new HashSet<>()),
                new SecondTestMiddleware()
        );
    }

    @Test
    public void shouldBuildAChainOfMiddleware() {

        CommandBusMiddlewareChain chain = new CommandBusMiddlewareChain.Factory().chain(
                new FirstTestMiddleware(),
                new CommandBusDispatcher(new HashSet<>()));

        assertTrue(chain.usesMiddlewareInstanceOf(FirstTestMiddleware.class));
        assertTrue(chain.usesMiddlewareInstanceOf(CommandBusDispatcher.class));
        assertFalse(chain.usesMiddlewareInstanceOf(SecondTestMiddleware.class));
    }

    @Test(expected = CommandBusDispatcher.CommandHandlerNotFoundException.class)
    public void shouldFailToProcessCommandsWhenNoRightHandler() {

        CommandBusMiddlewareChain chain = new CommandBusMiddlewareChain.Factory().chain(
                new FirstTestMiddleware(),
                new SecondTestMiddleware(),
                new CommandBusDispatcher(new HashSet<>()));

        chain.dispatch(new DoNothingCommand());
    }

    @Test
    public void shouldProcessCommandsWhenRightHandler() {
        Set<DoNothingCommandHandler> handlers = Stream.of(new DoNothingCommandHandler()).collect(toSet());
        CommandBusMiddlewareChain chain = new CommandBusMiddlewareChain.Factory().chain(
                new FirstTestMiddleware(),
                new SecondTestMiddleware(),
                new CommandBusDispatcher(handlers));
        CommandResponse<NoResult> response = chain.dispatch(new DoNothingCommand());
    }


    static class FirstTestMiddleware implements CommandBusMiddleware {

        private final static Logger LOGGER = Logger.getLogger(FirstTestMiddleware.class.getName());

        @Override
        public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {
            LOGGER.info("FirstTestMiddleware : dispatching");
            CommandResponse<T> response = next.dispatch(command);
            LOGGER.info("FirstTestMiddleware : dispatched");
            return response;
        }
    }

    static class SecondTestMiddleware implements CommandBusMiddleware {

        private final static Logger LOGGER = Logger.getLogger(SecondTestMiddleware.class.getName());

        @Override
        public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {
            LOGGER.info("SecondTestMiddleware : dispatching");
            CommandResponse<T> response = next.dispatch(command);
            LOGGER.info("SecondTestMiddleware : dispatched");
            return response;
        }
    }

    static class DoNothingCommand implements Command<NoResult> {

    }

    static class DoNothingCommandHandler implements CommandHandler<NoResult, DoNothingCommand> {
        @Override
        public CommandResponse<NoResult> handle(DoNothingCommand command) {
            return CommandResponse.withoutResult();
        }

        @Override
        public Class<DoNothingCommand> listenTo() {
            return DoNothingCommand.class;
        }
    }

}