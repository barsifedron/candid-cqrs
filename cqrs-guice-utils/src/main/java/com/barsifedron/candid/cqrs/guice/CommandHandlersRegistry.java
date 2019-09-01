package com.barsifedron.candid.cqrs.guice;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandHandler;
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
public class CommandHandlersRegistry {

    private final Injector injector;
    private final Set<String> packages;
    private Map<Class<Command>, Supplier<CommandHandler>> map;

    @Inject
    public CommandHandlersRegistry(Injector injector, @Named("CQRS") Set<String> packages) {
        this.injector = injector;
        this.packages = packages;
        init();
    }

    /**
     * We'll build the map matching the command types to the right command handler.
     * To save resources we will map each command type to a provider so we only instantiate what we need at the right time.
     */
    private void init() {
        map = new Reflections(packages.toArray())
                .getSubTypesOf(CommandHandler.class)
                .stream()
                .collect(toMap(
                        commandHandler -> commandType(commandHandler),
                        commandHandler -> {
                            Provider<? extends CommandHandler> provider = injector.getProvider(commandHandler);
                            return () -> provider.get();
                        }));
    }

    /**
     * The type of commands this handler can handle
     */
    private Class<Command> commandType(Class<? extends CommandHandler> commandHandler) {
        return (Class<Command>) ((ParameterizedType) commandHandler.getGenericInterfaces()[0])
                .getActualTypeArguments()[1];
    }

    public Map<Class<Command>, Supplier<CommandHandler>> handlers() {
        return map;
    }
}
