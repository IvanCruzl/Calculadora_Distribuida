module com.ivan.cliente {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.ivan.cliente to javafx.fxml;
    exports com.ivan.cliente;
}
