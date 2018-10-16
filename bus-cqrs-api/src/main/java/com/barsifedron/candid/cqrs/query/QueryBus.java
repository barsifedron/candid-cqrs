package com.barsifedron.candid.cqrs.query;


import java.io.Serializable;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

/**
 * A Query Bus aka a dispatcher.
 */
public interface QueryBus {

    <T> T dispatch(Query<T> query);

}
