package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.query;

import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.query.middleware.ExecutionDurationLoggingQueryBusMiddleware;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.query.ValidatingQueryBusMiddleware;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.query.middleware.DetailedLoggingQueryBusMiddleware;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.query.middleware.ExceptionLoggingQueryBusMiddleware;
import com.barsifedron.candid.cqrs.query.QueryBus;
import com.barsifedron.candid.cqrs.query.QueryBusMiddleware;
import com.barsifedron.candid.cqrs.query.middleware.QueryBusDispatcher;
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
                "com.barsifedron.candid.cqrs.springboot.app",
                "com.barsifedron.candid.cqrs.happy.query");
    }

    public QueryBus simpleBus() {

        return QueryBusMiddleware.chainManyIntoAQueryBus(
                new ExceptionLoggingQueryBusMiddleware(),
                new DetailedLoggingQueryBusMiddleware(),
                new ExecutionDurationLoggingQueryBusMiddleware(),
                new ValidatingQueryBusMiddleware(),
                new QueryBusDispatcher(queryHandlersRegistry.handlers()));
    }
}
