import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ConnectionAlert extends Alert {


    private TextField nameField = new TextField();
    private TextField timeField = new TextField();

    public ConnectionAlert(String fromName, String toName) {
        super(AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Time:"), 0, 1);
        grid.add(timeField, 1, 1);
        getDialogPane().setContent(grid);
        setTitle("Connection");
        setHeaderText("Connection from " + fromName + " to " + toName);


    }

    public String getName() {
        return nameField.getText();
    }

    public String getTime() {
        return timeField.getText();
    }

    public void setName(String name) {
        nameField.setText(name);
    }

    public void setTime(String time) {
        timeField.setText(time);
    }
    public void setNameEditable(boolean bool){
        nameField.setEditable(bool);
    }
    public void setTimeEditable(boolean bool){
        timeField.setEditable(bool);
    }
}
