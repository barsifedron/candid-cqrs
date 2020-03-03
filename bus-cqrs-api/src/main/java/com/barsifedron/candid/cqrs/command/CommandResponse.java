package com.barsifedron.candid.cqrs.command;

import com.barsifedron.candid.cqrs.domainevent.DomainEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Commands should not really return data. At the most, the result field should
 * only return an Id or a status. This class would make for a nice Tuple. But
 * when you have lemons...
 * The events describe facts that happened in your system as the result of your command.
 * Example: if your command was "Update user address", your events could be something like
 * "USER_ADDRESS_UPDATED".
 * Event listeners will react to those to update the READ model or handle side effects
 * that do not really have to do with the core of your domain (which was only to update the user address),
 * for example send an email, add a log somewhere or inform the sales team....
 */
public class CommandResponse<K> {

    public final K result;
    public final List<DomainEvent> domainEvents;

    public CommandResponse(K result) {
        this(result, new ArrayList<>());
    }

    public CommandResponse(K result, DomainEvent... domainEvents) {
        this(result, Stream.of(domainEvents).collect(toList()));
    }

    public CommandResponse(K result, List<DomainEvent> domainEvents) {
        this.result = result;
        this.domainEvents = domainEvents;
    }

    public <T> CommandResponse<T> withResult(T result) {
        return new CommandResponse<>(result, domainEvents);
    }

    /**
     * Does not override the list but add to the existing events.
     */
    public CommandResponse<K> withAddedDomainEvents(DomainEvent... events) {
        return withAddedDomainEvents(Arrays.asList(events));
    }

    public CommandResponse<K> withAddedDomainEvents(List<DomainEvent> events) {
        List<DomainEvent> newDomainEvents = domainEvents.stream().collect(toList());
        events.forEach(newDomainEvents::add);
        return new CommandResponse(result, newDomainEvents);
    }

    public static CommandResponse<NoResult> withoutResults(DomainEvent... events) {
        return new CommandResponse(new NoResult(), events);
    }

    public static CommandResponse<NoResult> empty() {
        return new CommandResponse(new NoResult(), new ArrayList<>());
    }

}