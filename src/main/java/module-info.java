module com.mygg {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires com.google.gson;
    requires javafx.media;

    // Mengizinkan semua subpackages com.mygg.* diakses publik
    exports com.mygg;
    exports com.mygg.core;
    exports com.mygg.entities;
    exports com.mygg.render;
    exports com.mygg.util;
    exports com.mygg.managers;
    exports com.mygg.network;

    // Mengizinkan JavaFX (FXML) & Gson menggunakan reflection
    opens com.mygg to javafx.fxml, com.google.gson, javafx.media;
    opens com.mygg.core to javafx.fxml, com.google.gson, javafx.media;
    opens com.mygg.entities to javafx.fxml, com.google.gson, javafx.media;
    opens com.mygg.render to javafx.fxml, com.google.gson, javafx.media;
    opens com.mygg.util to javafx.fxml, com.google.gson, javafx.media;
    opens com.mygg.managers to javafx.fxml, com.google.gson, javafx.media;
    opens com.mygg.network to javafx.fxml, com.google.gson, javafx.media;
}
