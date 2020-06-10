package com.barsifedron.candid.cqrs.happy.shell.controllers;

import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.happy.command.BorrowItemCommand;
import com.barsifedron.candid.cqrs.happy.command.BorrowItemCommandHandler;
import com.barsifedron.candid.cqrs.happy.command.RegisterNewMemberCommand;
import com.barsifedron.candid.cqrs.happy.command.RegisterNewMemberCommandHandler;
import com.barsifedron.candid.cqrs.happy.domain.LoanId;
import com.barsifedron.candid.cqrs.happy.domain.LoanRepository;
import com.barsifedron.candid.cqrs.happy.domain.MemberId;
import com.barsifedron.candid.cqrs.happy.query.GetMemberQuery;
import com.barsifedron.candid.cqrs.happy.query.GetMemberQueryHandler;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.query.QueryBusFactory;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.middleware.ValidatingCommandBusMiddleware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/members")
public class MembersController {

    private final CommandBus commandBus;
    private final QueryBusFactory queryBusFactory;
    private final LoanRepository loanRepository;

    @Inject
    public MembersController(CommandBus commandBus,
            QueryBusFactory queryBusFactory,
            LoanRepository loanRepository) {
        this.commandBus = commandBus;
        this.queryBusFactory = queryBusFactory;
        this.loanRepository = loanRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<GetMemberQueryHandler.MemberDto> registerNewMember(
            @RequestBody RegisterNewMemberCommand command) {

        CommandResponse<MemberId> commandResponse = commandBus.dispatch(command
                .toBuilder()
                .memberId(new MemberId().id())
                .build());

        GetMemberQueryHandler.MemberDto dto = queryBusFactory.simpleBus().dispatch(
                GetMemberQuery
                        .builder()
                        .memberId(commandResponse.result.id())
                        .build());

        return new ResponseEntity(dto, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "{memberId}/loans")
    public ResponseEntity<String> borrowItem(
            @PathVariable String memberId,
            @RequestBody BorrowItemCommand borrowItemCommand) {

        CommandResponse<LoanId> commandResponse = commandBus.dispatch(borrowItemCommand
                .toBuilder()
                .loanId(new LoanId().asString())
                .memberId(memberId)
                .notification(BorrowItemCommandHandler.NOTIFICATION.EMAIL_CONFIRMATION_TO_MEMBER)
                .borrowedOn(LocalDate.now())
                .build());

        return new ResponseEntity<>(commandResponse.result.asString(), HttpStatus.CREATED);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/{memberId}")
    public ResponseEntity<GetMemberQueryHandler.MemberDto> getMember(@PathVariable String memberId) {

        GetMemberQueryHandler.MemberDto dto = queryBusFactory.simpleBus().dispatch(
                GetMemberQuery
                        .builder()
                        .memberId(memberId)
                        .build());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    //
    // Spring handling of errors is pretty bad
    //

    @ExceptionHandler(value = { ValidatingCommandBusMiddleware.IllegalCommandException.class })
    public void onValidationExceptions(RuntimeException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = {
            RegisterNewMemberCommandHandler.EmailAlreadyInUseException.class,
            RegisterNewMemberCommandHandler.MemberIdAlreadyInUseException.class })
    public void onRegisterMemberExceptions(RuntimeException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler({
            BorrowItemCommandHandler.ItemDoesNotExistException.class,
            BorrowItemCommandHandler.MemberDoesNotExistException.class,
            BorrowItemCommandHandler.ItemHasLoanInProgressException.class,
            BorrowItemCommandHandler.MemberHasLoanInProgressException.class
    })
    public void onBorrowItemException(RuntimeException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
