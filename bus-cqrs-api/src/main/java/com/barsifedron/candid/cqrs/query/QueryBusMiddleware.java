package com.barsifedron.candid.cqrs.query;

import java.util.List;
import java.util.stream.Stream;

/**
 * A really simple message interceptor interface.
 * We'll use this to create mighty chains of decorators!
 * <p>
 * When it receives a message, it CAN do things before and after sending it to the bus.
 * <p>
 * Typically :
 * <p>
 * 1. Receive message
 * 2. Do some operation if you want (log the message for example)
 * 3. Forward the message to the decorated bus -&gt; The decorated bus processes the message
 * 4. the decorated bus returns the result of the processing (when there is one)
 * 5. The Middleware intercepts the bus result and can do something with it IF you want (log the result for example)
 * 6. the middleware returns the result.
 * <p>
 * <p>
 * This really simple interface allows quite powerful things:
 * <p>
 * 1. You can always make a composite Bus by combining a middleware and a bus.
 * 2. You can always make a composite middleware by composing two middleware. (And if you can with two you can with any number of them)
 * <p>
 * It really is nothing more than a decorator in disguise. And while you can totally code a command bus using decorators,
 * this approach makes for cleaner wiring of your components when one constructs a bus.
 * <p>
 * Examples in the code should make this clear.
 */
public interface QueryBusMiddleware {

    <T> T dispatch(Query<T> query, QueryBus next);

    /**
     * Passes to the next in line without doing anything.
     * Logically useless but helpful for some wiring operations.
     */
    static QueryBusMiddleware neutral() {
        return new QueryBusMiddleware() {
            @Override
            public <T> T dispatch(Query<T> query, QueryBus next) {
                return next.dispatch(query);
            }
        };
    }

    /**
     * Decorates a bus with this middleware.
     */
    default QueryBus decorate(QueryBus bus) {
        QueryBusMiddleware thisMiddleware = this;
        QueryBus decoratedQueryBus = new QueryBus() {
            @Override
            public <T> T dispatch(Query<T> command) {
                return thisMiddleware.dispatch(command, bus);
            }
        };
        return decoratedQueryBus;
    }

    /**
     * Decorates an existing middleware with this middleware.
     * We can always make a composite middleware from two middleware....
     */
    default QueryBusMiddleware compose(QueryBusMiddleware middleware) {
        QueryBusMiddleware thisMiddleware = this;
        QueryBusMiddleware decoratedQueryBusMiddleware = new QueryBusMiddleware() {
            @Override
            public <T> T dispatch(Query<T> query, QueryBus next) {
                return thisMiddleware.dispatch(query, middleware.decorate(next));
            }
        };
        return decoratedQueryBusMiddleware;
    }

    /**
     * ... And when you can compose two you can compose many. Functionally...
     */
    static QueryBusMiddleware compositeOf(QueryBusMiddleware... middlewares) {
        return Stream.of(middlewares).reduce(QueryBusMiddleware.neutral(), (m1, m2) -> m1.compose(m2));
    }

    /**
     * ... or recursively. Whatever you like most.
     */
    static QueryBusMiddleware compositeOf(List<QueryBusMiddleware> middlewares) {
        if (middlewares.isEmpty()) {
            return QueryBusMiddleware.neutral();
        }
        if (middlewares.size() == 1) {
            return middlewares.get(0);
        }
        return middlewares.get(0).compose(compositeOf(middlewares.subList(1, middlewares.size())));
    }

}
