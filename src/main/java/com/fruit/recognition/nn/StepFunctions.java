package com.fruit.recognition.nn;

public class StepFunctions
{
    private StepFunctions()
    {
    }

    public static double sigmoid(double x)
    {
        return 1 / (1 + (1 / Math.exp(x)));
    }

    public static double tanh(double x)
    {
        return (2 / (1 + (1 / Math.exp(x * 2)))) - 1;
    }

    public static double relu(double x)
    {
        if(x > 0)
            return x;

        return 0;
    }

    public static double sigmoidD(double x)
    {
        return x * (1 - x);
    }

    public static double tanhD(double x)
    {
        return 1 - Math.pow(x, 2);
    }

    public static int reluD(double x)
    {
        if(x >= 0)
            return 1;

        return -1;
    }
}
