package com.barsifedron.candid.cqrs.query;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toMap;


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

    <T> T dispatch(Query<T> command, QueryBusMiddlewareChain next);

    /**
     * This is in charge of dispatching the query to the right Query Handler.
     * This will only allow for one handler per query type. As it should be.
     */
    class Dispatcher implements QueryBusMiddleware {

        private final Map<Class, QueryHandler> handlers;

        /**
         * Your set of handlers should be given to you by your depedency injection tool.
         * See examples in others modules.
         */
        public Dispatcher(Set<? extends QueryHandler> queryHandlers) {
            this(queryHandlers
                    .stream()
                    .collect(toMap(
                            handler -> handler.listenTo(),
                            handler -> handler)));
        }

        public Dispatcher(Map<Class, QueryHandler> handlers) {
            this.handlers = handlers;
        }

        @Override
        public <T> T dispatch(Query<T> query, QueryBusMiddlewareChain notUsed) {
            QueryHandler queryHandler = Optional
                    .ofNullable(query)
                    .map(Query::getClass)
                    .map(handlers::get)
                    .orElseThrow(() -> new QueryHandlerNotFoundException(query.getClass()));
            return (T) queryHandler.handle(query);
        }
    }

    class QueryHandlerNotFoundException extends RuntimeException {
        public QueryHandlerNotFoundException(Class<? extends Query> aClass) {
            super("Could not find Query Handler for auery of type " + aClass.getName());
        }
    }
}
