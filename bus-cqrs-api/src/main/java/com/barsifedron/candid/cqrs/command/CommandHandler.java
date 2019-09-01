package com.barsifedron.candid.cqrs.command;

/**
 * Will handle a command and (optionally) return a  result after processing
 */
public interface CommandHandler<V, K extends Command<V>> {

    CommandResponse<V> handle(K command);

    Class<K> listenTo();

}
