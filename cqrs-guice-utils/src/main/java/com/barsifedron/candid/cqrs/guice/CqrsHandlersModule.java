package com.barsifedron.candid.cqrs.guice;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This module will find all command, query and domain event handlers
 * so we can inject them to create our message buses.
 *
 *
 * This one is to use if your guice version does not handle MapBinder well.
 */
public class CqrsHandlersModule extends AbstractModule {

    private String[] packageNames;

    public CqrsHandlersModule(String... packageNames) {
        this.packageNames = packageNames;
    }

    @Override
    protected void configure() {
        bind(new TypeLiteral<Set<String>>() {
        })
                .annotatedWith(Names.named("CQRS"))
                .toInstance(Stream.of(packageNames).collect(Collectors.toSet()));
        bind(QueryHandlersRegistry.class).asEagerSingleton();
        bind(CommandHandlersRegistry.class).asEagerSingleton();
        bind(DomainEventHandlersRegistry.class).asEagerSingleton();
    }
}