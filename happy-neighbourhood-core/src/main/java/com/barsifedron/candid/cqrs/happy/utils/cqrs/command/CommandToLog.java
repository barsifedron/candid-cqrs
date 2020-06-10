package com.barsifedron.candid.cqrs.happy.utils.cqrs.command;

/**
 * Commands and events implementing this interface will see their details
 * logged.
 * 
 * Be careful to not use when sensitive data is passed around.
 * 
 * Also be careful if you command response contains 10k transaction lines.
 */
public interface CommandToLog {
}
