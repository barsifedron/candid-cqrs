package com.barsifedron.candid.cqrs.happy.utils.cqrs.command.middleware;



import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandBus;
import com.barsifedron.candid.cqrs.command.CommandBusMiddleware;
import com.barsifedron.candid.cqrs.command.CommandResponse;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A decorating command middleware that will validate all the commands that passes through it.
 */
public class ValidatingCommandBusMiddleware implements CommandBusMiddleware {

    private final Validator validator;

    @Inject
    public ValidatingCommandBusMiddleware() {
        this(Validation.buildDefaultValidatorFactory().getValidator());
    }

    public ValidatingCommandBusMiddleware(Validator validator) {
        this.validator = validator;
    }

    @Override
    public <T> CommandResponse<T> dispatch(Command<T> command, CommandBus bus) {
        Set<ConstraintViolation<Command>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            throw new IllegalCommandException(violations);
        }
        return bus.dispatch(command);
    }

    public static class IllegalCommandException extends RuntimeException {
        public IllegalCommandException(Set<ConstraintViolation<Command>> violations) {
            super(violations
                    .stream()
                    .map(violation -> violation.getPropertyPath().toString() + ": " + violation.getMessage())
                    .collect(Collectors.joining("\n", "Command was invalid:\n", "")));
        }
    }
}
