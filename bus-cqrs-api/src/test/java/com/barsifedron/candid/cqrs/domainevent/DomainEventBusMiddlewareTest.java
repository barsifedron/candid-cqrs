package com.barsifedron.candid.cqrs.domainevent;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DomainEventBusMiddlewareTest {

    @Test
    public void canDecorateADomainEventBusOrADomainEventMiddleware() {

        List<String> logs = new ArrayList<>();

        DomainEventBusMiddleware firstMiddleware = (domainEvent, next) -> {
            logs.add("First middleware");
            next.dispatch(domainEvent);
            logs.add("First middleware");
        };

        DomainEventBusMiddleware secondMiddleware = (domainEvent, next) -> {
            logs.add("\tSecond middleware");
            next.dispatch(domainEvent);
            logs.add("\tSecond middleware");

        };

        DomainEventBus baseBus = domainEvent -> {
            logs.add("\t\tDecorated bus execution.");
            new MapDomainEventBus(new NothingToDoDomainEventHandler()).dispatch(domainEvent);
        };

        DomainEventBus domainEventBus = firstMiddleware.decorate(secondMiddleware.decorate(baseBus));
        DomainEventBus secondDomainEventBus = (firstMiddleware.compose(secondMiddleware)).decorate(baseBus);

        domainEventBus.dispatch(new NothingToDoEvent());
        assertEquals(
                "First middleware\n" +
                        "\tSecond middleware\n" +
                        "\t\tDecorated bus execution.\n" +
                        "\tSecond middleware\n" +
                        "First middleware",
                logs.stream().collect(Collectors.joining("\n")));

        logs.clear();

        secondDomainEventBus.dispatch(new NothingToDoEvent());
        assertEquals(
                "First middleware\n" +
                        "\tSecond middleware\n" +
                        "\t\tDecorated bus execution.\n" +
                        "\tSecond middleware\n" +
                        "First middleware",
                logs.stream().collect(Collectors.joining("\n")));
    }

    @Test
    public void shouldProcessDomainEventsWhenRightHandler() {
        Set<NothingToDoDomainEventHandler> handlers = Stream.of(new NothingToDoDomainEventHandler()).collect(toSet());
        DomainEventBus chain = DomainEventBusMiddleware
                .compositeOf(
                        new FirstTestMiddleware(),
                        new SecondTestMiddleware())
                .decorate(new MapDomainEventBus(handlers));
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