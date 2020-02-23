package com.barsifedron.candid.cqrs.springboot.sample.readside;

import com.barsifedron.candid.cqrs.query.Query;
import com.barsifedron.candid.cqrs.query.QueryResultToLog;
import com.barsifedron.candid.cqrs.query.QueryToLog;

public class GetSomethingWasDoneCounterValueQuery implements Query<Long>, QueryToLog, QueryResultToLog {
}
