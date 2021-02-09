package com.barsifedron.candid.cqrs.command;

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
public interface CommandBusMiddleware {

    <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next);

    /**
     * Passes to the next in line without doing anything.
     * Logically useless but helpful for some wiring operations.
     */
    static CommandBusMiddleware neutral() {
        return new CommandBusMiddleware() {
            @Override
            public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {
                return next.dispatch(command);
            }
        };
    }

    /**
     * Decorates a bus with this middleware.
     * An incoming message will go through THIS middleware, which can choose to intercept it, before being passed down to the bus.
     */
    default CommandBus decorate(CommandBus bus) {
        CommandBusMiddleware thisMiddleware = this;
        CommandBus decoratedCommandBus = new CommandBus() {
            @Override
            public <T> CommandResponse<T> dispatch(Command<T> command) {
                return thisMiddleware.dispatch(command, bus);
            }
        };
        return decoratedCommandBus;
    }

    /**
     * Decorates an existing middleware with this middleware.
     * An incoming message will go through THIS middleware, which can choose to intercept it,
     * before being passed down to the next one.
     * <p>
     * We can always make a composite middleware from two middleware....
     */
    default CommandBusMiddleware compose(CommandBusMiddleware middleware) {
        CommandBusMiddleware thisMiddleware = this;
        CommandBusMiddleware decoratedCommandBusMiddleware = new CommandBusMiddleware() {
            @Override
            public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {
                return thisMiddleware.dispatch(command, middleware.decorate(next));
            }
        };
        return decoratedCommandBusMiddleware;
    }

    /**
     * ... And when you can compose two you can compose many. Functionally...
     */
    static CommandBusMiddleware compositeOf(CommandBusMiddleware... middlewares) {
        return Stream.of(middlewares).reduce(CommandBusMiddleware.neutral(), (m1, m2) -> m1.compose(m2));
    }

    /**
     * ... or recursively. Whatever you like most.
     */
    static CommandBusMiddleware compositeOf(List<CommandBusMiddleware> middlewares) {
        if (middlewares.isEmpty()) {
            return CommandBusMiddleware.neutral();
        }
        if (middlewares.size() == 1) {
            return middlewares.get(0);
        }
        return middlewares.get(0).compose(compositeOf(middlewares.subList(1, middlewares.size())));
    }

}
