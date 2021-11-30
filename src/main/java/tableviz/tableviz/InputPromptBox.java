package tableviz.tableviz;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class InputPromptBox {
    private final InputPromptOnSubmit onSubmit;
    private String[] prompts = null;
    private int passwordPrompt = -1;
    private Integer[] textAreas = null;
    private boolean isCancellable = false;
    private HashMap<Integer, String> defaultPrompts = new HashMap<>();

    InputPromptBox(InputPromptOnSubmit onSubmit_) {
        onSubmit = onSubmit_;
    }

    public void setPrompts(String... prompts_) {
        prompts = prompts_;
    }

    public void setTextAreas(Integer... areas) { textAreas = areas; }

    public void addDefaultPrompt(Integer index, String prompt) {
        defaultPrompts.put(index, prompt);
    }

    public void setPasswordInput(int passwordPrompt_) {
        passwordPrompt = passwordPrompt_;
    }

    private void setControllerValuesAndShow(InputPromptController controller, Scene scene) {
        controller.setPasswordInput(passwordPrompt);
        if (textAreas != null) {
            controller.setTextAreas(textAreas);
        }
        controller.setDefaultPrompts(defaultPrompts);
        controller.setPrompts(prompts);
        controller.setOnSubmit(onSubmit);
        controller.setCancellable(isCancellable);

        if (isCancellable) {
            controller.showCancelButton();
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Input");
        stage.show();
        stage.setResizable(false);
        controller.setStage(stage);
    }

    public void show(String kind) throws IOException {
        if (kind.equals("InputPromptController")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("input-prompt-view.fxml"));
            Scene scene = new Scene(loader.load());
            InputPromptController controller = loader.getController();
            setControllerValuesAndShow(controller, scene);
        } else if (kind.equals("WhereClauseInputPromptController")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("where-clause-input-prompt-view.fxml"));
            Scene scene = new Scene(loader.load());
            WhereClauseInputPromptController controller = loader.getController();
            setControllerValuesAndShow(controller, scene);
        }
    }

    public void setCancellable(boolean value) {
        isCancellable = value;
    }

    public boolean getCancellable() {
        return isCancellable;
    }
}
