package com.barsifedron.candid.cqrs.query.middleware;

import com.barsifedron.candid.cqrs.query.Query;
import com.barsifedron.candid.cqrs.query.QueryBusMiddleware;
import com.barsifedron.candid.cqrs.query.QueryBusMiddlewareChain;
import com.barsifedron.candid.cqrs.query.QueryHandler;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

/**
 * This is in charge of dispatching the query to the right Query Handler.
 * This will only allow for one handler per query type. As it should be.
 */
public class QueryBusDispatcher implements QueryBusMiddleware {

    private final Map<Class, QueryHandler> handlers;

    /**
     * Your set of handlers should be given to you by your depedency injection tool.
     * See examples in others modules.
     */
    public QueryBusDispatcher(Set<? extends QueryHandler> queryHandlers) {
        handlers = queryHandlers
                .stream()
                .collect(toMap(
                        handler -> handler.listenTo(),
                        handler -> handler));
    }

    @Override
    public <T> T dispatch(Query<T> query, QueryBusMiddlewareChain notUsed) {
        QueryHandler queryHandler = Optional
                .ofNullable(query)
                .map(Query::getClass)
                .map(handlers::get)
                .orElseThrow(() -> new QueryHandlerNotFoundException(query.getClass()));
        return (T) queryHandler.handle(query);
    }

    public static class QueryHandlerNotFoundException extends RuntimeException {
        public QueryHandlerNotFoundException(Class<? extends Query> aClass) {
            super("Could not find Query Handler for auery of type " + aClass.getName());
        }
    }
}