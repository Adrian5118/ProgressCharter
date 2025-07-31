package com.progresscharter.progresscharter;

import com.progresscharter.progresscharter.gui.MainGUI;
import com.progresscharter.progresscharter.lib.FileHandler;
import com.progresscharter.progresscharter.lib.ProjectHandler;
import com.sun.tools.javac.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        Scene scene = new Scene(new VBox(), 1366, 768);
        scene.setRoot(new MainGUI(scene, stage).getView());
        scene.getStylesheets().add(
                HelloApplication.class.getResource("stylesheet.css").toString()
        );
        stage.setTitle("ProgressCharter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}