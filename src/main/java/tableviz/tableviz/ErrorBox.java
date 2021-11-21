package tableviz.tableviz;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ErrorBox {
    private final String heading;
    private final String shortReason;
    private final String longReason;
    private final boolean shouldExit;

    ErrorBox(String heading_, String shortReason_, String longReason_, boolean shouldExit_) {
        heading = heading_;
        shortReason = shortReason_;
        longReason = longReason_;
        shouldExit = shouldExit_;
    }

    public void show() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("error-view.fxml"));
        try {
            Scene scene = new Scene(loader.load());
            Stage stage2 = new Stage();

            ErrorController controller = loader.getController();
            controller.shouldExit = shouldExit;
            controller.stage = stage2;

            controller.setHeading(heading);
            controller.setShortReason(shortReason);
            controller.setLongReason(longReason);
            controller.setStageResize();
            controller.setLineWidth();

            stage2.setScene(scene);
            stage2.show();
        } catch (IOException e) {
            TableViz.panic(e);
        }
    }
}
