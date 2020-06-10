package com.barsifedron.candid.cqrs.happy.utils.cqrs.query;

/**
 * Queries implementing this interface will see their details logged.
 * 
 * Be careful to not use when sensitive data is passed around.
 * 
 * Also be careful if you command contains 10k transaction lines.
 */
public interface QueryToLog {
}
