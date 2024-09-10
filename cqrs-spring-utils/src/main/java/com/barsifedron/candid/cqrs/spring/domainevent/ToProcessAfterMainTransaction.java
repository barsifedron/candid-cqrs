package com.barsifedron.candid.cqrs.spring.domainevent;

/**
 * A marker interface.
 *
 * If the bus allows it, domain event handlers marked with it will be executed AFTER
 * the main database transaction
 */
public interface ToProcessAfterMainTransaction {
}
