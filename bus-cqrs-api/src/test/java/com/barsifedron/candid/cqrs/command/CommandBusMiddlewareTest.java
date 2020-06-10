package com.barsifedron.candid.cqrs.command;

import com.barsifedron.candid.cqrs.command.middleware.CommandBusDispatcher;
import com.barsifedron.candid.cqrs.command.middleware.CommandResponseDomainEventsDispatcher;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddleware;
import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import com.barsifedron.candid.cqrs.domainevent.middleware.DomainEventBusDispatcher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;

public class CommandBusMiddlewareTest {

    public CommandBusMiddlewareTest() {
    }

    @Test
    public void canDecorateACommandBusOrACommandMiddleware() {

        List<String> logs = new ArrayList<>();

        CommandBusMiddleware firstMiddleware = new CommandBusMiddleware() {
            @Override
            public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {
                logs.add("First middleware");
                CommandResponse<T> commandResponse = next.dispatch(command);
                logs.add("First middleware");
                return commandResponse;
            }
        };

        CommandBusMiddleware secondMiddleware = new CommandBusMiddleware() {
            @Override
            public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {
                logs.add("\tSecond middleware");
                CommandResponse<T> commandResponse = next.dispatch(command);
                logs.add("\tSecond middleware");
                return commandResponse;
            }
        };

        CommandBus baseBus = new CommandBus() {
            @Override
            public <T> CommandResponse<T> dispatch(Command<T> command) {
                logs.add("\t\tDecorated bus execution.");
                return new CommandBusDispatcher(new ProducesThreeEventsCommandHandler()).dispatch(command, null);
            }
        };

        CommandBus commandBus = firstMiddleware.decorate(secondMiddleware.decorate(baseBus));
        CommandBus secondCommandBus = (firstMiddleware.decorate(secondMiddleware)).decorate(baseBus);

        commandBus.dispatch(new CommandThatProducesThreeEvents());
        assertEquals(
                "First middleware\n" +
                        "\tSecond middleware\n" +
                        "\t\tDecorated bus execution.\n" +
                        "\tSecond middleware\n" +
                        "First middleware",
                logs.stream().collect(Collectors.joining("\n")));

        logs.clear();

        secondCommandBus.dispatch(new CommandThatProducesThreeEvents());
        assertEquals(
                "First middleware\n" +
                        "\tSecond middleware\n" +
                        "\t\tDecorated bus execution.\n" +
                        "\tSecond middleware\n" +
                        "First middleware",
                logs.stream().collect(Collectors.joining("\n")));
    }

    @Test
    public void shouldFailToConstructEmptyMiddlewareChain() {

        assertThrows(
                RuntimeException.class,
                () ->
                        CommandBusMiddleware.chainManyIntoACommandBus()
        )

        ;
    }

    @Test
    public void shouldFailIfNoDispatcherMiddleware() {
        assertThrows(
                RuntimeException.class,
                () -> CommandBusMiddleware.chainManyIntoACommandBus(new FirstTestMiddleware()));
    }

    @Test
    public void shouldFailIfLastMiddlewareInChainIsNotTheDispatcher() {
        assertThrows(
                RuntimeException.class,
                () ->

                        CommandBusMiddleware.chainManyIntoACommandBus(
                                new FirstTestMiddleware(),
                                new CommandBusDispatcher(new HashSet<>()),
                                new SecondTestMiddleware()
                        ));
    }

    @Test
    public void shouldFailToProcessCommandsWhenNoMatchingHandler() {
        assertThrows(
                CommandBusDispatcher.CommandHandlerNotFoundException.class,
                () -> CommandBusMiddleware
                        .chainManyIntoACommandBus(
                                new FirstTestMiddleware(),
                                new SecondTestMiddleware(),
                                new CommandBusDispatcher(new HashSet<>()))
                        .dispatch(new CommandThatProducesThreeEvents()));
    }

    @Test
    public void canDispatchToEventBus() {

        // Given
        DomainEventBus domainEventBus = Mockito.mock(DomainEventBus.class);
        CommandBus commandBus = CommandBusMiddleware.chainManyIntoACommandBus(
                new CommandResponseDomainEventsDispatcher(domainEventBus),
                new CommandBusDispatcher(new ProducesThreeEventsCommandHandler()));

        // when
        CommandResponse<NoResult> response = commandBus.dispatch(new CommandThatProducesThreeEvents());

        // then
        for (DomainEvent domainEvent : response.domainEvents) {
            Mockito.verify(domainEventBus, times(1)).dispatch(domainEvent);
        }
        Mockito.verifyNoMoreInteractions(domainEventBus);

    }

    @Test
    public void canDispatchCommandResponsesEventsToRightEventListener() {

        // Given
        TestDomainEventHandler firstEventHandler = new TestDomainEventHandler(FirstTestDomainEvent.class);
        TestDomainEventHandler secondEventHandler = new TestDomainEventHandler(SecondTestDomainEvent.class);
        TestDomainEventHandler thirdEventHandler = new TestDomainEventHandler(ThirdTestDomainEvent.class);
        DomainEventBus eventBus = DomainEventBusMiddleware.chainManyIntoADomainEventBus(
                new DomainEventBusDispatcher(
                        firstEventHandler,
                        secondEventHandler,
                        thirdEventHandler));

        // Given
        CommandBus commandBus = CommandBusMiddleware.chainManyIntoACommandBus(
                new CommandResponseDomainEventsDispatcher(eventBus),
                new CommandBusDispatcher(new ProducesThreeEventsCommandHandler()));

        // When
        commandBus.dispatch(new CommandThatProducesThreeEvents());

        // Then
        assertTrue(firstEventHandler.receivedEvent);
        assertTrue(secondEventHandler.receivedEvent);
        assertTrue(thirdEventHandler.receivedEvent);

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

    static class CommandThatProducesThreeEvents implements Command<NoResult> {
    }

    static class ProducesThreeEventsCommandHandler implements CommandHandler<NoResult, CommandThatProducesThreeEvents> {
        @Override
        public CommandResponse<NoResult> handle(CommandThatProducesThreeEvents command) {
            return CommandResponse.empty().withAddedDomainEvents(
                    new FirstTestDomainEvent(),
                    new SecondTestDomainEvent(),
                    new ThirdTestDomainEvent());
        }

        @Override
        public Class<CommandThatProducesThreeEvents> listenTo() {
            return CommandThatProducesThreeEvents.class;
        }

    }

    static class FirstTestDomainEvent implements DomainEvent {
    }

    static class SecondTestDomainEvent implements DomainEvent {
    }

    static class ThirdTestDomainEvent implements DomainEvent {
    }

    static class TestDomainEventHandler<K extends DomainEvent> implements DomainEventHandler<K> {

        boolean receivedEvent = false;
        private Class<K> aDomainEventClass;

        public TestDomainEventHandler(Class<K> aDomainEventClass) {
            this.aDomainEventClass = aDomainEventClass;
        }

        @Override
        public void handle(K event) {
            receivedEvent = true;
        }

        @Override
        public Class<K> listenTo() {
            return aDomainEventClass;
        }
    }
}