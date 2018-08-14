package com.barsifedron.candid.cqs.command;

public interface
SimpleCommandHandler<V, K extends SimpleCommand<V>> {

    /**
     * Commands should be side effect free. At the most, this should only return an
     * Id or a status
     */
    V handle(K command);

    Class<K> listenTo();
}