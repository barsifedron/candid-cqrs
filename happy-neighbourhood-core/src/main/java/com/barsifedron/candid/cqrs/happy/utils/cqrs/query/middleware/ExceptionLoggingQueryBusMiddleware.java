package com.barsifedron.candid.cqrs.happy.utils.cqrs.query.middleware;


import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.middleware.DetailedLoggingCommandBusMiddleware;
import com.barsifedron.candid.cqrs.query.Query;
import com.barsifedron.candid.cqrs.query.QueryBus;
import com.barsifedron.candid.cqrs.query.QueryBusMiddleware;

import java.util.logging.Logger;

public class ExceptionLoggingQueryBusMiddleware implements QueryBusMiddleware {

    private final static Logger LOGGER = Logger.getLogger(DetailedLoggingCommandBusMiddleware.class.getName());

    @Override
    public <T> T dispatch(Query<T> query, QueryBus bus) {
        try {
            return bus.dispatch(query);
        } catch (Throwable th) {

            LOGGER.severe(""
                    + "An error was raised while executing query of type "
                    + query.getClass().getName()
                    + ". Error was of type "
                    + th.getClass().getName()
                    + ", with error message :\n"
                    + th.getMessage()
            );
            throw th;
        }
    }
}
