package co.edu.uniquindio.techpark.model.entities;

import java.util.Objects;

public class Notification {
    private String id;
    private String message;
    private String dateTime;
    private boolean read;
    private String recipientId;

    public Notification(String id, String message, String dateTime, String recipientId) {
        this.id = id;
        this.message = message;
        this.dateTime = dateTime;
        this.read = false;
        this.recipientId = recipientId;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isRead() {
        return read;
    }
    public void setRead(boolean read) {
        this.read = read;
    }

    public String getRecipientId() {
        return recipientId;
    }
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return read == that.read && Objects.equals(id, that.id) && Objects.equals(message, that.message) && Objects.equals(dateTime, that.dateTime) && Objects.equals(recipientId, that.recipientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, dateTime, read, recipientId);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", read=" + read +
                ", recipientId='" + recipientId + '\'' +
                '}';
    }

    public void markAsRead() {
        this.read = true;
        System.out.println("Notification '" + id + "' marked as read.");
    }

    public String getContent() {
        return "[" + dateTime + "] " + message;
    }
}