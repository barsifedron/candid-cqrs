package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.query.middleware;

import com.barsifedron.candid.cqrs.query.Query;
import com.barsifedron.candid.cqrs.query.QueryBus;
import com.barsifedron.candid.cqrs.query.QueryBusMiddleware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class CacheQueryBusMiddleware implements QueryBusMiddleware {

    @Autowired
    public CacheQueryBusMiddleware() {
    }

    @Cacheable(value = "queries", key = "#query")
    @Override
    public <T> T dispatch(Query<T> query, QueryBus next) {
        return next.dispatch(query);
    }

    /**
     * For some reason, the above @Cacheable annotation is not taken into account by Spring.
     * This might have to do with a clash between spring proxy and the middleware interface.
     * Till I figure it out, calling this method from the query bus does the trick.
     */
    @Cacheable(value = "queries", key = "#query")
    public <T> T runInCache(Query<T> query, QueryBus next) {
        return this.dispatch(query, next);
    }

}
