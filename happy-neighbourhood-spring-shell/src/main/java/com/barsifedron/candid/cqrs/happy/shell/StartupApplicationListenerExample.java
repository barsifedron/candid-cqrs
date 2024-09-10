package com.barsifedron.candid.cqrs.happy.shell;

import com.barsifedron.candid.cqrs.happy.command.BorrowItemCommand;
import com.barsifedron.candid.cqrs.happy.command.BorrowItemCommandHandler;
import com.barsifedron.candid.cqrs.happy.command.RegisterNewItemCommand;
import com.barsifedron.candid.cqrs.happy.command.RegisterNewMemberCommand;
import com.barsifedron.candid.cqrs.happy.command.ReturnItemCommand;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.command.CommandBusFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class StartupApplicationListenerExample implements
        ApplicationListener<ContextRefreshedEvent> {

    public static int counter;
    private CommandBusFactory commandBusFactory;

    @Inject
    public StartupApplicationListenerExample(CommandBusFactory commandBusFactory) {
        this.commandBusFactory = commandBusFactory;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        commandBusFactory.simpleBus().dispatch(
                RegisterNewMemberCommand
                        .builder()
                        .memberId("john")
                        .firstname("the")
                        .surname("first")
                        .email("the.first@email.com")
                        .build()
        );

        commandBusFactory.simpleBus().dispatch(
                RegisterNewItemCommand
                        .builder()
                        .id("hammerId")
                        .name("hammmer")
                        .dailyRate(new BigDecimal("1.00"))
                        .dailyFineWhenLateReturn(new BigDecimal("2.00"))
                        .maximumLoanPeriod(14)
                        .build()
        );

        commandBusFactory.withOutsideTransactionCapability().dispatch(
                BorrowItemCommand
                        .builder()
                        .itemId("hammerId")
                        .loanId("loan1")
                        .memberId("john")
                        .borrowedOn(LocalDate.now().minusDays(25))
                        .notification(BorrowItemCommandHandler.NOTIFICATION.EMAIL_CONFIRMATION_TO_MEMBER)
                        .build()
        );

        commandBusFactory.simpleBus().dispatch(
                ReturnItemCommand
                        .builder()
                        .itemId("hammerId")
                        .build()
        );

        commandBusFactory.withOutsideTransactionCapability().dispatch(
                BorrowItemCommand
                        .builder()
                        .itemId("hammerId")
                        .loanId("loan2")
                        .memberId("john")
                        .borrowedOn(LocalDate.now().minusDays(20))
                        .notification(BorrowItemCommandHandler.NOTIFICATION.EMAIL_CONFIRMATION_TO_MEMBER)
                        .build()
        );

        commandBusFactory.simpleBus().dispatch(
                ReturnItemCommand
                        .builder()
                        .itemId("hammerId")
                        .build()
        );

        commandBusFactory.withOutsideTransactionCapability().dispatch(
                BorrowItemCommand
                        .builder()
                        .itemId("hammerId")
                        .loanId("loan3")
                        .memberId("john")
                        .borrowedOn(LocalDate.now().minusDays(10))
                        .notification(BorrowItemCommandHandler.NOTIFICATION.EMAIL_CONFIRMATION_TO_MEMBER)
                        .build()
        );

        commandBusFactory.simpleBus().dispatch(
                ReturnItemCommand
                        .builder()
                        .itemId("hammerId")
                        .build()
        );


    }
}