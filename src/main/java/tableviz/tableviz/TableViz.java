package tableviz.tableviz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;
import java.io.IOException;
import java.util.Arrays;

public class TableViz extends Application {
    //    public void start(Stage stage) throws IOException {
//        Statement stmt = null;
//        ResultSet rs = null;
//
//        try {
//            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/mytables_dc?user=dc&password=pass");
//
//            stmt = conn.createStatement();
//            rs = stmt.executeQuery("SHOW TABLES");
//
//            FXMLLoader fxmlLoader = new FXMLLoader(TableViz.class.getResource("hello-view.fxml"));
//            Scene scene = new Scene(fxmlLoader.load());
//
//            stage.setTitle("TableViz");
//            stage.setScene(scene);
//            stage.show();
//        } catch (SQLException ex) {
//            System.out.println("Stack trace:");
//            ex.printStackTrace();
//            System.out.println("SQLException: " + ex.getMessage());
//            System.out.println("SQLState: " + ex.getSQLState());
//            System.out.println("VendorError: " + ex.getErrorCode());
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (rs != null) {
//                try {
//                    rs.close();
//                } catch (Exception ignored) {
//                }
//            }
//            if (stmt != null) {
//                try {
//                    stmt.close();
//                } catch (Exception ignored) {
//                }
//            }
//        }
//
//    }
    public static void panic(Exception e) {
        System.err.println("[PANIC] Stack trace:");
        e.printStackTrace();
        System.err.println("[PANIC] Error: Cannot create error message: " + e.getMessage());
        System.err.println("[PANIC] EXITING");
        System.exit(0);
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