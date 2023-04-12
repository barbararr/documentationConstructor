module com.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires commons.io;
    requires jettison;


    opens com.example.DocumentaionConstructor to javafx.fxml;
    exports com.example.DocumentaionConstructor;
}