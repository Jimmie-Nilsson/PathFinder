import java.util.*;

public class ListGraph<T> implements Graph<T> {

    private Map<T, Set<Edge<T>>> nodes = new HashMap<>();


    public void add(T node) {
        nodes.putIfAbsent(node, new HashSet<>());
    }

    public void remove(T node) {
        checkNode(node);
        for (Set<Edge<T>> set : nodes.values()) {
            Iterator<Edge<T>> it = set.iterator();
            while (it.hasNext()) {
                Edge<T> edge = it.next();
                if (edge.getDestination().equals(node)) {
                    it.remove();
                }
            }
        }
        nodes.remove(node);

    }

    @Override
    public boolean pathExists(T from, T to) {
        if (!nodes.containsKey(from) || !nodes.containsKey(to))
            return false;
        Set<T> visited = new HashSet<>();
        depthFirstSearch(from, to, visited);
        return visited.contains(to);
    }

    @Override
    public List<Edge<T>> getPath(T from, T to) {
        if (!nodes.containsKey(from) || !nodes.containsKey(to))
            return null;
        Map<T, T> connections = new HashMap<>();
        LinkedList<T> queue = new LinkedList<>();

        connections.put(from, null);
        queue.add(from);

        while (!queue.isEmpty()) {
            T t = queue.pollFirst();
            for (Edge<T> edge : nodes.get(t)) {
                T destination = edge.getDestination();
                if (!connections.containsKey(destination)) {
                    connections.put(destination, t);
                    queue.add(destination);
                }
            }
        }
        if (!connections.containsKey(to)) {
            return null;
        }
        return gatherPath(from, to, connections);
    }

    public void connect(T nodeA, T nodeB, String name, int weight) {
        checkNodes(nodeA, nodeB);
        if (weight < 0) {
            throw new IllegalArgumentException();
        }
        Edge<T> edgeA = new Edge<T>(nodeB, name, weight);
        Edge<T> edgeB = new Edge<T>(nodeA, name, weight);

        for (Edge<T> e : nodes.get(nodeA)) {
            if (e.getDestination().equals(edgeA.getDestination())) {
                throw new IllegalStateException();
            }
        }
        nodes.get(nodeA).add(edgeA);
        nodes.get(nodeB).add(edgeB);
    }

    @Override
    public void setConnectionWeight(T nodeA, T nodeB, int weight) {
        checkNodes(nodeA, nodeB);
        if (weight < 0)
            throw new IllegalArgumentException();


        if (getEdgeBetween(nodeA, nodeB) == null)
            throw new NoSuchElementException();

        Edge<T> edgeA = getEdgeBetween(nodeA, nodeB);
        edgeA.setWeight(weight);
        Edge<T> edgeB = getEdgeBetween(nodeB, nodeA);
        edgeB.setWeight(weight);
    }

    @Override
    public Set<T> getNodes() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    @Override
    public Collection<Edge<T>> getEdgesFrom(T node) {

        checkNode(node);
        return nodes.get(node);
    }

    @Override
    public Edge<T> getEdgeBetween(T node, T destination) {
        checkNodes(node, destination);
        for (Edge<T> edge : nodes.get(node)) {
            if (edge.getDestination().equals(destination)) {
                return edge;
            }
        }
        return null;
    }

    @Override
    public void disconnect(T nodeA, T nodeB) {

        checkNodes(nodeA, nodeB);
        Edge<T> edgeA = getEdgeBetween(nodeA, nodeB);
        Edge<T> edgeB = getEdgeBetween(nodeB, nodeA);
        checkEdges(edgeA, edgeB);
        getEdgesFrom(nodeA).remove(edgeA);
        getEdgesFrom(nodeB).remove(edgeB);
    }

    private void checkNodes(T nodeA, T nodeB) {
        if (!nodes.containsKey(nodeA) || !nodes.containsKey(nodeB))
            throw new NoSuchElementException();
    }

    private void checkNode(T node) {
        if (!nodes.containsKey(node))
            throw new NoSuchElementException();
    }

    private void checkEdges(Edge<T> edgeA, Edge<T> edgeB) {
        if (edgeA == null || edgeB == null)
            throw new IllegalStateException();

    }

    private void depthFirstSearch(T from, T destination, Set<T> visited) {
        visited.add(from);

        if (from.equals(destination)) {
            return;
        }
        for (Edge<T> edge : nodes.get(from)) {
            T t = edge.getDestination();
            if (!visited.contains(t)) {
                 depthFirstSearch(t, destination, visited);
            }
        }
    }
    private List<Edge<T>> gatherPath(T from, T to, Map<T,T> connections){
        LinkedList<Edge<T>> path = new LinkedList<>();
        T current = to;


        while (!current.equals(from)){
            T next = connections.get(current);
            Edge<T> edge = getEdgeBetween(next, current);
            path.addFirst(edge);
            current = next;
        }
        return path;
    }

    public String toString(){
        return nodes.toString();
    }

}