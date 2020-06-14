package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.happy.utils.cqrs.query.QueryResultToLog;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.query.QueryToLog;
import com.barsifedron.candid.cqrs.query.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.util.List;

@Builder
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class GetItemsQuery implements Query<List<GetItemsQueryHandler.ItemDto>>, QueryToLog, QueryResultToLog {

    public String itemId;

}
