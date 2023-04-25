import javafx.scene.Node;

public class Location {
    private final String name;
    private final double x;
    private final double y;


    public Location(String name, double x, double y){
        this.name = name;
        this.x = x;
        this.y = y;
    }
    public String getName(){
        return name;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Location other){
            return other.name.equals(this.name) && other.x == this.x && other.y == this.y;
        }
        return false;
    }
    public String toString(){
        return String.format("%s Latitude: %01f Longitude: %01f", name, x, y);
    }
}
