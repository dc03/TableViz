package tableviz.tableviz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.HashMap;
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
    @FXML
    private Button cancelButton = new Button();

    @FXML
    private void submit(ActionEvent event) {
        onSubmit.onSubmit(this);
        stage.hide();
    }

    @FXML
    private void cancel(ActionEvent event) {
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
        int i = 0;
        for (String name : names) {
            labels.add(new Label(name));
            if (i == passwordPrompt) {
                inputs.add(new PasswordField());
            } else if (textAreas.contains(i)) {
                inputs.add(new TextArea());
                if (defaultPrompts.containsKey(i)) {
                    ((TextArea)(inputs.lastElement())).setText(defaultPrompts.get(i));
                }
            } else {
                inputs.add(new TextField());
                if (defaultPrompts.containsKey(i)) {
                    ((TextField)(inputs.lastElement())).setText(defaultPrompts.get(i));
                }
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

    public void setTextAreas(Integer[] areas) {
        if (areas.length > 0) {
            Collections.addAll(textAreas, areas);
        }
    }

    public void setDefaultPrompts(HashMap<Integer, String> defaults) {
        defaultPrompts = defaults;
    }

    public Vector<String> getValues() {
        Vector<String> values = new Vector<>();
        int i = 0;
        for (Node node : inputs) {
            if (i == passwordPrompt) {
                values.add(((PasswordField) node).getText());
            } else if (textAreas.contains(i)) {
                values.add(((TextArea) node).getText());
            } else {
                values.add(((TextField) node).getText());
            }
            i++;
        }

        return values;
    }

    public void setCancellable(boolean value) {
        isCancellable = value;
    }

    public boolean getCancellable() {
        return isCancellable;
    }

    public void showCancelButton() {
        cancelButton.setVisible(true);
    }

    private final Vector<Label> labels = new Vector<>();
    private final Vector<Node> inputs = new Vector<>();
    private InputPromptOnSubmit onSubmit = null;
    private boolean isCancellable = false;
    private int passwordPrompt = -1;
    private Vector<Integer> textAreas = new Vector<>();
    private HashMap<Integer, String> defaultPrompts = new HashMap<>();

    public Stage stage = null;
}
