package com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.command;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class TransactionalCommandBusMiddleware implements CommandBusMiddleware {

    @Override
    @Transactional
    public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus next) {
        return next.dispatch(command);
    }

    /**
     * For some reason, the above @Transactional method is not taken into account by Spring.
     * This might have to do with a clash between spring proxy and the middleware interface.
     * Till I figure it out, calling this method instead from the command bus does the work
     */
    @Transactional
    public <T> CommandResponse<T> runInTransaction(Command<T> command, CommandBus next) {
        return next.dispatch(command);
    }

}
