package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.happy.domain.Item;
import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.ItemsRepository;
import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.happy.domain.LoanId;
import com.barsifedron.candid.cqrs.happy.domain.LoanRepository;
import com.barsifedron.candid.cqrs.happy.domain.MembersRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReturnItemCommandHandlerTest {

    @Test
    void shouldFailIfItemDoesNotExist() {

        ItemsRepository.InMemory items = new ItemsRepository.InMemory();
        LoanRepository.InMemory loans = new LoanRepository.InMemory();
        MembersRepository members = new MembersRepository.InMemory();

        items.add(Item
                .builder()
                .id(new ItemId("iDoExist"))
                .build());

        ReturnItemCommandHandler handler = new ReturnItemCommandHandler(items, loans, members);

        assertThrows(
                ReturnItemCommandHandler.UnknownItemException.class,
                () -> handler.handle(ReturnItemCommand
                        .builder()
                        .itemId("iDoNOTExist")
                        .ifNoActiveLoanIsFound(ReturnItemCommand.IF_NO_ACTIVE_LOAN_FOUND.FAIL)
                        .build()));

    }

    @Test
    void shouldFailIfItemIsNotInAnActiveLoan() {

        ItemsRepository.InMemory items = new ItemsRepository.InMemory();
        LoanRepository.InMemory loans = new LoanRepository.InMemory();
        MembersRepository members = new MembersRepository.InMemory();

        items.add(Item
                .builder()
                .id(new ItemId("iDoExist"))
                .build());

        loans.add(Loan
                .builder()
                .id(new LoanId("loan1"))
                .itemId(new ItemId("iDoExist"))
                .status(Loan.STATUS.RETURNED)
                .build());

        ReturnItemCommandHandler handler = new ReturnItemCommandHandler(items, loans, members);

        assertThrows(
                ReturnItemCommandHandler.NoActiveLoanForItemException.class,
                () -> handler.handle(ReturnItemCommand
                        .builder()
                        .itemId("iDoExist")
                        .ifNoActiveLoanIsFound(ReturnItemCommand.IF_NO_ACTIVE_LOAN_FOUND.FAIL)
                        .build()));

    }

    @Test
    void shouldNotFailIfItemIsNotInAnActiveLoan() {

        ItemsRepository.InMemory items = new ItemsRepository.InMemory();
        LoanRepository.InMemory loans = new LoanRepository.InMemory();
        MembersRepository members = new MembersRepository.InMemory();

        items.add(Item
                .builder()
                .id(new ItemId("iDoExist"))
                .build());

        loans.add(Loan
                .builder()
                .id(new LoanId("loan1"))
                .itemId(new ItemId("iDoExist"))
                .status(Loan.STATUS.RETURNED)
                .build());

        ReturnItemCommandHandler handler = new ReturnItemCommandHandler(items, loans, members);

        assertDoesNotThrow(() -> {
            handler.handle(ReturnItemCommand
                    .builder()
                    .itemId("iDoExist")
                    .ifNoActiveLoanIsFound(ReturnItemCommand.IF_NO_ACTIVE_LOAN_FOUND.IGNORE_SILENTELY)
                    .build());
        });

    }
}