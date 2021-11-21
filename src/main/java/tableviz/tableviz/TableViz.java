package tableviz.tableviz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;
import java.io.IOException;
import java.util.Arrays;

public class TableViz extends Application {
    public static String version = "0.0.1";

    public static void panic(Exception e) {
        System.err.println("[PANIC] Stack trace:");
        e.printStackTrace();
        System.err.println("[PANIC] Error: " + e.getMessage());
        System.err.println("[PANIC] EXITING");
        System.exit(0);
    }

    public static String formatLongSQLError(SQLException ex) {
        return "SQLException : " + ex.getMessage() + "\nSQLState     : " + ex.getSQLState()
                + "\nVendor Error : " + ex.getErrorCode();
    }

    private void showLoginWindow(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.stage = stage;
        controller.main = this;

        stage.setScene(scene);
        stage.setTitle("Login");
        stage.resizableProperty().set(false);
        stage.show();
    }

    @Override
    public void start(Stage stage) throws Exception {
        showLoginWindow(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
