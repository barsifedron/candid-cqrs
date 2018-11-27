package com.barsifedron.candid.app.loan.command;

import com.barsifedron.candid.app.items.domain.Item;
import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.items.domain.ItemsRepository;
import com.barsifedron.candid.app.loan.domainevents.ItemLentToMember;
import com.barsifedron.candid.app.loan.domain.Loan;
import com.barsifedron.candid.app.loan.domain.LoanId;
import com.barsifedron.candid.app.loan.domain.LoanRepository;
import com.barsifedron.candid.app.members.domain.Member;
import com.barsifedron.candid.app.members.domain.MemberId;
import com.barsifedron.candid.app.members.domain.MembersRepository;
import com.barsifedron.candid.cqrs.command.CommandHandler;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.domainevent.DomainEvent;

import javax.inject.Inject;

public class LendItemToMemberCommandHandler implements CommandHandler<LoanId, LendItemToMember> {

    private final LoanRepository loanRepository;
    private final ItemsRepository itemsRepository;
    private final MembersRepository membersRepository;

    @Inject
    public LendItemToMemberCommandHandler(
            LoanRepository loanRepository,
            ItemsRepository itemsRepository,
            MembersRepository membersRepository) {

        this.loanRepository = loanRepository;
        this.itemsRepository = itemsRepository;
        this.membersRepository = membersRepository;
    }

    @Override
    public CommandResponse<LoanId> handle(LendItemToMember command) {

        // Food for your thoughts:
        // What about checking the existence of already existing loans?
        // While it makes sense for the member (a member can only borrow one item at a time)
        // it does not necessarily makes sens for the item. If someone goes to the register with the item in hands
        // and wants to borrow it, the fact that a faulty Loan was not closed in our system should not prevent
        // the new loan to happen. Checks should be in accordance to your business use cases.
        // Usual recommendations about avoiding read queries in the repository still apply.

        // For our needs, raising an alert to the operator should be enough and they could easily
        // manually close any existing loan. (Or all of them in a single action)

        Item item = itemsRepository.get(new ItemId(command.itemId));
        Member member = membersRepository.get(new MemberId(command.memberId));

        Loan loan = new Loan(
                new ItemId(command.itemId),
                new MemberId(command.memberId));

        // Simply add to your collections-like repository.
        // You should never deal with transactions at this level.
        // That is the job of the command bus middleware or, at the really worst, of the underlying repository implementation.
        // So no "update", "persist" etc...
        loanRepository.add(loan);

        // This is a LOCAL domain event, which can be listened to in order to create "side effects".
        // Not something to send to kafka, aws or others micro services.
        //
        // Send a welcome email, create an activity log, update a counter... none of these things should happen in your command handler.
        // Side effects will only ever be triggered through local domain events.
        // Good news : you have nothing to do but to add the domain events to the returned response. The command bus automatically dispatches them to their rightful handlers.
        // Note : we can generate more than one domain event. But also no event at all.
        DomainEvent domainEvent = new ItemLentToMember(
                item.id(),
                item.name(),
                member.memberId(),
                member.firstName(),
                member.familyName());
        return new CommandResponse<>(loan.id(), domainEvent);

    }

    @Override
    public Class<LendItemToMember> listenTo() {
        return LendItemToMember.class;
    }
}
