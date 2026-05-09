package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.AttractionStatus;
import co.edu.uniquindio.techpark.model.enums.TicketType;
import co.edu.uniquindio.techpark.model.enums.UserRole;

public class Operator extends User {
    private String assignedZoneId;

    public Operator(String id, String name, String document, String email, String password) {
        super(id, name, document, email, password, UserRole.OPERATOR);
        this.assignedZoneId = null;
    }

    public String getAssignedZoneId() {
        return assignedZoneId;
    }
    public void setAssignedZoneId(String assignedZoneId) {
        this.assignedZoneId = assignedZoneId;
    }

    @Override
    public String toString() {
        return "Operator{" +
                "assignedZoneId='" + assignedZoneId + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", document='" + document + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userRole=" + userRole +
                '}';
    }

    @Override
    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public boolean validateAccess(Visitor visitor, Attraction attraction) {
        if (!isInMyZone(attraction)) {
            System.out.println("The attraction does not belong to the assigned zone.");
            return false;
        }
        if (attraction.getStatus() != AttractionStatus.ACTIVE) {
            System.out.println("The attraction is not active.");
            return false;
        }
        if (!visitor.meetsRestrictions(attraction)) {
            System.out.println("The visitor does not meet the safety restrictions.");
            return false;
        }
        if (attraction.getAdditionalCost() > 0 && visitor.getTicket().getType() == TicketType.GENERAL) {
            if (!visitor.hasSufficientBalance(attraction.getAdditionalCost())) {
                System.out.println("Insufficient balance for this attraction.");
                return false;
            }
            visitor.deductBalance(attraction.getAdditionalCost());
        }
        return true;
    }

    public boolean changeAttractionStatus(Attraction attraction, AttractionStatus newStatus, String reason) {
        if (!isInMyZone(attraction)) {
            System.out.println("Access denied: attraction outside the assigned zone.");
            return false;
        }
        attraction.changeStatus(newStatus, reason);
        System.out.println("Status of " + attraction.getName() +
                " changed to " + newStatus);
        return true;
    }

    public boolean registerInspection(Attraction attraction, TechnicalInspection inspection) {
        if (!isInMyZone(attraction)) {
            System.out.println("Access denied: attraction outside the assigned zone.");
            return false;
        }
        attraction.addInspection(inspection);
        if (inspection.isSuccessful() && attraction.getStatus() == AttractionStatus.MAINTENANCE) {
            attraction.changeStatus(AttractionStatus.ACTIVE, null);
            System.out.println("Successful inspection. Attraction " +
                    attraction.getName() + " reactivated.");
        }
        return true;
    }

    public Visitor processNextInQueue(Attraction attraction) {
        if (!isInMyZone(attraction)) {
            System.out.println("Access denied: attraction outside the assigned zone.");
            return null;
        }
        Visitor next = attraction.getVirtualQueue().dequeue();
        if (next != null) {
            System.out.println("Processing visitor: " + next.getName() +
                    " (priority " + next.getPriority() + ")");
        }
        return next;
    }

    private boolean isInMyZone(Attraction attraction) {
        if (assignedZoneId == null) return false;
        return assignedZoneId.equals(attraction.getZoneId());
    }

    public boolean hasAssignedZone() {
        return assignedZoneId != null;
    }
}