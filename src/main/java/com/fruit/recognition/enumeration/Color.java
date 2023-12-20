package com.fruit.recognition.enumeration;

public enum Color
{
    GREEN(1),
    YELLOW(2),
    ORANGE(3);

    private final int value;

    Color(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
