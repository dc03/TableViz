module tableviz.tableviz {
    requires javafx.controls;
    requires javafx.fxml;


    opens tableviz.tableviz to javafx.fxml;
    exports tableviz.tableviz;
}