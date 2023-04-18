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
        Set<T> visited = new HashSet<>();
        dfs(from, to, visited);
        return visited.contains(to);
    }

    @Override
    public List<Edge<T>> getPath(T from, T to) {
//        Set<T> visited = new HashSet<>();
//        Stack<T> path = new Stack<>();
//        path.push(from);
//
//        depthFirstSearch(from,to,visited,path);
//        List<Edge<T>> list = (List<Edge<T>>) (Object) Arrays.asList(path.toArray());
//        return list;
        return null;
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

    //    private Stack<T> depthFirstSearch(T from, T destination, Set<T> visited, Stack<T> pathSoFar) {
//        visited.add(from);
//
//        if (from.equals(destination)){
//            return pathSoFar;
//        }
//        for (Edge<T> edge : nodes.get(from)){
//            T t = edge.getDestination();
//            if (!visited.contains(t)){
//                pathSoFar.push(t);
//                Stack<T> ts = depthFirstSearch(t, destination,visited,pathSoFar);
//                if (!ts.isEmpty()){
//                    return ts;
//                }else{
//                    pathSoFar.pop();
//                }
//            }
//        }
//        return new Stack<T>();
//    }
    private void dfs(T from, T destination, Set<T> visited) {
        visited.add(from);

        if (from.equals(destination)) {
            return;
        }
        for (Edge<T> edge : nodes.get(from)) {
            if (!visited.contains(edge.getDestination())) {
                dfs(edge.getDestination(), destination, visited);
            }
        }

    }
}