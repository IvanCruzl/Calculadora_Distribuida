module com.ivan.calcup {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.ivan.calcup to javafx.fxml;
    exports com.ivan.calcup;
}
