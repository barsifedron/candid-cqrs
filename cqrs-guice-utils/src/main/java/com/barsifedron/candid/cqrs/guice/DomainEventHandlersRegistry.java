package com.barsifedron.candid.cqrs.guice;

import com.barsifedron.candid.cqrs.domainevent.DomainEvent;
import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.reflections.Reflections;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * This is a really ugly way of doing things, due to guice limitations. We
 * should be able to do better when we upgrade versions. Theoretically,
 * MapBinder should allow to inject a Map directly.
 */
public class DomainEventHandlersRegistry {

    private final Injector injector;
    private final Set<String> packages;
    private Map<Class<DomainEvent>, List<Supplier<DomainEventHandler>>> map;

    @Inject
    public DomainEventHandlersRegistry(Injector injector, @Named("CQRS") Set<String> packages) {
        this.injector = injector;
        this.packages = packages;
        init();
    }

    /**
     * We'll build the map matching the event types to the right command handler.
     * To save resources we will map each event type to a provider so we only instantiate what we need at the right time.
     */
    private void init() {
        Map<Class<DomainEvent>, List<Supplier<DomainEventHandler>>> workMap = new HashMap<>();
        new Reflections(packages.toArray())
                .getSubTypesOf(DomainEventHandler.class)
                .stream()
                .forEach(domainEventHandler -> {
                    Class<DomainEvent> domainEventType = domainEventType(domainEventHandler);
                    Provider<DomainEventHandler> handlerProvider = provider(domainEventHandler);
                    workMap.putIfAbsent(domainEventType, new ArrayList<>());
                    workMap.get(domainEventType).add(() -> handlerProvider.get());
                });
        map = workMap;
    }

    private Provider<DomainEventHandler> provider(Class<? extends DomainEventHandler> domainEventHandler) {
        return (Provider<DomainEventHandler>) injector.getProvider(domainEventHandler);
    }

    /**
     * The type of events this handler can handle
     */
    private Class<DomainEvent> domainEventType(Class<? extends DomainEventHandler> domainEventHandler) {
        return (Class<DomainEvent>) ((ParameterizedType) domainEventHandler.getGenericInterfaces()[0])
                .getActualTypeArguments()[0];
    }

    public Map<Class<DomainEvent>, List<Supplier<DomainEventHandler>>> handlers() {
        return map;
    }
}
