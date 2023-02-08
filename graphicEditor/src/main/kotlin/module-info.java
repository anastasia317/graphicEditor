module com.example.graphiceditor {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires opencv;


    opens com.example.graphiceditor to javafx.fxml;
    exports com.example.graphiceditor;
}