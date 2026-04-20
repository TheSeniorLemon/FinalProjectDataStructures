package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.UserRole;

public class Administrator extends User{
    public Administrator(String id, String name, String email, String password, UserRole userRole) {
        super(id, name, email, password, userRole);
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userRole=" + userRole +
                '}';
    }

    @Override
    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public Zone createZone(String id, String name, int maxCapacity) {
        Zone zone = new Zone(id, name, maxCapacity);
        System.out.println("Zone created: " + name);
        return zone;
    }

    public boolean modifyZone(Zone zone, String newName, int newCapacity) {
        if (zone == null) return false;
        zone.setName(newName);
        zone.setMaxCapacity(newCapacity);
        System.out.println("Zone updated: " + newName);
        return true;
    }

    public boolean createAttraction(Zone zone, Attraction attraction) {
        if (zone == null || attraction == null) return false;
        zone.addAttraction(attraction);
        System.out.println("Attraction '" + attraction.getName() +
                "' added to zone " + zone.getName());
        return true;
    }

    public boolean assignOperator(Operator operator, Zone zone) {
        if (operator == null || zone == null) return false;
        if (operator.hasAssignedZone()) {
            System.out.println("The operator already has an assigned zone.");
            return false;
        }
        zone.assignOperator(operator);
        operator.setAssignedZoneId(zone.getId());
        System.out.println("Operator " + operator.getName() +
                " assigned to zone " + zone.getName());
        return true;
    }

    public boolean reassignOperator(Operator operator, Zone previousZone, Zone newZone) {
        if (operator == null || newZone == null) return false;
        if (previousZone != null) {
            if (!previousZone.hasAvailableOperator()) {
                System.out.println("The current zone would be left without operators. Assignment canceled.");
                return false;
            }
            previousZone.removeOperator(operator.getId());
        }
        newZone.assignOperator(operator);
        operator.setAssignedZoneId(newZone.getId());
        System.out.println("Operator " + operator.getName() +
                " reassigned to zone " + newZone.getName());
        return true;
    }

    public void activateWeatherAlert(Park park, AlertType type) {
        if (park == null) return;
        park.activateWeatherAlert(type);
        System.out.println("Weather alert activated: " + type);
    }

    public void deactivateWeatherAlert(Park park) {
        if (park == null) return;
        park.deactivateWeatherAlert();
        System.out.println("Weather alert deactivated.");
    }

    public Report generateReport(Park park) {
        if (park == null) return null;
        Report report = park.closeDay();
        System.out.println("Report generated: " + report.getDate());
        return report;
    }

    public void checkGraphStatus(Park park) {
        if (park == null) return;
        Graph<Attraction> graph = park.getGraphMap();
        System.out.println("Nodes in the graph: " + graph.getNumNodes());
    }

    public LinkedList<GraphNode<Attraction>> detectClusters(Park park) {
        if (park == null) return null;
        return park.getGraphMap().detectClusters();
    }
}