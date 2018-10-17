package com.barsifedron.candid.cqrs.command;

import com.barsifedron.candid.cqrs.domainevent.*;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

public class CommandBusMiddlewareTest {

    public CommandBusMiddlewareTest() {
    }

    @Test
    public void canDispatchToEventBus() {

        // Given
        DomainEventBus domainEventBus = Mockito.mock(DomainEventBus.class);
        Set<CommandHandler> handlers = Stream.of(new ProducesThreeEventsCommandHandler()).collect(toSet());

        CommandBusMiddlewareChain chain = new CommandBusMiddlewareChain.Factory().chainOfMiddleware(
                new CommandBusMiddleware.EventBusDispatcherMiddleware(domainEventBus),
                new CommandBusMiddleware.Dispatcher(handlers));
        CommandBus commandBus = chain::dispatch;

        // when
        CommandResponse<Void> response = commandBus.dispatch(new CommandThatProducesThreeEvents());

        // then
        for (DomainEvent domainEvent : response.events) {
            Mockito
                    .verify(domainEventBus, times(1))
                    .dispatch(domainEvent);
        }
        Mockito.verifyNoMoreInteractions(domainEventBus);

    }

    @Test
    public void canDispatchCommandResponsesEventsToRightEventListener() {


        // Given

        // Event bus
        DomainEventHandler firstEventHandler = Mockito.mock(DomainEventHandler.class);
        DomainEventHandler secondEventHandler = Mockito.mock(DomainEventHandler.class);
        DomainEventHandler thirdEventHandler = Mockito.mock(DomainEventHandler.class);

        Mockito.when(firstEventHandler.listenTo()).thenReturn(FirstTestDomainEvent.class);
        Mockito.when(secondEventHandler.listenTo()).thenReturn(SecondTestDomainEvent.class);
        Mockito.when(thirdEventHandler.listenTo()).thenReturn(ThirdTestDomainEvent.class);

        Set<DomainEventHandler> eventHandlers = Stream.of(firstEventHandler, secondEventHandler, thirdEventHandler).collect(toSet());
        DomainEventBus eventBus = event -> new DomainEventBusMiddlewareChain
                .Factory()
                .chainOfMiddleware(new DomainEventBusMiddleware.Dispatcher(eventHandlers))
                .dispatch(event);

        // Command bus
        Set<CommandHandler> handlers = Stream.of(new ProducesThreeEventsCommandHandler()).collect(toSet());
        CommandBusMiddlewareChain chainOfCommandMiddleware = new CommandBusMiddlewareChain.Factory().chainOfMiddleware(
                new CommandBusMiddleware.EventBusDispatcherMiddleware(eventBus),
                new CommandBusMiddleware.Dispatcher(handlers));
        CommandBus commandBus = chainOfCommandMiddleware::dispatch;

        // when
        commandBus.dispatch(new CommandThatProducesThreeEvents());

        // then
        Mockito
                .verify(firstEventHandler, times(1))
                .handle(any(FirstTestDomainEvent.class));
        Mockito
                .verify(secondEventHandler, times(1))
                .handle(any(SecondTestDomainEvent.class));
        Mockito
                .verify(thirdEventHandler, times(1))
                .handle(any(ThirdTestDomainEvent.class));

    }


    static class CommandThatProducesThreeEvents implements Command<Void> {
    }

    static class ProducesThreeEventsCommandHandler implements CommandHandler<Void, CommandThatProducesThreeEvents> {
        @Override
        public CommandResponse<Void> handle(CommandThatProducesThreeEvents command) {
            return new CommandResponse<>(
                    null,
                    new FirstTestDomainEvent(),
                    new SecondTestDomainEvent(),
                    new ThirdTestDomainEvent());
        }

        @Override
        public Class listenTo() {
            return CommandThatProducesThreeEvents.class;
        }
    }


    static class FirstTestDomainEvent implements DomainEvent {
    }

    static class SecondTestDomainEvent implements DomainEvent {
    }

    static class ThirdTestDomainEvent implements DomainEvent {
    }
}