package com.barsifedron.candid.cqrs.happy.query;

import com.barsifedron.candid.cqrs.happy.query.GetMemberQueryHandler.MemberDto;
import com.barsifedron.candid.cqrs.query.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.List;

@Builder
@EqualsAndHashCode
@AllArgsConstructor
@ToString
public class GetMemberQuery implements Query<Collection<MemberDto>> {

    public String memberId;
}
