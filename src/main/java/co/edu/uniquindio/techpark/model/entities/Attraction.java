package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.AttractionStatus;
import co.edu.uniquindio.techpark.model.enums.AttractionType;
import co.edu.uniquindio.techpark.model.structures.LinkedList;
import co.edu.uniquindio.techpark.model.structures.PriorityQueue;

import java.util.Objects;

public class Attraction implements Comparable<Attraction> {
    private String id;
    private String zoneId;
    private String name;
    private AttractionType type;
    private int capacityPerCycle;
    private float minimumHeight;
    private int minimumAge;
    private double additionalCost;
    private int visitorCount;
    private int estimatedWaitTime;
    private AttractionStatus status;
    private String closureReason;
    private PriorityQueue<Visitor> virtualQueue;
    private LinkedList<TechnicalInspection> inspections;

    private static final int MAINTENANCE_LIMIT = 500;

    public Attraction(String id, String name, AttractionType type, int capacityPerCycle, float minimumHeight, int minimumAge, double additionalCost, int estimatedWaitTime) {
        this.id = id;
        this.zoneId = null;
        this.name = name;
        this.type = type;
        this.capacityPerCycle = capacityPerCycle;
        this.minimumHeight = minimumHeight;
        this.minimumAge = minimumAge;
        this.additionalCost = additionalCost;
        this.visitorCount = 0;
        this.estimatedWaitTime = estimatedWaitTime;
        this.status = AttractionStatus.ACTIVE;
        this.closureReason = null;
        this.virtualQueue = new PriorityQueue<>();
        this.inspections = new LinkedList<>();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getZoneId() {
        return zoneId;
    }
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public AttractionType getType() {
        return type;
    }
    public void setType(AttractionType type) {
        this.type = type;
    }

    public int getCapacityPerCycle() {
        return capacityPerCycle;
    }
    public void setCapacityPerCycle(int capacityPerCycle) {
        this.capacityPerCycle = capacityPerCycle;
    }

    public float getMinimumHeight() {
        return minimumHeight;
    }
    public void setMinimumHeight(float minimumHeight) {
        this.minimumHeight = minimumHeight;
    }

    public int getMinimumAge() {
        return minimumAge;
    }
    public void setMinimumAge(int minimumAge) {
        this.minimumAge = minimumAge;
    }

    public double getAdditionalCost() {
        return additionalCost;
    }
    public void setAdditionalCost(double additionalCost) {
        this.additionalCost = additionalCost;
    }

    public int getVisitorCount() {
        return visitorCount;
    }
    public void setVisitorCount(int visitorCount) {
        this.visitorCount = visitorCount;
    }

    public int getEstimatedWaitTime() {
        return estimatedWaitTime;
    }
    public void setEstimatedWaitTime(int estimatedWaitTime) {
        this.estimatedWaitTime = estimatedWaitTime;
    }

    public AttractionStatus getStatus() {
        return status;
    }
    public void setStatus(AttractionStatus status) {
        this.status = status;
    }

    public String getClosureReason() {
        return closureReason;
    }
    public void setClosureReason(String closureReason) {
        this.closureReason = closureReason;
    }

    public PriorityQueue<Visitor> getVirtualQueue() {
        return virtualQueue;
    }
    public void setVirtualQueue(PriorityQueue<Visitor> virtualQueue) {
        this.virtualQueue = virtualQueue;
    }

    public LinkedList<TechnicalInspection> getInspections() {
        return inspections;
    }
    public void setInspections(LinkedList<TechnicalInspection> inspections) {
        this.inspections = inspections;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Attraction that = (Attraction) o;
        return capacityPerCycle == that.capacityPerCycle && Float.compare(minimumHeight, that.minimumHeight) == 0 && minimumAge == that.minimumAge && Double.compare(additionalCost, that.additionalCost) == 0 && visitorCount == that.visitorCount && estimatedWaitTime == that.estimatedWaitTime && Objects.equals(id, that.id) && Objects.equals(zoneId, that.zoneId) && Objects.equals(name, that.name) && type == that.type && status == that.status && Objects.equals(closureReason, that.closureReason) && Objects.equals(virtualQueue, that.virtualQueue) && Objects.equals(inspections, that.inspections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, zoneId, name, type, capacityPerCycle, minimumHeight, minimumAge, additionalCost, visitorCount, estimatedWaitTime, status, closureReason, virtualQueue, inspections);
    }

    @Override
    public String toString() {
        return "Attraction{" +
                "id='" + id + '\'' +
                ", zoneId='" + zoneId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", capacityPerCycle=" + capacityPerCycle +
                ", minimumHeight=" + minimumHeight +
                ", minimumAge=" + minimumAge +
                ", additionalCost=" + additionalCost +
                ", visitorCount=" + visitorCount +
                ", estimatedWaitTime=" + estimatedWaitTime + " min" +
                ", status=" + status +
                ", closureReason='" + closureReason + '\'' +
                ", virtualQueue=PriorityQueue[size=" + virtualQueue.getSize() + "]" +
                ", inspections=" + inspections +
                ", inQueue=" + virtualQueue.getSize() +
                '}';
    }

    public int compareTo(Attraction other) {
        if (other == null) return 1;
        return this.name.compareToIgnoreCase(other.getName());
    }

    // ----------------------------------------------------------------
    // access control
    // ----------------------------------------------------------------

    public boolean registerEntry(Visitor visitor) {
        if (visitor == null) return false;
        if (status != AttractionStatus.ACTIVE) {
            System.out.println("The attraction '" + name + "' is not active.");
            return false;
        }
        if (!meetsRestrictions(visitor)) {
            System.out.println("The visitor does not meet the safety restrictions.");
            return false;
        }
        visitorCount++;
        updateWaitTime();
        checkMaintenance();
        return true;
    }

    public boolean meetsRestrictions(Visitor visitor) {
        if (visitor == null) return false;
        if (visitor.getAge() < minimumAge) {
            System.out.println("Insufficient age. Minimum: " + minimumAge + " years.");
            return false;
        }
        if (visitor.getHeight() < minimumHeight) {
            System.out.println("Insufficient height. Minimum: " + minimumHeight + " cm.");
            return false;
        }
        return true;
    }

    // ----------------------------------------------------------------
    // virtual queue
    // ----------------------------------------------------------------

    public boolean addToVirtualQueue(Visitor visitor) {
        if (visitor == null) return false;
        if (status != AttractionStatus.ACTIVE) {
            System.out.println("Cannot enqueue in an inactive attraction.");
            return false;
        }
        virtualQueue.enqueue(visitor, visitor.getPriority());
        updateWaitTime();
        System.out.println("Visitor '" + visitor.getName() +
                "' added to queue of '" + name +
                "' with priority " + visitor.getPriority() + ".");
        return true;
    }

    public Visitor processNext() {
        if (virtualQueue.isEmpty()) {
            System.out.println("The queue of '" + name + "' is empty.");
            return null;
        }
        Visitor next = virtualQueue.dequeue();
        updateWaitTime();
        return next;
    }

    public int getQueuePosition(Visitor visitor) {
        return virtualQueue.getPosition(visitor);
    }

    // ----------------------------------------------------------------
    // status and maintenance
    // ----------------------------------------------------------------

    public void changeStatus(AttractionStatus newStatus, String reason) {
        this.status = newStatus;
        if (newStatus == AttractionStatus.CLOSED
                || newStatus == AttractionStatus.MAINTENANCE) {
            this.closureReason = reason;
        } else {
            this.closureReason = null;
        }
        System.out.println("Attraction '" + name + "' -> " + newStatus +
                (reason != null ? " | Reason: " + reason : ""));
    }

    public boolean checkMaintenance() {
        if (visitorCount > 0
                && visitorCount % MAINTENANCE_LIMIT == 0
                && status == AttractionStatus.ACTIVE) {
            changeStatus(AttractionStatus.MAINTENANCE,
                    "Automatic preventive maintenance at " +
                            visitorCount + " visitors.");
            System.out.println("ALERT: Attraction '" + name +
                    "' blocked for technical inspection.");
            return true;
        }
        return false;
    }

    public void addInspection(TechnicalInspection inspection) {
        if (inspection == null) return;
        inspections.add(inspection);
        System.out.println("Inspection recorded for attraction '" + name + "'.");
    }

    public TechnicalInspection getLastInspection() {
        int n = inspections.getSize();
        if (n == 0) return null;
        return inspections.get(n - 1);
    }

    // ----------------------------------------------------------------
    // wait time
    // ----------------------------------------------------------------

    private void updateWaitTime() {
        int inQueue = virtualQueue.getSize();
        if (capacityPerCycle <= 0) return;
        int cyclesNeeded = (int) Math.ceil((double) inQueue / capacityPerCycle);
        estimatedWaitTime = cyclesNeeded * 5;
    }

    public int calculateWaitTime() {
        updateWaitTime();
        return estimatedWaitTime;
    }
}