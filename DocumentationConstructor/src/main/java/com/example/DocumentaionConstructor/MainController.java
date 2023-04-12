package com.example.DocumentaionConstructor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // Поле для записи текста.
    @FXML
    private TextArea textArea;

    // Лист с полями для заполнения.
    @FXML
    private ListView listView = new ListView();

    // Файл Json.
    private File jsonFile;
    // Конвертер Json-а.
    private JsonConverter jsonConverter = new JsonConverter();
    // Сцена окна с информацией.
    private Stage helpStage;
    // Сцена основного (этого) окна.
    private Stage mainStage;
    // Сцена начального окна.
    private Stage startStage;

    @FXML
    private SplitPane splitPane = new SplitPane();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listView.getItems().addAll(jsonConverter.GetNamesForListView());
        MultipleSelectionModel<String> selectionModel = listView.getSelectionModel();

        // Обработка события выбора пункта в списке.
        selectionModel.selectedItemProperty().addListener(new ChangeListener<String>(){
            public void changed(ObservableValue<? extends String> changed, String oldValue, String newValue) {
                textArea.setText(jsonConverter.GetValue(newValue));
            }
        });

        // Обработка события передвижения разделителя.
        double absolutePosition = splitPane.getDividers().get(0).getPosition();
        splitPane.getDividers().get(0).positionProperty().addListener((observable,oldValue,newValue) -> {
            splitPane.setDividerPosition(0, absolutePosition);
        });
    }

    private javafx.event.EventHandler<WindowEvent> closeEventHandler = new javafx.event.EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent event) {
            if (helpStage != null && helpStage.isShowing()) helpStage.close();
        }
    };

    public javafx.event.EventHandler<WindowEvent> getCloseEventHandler(){
        return closeEventHandler;
    }


    /**
     * Загрузка второго окна с текстом.
     * @param windowName название окна
     * @param windowText текст отображаемый на окне
     * @throws IOException ошибка при загрузке
     */
    private void loadSecondWindow(String windowName, String windowText) throws IOException {
        // Создание второго окна.
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("helpWindow.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();

        helpStage = new Stage();
        helpStage.initModality(Modality.WINDOW_MODAL);
        helpStage.setTitle(windowName);
        helpStage.setScene(new Scene(root1));

        // Задание отображаемого текста.
        InfoWindowController infoWindowController = fxmlLoader.getController();
        infoWindowController.setInformationText(windowText);

        helpStage.show();
    }

    /**
     * Задание главной сцены.
     * @param stage сцена
     */
    public void setMainStage(Stage stage) {
        mainStage = stage;
    }

    /**
     * Задание начальной сцены.
     * @param stage сцена
     */
    public void setStartStage(Stage stage) {
        startStage = stage;
    }

    /**
     * Чтение текста информации из файла.
     * @return текст информации или сообщение об ошибке.
     */
    private String getHelpText() {
        try {
            Path path = Paths.get("src/helpText.txt");
            Charset charset = StandardCharsets.UTF_8;
            return new String(Files.readAllBytes(path), charset);
        } catch (IOException e) {
            return "Error while reading help file";
        }
    }

    /**
     * Обработчик нажатия на кнопку Help.
     * @throws IOException ошибка при открытии файла с информацией
     */
    @FXML
    protected void onHelpButtonClick() throws IOException {
        if (helpStage == null || !helpStage.isShowing()) {
            loadSecondWindow("Help Information", getHelpText());
        } else {
            helpStage.toFront();
        }
    }

    /**
     * Обработчик нажатия на кнопку Save.
     * Сохранение выбранного пункта.
     */
    @FXML
    protected void onSaveButtonClick() {
        if (listView.getSelectionModel().getSelectedItem() != null)
            jsonConverter.SetValue(listView.getSelectionModel().getSelectedItem().toString(), textArea.getText());
    }

    /**
     * Обработчик нажатия на кнопку Finish.
     * Сохранение заполненных данных
     */
    @FXML
    protected void onFinishButtonClick() {
        try {
            if (jsonConverter.MoveJsonValuesToTemplate()) {
                // Открытие окна для сохранения файла.
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEX files (*.tex)", "*.tex");
                fileChooser.getExtensionFilters().add(extFilter);

                File file = fileChooser.showSaveDialog(mainStage);
                if (file != null) {
                    // Сохраняем в файл заполненный шаблон.
                    saveTextToFile(file);
                    mainStage.close();
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

    /**
     * Созранение текста заполненного шаблона в файл.
     * С выбором заполнять другой шаблон или нет.
     * @param file файл, куда надо созранить текст
     */
    private void saveTextToFile(File file) {
        try {
            // Запись в файл заполненного шаблона.
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(jsonConverter.getResult());
            writer.close();

            // Уведомление об успешном созранении.
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Файл успешно сохранен!\nХотите заполнить новый шаблон?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            // Если пользователь хочет заполнить другой шаблон,
            // открываем стартовое окно и закрываем текущее.
            if (alert.getResult() == ButtonType.YES) {
               startStage.show();
               mainStage.close();
            }
        } catch (IOException ex) {
           Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
           alert.showAndWait();
        }
    }

    /**
     * Задание конвертора и заполнение элементов.
     * @param converter конвертер
     */
    public void setJsonConverter(JsonConverter converter) {
        jsonConverter = converter;
        listView.getItems().addAll(jsonConverter.GetNamesForListView());
    }

    /**
     * Задание файла, хранящего json.
     * @param file файл
     */
    public void setJsonFile(File file) {
        jsonFile = file;
    }
}