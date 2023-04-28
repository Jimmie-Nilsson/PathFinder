import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Location extends Circle {
    private final String name;
    private final double x;
    private final double y;
    private Color color;


    public Location(String name, double x, double y) {
        super(x, y, 10, Color.BLUE);
        this.color = Color.BLUE;
        setFill(Color.BLUE);
        this.name = name;
        this.x = x;
        this.y = y;
        //setOnMouseClicked(new ClickHandler());
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    public void flipColor(){
        if (color.equals(Color.RED)){
            this.color = Color.BLUE;
            setFill(color);
        }else {
            this.color = Color.RED;
            setFill(color);
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Location other) {
            return other.name.equals(this.name) && other.x == this.x && other.y == this.y;
        }
        return false;
    }

    public String toString() {
        return String.format("%s [%.01f  %.01f]", name, x, y);
    }

//    class ClickHandler implements EventHandler<MouseEvent> {
//        @Override
//        public void handle(MouseEvent event) {
//            if (color.equals(Color.BLUE)) {
//                setFill(Color.RED);
//                color = Color.RED;
//            } else {
//                setFill(Color.BLUE);
//                color = Color.BLUE;
//            }
//        }
//    }
}
