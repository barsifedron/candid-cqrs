package com.barsifedron.candid.cqrs.command;

import  com.barsifedron.candid.cqrs.event.Event;
import java.util.List;

/**
 * Commands should not really return data. At the most, the result field should
 * only return an Id or a status. This class would make for a nice Tuple. But
 * when you have lemons...
 */
public class CommandResponse<K> {
    public final K result;
    public final List<Event> events;

    public CommandResponse(K result, List<Event> events) {
        this.result = result;
        this.events = events;
    }
}