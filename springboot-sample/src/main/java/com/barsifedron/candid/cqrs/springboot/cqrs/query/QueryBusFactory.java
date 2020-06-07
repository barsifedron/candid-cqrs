package com.barsifedron.candid.cqrs.springboot.cqrs.query;

import com.barsifedron.candid.cqrs.query.QueryBus;
import com.barsifedron.candid.cqrs.query.QueryBusMiddleware;
import com.barsifedron.candid.cqrs.query.middleware.*;
import com.barsifedron.candid.cqrs.spring.QueryHandlersRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class QueryBusFactory {

    private final QueryHandlersRegistry queryHandlersRegistry;

    @Autowired
    public QueryBusFactory(ApplicationContext applicationContext) {
        this.queryHandlersRegistry = new QueryHandlersRegistry(
                applicationContext,
                "com.barsifedron.candid.cqrs.springboot.app");
    }

    public QueryBus simpleBus() {

        return QueryBusMiddleware.chainManyIntoAQueryBus(
                new ExceptionLoggingQueryBusMiddleware(),
                new DetailedLoggingQueryBusMiddleware(),
                new ExecutionTimeLoggingQueryMiddleware(),
                new ValidatingQueryBusMiddleware(),
                new QueryBusDispatcher(queryHandlersRegistry.handlers()));
    }
}
