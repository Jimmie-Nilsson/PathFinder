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
import java.util.*;

public class PathFinder extends Application {

    // CHANGE setIDs and make them in the start method, so I can convert variables to local variables.
    // ASK ABOUT CHANGE ABOVE
    // if you click the menu after clicking new location it doesn't reset the crosshair or the map FIX ? ask about this.

    private static final String MAP_NAME = "file:europa.gif";
    private ListGraph<Location> graph = new ListGraph<>();
    private VBox root;
    private Pane map;
    private FlowPane buttons;
    private Button newPlace;
    private Stage primaryStage;
    private Location locationOne;
    private Location locationTwo;
    private boolean changes;


    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new VBox();
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem menuFileNewMap = new MenuItem("New Map");
        menuFileNewMap.setOnAction(event -> {
            openMap(MAP_NAME);
            changes = true;
        });

        MenuItem menuFileOpen = new MenuItem("Open");
        menuFileOpen.setOnAction(new OpenMapHandler());

        MenuItem menuSave = new MenuItem("Save");
        menuSave.setOnAction(new SaveMapHandler());

        MenuItem menuSaveImg = new MenuItem("Save Image");
        menuSaveImg.setOnAction(new SaveImgHandler());

        MenuItem menuExit = new MenuItem("Exit");
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

        Button findPath = new Button("Find Path");
        findPath.setOnAction(new ShowPathHandler());

        Button showCon = new Button("Show Connection");
        showCon.setOnAction(new ShowConnectionHandler());

        newPlace = new Button("New Place");
        newPlace.setOnAction(new NewLocationHandler());

        Button newCon = new Button("New Connection");
        newCon.setOnAction(new NewConnectionHandler());

        Button changeCon = new Button("Change connection");
        changeCon.setOnAction(new ChangeConnectionHandler());

        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(findPath, showCon, newPlace, newCon, changeCon);
        buttons.setDisable(true);


        map = new Pane();
        map.setVisible(false);
        root.getChildren().add(menuBar);
        root.getChildren().add(buttons);
        root.getChildren().add(map);


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


        Scene scene = new Scene(root, 550, 100);
        primaryStage.setOnCloseRequest(new ExitHandler());
        primaryStage.setScene(scene);
        primaryStage.setTitle("PathFinder");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void openMap(String mapName) {
        locationOne = null;
        locationTwo = null;
        graph = new ListGraph<>();
        if (changes) {
            if (unsavedChangesAlert())
                return;
        }

        map.getChildren().clear();
        Image background = new Image(mapName);
        ImageView bg = new ImageView(background);
        map.getChildren().add(bg);
        // I could add all the elements here, but it doesn't look as nice as if I did it manually.
        primaryStage.setHeight(background.getHeight() + 110); // 110 is extra pixels by other elements
        primaryStage.setWidth(background.getWidth() + 15); // 15 is padding to make the map look better in the scene
        buttons.setDisable(false);
        map.setVisible(true);
    }
// I decided not to use this method because then all the variables needed to be declared outside the start method.
    // so I decided to have a little messier start method to save declaring variables outside the start method.

//    private void setIDs() {
//        menuBar.setId("menu");
//        fileMenu.setId("menuFile");
//        menuFileNewMap.setId("menuNewMap");
//        menuFileOpen.setId("menuOpenFile");
//        menuSave.setId("menuSaveFile");
//        menuSaveImg.setId("menuSaveImage");
//        menuExit.setId("menuExit");
//        findPath.setId("btnFindPath");
//        showCon.setId("btnShowConnection");
//        newPlace.setId("btnNewPlace");
//        changeCon.setId("btnChangeConnection");
//        newCon.setId("btnNewConnection");
//        map.setId("outputArea");
//    }

    class OpenMapHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent actionEvent) {
            if (changes) {
                if (unsavedChangesAlert())
                    return;
            }
            try {
                changes = false;
                loadGraphFromFile();
                loadGraphToMap(graph);
            } catch (FileNotFoundException error) {
                showErrorAlert("Could not find File: europa.graph");
            } catch (IOException error) {
                showErrorAlert(error.getMessage());
            } catch (IndexOutOfBoundsException error) {
                showErrorAlert("File does not contain any Locations");
            }
        }
    }

    private void loadGraphFromFile() throws IOException {
        Map<String, Location> locations = new HashMap<>();
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
    }

    private boolean unsavedChangesAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning!");
        alert.setContentText("Unsaved changes, continue anyway?");
        alert.setHeaderText(null);
        Optional<ButtonType> choice = alert.showAndWait();
        return choice.isPresent() && choice.get() != ButtonType.OK;
    }

    private void loadGraphToMap(ListGraph<Location> graph) {
        for (Location loc : graph.getNodes()) {
            loadLocationToMap(loc);

            for (Edge<Location> edge : graph.getEdgesFrom(loc)) {
                loadConnectionToMap(loc, edge.getDestination());
            }
        }
    }

    private void loadLocationToMap(Location loc) {
        loc.setOnMouseClicked(new ClickHandler());
        map.getChildren().add(loc);
        loadCityTextToMap(loc);
        loc.setId(loc.getName());
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
                    loadLocationToMap(loc);
                    changes = true;

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
            if (locationOne == null && !loc.equals(locationTwo)) {
                locationOne = loc;
                locationOne.flipColor();
            } else if (locationTwo == null && !loc.equals(locationOne)) {
                locationTwo = loc;
                locationTwo.flipColor();
            } else if (locationOne != null && locationOne.equals(loc)) {
                locationOne.flipColor();
                locationOne = null;
                if (locationTwo != null) {
                    locationOne = locationTwo;
                    locationTwo = null;
                }
            } else if (locationTwo != null && locationTwo.equals(loc)) {
                locationTwo.flipColor();
                locationTwo = null;
            }
        }
    }

    class ChangeConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (isSelectionInvalid()) {
                showErrorAlert("Two places must be selected!");
                return;
            }
            if (graph.getEdgeBetween(locationOne, locationTwo) == null) {
                showErrorAlert("No connection between: " + locationOne.getName() + " and: " + locationTwo.getName());
                return;
            }
            ConnectionForm dialog = new ConnectionForm(locationOne.getName(), locationTwo.getName());
            dialog.setName(graph.getEdgeBetween(locationOne, locationTwo).getName());
            dialog.setTime("");
            dialog.setNameEditable(false);
            Optional<ButtonType> answer = dialog.showAndWait();
            if (answer.isPresent() && answer.get() == ButtonType.OK) {
                try {
                    int time = Integer.parseInt(dialog.getTime());
                    graph.setConnectionWeight(locationOne, locationTwo, time);
                    changes = true;
                } catch (NumberFormatException error) {
                    showErrorAlert("Wrong format in Time field only positive whole numbers are allowed!");
                } catch (IllegalArgumentException error) {
                    showErrorAlert("Time is not allowed to be negative!");
                }
            }
        }
    }

    class ShowConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (isSelectionInvalid()) {
                showErrorAlert("Two places must be selected!");
                return;
            }
            if (graph.getEdgeBetween(locationOne, locationTwo) == null) {
                showErrorAlert("No connection between: " + locationOne.getName() + " and: " + locationTwo.getName());
                return;
            }
            ConnectionForm dialog = new ConnectionForm(locationOne.getName(), locationTwo.getName());
            dialog.setName(graph.getEdgeBetween(locationOne, locationTwo).getName());
            dialog.setTime("" + graph.getEdgeBetween(locationOne, locationTwo).getWeight());
            dialog.setNameEditable(false);
            dialog.setTimeEditable(false);
            dialog.showAndWait();
        }
    }

    class NewConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (isSelectionInvalid()) {
                showErrorAlert("Two places must be selected!");
                return;
            }
            if (graph.getEdgeBetween(locationOne, locationTwo) != null) {
                showErrorAlert("Connection already present only 1 connection allowed!");
                return;
            }
            ConnectionForm dialog = new ConnectionForm(locationOne.getName(), locationTwo.getName());
            try {
                Optional<ButtonType> answer = dialog.showAndWait();
                if (answer.isPresent() && answer.get() == ButtonType.OK) {
                    if (dialog.getName().equals("") || dialog.getTime().equals("")) {
                        showErrorAlert("Both fields are required!");
                        return;
                    }
                    String name = dialog.getName();
                    int time = Integer.parseInt(dialog.getTime());
                    graph.connect(locationOne, locationTwo, name, time);
                    loadConnectionToMap(locationOne, locationTwo);
                    changes = true;
                }
            } catch (NumberFormatException error) {
                showErrorAlert("Wrong format in Time field only positive whole numbers are allowed!");
            } catch (IllegalArgumentException error) {
                showErrorAlert("Time is not allowed to be negative!");
            }
        }
    }

    private boolean isSelectionInvalid() {
        return locationOne == null || locationTwo == null;
    }

    class ShowPathHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (locationOne == null || locationTwo == null) {
                showErrorAlert("Two Locations must be selected");
                return;
            }
            if (!graph.pathExists(locationOne, locationTwo)) {
                showErrorAlert("No path between: " + locationOne.getName() + " and " + locationTwo.getName());
                return;
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Connection");
            alert.setHeaderText("Path from " + locationOne.getName() + " to " + locationTwo.getName() + ":");
            TextArea textArea = new TextArea();
            alert.getDialogPane().setContent(textArea);
            ArrayList<Edge<Location>> locations = new ArrayList<>(graph.getPath(locationOne, locationTwo));
            StringBuilder sb = new StringBuilder();
            int totalTime = 0;
            for (Edge<Location> edge : locations) {
                sb.append(edge);
                sb.append("\n");
                totalTime += edge.getWeight();
            }
            textArea.setText(sb + "Total " + totalTime);
            alert.showAndWait();
        }
    }

    class SaveMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try {
                FileWriter file = new FileWriter("europa.graph");
                PrintWriter out = new PrintWriter(file);
                out.println("file:europa.gif");
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

    private void showErrorAlert(String textToDisplay) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText(null);
        alert.setContentText(textToDisplay);
        alert.showAndWait();
    }
}