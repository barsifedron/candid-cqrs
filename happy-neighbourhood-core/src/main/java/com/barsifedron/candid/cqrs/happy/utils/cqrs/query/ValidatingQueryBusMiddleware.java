package com.barsifedron.candid.cqrs.happy.utils.cqrs.query;

import com.barsifedron.candid.cqrs.query.Query;
import com.barsifedron.candid.cqrs.query.QueryBus;
import com.barsifedron.candid.cqrs.query.QueryBusMiddleware;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A decorating command middleware that will validate all the queries that passes through it.
 */
public class ValidatingQueryBusMiddleware implements QueryBusMiddleware {

    private final Validator validator;

    @Inject
    public ValidatingQueryBusMiddleware() {
        this(Validation.buildDefaultValidatorFactory().getValidator());
    }

    public ValidatingQueryBusMiddleware(Validator validator) {
        this.validator = validator;
    }

    @Override
    public <T> T dispatch(Query<T> query, QueryBus bus) {
        Set<ConstraintViolation<Query>> violations = validator.validate(query);
        if (!violations.isEmpty()) {
            throw new IllegalCommandException(violations);
        }
        return bus.dispatch(query);
    }

    public static class IllegalCommandException extends RuntimeException {
        public IllegalCommandException(Set<ConstraintViolation<Query>> violations) {
            super(violations
                    .stream()
                    .map(violation -> violation.getPropertyPath().toString() + ": " + violation.getMessage())
                    .collect(Collectors.joining("\n", "Query was invalid:\n", "")));
        }
    }
}
