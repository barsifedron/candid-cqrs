package com.barsifedron.candid.cqrs.query;


import com.barsifedron.candid.cqrs.query.middleware.QueryBusDispatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The middleware intercept your query on its way to or back from the query handlers.
 * Think of this as a chain of decorators, each one adding its own behaviour to the process.
 * <p>
 * This is an extremely powerful way to add common behavior to all your query processing.
 * Simple examples of middleware:
 * A middleware opening and closing transactions around your query handling.
 * A middleware logging the execution time taken to process your query etc...
 * <p>
 * To help you understand, a few examples are provided in the bus-cqrs-example project
 * <p>
 * See an alternative way of doing this in the `bus-cqs` module.
 */
public interface QueryBusMiddleware {

    <T> T dispatch(Query<T> command, QueryBus next);

    /**
     * Decorates a command bus with this middleware.
     */
    default QueryBus decorate(QueryBus bus) {
        QueryBusMiddleware thisMiddleware = this;
        QueryBus decoratedQueryBus = new QueryBus() {
            @Override
            public <T> T dispatch(Query<T> command) {
                return thisMiddleware.dispatch(command, bus);
            }
        };
        return decoratedQueryBus;
    }

    /**
     * Decorates an existing middleware with this middleware.
     */
    default QueryBusMiddleware decorate(QueryBusMiddleware middleware) {
        QueryBusMiddleware thisMiddleware = this;
        QueryBusMiddleware decoratedQueryBusMiddleware = new QueryBusMiddleware() {
            @Override
            public <T> T dispatch(Query<T> command, QueryBus next) {
                return thisMiddleware.dispatch(command, middleware.decorate(next));
            }
        };
        return decoratedQueryBusMiddleware;
    }

    static QueryBus chainManyIntoAQueryBus(QueryBusMiddleware... middlewares) {
        return Chain.manyIntoAQueryBus(Arrays.asList(middlewares));
    }

    /**
     * Creates a query bus from a list of middleware, wrapping them recursively into each others.
     * The "last" middleware called must ALWAYS be the dispatcher and, contrarily to the others,
     * will not forward the command but finally handle it to the command handlers.
     * Hence the last Chain built with "null".
     */
    class Chain {

        static QueryBus manyIntoAQueryBus(List<QueryBusMiddleware> middlewares) {
            validate(middlewares);
            if (middlewares.size() == 1) {
                return middlewares.get(0).decorate((QueryBus) null);
            }
            return middlewares.get(0).decorate(manyIntoAQueryBus(middlewares.subList(1, middlewares.size())));
        }

        private static void validate(List<QueryBusMiddleware> middlewares) {
            if (middlewares == null) {
                throw new RuntimeException("Can not create a middleware chain from a null list of middlewares");
            }
            if (middlewares.isEmpty()) {
                throw new RuntimeException("Can not operate on an empty list of middlewares");
            }
            if (middlewares.stream().anyMatch(Objects::isNull)) {
                throw new RuntimeException("Can not accept a null middleware in the lists of middlewares");
            }
            QueryBusMiddleware lastMiddlewareInChain = middlewares.get(middlewares.size() - 1);
            if (!lastMiddlewareInChain.getClass().isAssignableFrom(QueryBusDispatcher.class)) {
                throw new RuntimeException(
                        "The last middleware of the chain must always be the one dispatching to handlers.");
            }
        }
    }


}
