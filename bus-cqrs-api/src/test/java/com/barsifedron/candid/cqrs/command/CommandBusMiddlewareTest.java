package com.barsifedron.candid.cqrs.command;

import com.barsifedron.candid.cqrs.command.middleware.CommandBusDispatcher;
import com.barsifedron.candid.cqrs.command.middleware.DomainEventsDispatcher;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBus;
import com.barsifedron.candid.cqrs.domainevent.DomainEventBusMiddlewareChain;
import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import com.barsifedron.candid.cqrs.domainevent.middleware.DomainEventBusDispatcher;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

public class CommandBusMiddlewareTest {

    public CommandBusMiddlewareTest() {
    }

    @Test
    public void canDecorateACommandBus() {

        List<String> logs = new ArrayList<>();

        CommandBusMiddleware firstMiddleware = new CommandBusMiddleware() {
            @Override
            public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {
                logs.add("First middleware, before forward.");
                CommandResponse<T> commandResponse = next.dispatch(command);
                logs.add("First middleware, after forward.");
                return commandResponse;
            }
        };

        CommandBusMiddleware secondMiddleware = new CommandBusMiddleware() {
            @Override
            public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {
                logs.add("Second middleware, before forward.");
                CommandResponse<T> commandResponse = next.dispatch(command);
                logs.add("Second middleware, after forward.");
                return commandResponse;
            }
        };

        Set<CommandHandler> handlers = Stream.of(new ProducesThreeEventsCommandHandler()).collect(toSet());
        CommandBus baseBus = new CommandBus() {
            @Override
            public <T> CommandResponse<T> dispatch(Command<T> command) {
                logs.add("Decorated bus execution.");
                return new CommandBusDispatcher(handlers).dispatch(command, null);
            }
        };

        CommandBus commandBus = firstMiddleware.wrap(secondMiddleware.wrap(baseBus));
        CommandBus secondCommandBus = (firstMiddleware.wrap(secondMiddleware)).wrap(baseBus);

        commandBus.dispatch(new CommandThatProducesThreeEvents());
        assertEquals(
                "First middleware, before forward.\n" +
                        "Second middleware, before forward.\n" +
                        "Decorated bus execution.\n" +
                        "Second middleware, after forward.\n" +
                        "First middleware, after forward.",
                logs.stream().collect(Collectors.joining("\n")));

        logs.clear();

        secondCommandBus.dispatch(new CommandThatProducesThreeEvents());
        assertEquals(
                "First middleware, before forward.\n" +
                        "Second middleware, before forward.\n" +
                        "Decorated bus execution.\n" +
                        "Second middleware, after forward.\n" +
                        "First middleware, after forward.",
                logs.stream().collect(Collectors.joining("\n")));
    }

    @Test
    public void canDispatchToEventBus() {

        // Given
        DomainEventBus domainEventBus = Mockito.mock(DomainEventBus.class);
        Set<CommandHandler> handlers = Stream.of(new ProducesThreeEventsCommandHandler()).collect(toSet());
        CommandBus commandBus = new WiredCommandBus().of(
                new DomainEventsDispatcher(domainEventBus),
                new CommandBusDispatcher(handlers));

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
        // Domain event bus
        TestDomainEventHandler firstEventHandler = new TestDomainEventHandler(FirstTestDomainEvent.class);
        TestDomainEventHandler secondEventHandler = new TestDomainEventHandler(SecondTestDomainEvent.class);
        TestDomainEventHandler thirdEventHandler = new TestDomainEventHandler(ThirdTestDomainEvent.class);
        DomainEventBus eventBus = new DomainEventBusMiddlewareChain.Factory().chainOfMiddleware(
                new DomainEventBusDispatcher(
                        firstEventHandler,
                        secondEventHandler,
                        thirdEventHandler));

        // Given
        // Command Bus
        CommandBus commandBus = new WiredCommandBus().of(
                new DomainEventsDispatcher(eventBus),
                new CommandBusDispatcher(new ProducesThreeEventsCommandHandler()));
        // When
        commandBus.dispatch(new CommandThatProducesThreeEvents());

        // Then
        Assert.assertTrue(firstEventHandler.receivedEvent);
        Assert.assertTrue(secondEventHandler.receivedEvent);
        Assert.assertTrue(thirdEventHandler.receivedEvent);

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