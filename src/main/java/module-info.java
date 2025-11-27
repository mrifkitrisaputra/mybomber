module com.mygg {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mygg to javafx.fxml;
    exports com.mygg;
}
