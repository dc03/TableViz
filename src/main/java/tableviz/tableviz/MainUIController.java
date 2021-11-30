package tableviz.tableviz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    private VBox tableControls = new VBox();

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
                    stage.setTitle("TableViz");
                    tableControls.setVisible(false);
                    tableControls.getChildren().clear();
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

        if (refreshButton != null) {
            refreshButton.setDisable(true);
        }

        try {
            prompt.show("InputPromptController");
        } catch (IOException e) {
            TableViz.panic(e);
        }

    }

    private void showHelp(ActionEvent event) {
        Dialog<String> dialog = new Dialog<>();
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

    private void refreshDatabase(ActionEvent event) {
        try {
            Vector<String> tables = handler.getTables();
            tableControls.setVisible(false);
            tableControls.getChildren().clear();

            tableView.getItems().clear();
            tableView.getColumns().clear();
            setupTableList(tables);
        } catch (SQLException e) {
            new ErrorBox("Warning", "Could not load table names", TableViz.formatLongSQLError(e), false).show();
        }
    }

    private void setupTableList(Vector<String> tables) {
        ObservableList<String> content = FXCollections.observableArrayList();
        content.addAll(tables);
        tableControls.getChildren().clear();
        tableList.setItems(content);
        tableList.setOnMouseClicked(mouseEvent -> {
            table = tableList.getSelectionModel().getSelectedItem();
            loadTable(table);
            tableControls.setVisible(true);
            rowDetails.setVisible(false);
            rowDetails.getChildren().clear();

            if (refreshButton != null) {
                refreshButton.setDisable(false);
            }
        });
    }

    private void refreshTable(ActionEvent event) {
        loadTable(table);
    }

    private void setupFileMenu() {
        MenuItem exitButton = new MenuItem("Exit");
        MenuItem switchDatabase = new MenuItem("Switch databases");
        exitButton.setOnAction(this::exit);
        switchDatabase.setOnAction(this::switchDB);
        file.getItems().addAll(switchDatabase, new SeparatorMenuItem(), exitButton);
    }

    private void setupOptionsMenu() {
        MenuItem refreshDB = new MenuItem("Refresh Database");
        refreshDB.setOnAction(this::refreshDatabase);
        MenuItem refreshTable = new MenuItem("Refresh current table");
        refreshTable.setOnAction(this::refreshTable);
        refreshButton = refreshTable;
        refreshTable.setDisable(true);
        options.getItems().addAll(refreshDB, refreshTable);
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

    private Vector<TableColumn<Map, String>> setTableData(TableData tableData) {
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
        tableCols = columns;
        return columns;
    }

    private void setTableViewOnClick(String tableName) {
        tableView.setOnMouseClicked(mouseEvent -> {
            Map<String, String> row = (Map<String, String>) tableView.getSelectionModel().getSelectedItem();
            if (row == null) {
                return;
            }
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
                    box.setTextAreas(0);
                    box.addDefaultPrompt(0, value.getText());
                    box.setPrompts(column.getText());
                    box.setCancellable(true);
                    try {
                        box.show("InputPromptController");
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
                try {
                    handler.deleteRow(tableName, row);
                    tableView.getItems().remove(tableView.getSelectionModel().getSelectedIndex());
                    rowDetails.setVisible(false);
                    rowDetails.getChildren().clear();
                } catch (SQLException e) {
                    new ErrorBox("Error", "Could not delete row", TableViz.formatLongSQLError(e), false).show();
                }
            });
            Button update = new Button("Update");
            update.setOnAction(event -> {
                try {
                    Map<String, String> selected = (Map<String, String>) tableView.getSelectionModel().getSelectedItem();
                    HashMap<String, String> newValues = new HashMap<>();
                    ObservableList<Node> children = details.getChildren();
                    for (int j = 0; j < children.size(); j += 4) {
                        Label columnName = (Label) children.get(j);
                        TextField columnValue = (TextField) (((HBox) children.get(j + 2)).getChildren().get(0));
                        newValues.put(columnName.getText(), columnValue.getText());
                    }
                    handler.updateRow(table, selected, newValues);
                    tableView.getItems().remove(tableView.getSelectionModel().getSelectedIndex());
                    tableView.getItems().add(newValues);
                    tableView.getSelectionModel().select(newValues);
                } catch (SQLException e) {
                    new ErrorBox("Error", "Could not update row", TableViz.formatLongSQLError(e), false).show();
                }
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
    }

    private HBox setTableColumnHideButtons(TableData tableData, Vector<TableColumn<Map, String>> columns) {
        HBox columnChecks = new HBox();
        for (Map.Entry<String, String> column : tableData.columns.data.entrySet()) {
            CheckBox button = new CheckBox(column.getKey());
            button.setSelected(true);
            button.setOnAction(event -> {
                String target = button.getText();
                for (TableColumn<Map, String> column1 : columns) {
                    if (column1.getText().equals(target)) {
                        column1.setVisible(!column1.isVisible());
                    }
                }
            });
            columnChecks.getChildren().add(button);
        }
        columnChecks.getStyleClass().add("controls-columns");
        columnChecks.setAlignment(Pos.CENTER);
        columnChecks.setSpacing(20);
        return columnChecks;
    }

    @FXML
    private void loadTable(String tableName) {
        try {
            TableData tableData = handler.getTableData(tableName);

            Vector<TableColumn<Map, String>> columns = setTableData(tableData);
            setTableViewOnClick(tableName);

            HBox container = new HBox();
            HBox columnChecks = setTableColumnHideButtons(tableData, columns);

            container.getStyleClass().add("controls-buttons");
            container.setAlignment(Pos.CENTER);
            container.setSpacing(15);


            Button delete = new Button("Delete table");
            Button insert = new Button("Insert data");
            Button filter = new Button("Filter");
            Button transactEnable = new Button("Begin transaction");
            Button commit = new Button("Commit changes");
            Button discardChanges = new Button("Discard changes");
            commit.setDisable(true);
            discardChanges.setDisable(true);

            delete.setOnAction(event -> {
                VBox cont = new VBox();
                cont.setAlignment(Pos.CENTER);
                cont.getStylesheets().add(getClass().getResource("login-view.css").toExternalForm());

                Label warning1 = new Label("Are you sure you want to delete table '" + table + "'?");
                cont.getChildren().add(warning1);
                TextField input = new TextField();

                Label warning2 = new Label("Note: This action cannot be undone!");
                Label warning3 = new Label("Type 'Yes, do as I say!' in the box below to confirm");
                Label jank = new Label();
                jank.getStyleClass().add("jank");
                cont.getChildren().addAll(warning2, warning3, jank, input);

                ButtonType delete1 = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                Dialog<String> dialog = new Dialog<>();
                dialog.getDialogPane().getStylesheets().addAll(
                        getClass().getResource("button.css").toExternalForm(),
                        getClass().getResource("login-view.css").toExternalForm());
                dialog.getDialogPane().setStyle("-fx-background-color: white");
                dialog.getDialogPane().setContent(cont);
                dialog.getDialogPane().getButtonTypes().addAll(cancel, delete1);

                Button delete2 = (Button) dialog.getDialogPane().lookupButton(delete1);
                delete2.setOnAction(event1 -> {
                    if (input.getText().equals("Yes, do as I say!")) {
                        try {
                            handler.deleteTable(table);
                        } catch (SQLException e) {
                            new ErrorBox("Error", "Could not delete table", TableViz.formatLongSQLError(e), false).show();
                        }
                        refreshDatabase(null);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Could not delete table '" + table + "'");
                        alert.getDialogPane().getStylesheets().addAll(
                                getClass().getResource("button.css").toExternalForm(),
                                getClass().getResource("login-view.css").toExternalForm());
                        alert.getDialogPane().setStyle("-fx-background-color: white");
                        alert.setTitle("Error");
                        alert.showAndWait();
                    }
                });

                dialog.setTitle("Delete table");
                dialog.show();
            });

            insert.setOnAction(event -> {
                InputPromptBox box = new InputPromptBox(controller -> {
                    try {
                        handler.insertIntoTable(table, controller.getValues());
                        loadTable(table);
                    } catch (SQLException e) {
                        new ErrorBox("Error", "Could not insert values into table", TableViz.formatLongSQLError(e), false).show();
                    }
                });

                String[] values = new String[tableData.columns.data.size()];
                int i = 0;
                for (Map.Entry<String, String> column : tableData.columns.data.entrySet()) {
                    values[i++] = column.getKey();
                }
                box.setPrompts(values);
                try {
                    box.show("InputPromptController");
                } catch (IOException e) {
                    TableViz.panic(e);
                }
            });

            filter.setOnAction(event -> {
                String[] prompts = new String[columns.size()];
                for (int i = 0; i < prompts.length; i++) {
                    prompts[i] = columns.get(i).getText();
                }

                InputPromptBox box = new InputPromptBox(controller -> {
                    try {
                        HashMap<String, String> filters = new HashMap<>();
                        for (int i = 0; i < prompts.length; i++) {
                            if (!controller.getValues().get(i).isEmpty()) {
                                filters.put(prompts[i], controller.getValues().get(i));
                            }
                        }
                        TableData filtered;
                        if (filters.isEmpty()) {
                            filtered = handler.getTableData(table);
                        } else {
                            filtered = handler.filterValues(table, filters);
                        }
                        Vector<TableColumn<Map, String>> filteredColumns = setTableData(filtered);
                        Vector<TableColumn<Map, String>> temp = tableCols;
                        setTableViewOnClick(tableName);

                        int i = 0;
                        HBox checks = setTableColumnHideButtons(filtered, filteredColumns);
                        for (TableColumn<Map, String> column : temp) {
                            CheckBox check = (CheckBox) (checks.getChildren().get(i));
                            System.out.println(column.getText() + column.isVisible());
                            for (TableColumn<Map, String> filteredColumn : filteredColumns) {
                                if (filteredColumn.getText().equals(column.getText())) {
                                    filteredColumn.setVisible(column.isVisible());
                                }
                            }
                            i++;
                        }
                        tableControls.getChildren().remove(0);
                        tableControls.getChildren().add(0, checks);
                    } catch (SQLException e) {
                        new ErrorBox("Error", "Could not filter values from table", TableViz.formatLongSQLError(e),
                                false).show();
                    }
                });
                box.setPrompts(prompts);
                try {
                    box.show("WhereClauseInputPromptController");
                } catch (IOException e) {
                    TableViz.panic(e);
                }
            });

            transactEnable.setOnAction(event -> {
                try {
                    handler.beginTransaction();
                } catch (SQLException e) {
                    new ErrorBox("Error", "Could not start transaction", TableViz.formatLongSQLError(e), false).show();
                }
                isTransaction = true;
                commit.setDisable(false);
                discardChanges.setDisable(false);
                transactEnable.setDisable(true);
            });

            commit.setOnAction(event -> {
                try {
                    handler.commitChanges();
                    loadTable(table);
                    rowDetails.setVisible(false);
                    rowDetails.getChildren().clear();
                } catch (SQLException e) {
                    new ErrorBox("Error", "Could not commit changes", TableViz.formatLongSQLError(e), false).show();
                }
                isTransaction = false;
                commit.setDisable(true);
                discardChanges.setDisable(true);
                transactEnable.setDisable(false);
            });

            discardChanges.setOnAction(event -> {
                try {
                    handler.discardChanges();
                    loadTable(table);
                    rowDetails.setVisible(false);
                    rowDetails.getChildren().clear();
                } catch (SQLException e) {
                    new ErrorBox("Error", "Could not rollback changes", TableViz.formatLongSQLError(e), false).show();
                }
                isTransaction = false;
                commit.setDisable(true);
                discardChanges.setDisable(true);
                transactEnable.setDisable(false);
            });

            container.getChildren().addAll(delete, insert, filter, transactEnable, commit, discardChanges);

            tableControls.getChildren().clear();
            tableControls.getChildren().addAll(columnChecks, container);
            tableControls.setMinHeight(200);

            stage.setTitle("TableViz - " + tableName);
        } catch (SQLException e) {
            new ErrorBox("Warning", "Could not load table data", TableViz.formatLongSQLError(e), false).show();
        }
    }

    @FXML
    public void initializeTableList(SQLHandler handler) {
        this.handler = handler;
        try {
            Vector<String> tables = handler.getTables();
            setupTableList(tables);
        } catch (SQLException e) {
            new ErrorBox("Warning", "Could not load table names", TableViz.formatLongSQLError(e), false).show();
        }
    }

    private final Menu file = new Menu("File");
    private final Menu options = new Menu("Options");
    private final Menu help = new Menu("Help");
    private String table = "";
    private MenuItem refreshButton = null;
    private boolean isTransaction = false;
    private Vector<TableColumn<Map, String>> tableCols = null;

    public Stage stage = null;
    public SQLHandler handler = null;
    public TableViz main = null;
}
