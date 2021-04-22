package ru.job4j.tracker;

import org.junit.After;
import org.junit.Before;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.Assert.*;

public class SqlTrackerTest {

    public Connection init() {
        try (InputStream in = SqlTracker.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            return DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")

            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void createItem() throws Exception {
        try (SqlTracker tracker = new SqlTracker(ConnectionRollback.create(this.init()))) {
            tracker.add(new Item("name"));
            assertThat(tracker.findByName("name").size(), is(1));
        }
    }

    @Test
    public void createAndReplaceItem() throws Exception {
        try (SqlTracker tracker = new SqlTracker(ConnectionRollback.create(this.init()))) {
            tracker.add(new Item( "name"));
            tracker.replace(1,new Item("chahgeName"));
            assertThat(tracker.findById(1).getName(), is("chahgeName"));
        }
    }

    @Test
    public void createAndDeleteItem() throws Exception {
        try (SqlTracker tracker = new SqlTracker(ConnectionRollback.create(this.init()))) {
            tracker.add(new Item( "name"));
            tracker.add(new Item( "name"));
            tracker.delete(1);
            assertThat(tracker.findByName("name").size(), is(1));
        }
    }

    @Test
    public void findAllItem() throws Exception {
        try (SqlTracker tracker = new SqlTracker(ConnectionRollback.create(this.init()))) {
            tracker.add(new Item( "name"));
            tracker.add(new Item( "name"));
            tracker.add(new Item( "name3"));
            assertThat(tracker.findAll().size(), is(3));
        }
    }

}