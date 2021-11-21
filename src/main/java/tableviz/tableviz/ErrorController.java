package tableviz.tableviz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ErrorController {
    @FXML
    private Label heading = new Label();
    @FXML
    private Text shortReason = new Text();
    @FXML
    private VBox outerContainer = new VBox();
    @FXML
    private Line hline = new Line();
    @FXML
    private Text longReason = new Text();
    @FXML
    private Button ok = new Button();

    @FXML
    private void initialize() {
        ok.setText("Ok");
    }

    public void setHeading(String value) {
        heading.setText(value);
        stage.setTitle(value);
    }

    public void setShortReason(String reason) {
        shortReason.setText(reason);
    }

    public void setLongReason(String reason) {
        if (reason.equals("")) {
            outerContainer.setVisible(false);
        } else {
            longReason.setText(reason);
        }
    }

    public void onOkClick(ActionEvent actionEvent) {
        stage.hide();
        if (shouldExit) {
            System.exit(0);
        }
    }

    public void setStageResize() {
        stage.setResizable(false);
    }

    public void setLineWidth() {
        hline.setEndX(400);
    }

    public boolean shouldExit = false;
    public Stage stage = null;
}
