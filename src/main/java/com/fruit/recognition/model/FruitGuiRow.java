package com.fruit.recognition.model;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FruitGuiRow
{
    private final ComboBox<String> fruitType;

    private final TextField sweetness;

    private final ComboBox<String> color;

    public FruitGuiRow(ComboBox<String> fruitType, TextField sweetness, ComboBox<String> color)
    {
        this.fruitType = fruitType;
        this.sweetness = sweetness;
        this.color = color;
    }

    public ComboBox<String> getFruitType()
    {
        return fruitType;
    }

    public TextField getSweetness()
    {
        return sweetness;
    }

    public ComboBox<String> getColor()
    {
        return color;
    }
}
