package com.jaoafa.Javajaotan.Lib;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDBManager {
    String jdbcUrl;
    Connection conn = null;

    public SQLiteDBManager(String dbfile) throws ClassNotFoundException, IOException {
        Class.forName("org.sqlite.JDBC");
        File file = new File(dbfile);
        if (file.exists() && file.canRead() && file.canWrite()) {
            jdbcUrl = "jdbc:sqlite:" + file.getAbsolutePath();
            return;
        } else {
            String parent = file.getParent();
            if (new File(parent).exists() && new File(parent).canWrite()) {
                // ok
                jdbcUrl = "jdbc:sqlite:" + file.getAbsolutePath();
                return;
            }
        }
        // err
        throw new IOException("You do not have access permission for the specified DB file.");
    }

    public SQLiteDBManager(File file) throws ClassNotFoundException, IOException {
        Class.forName("org.sqlite.JDBC");
        if (file.exists() && file.canRead() && file.canWrite()) {
            jdbcUrl = "jdbc:sqlite:" + file.getAbsolutePath();
            return;
        } else {
            String parent = file.getParent();
            if (new File(parent).exists() && new File(parent).canWrite()) {
                // ok
                jdbcUrl = "jdbc:sqlite:" + file.getAbsolutePath();
                return;
            }
        }
        // err
        throw new IOException("You do not have access permission for the specified DB file.");
    }

    public Connection getConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            return conn;
        }
        conn = DriverManager.getConnection(jdbcUrl);
        return conn;
    }
}
