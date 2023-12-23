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

import com.fruit.recognition.nn.NeuralNetwork;

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

    @FXML
    private TextField goal;

    @FXML
    private CheckBox goalEnable;

    @FXML
    private TextField sweetnessTestField;

    @FXML
    private ComboBox<String> colorTestBox;

    private final NeuralNetwork neuralNetwork = new NeuralNetwork();

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
    void test()
    {
        if(sweetnessTestField.getText().isBlank())
        {
            showDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Messing Sweetness",
                "Enter Sweetness Value [1-10]"
            );

            return;
        }

        double sweetness = Double.parseDouble(sweetnessTestField.getText());
        int color = Color.valueOf(colorTestBox.getSelectionModel().getSelectedItem()).getValue();
        neuralNetwork.setHiddenActivation(Activation.valueOf(activation.getSelectionModel().getSelectedItem()));

        String fruitResult = "";

        double[] input = {sweetness, color};
        int output = neuralNetwork.test(input);

        switch (output)
        {
            case 0 -> fruitResult = "Fruit: Apple";
            case 1 -> fruitResult = "Fruit: Banana";
            case 2 -> fruitResult = "Fruit: Orange";
            default -> System.out.println("Unknown Result");
        }

        showDialog(
            Alert.AlertType.INFORMATION,
            "Result",
            "Success",
            fruitResult
        );
    }

    @FXML
    void learn()
    {
        String errorMessage = "";

        if(neurons.getText().isBlank() || Integer.parseInt(neurons.getText()) == 0)
            errorMessage += "*Messing Neurons Number\n";

        if(learningRate.getText().isBlank() || Double.parseDouble(learningRate.getText()) == 0)
            errorMessage += "*Messing Learning Rate\n";

        if(epochs.getText().isBlank() || Integer.parseInt(epochs.getText()) == 0)
            errorMessage += "*Messing Epochs Number\n";

        if(goalEnable.isSelected() && (goal.getText().isBlank() || Double.parseDouble(goal.getText()) == 0))
            errorMessage += "*Messing Goal Number\n";

        if(dataTable.getItems().isEmpty())
            errorMessage += "*Messing Data\n";

        if(!errorMessage.isEmpty())
        {
            showDialog(
                Alert.AlertType.ERROR,
                "Error",
                "Something Went Wrong",
                errorMessage
            );

            return;
        }

        neuralNetwork.setEpochs(Integer.parseInt(epochs.getText()));
        neuralNetwork.setHidden(Integer.parseInt(neurons.getText()));
        neuralNetwork.setLearningRate(Double.parseDouble(learningRate.getText()));
        neuralNetwork.setHiddenActivation(Activation.valueOf(activation.getSelectionModel().getSelectedItem()));

        if(goalEnable.isSelected())
            neuralNetwork.setGoal(Double.parseDouble(goal.getText()));

        double[][] input = new double[dataTable.getItems().size()][2];
        double[][] output = new double[dataTable.getItems().size()][3];

        for(int i = 0 ; i < dataTable.getItems().size() ; i++)
        {
            FruitGuiRow row = dataTable.getItems().get(i);

            input[i][0] = Double.parseDouble(row.getSweetness().getText());
            input[i][1] = Color.valueOf(row.getColor().getSelectionModel().getSelectedItem()).getValue();

            output[i][0] = FruitType.valueOf(row.getFruitType().getSelectionModel().getSelectedItem()) == FruitType.APPLE ? 1 : 0;
            output[i][1] = FruitType.valueOf(row.getFruitType().getSelectionModel().getSelectedItem()) == FruitType.BANANA ? 1 : 0;
            output[i][2] = FruitType.valueOf(row.getFruitType().getSelectionModel().getSelectedItem()) == FruitType.ORANGE ? 1 : 0;
        }

        long startTime = System.currentTimeMillis();
        double []statistics = neuralNetwork.train(input, output);
        long endTime = System.currentTimeMillis();

        String statisticsMessage =
            "Time Taken: " + (endTime - startTime) + " ms\n" +
            "MSE: " + statistics[1] + "\n" +
            "Accuracy: " + statistics[0] + "\n" +
            "Epoch: " + statistics[2];

        showDialog(
            Alert.AlertType.INFORMATION,
            "Result",
            "Operation Done!",
            statisticsMessage
        );
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

    @FXML
    void goalEnable()
    {
        goal.setDisable(!goalEnable.isSelected());
        neuralNetwork.setGoalEnabled(goalEnable.isSelected());
    }

    @FXML
    void reset()
    {
        fruitRows.clear();
        dataTable.getItems().clear();
        dataTable.refresh();

        neuralNetwork.setEpochs(1);
        neuralNetwork.setHidden(1);
        neuralNetwork.setLearningRate(0.1);
        neuralNetwork.setHiddenActivation(Activation.TANH);

        goal.setText("0.0");
        epochs.setText("1");
        neurons.setText("1");
        learningRate.setText("0.1");
        sweetnessTestField.setText("1");

        goal.setDisable(true);
        goalEnable.setSelected(false);

        activation.getSelectionModel().selectFirst();
        colorTestBox.getSelectionModel().selectFirst();
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

    private <T> void addValidationOnField(TextField textField, StringConverter<T> stringConverter, String regex, T defaultValue)
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

        colorTestBox.getItems().addAll(Arrays.stream(Color.values()).map(Enum::name).toList());
        colorTestBox.getSelectionModel().selectFirst();

        addValidationOnField(epochs, new IntegerStringConverter(),"[0-9]+", 1);
        addValidationOnField(neurons, new IntegerStringConverter(),"[0-9]+", 1);
        addValidationOnField(goal, new DoubleStringConverter(),"0\\.[0-9]+|1", 0.1);
        addValidationOnField(learningRate, new DoubleStringConverter(),"0\\.[0-9]+|1", 0.1);
        addValidationOnField(sweetnessTestField, new DoubleStringConverter(),"^(?:[1-9](\\.\\d+)?|10)$", 1.0);

        dataTable.setItems(fruitRows);
        fruitType.setCellValueFactory(new PropertyValueFactory<>("fruitType"));
        sweetness.setCellValueFactory(new PropertyValueFactory<>("sweetness"));
        color.setCellValueFactory(new PropertyValueFactory<>("color"));
    }
}