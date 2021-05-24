package ru.job4j.tracker;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class HbmTrackerTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void createItem() throws Exception {
        try (HbmTracker tracker = new HbmTracker()) {
            tracker.add(new Item("name"));
            assertThat(tracker.findByName("name").size(), is(1));
        }
    }

    @Test
    public void createAndReplaceItem() throws Exception {
        try (HbmTracker tracker = new HbmTracker()) {
            tracker.add(new Item( "name"));
            tracker.replace(1,new Item("chahgeName"));
            assertThat(tracker.findById(1).getName(), is("chahgeName"));
        }
    }

    @Test
    public void createAndDeleteItem() throws Exception {
        try (HbmTracker tracker = new HbmTracker()) {
            tracker.add(new Item( "name"));
            tracker.add(new Item( "name"));
            tracker.delete(1);
            assertThat(tracker.findByName("name").size(), is(1));
        }
    }

    @Test
    public void findAllItem() throws Exception {
        try (HbmTracker tracker = new HbmTracker()) {
            tracker.add(new Item( "name"));
            tracker.add(new Item( "name"));
            tracker.add(new Item( "name3"));
            assertThat(tracker.findAll().size(), is(3));
        }
    }
}