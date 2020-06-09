package com.barsifedron.candid.cqrs.happy.command;

import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.command.NoResult;
import com.barsifedron.candid.cqrs.happy.domain.Item;
import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.ItemsRepository;
import com.barsifedron.candid.cqrs.happy.domain.Loan;
import com.barsifedron.candid.cqrs.happy.domain.LoanId;
import com.barsifedron.candid.cqrs.happy.domain.LoanRepository;
import com.barsifedron.candid.cqrs.happy.domain.Member;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import com.barsifedron.candid.cqrs.happy.domain.MembersRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CreateNewLoanCommandHandlerTest {

    @Test
    void shouldFailIfMemberDoesNotExist() {

        MembersRepository.InMemory members = new MembersRepository.InMemory();

        CreateNewLoanCommand command = CreateNewLoanCommand
                .builder()
                .memberId("iDoNotExist")
                .build();

        CreateNewLoanCommandHandler handler = new CreateNewLoanCommandHandler(
                new LoanRepository.InMemory(),
                new ItemsRepository.InMemory(),
                members);

        assertThrows(
                CreateNewLoanCommandHandler.MemberDoesNotExistException.class,
                () -> handler.handle(command));

    }

    @Test
    void shouldFailIfItemDoesNotExist() {

        MembersRepository.InMemory members = new MembersRepository.InMemory();
        members.add(Member.builder().memberId(new MemberId("iExist")).build());

        CreateNewLoanCommand command = CreateNewLoanCommand
                .builder()
                .memberId("iExist")
                .itemId("iDoNotExist")
                .build();

        CreateNewLoanCommandHandler handler = new CreateNewLoanCommandHandler(
                new LoanRepository.InMemory(),
                new ItemsRepository.InMemory(),
                members);

        assertThrows(
                CreateNewLoanCommandHandler.ItemDoesNotExistException.class,
                () -> handler.handle(command));

    }

    @Test
    void shouldFailIMemberHasALoanInProgress() {

        LoanRepository.InMemory loans = new LoanRepository.InMemory();
        ItemsRepository.InMemory items = new ItemsRepository.InMemory();
        MembersRepository.InMemory members = new MembersRepository.InMemory();

        members.add(
                Member
                        .builder()
                        .memberId(new MemberId("John"))
                        .build(),
                Member
                        .builder()
                        .memberId(new MemberId("Jack"))
                        .build()
        );

        items.add(
                Item
                        .builder()
                        .name("Screwdriver")
                        .id(new ItemId("Screwdriver - 1"))
                        .build(),
                Item
                        .builder()
                        .name("Screwdriver")
                        .id(new ItemId("Screwdriver - 2"))
                        .dailyRate(new BigDecimal("0.50"))
                        .dailyFineWhenLateReturn(new BigDecimal("2.00"))
                        .maximumLoanPeriod(10)
                        .build(),
                Item
                        .builder()
                        .name("Screwdriver")
                        .id(new ItemId("Screwdriver - 3"))
                        .dailyRate(new BigDecimal("0.50"))
                        .dailyFineWhenLateReturn(new BigDecimal("2.00"))
                        .maximumLoanPeriod(10)
                        .build());

        loans.add(Loan
                .builder()
                .id(new LoanId("loan -1"))
                .itemId(new ItemId("Screwdriver - 1"))
                .memberId(new MemberId("John"))
                .status(Loan.STATUS.IN_PROGRESS)
                .build());

        CreateNewLoanCommandHandler handler = new CreateNewLoanCommandHandler(
                loans,
                items,
                members);

        assertDoesNotThrow(
                () -> handler.handle(CreateNewLoanCommand
                        .builder()
                        .memberId("Jack")
                        .itemId("Screwdriver - 2")
                        .build()));

        assertThrows(
                CreateNewLoanCommandHandler.MemberHasLoanInProgressException.class,
                () -> handler.handle(CreateNewLoanCommand
                        .builder()
                        .memberId("John")
                        .itemId("Screwdriver - 3")
                        .build()));

    }

    @Test
    void shouldFailIItemHasALoanInProgress() {

        LoanRepository.InMemory loans = new LoanRepository.InMemory();
        ItemsRepository.InMemory items = new ItemsRepository.InMemory();
        MembersRepository.InMemory members = new MembersRepository.InMemory();

        members.add(
                Member
                        .builder()
                        .memberId(new MemberId("John"))
                        .build(),
                Member
                        .builder()
                        .memberId(new MemberId("Jack"))
                        .build(),
                Member
                        .builder()
                        .memberId(new MemberId("Sophie"))
                        .build()
        );

        items.add(
                Item
                        .builder()
                        .name("Screwdriver")
                        .id(new ItemId("Screwdriver - 1"))
                        .build(),
                Item
                        .builder()
                        .name("Screwdriver")
                        .id(new ItemId("Screwdriver - 2"))
                        .dailyRate(new BigDecimal("0.50"))
                        .dailyFineWhenLateReturn(new BigDecimal("2.00"))
                        .maximumLoanPeriod(10)
                        .build(),
                Item
                        .builder()
                        .name("Screwdriver")
                        .id(new ItemId("Screwdriver - 3"))
                        .dailyRate(new BigDecimal("0.50"))
                        .dailyFineWhenLateReturn(new BigDecimal("2.00"))
                        .maximumLoanPeriod(10)
                        .build());
        loans.add(Loan
                .builder()
                .id(new LoanId("loan -1"))
                .itemId(new ItemId("Screwdriver - 1"))
                .memberId(new MemberId("John"))
                .status(Loan.STATUS.IN_PROGRESS)
                .build());

        CreateNewLoanCommandHandler handler = new CreateNewLoanCommandHandler(
                loans,
                items,
                members
        );

        assertDoesNotThrow(
                () -> handler.handle(CreateNewLoanCommand
                        .builder()
                        .memberId("Sophie")
                        .itemId("Screwdriver - 3")
                        .build()));

        assertThrows(
                CreateNewLoanCommandHandler.ItemHasLoanInProgressException.class,
                () -> handler.handle(CreateNewLoanCommand
                        .builder()
                        .memberId("Jack")
                        .itemId("Screwdriver - 1")
                        .build()));

    }

    @Test
    void shouldCreateProperLoan() {

        LoanRepository.InMemory loans = new LoanRepository.InMemory();
        ItemsRepository.InMemory items = new ItemsRepository.InMemory();
        MembersRepository.InMemory members = new MembersRepository.InMemory();

        members.add(
                Member
                        .builder()
                        .memberId(new MemberId("John"))
                        .build(),
                Member
                        .builder()
                        .memberId(new MemberId("Jack"))
                        .build(),
                Member
                        .builder()
                        .memberId(new MemberId("Sophie"))
                        .build()
        );

        items.add(
                Item
                        .builder()
                        .name("Screwdriver")
                        .id(new ItemId("Screwdriver - 1"))
                        .build(),
                Item
                        .builder()
                        .name("Screwdriver")
                        .id(new ItemId("Screwdriver - 2"))
                        .build(),
                Item
                        .builder()
                        .name("Screwdriver")
                        .id(new ItemId("Screwdriver - 3"))
                        .dailyRate(new BigDecimal("0.50"))
                        .dailyFineWhenLateReturn(new BigDecimal("2.00"))
                        .maximumLoanPeriod(10)
                        .build());

        loans.add(Loan
                .builder()
                .id(new LoanId("loan -1"))
                .itemId(new ItemId("Screwdriver - 1"))
                .memberId(new MemberId("John"))
                .status(Loan.STATUS.IN_PROGRESS)
                .build());

        CreateNewLoanCommandHandler handler = new CreateNewLoanCommandHandler(
                loans,
                items,
                members
        );

        CommandResponse<LoanId> commandResponse = assertDoesNotThrow(
                () -> handler.handle(CreateNewLoanCommand
                        .builder()
                        .loanId("loan - 1")
                        .memberId("Sophie")
                        .itemId("Screwdriver - 3")
                        .borrowedOn(LocalDate.parse("2020-06-09"))
                        .build()));

        Loan newLoan = loans.get(new LoanId("loan - 1"));

        assertTrue(newLoan.hasItemId(new ItemId("Screwdriver - 3")));
        assertTrue(newLoan.hasMemberId(new MemberId("Sophie")));
        assertTrue(newLoan.wasBorrowedOn(LocalDate.parse("2020-06-09")));

    }

    @Test
    void shouldCreateLoanWithBorrowedDateSetToNowIfNotDefined() {

        LoanRepository.InMemory loans = new LoanRepository.InMemory();
        ItemsRepository.InMemory items = new ItemsRepository.InMemory();
        MembersRepository.InMemory members = new MembersRepository.InMemory();

        members.add(
                Member
                        .builder()
                        .memberId(new MemberId("John"))
                        .build(),
                Member
                        .builder()
                        .memberId(new MemberId("Jack"))
                        .build(),
                Member
                        .builder()
                        .memberId(new MemberId("Sophie"))
                        .build()
        );

        items.add(
                Item
                        .builder()
                        .name("Screwdriver")
                        .id(new ItemId("Screwdriver - 1"))
                        .build(),
                Item
                        .builder()
                        .name("Screwdriver")
                        .id(new ItemId("Screwdriver - 2"))
                        .build(),
                Item
                        .builder()
                        .name("Screwdriver")
                        .id(new ItemId("Screwdriver - 3"))
                        .dailyRate(new BigDecimal("0.50"))
                        .dailyFineWhenLateReturn(new BigDecimal("2.00"))
                        .maximumLoanPeriod(10)
                        .build());

        loans.add(Loan
                .builder()
                .id(new LoanId("loan -1"))
                .itemId(new ItemId("Screwdriver - 1"))
                .memberId(new MemberId("John"))
                .status(Loan.STATUS.IN_PROGRESS)
                .build());

        CreateNewLoanCommandHandler handler = new CreateNewLoanCommandHandler(
                loans,
                items,
                members
        );

        CommandResponse<LoanId> commandResponse = assertDoesNotThrow(
                () -> handler.handle(CreateNewLoanCommand
                        .builder()
                        .loanId("loan - 1")
                        .memberId("Sophie")
                        .itemId("Screwdriver - 3")
                        .build()));

        Loan newLoan = loans.get(new LoanId("loan - 1"));

        assertTrue(newLoan.hasItemId(new ItemId("Screwdriver - 3")));
        assertTrue(newLoan.hasMemberId(new MemberId("Sophie")));
        assertTrue(newLoan.wasBorrowedOn(LocalDate.now()));

    }

}