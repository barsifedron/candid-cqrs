package com.barsifedron.candid.cqrs.happy.domainevents;

import com.barsifedron.candid.cqrs.domainevent.DomainEventHandler;
import lombok.Builder;

import javax.inject.Inject;

/**
 * Communicates the fact a new item was borrowed to the external world through an amazon queue.
 */
public class ItemBorrowedDomainEventHandler_NotifyExternalService
        implements DomainEventHandler<ItemBorrowedDomainEvent> {

    private final AmazonSqsPublisher amazonSqsPublisher;

    @Inject
    public ItemBorrowedDomainEventHandler_NotifyExternalService(
            AmazonSqsPublisher amazonSqsPublisher) {
        this.amazonSqsPublisher = amazonSqsPublisher;
    }

    @Override
    public void handle(ItemBorrowedDomainEvent event) {
        amazonSqsPublisher.publish(ItemBorrowedDomainEvent.builder().itemId(event.itemId).build());
    }

    @Override
    public Class<ItemBorrowedDomainEvent> listenTo() {
        return ItemBorrowedDomainEvent.class;
    }

    // this interface could be in your hexagon with an implementation class in infrastructure package.
    // You might want a more abstract name like OutsideWorldPublisher or whatever.
    public interface AmazonSqsPublisher {
        void publish(ItemBorrowedDomainEvent build);
    }

    /**
     * This SQS event is for the external world.
     * It can contain or not the same data as the ItemBorrowedDomainEvent event.
     * In any case good practice recommend to expose the minimum amount of information and duplicate what was in the DOMAIN event.
     * The two types of events are different concepts and should be treated as such.
     */
    @Builder
    static class ItemBorrowedForExternalWorldEvent {
        Integer itemId;
        //... other interesting fields;
    }

}
