package com.fruit.recognition.nn;

import java.util.Random;

import com.fruit.recognition.enumeration.Activation;
import static com.fruit.recognition.nn.StepFunctions.*;

public class NeuralNetwork
{
    private final int INPUT = 2;

    private final int OUTPUT = 3;

    private Activation activation = Activation.TAN;

    private int hidden = 1;

    private double learningRate = 0.1;

    private int epochs = 1;

    private double[][] inputWeights;

    private double[][] outputWeights;

    public NeuralNetwork()
    {
        init();
    }

    public void setActivation(Activation activation)
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

    private void init()
    {
        Random random = new Random();

        inputWeights = new double[INPUT][hidden];
        outputWeights = new double[hidden][OUTPUT];

        for (int i = 0; i < INPUT; i++)
            for (int j = 0; j < hidden; j++)
                inputWeights[i][j] = random.nextDouble() - 0.5;

        for (int i = 0; i < hidden; i++)
            for (int j = 0; j < OUTPUT ; j++)
                outputWeights[i][j] = random.nextDouble() - 0.5;
    }

    public void train(double[][] input, double[][] expectedOutput)
    {
        for (int i = 0 ; i < epochs ; i++)
        {
            for (int j = 0 ; j < input.length ; j++)
            {
                double[] inputRow = input[j];
                double[] hiddenNeurons = new double[hidden];
                double[] output = new double[OUTPUT];

                double[] outputRow = expectedOutput[j];

                double[] oe = new double[OUTPUT];
                double[] he = new double[hidden];

                calculateOutput(inputRow, hiddenNeurons, inputWeights, hidden, INPUT);
                calculateOutput(hiddenNeurons, output, outputWeights, OUTPUT, hidden);
                calculateError(he, oe, outputRow, output, hiddenNeurons);
                updateWeight(inputWeights, he, inputRow, hidden, INPUT);
                updateWeight(outputWeights, oe, hiddenNeurons, OUTPUT, hidden);
            }
        }
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
            for (int y = 0; y < OUTPUT; y++)
                sum += oe[y] * outputWeights[i][y];

            if(activation == Activation.RELU)
                he[i] = sum * reluD(hiddenNeurons[i]);
            else if(activation == Activation.SIGMOID)
                he[i] = sum * sigmoidD(hiddenNeurons[i]);
            else if(activation == Activation.TAN)
                he[i] = sum * tanD(hiddenNeurons[i]);
        }
    }

    private void updateWeight(
        double [][]target,
        double []error,
        double []input,
        int iLimit,
        int jLimit
    )
    {
        for (int i = 0; i < iLimit; i++)
            for (int j = 0; j < jLimit ; j++)
                target[j][i] += learningRate * error[i] * input[j];
    }

    public int predict(double[] input)
    {
        double[] hiddenNeurons = new double[hidden];
        double[] output = new double[OUTPUT];

        calculateOutput(input, hiddenNeurons, inputWeights, hidden, INPUT);
        calculateOutput(hiddenNeurons, output, outputWeights, OUTPUT, hidden);

        int index = 0;
        for (int i = 1 ; i < OUTPUT ; i++)
            if(output[i] > output[index])
                index = i;

        return index;
    }

    private void calculateOutput(
        double []input,
        double []target,
        double [][]weights,
        int iLimit,
        int jLimit
    )
    {
        double theta = 0.3;

        for (int i = 0; i < iLimit; i++)
        {
            double sum = 0;
            for (int j = 0; j < jLimit; j++)
                sum += input[j] * weights[j][i];

            if(activation == Activation.RELU)
                target[i] = relu(sum + theta);
            else if(activation == Activation.SIGMOID)
                target[i] = sigmoid(sum + theta);
            else if(activation == Activation.TAN)
                target[i] = tan(sum + theta);
        }
    }
}
