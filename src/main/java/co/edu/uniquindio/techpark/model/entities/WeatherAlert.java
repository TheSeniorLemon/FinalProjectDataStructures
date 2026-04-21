package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.AlertType;
import co.edu.uniquindio.techpark.model.enums.AttractionType;
import co.edu.uniquindio.techpark.model.structures.LinkedList;

import java.util.Objects;

public class WeatherAlert {
    private String id;
    private AlertType type;
    private String description;
    private String dateTime;
    private boolean active;

    public WeatherAlert(String id, AlertType type, String description, String dateTime) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.dateTime = dateTime;
        this.active = false;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public AlertType getType() {
        return type;
    }
    public void setType(AlertType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
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
        WeatherAlert that = (WeatherAlert) o;
        return active == that.active && Objects.equals(id, that.id) && type == that.type && Objects.equals(description, that.description) && Objects.equals(dateTime, that.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, description, dateTime, active);
    }

    @Override
    public String toString() {
        return "WeatherAlert{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", active=" + active +
                '}';
    }

    // ----------------------------------------------------------------
    // activation and deactivation
    // ----------------------------------------------------------------

    public void activate() {
        this.active = true;
        System.out.println("Weather alert activated: " + type +
                " | " + description +
                " | Time: " + dateTime);
    }

    public void deactivate() {
        this.active = false;
        System.out.println("Weather alert deactivated: " + type);
    }

    // ----------------------------------------------------------------
    // impact logic
    // ----------------------------------------------------------------

    public boolean affectsAttraction(AttractionType attractionType) {
        if (!active || attractionType == null) return false;
        switch (type) {
            case ELECTRICAL_STORM:
                return attractionType == AttractionType.WATER
                        || attractionType == AttractionType.MECHANICAL_HEIGHT;
            case HEAVY_RAIN:
                return attractionType == AttractionType.WATER
                        || attractionType == AttractionType.MECHANICAL_HEIGHT;
            default:
                return false;
        }
    }

    public String getNotificationMessage() {
        return "WEATHER ALERT [" + type + "]: " + description +
                ". Water attractions and high mechanical rides " +
                "have been closed for safety. " +
                "Please proceed to covered areas.";
    }

    public void notifyAffected(LinkedList<Visitor> visitors) {
        if (visitors == null || visitors.getSize() == 0) return;
        String message = getNotificationMessage();
        int n = visitors.getSize();
        int notified = 0;
        for (int i = 0; i < n; i++) {
            Visitor v = visitors.get(i);
            if (v != null && v.getTicket() != null && v.getTicket().isActive()) {
                System.out.println("Notifying: " + v.getName() +
                        " -> " + message);
                notified++;
            }
        }
        System.out.println("Total notified: " + notified + " visitors.");
    }
}