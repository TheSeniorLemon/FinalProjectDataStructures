package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.TicketType;

import java.time.LocalDateTime;

public abstract class Ticket {
    protected String id;
    protected TicketType type;
    protected double price;
    protected LocalDateTime purchaseDate;
    protected boolean active;

    public Ticket(String id, TicketType type, double price, LocalDateTime purchaseDate) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.purchaseDate = purchaseDate;
        this.active = true;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public TicketType getType() {
        return type;
    }
    public void setType(TicketType type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", purchaseDate='" + purchaseDate + '\'' +
                ", active=" + active +
                '}';
    }

    public abstract int getPriority();

    public void deactivate() {
        this.active = false;
    }
}