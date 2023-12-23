package com.fruit.recognition;

import javafx.fxml.FXMLLoader;
import javafx.application.Application;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import java.io.IOException;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 480, 650);
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("images/icon.png"))));
        stage.setTitle("Fruit Recognition");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}