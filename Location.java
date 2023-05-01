// PROG2 VT2023, Inlämningsuppgift, del 2
// Grupp 230
// Jimmie Nilsson jini6619
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Objects;

public class Location extends Circle {
    private final String name;
    private Color color;


    public Location(String name, double x, double y) {
        super(x, y, 10, Color.BLUE);
        this.color = Color.BLUE;
        setFill(Color.BLUE);
        this.name = name;
    }

    public String getName() {
        return name;
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
            return other.name.equals(this.name) && other.getCenterX() == this.getCenterX() && other.getCenterY() == this.getCenterY();
        }
        return false;
    }

   @Override
   public int hashCode(){
        return Objects.hash(name,getCenterX(),getCenterY());
   }

    public String toString() {
        return String.format("%s [%.01f  %.01f]", name, getCenterX(), getCenterY());
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
