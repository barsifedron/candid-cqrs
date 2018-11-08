package com.barsifedron.candid.app.loan.infrastructure;

import com.barsifedron.candid.app.items.domain.ItemId;
import com.barsifedron.candid.app.loan.domain.LoanRepository;
import com.barsifedron.candid.app.loan.domain.Loan;
import com.barsifedron.candid.app.loan.domain.LoanId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryLoanRepository implements LoanRepository {

    private final Map<LoanId, Loan> map;

    public InMemoryLoanRepository() {
        this(new HashMap<>());
    }

    public InMemoryLoanRepository(Map<LoanId, Loan> map) {
        this.map = map;
    }


    @Override
    public Loan get(LoanId id) {
        return Optional
                .ofNullable(map.get(id))
                .orElseThrow(() -> new RuntimeException("Item not found id : " + id));
    }

    @Override
    public Loan getActiveLoan(ItemId itemId) {
        return map
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().hasItemId(itemId))
                .filter(entry -> entry.getValue().hasStatus(Loan.STATUS.BORROWED))
                .findFirst()
                .map(entry -> entry.getValue())
                .get();
    }

    @Override
    public void add(Loan loan) {
        map.put(loan.id(), loan);
    }
}
