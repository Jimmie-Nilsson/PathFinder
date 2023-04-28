

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.w3c.dom.events.Event;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class PathFinder extends Application {

    // Change variable names later
    // clean up code
    //
    //
    //
    private static final String MAP_NAME = "file:europa.gif";
    private ListGraph<Location> graph;
    private VBox root;
    private Pane map;
    private MenuBar menuBar;
    private Menu fileMenu;
    private MenuItem menuFileNewMap;
    private MenuItem menuFileOpen;
    private MenuItem menuSave;
    private MenuItem menuSaveImg;
    private MenuItem menuExit;
    private FlowPane buttons;
    private Button findPath;
    private Button showCon;
    private Button newPlace;
    private Button newCon;
    private Button changeCon;
    private Stage primaryStage;
    private Location locA = null, locB = null;
    private boolean changes = false;


    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new VBox();
        menuBar = new MenuBar();
        fileMenu = new Menu("File");

        menuFileNewMap = new MenuItem("New Map");
        menuFileNewMap.setOnAction(event -> openMap(MAP_NAME));

        menuFileOpen = new MenuItem("Open");
        menuFileOpen.setOnAction(new OpenMapHandler());

        menuSave = new MenuItem("Save");
        menuSave.setOnAction(new SaveMapHandler());

        menuSaveImg = new MenuItem("Save Image");
        menuSaveImg.setOnAction(new SaveImgHandler());

        menuExit = new MenuItem("Exit");
        menuExit.setOnAction(event -> {
            if (changes) {
                primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            } else {
                primaryStage.close();
            }
        });


        fileMenu.getItems().addAll(menuFileNewMap, menuFileOpen, menuSave, menuSaveImg, menuExit);
        menuBar.getMenus().add(fileMenu);


        buttons = new FlowPane();
        buttons.setPadding(new Insets(10));
        buttons.setHgap(10);
        findPath = new Button("Find Path");
        showCon = new Button("Show Connection");
        newPlace = new Button("New Place");
        newPlace.setOnAction(new NewLocationHandler());
        newCon = new Button("New Connection");
        changeCon = new Button("Change connection");
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(findPath, showCon, newPlace, newCon, changeCon);
        buttons.setDisable(true);


        root.getChildren().add(menuBar);
        root.getChildren().add(buttons);
        Scene scene = new Scene(root, 550, 100);
        primaryStage.setOnCloseRequest(new ExitHandler());
        primaryStage.setScene(scene);
        primaryStage.setTitle("PathFinder");
        setIDs();
        primaryStage.show();


    }

    public static void main(String[] args) {
        launch();
    }

    private void openMap(String mapName) {
        locA = null;
        locB = null;
        if (changes) {
            // write code here for handling unsaved changes...
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning!");
            alert.setContentText("Unsaved changes, continue anyway?");
            alert.setHeaderText(null);
            Optional<ButtonType> choice = alert.showAndWait();
            if (choice.isPresent() && choice.get() != ButtonType.OK) {
                return;
            }
        }
        root.getChildren().remove(map);
        map = new Pane();
        Image background = new Image(mapName);
        ImageView bg = new ImageView(background);
        map.getChildren().add(bg);
        root.getChildren().add(map);
        primaryStage.setHeight(background.getHeight() + 110); // 110 is extra pixels by other elements
        primaryStage.setWidth(background.getWidth() + 15); // 15 is padding to make the map look better in the scene
        buttons.setDisable(false);
        changes = true;


    }

    private void setIDs() {
        menuBar.setId("menu");
        fileMenu.setId("menuFile");
        menuFileNewMap.setId("menuNewMap");
        menuFileOpen.setId("menuOpenFile");
        menuSave.setId("menuSaveFile");
        menuSaveImg.setId("menuSaveImage");
        menuExit.setId("menuExit");
        // add buttons here later
    }


    class ExitHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent event) {
            if (changes) {
                Alert msgBox = new Alert(Alert.AlertType.CONFIRMATION);
                msgBox.setTitle("Warning!");
                msgBox.setContentText("Unsaved changes, exit anyway?");
                msgBox.setHeaderText(null);
                Optional<ButtonType> choice = msgBox.showAndWait();
                if (choice.isPresent() && choice.get() != ButtonType.OK) {
                    event.consume();
                }
            }
        }
    }

    class OpenMapHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent actionEvent) {
            Map<String, Location> locations = new HashMap<>();
            graph = new ListGraph<>();
//            if (changes) {
//                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                alert.setTitle("Warning!");
//                alert.setContentText("Unsaved changes, continue anyway?");
//                alert.setHeaderText(null);
//                Optional<ButtonType> choice = alert.showAndWait();
//                if (choice.isPresent() && choice.get() != ButtonType.OK) {
//                    return;
//                }
//            }
            try {
                FileReader file = new FileReader("europa.graph");
                BufferedReader in = new BufferedReader(file);
                String line = in.readLine();
                openMap(line);
                line = in.readLine();
                String[] tokens = line.split(";");
                for (int i = 0; i < tokens.length; i += 3) {
                    Location loc = new Location(tokens[i], Double.parseDouble(tokens[i + 1]), Double.parseDouble(tokens[i + 2]));
                    graph.add(loc);
                    locations.put(loc.getName(), loc);
                }
                while ((line = in.readLine()) != null) {
                    tokens = line.split(";");
                    if (locations.containsKey(tokens[0]) && locations.containsKey(tokens[1])) {
                        if (graph.getEdgeBetween(locations.get(tokens[0]), locations.get(tokens[1])) == null) {
                            graph.connect(locations.get(tokens[0]), locations.get(tokens[1]), tokens[2], Integer.parseInt(tokens[3]));
                        }
                    }

                }
                in.close();
                file.close();
                loadGraphToMap(graph);
            } catch (FileNotFoundException e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("Could not find File: europa.graph");
                error.showAndWait();
            } catch (IOException e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText(e.getMessage());
                error.showAndWait();
            }
        }
    }

    class SaveMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try {
                FileWriter file = new FileWriter("europa1.graph");// change this later this is for testing
                PrintWriter out = new PrintWriter(file);
                out.println("file:europa.gif");
                for (Location loc : graph.getNodes()) {
                    out.format("%s;%.01f;%.01f;", loc.getName(), loc.getX(), loc.getY());
                }
                out.println();
                for (Location loc : graph.getNodes()) {
                    for (Edge<Location> edge : graph.getEdgesFrom(loc)) {
                        out.format("%s;%s;%s;%d\n", loc.getName(), edge.getDestination().getName(), edge.getName(), edge.getWeight());
                    }
                }
                out.close();
                file.close();
                changes = false;
            } catch (FileNotFoundException e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText("Could not write to File: europa.graph");
                error.showAndWait();

            } catch (IOException e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.setContentText(e.getMessage());
                error.showAndWait();
            }
        }
    }

    private void loadGraphToMap(ListGraph<Location> graph) {
        for (Location loc : graph.getNodes()) {
            loc.setOnMouseClicked(new ClickHandler());
            map.getChildren().add(loc);
            Text cityName = new Text(loc.getX() + loc.getRadius(), loc.getY() + loc.getRadius(), loc.getName());

            // x + 5 and y + 15 is moving them a few pixels so  that the text gets away from the nodes, so it is easier to read
            cityName.setStrokeWidth(5);
            cityName.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
            cityName.setDisable(true);
            map.getChildren().add(cityName);

            for (Edge<Location> edge : graph.getEdgesFrom(loc)) {
                Line connection = new Line(loc.getX(), loc.getY(), edge.getDestination().getX(), edge.getDestination().getY());
                connection.setStrokeWidth(2);
                connection.setDisable(true); // makes lines not clickable on map easier to click nodes
                map.getChildren().add(connection);
            }
        }
    }

    class SaveImgHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            try {
                WritableImage image = root.snapshot(null, null);
                BufferedImage buffImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(buffImage, "png", new File("capture.png"));
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "" + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    class NewLocationHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            graph = new ListGraph<>();
            root.cursorProperty().setValue(Cursor.CROSSHAIR);
            newPlace.setDisable(true);


            map.setOnMouseClicked(mouseEvent -> {
                TextInputDialog nameOfLoc = new TextInputDialog("Name");
                nameOfLoc.setHeaderText(null);
                nameOfLoc.setContentText("Name of place:");
                Optional<String> name = nameOfLoc.showAndWait();
                if (name.isPresent()) {
                    Location loc = new Location(name.get(), mouseEvent.getX(), mouseEvent.getY());
                    graph.add(loc);
                    loadGraphToMap(graph);
                }
                root.cursorProperty().setValue(Cursor.DEFAULT);
                newPlace.setDisable(false);
                map.setOnMouseClicked(null);
            });


        }
    }

    class ClickHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            Location loc = (Location) event.getSource();
            if (locA == null && !loc.equals(locB)) {
                locA = loc;
                locA.flipColor();
            } else if (locB == null && !loc.equals(locA)) {
                locB = loc;
                locB.flipColor();
            } else if (locA != null && locA.equals(loc)) {
                locA.flipColor();
                locA = null;
            } else if (locB != null && locB.equals(loc)) {
                locB.flipColor();
                locB = null;
            }
        }
    }
}