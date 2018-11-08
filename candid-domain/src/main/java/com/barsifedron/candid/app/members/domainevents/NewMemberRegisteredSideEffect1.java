package com.barsifedron.candid.app.members.domainevents;

import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;

/**
 * TODO
 */
public class NewMemberRegisteredSideEffect1 implements DomainEventHandler<NewMemberRegistered> {

    @Override
    public void handle(NewMemberRegistered event) {
        // todo : Schedule the sending of a welcome email to the newly registered member.
        // (the actual sending should happen outside of the transaction)
        // I leave that to you.
    }

    @Override
    public Class<NewMemberRegistered> listenTo() {
        return NewMemberRegistered.class;
    }
}
