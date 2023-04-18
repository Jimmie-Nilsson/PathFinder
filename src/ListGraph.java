import java.util.*;

public class ListGraph<T> implements Graph<T> {

    private Map<T, Set<Edge<T>>> nodes = new HashMap<>();


    public void add(T node) {
        nodes.putIfAbsent(node, new HashSet<>());
    }

    public void remove(T node){
        if(!nodes.containsKey(node)){
            throw new NoSuchElementException();
        }

        for (Set<Edge<T>> set : nodes.values()){
            Iterator<Edge<T>> it = set.iterator();
            while (it.hasNext()){
                 Edge<T> edge = it.next();
                 if(edge.getDestination().equals(node)){
                     it.remove();
                }
            }
        }
        nodes.remove(node);

    }

    @Override
    public boolean pathExists(T from, T to) {
        return false;
    }

    @Override
    public List<Edge<T>> getPath(T from, T to) {
        return null;
    }

    public void connect (T nodeA, T nodeB, String name, int weight){
        if (!nodes.containsKey(nodeA) || !nodes.containsKey(nodeB)){
            throw new NoSuchElementException();
        }
        if (weight < 0){
            throw new IllegalArgumentException();
        }
        Edge<T> edgeA = new Edge<T>(nodeB, name, weight);
        Edge<T> edgeB = new Edge<T>(nodeA, name, weight);

        for (Edge<T> e : nodes.get(nodeA)){
            if (e.getDestination().equals(edgeA.getDestination())) {
                throw new IllegalStateException();
            }
        }
        nodes.get(nodeA).add(edgeA);
        nodes.get(nodeB).add(edgeB);
    }

    @Override
    public void setConnectionWeight(T node1, T node2, int weight) {

    }

    @Override
    public Set<T> getNodes() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    @Override
    public Collection<Edge<T>> getEdgesFrom(T node) {

        if (!nodes.containsKey(node)) {
            throw new NoSuchElementException();
        }
        return nodes.get(node);
    }
    @Override
    public Edge<T> getEdgeBetween(T node, T destination) {
        if (!nodes.containsKey(node) || !nodes.containsKey(destination)){
            throw new NoSuchElementException();
        }
        for (Edge<T> edge : nodes.get(node)){
            if (edge.getDestination().equals(destination)){
                return edge;
            }
        }
        return null;
    }

    @Override
    public void disconnect(T nodeA, T nodeB) {
        Edge<T> edge = getEdgeBetween(nodeA, nodeB);
        nodes.remove(edge);



    }
}