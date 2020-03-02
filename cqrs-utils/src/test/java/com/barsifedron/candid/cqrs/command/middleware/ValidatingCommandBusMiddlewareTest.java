package com.barsifedron.candid.cqrs.command.middleware;

import com.barsifedron.candid.cqrs.command.*;
import org.junit.Test;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidatingCommandBusMiddlewareTest {

    public ValidatingCommandBusMiddlewareTest() {
    }

    @Test(expected = ValidatingCommandBusMiddleware.IllegalCommandException.class)
    public void shouldFailIfNameIsNull() {
        CommandBus bus =  new WiredCommandBus().of(
                new ValidatingCommandBusMiddleware(),
                new CommandBusDispatcher(new HashSet<>()));
        TestCommand testCommand = new TestCommand(null, 21);
        bus.dispatch(testCommand);
    }

    @Test(expected = ValidatingCommandBusMiddleware.IllegalCommandException.class)
    public void shouldFailIfAgeIsNegative() {
        CommandBus bus = new WiredCommandBus().of(
                new ValidatingCommandBusMiddleware(),
                new CommandBusDispatcher(new HashSet<>()));
        TestCommand testCommand = new TestCommand("jack Malone", -9);
        bus.dispatch(testCommand);
    }

    @Test(expected = ValidatingCommandBusMiddleware.IllegalCommandException.class)
    public void shouldFailIfMinor() {
        CommandBus bus = new WiredCommandBus().of(
                new ValidatingCommandBusMiddleware(),
                new CommandBusDispatcher(new HashSet<>()));
        TestCommand testCommand = new TestCommand("jack malone", 16);
        bus.dispatch(testCommand);
    }

    @Test(expected = ValidatingCommandBusMiddleware.IllegalCommandException.class)
    public void shouldFailIfVotedForBrexit() {
        CommandBus bus = new WiredCommandBus().of(
                new ValidatingCommandBusMiddleware(),
                new CommandBusDispatcher(new HashSet<>()));
        TestCommand testCommand = new TestCommand("jack malone", 70);
        bus.dispatch(testCommand);
    }

    @Test
    public void shouldBeHappyWhenTheCommandIsValid() {
        CommandBus bus = new WiredCommandBus().of(
                new ValidatingCommandBusMiddleware(),
                new CommandBusDispatcher(Stream.of(new TestCommandHandler()).collect(Collectors.toSet())));
        TestCommand testCommand = new TestCommand("jack malone", 65);
        bus.dispatch(testCommand);
    }

    public static class TestCommand implements Command<NoResult> {

        @NotNull(message = "no null name")
        String name;

        @Min(18)
        @Max(65)
        Integer age;

        public TestCommand(String name, int age) {
            this.name = name;
            this.age = age;
        }

    }

    static class TestCommandHandler implements CommandHandler<NoResult, TestCommand> {

        @Override
        public CommandResponse<NoResult> handle(TestCommand command) {
            return CommandResponse.empty();
        }

        @Override
        public Class<TestCommand> listenTo() {
            return TestCommand.class;
        }
    }
}