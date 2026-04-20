package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.TicketType;

import java.time.LocalDateTime;

public class GeneralTicket extends Ticket {
    public GeneralTicket(String id, TicketType type, double price, LocalDateTime purchaseDate) {
        super(id, type, price, purchaseDate);
    }

    @Override
    public String toString() {
        return "GeneralTicket{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", purchaseDate=" + purchaseDate +
                ", active=" + active +
                '}';
    }

    @Override
    public int getPriority() {
        return 2;
    }
}