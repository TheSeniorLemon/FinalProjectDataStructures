package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.TicketType;
import co.edu.uniquindio.techpark.model.entities.Attraction;

import java.util.Objects;

public class VisitHistory {
    private String attractionId;
    private String attractionName;
    private String dateTime;
    private double incurredCost;
    private TicketType ticketTypeUsed;
    private int estimatedDurationMinutes;

    public VisitHistory(String attractionId, String attractionName, String dateTime, double incurredCost, TicketType ticketTypeUsed, int estimatedDurationMinutes) {
        this.attractionId = attractionId;
        this.attractionName = attractionName;
        this.dateTime = dateTime;
        this.incurredCost = incurredCost;
        this.ticketTypeUsed = ticketTypeUsed;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public VisitHistory(String attractionId, String attractionName, String dateTime, double incurredCost, TicketType ticketTypeUsed) {
        this(attractionId, attractionName, dateTime, incurredCost, ticketTypeUsed, estimateDuration(attractionId));
    }

    public static VisitHistory from(Attraction attraction, double incurredCost, TicketType ticketType) {
        return new VisitHistory(
                attraction.getId(),
                attraction.getName(),
                java.time.LocalDateTime.now().toString(),
                incurredCost,
                ticketType,
                attraction.getEstimatedWaitTime() > 0
                        ? attraction.getEstimatedWaitTime()
                        : 10
        );
    }

    private static int estimateDuration(String attractionId) {
        return 10;
    }

    public String getAttractionId() {
        return attractionId;
    }
    public void setAttractionId(String attractionId) {
        this.attractionId = attractionId;
    }

    public String getAttractionName() {
        return attractionName;
    }
    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getIncurredCost() {
        return incurredCost;
    }
    public void setIncurredCost(double incurredCost) {
        this.incurredCost = incurredCost;
    }

    public TicketType getTicketTypeUsed() {
        return ticketTypeUsed;
    }
    public void setTicketTypeUsed(TicketType ticketTypeUsed) {
        this.ticketTypeUsed = ticketTypeUsed;
    }

    public int getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }
    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        VisitHistory that = (VisitHistory) o;
        return Objects.equals(attractionId, that.attractionId) && Objects.equals(attractionName, that.attractionName) && Objects.equals(dateTime, that.dateTime) && Objects.equals(incurredCost, that.incurredCost) && ticketTypeUsed == that.ticketTypeUsed && Objects.equals(estimatedDurationMinutes, that.estimatedDurationMinutes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attractionId, attractionName, dateTime, incurredCost, ticketTypeUsed, estimatedDurationMinutes);
    }

    @Override
    public String toString() {
        return "VisitHistory{" +
                "attractionId='" + attractionId + '\'' +
                ", attractionName='" + attractionName + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", incurredCost=" + incurredCost +
                ", ticketTypeUsed=" + ticketTypeUsed +
                ", estimatedDurationMinutes=" + estimatedDurationMinutes +
                '}';
    }

    public String getSummary() {
        return "[" + dateTime + "] " + attractionName +
                " | Ticket: " + ticketTypeUsed +
                " | Additional cost: $" + incurredCost +
                " | Approx duration: " + estimatedDurationMinutes + " min";
    }
}