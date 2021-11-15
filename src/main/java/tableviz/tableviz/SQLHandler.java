package tableviz.tableviz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHandler {
    Connection connection = null;

    public void openConnection(String db, String username, String password) throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost/" + db, username, password);
    }
}
