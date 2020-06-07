package com.barsifedron.candid.cqrs.springboot;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListenerExample implements
        ApplicationListener<ContextRefreshedEvent> {

    public static int counter;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("YOOOOOOOOOOd");
        System.out.println("YOOOOOOOOOOd");
        System.out.println("YOOOOOOOOOOd");
        System.out.println("YOOOOOOOOOOd");
        System.out.println("YOOOOOOOOOOd");
    }
}