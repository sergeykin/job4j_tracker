package ru.job4j.tracker;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.sql.Timestamp;
import java.util.List;

public class HibernateRun {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        try {
                SessionFactory sf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
                Item item = create(new Item("Learn Hibernate", "description 01", new Timestamp(1459510232000L) ), sf);
                System.out.println(item);
                item.setName("Learn Hibernate 5.");
                update(item, sf);
                System.out.println(item);
                Item rsl = findById(item.getId(), sf);
                System.out.println(rsl);

                List<Item> list = findAll(sf);
                for (Item it : list) {
                    System.out.println(it);
                }
                delete(6456, sf);
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static Item create(Item item, SessionFactory sf) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(item);
            session.getTransaction().commit();
        }
        return item;
    }

    public static void update(Item item, SessionFactory sf) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            session.update(item);
            session.getTransaction().commit();
        }
    }

    public static void delete(Integer id, SessionFactory sf) {
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            Item item = new Item(null, null, null);
            item.setId(id);
            session.delete(item);
            session.getTransaction().commit();
        }
    }

    public static List<Item> findAll(SessionFactory sf) {
        List result;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            result = session.createQuery("from ru.job4j.tracker.Item").list();
            session.getTransaction().commit();
        }
        return result;
    }

    public static Item findById(Integer id, SessionFactory sf) {
        Item result;
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            result = session.get(Item.class, id);
            session.getTransaction().commit();
        }
        return result;
    }
}
