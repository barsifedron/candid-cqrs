package com.barsifedron.candid.cqrs.spring;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandHandler;
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
public class CommandHandlersRegistry {

    private final Map<Class<Command>, Supplier<CommandHandler>> map = new HashMap<>();

    @Autowired
    public CommandHandlersRegistry(ApplicationContext applicationContext) {
        this(applicationContext, "");
    }

    /**
     * Will scan packages for Command Handlers.
     * <p>
     * If you use @Component on your handlers, you will have errors here as the classes will be found twice.
     * Once because of the annotation and once because of this scanning.
     * <p>
     * As a rule, either use the @Component annotation on ALL your handlers and use the above constructor
     * OR
     * Never use the annotation and scan packages. It is one or the other.
     */
    public CommandHandlersRegistry(ApplicationContext applicationContext, String... packages) {

        registerHandlersToApplicationContext(applicationContext, packages);

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

    private void registerHandlersToApplicationContext(
            ApplicationContext applicationContext,
            String... packages) {

        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry) factory;

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(CommandHandler.class));

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
