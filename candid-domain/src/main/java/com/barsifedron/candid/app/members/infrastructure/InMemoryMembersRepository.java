package com.barsifedron.candid.app.members.infrastructure;

import com.barsifedron.candid.app.members.domain.Member;
import com.barsifedron.candid.app.members.domain.MemberId;
import com.barsifedron.candid.app.members.domain.MembersRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryMembersRepository implements MembersRepository {

    private Map<MemberId, Member> map = new HashMap<>();

    @Override
    public boolean hasMemberWithEmail(String email) {
        return map
                .values()
                .stream()
                .anyMatch(member -> member.hasEmail(email));
    }

    @Override
    public Member get(MemberId memberId) {
        return Optional
                .ofNullable(map.get(memberId))
                .orElseThrow(() -> new RuntimeException("Member not found for member id : " + memberId.id()));
    }

    @Override
    public void add(Member member) {
        map.put(member.memberId(), member);
    }
}
