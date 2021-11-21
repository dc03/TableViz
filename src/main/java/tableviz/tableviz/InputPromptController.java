package tableviz.tableviz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Vector;

interface InputPromptOnSubmit {
    void onSubmit(InputPromptController controller);
}

public class InputPromptController {
    @FXML
    private VBox mainContainer = new VBox();
    @FXML
    private GridPane container = new GridPane();
    @FXML
    private Button submitButton = new Button();

    Vector<Label> labels = new Vector<>();
    Vector<Node> inputs = new Vector<>();
    InputPromptOnSubmit onSubmit = null;
    Stage stage = null;

    int passwordPrompt = -1;
    int numPrompts;


    @FXML
    private void submit(ActionEvent event) {
        onSubmit.onSubmit(this);
        stage.hide();
    }

    public void setPasswordInput(int index) {
        passwordPrompt = index;
    }

    public void setStage(Stage stage_) {
        stage = stage_;
    }

    public void setOnSubmit(InputPromptOnSubmit onSubmit_) {
        onSubmit = onSubmit_;
    }

    public void setPrompts(String... names) {
        numPrompts = names.length;
        int i = 0;
        for (String name : names) {
            labels.add(new Label(name));
            if (i == passwordPrompt) {
                inputs.add(new PasswordField());
            } else {
                inputs.add(new TextField());
            }
            i++;
        }

        for (int j = 0; j < labels.size(); j++) {
            container.add(labels.get(j), 0, j);
            Label jank = new Label();
            jank.getStyleClass().add("jank");
            container.add(jank, 1, j);
        }
        for (int j = 0; j < inputs.size(); j++) {
            container.add(inputs.get(j), 2, j);
        }

    }

    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("input-prompt-view.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.show();
    }

    public Vector<String> getValues() {
        Vector<String> values = new Vector<>();
        int i = 0;
        for (Node node : inputs) {
            if (i == passwordPrompt) {
                values.add(((PasswordField) node).getText());
            } else {
                values.add(((TextField) node).getText());
            }
            i++;
        }

        return values;
    }
}
