package com.barsifedron.candid.cqrs.spring;

import com.barsifedron.candid.cqrs.command.Command;
import com.barsifedron.candid.cqrs.command.CommandHandler;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Will scan packages for Command Handlers.
 * <p>
 * Warning, if you use @Component on your handler, you will have errors here as the class will be found twice.
 * One because of the annotation and one because of this scanning.
 */
public class CommandHandlersRegistryNoAnnotation {

    private CommandHandlersRegistry decorated;

    public CommandHandlersRegistryNoAnnotation(ApplicationContext applicationContext, String... packages) {

        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) factory;

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(CommandHandler.class));

        Stream
                .of(packages)
                .filter(thePackage -> !StringUtils.isEmpty(thePackage))
                .map(provider::findCandidateComponents)
                .flatMap(Collection::stream)
                .forEach(beanDefinition -> {

                    // register the handlers in the application context
                    beanDefinitionRegistry.registerBeanDefinition(
                            beanDefinition.getBeanClassName(),
                            new GenericBeanDefinition(beanDefinition));
                });

        // now the beans are in the application context
        // lets pass it to the "classic" registry
        decorated = new CommandHandlersRegistry(applicationContext);
    }

    public Map<Class<Command>, Supplier<CommandHandler>> handlers() {
        return decorated.handlers();
    }

}
