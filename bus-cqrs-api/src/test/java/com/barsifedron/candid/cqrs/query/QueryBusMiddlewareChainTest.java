package com.barsifedron.candid.cqrs.query;


import com.barsifedron.candid.cqrs.query.middleware.QueryBusDispatcher;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QueryBusMiddlewareChainTest {

    public QueryBusMiddlewareChainTest() {
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailToConstructEmptyMiddlewareChain() {
        new QueryBusMiddlewareChain.Factory().chainOfMiddleware(new ArrayList<>());
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailINoDispatcherMiddleware() {
        new QueryBusMiddlewareChain.Factory().chainOfMiddleware(new FirstTestMiddleware());
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailIfLastMiddlewareInChainIsNotTheDispatcher() {
        new QueryBusMiddlewareChain.Factory().chainOfMiddleware(
                new FirstTestMiddleware(),
                new QueryBusDispatcher(new HashSet<>()),
                new SecondTestMiddleware()
        );
    }


    @Test(expected = RuntimeException.class)
    public void shouldFailToBuildAChainOfMiddlewareIfOneIsNull() {
        new QueryBusMiddlewareChain.Factory().chainOfMiddleware(
                new FirstTestMiddleware(),
                new SecondTestMiddleware(),
                null,
                new QueryBusDispatcher(new HashSet<>()));
    }

    @Test
    public void shouldBuildAChainOfMiddleware() {
        QueryBusMiddlewareChain chain = new QueryBusMiddlewareChain.Factory().chainOfMiddleware(
                new FirstTestMiddleware(),
                new QueryBusDispatcher(new HashSet<>()));
        assertTrue(chain.containsInstanceOf(FirstTestMiddleware.class));
        assertTrue(chain.containsInstanceOf(QueryBusDispatcher.class));
        assertFalse(chain.containsInstanceOf(SecondTestMiddleware.class));
    }

    @Test(expected = QueryBusDispatcher.QueryHandlerNotFoundException.class)
    public void shouldFailToProcessQuerysWhenNoRightHandler() {
        QueryBusMiddlewareChain chain = new QueryBusMiddlewareChain.Factory().chainOfMiddleware(
                new FirstTestMiddleware(),
                new SecondTestMiddleware(),
                new QueryBusDispatcher(new HashSet<>()));
        chain.dispatch(new ReturnTwoQuery());
    }

    @Test
    public void shouldProcessQuerysWhenRightHandler() {
        QueryBusMiddlewareChain chain = new QueryBusMiddlewareChain.Factory().chainOfMiddleware(
                new FirstTestMiddleware(),
                new SecondTestMiddleware(),
                new QueryBusDispatcher(new ReturnTwoQueryHandler()));
        Integer response = chain.dispatch(new ReturnTwoQuery());
        assertEquals(Integer.valueOf(2), response);
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