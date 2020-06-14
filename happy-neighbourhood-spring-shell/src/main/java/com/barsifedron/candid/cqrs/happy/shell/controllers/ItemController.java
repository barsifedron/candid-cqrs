package com.barsifedron.candid.cqrs.happy.shell.controllers;

import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandResponse;
import com.barsifedron.candid.cqrs.command.NoResult;
import com.barsifedron.candid.cqrs.happy.command.RegisterNewItemCommand;
import com.barsifedron.candid.cqrs.happy.command.RegisterNewItemCommandHandler;
import com.barsifedron.candid.cqrs.happy.command.ReturnItemCommand;
import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.query.GetItemsQuery;
import com.barsifedron.candid.cqrs.happy.query.GetItemsQueryHandler;
import com.barsifedron.candid.cqrs.happy.shell.utils.cqrs.query.QueryBusFactory;
import com.barsifedron.candid.cqrs.happy.utils.cqrs.command.middleware.ValidatingCommandBusMiddleware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final CommandBus commandBus;
    private final QueryBusFactory queryBusFactory;

    @Inject
    public ItemController(
            CommandBus commandBus,
            QueryBusFactory queryBusFactory) {
        this.commandBus = commandBus;
        this.queryBusFactory = queryBusFactory;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<GetItemsQueryHandler.ItemDto> registerNewItem(@RequestBody RegisterNewItemCommand command) {

        CommandResponse<ItemId> commandResponse = commandBus.dispatch(command
                .toBuilder()
                .id(new ItemId().id())
                .build());

        return new ResponseEntity(
                getOneItem(commandResponse.result.id()),
                HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{itemId}/return")
    public ResponseEntity<String> returnItem(@PathVariable String itemId) {

        commandBus.dispatch(ReturnItemCommand
                .builder()
                .itemId(itemId)
                .ifNoActiveLoanIsFound(ReturnItemCommand.IF_NO_ACTIVE_LOAN_FOUND.FAIL)
                .build());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<GetItemsQueryHandler.ItemDto>> getItems() {

        List<GetItemsQueryHandler.ItemDto> dtos = queryBusFactory.simpleBus().dispatch(GetItemsQuery
                .builder()
                .build());

        return new ResponseEntity(dtos, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{itemId}")
    public ResponseEntity<GetItemsQueryHandler.ItemDto> getItem(@PathVariable String itemId) {
        return new ResponseEntity(getOneItem(itemId), HttpStatus.OK);
    }

    private GetItemsQueryHandler.ItemDto getOneItem(String itemId) {
        List<GetItemsQueryHandler.ItemDto> dtos = queryBusFactory.simpleBus().dispatch(GetItemsQuery
                .builder()
                .itemId(itemId)
                .build());
        return dtos.stream().findFirst().orElse(null);
    }

    //
    // Spring handling of errors is pretty bad
    //

    @ExceptionHandler(value = { ValidatingCommandBusMiddleware.IllegalCommandException.class })
    public void onValidationExceptions(RuntimeException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(value = { RegisterNewItemCommandHandler.ItemIdAlreadyInUseException.class })
    public void onRegisterItemExceptions(RuntimeException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
