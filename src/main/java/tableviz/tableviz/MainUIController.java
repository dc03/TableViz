package tableviz.tableviz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    @FXML
    private GridPane rowDetails = new GridPane();
    @FXML
    private GridPane tableControls = new GridPane();

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
        prompt.setCancellable(true);

        try {
            prompt.show();
        } catch (IOException e) {
            TableViz.panic(e);
        }

    }

    private void showHelp(ActionEvent event) {
        Dialog<String> dialog = new Dialog<String>();
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("help.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("help");

        ButtonType button = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);

        HBox container = new HBox();

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        ImageView logo = new ImageView(getClass().getResource("logo.png").toExternalForm());
        logo.setPreserveRatio(true);
        logo.setFitHeight(150);

        Label info = new Label("TableViz - A MySQL table visualizer (v" + TableViz.version + ")");
        info.getStyleClass().add("info");
        Label sources = new Label("Sources hosted at:");
        Hyperlink link = new Hyperlink("https://github.com/dc03/tableviz");
        link.getStyleClass().add("link");
        link.setOnAction(t -> main.getHostServices().showDocument(link.getText()));
        Label copyright = new Label("Copyright (C) 2021 Dhruv Chawla");
        copyright.getStyleClass().add("copyright");
        Label license = new Label("Licensed under the MIT license");
        license.getStyleClass().add("license");

        vbox.getChildren().addAll(info, sources, link, copyright, license);
        container.getChildren().addAll(logo, vbox);

        dialog.getDialogPane().getButtonTypes().add(button);
        dialog.getDialogPane().setContent(container);
        dialog.showAndWait();
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
        MenuItem helpButton = new MenuItem("About");
        helpButton.setOnAction(this::showHelp);
        help.getItems().addAll(helpButton);
    }

    @FXML
    private void initialize() {
        setupFileMenu();
        setupOptionsMenu();
        setupHelpMenu();

        tableView.setMinWidth(550);
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
            tableView.setOnMouseClicked(mouseEvent -> {
                Map<String, String> row = (Map<String, String>) tableView.getSelectionModel().getSelectedItem();
                GridPane details = new GridPane();
                int i = 0;
                for (Map.Entry<String, String> attr : row.entrySet()) {
                    Label column = new Label(attr.getKey());
                    TextField value = new TextField(attr.getValue());
                    Button edit = new Button("Edit");
                    HBox valueContainer = new HBox(value);
                    valueContainer.getStyleClass().add("value-container");
                    column.getStyleClass().add("row-details-column");
                    value.getStyleClass().add("row-details-value");

                    edit.getStyleClass().add("button");
                    edit.setOnAction(event -> {
                        InputPromptBox box = new InputPromptBox(controller -> {
                            String newValue = controller.getValues().get(0);
                            value.setText(newValue);
                        });
                        box.setPrompts(column.getText());
                        box.setCancellable(true);
                        try {
                            box.show();
                        } catch (IOException e) {
                            TableViz.panic(e);
                        }
                    });

                    details.add(column, 0, i);
                    details.add(new Label("="), 1, i);
                    details.add(valueContainer, 2, i);
                    details.add(edit, 3, i);
                    i++;
                }
                details.getStyleClass().add("row-details");

                Button delete = new Button("Delete");
                delete.setOnAction(event -> {
                    handler.deleteRow(row);
                    loadTable(table);
                });
                Button update = new Button("Update");
                update.setOnAction(event -> {
                });

                HBox buttons = new HBox();
                buttons.setAlignment(Pos.CENTER);
                buttons.getChildren().addAll(update, delete);
                buttons.getStyleClass().add("buttons");
                buttons.setSpacing(10);

                VBox vbox = new VBox(details, buttons);
                rowDetails.getChildren().add(vbox);
                rowDetails.setVisible(true);
            });
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
                table = tableList.getSelectionModel().getSelectedItem();
                loadTable(table);
                tableControls.setVisible(true);
                rowDetails.setVisible(false);
                rowDetails.getChildren().clear();
                mainView.setBottom(data_controls);
            });
        } catch (SQLException e) {
            new ErrorBox("Warning", "Could not load table names", TableViz.formatLongSQLError(e), false).show();
        }
    }

    private final HBox data_controls = new HBox();
    private final Menu file = new Menu("File");
    private final Menu options = new Menu("Options");
    private final Menu help = new Menu("Help");
    private String table = "";

    public Stage stage = null;
    public SQLHandler handler = null;
    public TableViz main = null;
}
