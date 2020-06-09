package com.barsifedron.candid.cqrs.springboot;

import com.barsifedron.candid.cqrs.happy.command.CreateNewLoanCommand;
import com.barsifedron.candid.cqrs.happy.command.RegisterNewItemCommand;
import com.barsifedron.candid.cqrs.happy.command.RegisterNewMemberCommand;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import com.barsifedron.candid.cqrs.springboot.cqrs.command.CommandBusFactory;
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

        commandBusFactory.simpleBus().dispatch(
                CreateNewLoanCommand
                        .builder()
                        .itemId("hammerId")
                        .loanId("loan1")
                        .memberId("john")
                        .borrowedOn(LocalDate.now())
                        .build()
        );

    }
}