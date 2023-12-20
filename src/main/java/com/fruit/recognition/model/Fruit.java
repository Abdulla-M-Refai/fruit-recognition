package com.fruit.recognition.model;

import com.fruit.recognition.enumeration.Color;
import com.fruit.recognition.enumeration.FruitType;

public class Fruit
{
    private FruitType fruitType;

    private int sweetness;

    private Color color;

    public Fruit(FruitType fruitType, int sweetness, Color color)
    {
        this.fruitType = fruitType;
        this.sweetness = sweetness;
        this.color = color;
    }

    public int getSweetness()
    {
        return sweetness;
    }

    public void setSweetness(int sweetness)
    {
        this.sweetness = sweetness;
    }

    public Color getColor()
    {
        return color;
    }

    public void setColor(Color color)
    {
        this.color = color;
    }

    public FruitType getFruitType()
    {
        return fruitType;
    }

    public void setFruitType(FruitType fruitType)
    {
        this.fruitType = fruitType;
    }

    @Override
    public String toString()
    {
        return "Fruit{" +
                "sweetness=" + sweetness +
                ", color=" + color +
                ", fruitType=" + fruitType +
                '}';
    }
}
