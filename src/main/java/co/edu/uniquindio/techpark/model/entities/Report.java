package co.edu.uniquindio.techpark.model.entities;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

public class Report {
    private final LocalDate date;
    private final double totalRevenue;
    private final int totalVisitors;
    private final Map<String, Integer> mostVisitedAttractions;
    private final double averageWaitTime;
    private final List<String> weatherClosures;
    private final List<String> maintenanceAlerts;

    private Report(Builder builder) {
        this.date = builder.date;
        this.totalRevenue = builder.totalRevenue;
        this.totalVisitors = builder.totalVisitors;
        this.mostVisitedAttractions = builder.mostVisitedAttractions;
        this.averageWaitTime = builder.averageWaitTime;
        this.weatherClosures = builder.weatherClosures;
        this.maintenanceAlerts = builder.maintenanceAlerts;
    }

    public static class Builder {
        private LocalDate date;
        private double totalRevenue;
        private int totalVisitors;
        private Map<String, Integer> mostVisitedAttractions;
        private double averageWaitTime;
        private List<String> weatherClosures;
        private List<String> maintenanceAlerts;

        public Builder(LocalDate date) {
            this.date = date;
        }

        public Builder revenue(double totalRevenue) {
            this.totalRevenue = totalRevenue;
            return this;
        }

        public Builder visitors(int totalVisitors) {
            this.totalVisitors = totalVisitors;
            return this;
        }

        public Builder mostVisited(Map<String, Integer> mostVisitedAttractions) {
            this.mostVisitedAttractions = mostVisitedAttractions;
            return this;
        }

        public Builder avgWaitTime(double averageWaitTime) {
            this.averageWaitTime = averageWaitTime;
            return this;
        }

        public Builder weatherClosures(List<String> closures) {
            this.weatherClosures = closures;
            return this;
        }

        public Builder maintenanceAlerts(List<String> alerts) {
            this.maintenanceAlerts = alerts;
            return this;
        }

        public Report build() {
            return new Report(this);
        }
    }

    // Getters
    public LocalDate getDate() { return date; }
    public double getTotalRevenue() { return totalRevenue; }
    public int getTotalVisitors() { return totalVisitors; }
    public Map<String, Integer> getMostVisitedAttractions() { return mostVisitedAttractions; }
    public double getAverageWaitTime() { return averageWaitTime; }
    public List<String> getWeatherClosures() { return weatherClosures; }
    public List<String> getMaintenanceAlerts() { return maintenanceAlerts; }
}