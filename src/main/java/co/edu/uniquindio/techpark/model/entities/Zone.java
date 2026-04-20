package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.AlertType;
import co.edu.uniquindio.techpark.model.enums.AttractionStatus;
import co.edu.uniquindio.techpark.model.enums.AttractionType;

import java.util.LinkedList;
import java.util.Objects;

public class Zone {
    private String id;
    private String name;
    private int maxCapacity;
    private int currentVisitors;
    private LinkedList<Attraction> attractions;
    private LinkedList<Operator> operators;

    public Zone(String id, String name, int maxCapacity) {
        this.id = id;
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.currentVisitors = 0;
        this.attractions = new LinkedList<>();
        this.operators = new LinkedList<>();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentVisitors() {
        return currentVisitors;
    }
    public void setCurrentVisitors(int currentVisitors) {
        this.currentVisitors = currentVisitors;
    }

    public LinkedList<Attraction> getAttractions() {
        return attractions;
    }
    public void setAttractions(LinkedList<Attraction> attractions) {
        this.attractions = attractions;
    }

    public LinkedList<Operator> getOperators() {
        return operators;
    }
    public void setOperators(LinkedList<Operator> operators) {
        this.operators = operators;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return maxCapacity == zone.maxCapacity && currentVisitors == zone.currentVisitors && Objects.equals(id, zone.id) && Objects.equals(name, zone.name) && Objects.equals(attractions, zone.attractions) && Objects.equals(operators, zone.operators);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, maxCapacity, currentVisitors, attractions, operators);
    }

    @Override
    public String toString() {
        return "Zone{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", maxCapacity=" + maxCapacity +
                ", currentVisitors=" + currentVisitors +
                ", attractions=" + attractions +
                ", operators=" + operators +
                '}';
    }

    // ----------------------------------------------------------------
    // attraction management
    // ----------------------------------------------------------------

    public boolean addAttraction(Attraction attraction) {
        if (attraction == null) return false;
        if (findAttraction(attraction.getId()) != null) {
            System.out.println("The attraction already exists in the zone.");
            return false;
        }
        attraction.setZoneId(this.id);
        attractions.add(attraction);
        System.out.println("Attraction '" + attraction.getName() +
                "' added to zone '" + name + "'.");
        return true;
    }

    public boolean removeAttraction(String attractionId) {
        Attraction target = findAttraction(attractionId);
        if (target == null) {
            System.out.println("Attraction not found in the zone.");
            return false;
        }
        if (target.getStatus() == AttractionStatus.ACTIVE
                && !target.getVirtualQueue().isEmpty()) {
            System.out.println("Cannot remove an active attraction with visitors in queue.");
            return false;
        }
        boolean result = attractions.remove(target);
        if (result) {
            target.setZoneId(null);
            System.out.println("Attraction '" + target.getName() + "' removed from the zone.");
        }
        return result;
    }

    public Attraction findAttraction(String attractionId) {
        int n = attractions.size();
        for (int i = 0; i < n; i++) {
            Attraction a = attractions.get(i);
            if (a != null && a.getId().equals(attractionId)) {
                return a;
            }
        }
        return null;
    }

    public LinkedList<Attraction> getActiveAttractions() {
        LinkedList<Attraction> active = new LinkedList<>();
        int n = attractions.size();
        for (int i = 0; i < n; i++) {
            Attraction a = attractions.get(i);
            if (a != null && a.getStatus() == AttractionStatus.ACTIVE) {
                active.add(a);
            }
        }
        return active;
    }

    public LinkedList<Attraction> getAttractionsByType(AttractionType type) {
        LinkedList<Attraction> result = new LinkedList<>();
        int n = attractions.size();
        for (int i = 0; i < n; i++) {
            Attraction a = attractions.get(i);
            if (a != null && a.getType() == type) {
                result.add(a);
            }
        }
        return result;
    }

    // ----------------------------------------------------------------
    // operator management
    // ----------------------------------------------------------------

    public boolean assignOperator(Operator operator) {
        if (operator == null) return false;
        if (findOperator(operator.getId()) != null) {
            System.out.println("The operator is already assigned to this zone.");
            return false;
        }
        operators.add(operator);
        System.out.println("Operator '" + operator.getName() +
                "' assigned to zone '" + name + "'.");
        return true;
    }

    public boolean removeOperator(String operatorId) {
        if (getOperatorCount() <= 1) {
            System.out.println("The zone must have at least one operator. Removal canceled.");
            return false;
        }
        Operator target = findOperator(operatorId);
        if (target == null) {
            System.out.println("Operator not found in the zone.");
            return false;
        }
        boolean result = operators.remove(target);
        if (result) {
            target.setAssignedZoneId(null);
            System.out.println("Operator '" + target.getName() +
                    "' removed from zone '" + name + "'.");
        }
        return result;
    }

    public Operator findOperator(String operatorId) {
        int n = operators.size();
        for (int i = 0; i < n; i++) {
            Operator op = operators.get(i);
            if (op != null && op.getId().equals(operatorId)) {
                return op;
            }
        }
        return null;
    }

    public boolean hasAvailableOperator() {
        return operators.size() > 1;
    }

    public int getOperatorCount() {
        return operators.size();
    }

    // ----------------------------------------------------------------
    // capacity and visitor control
    // ----------------------------------------------------------------

    public boolean hasCapacity() {
        return currentVisitors < maxCapacity;
    }

    public boolean registerEntry() {
        if (!hasCapacity()) {
            System.out.println("Zone '" + name + "' is full. Entry denied.");
            return false;
        }
        currentVisitors++;
        return true;
    }

    public boolean registerExit() {
        if (currentVisitors <= 0) {
            System.out.println("No visitors registered in the zone.");
            return false;
        }
        currentVisitors--;
        return true;
    }

    public int getAvailableCapacity() {
        return maxCapacity - currentVisitors;
    }

    // ----------------------------------------------------------------
    // weather alert
    // ----------------------------------------------------------------

    public int closeAttractionsDueToWeather(AlertType alert) {
        int closed = 0;
        int n = attractions.size();
        for (int i = 0; i < n; i++) {
            Attraction a = attractions.get(i);
            if (a == null) continue;
            boolean isWater   = a.getType() == AttractionType.WATER;
            boolean isMechanical = a.getType() == AttractionType.MECHANICAL_HEIGHT;
            boolean shouldClose = (alert == AlertType.ELECTRICAL_STORM
                    || alert == AlertType.HEAVY_RAIN)
                    && (isWater || isMechanical);
            if (shouldClose && a.getStatus() == AttractionStatus.ACTIVE) {
                a.changeStatus(AttractionStatus.CLOSED, "Closed due to " + alert);
                closed++;
            }
        }
        System.out.println(closed + " attractions closed in zone '" +
                name + "' due to alert: " + alert);
        return closed;
    }
}