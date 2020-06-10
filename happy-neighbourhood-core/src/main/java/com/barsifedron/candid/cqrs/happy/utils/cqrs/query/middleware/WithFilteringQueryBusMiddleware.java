package com.barsifedron.candid.cqrs.happy.utils.cqrs.query.middleware;

import com.barsifedron.candid.cqrs.query.Query;
import com.barsifedron.candid.cqrs.query.QueryBus;
import com.barsifedron.candid.cqrs.query.QueryBusMiddleware;

/**
 * For the sake of providing an example of a decorating function:
 * A decorator filtering queries, and only processing them if they implement a certain interface.
 */
public class WithFilteringQueryBusMiddleware<V> implements QueryBusMiddleware {

    private final Class<? extends V> filteringClass;

    public WithFilteringQueryBusMiddleware(Class<? extends V> filteringClass) {
        this.filteringClass = filteringClass;
    }

    public <T> T dispatch(Query<T> query, QueryBus next) {
        if (query.getClass().isInstance(filteringClass)) {
            return next.dispatch(query);
        }
        return null;
    }
}