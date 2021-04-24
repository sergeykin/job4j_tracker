package ru.job4j.tracker;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.persistence.Query;
import java.sql.Timestamp;
import java.util.List;

public class HbmTracker implements Store, AutoCloseable {
    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    @Override
    public void init() {

    }

    @Override
    public Item add(Item item) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(item);
            session.getTransaction().commit();
        }
        return item;
    }

    @Override
    public boolean replace(Integer id, Item item) {
        try (Session session = sf.openSession()) {
            Item itemold = this.findById(id);
            itemold.setName(item.getName());
            itemold.setCreated(item.getCreated());
            itemold.setDescription(item.getDescription());
            session.beginTransaction();
            session.update(itemold);
            session.getTransaction().commit();
        }
        return true;
    }

    @Override
    public boolean delete(Integer id) {
        boolean result  = false;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            Item item = this.findById(id);
            if (item != null) {
                session.delete(item);
                result = true;
            }
            session.getTransaction().commit();
        }
        return result;
    }

    @Override
    public List<Item> findAll() {
        List result;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            result = session.createQuery("from ru.job4j.tracker.Item").list();
            session.getTransaction().commit();
        }
        return result;
    }

    @Override
    public List<Item> findByName(String key) {
        List result;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            String hql = "from ru.job4j.tracker.Item where name = :name";
            Query query = session.createQuery(hql);
            query.setParameter("name", key);
            result = query.getResultList();
            session.getTransaction().commit();
        }
        return result;
    }

    @Override
    public Item findById(Integer id) {
        Item result;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            result = session.get(Item.class, id);
            session.getTransaction().commit();
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    public static void main(String[] args) throws Exception {
        HbmTracker hbmTracker = new HbmTracker();
        hbmTracker.init();
        Item item = new Item("name1", "descr1", new Timestamp(1459510232000L));
        System.out.println(item.toString());
        Item item2 = new Item("name2", "descr2", new Timestamp(1459510232000L));
        hbmTracker.add(item);
        hbmTracker.add(item2);
        System.out.println(item.toString());
        System.out.println(hbmTracker.findAll());
        hbmTracker.replace(item.getId(), item2);
        System.out.println(hbmTracker.findByName("name1"));
        System.out.println(hbmTracker.findAll());
        hbmTracker.delete(item.getId());
        hbmTracker.delete(item2.getId());
        hbmTracker.close();
    }
}
