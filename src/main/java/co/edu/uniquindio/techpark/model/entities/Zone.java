package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.dataStructures.SimpleLinkedList;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    private String id, name;
    private int maxCapacity;
    private SimpleLinkedList<Attraction> attractions;
    private SimpleLinkedList<Operator> operators;

    private Zone(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.maxCapacity = builder.maxCapacity;
        this.attractions = new SimpleLinkedList<>();
        this.operators = new SimpleLinkedList<>();
    }

    public static class Builder {
        private String id, name;
        private int maxCapacity;

        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder maxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Zone build() {
            return new Zone(this);
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getMaxCapacity() { return maxCapacity; }
    public SimpleLinkedList<Attraction> getAttractions() { return attractions; }
    public SimpleLinkedList<Operator> getOperators() { return operators; }
}