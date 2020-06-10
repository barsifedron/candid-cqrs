package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.query.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Builder
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class GetItemQuery implements Query<GetItemQueryHandler.ItemDto> {

    @NotNull
    public String itemId;

}
