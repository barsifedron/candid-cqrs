package com.barsifedron.candid.app.loan.command;

import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.loan.domain.LoanRepository;
import com.barsifedron.candid.app.loan.domainevents.ItemReturned;
import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.command.NoResult;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;

import javax.inject.Inject;

public class ReturnItemCommandHandler implements CommandHandler<NoResult, ReturnItem> {

    private LoanRepository loanRepository;

    @Inject
    public ReturnItemCommandHandler(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public CommandResponse<NoResult> handle(ReturnItem command) {

        // Food for your thoughts:
        // Should we also use the member Id in the command?
        // If someone finds the item on the street, we want to be able to close the loan,
        // even without the member id.
        // DTOs should be in accordance to your business use cases.
        loanRepository.getActiveLoan(new ItemId(command.itemId)).markReturned();

        // This is a LOCAL domain event, which can be listened to in order to create "side effects".
        // Not something to send to kafka, aws or others micro services.
        //
        // Send a welcome email, create an activity log, update a counter... none of these things should happen in your command handler.
        // Side effects will only ever be triggered through local domain events.
        // Good news : you have nothing to do but to add the domain events to the returned response. The command bus automatically dispatches them to their rightful handlers.
        // Note : we can generate more than one domain event. But also no event at all.
        DomainEvent itemReturned = new ItemReturned();
        return CommandResponse.empty().withAddedDomainEvents(itemReturned);
    }

    @Override
    public Class<ReturnItem> listenTo() {
        return ReturnItem.class;
    }
}
