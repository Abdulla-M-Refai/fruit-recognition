package com.fruit.recognition.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.FileChooser;
import javafx.scene.layout.AnchorPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ResourceBundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.fruit.recognition.model.FruitGuiRow;

import com.fruit.recognition.enumeration.Color;
import com.fruit.recognition.enumeration.FruitType;
import com.fruit.recognition.enumeration.Activation;

public class MainViewController implements Initializable
{
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ComboBox<String> activation;

    @FXML
    private TableView<FruitGuiRow> dataTable;

    @FXML
    private TableColumn<FruitGuiRow, ComboBox<String>> fruitType;

    @FXML
    private TableColumn<FruitGuiRow, ComboBox<String>> color;

    @FXML
    private TableColumn<FruitGuiRow, TextField> sweetness;

    @FXML
    private TextField epochs;

    @FXML
    private TextField learningRate;

    @FXML
    private TextField neurons;

    private final ObservableList<FruitGuiRow> fruitRows = FXCollections.observableArrayList();

    @FXML
    void addRow()
    {
        addFruit(
            FruitType.APPLE.name(),
            Color.GREEN.name(),
            1
        );
    }

    @FXML
    void loadExcelFile()
    {
        try
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Excel File Chooser");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel Sheet Files", "*.xlsx")
            );

            File selectedFile = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());

            if(selectedFile != null)
            {
                FileInputStream fis = new FileInputStream(selectedFile);
                XSSFWorkbook wb = new XSSFWorkbook(fis);

                XSSFSheet sheet = wb.getSheetAt(0);

                for (Row row : sheet)
                {
                    Iterator<Cell> cellIterator = row.cellIterator();
                    addFruit(
                        cellIterator.next().getStringCellValue(),
                        cellIterator.next().getStringCellValue(),
                        cellIterator.next().getNumericCellValue()
                    );
                }
            }
        }
        catch (IOException e)
        {
            showDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Can't Open The File",
                e.getMessage()
            );
        }
        catch (Exception e)
        {
            showDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Something Went Wrong",
                e.getMessage()
            );
        }
    }

    @FXML
    void learn()
    {

    }

    @FXML
    void test()
    {

    }

    private void addFruit(
        String fruit,
        String color,
        double sweetness
    )
    {
        ComboBox<String> fruitTypeBox = new ComboBox<>();
        fruitTypeBox.getItems().addAll(Arrays.stream(FruitType.values()).map(Enum::name).toList());
        fruitTypeBox.getSelectionModel().select(fruit);

        TextField sweetnessField = new TextField();
        addValidationOnField(sweetnessField, new DoubleStringConverter(),"^(?:[1-9](\\.\\d+)?|10)$", sweetness);

        ComboBox<String> colorBox = new ComboBox<>();
        colorBox.getItems().addAll(Arrays.stream(Color.values()).map(Enum::name).toList());
        colorBox.getSelectionModel().select(color);

        fruitRows.add(
            new FruitGuiRow(
                fruitTypeBox,
                sweetnessField,
                colorBox
            )
        );

        dataTable.refresh();
        dataTable.scrollTo(dataTable.getItems().size());
    }

    private void showDialog(
        Alert.AlertType alertType,
        String title,
        String header,
        String content
    )
    {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.initOwner(anchorPane.getScene().getWindow());
        alert.showAndWait();
    }

    private void addValidationOnField(TextField textField, StringConverter stringConverter,String regex, Number defaultValue)
    {
        textField.setTextFormatter(new TextFormatter<>(stringConverter, defaultValue, change ->
        {
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(change.getControlNewText());
            return (matcher.matches() || matcher.hitEnd()) ? change : null;
        }));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        activation.getItems().addAll(Arrays.stream(Activation.values()).map(Enum::name).toList());
        activation.getSelectionModel().selectFirst();

        addValidationOnField(learningRate, new DoubleStringConverter(),"0\\.[1-9]|1", 0.1);
        addValidationOnField(neurons, new IntegerStringConverter(),"[0-9]+", 1);
        addValidationOnField(epochs, new IntegerStringConverter(),"[0-9]+", 1);

        dataTable.setItems(fruitRows);
        fruitType.setCellValueFactory(new PropertyValueFactory<>("fruitType"));
        sweetness.setCellValueFactory(new PropertyValueFactory<>("sweetness"));
        color.setCellValueFactory(new PropertyValueFactory<>("color"));
    }
}