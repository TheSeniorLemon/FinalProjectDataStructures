package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.TicketType;

public class Ticket {
    private final String id;
    private final TicketType type;
    private final double basePrice;
    private final double discount;
    private final double finalPrice;
    private final boolean priority;
    private final boolean active;

    private Ticket(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.basePrice = builder.basePrice;
        this.discount = builder.discount;
        this.active = true;
        this.priority = (this.type == TicketType.FAST_PASS);
        this.finalPrice = this.basePrice - this.discount;
    }

    public static class Builder {
        private String id;
        private TicketType type;
        private double basePrice;
        private double discount = 0;

        public Builder(String id, TicketType type) {
            this.id = id;
            this.type = type;
        }

        public Builder basePrice(double price) {
            this.basePrice = price;
            return this;
        }

        public Builder applyAdminDiscount(double amount) throws Exception {
            if (!(amount >= 0 && amount <= 1)) {
                throw new Exception("Discount must be a value between 0 and 1.");
            }
            this.discount = amount;
            return this;
        }

        public Ticket build() {
            if (this.id == null || this.id.isEmpty()) {
                throw new IllegalStateException("Ticket must have a generated ID.");
            }
            if (this.basePrice <= 0) {
                throw new IllegalStateException("Base price must be greater than 0");
            }
            return new Ticket(this);
        }
    }

    public String getId() { return id; }
    public TicketType getType() { return type; }
    public double getBasePrice() { return basePrice; }
    public double getDiscount() { return discount; }
    public double getFinalPrice() { return finalPrice; }
    public boolean hasPriority() { return priority; }
    public boolean isActive() { return active; }
}