package tableviz.tableviz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class LoginController {
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
    private Label errorLabel = new Label();

    @FXML
    public void submit(ActionEvent actionEvent) {
        if (dbNameInput.getText().isBlank()) {
            errorLabel.setText("Error: Database name cannot be empty");
            return;
        } else if (userNameInput.getText().isBlank()) {
            errorLabel.setText("Error: Username cannot be empty");
            return;
        }

        errorLabel.setText("");
        SQLHandler handler = new SQLHandler();
        try {
            handler.openConnection(dbNameInput.getText(), userNameInput.getText(), passwordInput.getText());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        } catch (SQLException ex) {
            errorLabel.setText("Error: " + ex.getMessage());
        }
    }

    public Stage stage = null;
    public TableViz main = null;
}
