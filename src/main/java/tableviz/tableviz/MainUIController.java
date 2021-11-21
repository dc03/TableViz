package tableviz.tableviz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

public class MainUIController {
    @FXML
    private BorderPane mainView = new BorderPane();
    @FXML
    private MenuBar mainMenu = new MenuBar();
    @FXML
    private ListView<String> tableList = new ListView<>();
    @FXML
    private TableView<Map> tableView = new TableView<>();

    private HBox data_controls = new HBox();
    private Menu file = new Menu("File");
    private Menu options = new Menu("Options");
    private Menu help = new Menu("Help");

    private void exit(ActionEvent event) {
        stage.hide();
        if (handler != null) {
            try {
                handler.closeConnection();
            } catch (SQLException e) {
                new ErrorBox("Error", "Could not close MySQL connection", TableViz.formatLongSQLError(e), true);
            }
        }
        System.exit(0);
    }

    private void switchDB(ActionEvent event) {
        InputPromptBox prompt = new InputPromptBox(inputPrompt -> {
            Vector<String> values = inputPrompt.getValues();
            SQLHandler handler = new SQLHandler();
            try {
                String db = values.get(0);
                String username = values.get(1);
                String password = values.get(2);

                if (db.isEmpty() || username.isEmpty()) {
                    new ErrorBox("Error",
                            (db.isEmpty() ? "Database name cannot be empty" : "Username cannot be empty"), "", false).show();
                } else {
                    handler.openConnection(db, username, password);
                    initializeTableList(handler);
                    tableView.getItems().clear();
                    tableView.getColumns().clear();
                }
            } catch (SQLException e) {
                new ErrorBox("Error", "Could not open MySQL connection", TableViz.formatLongSQLError(e), false).show();
            }
        });
        prompt.setPasswordInput(2);
        prompt.setPrompts("Database name", "Username", "Password");

        try {
            prompt.show();
        } catch (IOException e) {
            TableViz.panic(e);
            new ErrorBox("Error", e.getMessage(), "", false).show();
        }

    }

    private void setupFileMenu() {
        MenuItem exitButton = new MenuItem("Exit");
        MenuItem switchDatabase = new MenuItem("Switch databases");
        exitButton.setOnAction(this::exit);
        switchDatabase.setOnAction(this::switchDB);
        file.getItems().addAll(switchDatabase, new SeparatorMenuItem(), exitButton);
    }

    private void setupOptionsMenu() {

    }

    private void setupHelpMenu() {

    }

    @FXML
    private void initialize() {
        setupFileMenu();
        setupOptionsMenu();
        setupHelpMenu();

        mainMenu.getMenus().addAll(file, options, help);
    }

    @FXML
    private void loadTable(String tableName) {
        try {
            TableData tableData = handler.getTableData(tableName);
            ObservableList<Map> data = FXCollections.observableArrayList();

            for (TableRow row : tableData.data) {
                data.add(row.data);
            }

            Vector<TableColumn<Map, String>> columns = new Vector<>();
            for (Map.Entry<String, String> column : tableData.columns.data.entrySet()) {
                TableColumn<Map, String> col = new TableColumn<>(column.getKey());
                col.setCellValueFactory(new MapValueFactory<>(column.getKey()));
                columns.add(col);
            }

            tableView.setItems(data);
            tableView.getColumns().setAll(columns);
        } catch (SQLException e) {
            new ErrorBox("Warning", "Could not load table data", TableViz.formatLongSQLError(e), false).show();
        }
    }

    @FXML
    public void initializeTableList(SQLHandler handler) {
        this.handler = handler;
        try {
            Vector<String> tables = handler.getTables();

            ObservableList<String> content = FXCollections.observableArrayList();
            content.addAll(tables);
            tableList.setItems(content);
            tableList.setOnMouseClicked(mouseEvent -> {
                loadTable(tableList.getSelectionModel().getSelectedItem());
                mainView.setBottom(data_controls);
            });
        } catch (SQLException e) {
            new ErrorBox("Warning", "Could not load table names", TableViz.formatLongSQLError(e), false).show();
        }
    }

    public Stage stage = null;
    public SQLHandler handler = null;
}
