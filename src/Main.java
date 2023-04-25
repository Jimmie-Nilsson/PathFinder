

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main extends Application {

    // Change variable names later
    // clean up code
    //
    //
    //
    private VBox root;
    private Pane map;
    private MenuBar menuBar;
    private Menu fileMenu;
    private MenuItem menuFileNewMap;
    private MenuItem menuFileOpen;
    private MenuItem menuSave;
    private MenuItem menuSaveImg;
    private MenuItem menuExit;
    private Button findPath;
    private Button showCon;
    private Button newPlace;
    private Button newCon;
    private Button changeCon;
    private Stage primaryStage;
    private boolean changes = false;


    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new VBox();
        menuBar = new MenuBar();
        fileMenu = new Menu("File");
        menuFileNewMap = new MenuItem("New Map");
        menuFileNewMap.setOnAction(new NewMapHandler());

        menuFileOpen = new MenuItem("Open");
        menuFileOpen.setOnAction(new OpenMapHandler());

        menuSave = new MenuItem("Save");

        menuSaveImg = new MenuItem("Save Image");

        menuExit = new MenuItem("Exit");
        menuExit.setOnAction(event -> { // This is a work in progress just testing things out!!
            Alert msgBox = new Alert(Alert.AlertType.CONFIRMATION);
            msgBox.setTitle("Warning!");
            msgBox.setContentText("Unsaved changes, exit anyway?");
            msgBox.setHeaderText(null);
            msgBox.showAndWait();
        }); // END OF TESTING


        fileMenu.getItems().addAll(menuFileNewMap, menuFileOpen, menuSave, menuSaveImg, menuExit);
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
        setIDs();

    }

    public static void main(String[] args) {
        launch();
        shutdown();
    }

    public static void shutdown() {
        System.out.println("Goodbye");
    }

    class NewMapHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            changes = true;
            if (map == null) {
                openMap("file:europa.gif");
//            map = new Pane();
//            Image background = new Image("file:europa.gif");
//            ImageView bg = new ImageView(background);
//            map.getChildren().add(bg);
//            root.getChildren().add(map);
//            primaryStage.setHeight(background.getHeight() + 110);
//            primaryStage.setWidth(background.getWidth() + 15);
        }else {
                root.getChildren().remove(map);
                map = null;
                handle(event);
            }
        }
    }
    private void openMap(String mapName){
        map = new Pane();
        Image background = new Image(mapName);
        ImageView bg = new ImageView(background);
        map.getChildren().add(bg);
        root.getChildren().add(map);
        primaryStage.setHeight(background.getHeight() + 110); // 110 is extra pixels by other elements
        primaryStage.setWidth(background.getWidth() + 15); // 15 is padding to make the map look better

    }
    private void setIDs(){
        menuBar.setId("menu");
        fileMenu.setId("menuFile");
        menuFileNewMap.setId("menuNewMap");
        menuFileOpen.setId("menuOpenFile");
        menuSave.setId("menuSaveFile");
        menuSaveImg.setId("menuSaveImage");
        menuExit.setId("menuExit");
        // add buttons here later
    }
    private boolean mapExists(){
        return root.getChildren().contains(map);
    }
    class OpenMapHandler implements EventHandler<ActionEvent>{

        @Override
        public void handle(ActionEvent actionEvent) {
            try {
                FileReader file = new FileReader("europa.graph");
                BufferedReader in = new BufferedReader(file);
                String line = in.readLine();
                openMap(line);
                line = in.readLine();
                String[] locations =  line.split(";");
                for (int i = 0; i < locations.length; i+= 3)

                while ((line = in.readLine()) != null){

                }
            }catch (FileNotFoundException e){
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("Could not open File:europa.graph");
                error.showAndWait();
            }catch (IOException e){
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText(e.getMessage());
            }
        }
    }
}