package ru.job4j.tracker;


import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class SqlTracker implements Store {
    private Connection cn;

    public static void main(String[] args) {
        try (Store tracker = new SqlTracker()) {
            tracker.init();
            tracker.add(new Item(7,"items7"));
            System.out.println(Arrays.toString(tracker.findAll().toArray()));
            System.out.println(tracker.findById(7).toString());
            System.out.println(tracker.findByName("items7").toString());
            tracker.replace(7, new Item(123,"items7+changed"));
            System.out.println(Arrays.toString(tracker.findAll().toArray()));
            tracker.delete(7);
            System.out.println(Arrays.toString(tracker.findAll().toArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        try (InputStream in = SqlTracker.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }

    @Override
    public Item add(Item item) {
        try (PreparedStatement st = this.cn.prepareStatement("insert into items(id, name) values(?, ?);")) {
            st.setInt(1, item.getId());
            st.setString(2, item.getName());
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public boolean replace(Integer id, Item item) {
        boolean result = false;
        try (PreparedStatement st = this.cn.prepareStatement("update items set name=(?) where id=(?);")) {
            st.setString(1, item.getName());
            st.setInt(2, id);
            result = st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean delete(Integer id) {
        boolean result = false;
        try (PreparedStatement st = this.cn.prepareStatement("delete from items where id=(?);")) {
            st.setInt(1, id);
            st.executeUpdate();
            result = st.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Item> findAll() {
        List<Item> result = new ArrayList<>();
        try (PreparedStatement st = this.cn.prepareStatement("select * from items;")) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    result.add(new Item(rs.getInt("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Item> findByName(String key) {
        List<Item> result = new ArrayList<>();
        try (PreparedStatement st = this.cn.prepareStatement("select * from items where name=?")) {
            st.setString(1, key);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    result.add(new Item(rs.getInt("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Item findById(Integer id) {
        Item result = null;
        try (PreparedStatement st = this.cn.prepareStatement("select * from items where id=?")) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    result = new Item(rs.getInt("id"), rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
