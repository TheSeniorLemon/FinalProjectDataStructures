package co.edu.uniquindio.techpark.model.entities;

public class Operator {
    private String id, name, assignedZoneId;

    private Operator(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.assignedZoneId = builder.assignedZoneId;
    }

    public static class Builder {
        private String id, name, assignedZoneId;

        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder assignedZoneId(String assignedZoneId) {
            this.assignedZoneId = assignedZoneId;
            return this;
        }

        public Operator build() {
            return new Operator(this);
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAssignedZoneId() { return assignedZoneId; }
    public void setAssignedZoneId(String assignedZoneId) { this.assignedZoneId = assignedZoneId; }
}