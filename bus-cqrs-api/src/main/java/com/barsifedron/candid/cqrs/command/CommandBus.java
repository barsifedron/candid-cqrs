package com.barsifedron.candid.cqrs.command;


/**
 * A really simple command bus. Dispatches a command to its handler.
 * <p>
 * Inspired by the second part of this great presentation:
 * https://speakerdeck.com/lilobase/cqrs-fonctionnel-event-sourcing-and-domain-driven-design-breizhcamp-2017
 * <p>
 * See also : `https://www.slideshare.net/rosstuck/command-bus-to-awesome-town`
 */
public interface CommandBus {

    <T> CommandResponse<T> dispatch(Command<T> command);

}