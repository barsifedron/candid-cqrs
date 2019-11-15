package com.barsifedron.candid.cqrs.spring;

import com.barsifedron.candid.cqrs.domainevent.DomainEvent;
import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DomainEventHandlersRegistry {

    private final Map<Class<DomainEvent>, List<Supplier<DomainEventHandler>>> map = new HashMap<>();

    @Autowired
    public DomainEventHandlersRegistry(ApplicationContext applicationContext) {
        String[] names = applicationContext.getBeanNamesForType(DomainEventHandler.class);
        for (String name : names) {
            register(applicationContext, name);
        }
    }

    private void register(ApplicationContext applicationContext, String name) {
        Class<DomainEventHandler<?>> handlerClass = (Class<DomainEventHandler<?>>) applicationContext.getType(name);
        Class<?>[] generics = GenericTypeResolver.resolveTypeArguments(handlerClass, DomainEventHandler.class);
        Class<DomainEvent> domainType = (Class<DomainEvent>) generics[0];
        map.putIfAbsent(domainType, new ArrayList<>());
        map.get(domainType).add(() -> applicationContext.getBean(handlerClass));
    }

    public Map<Class<DomainEvent>, List<Supplier<DomainEventHandler>>> handlers() {
        return map;
    }

}
