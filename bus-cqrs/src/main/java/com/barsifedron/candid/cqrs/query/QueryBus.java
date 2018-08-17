package com.barsifedron.candid.cqrs.query;


import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * A Query Bus aka a dispatcher.
 */
public interface QueryBus {

    <T> T dispatch(Query<T> query);

    class Dispatcher implements QueryBus {

        private final Map<Class, QueryHandler> handlers;
        private final static Logger LOGGER = Logger.getLogger(Dispatcher.class.getName());

        public Dispatcher(Set<? extends QueryHandler> queryHandlers) {
            this(queryHandlers.stream().collect(
                    toMap(
                            handler -> handler.listenTo(),
                            handler -> handler)));
        }

        public Dispatcher(Map<Class, QueryHandler> handlers) {
            this.handlers = handlers;
        }

        @Override
        public <T> T dispatch(Query<T> query) {
            QueryHandler queryHandler = Optional
                    .ofNullable(query)
                    .map(q -> q.getClass())
                    .map(handlers::get)
                    .orElseThrow(() -> {
                        LOGGER.info("Could not find handler for query  of type :" + query.getClass().getName());
                        return new QueryHandlerNotFoundException(query.getClass());
                    });
            return (T) queryHandler.handle(query);
        }

        private class QueryHandlerNotFoundException extends RuntimeException {
            public QueryHandlerNotFoundException(Class<? extends Query> aClass) {
                super("Could not find Query Handler for auery of type " + aClass.getName());
            }
        }

    }

    /**
     * For the sake of providing an example of a decorating function:
     * A decorator calculating the execution time of our queries handling.
     * <p>
     * Now it is up to you to create others! Some possible things here : Validating your query dtos,
     * wrapping the query execution within a database transaction, etc...
     * The sky is your limit
     */
    class WithExecutionTime implements QueryBus {

        private final QueryBus next;
        private final static Logger LOGGER = Logger.getLogger(WithExecutionTime.class.getName());

        public WithExecutionTime(QueryBus next) {
            this.next = next;
        }

        public <T> T dispatch(Query<T> query) {
            LOGGER.info("Processing simple query of type :" + query.getClass().getName());

            long timeBefore = System.nanoTime();
            T result = next.dispatch(query);
            long timeAfter = System.nanoTime();
            LOGGER.info("" +
                    "Done processing simple query of type" + query.getClass().getName() +
                    "\nExecution time was :" + ((timeAfter - timeBefore) / 1000000) + " ms");
            return result;
        }
    }

    /**
     * For the sake of providing an example of a decorating function:
     * A decorator filtering queries, and only processing them if they implement a certain interface.
     */
    class WithFiltering<V> implements QueryBus {

        private final Class<? extends V> filteringClass;
        private final QueryBus next;

        public WithFiltering(Class<? extends V> filteringClass, QueryBus next) {
            this.filteringClass = filteringClass;
            this.next = next;
        }

        public <T> T dispatch(Query<T> query) {
            if (query.getClass().isInstance(filteringClass)) {
                return next.dispatch(query);
            }
            return null;
        }
    }


    class Factory {

        private Set<QueryHandler> queryHandlers;

        /**
         * This should totally be injected by you dependency injection tool. See examples in other modules.
         */
        public Factory(Set<QueryHandler> queryHandlers) {
            this.queryHandlers = queryHandlers;
        }

        /**
         * Keep wrapping them with other decorators to add behavior. If it becomes too much, why not look at the middleware way of doing things
         * in the middleware package?
         */
        public QueryBus simpleQueryBus() {
            return new WithExecutionTime(new Dispatcher(queryHandlers));
        }

        /**
         * A bus filtering (and only accepting to process) query dtos that also implement the Serializable interface.
         * Not sure why one would want to do that but it makes for an example of wrapping query buses to obtain complex behaviors.
         */
        public QueryBus simpleFilteringQueryBus() {
            return new WithFiltering(
                    Serializable.class,
                    new WithExecutionTime(
                            new Dispatcher(queryHandlers)));
        }

    }
}
