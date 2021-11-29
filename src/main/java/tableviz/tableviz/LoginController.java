package tableviz.tableviz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML
    ImageView logo = new ImageView();

    @FXML
    private Label loginPrompt = new Label();

    @FXML
    private Label dbNamePrompt = new Label();
    @FXML
    private TextField dbNameInput = new TextField();

    @FXML
    private Label userNamePrompt = new Label();
    @FXML
    private TextField userNameInput = new TextField();

    @FXML
    private Label passwordPrompt = new Label();
    @FXML
    private PasswordField passwordInput = new PasswordField();

    @FXML
    public void submit(ActionEvent actionEvent) {
        if (dbNameInput.getText().isBlank()) {
            Alert dialog = new Alert(Alert.AlertType.ERROR, "Database name cannot be empty");
            dialog.getDialogPane().getStylesheets().addAll(
                    getClass().getResource("button.css").toExternalForm(),
                    getClass().getResource("login-view.css").toExternalForm());
            dialog.getDialogPane().setStyle("-fx-background-color: white");
            dialog.showAndWait();
            return;
        } else if (userNameInput.getText().isBlank()) {
            Alert dialog = new Alert(Alert.AlertType.ERROR, "Username cannot be empty");
            dialog.getDialogPane().getStylesheets().addAll(
                    getClass().getResource("button.css").toExternalForm(),
                    getClass().getResource("login-view.css").toExternalForm());
            dialog.getDialogPane().setStyle("-fx-background-color: white");
            dialog.showAndWait();
            return;
        }

        SQLHandler handler = new SQLHandler();
        try {
            handler.openConnection(dbNameInput.getText(), userNameInput.getText(), passwordInput.getText());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Scene scene = new Scene(loader.load());
            MainUIController ui = loader.getController();
            ui.stage = stage;
            ui.main = main;
            ui.stage.setResizable(true);
            ui.initializeTableList(handler);

            stage.hide();
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.setTitle("TableViz");
            stage.show();
        } catch (SQLException ex) {
            new ErrorBox("Error", "Could not open MySQL connection",
                    TableViz.formatLongSQLError(ex), true).show();
        } catch (IOException e) {
            TableViz.panic(e);
        }
    }

    public Stage stage = null;
    public TableViz main = null;
}
