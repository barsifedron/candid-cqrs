package com.barsifedron.candid.cqrs.query.middleware;

import com.barsifedron.candid.cqrs.command.middleware.DetailedLoggingCommandBusMiddleware;
import com.barsifedron.candid.cqrs.query.Query;
import com.barsifedron.candid.cqrs.query.QueryBus;
import com.barsifedron.candid.cqrs.query.QueryBusMiddleware;

import java.util.logging.Logger;

public class ExecutionTimeLoggingQueryMiddleware implements QueryBusMiddleware {

    private final static Logger LOGGER = Logger.getLogger(DetailedLoggingCommandBusMiddleware.class.getName());

    public <T> T dispatch(Query<T> query, QueryBus next) {

        long timeBefore = System.nanoTime();

        T result = next.dispatch(query);

        long timeAfter = System.nanoTime();
        LOGGER.info("" +
                "Done processing query of type : " + query.getClass().getName() +
                "\nExecution time was :" + ((timeAfter - timeBefore) / 1000000) + " ms");

        return result;
    }

}
