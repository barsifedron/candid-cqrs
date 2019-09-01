package com.barsifedron.candid.cqrs.query.middleware;


import com.barsifedron.candid.cqrs.query.Query;
import com.barsifedron.candid.cqrs.query.QueryBus;
import com.barsifedron.candid.cqrs.query.QueryBusMiddleware;
import com.barsifedron.candid.cqrs.query.QueryHandler;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * This is in charge of dispatching the query to the right Query Handler.
 * This will only allow for one handler per query type. As it should be.
 */
public class QueryBusDispatcher implements QueryBusMiddleware {

    private final Map<Class<Query>, Supplier<QueryHandler>> handlers;

    public QueryBusDispatcher(QueryHandler... queryHandlers) {
        this(Stream.of(queryHandlers).collect(Collectors.toSet()));
    }

    public QueryBusDispatcher(Set<? extends QueryHandler> queryHandlers) {
        this(queryHandlers.stream().collect(toMap(
                handler -> handler.listenTo(),
                handler -> () -> handler))
        );
    }

    /**
     * Your set of handlers should be given to you by your dependency injection tool.
     * See examples in others modules.
     */
    public QueryBusDispatcher(Map<Class<Query>, Supplier<QueryHandler>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public <T> T dispatch(Query<T> query, QueryBus unreachableQueryBus) {
        QueryHandler queryHandler = Optional
                .ofNullable(handlers.get(query.getClass()))
                .map(handlerSupplier -> handlerSupplier.get())
                .orElseThrow(() -> new QueryHandlerNotFoundException(query.getClass()));
        return (T) queryHandler.handle(query);
    }

    public static class QueryHandlerNotFoundException extends RuntimeException {
        public QueryHandlerNotFoundException(Class<? extends Query> aClass) {
            super("Could not find Query Handler for auery of type " + aClass.getName());
        }
    }
}