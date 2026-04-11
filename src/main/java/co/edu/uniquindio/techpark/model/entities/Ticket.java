package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.TicketType;

public class Ticket {
    private String id;
    private TicketType type;
    private double price;
    private boolean active;

    private Ticket(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.price = builder.price;
        this.active = true;
    }

    public static class Builder {
        private String id;
        private TicketType type;
        private double price;

        public Builder(String id, TicketType type) {
            this.id = id;
            this.type = type;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Ticket build() {
            return new Ticket(this);
        }
    }

    public String getId() { return id; }
    public TicketType getType() { return type; }
    public double getPrice() { return price; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}