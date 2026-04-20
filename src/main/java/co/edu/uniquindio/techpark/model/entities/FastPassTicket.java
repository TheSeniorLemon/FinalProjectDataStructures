package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.TicketType;

import java.time.LocalDateTime;
import java.util.LinkedList;

public class FastPassTicket extends Ticket {
    private LinkedList<String> enabledAttractions;

    public FastPassTicket(String id, TicketType type, double price, LocalDateTime purchaseDate, LinkedList<String> enabledAttractions) {
        super(id, type, price, purchaseDate);
        this.enabledAttractions = enabledAttractions;
    }

    public LinkedList<String> getEnabledAttractions() {
        return enabledAttractions;
    }
    public void setEnabledAttractions(LinkedList<String> enabledAttractions) {
        this.enabledAttractions = enabledAttractions;
    }

    @Override
    public String toString() {
        return "FastPassTicket{" +
                "enabledAttractions=" + enabledAttractions +
                ", id='" + id + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", purchaseDate=" + purchaseDate +
                ", active=" + active +
                '}';
    }


    @Override
    public int getPriority() {
        return 1;
    }

    public void addEnabledAttraction(String attractionId) {
        if (!hasPriorityAccess(attractionId)) {
            enabledAttractions.add(attractionId);
        }
    }

    public boolean hasPriorityAccess(String attractionId) {
        int n = enabledAttractions.size();
        for (int i = 0; i < n; i++) {
            String id = enabledAttractions.get(i);
            if (id != null && id.equals(attractionId)) {
                return true;
            }
        }
        return false;
    }

    public void enableAllAttractions(LinkedList<String> ids) {
        int n = ids.size();
        for (int i = 0; i < n; i++) {
            addEnabledAttraction(ids.get(i));
        }
    }
}