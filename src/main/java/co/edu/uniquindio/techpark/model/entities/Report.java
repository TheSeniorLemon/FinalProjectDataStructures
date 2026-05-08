package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.AlertType;
import co.edu.uniquindio.techpark.model.enums.AttractionStatus;
import co.edu.uniquindio.techpark.model.structures.LinkedList;

import java.util.Objects;

public class Report {
    private String id;
    private String date;
    private double dailyRevenue;
    private int totalVisitors;
    private int weatherClosures;
    private int maintenanceAlerts;
    private int totalIncidents;
    private LinkedList<String> logs;
    private LinkedList<Attraction> attractionsSnapshot;

    public Report(String id, String date) {
        this.id = id;
        this.date = date;
        this.dailyRevenue = 0.0;
        this.totalVisitors = 0;
        this.weatherClosures = 0;
        this.maintenanceAlerts = 0;
        this.totalIncidents = 0;
        this.logs = new LinkedList<>();
        this.attractionsSnapshot = new LinkedList<>();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public double getDailyRevenue() {
        return dailyRevenue;
    }
    public void setDailyRevenue(double dailyRevenue) {
        this.dailyRevenue = dailyRevenue;
    }

    public int getTotalVisitors() {
        return totalVisitors;
    }
    public void setTotalVisitors(int totalVisitors) {
        this.totalVisitors = totalVisitors;
    }

    public int getWeatherClosures() {
        return weatherClosures;
    }
    public void setWeatherClosures(int weatherClosures) {
        this.weatherClosures = weatherClosures;
    }

    public int getMaintenanceAlerts() {
        return maintenanceAlerts;
    }
    public void setMaintenanceAlerts(int maintenanceAlerts) {
        this.maintenanceAlerts = maintenanceAlerts;
    }

    public int getTotalIncidents() {
        return totalIncidents;
    }
    public void setTotalIncidents(int totalIncidents) {
        this.totalIncidents = totalIncidents;
    }

    public LinkedList<String> getLogs() {
        return logs;
    }
    public void setLogs(LinkedList<String> logs) {
        this.logs = logs;
    }

    public LinkedList<Attraction> getAttractionsSnapshot() {
        return attractionsSnapshot;
    }
    public void setAttractionsSnapshot(LinkedList<Attraction> attractionsSnapshot) {
        this.attractionsSnapshot = attractionsSnapshot;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Double.compare(dailyRevenue, report.dailyRevenue) == 0 && totalVisitors == report.totalVisitors && weatherClosures == report.weatherClosures && maintenanceAlerts == report.maintenanceAlerts && totalIncidents == report.totalIncidents && Objects.equals(id, report.id) && Objects.equals(date, report.date) && Objects.equals(logs, report.logs) && Objects.equals(attractionsSnapshot, report.attractionsSnapshot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, dailyRevenue, totalVisitors, weatherClosures, maintenanceAlerts, totalIncidents, logs, attractionsSnapshot);
    }

    @Override
    public String toString() {
        return "Report{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", dailyRevenue=" + String.format("%.2f", dailyRevenue) +
                ", totalVisitors=" + totalVisitors +
                ", weatherClosures=" + weatherClosures +
                ", maintenanceAlerts=" + maintenanceAlerts +
                ", totalIncidents=" + totalIncidents +
                ", logs=" + logs +
                ", attractionsSnapshot=" + attractionsSnapshot +
                '}';
    }

    // ----------------------------------------------------------------
    // event logging during the day
    // ----------------------------------------------------------------

    public void registerEntry(Visitor visitor, double ticketAmount) {
        if (visitor == null) return;
        totalVisitors++;
        dailyRevenue += ticketAmount;
        logs.add("[ENTRY] " + visitor.getName() +
                " | Ticket: " + visitor.getTicket().getType() +
                " | $" + ticketAmount);
    }

    public void registerWeatherClosure(String attractionName, AlertType alert) {
        weatherClosures++;
        totalIncidents++;
        logs.add("[WEATHER] Attraction '" + attractionName +
                "' closed due to " + alert);
    }

    public void registerMaintenanceAlert(String attractionName, int visitorCount) {
        maintenanceAlerts++;
        logs.add("[MAINTENANCE] Attraction '" + attractionName +
                "' blocked after reaching " +
                visitorCount + " accumulated visitors.");
    }

    public void registerIncident(String description) {
        if (description == null || description.isEmpty()) return;
        totalIncidents++;
        logs.add("[INCIDENT] " + description);
    }

    public void registerEvent(String description) {
        if (description == null || description.isEmpty()) return;
        logs.add("[EVENT] " + description);
    }

    public void loadAttractionsSnapshot(LinkedList<Attraction> attractions) {
        if (attractions == null) return;
        int n = attractions.getSize();
        for (int i = 0; i < n; i++) {
            Attraction a = attractions.get(i);
            if (a != null) attractionsSnapshot.add(a);
        }
    }

    // ----------------------------------------------------------------
    // queries and statistics
    // ----------------------------------------------------------------

    public LinkedList<Attraction> getMostVisitedAttractions() {
        int n = attractionsSnapshot.getSize();
        if (n == 0) return new LinkedList<>();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                Attraction a = attractionsSnapshot.get(j);
                Attraction b = attractionsSnapshot.get(j + 1);
                if (a != null && b != null
                        && a.getVisitorCount() < b.getVisitorCount()) {
                    swap(j, j + 1);
                }
            }
        }
        return attractionsSnapshot;
    }

    private void swap(int i, int j) {
        Attraction ai = attractionsSnapshot.get(i);
        Attraction aj = attractionsSnapshot.get(j);
        attractionsSnapshot.remove(ai);
        attractionsSnapshot.remove(aj);

        LinkedList<Attraction> temp = new LinkedList<>();
        int n = attractionsSnapshot.getSize();
        for (int k = 0; k < n; k++) {
            if (k == i) {
                temp.add(aj);
            } else if (k == j) {
                temp.add(ai);
            } else {
                temp.add(attractionsSnapshot.get(k));
            }
        }
        attractionsSnapshot = temp;
    }

    public double getAverageWaitTime() {
        int n = attractionsSnapshot.getSize();
        if (n == 0) return 0.0;
        double total = 0.0;
        int count = 0;
        for (int i = 0; i < n; i++) {
            Attraction a = attractionsSnapshot.get(i);
            if (a != null) {
                total += a.getEstimatedWaitTime();
                count++;
            }
        }
        return count > 0 ? total / count : 0.0;
    }

    public LinkedList<Attraction> getAttractionsWithMostIncidents() {
        LinkedList<Attraction> result = new LinkedList<>();
        int n = attractionsSnapshot.getSize();
        for (int i = 0; i < n; i++) {
            Attraction a = attractionsSnapshot.get(i);
            if (a != null && a.getInspections().getSize() > 0) {
                result.add(a);
            }
        }
        return result;
    }

    public LinkedList<Attraction> getAttractionsUnderMaintenance() {
        LinkedList<Attraction> result = new LinkedList<>();
        int n = attractionsSnapshot.getSize();
        for (int i = 0; i < n; i++) {
            Attraction a = attractionsSnapshot.get(i);
            if (a != null && a.getStatus() == AttractionStatus.MAINTENANCE) {
                result.add(a);
            }
        }
        return result;
    }

    public String export() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("  DAILY REPORT - TECH-PARK UQ\n");
        sb.append("========================================\n");
        sb.append("ID:                  ").append(id).append("\n");
        sb.append("Date:                ").append(date).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("Total visitors:      ").append(totalVisitors).append("\n");
        sb.append("Daily revenue:       $").append(String.format("%.2f", dailyRevenue)).append("\n");
        sb.append("Avg wait time:       ").append(String.format("%.1f", getAverageWaitTime())).append(" min\n");
        sb.append("Weather closures:    ").append(weatherClosures).append("\n");
        sb.append("Maintenance alerts:  ").append(maintenanceAlerts).append("\n");
        sb.append("Total incidents:     ").append(totalIncidents).append("\n");
        sb.append("----------------------------------------\n");
        sb.append("MOST VISITED ATTRACTIONS:\n");
        LinkedList<Attraction> mostVisited = getMostVisitedAttractions();
        int top = Math.min(5, mostVisited.getSize());
        for (int i = 0; i < top; i++) {
            Attraction a = mostVisited.get(i);
            if (a != null) {
                sb.append("  ").append(i + 1).append(". ")
                        .append(a.getName())
                        .append(" - ").append(a.getVisitorCount())
                        .append(" visitors\n");
            }
        }
        sb.append("----------------------------------------\n");
        sb.append("EVENT LOG (").append(logs.getSize()).append(" records):\n");
        int n = logs.getSize();
        for (int i = 0; i < n; i++) {
            String log = logs.get(i);
            if (log != null) sb.append("  ").append(log).append("\n");
        }
        sb.append("========================================\n");
        return sb.toString();
    }
}