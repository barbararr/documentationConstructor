package com.example.DocumentaionConstructor;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Класс обработчик json.
 * Читает файл json, записывает значения в объект, работает с шаблоном.
 */
public class JsonConverter {

    // Объект json.
    private JSONObject json;
    // Путь к файлу с шаблоном.
    private String template_location;
    // Заполненный шаблон.
    private String result;

    public JsonConverter() {
        json = new JSONObject();
    }

    public JsonConverter(File f) throws JSONException, IOException {
        ReadJson(f);
    }

    /**
     * Чтение Json из файла.
     * @param file файл с Json
     * @throws IOException ошибки при чтении файла
     * @throws JSONException ошибки при чтении Json
     */
    private void ReadJson(File file) throws IOException, JSONException {
        Scanner scanner = new Scanner(System.in);
        File f = file;

        if (f.exists()) {
            // Читаем файл  и создаем объект json.
            InputStream is = new FileInputStream(file);
            String jsonTxt = IOUtils.toString(is, "UTF-8");
            json = new JSONObject(jsonTxt);

            template_location = json.getString("template_Location");
            // Если нет адреса шаблона, то выбрасываем ошибку.
            if (Objects.equals(template_location, "") || !Files.exists(Path.of(template_location))) throw new IOException();
        }
    }

    /**
     * Получение названий полей для заполнения шаблона из json.
     * @return названий полей для заполнения
     */
    public ObservableList<String> GetNamesForListView() {
        ObservableList<String> namesList = FXCollections.<String>observableArrayList();

        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            // Пользователю не должно выводиться поле с адресом шаблона.
            if (!Objects.equals(key, "template_Location")) {
                // Заменяем символы для лучшего восприятия пользователем.
                namesList.add(key.replaceAll("_", " "));
            }
        }
        return namesList;
    }

    /**
     * Заменяем символы в названиях для дальнейшего поиска по json.
     * @param name название
     * @return измененное имя
     */
    private String ConvertSting(String name) {
        return name.replaceAll(" ", "_");
    }

    /**
     * Получение значения из json.
     * @param name ключ
     * @return значение
     */
    public String GetValue(String name) {
        try {
            return json.getString(ConvertSting(name));
        }
        catch (JSONException e) {
            return "Error";
        }
    }

    /**
     * Задание значения по ключу.
     * @param name ключ
     * @param value значение
     */
    public void SetValue(String name, String value) {
        try {
            json.put(ConvertSting(name), value);
        }
        catch (JSONException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Произошла ошибка при записи в json", ButtonType.OK);
            alert.showAndWait();
        }
    }

    /**
     * Перенос заданных пользователем значений в шаблон.
     * @return true если успешно
     */
    public boolean MoveJsonValuesToTemplate() {
        try {
            // Чтение файла шаблона.
            Path path = Paths.get(template_location);
            Charset charset = StandardCharsets.UTF_8;
            String content = new String(Files.readAllBytes(Path.of(template_location)), charset);

            // Запись пользовательских данных в шаблон.
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = (String) json.get(key);
                content = content.replaceAll("\\$" + key, value);
            }
            result = content;
            return true;
        } catch (IOException | JSONException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "В JSON файле не указано местоположение шаблона: template_Location", ButtonType.OK);
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Получение текста шаблона.
     * @return текст шаблона
     */
    public String getTemplateText()  {
        try {
            Path path = Paths.get(template_location);
            Charset charset = StandardCharsets.UTF_8;
            return new String(Files.readAllBytes(Path.of(template_location)), charset);
        } catch (IOException e) {
            return "Error";
        }
    }

    /**
     * Получение заполненного шаблона.
     * @return текст шаблона
     */
    public String getResult() {
        return result;
    }
}
