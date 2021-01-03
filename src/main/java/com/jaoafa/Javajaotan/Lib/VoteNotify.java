package com.jaoafa.Javajaotan.Lib;

import com.jaoafa.Javajaotan.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VoteNotify {
    long userid;
    String mcjp_type = null;
    int mcjp_time = -1;
    String mono_type = null;
    int mono_time = -1;

    public VoteNotify(long userid) throws SQLException {
        this.userid = userid;

        fetch();
    }

    public void fetch() throws SQLException {
        MySQLDBManager MySQLDBManager = Main.MySQLDBManager;

        if (MySQLDBManager == null) {
            return;
        }

        Connection conn = MySQLDBManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM votenotify WHERE disid = ?");
        stmt.setLong(1, userid);
        ResultSet res = stmt.executeQuery();
        while (res.next()) {
            String service = res.getString("service");
            int time = res.getInt("time");
            String type = res.getString("type");

            if (service.equals("mcjp")) {
                mcjp_time = time;
                mcjp_type = type;
            } else if (service.equals("mono")) {
                mono_time = time;
                mono_type = type;
            }
        }
        res.close();
        stmt.close();
    }

    public int getMCJPTime() {
        return mcjp_time;
    }

    public String getMCJPType() {
        return mcjp_type;
    }

    public int getMONOTime() {
        return mono_time;
    }

    public String getMONOType() {
        return mono_type;
    }

    public void enable(String service, int time, String type) throws SQLException {
        if (checkService(service)) {
            throw new IllegalArgumentException("service");
        }
        if (checkType(type)) {
            throw new IllegalArgumentException("type");
        }
        MySQLDBManager MySQLDBManager = Main.MySQLDBManager;

        if (MySQLDBManager == null) {
            return;
        }
        Connection conn = MySQLDBManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM votenotify WHERE disid = ? AND service = ?");
        stmt.setLong(1, userid);
        stmt.setString(2, service);
        ResultSet res = stmt.executeQuery();
        if (res.next()) {
            // exist -> update
            PreparedStatement update_stmt = conn.prepareStatement("UPDATE votenotify SET time = ?, type = ? WHERE disid = ? AND service = ?");
            update_stmt.setInt(1, time);
            update_stmt.setString(2, type);
            update_stmt.setLong(3, userid);
            update_stmt.setString(4, service);
            update_stmt.executeUpdate();
            update_stmt.close();
        } else {
            PreparedStatement insert_stmt = conn.prepareStatement("INSERT INTO votenotify (disid, service, time, type, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)");
            insert_stmt.setLong(1, userid);
            insert_stmt.setString(2, service);
            insert_stmt.setInt(3, time);
            insert_stmt.setString(4, type);
            insert_stmt.executeUpdate();
            insert_stmt.close();
        }
        res.close();
        stmt.close();
    }

    public void disable(String service) throws SQLException {
        if (checkService(service)) {
            throw new IllegalArgumentException("service");
        }
        MySQLDBManager MySQLDBManager = Main.MySQLDBManager;

        if (MySQLDBManager == null) {
            return;
        }
        Connection conn = MySQLDBManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM votenotify WHERE disid = ? AND service = ?");
        stmt.setLong(1, userid);
        stmt.setString(2, service);
        ResultSet res = stmt.executeQuery();
        if (res.next()) {
            PreparedStatement update_stmt = conn.prepareStatement("DELETE FROM votenotify WHERE disid = ? AND service = ?");
            stmt.setLong(1, userid);
            stmt.setString(2, service);
            update_stmt.executeUpdate();
            update_stmt.close();
        }
    }

    public boolean checkService(String service) {
        return service.equals("mcjp") || service.equals("mono");
    }

    public boolean checkType(String type) {
        return type.equals("everyday") || type.equals("before");
    }
}
