package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.TicketType;

import java.util.Objects;

public abstract class Ticket {
    protected String id;
    protected TicketType type;
    protected double price;
    protected String purchaseDate;
    protected boolean active;

    public Ticket(String id, TicketType type, double price, String purchaseDate) {
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

    public String getPurchaseDate() {
        return purchaseDate;
    }
    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Double.compare(price, ticket.price) == 0 && active == ticket.active && Objects.equals(id, ticket.id) && type == ticket.type && Objects.equals(purchaseDate, ticket.purchaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, price, purchaseDate, active);
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