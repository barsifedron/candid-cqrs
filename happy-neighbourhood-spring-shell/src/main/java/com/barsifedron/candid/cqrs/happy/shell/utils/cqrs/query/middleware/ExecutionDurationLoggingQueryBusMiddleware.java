package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.query.middleware;

import com.barsifedron.candid.cqrs.query.Query;
import com.barsifedron.candid.cqrs.query.QueryBus;
import com.barsifedron.candid.cqrs.query.QueryBusMiddleware;

import java.util.logging.Logger;

/**
 * For the sake of providing an example of a decorating function:
 * A decorator calculating the execution time of our queries handling.
 * <p>
 * Now it is up to you to create others! Some possible things here : Validating your query dtos,
 * wrapping the query execution within a database transaction, etc...
 * The sky is your limit
 */
public class ExecutionDurationLoggingQueryBusMiddleware implements QueryBusMiddleware {

    private final static Logger LOGGER = Logger.getLogger(ExecutionDurationLoggingQueryBusMiddleware.class.getName());

    public ExecutionDurationLoggingQueryBusMiddleware() {
    }

    public <T> T dispatch(Query<T> query, QueryBus next) {
        LOGGER.info("Processing simple query of type :" + query.getClass().getName());

        long timeBefore = System.nanoTime();
        T result = next.dispatch(query);
        long timeAfter = System.nanoTime();
        LOGGER.info("" +
                "Done processing query of type" + query.getClass().getName() +
                "\nExecution time was :" + ((timeAfter - timeBefore) / 1000000) + " ms");
        return result;
    }
}