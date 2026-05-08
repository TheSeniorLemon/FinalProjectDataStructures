package co.edu.uniquindio.techpark.model.structures;

public class GraphEdge<T extends Comparable<T>> {
    private GraphNode<T> destination;
    private double weight;
    private String description;

    public GraphEdge(GraphNode<T> destination, double weight, String description) {
        this.destination = destination;
        this.weight = weight;
        this.description = description;
    }

    public GraphNode<T> getDestination() {
        return destination;
    }
    public void setDestination(GraphNode<T> destination) {
        this.destination = destination;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "GraphEdge{" +
                "destination=" + destination +
                ", weight=" + weight +
                ", description='" + description + '\'' +
                '}';
    }
}