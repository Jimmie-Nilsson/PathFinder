// PROG2 VT2023, Inlämningsuppgift, del 1
// Grupp 230
// Jimmie Nilsson jini6619



public class Edge<T> {

    private final T destination;
    private final String name;
    private int weight;


    public Edge(T destination, String name, int weight){
        this.destination = destination;
        this.name = name;
        this.weight = weight;
    }

    public T getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int newWeight) {
        if (newWeight < 0) {
            throw new IllegalArgumentException("Illegal weight");
        }
        weight = newWeight;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return String.format("to "+ destination +" by " +getName() + " takes %d",weight);
    }
}
