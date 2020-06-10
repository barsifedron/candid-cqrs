package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.query.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Builder
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class GetMemberQuery implements Query<GetMemberQueryHandler.MemberDto> {

    @NotEmpty
    public String memberId;
}
