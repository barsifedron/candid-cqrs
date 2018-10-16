package com.barsifedron.candid.cqrs.domainevent;


/**
 * You should differentiate between events that stay local to your micro-service / bounded-context
 * and the ones that you make available to your whole app.
 * Other devs could listen to events you publish without you knowing about it which would complicate
 * potential refactors. Better limit as much as possible.
 */
public interface DomainEvent {
}
