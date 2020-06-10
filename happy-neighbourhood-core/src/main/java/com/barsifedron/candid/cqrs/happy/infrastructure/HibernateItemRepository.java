package com.barsifedron.candid.cqrs.happy.infrastructure;

import com.barsifedron.candid.cqrs.happy.domain.Item;
import com.barsifedron.candid.cqrs.happy.domain.ItemId;
import com.barsifedron.candid.cqrs.happy.domain.ItemsRepository;
import com.barsifedron.candid.cqrs.happy.domain.QItem;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

public class HibernateItemRepository implements ItemsRepository {

    private final EntityManager entityManager;

    @Inject
    public HibernateItemRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void add(List<Item> item) {
        item.stream().forEach(entityManager::persist);
    }

    @Override
    public Item get(ItemId id) {
        return new JPAQueryFactory(entityManager)
                .select(QItem.item)
                .from(QItem.item)
                .where(QItem.item.id.eq(id))
                .fetchOne();
    }
}
