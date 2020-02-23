package com.barsifedron.candid.cqrs.springboot.sample.readside;

import com.barsifedron.candid.cqrs.query.QueryHandler;
import com.barsifedron.candid.cqrs.springboot.sample.ThingsDoneCounter;
import org.springframework.stereotype.Component;

@Component
public class GetSomethingWasDoneCounterValueQueryHandler implements QueryHandler<Long, GetSomethingWasDoneCounterValueQuery> {

    @Override
    public Long handle(GetSomethingWasDoneCounterValueQuery query) {
        return new ThingsDoneCounter().value();
    }

    @Override
    public Class<GetSomethingWasDoneCounterValueQuery> listenTo() {
        return GetSomethingWasDoneCounterValueQuery.class;
    }
}
