// PROG2 VT2023, Inlämningsuppgift, del 2
// Grupp 230
// Jimmie Nilsson jini6619



import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class PathFinder extends Application {

    // Change variable names later
    // clean up code
    // MAKE MORE METHODS REPEATING WAY TOO MUCH CODE
    // CHANGE OPEN MAP AND SAVE MAP FROM europa1 to europa
    private static final String MAP_NAME = "file:europa.gif";
    private ListGraph<Location> graph = new ListGraph<>();
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
    private Location locA;
    private Location locB;
    private boolean changes;


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
        findPath.setOnAction(new ShowPathHandler());
        showCon = new Button("Show Connection");
        showCon.setOnAction(new ConnectionHandler());
        newPlace = new Button("New Place");
        newPlace.setOnAction(new NewLocationHandler());
        newCon = new Button("New Connection");
        newCon.setOnAction(new ConnectionHandler());
        changeCon = new Button("Change connection");
        changeCon.setOnAction(new ConnectionHandler());
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(findPath, showCon, newPlace, newCon, changeCon);
        buttons.setDisable(true);


        map = new Pane();
        root.getChildren().add(map);
        map.setVisible(false);
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
        map.setVisible(true);
        changes = true; // this creates a bug when you open a file from a file but make no changes.


    }

    private void setIDs() {
        menuBar.setId("menu");
        fileMenu.setId("menuFile");
        menuFileNewMap.setId("menuNewMap");
        menuFileOpen.setId("menuOpenFile");
        menuSave.setId("menuSaveFile");
        menuSaveImg.setId("menuSaveImage");
        menuExit.setId("menuExit");
        findPath.setId("btnFindPath");
        showCon.setId("btnShowConnection");
        newPlace.setId("btnNewPlace");
        changeCon.setId("btnChangeConnection");
        newCon.setId("btnNewConnection");
        map.setId("outputArea");


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
            locA = null;
            locB = null;
            try {
                FileReader file = new FileReader("europa1.graph"); // CHANGE THIS
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
                changes = false;
            } catch (FileNotFoundException e) {
                showErrorAlert("Could not find File: europa.graph");
            } catch (IOException e) {
                showErrorAlert(e.getMessage());
            }
        }
    }

    class SaveMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try {
                FileWriter file = new FileWriter("europa1.graph"); // CHANGE THIS
                PrintWriter out = new PrintWriter(file);
                out.println("file:europa.gif");
                DecimalFormat df = new DecimalFormat("###.##");

                for (Location loc : graph.getNodes()) {
                    // out.format("%s;%.01f;%.01f;", loc.getName(), loc.getCenterX(), loc.getCenterY()); THIS DOESN'T WORK BECAUSE OF LOCALE SETTINGS
                    out.format("%s;%.05s;%.05s;", loc.getName(), loc.getCenterX(), loc.getCenterY());
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
                showErrorAlert("Could not write to File: europa.graph");

            } catch (IOException e) {
                showErrorAlert(e.getMessage());
            }
        }
    }

    private void loadGraphToMap(ListGraph<Location> graph) {
        for (Location loc : graph.getNodes()) {
            loc.setOnMouseClicked(new ClickHandler());
            map.getChildren().add(loc);
            loadCityTextToMap(loc);
            loc.setId(loc.getName()); // check this later

            for (Edge<Location> edge : graph.getEdgesFrom(loc)) {
                loadConnectionToMap(loc, edge.getDestination());
            }
        }
    }

    private void loadConnectionToMap(Location locationA, Location locationB) {
        Line connection = new Line(locationA.getCenterX(), locationA.getCenterY(), locationB.getCenterX(), locationB.getCenterY());
        connection.setStrokeWidth(2);
        connection.setDisable(true); // makes lines not clickable on map easier to click nodes
        map.getChildren().add(connection);
    }

    private void loadCityTextToMap(Location city) {
        Text cityName = new Text(city.getCenterX() + city.getRadius(), city.getCenterY() + city.getRadius(), city.getName());
        cityName.setStrokeWidth(5);
        cityName.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.REGULAR, 12));
        cityName.setDisable(true);
        map.getChildren().add(cityName);
    }

    class SaveImgHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            try {
                WritableImage image = root.snapshot(null, null);
                BufferedImage buffImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(buffImage, "png", new File("capture.png"));
            } catch (IOException e) {
                showErrorAlert(e.getMessage());
            }
        }
    }

    class NewLocationHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            root.cursorProperty().setValue(Cursor.CROSSHAIR);
            newPlace.setDisable(true);
            map.setOnMouseClicked(mouseEvent -> {
                TextInputDialog nameOfLoc = new TextInputDialog("Name");
                nameOfLoc.setHeaderText(null);
                nameOfLoc.setContentText("Name of place:");
                Optional<String> name = nameOfLoc.showAndWait();
                if (name.isPresent() && !name.get().equals("")) {
                    Location loc = new Location(name.get(), mouseEvent.getX(), mouseEvent.getY());
                    graph.add(loc);
                    loc.setOnMouseClicked(new ClickHandler());
                    map.getChildren().add(loc);
                    loadCityTextToMap(loc);
                } else if (name.isPresent()) {
                    showErrorAlert("Name can not be empty!");
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
                if (locB != null) {
                    locA = locB;
                    locB = null;
                }
            } else if (locB != null && locB.equals(loc)) {
                locB.flipColor();
                locB = null;
            }
        }
    }

    class ConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (locA == null || locB == null) {
                showErrorAlert("Two places must be selected!");
            } else {
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));
                TextField nameInput = new TextField();
                TextField timeInput = new TextField();
                grid.add(new Label("Name:"), 0, 0);
                grid.add(nameInput, 1, 0);
                grid.add(new Label("Time:"), 0, 1);
                grid.add(timeInput, 1, 1);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Connection");
                alert.setHeaderText("Connection from " + locA.getName() + " to " + locB.getName());
                alert.getDialogPane().setContent(grid);

                if (graph.getEdgeBetween(locA, locB) == null && event.getSource().equals(newCon)) {
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        if (nameInput.getText().equals("") || timeInput.getText().equals("")) {
                            showErrorAlert("Both fields are required!");
                            return;
                        }
                        String name = nameInput.getText();
                        String timeText = timeInput.getText();
                        try {
                            int time = Integer.parseInt(timeText);
                            graph.connect(locA, locB, name, time);
                            loadConnectionToMap(locA, locB);
                            changes = true;
                        } catch (NumberFormatException e) {
                            showErrorAlert("Wrong format in Time field only positive whole numbers are allowed!");
                        }
                    }
                }else if (event.getSource().equals(showCon) || event.getSource().equals(changeCon)) {
                    if (graph.getEdgeBetween(locA, locB) == null){
                        showErrorAlert("No connection between: " +locA.getName() + " and: " + locB.getName());
                        return;
                    }


                    if (event.getSource().equals(showCon)) {
                        nameInput.setText(graph.getEdgeBetween(locA, locB).getName());
                        nameInput.setEditable(false);
                        timeInput.setText("" + graph.getEdgeBetween(locA, locB).getWeight());
                        timeInput.setEditable(false);
                        alert.showAndWait();



                    }else if (event.getSource().equals(changeCon)){
                        nameInput.setText(graph.getEdgeBetween(locA, locB).getName());
                        nameInput.setEditable(false);
                        timeInput.setEditable(true);
                        timeInput.setText("");
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK){
                            try{
                                int time = Integer.parseInt(timeInput.getText());
                                if (time < 0){
                                    showErrorAlert("Only positive numbers allowed!");
                                    return;
                                }
                                graph.setConnectionWeight(locA, locB, time);
                                changes = true;
                            }catch (NumberFormatException e){
                                showErrorAlert("Wrong format in Time field only positive whole numbers are allowed!");
                            }
                        }
                    }
                }
            }
        }
    }

    class ShowPathHandler implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            // use Path exists and Gather path her
            if (locA == null || locB == null){
                showErrorAlert("Two Locations must be selected");
                return;
            }
            if (!graph.pathExists(locA, locB)){
                showErrorAlert("No path between: " + locA.getName() + " and " + locB.getName());
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Connection");
            alert.setHeaderText("Path from " + locA.getName() + " to " + locB.getName() + ":");
            TextArea textArea = new TextArea();
            alert.getDialogPane().setContent(textArea);
            ArrayList<Edge<Location>> locations = new ArrayList<>(graph.getPath(locA, locB));
            StringBuilder sb = new StringBuilder();
            int totalTime = 0;
            for (Edge<Location> edge : locations){
               // sb.append(edge); this doesn't work the way I want it too.
                sb.append("to ");
                sb.append(edge.getDestination().getName());
                sb.append(" by ");
                sb.append(edge.getName());
                sb.append(" takes ");
                sb.append(edge.getWeight());
                sb.append("\n");
                totalTime += edge.getWeight();
            }
            textArea.setText(sb.toString() + "Total " + totalTime);
            alert.showAndWait();
        }
    }

    private void showErrorAlert(String textToDisplay) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(null);
        alert.setContentText(textToDisplay);
        alert.showAndWait();
    }
}