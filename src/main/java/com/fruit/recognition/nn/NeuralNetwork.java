package com.fruit.recognition.nn;

import java.util.Random;

import com.fruit.recognition.enumeration.Activation;

import static com.fruit.recognition.nn.StepFunctions.*;

public class NeuralNetwork
{
    private final int INPUT = 2;

    private final int OUTPUT = 3;

    private Activation activation = Activation.TANH;

    private int hidden = 1;

    private double learningRate = 0.1;

    private int epochs = 1;

    private double goal = 0;

    boolean isGoalEnabled = false;

    private double[][] inputWeights;

    private double[][] outputWeights;

    private double[] hiddenThetas;

    private double[] outputThetas;

    public NeuralNetwork()
    {
        init();
    }

    public void setHiddenActivation(Activation activation)
    {
        this.activation = activation;
    }

    public void setHidden(int hidden)
    {
        int prev = this.hidden;
        this.hidden = hidden;

        if(prev != hidden)
            init();
    }

    public void setLearningRate(double learningRate)
    {
        this.learningRate = learningRate;
    }

    public void setEpochs(int epochs)
    {
        this.epochs = epochs;
    }

    public void setGoal(double goal)
    {
        this.goal = goal;
    }

    public void setGoalEnabled(boolean goalEnabled)
    {
        isGoalEnabled = goalEnabled;
    }

    private void init()
    {
        Random random = new Random();

        inputWeights = new double[INPUT][hidden];
        outputWeights = new double[hidden][OUTPUT];

        hiddenThetas = new double[hidden];
        outputThetas = new double[OUTPUT];

        for (int i = 0; i < INPUT ; i++)
            for (int j = 0 ; j < hidden ; j++)
                inputWeights[i][j] = random.nextDouble() - 0.5;

        for (int i = 0; i < hidden; i++)
            for (int j = 0; j < OUTPUT ; j++)
                outputWeights[i][j] = random.nextDouble() - 0.5;

        for (int i = 0; i < hidden ; i++)
            hiddenThetas[i] = random.nextDouble();

        for (int i = 0; i < OUTPUT ; i++)
            outputThetas[i] = random.nextDouble();
    }

    public double[] train(double[][] input, double[][] expectedOutput)
    {
        int correctPrediction = 0;
        int totalPrediction = 0;
        double sumSquaredError = 0;

        for (int i = 0; i < epochs ; i++)
        {
            for (int j = 0; j < input.length; j++)
            {
                double[] inputRow = input[j];
                double[] hiddenNeurons = new double[hidden];
                double[] output = new double[OUTPUT];

                double[] outputRow = expectedOutput[j];
                double[] oe = new double[OUTPUT];
                double[] he = new double[hidden];

                calculateOutput(inputRow, hiddenNeurons, inputWeights, hiddenThetas, hidden, INPUT, this.activation);
                calculateOutput(hiddenNeurons, output, outputWeights, outputThetas, OUTPUT, hidden, Activation.SIGMOID);
                calculateError(he, oe, outputRow, output, hiddenNeurons);
                updateWeight(inputWeights, he, inputRow, hiddenThetas, hidden, INPUT);
                updateWeight(outputWeights, oe, hiddenNeurons, outputThetas, OUTPUT, hidden);

                int index = test(inputRow);
                int actualIndex = maxElementIndex(output);

                if(index == actualIndex)
                    correctPrediction++;

                totalPrediction++;

                for (int k = 0; k < OUTPUT; k++)
                {
                    double error = outputRow[k] - output[k];
                    sumSquaredError += error * error;
                }
            }
        }

        double mse = sumSquaredError / totalPrediction;
        double accuracy = (double) correctPrediction / totalPrediction;

        return new double[]{accuracy, mse};
    }

    private void calculateError(
        double []he,
        double []oe,
        double []outputRow,
        double []output,
        double []hiddenNeurons
    )
    {
        for (int i = 0; i < OUTPUT; i++)
            oe[i] = outputRow[i] - output[i];

        for (int i = 0; i < hidden; i++)
        {
            double sum = 0;
            for (int j = 0; j < OUTPUT; j++)
                sum += oe[j] * outputWeights[i][j];

            if(activation == Activation.RELU)
                he[i] = sum * reluD(hiddenNeurons[i]);
            else if(activation == Activation.SIGMOID)
                he[i] = sum * sigmoidD(hiddenNeurons[i]);
            else if(activation == Activation.TANH)
                he[i] = sum * tanhD(hiddenNeurons[i]);
        }
    }

    private void updateWeight(
        double [][]target,
        double []error,
        double []input,
        double []thetas,
        int iLimit,
        int jLimit
    )
    {
        for (int i = 0; i < iLimit; i++)
        {
            for (int j = 0; j < jLimit ; j++)
                target[j][i] += learningRate * error[i] * input[j];
            thetas[i] += learningRate * error[i];
        }
    }

    public int test(double[] input)
    {
        double[] hiddenNeurons = new double[hidden];
        double[] output = new double[OUTPUT];

        calculateOutput(input, hiddenNeurons, inputWeights, hiddenThetas, hidden, INPUT, this.activation);
        calculateOutput(hiddenNeurons, output, outputWeights, outputThetas, OUTPUT, hidden, Activation.SIGMOID);

        return maxElementIndex(output);
    }

    private void calculateOutput(
        double []input,
        double []target,
        double [][]weights,
        double []thetas,
        int iLimit,
        int jLimit,
        Activation activation
    )
    {
        for (int i = 0; i < iLimit; i++)
        {
            double sum = 0;
            for (int j = 0; j < jLimit; j++)
                sum += input[j] * weights[j][i];

            if(activation == Activation.RELU)
                target[i] = relu(sum + thetas[i]);
            else if(activation == Activation.SIGMOID)
                target[i] = sigmoid(sum + thetas[i]);
            else if(activation == Activation.TANH)
                target[i] = tanh(sum + thetas[i]);
        }
    }

    private int maxElementIndex(double[] array)
    {
        int index = 0;
        for (int i = 1; i < array.length; i++)
            if (array[i] > array[index])
                index = i;

        return index;
    }
}