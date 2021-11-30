package tableviz.tableviz;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

class TableRow {
    public final Map<String, String> data;

    TableRow(Map<String, String> data_) {
        data = data_;
    }
}

class TableData {
    public final TableRow columns;
    public final Vector<TableRow> data;

    TableData(TableRow columns_, Vector<TableRow> data_) {
        columns = columns_;
        data = data_;
    }
}

public class SQLHandler {
    private Connection connection = null;

    public void openConnection(String db, String username, String password) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost/" + db, username, password);
    }

    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public Vector<String> getTables() throws SQLException {
        assert connection != null;

        Statement statement = connection.createStatement();
        ResultSet set = statement.executeQuery("SHOW TABLES");

        Vector<String> values = new Vector<>();
        while (set.next()) {
            values.add(set.getString(1));
        }

        return values;
    }

    public TableData getTableData(String tableName) throws SQLException {
        Map<String, String> columns = new HashMap<>();
        Vector<TableRow> rows = new Vector<>();

        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery("SELECT * FROM " + tableName);

        ResultSetMetaData metaData = result.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columns.put(metaData.getColumnName(i), "");
        }

        while (result.next()) {
            Map<String, String> data = new HashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                data.put(metaData.getColumnName(i), result.getString(i));
            }
            rows.add(new TableRow(data));
        }

        return new TableData(new TableRow(columns), rows);
    }

    public void deleteRow(String tableName, Map<String, String> values) throws SQLException {
        Statement statement = connection.createStatement();
        StringBuilder query = new StringBuilder("DELETE FROM `" + tableName + "` WHERE ");
        int i = 0;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            query.append("`").append(entry.getKey()).append("` = '").append(entry.getValue()).append("'");
            if (i < values.size() - 1) {
                query.append(" AND ");
            }
            i++;
        }
        query.append(" LIMIT 1");
        statement.executeUpdate(query.toString());
    }

    public void updateRow(String tableName, Map<String, String> oldValues, Map<String, String> newValues) throws SQLException {
        assert oldValues.size() == newValues.size();

        long ineqCount =
                oldValues.entrySet().stream().filter(e -> !e.getValue().equals(newValues.get(e.getKey()))).count();
        if (ineqCount == 0) {
            return;
        }

        StringBuilder query = new StringBuilder("UPDATE `" + tableName + "` SET ");
        int j = 0;
        for (Map.Entry<String, String> e : oldValues.entrySet()) {
            if (!e.getValue().equals(newValues.get(e.getKey()))) {
                query.append("`").append(e.getKey()).append("` = ").append("'").append(newValues.get(e.getKey())).append("'");
                if (j < ineqCount - 1) {
                    query.append(", ");
                }
                j++;
            }
        }

        query.append(" WHERE ");
        int i = 0;
        for (Map.Entry<String, String> e : oldValues.entrySet()) {
            query.append("`").append(e.getKey()).append("` = ").append("'").append(e.getValue()).append("'");
            if (i < oldValues.size() - 1) {
                query.append(" AND ");
            }
            i++;
        }
        query.append(" LIMIT 1");
        connection.createStatement().executeUpdate(query.toString());
    }

    public void beginTransaction() throws SQLException {
        connection.createStatement().execute("START TRANSACTION");
    }

    public void commitChanges() throws SQLException {
        connection.createStatement().execute("COMMIT");
    }

    public void discardChanges() throws SQLException {
        connection.createStatement().execute("ROLLBACK");
    }

    public void deleteTable(String tableName) throws SQLException {
        connection.createStatement().execute("DROP TABLE `" + tableName + "`");
    }

    public void insertIntoTable(String tableName, Vector<String> values) throws SQLException {
        StringBuilder query = new StringBuilder("INSERT INTO `" + tableName + "` VALUES(");
        int i = 0;
        for (String str : values) {
            query.append("'").append(str).append("'");
            if (i < values.size() - 1) {
                query.append(", ");
            }
            i++;
        }
        query.append(")");
        connection.createStatement().executeUpdate(query.toString());
    }
}
