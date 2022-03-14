package com.dlindbla.travellingsalesmanproblemdemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
       // AnchorPane root = (AnchorPane) FXMLLoader.load(HelloApplication.class.getResource("hello-view.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/view/hello-view.fxml"));
        Pane pane = fxmlLoader.load();
        //TSPCanvasController tspCanvasController = new TSPCanvasController();

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("TSP");
        stage.show();

        TSPCanvasController tspCanvasController = fxmlLoader.getController();
        tspCanvasController.configureNodes();



    }

    public static void main(String[] args) {
        launch();
    }
}