package com.barsifedron.candid.cqrs.query;


import com.barsifedron.candid.cqrs.query.middleware.ExecutionDurationLoggingQueryBusMiddleware;
import com.barsifedron.candid.cqrs.query.middleware.QueryBusDispatcher;
import com.barsifedron.candid.cqrs.query.middleware.WithFilteringQueryBusMiddleware;

import java.io.Serializable;
import java.util.Set;

public class QueryBusFactory {

    private Set<QueryHandler> queryHandlers;

    /**
     * This should totally be injected by you dependency injection tool. See examples in other modules.
     */
    public QueryBusFactory(Set<QueryHandler> queryHandlers) {
        this.queryHandlers = queryHandlers;
    }

    /**
     * Keep wrapping them with other decorators to add behavior. If it becomes too much, why not look at the middleware way of doing things
     * in the middleware package?
     */
    public QueryBus newSimpleQueryBus() {
        return QueryBusMiddleware.chainManyIntoAQueryBus(
                new ExecutionDurationLoggingQueryBusMiddleware(),
                new QueryBusDispatcher(queryHandlers)
        );
    }

    /**
     * A bus filtering (and only accepting to process) query dtos that also implement the Serializable interface.
     * Not sure why one would want to do that but it makes for an example of wrapping query buses to obtain complex behaviors.
     */
    public QueryBus newSimpleQueryBusWithFiltering() {
        return QueryBusMiddleware.chainManyIntoAQueryBus(
                new ExecutionDurationLoggingQueryBusMiddleware(),
                new WithFilteringQueryBusMiddleware(Serializable.class),
                new QueryBusDispatcher(queryHandlers)
        );
    }

}