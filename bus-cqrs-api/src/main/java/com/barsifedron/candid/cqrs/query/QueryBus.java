package com.barsifedron.candid.cqrs.query;


/**
 * A Query Bus aka a dispatcher.
 */
public interface QueryBus {

    <T> T dispatch(Query<T> query);

}
