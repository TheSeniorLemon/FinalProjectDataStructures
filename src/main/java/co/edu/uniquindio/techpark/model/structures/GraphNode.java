package co.edu.uniquindio.techpark.model.structures;

public class GraphNode<T extends Comparable<T>> {
    private T data;
    private LinkedList<GraphEdge<T>> edges;
    private boolean visited;
    private double distance;
    private GraphNode<T> previous;

    public GraphNode(T data) {
        this.data = data;
        this.edges = new LinkedList<>();
        this.visited = false;
        this.distance = Double.MAX_VALUE;
        this.previous = null;
    }

    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }

    public LinkedList<GraphEdge<T>> getEdges() {
        return edges;
    }
    public void setEdges(LinkedList<GraphEdge<T>> edges) {
        this.edges = edges;
    }

    public boolean isVisited() {
        return visited;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }

    public GraphNode<T> getPrevious() {
        return previous;
    }
    public void setPrevious(GraphNode<T> previous) {
        this.previous = previous;
    }

    @Override
    public String toString() {
        return "GraphNode{" +
                "data=" + data +
                ", edges=" + edges +
                ", visited=" + visited +
                ", distance=" + (distance == Double.MAX_VALUE ? "INF" : distance) +
                ", previous=" + previous +
                '}';
    }

    public void addEdge(GraphEdge<T> edge) {
        if (edge == null) return;
        edges.add(edge);
    }

    public boolean removeEdge(GraphNode<T> destination) {
        int n = edges.getSize();
        for (int i = 0; i < n; i++) {
            GraphEdge<T> e = edges.get(i);
            if (e != null && e.getDestination().equals(destination)) {
                return edges.remove(e);
            }
        }
        return false;
    }

    public boolean hasEdgeTo(GraphNode<T> destination) {
        int n = edges.getSize();
        for (int i = 0; i < n; i++) {
            GraphEdge<T> e = edges.get(i);
            if (e != null && e.getDestination().equals(destination)) return true;
        }
        return false;
    }

    public void reset() {
        this.visited = false;
        this.distance = Double.MAX_VALUE;
        this.previous = null;
    }
}