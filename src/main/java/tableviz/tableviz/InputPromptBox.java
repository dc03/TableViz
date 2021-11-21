package tableviz.tableviz;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class InputPromptBox {
    InputPromptOnSubmit onSubmit;
    String[] prompts = null;
    int passwordPrompt = -1;

    InputPromptBox(InputPromptOnSubmit onSubmit_) {
        onSubmit = onSubmit_;
    }

    public void setPrompts(String... prompts_) {
        prompts = prompts_;
    }

    public void setPasswordInput(int passwordPrompt_) {
        passwordPrompt = passwordPrompt_;
    }

    public void show() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("input-prompt-view.fxml"));
        Scene scene = new Scene(loader.load());
        InputPromptController controller = loader.getController();
        controller.setPasswordInput(passwordPrompt);
        controller.setPrompts(prompts);
        controller.setOnSubmit(onSubmit);

        Stage stage = new Stage();
        controller.setStage(stage);
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }
}
