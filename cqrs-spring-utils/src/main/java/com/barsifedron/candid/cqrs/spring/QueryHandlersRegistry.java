package com.barsifedron.candid.cqrs.spring;

import com.barsifedron.candid.cqrs.query.Query;
import com.barsifedron.candid.cqrs.query.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class QueryHandlersRegistry {

    private Map<Class<Query>, Supplier<QueryHandler>> map;

    @Autowired
    public QueryHandlersRegistry(ApplicationContext applicationContext) {
        map = new HashMap<>();
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

}
