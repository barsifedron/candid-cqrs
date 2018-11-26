package com.barsifedron.candid.cqrs.domainevent;

import com.barsifedron.candid.cqrs.domainevent.middleware.DomainEventBusDispatcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;

public class DomainEventBusMiddlewareChainTest {

    public DomainEventBusMiddlewareChainTest() {
    }

    @Test(expected = java.lang.RuntimeException.class)
    public void shouldFailToConstructEmptyMiddlewareChain() {
        new DomainEventBusMiddlewareChain.Factory().chainOfMiddleware(new ArrayList<>());
    }

    @Test(expected = java.lang.RuntimeException.class)
    public void shouldFailINoDispatcherMiddleware() {
        new DomainEventBusMiddlewareChain.Factory().chainOfMiddleware(new FirstTestMiddleware());
    }

    @Test(expected = java.lang.RuntimeException.class)
    public void shouldFailIfLastMiddlewareInChainIsNotTheDispatcher() {
        new DomainEventBusMiddlewareChain.Factory().chainOfMiddleware(
                new FirstTestMiddleware(),
                new DomainEventBusDispatcher(new HashSet<>()),
                new SecondTestMiddleware()
        );
    }


    @Test(expected = java.lang.RuntimeException.class)
    public void shouldFailToBuildAChainOfMiddlewareIfOneIsNull() {
        new DomainEventBusMiddlewareChain.Factory().chainOfMiddleware(
                new FirstTestMiddleware(),
                new SecondTestMiddleware(),
                null,
                new DomainEventBusDispatcher(new HashSet<>()));
    }

    @Test
    public void shouldBuildAChainOfMiddleware() {
        DomainEventBusMiddlewareChain chain = new DomainEventBusMiddlewareChain.Factory().chainOfMiddleware(
                new FirstTestMiddleware(),
                new DomainEventBusDispatcher(new HashSet<>()));
        assertTrue(chain.containsInstanceOf(FirstTestMiddleware.class));
        assertTrue(chain.containsInstanceOf(DomainEventBusDispatcher.class));
        assertFalse(chain.containsInstanceOf(SecondTestMiddleware.class));
    }


    @Test
    public void shouldProcessDomainEventsWhenRightHandler() {
        Set<NothingToDoDomainEventHandler> handlers = Stream.of(new NothingToDoDomainEventHandler()).collect(toSet());
        DomainEventBusMiddlewareChain chain = new DomainEventBusMiddlewareChain.Factory().chainOfMiddleware(
                new FirstTestMiddleware(),
                new SecondTestMiddleware(),
                new DomainEventBusDispatcher(handlers));
        chain.dispatch(new NothingToDoEvent());
    }


    static class FirstTestMiddleware implements DomainEventBusMiddleware {

        private final static Logger LOGGER = Logger.getLogger(FirstTestMiddleware.class.getName());

        @Override
        public void dispatch(DomainEvent domainEvent, DomainEventBus next) {
            LOGGER.info("FirstTestMiddleware : dispatching");
            next.dispatch(domainEvent);
            LOGGER.info("FirstTestMiddleware : dispatched");
        }
    }

    static class SecondTestMiddleware implements DomainEventBusMiddleware {

        private final static Logger LOGGER = Logger.getLogger(SecondTestMiddleware.class.getName());

        @Override
        public void dispatch(DomainEvent event, DomainEventBus next) {
            LOGGER.info("SecondTestMiddleware : dispatching");
            next.dispatch(event);
            LOGGER.info("SecondTestMiddleware : dispatched");
        }
    }

    static class NothingToDoEvent implements DomainEvent {
    }

    static class NothingToDoDomainEventHandler implements DomainEventHandler<NothingToDoEvent> {

        private final static Logger LOGGER = Logger.getLogger(NothingToDoDomainEventHandler.class.getName());

        @Override
        public void handle(NothingToDoEvent query) {
            LOGGER.info("I do nothing.");
        }

        @Override
        public Class listenTo() {
            return NothingToDoEvent.class;
        }
    }
}