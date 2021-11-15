module tableviz.tableviz {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens tableviz.tableviz to javafx.fxml;
    exports tableviz.tableviz;
}