package com.barsifedron.candid.cqrs.spring;

import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import com.barsifedron.candid.cqrs.query.Query;
import com.barsifedron.candid.cqrs.query.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class QueryHandlersRegistry {

    private final Map<Class<Query>, Supplier<QueryHandler>> map = new HashMap<>();

    @Autowired
    public QueryHandlersRegistry(ApplicationContext applicationContext) {
        this(applicationContext, "");
    }

    public QueryHandlersRegistry(ApplicationContext applicationContext, String... packages) {

        registerHandlersToApplicationContext(applicationContext, packages);

        String[] names = applicationContext.getBeanNamesForType(QueryHandler.class);
        for (String name : names) {
            register(applicationContext, name);
        }
    }

    private void register(ApplicationContext applicationContext, String name) {
        Class<QueryHandler<?, ?>> handlerClass = (Class<QueryHandler<?, ?>>) applicationContext.getType(name);
        Class<?>[] generics = GenericTypeResolver.resolveTypeArguments(handlerClass, QueryHandler.class);
        Class<Query> queryType = (Class<Query>) generics[1];
        map.put(queryType, () -> applicationContext.getBean(handlerClass));
    }

    public Map<Class<Query>, Supplier<QueryHandler>> handlers() {
        return map;
    }

    private void registerHandlersToApplicationContext(
            ApplicationContext applicationContext,
            String... packages) {

        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry) factory;

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(QueryHandler.class));

        Stream
                .of(packages)
                .filter(thePackage -> !StringUtils.isEmpty(thePackage))
                .map(provider::findCandidateComponents)
                .flatMap(Collection::stream)
                .forEach(handlerDefinition -> {

                    // register the handlers in the application context
                    definitionRegistry.registerBeanDefinition(
                            handlerDefinition.getBeanClassName(),
                            new GenericBeanDefinition(handlerDefinition));
                });
    }

}
