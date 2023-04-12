package com.example.DocumentaionConstructor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class InfoWindowController implements Initializable {

    /**
     * Текст окна.
     */
    @FXML
    private TextArea informationTextArea = new TextArea();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        informationTextArea.setEditable(false);
    }

    /**
     * Задание текста окна.
     * @param text текст
     */
    public void setInformationText(String text) {
        informationTextArea.setText(text);
    }
}
