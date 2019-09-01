package com.barsifedron.candid.cqrs.guice;

import com.barsifedron.candid.cqrs.query.Query;
import com.barsifedron.candid.cqrs.query.QueryHandler;
import com.google.inject.Injector;
import com.google.inject.Provider;
import org.reflections.Reflections;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;

/**
 * This is a really ugly way of doing things, due to guice limitations. We
 * should be able to do better when we upgrade versions. Theoretically,
 * MapBinder should allow to inject a Map directly.
 */
public class QueryHandlersRegistry {

    private final Injector injector;
    private final Set<String> packages;
    private Map<Class<Query>, Supplier<QueryHandler>> map;

    @Inject
    public QueryHandlersRegistry(Injector injector, @Named("CQRS") Set<String> packages) {
        this.injector = injector;
        this.packages = packages;
        init();
    }

    /**
     * We'll build the map matching the query types to the right query handler.
     * To save resources we will map each command type to a provider so we only instantiate what we need at the right time.
     */
    private void init() {
        map = new Reflections(packages.toArray())
                .getSubTypesOf(QueryHandler.class)
                .stream()
                .collect(toMap(
                        queryHandler -> queryType(queryHandler),
                        queryHandler -> {
                            Provider<? extends QueryHandler> provider = injector.getProvider(queryHandler);
                            return () -> provider.get();
                        }));
    }

    /**
     * The type of queries this handler can handle
     */
    private Class<Query> queryType(Class<? extends QueryHandler> queryHandler) {
        return (Class<Query>) ((ParameterizedType) queryHandler.getGenericInterfaces()[0]).getActualTypeArguments()[1];
    }

    public  Map<Class<Query>, Supplier<QueryHandler>>  handlers() {
        return map;
    }
}
