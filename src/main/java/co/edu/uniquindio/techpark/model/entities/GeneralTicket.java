package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.TicketType;

public class GeneralTicket extends Ticket {
    public GeneralTicket(String id, double price, String purchaseDate) {
        super(id, TicketType.GENERAL, price, purchaseDate);
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