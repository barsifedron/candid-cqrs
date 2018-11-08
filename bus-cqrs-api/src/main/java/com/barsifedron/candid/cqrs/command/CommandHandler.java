package com.barsifedron.candid.cqrs.command;

public interface CommandHandler<V, K extends Command<V>> {

    CommandResponse<V> handle(K command);


    // should we remove the typing ? todo
    Class listenTo();

}
