package com.barsifedron.candid.cqrs.domain;

/**
 * When queries implement this interface, the result of their execution will be
 * logged
 *
 * Be careful to not use when sensitive data is passed around.
 *
 * Also be careful if you response contains 10k transaction lines.
 */
public interface DomainEventToLog {
}
