package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.AttractionStatus;
import co.edu.uniquindio.techpark.model.enums.AttractionType;
import co.edu.uniquindio.techpark.model.enums.TicketType; // Necesario para la lógica de prioridad
import co.edu.uniquindio.techpark.dataStructures.SimpleLinkedList;
import co.edu.uniquindio.techpark.dataStructures.PriorityQueue;

public class Attraction {
    private String id, name, closeReason;
    private AttractionType type;
    private AttractionStatus status;
    private int maxCapacity, accumulatedVisitors, estimatedWaitTime;
    private double minHeight, additionalCost;
    private int minAge;
    private SimpleLinkedList<Edge> adjacentEdges;
    private PriorityQueue<Visitor> waitingQueue;

    private Attraction(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.maxCapacity = builder.maxCapacity;
        this.minHeight = builder.minHeight;
        this.minAge = builder.minAge;
        this.additionalCost = builder.additionalCost;
        this.status = AttractionStatus.ACTIVE;
        this.accumulatedVisitors = 0;
        this.estimatedWaitTime = 0;
        this.closeReason = null;
        this.adjacentEdges = new SimpleLinkedList<>();
        this.waitingQueue = new PriorityQueue<>();
    }

    /**
     * Agrega una atraccion vecina al final de la lista de adyacencia (O(n)).
     */
    public void addNeighbor(Attraction target, double distance) {
        this.adjacentEdges.addLast(new Edge(target, distance));
    }

    /**
     * Inserta un visitante en la cola usando la logica de prioridad del sistema.
     * Prioridad 1 para Fast-Pass, Prioridad 2 para los demas.
     */
    public void addVisitorToQueue(Visitor visitor, TicketType ticketType) {
        int priority = (ticketType == TicketType.FAST_PASS) ? 1 : 2;
        this.waitingQueue.enqueue(visitor, priority);
    }

    /**
     * Extrae al siguiente visitante de la cola de prioridad (O(1)).
     */
    public Visitor processNextVisitor() {
        return this.waitingQueue.dequeue();
    }

    /**
     * Clase interna que representa la conexion entre dos nodos del grafo.
     */
    public static class Edge {
        private Attraction target;
        private double weight;

        public Edge(Attraction target, double weight) {
            this.target = target;
            this.weight = weight;
        }

        public Attraction getTarget() { return target; }
        public double getWeight() { return weight; }
    }

    public static class Builder {
        private String id, name;
        private AttractionType type;
        private int maxCapacity, minAge;
        private double minHeight, additionalCost;

        public Builder(String id, String name, AttractionType type) {
            this.id = id;
            this.name = name;
            this.type = type;
        }

        public Builder maxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Builder minHeight(double minHeight) {
            this.minHeight = minHeight;
            return this;
        }

        public Builder minAge(int minAge) {
            this.minAge = minAge;
            return this;
        }

        public Builder additionalCost(double additionalCost) {
            this.additionalCost = additionalCost;
            return this;
        }

        public Attraction build() {
            return new Attraction(this);
        }
    }

    // Getters y Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public AttractionType getType() { return type; }
    public AttractionStatus getStatus() { return status; }
    public void setStatus(AttractionStatus status) { this.status = status; }
    public int getMaxCapacity() { return maxCapacity; }
    public double getMinHeight() { return minHeight; }
    public int getMinAge() { return minAge; }
    public double getAdditionalCost() { return additionalCost; }
    public int getAccumulatedVisitors() { return accumulatedVisitors; }
    public void setAccumulatedVisitors(int accumulatedVisitors) { this.accumulatedVisitors = accumulatedVisitors; }
    public int getEstimatedWaitTime() { return estimatedWaitTime; }
    public void setEstimatedWaitTime(int estimatedWaitTime) { this.estimatedWaitTime = estimatedWaitTime; }
    public String getCloseReason() { return closeReason; }
    public void setCloseReason(String closeReason) { this.closeReason = closeReason; }
    public SimpleLinkedList<Edge> getAdjacentEdges() { return adjacentEdges; }
    public PriorityQueue<Visitor> getWaitingQueue() { return waitingQueue; }
}