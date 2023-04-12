package com.example.DocumentaionConstructor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("startWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setResizable(false);
        stage.setTitle("Documentation Constructor");
        stage.setScene(scene);
        StartController startController = fxmlLoader.getController();
        startController.setCurrentStage(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}