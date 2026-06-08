package org.example;

import java.sql.*;
import java.util.*;

public class MySqlHelper {

    private static final String URL =
            "jdbc:mysql://localhost:3306/student_system?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "YourPassword";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public int executeUpdate(String sql, Object... params) {
        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            setParams(ps, params);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("执行更新失败：" + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> result = new ArrayList<>();

        try (
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            setParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    result.add(row);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("执行查询失败：" + e.getMessage(), e);
        }

        return result;
    }

    public Map<String, Object> executeQueryOne(String sql, Object... params) {
        List<Map<String, Object>> list = executeQuery(sql, params);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    private void setParams(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) {
            return;
        }

        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}