package com.example.DocumentaionConstructor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.codehaus.jettison.json.JSONException;

import java.io.File;
import java.io.IOException;

public class StartController {

    // Текущая сцена.
    private Stage currentStage;
    // Текст информации.
    private String info = "Загрузите файл формата .json для выбранного шаблона, " +
                          "\nгде в первой строке предварительно укажите значение template_location - путь к шаблону .txt на компьютере, " +
                          "\nостальные параметры оставьте нетронутыми";

    private File openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Document");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(currentStage);
        return file;
    }

    /**
     * Обработчик нажатия на кнопку Start.
     * Загрузка файла для начала работы программы.
     */
    @FXML
    protected void onStartButtonClick() {
        // Открытие окна загрузчика файла.
        /*FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Document");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(currentStage);

         */
        File file = openFileChooser();
        if (file != null) {
            try {
                JsonConverter converter = new JsonConverter(file);
                FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("mainWindow.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();

                // Задание конвертера.
                MainController helloController = fxmlLoader.getController();
                helloController.setJsonFile(file);
                helloController.setJsonConverter(converter);

                // Задание сцены главного окна.
                Stage mainStage = new Stage();
                mainStage.initModality(Modality.WINDOW_MODAL);
                mainStage.setTitle("Documentation Constructor");
                mainStage.setResizable(false);
                mainStage.setScene(new Scene(root1));

                helloController.setMainStage(mainStage);
                helloController.setStartStage(currentStage);

                // Добавление обработчика закрытия окна.
                mainStage.setOnCloseRequest(helloController.getCloseEventHandler());
                mainStage.show();
                currentStage.hide();
            }
            catch (JSONException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка в JSON файле", ButtonType.OK);
                alert.showAndWait();
            }
            catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Ошибка! Возможно в JSON файле не указано местоположение шаблона: template_Location", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    /**
     * Задание текущей сцены.
     * @param stage сцена
     */
    public void setCurrentStage(Stage stage) {
        currentStage = stage;
    }

    /**
     * Получение текущей сцены.
     * @return сцена
     */
    public Stage getCurrentStage() {
        return currentStage;
    }

    /**
     * Обработчик нажатия на кнопку Info.
     * Вывод текста информации о том, что нужно загрузить в программу.
     */
    @FXML
    protected void onInfoButtonClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, info, ButtonType.OK);
        alert.setTitle("Information");
        alert.setHeaderText("Information");
        alert.showAndWait();
    }
}
