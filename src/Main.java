

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private TextField textField;
    private Label label;
    private Label hejLabel;
    public void start(Stage primaryStage){
        VBox root = new VBox();
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        menuBar.getMenus().add(fileMenu);


        FlowPane buttons = new FlowPane();
        buttons.setPadding(new Insets(10));
        buttons.setHgap(10);
        Button findPath = new Button("Find Path");
        Button showCon = new Button("Show Connection");
        Button newPlace = new Button("New Place");
        Button newCon = new Button("New Connection");
        Button changeCon = new Button("Change connection");
        buttons.getChildren().addAll(findPath,showCon,newPlace,newCon,changeCon);



        root.getChildren().add(menuBar);
        root.getChildren().add(buttons);
        Scene scene = new Scene(root,550, 100);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PathFinder");
        primaryStage.show();

    }
    public static void main(String[] args) {
        launch();
    }
    class Handler implements EventHandler<ActionEvent>{
        public void handle (ActionEvent event){
            String name = textField.getText();
            hejLabel.setText(name);
        }
    }
}