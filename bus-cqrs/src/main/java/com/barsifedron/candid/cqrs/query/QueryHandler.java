package com.barsifedron.candid.cqrs.query;

public interface QueryHandler<V, K extends Query> {

    V handle(K query);

    Class<K> listenTo();
}
