package com.barsifedron.candid.cqrs.query.middleware;


import com.barsifedron.candid.cqrs.command.middleware.DetailedLoggingCommandBusMiddleware;
import com.barsifedron.candid.cqrs.query.*;

import java.util.logging.Logger;

public class DetailedLoggingQueryBusMiddleware implements QueryBusMiddleware {

    private final static Logger LOGGER = Logger.getLogger(DetailedLoggingCommandBusMiddleware.class.getName());

    @Override
    public <T> T dispatch(Query<T> query, QueryBus queryBus) {
        logQuery(query);
        T result = queryBus.dispatch(query);
        logQueryResponse(query, result);
        return result;
    }

    private <T> void logQuery(Query<T> query) {
        boolean logQueryDetail = QueryToLog.class.isAssignableFrom(query.getClass());
        if (logQueryDetail) {
            LOGGER.info("\n\nProcessing query :\n" + query.toString());
        }
        if (!logQueryDetail) {
            LOGGER.info("\n\nProcessing  query of type :\n" + query.getClass().getName());
        }
    }

    private <T> void logQueryResponse(Query<T> query, T result) {
        if (result == null) {
            LOGGER.info("Query response was : null");
            return;
        }
        boolean logQueryResult = QueryResultToLog.class.isAssignableFrom(query.getClass());
        if (logQueryResult) {
            LOGGER.info("Query response was : " + result);
        }
        if (!logQueryResult) {
            LOGGER.info("Query response was of type : " + result.getClass().getName());
        }
    }

}
