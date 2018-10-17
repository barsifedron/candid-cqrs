package com.barsifedron.candid.cqrs.domainevent;


/**
 * You should make a clear distinction between events that stay local to your micro-service / bounded-context
 * and the ones that you make available to your whole app.
 * Other devs could listen to events you publish without you knowing about it which would complicate
 * potential refactors. Better limit as much as possible.
 * <p>
 * This class is about local domain events. Not things to share on SQS or kafka.
 */
public interface DomainEvent {
}
