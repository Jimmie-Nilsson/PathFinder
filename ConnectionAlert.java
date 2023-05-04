import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ConnectionAlert extends Alert {


    private TextField inputOne = new TextField();
    private TextField inputTwo = new TextField();

    public ConnectionAlert(String labelOne, String labelTwo) {
        super(AlertType.CONFIRMATION);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label(labelOne), 0, 0);
        grid.add(inputOne, 1, 0);
        grid.add(new Label(labelTwo), 0, 1);
        grid.add(inputTwo, 1, 1);
        getDialogPane().setContent(grid);

    }

    public String getName() {
        return inputOne.getText();
    }

    public String getTime() {
        return inputTwo.getText();
    }

    public void setName(String name) {
        inputOne.setText(name);
    }

    public void setTime(String time) {
        inputTwo.setText(time);
    }
    public void setEditableOne(boolean bool){
        inputOne.setEditable(bool);
    }
    public void setEditableTwo(boolean bool){
        inputTwo.setEditable(bool);
    }
}
