

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
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

    private MenuItem newMap;
    private MenuItem open;
    private MenuItem save;
    private MenuItem saveImg;
    private MenuItem exit;
    private Button findPath;
    private Button showCon;
    private Button newPlace;
    private Button newCon;
    private Button changeCon;


    public void start(Stage primaryStage) {
        VBox root = new VBox();
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
         newMap = new MenuItem("New Map");
         open = new MenuItem("Open");
         save = new MenuItem("Save");
         saveImg = new MenuItem("Save Image");
         exit = new MenuItem("Exit");
         exit.setOnAction(event -> { // This is a work in progress just testing things out!!
             Alert msgBox = new Alert(Alert.AlertType.CONFIRMATION);
             msgBox.setTitle("Warning!");
             msgBox.setContentText("Unsaved changes, exit anyway?");
             msgBox.setHeaderText(null);
             msgBox.showAndWait();

         }); // END OF TESTING


        fileMenu.getItems().addAll(newMap, open, save, saveImg, exit);
        menuBar.getMenus().add(fileMenu);


        FlowPane buttons = new FlowPane();
        buttons.setPadding(new Insets(10));
        buttons.setHgap(10);
        findPath = new Button("Find Path");
        showCon = new Button("Show Connection");
        newPlace = new Button("New Place");
        newCon = new Button("New Connection");
        changeCon = new Button("Change connection");
        buttons.getChildren().addAll(findPath, showCon, newPlace, newCon, changeCon);


        root.getChildren().add(menuBar);
        root.getChildren().add(buttons);
        Scene scene = new Scene(root, 550, 100);
        primaryStage.setScene(scene);
        primaryStage.setTitle("PathFinder");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
        shutdown();
    }
    public static void shutdown(){
        System.out.println("Goodbye");
    }

    class Handler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {

        }
    }
}