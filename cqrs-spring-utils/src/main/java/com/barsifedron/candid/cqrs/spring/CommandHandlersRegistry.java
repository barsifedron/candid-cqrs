package com.barsifedron.candid.cqrs.spring;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandHandler;
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
public class CommandHandlersRegistry {

    private Map<Class<Command>, Supplier<CommandHandler>> map;

    @Autowired
    public CommandHandlersRegistry(ApplicationContext applicationContext) {
        map = new HashMap<>();
        String[] names = applicationContext.getBeanNamesForType(CommandHandler.class);
        for (String name : names) {
            register(applicationContext, name);
        }
    }

    private void register(ApplicationContext applicationContext, String name) {
        Class<CommandHandler<?, ?>> handlerClass = (Class<CommandHandler<?, ?>>) applicationContext.getType(name);
        Class<?>[] generics = GenericTypeResolver.resolveTypeArguments(handlerClass, CommandHandler.class);
        Class<Command> commandType = (Class<Command>) generics[1];
        map.put(commandType, () -> applicationContext.getBean(handlerClass));
    }

    public Map<Class<Command>, Supplier<CommandHandler>> handlers() {
        return map;
    }

}
