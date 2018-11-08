package com.barsifedron.candid.cqrs.command;

import org.junit.Test;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ValidatingCommandBusMiddlewareTest {


    public ValidatingCommandBusMiddlewareTest() {
    }


    @Test(expected = ValidatingCommandBusMiddleware.IllegalCommandException.class)
    public void shouldFailIfEmailIsBlank() {
        CommandBusMiddlewareChain chain = new CommandBusMiddlewareChain.Factory().chainOfMiddleware(
                new ValidatingCommandBusMiddleware(),
                new CommandBusMiddleware.Dispatcher(new HashSet<>()));
        CommandBus bus = chain::dispatch;
        TestCommand testCommand = new TestCommand("", "aName");
        bus.dispatch(testCommand);
    }

    @Test(expected = ValidatingCommandBusMiddleware.IllegalCommandException.class)
    public void shouldFailIfEmailIsNotValid() {
        CommandBusMiddlewareChain chain = new CommandBusMiddlewareChain.Factory().chainOfMiddleware(
                new ValidatingCommandBusMiddleware(),
                new CommandBusMiddleware.Dispatcher(new HashSet<>()));
        CommandBus bus = chain::dispatch;
        TestCommand testCommand = new TestCommand("jack@", "aName");
        bus.dispatch(testCommand);
    }

    @Test(expected = ValidatingCommandBusMiddleware.IllegalCommandException.class)
    public void shouldFailIfNameIsNull() {
        CommandBusMiddlewareChain chain = new CommandBusMiddlewareChain.Factory().chainOfMiddleware(
                new ValidatingCommandBusMiddleware(),
                new CommandBusMiddleware.Dispatcher(new HashSet<>()));
        CommandBus bus = chain::dispatch;
        TestCommand testCommand = new TestCommand("jack@hotmail.com", null);
        bus.dispatch(testCommand);
    }

    @Test(expected = ValidatingCommandBusMiddleware.IllegalCommandException.class)
    public void shouldFailIfNameIsBlank() {
        CommandBusMiddlewareChain chain = new CommandBusMiddlewareChain.Factory().chainOfMiddleware(
                new ValidatingCommandBusMiddleware(),
                new CommandBusMiddleware.Dispatcher(new HashSet<>()));
        CommandBus bus = chain::dispatch;
        TestCommand testCommand = new TestCommand("jack@hotmail.com", "");
        bus.dispatch(testCommand);
    }

    @Test
    public void shouldBeHappyWhenTheCommandIsValid() {
        CommandBusMiddlewareChain chain = new CommandBusMiddlewareChain.Factory().chainOfMiddleware(
                new ValidatingCommandBusMiddleware(),
                new CommandBusMiddleware.Dispatcher(Stream.of(new TestCommandHandler()).collect(Collectors.toSet())));
        CommandBus bus = chain::dispatch;
        TestCommand testCommand = new TestCommand("jack@hotmail.com", "steve");
        bus.dispatch(testCommand);
    }


    public static class TestCommand implements Command<NoResult> {
        @Email(message = "no invalid email")
        @NotBlank(message = "no blank email")
        String email;

        @NotBlank(message = "no blank name")
        String name;

        public TestCommand(String email, String name) {
            this.email = email;
            this.name = name;
        }
    }

    static class TestCommandHandler implements CommandHandler<NoResult, TestCommand> {

        @Override
        public CommandResponse<NoResult> handle(TestCommand command) {
            return CommandResponse.withoutResult();
        }

        @Override
        public Class<TestCommand> listenTo() {
            return TestCommand.class;
        }
    }
}