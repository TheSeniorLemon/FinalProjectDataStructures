package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.TicketType;

public class Ticket {
    private final String id;
    private final TicketType type;
    private final double basePrice;
    private final double discountValue; // El valor en dinero restado
    private final double finalPrice;
    private final boolean priority;
    private final boolean active;

    private Ticket(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.basePrice = builder.basePrice;
        this.active = true;

        // Regla de Negocio: La prioridad solo es para FAST_PASS
        this.priority = (this.type == TicketType.FAST_PASS);

        // Regla de Negocio: El descuento solo aplica si es FAMILIAR
        if (this.type == TicketType.FAMILY) {
            this.discountValue = this.basePrice * builder.discountRate;
        } else {
            this.discountValue = 0;
        }

        this.finalPrice = this.basePrice - this.discountValue;
    }

    public static class Builder {
        private String id;
        private TicketType type;
        private double basePrice;
        private double discountRate = 0; // Porcentaje variable (0.0 a 1.0)

        public Builder(String id, TicketType type) {
            this.id = id;
            this.type = type;
        }

        public Builder basePrice(double price) {
            this.basePrice = price;
            return this;
        }

        /**
         * Permite asignar el porcentaje de descuento.
         * Solo se procesará en el build() si el tipo es FAMILIAR.
         */
        public Builder discountRate(double rate) {
            if (rate < 0 || rate > 1) {
                throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 1");
            }
            this.discountRate = rate;
            return this;
        }

        public Ticket build() {
            if (this.id == null || this.id.isEmpty()) {
                throw new IllegalStateException("El tiquete debe tener un ID generado.");
            }
            if (this.basePrice <= 0) {
                throw new IllegalStateException("El precio base debe ser mayor a 0.");
            }
            return new Ticket(this);
        }
    }

    // Getters
    public String getId() { return id; }
    public TicketType getType() { return type; }
    public double getBasePrice() { return basePrice; }
    public double getDiscountValue() { return discountValue; }
    public double getFinalPrice() { return finalPrice; }
    public boolean hasPriority() { return priority; }
    public boolean isActive() { return active; }
}