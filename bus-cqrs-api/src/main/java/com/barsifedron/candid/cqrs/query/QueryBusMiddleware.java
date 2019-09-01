package com.barsifedron.candid.cqrs.query;


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
}
