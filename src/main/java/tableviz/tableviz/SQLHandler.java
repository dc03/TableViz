package tableviz.tableviz;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

class TableRow {
    public Map<String, String> data;

    TableRow(Map<String, String> data_) {
        data = data_;
    }
}

class TableData {
    public TableRow columns;
    public Vector<TableRow> data;

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
}
