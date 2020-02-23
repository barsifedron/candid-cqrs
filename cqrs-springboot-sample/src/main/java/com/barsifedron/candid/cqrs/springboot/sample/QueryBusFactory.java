package com.barsifedron.candid.cqrs.springboot.sample;

import com.barsifedron.candid.cqrs.query.QueryBusMiddlewareChain;
import com.barsifedron.candid.cqrs.query.middleware.*;
import com.barsifedron.candid.cqrs.spring.QueryHandlersRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryBusFactory {

    private final QueryHandlersRegistry queryHandlersRegistry;

    @Autowired
    public QueryBusFactory(QueryHandlersRegistry queryHandlersRegistry) {
        this.queryHandlersRegistry = queryHandlersRegistry;
    }

    public QueryBusMiddlewareChain simpleBus() {
        return new QueryBusMiddlewareChain.Factory().chainOfMiddleware(
                new ExceptionLoggingQueryBusMiddleware(),
                new DetailedLoggingQueryBusMiddleware(),
                new ExecutionTimeLoggingQueryMiddleware(),
                new ValidatingQueryBusMiddleware(),
                new QueryBusDispatcher(queryHandlersRegistry.handlers()));
    }
}
