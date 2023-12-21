module com.example.fruit_recognition {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.fruit.recognition to javafx.fxml;
    opens com.fruit.recognition.model to javafx.base;

    exports com.fruit.recognition;
    exports com.fruit.recognition.controller;
    opens com.fruit.recognition.controller to javafx.fxml;
    exports com.fruit.recognition.nn;
    exports com.fruit.recognition.enumeration;
    opens com.fruit.recognition.nn to javafx.fxml;
}