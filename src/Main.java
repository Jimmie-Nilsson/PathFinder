import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private TextField textField;
    private Label label;
    private Label hejLabel;
    public void start(Stage primaryStage){
        Pane root = new FlowPane();
        root.setStyle("-fx-font-size: 18");
        label = new Label("Hälsa");
        hejLabel = new Label("Hej");
        textField = new TextField();
        Button button = new Button();
        root.getChildren().addAll(label,textField,hejLabel,button);
        Scene scene = new Scene(root,300,200);
        button.setOnAction(new Handler());
        primaryStage.setScene(scene);
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