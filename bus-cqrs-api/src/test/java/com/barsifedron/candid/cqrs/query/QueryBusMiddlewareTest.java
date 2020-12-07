package com.barsifedron.candid.cqrs.query;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QueryBusMiddlewareTest {

    @Test
    public void canDecorateAQueryBusOrAQueryMiddleware() {

        List<String> logs = new ArrayList<>();

        QueryBusMiddleware firstMiddleware = new QueryBusMiddleware() {
            @Override
            public <T> T dispatch(Query<T> query, QueryBus next) {
                logs.add("First middleware");
                T queryResponse = next.dispatch(query);
                logs.add("First middleware");
                return queryResponse;
            }
        };

        QueryBusMiddleware secondMiddleware = new QueryBusMiddleware() {
            @Override
            public <T> T dispatch(Query<T> query, QueryBus next) {
                logs.add("\tSecond middleware");
                T queryResponse = next.dispatch(query);
                logs.add("\tSecond middleware");
                return queryResponse;
            }
        };

        QueryBus baseBus = new QueryBus() {
            @Override
            public <T> T dispatch(Query<T> query) {
                logs.add("\t\tDecorated bus execution.");
                return new MapQueryBus(new ReturnTwoQueryHandler()).dispatch(query);
            }
        };

        QueryBus queryBus = firstMiddleware.decorate(secondMiddleware.decorate(baseBus));
        QueryBus secondQueryBus = (firstMiddleware.compose(secondMiddleware)).decorate(baseBus);

        queryBus.dispatch(new ReturnTwoQuery());
        assertEquals(
                "First middleware\n" +
                        "\tSecond middleware\n" +
                        "\t\tDecorated bus execution.\n" +
                        "\tSecond middleware\n" +
                        "First middleware",
                logs.stream().collect(Collectors.joining("\n")));

        logs.clear();

        secondQueryBus.dispatch(new ReturnTwoQuery());
        assertEquals(
                "First middleware\n" +
                        "\tSecond middleware\n" +
                        "\t\tDecorated bus execution.\n" +
                        "\tSecond middleware\n" +
                        "First middleware",
                logs.stream().collect(Collectors.joining("\n")));
    }

    static class FirstTestMiddleware implements QueryBusMiddleware {

        private final static Logger LOGGER = Logger.getLogger(FirstTestMiddleware.class.getName());

        @Override
        public <T> T dispatch(Query<T> query, QueryBus next) {
            LOGGER.info("FirstTestMiddleware : dispatching");
            T response = next.dispatch(query);
            LOGGER.info("FirstTestMiddleware : dispatched");
            return response;
        }
    }

    static class SecondTestMiddleware implements QueryBusMiddleware {

        private final static Logger LOGGER = Logger.getLogger(SecondTestMiddleware.class.getName());

        @Override
        public <T> T dispatch(Query<T> query, QueryBus next) {
            LOGGER.info("SecondTestMiddleware : dispatching");
            T response = next.dispatch(query);
            LOGGER.info("SecondTestMiddleware : dispatched");
            return response;
        }
    }

    static class ReturnTwoQuery implements Query<Integer> {
    }

    static class ReturnTwoQueryHandler implements QueryHandler<Integer, ReturnTwoQuery> {

        @Override
        public Integer handle(ReturnTwoQuery query) {
            return 2;
        }

        @Override
        public Class listenTo() {
            return ReturnTwoQuery.class;
        }
    }

}