package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.NoResult;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.CommandResultToLog;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.CommandToLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ReturnItemCommand implements Command<NoResult>, CommandToLog, CommandResultToLog {

    // Food for your thoughts:
    // Should we also use the member Id in the command?
    // If someone finds the item on the street, we want to be able to close the loan,
    // even without the member id.

    public String itemId;
    public IF_NO_ACTIVE_LOAN_FOUND ifNoActiveLoanIsFound;

    /**
     * People can find items in the street or someone might not put them in the right spot,
     * or have done a bad job at tracking past items.
     * <p>
     * Not finding and active loan for an item, should not always mean failure when it is returned
     */
    public static enum IF_NO_ACTIVE_LOAN_FOUND {
        FAIL, IGNORE_SILENTELY
    }

}
