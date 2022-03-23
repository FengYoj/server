package com.jemmy.framework.component.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    private final Connection conn;

    private Statement statement = null;

    public Database(String url, String username, String password) throws SQLException {
        conn = DriverManager.getConnection(url, username, password);
    }

    public ResultSet getResultSet(String sql) throws SQLException {
        Statement statement = getStatement();

        return statement.executeQuery(sql);
    }

    public List<Map<String, Object>> getList(String sql) throws SQLException {
        var rs = getResultSet(sql);

        List<Map<String, Object>> res = new ArrayList<>();

        List<String> keys = new ArrayList<>();

        ResultSetMetaData rsMeta = rs.getMetaData();

        for (int i = 1; i < rsMeta.getColumnCount(); i++) {
            keys.add(rsMeta.getColumnName(i));
        }

        while(rs.next()) {
            Map<String, Object> map = new HashMap<>();

            for (int i = 0; i < keys.size(); i++) {
                map.put(keys.get(i), rs.getObject(i + 1));
            }

            res.add(map);
        }

        return res;
    }

    public int update(String sql) throws SQLException {
        Statement statement = getStatement();

        return statement.executeUpdate(sql);
    }

    private Statement getStatement() throws SQLException {
        if (statement != null) {
            return statement;
        }

        statement = conn.createStatement();

        return statement;
    }
}
