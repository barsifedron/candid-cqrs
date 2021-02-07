package com.barsifedron.candid.cqrs.domainevent;

import java.util.List;
import java.util.stream.Stream;

/**
 * A really simple  interceptor interface.
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
public interface DomainEventBusMiddleware {

    void dispatch(DomainEvent event, DomainEventBus next);

    /**
     * Passes to the next in line without doing anything.
     * Logically useless but helpful for some wiring operations.
     */
    static DomainEventBusMiddleware neutral() {
        return (event, bus) -> bus.dispatch(event);
    }

    /**
     * Decorates a bus with this middleware.
     * An incoming message will go through THIS middleware, which can choose to intercept it, before being passed down to the bus.
     */
    default DomainEventBus decorate(DomainEventBus bus) {
        DomainEventBusMiddleware thisMiddleware = this;
        DomainEventBus decoratedDomainEventBus = (event) -> thisMiddleware.dispatch(event, bus);
        return decoratedDomainEventBus;
    }

    /**
     * Decorates an existing middleware with this middleware.
     * An incoming message will go through THIS middleware, which can choose to intercept it,
     * before being passed down to the next one.
     * <p>
     * We can always make a composite middleware from two middleware....
     */
    default DomainEventBusMiddleware compose(DomainEventBusMiddleware middleware) {
        DomainEventBusMiddleware thisMiddleware = this;
        DomainEventBusMiddleware decoratedDomainEventBusMiddleware = (event, next) -> {
            thisMiddleware.dispatch(event, middleware.decorate(next));
        };
        return decoratedDomainEventBusMiddleware;
    }

    /**
     * ... And when you can compose two you can compose many. Functionally...
     */
    static DomainEventBusMiddleware compositeOf(DomainEventBusMiddleware... middlewares) {
        return Stream.of(middlewares).reduce(DomainEventBusMiddleware.neutral(), (m1, m2) -> m1.compose(m2));
    }

    /**
     * ... or recursively. Whatever you like most.
     */
    static DomainEventBusMiddleware compositeOf(List<DomainEventBusMiddleware> middlewares) {
        if (middlewares.isEmpty()){
            return DomainEventBusMiddleware.neutral();
        }
        if (middlewares.size() == 1) {
            return middlewares.get(0);
        }
        return middlewares.get(0).compose(compositeOf(middlewares.subList(1, middlewares.size())));
    }

}

