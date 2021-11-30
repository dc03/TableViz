package tableviz.tableviz;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.Vector;

public class WhereClauseInputPromptController extends InputPromptController {
    @Override
    public void setPrompts(String... names) {
        createLabelsAndInputs(names);
        addLabelsToContainer();

        for (int j = 0; j < labels.size(); j++) {
            HBox hbox = new HBox();
            ChoiceBox<String> cb = new ChoiceBox<>(FXCollections.observableArrayList(
                    "<", "<=", "=", "!=", ">=", ">", "LIKE"
            ));
            cb.getSelectionModel().select(0);
            hbox.getChildren().add(cb);
            hbox.getStyleClass().add("cb-container");
            container.add(hbox, 2, j);
            Label jank = new Label();
            jank.getStyleClass().add("jank2");
            container.add(jank, 3, j);
        }

        for (int j = 0; j < inputs.size(); j++) {
            container.add(inputs.get(j), 4, j);
        }
    }

    @Override
    public Vector<String> getValues() {
        Vector<String> values = new Vector<>();
        int i = 0;
        FilteredList<Node> cbs = container.getChildren().filtered(e -> e instanceof HBox);
        for (Node node : inputs) {
            HBox box = (HBox)(cbs.get(i));
            ChoiceBox<String> cb = (ChoiceBox<String>)(box.getChildren().get(0));
            String v = cb.getItems().get(cb.getSelectionModel().getSelectedIndex());
            if (i == passwordPrompt) {
                String text = ((PasswordField) node).getText();
                if (!text.isEmpty()) {
                    values.add(" " + v + " " + text);
                } else {
                    values.add("");
                }
            } else if (textAreas.contains(i)) {
                String text = ((TextArea) node).getText();
                if (!text.isEmpty()) {
                    values.add(" " + v + " " + text);
                } else {
                    values.add("");
                }
            } else {
                String text = ((TextField) node).getText();
                if (!text.isEmpty()) {
                    values.add(" " + v + " " + text);
                } else {
                    values.add("");
                }
            }
            i++;
        }

        return values;
    }
}
