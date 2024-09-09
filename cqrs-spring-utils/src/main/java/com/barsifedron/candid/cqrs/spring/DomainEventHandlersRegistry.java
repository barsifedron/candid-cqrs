package com.barsifedron.candid.cqrs.spring;

import com.barsifedron.candid.cqrs.domainevent.DomainEvent;
import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
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
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DomainEventHandlersRegistry {

    private final Map<Class<DomainEvent>, List<Supplier<DomainEventHandler>>> map = new HashMap<>();
    private final DomainEventHandlerInfoList allEventHandlersInfoList;

    @Autowired
    public DomainEventHandlersRegistry(ApplicationContext applicationContext) {
        this(applicationContext, "");
    }

    /**
     * Will scan packages for Domain event Handlers.
     * <p>
     * If you use @Component on your handlers, you will have errors here as the classes will be found twice.
     * Once because of the annotation and once because of this scanning.
     * <p>
     * As a rule, either use the @Component annotation on ALL your handlers and use the above constructor
     * OR
     * Never use the annotation and scan packages. It is one or the other.
     */
    public DomainEventHandlersRegistry(ApplicationContext applicationContext, String... packages) {

        Stream.of(packages).forEach(System.out::print);
        registerHandlersToApplicationContext(applicationContext, packages);

        String[] handlersClassNames = applicationContext.getBeanNamesForType(DomainEventHandler.class);

        List<DomainEventHandlerInfo> handlers = Stream
                .of(handlersClassNames)
                .map(name -> gatherHandlerInfos(applicationContext, name))
                .collect(toList());
        allEventHandlersInfoList = new DomainEventHandlerInfoList(handlers);
    }

    private DomainEventHandlerInfo gatherHandlerInfos(ApplicationContext applicationContext, String name) {

        // The domain event handler
        Class<DomainEventHandler<?>> handlerClass = (Class<DomainEventHandler<?>>) applicationContext.getType(name);

        // The domain event the handler will listen to
        Class<?>[] generics = GenericTypeResolver.resolveTypeArguments(handlerClass, DomainEventHandler.class);
        Class<DomainEvent> eventType = (Class<DomainEvent>) generics[0];

        // The supplier we will use to instantiate the handler
        Supplier<DomainEventHandler> handlerSupplier = () -> applicationContext.getBean(handlerClass);

        return new DomainEventHandlerInfo(
                eventType,
                handlerClass,
                handlerSupplier);
    }

    public Map<Class<DomainEvent>, List<Supplier<DomainEventHandler>>> handlers() {
        return allEventHandlersInfoList.asMap();
    }

    public DomainEventHandlerInfoList handlersList(){
        return allEventHandlersInfoList;
    }

    /**
     * Scan packages for instances of DomainEventHandlers and registers them in the spring boot
     * registry.
     */
    private void registerHandlersToApplicationContext(
            ApplicationContext applicationContext,
            String... packages) {

        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        BeanDefinitionRegistry definitionRegistry = (BeanDefinitionRegistry) factory;

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(DomainEventHandler.class));

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

    /**
     * A list with information about all the event handlers a bus will manage,
     * as well as a way to instantiate them when needed.
     */
    public static class DomainEventHandlerInfoList {

        private final List<DomainEventHandlerInfo> types;

        public DomainEventHandlerInfoList(List<DomainEventHandlerInfo> types) {
            this.types = types;
        }

        /**
         * Organises the list into a map that will allow the event bus to know,
         * for an event type, which handlers to instantiate.
         * Reminder, contrarily to commands and queries, more than one handler may listen to the same domain event.
         */
        public Map<Class<DomainEvent>, List<Supplier<DomainEventHandler>>> asMap() {
            return types.stream().collect(groupingBy(
                            type -> type.eventType,
                            mapping(type -> type.handlerSupplier, toList())));
        }

        /**
         * Recreates a list keeping only the subset satisfying the given constraint.
         * While in practice you should never really need this, it can be convenient for really specific use cases when we want to exclude some handlers from a bus.
         */
        public DomainEventHandlerInfoList satisfying(Predicate<Class<? extends DomainEventHandler>> predicate) {
            return new DomainEventHandlerInfoList(types
                    .stream()
                    .filter(handler -> predicate.test(handler.handlerType))
                    .collect(toList()));
        }

        /**
         * Recreates a list keeping only the subset satisfying the given constraint.
         * While in practice you should never really need this, it can be convenient for really specific use cases when we want to exclude some handlers from a bus.
         */
        public DomainEventHandlerInfoList forEventsSatisfying(Predicate<Class<DomainEvent>> predicate) {
            return new DomainEventHandlerInfoList(types
                    .stream()
                    .filter(handler -> predicate.test(handler.eventType))
                    .collect(toList()));
        }
    }

    private static class DomainEventHandlerInfo {

        private final Class<DomainEvent> eventType;
        private final Class<? extends DomainEventHandler> handlerType;
        private final Supplier<DomainEventHandler> handlerSupplier;

        public DomainEventHandlerInfo(
                Class<DomainEvent> eventType,
                Class<? extends DomainEventHandler> handlerType,
                Supplier<DomainEventHandler> handlerSupplier) {
            this.eventType = eventType;
            this.handlerType = handlerType;
            this.handlerSupplier = handlerSupplier;
        }
    }
}
