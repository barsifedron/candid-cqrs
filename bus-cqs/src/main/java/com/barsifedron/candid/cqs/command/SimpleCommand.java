package com.barsifedron.candid.cqs.command;


/**
 * A really simple CQRS / CQS Command side. No projections, no handling of
 * domain events (which can still be produced by your services/handlers if you
 * want to), no event bus, no side effects, no event sourcing. Just a simple and
 * convenient split between read and writes.
 *
 * Inspired by the first part of this great presentation:
 * https://speakerdeck.com/lilobase/cqrs-fonctionnel-event-sourcing-and-domain-driven-design-breizhcamp-2017
 *
 * @param <T>
 */
public interface SimpleCommand<T> {



}
