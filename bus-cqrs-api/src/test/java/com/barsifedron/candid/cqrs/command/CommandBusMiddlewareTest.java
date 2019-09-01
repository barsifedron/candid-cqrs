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

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.mockito.Mockito.times;

public class CommandBusMiddlewareTest {

    public CommandBusMiddlewareTest() {
    }

    @Test
    public void canDispatchToEventBus() {

        // Given
        DomainEventBus domainEventBus = Mockito.mock(DomainEventBus.class);
        Set<CommandHandler> handlers = Stream.of(new ProducesThreeEventsCommandHandler()).collect(toSet());
        CommandBus commandBus = new CommandBusMiddlewareChain.Factory().chainOfMiddleware(
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
        CommandBus commandBus = new CommandBusMiddlewareChain.Factory().chainOfMiddleware(
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