package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.AlertType;
import co.edu.uniquindio.techpark.model.enums.TicketType;
import co.edu.uniquindio.techpark.model.structures.*;

import java.util.Objects;

public final class Park {
    private String id;
    private String name;
    private int maxCapacity;
    private int currentVisitors;
    private boolean weatherAlertActive;
    private AlertType currentAlert;
    private LinkedList<Zone> zones;
    private LinkedList<Visitor> registeredVisitors;
    private LinkedList<Operator> operators;
    private LinkedList<Notification> notifications;
    private Graph<Attraction> mapGraph;
    private BSTree<Attraction> attractionCatalog;
    private Report dailyReport;
    private int ticketCounter;
    private int reportCounter;
    private int notificationCounter;
    private static volatile Park instance;

    private Park(String id, String name, int maxCapacity) {
        this.id = id;
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.currentVisitors = 0;
        this.weatherAlertActive = false;
        this.currentAlert = null;
        this.zones = new LinkedList<>();
        this.registeredVisitors = new LinkedList<>();
        this.operators = new LinkedList<>();
        this.notifications = new LinkedList<>();
        this.mapGraph = new Graph<>();
        this.attractionCatalog = new BSTree<>();
        this.dailyReport = null;
        this.ticketCounter = 0;
        this.reportCounter = 0;
        this.notificationCounter = 0;
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

    public boolean isWeatherAlertActive() {
        return weatherAlertActive;
    }
    public void setWeatherAlertActive(boolean weatherAlertActive) {
        this.weatherAlertActive = weatherAlertActive;
    }

    public AlertType getCurrentAlert() {
        return currentAlert;
    }
    public void setCurrentAlert(AlertType currentAlert) {
        this.currentAlert = currentAlert;
    }

    public LinkedList<Zone> getZones() {
        return zones;
    }
    public void setZones(LinkedList<Zone> zones) {
        this.zones = zones;
    }

    public LinkedList<Visitor> getRegisteredVisitors() {
        return registeredVisitors;
    }
    public void setRegisteredVisitors(LinkedList<Visitor> registeredVisitors) {
        this.registeredVisitors = registeredVisitors;
    }

    public LinkedList<Operator> getOperators() {
        return operators;
    }
    public void setOperators(LinkedList<Operator> operators) {
        this.operators = operators;
    }

    public LinkedList<Notification> getNotifications() {
        return notifications;
    }
    public void setNotifications(LinkedList<Notification> notifications) {
        this.notifications = notifications;
    }

    public Graph<Attraction> getMapGraph() {
        return mapGraph;
    }
    public void setMapGraph(Graph<Attraction> mapGraph) {
        this.mapGraph = mapGraph;
    }

    public BSTree<Attraction> getAttractionCatalog() {
        return attractionCatalog;
    }
    public void setAttractionCatalog(BSTree<Attraction> attractionCatalog) {
        this.attractionCatalog = attractionCatalog;
    }

    public Report getDailyReport() {
        return dailyReport;
    }
    public void setDailyReport(Report dailyReport) {
        this.dailyReport = dailyReport;
    }

    public int getTicketCounter() {
        return ticketCounter;
    }
    public void setTicketCounter(int ticketCounter) {
        this.ticketCounter = ticketCounter;
    }

    public int getReportCounter() {
        return reportCounter;
    }
    public void setReportCounter(int reportCounter) {
        this.reportCounter = reportCounter;
    }

    public int getNotificationCounter() {
        return notificationCounter;
    }
    public void setNotificationCounter(int notificationCounter) {
        this.notificationCounter = notificationCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Park park = (Park) o;
        return maxCapacity == park.maxCapacity && currentVisitors == park.currentVisitors && weatherAlertActive == park.weatherAlertActive && ticketCounter == park.ticketCounter && reportCounter == park.reportCounter && notificationCounter == park.notificationCounter && Objects.equals(id, park.id) && Objects.equals(name, park.name) && currentAlert == park.currentAlert && Objects.equals(zones, park.zones) && Objects.equals(registeredVisitors, park.registeredVisitors) && Objects.equals(operators, park.operators) && Objects.equals(notifications, park.notifications) && Objects.equals(mapGraph, park.mapGraph) && Objects.equals(attractionCatalog, park.attractionCatalog) && Objects.equals(dailyReport, park.dailyReport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, maxCapacity, currentVisitors, weatherAlertActive, currentAlert, zones, registeredVisitors, operators, notifications, mapGraph, attractionCatalog, dailyReport, ticketCounter, reportCounter, notificationCounter);
    }

    @Override
    public String toString() {
        return "Park{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", maxCapacity=" + maxCapacity +
                ", currentVisitors=" + currentVisitors +
                ", weatherAlertActive=" + weatherAlertActive +
                ", currentAlert=" + currentAlert +
                ", zones=" + zones +
                ", registeredVisitors=" + registeredVisitors +
                ", operators=" + operators +
                ", notifications=" + notifications +
                ", mapGraph=" + mapGraph +
                ", attractionCatalog=" + attractionCatalog +
                ", dailyReport=" + dailyReport +
                ", ticketCounter=" + ticketCounter +
                ", reportCounter=" + reportCounter +
                ", notificationCounter=" + notificationCounter +
                '}';
    }

    public static Park getInstance(String id, String name, int maxCapacity) {
        Park result = instance;
        if (result == null) {
            synchronized (Park.class) {
                result = instance;
                if (result == null) {
                    instance = result = new Park(id, name, maxCapacity);
                }
            }
        }
        return result;
    }

    public static Park getInstance() {
        Park result = instance;
        if (result == null) {
            throw new IllegalStateException(
                    "The park has not been initialized. Use getInstance(id, name, maxCapacity) first."
            );
        }
        return result;
    }

    // ----------------------------------------------------------------
    // zone management
    // ----------------------------------------------------------------

    public boolean addZone(Zone zone) {
        if (zone == null) return false;
        if (findZone(zone.getId()) != null) {
            System.out.println("The zone already exists in the park.");
            return false;
        }
        zones.add(zone);
        System.out.println("Zone '" + zone.getName() + "' added to the park.");
        return true;
    }

    public boolean removeZone(String zoneId) {
        Zone zone = findZone(zoneId);
        if (zone == null) {
            System.out.println("Zone not found.");
            return false;
        }
        if (zone.getAttractions().getSize() > 0) {
            System.out.println("A zone with active attractions cannot be removed.");
            return false;
        }
        boolean result = zones.remove(zone);
        if (result) {
            System.out.println("Zone '" + zone.getName() + "' removed from the park.");
        }
        return result;
    }

    public Zone findZone(String zoneId) {
        int n = zones.getSize();
        for (int i = 0; i < n; i++) {
            Zone z = zones.get(i);
            if (z != null && z.getId().equals(zoneId)) return z;
        }
        return null;
    }

    // ----------------------------------------------------------------
    // attraction management
    // ----------------------------------------------------------------

    public boolean registerAttractionInCatalog(Attraction attraction) {
        if (attraction == null) return false;
        attractionCatalog.insert(attraction);
        mapGraph.addNode(attraction);
        System.out.println("Attraction '" + attraction.getName() +
                "' registered in catalog and graph.");
        return true;
    }

    public boolean connectAttractions(Attraction origin, Attraction destination, double distance) {
        if (origin == null || destination == null || distance <= 0) return false;
        mapGraph.addEdge(origin, destination, distance);
        System.out.println("Path connected: " + origin.getName() +
                " <-> " + destination.getName() +
                " (distance: " + distance + " m)");
        return true;
    }

    public Attraction findAttractionByName(String name) {
        LinkedList<Attraction> inorder = attractionCatalog.inorder();
        int n = inorder.getSize();
        for (int i = 0; i < n; i++) {
            Attraction a = inorder.get(i);
            if (a != null && a.getName().equalsIgnoreCase(name)) return a;
        }
        return null;
    }

    public Attraction findAttractionInZone(String zoneId, String attractionId) {
        Zone zone = findZone(zoneId);
        if (zone == null) return null;
        return zone.findAttraction(attractionId);
    }

    public LinkedList<Attraction> listAllAttractions() {
        return attractionCatalog.inorder();
    }

    // ----------------------------------------------------------------
    // visitor and ticket management
    // ----------------------------------------------------------------

    public boolean registerVisitor(Visitor visitor) {
        if (visitor == null) return false;
        if (!hasAvailableCapacity()) {
            System.out.println("Park full. Cannot register more visitors.");
            return false;
        }
        registeredVisitors.add(visitor);
        currentVisitors++;
        if (dailyReport != null) {
            double ticketAmount = visitor.getTicket() != null
                    ? visitor.getTicket().getPrice() : 0.0;
            dailyReport.registerEntry(visitor, ticketAmount);
        }
        System.out.println("Visitor '" + visitor.getName() +
                "' registered in the park.");
        return true;
    }

    public boolean removeVisitor(String visitorId) {
        Visitor visitor = findVisitor(visitorId);
        if (visitor == null) {
            System.out.println("Visitor not found.");
            return false;
        }
        boolean result = registeredVisitors.remove(visitor);
        if (result) {
            currentVisitors--;
            System.out.println("Visitor '" + visitor.getName() +
                    "' removed from the park.");
        }
        return result;
    }

    public Visitor findVisitor(String visitorId) {
        int n = registeredVisitors.getSize();
        for (int i = 0; i < n; i++) {
            Visitor v = registeredVisitors.get(i);
            if (v != null && v.getId().equals(visitorId)) return v;
        }
        return null;
    }

    public Ticket sellTicket(TicketType type, Visitor visitor) {
        if (visitor == null || type == null) return null;
        if (!hasAvailableCapacity()) {
            System.out.println("Maximum capacity reached. Cannot sell more tickets.");
            return null;
        }
        ticketCounter++;
        String ticketId = "TK-" + String.format("%04d", ticketCounter);
        String date = java.time.LocalDate.now().toString();
        Ticket ticket = null;
        switch (type) {
            case GENERAL:
                ticket = new GeneralTicket(ticketId, 25000.0, date);
                break;
            case FAMILY:
                ticket = new FamilyTicket(ticketId, 80000.0, date, 15.0, 4);
                break;
            case FAST_PASS:
                ticket = new FastPassTicket(ticketId, 50000.0, date);
                break;
        }
        if (ticket != null) {
            visitor.setTicket(ticket);
            System.out.println(type + " ticket sold to '" + visitor.getName() + "'.");
        }
        return ticket;
    }

    // ----------------------------------------------------------------
    // operator management
    // ----------------------------------------------------------------

    public boolean registerOperator(Operator operator) {
        if (operator == null) return false;
        if (findOperator(operator.getId()) != null) {
            System.out.println("Operator already exists in the park.");
            return false;
        }
        operators.add(operator);
        System.out.println("Operator '" + operator.getName() + "' registered.");
        return true;
    }

    public boolean removeOperator(String operatorId) {
        Operator operator = findOperator(operatorId);
        if (operator == null) {
            System.out.println("Operator not found.");
            return false;
        }
        if (operator.hasAssignedZone()) {
            Zone zone = findZone(operator.getAssignedZoneId());
            if (zone != null && !zone.hasAvailableOperator()) {
                System.out.println("Cannot remove: zone would be left without operators.");
                return false;
            }
            if (zone != null) zone.removeOperator(operatorId);
        }
        boolean result = operators.remove(operator);
        if (result) {
            System.out.println("Operator '" + operator.getName() + "' removed.");
        }
        return result;
    }

    public Operator findOperator(String operatorId) {
        int n = operators.getSize();
        for (int i = 0; i < n; i++) {
            Operator op = operators.get(i);
            if (op != null && op.getId().equals(operatorId)) return op;
        }
        return null;
    }

    // ----------------------------------------------------------------
    // weather alert
    // ----------------------------------------------------------------

    public void activateWeatherAlert(AlertType type) {
        this.weatherAlertActive = true;
        this.currentAlert = type;
        int totalClosed = 0;
        int n = zones.getSize();
        for (int i = 0; i < n; i++) {
            Zone z = zones.get(i);
            if (z != null) {
                int closed = z.closeAttractionsDueToWeather(type);
                totalClosed += closed;
                if (dailyReport != null && closed > 0) {
                    dailyReport.registerEvent(closed +
                            " attractions closed in zone '" +
                            z.getName() + "' due to " + type);
                }
            }
        }
        String message = "WEATHER ALERT: " + type +
                ". " + totalClosed +
                " attractions closed. " +
                "Please move to covered areas.";
        notifyAllVisitors(message);
        System.out.println("Weather alert '" + type + "' activated. " +
                totalClosed + " attractions closed in total.");
    }

    public void deactivateWeatherAlert() {
        this.weatherAlertActive = false;
        this.currentAlert = null;
        notifyAllVisitors(
                "The weather alert has been lifted. " +
                        "Attractions will be gradually reopened.");
        System.out.println("Weather alert deactivated.");
    }

    // ----------------------------------------------------------------
    // notifications
    // ----------------------------------------------------------------

    public void notifyAllVisitors(String message) {
        int n = registeredVisitors.getSize();
        for (int i = 0; i < n; i++) {
            Visitor v = registeredVisitors.get(i);
            if (v != null) {
                sendNotification(v.getId(), message);
            }
        }
    }

    public void notifyVisitorsInQueue(Attraction attraction, String message) {
        PriorityQueue<Visitor> queue = attraction.getVirtualQueue();
        if (queue.isEmpty()) return;
        PriorityQueue<Visitor> temp = new PriorityQueue<>();
        while (!queue.isEmpty()) {
            Visitor v = queue.dequeue();
            sendNotification(v.getId(), message);
            temp.enqueue(v, v.getPriority());
        }
        while (!temp.isEmpty()) {
            Visitor v = temp.dequeue();
            queue.enqueue(v, v.getPriority());
        }
    }

    private void sendNotification(String recipientId, String message) {
        notificationCounter++;
        String notifId = "NTF-" + String.format("%04d", notificationCounter);
        String dateTime = java.time.LocalDateTime.now().toString();
        Notification notif = new Notification(notifId, message, dateTime, recipientId);
        notifications.add(notif);
    }

    // ----------------------------------------------------------------
    // routes and graph
    // ----------------------------------------------------------------

    public LinkedList<Attraction> calculateOptimalRoute(Attraction origin, Attraction destination) {
        if (origin == null || destination == null) return null;
        return mapGraph.dijkstra(origin, destination);
    }

    public LinkedList<Attraction> exploreFrom(Attraction origin) {
        if (origin == null) return null;
        return mapGraph.bfs(origin);
    }

    public LinkedList<GraphNode<Attraction>> detectClusters() {
        return mapGraph.detectClusters();
    }

    // ----------------------------------------------------------------
    // capacity
    // ----------------------------------------------------------------

    public boolean hasAvailableCapacity() {
        return currentVisitors < maxCapacity;
    }

    public int getAvailableCapacity() {
        return maxCapacity - currentVisitors;
    }

    public double getOccupancyPercentage() {
        if (maxCapacity == 0) return 0.0;
        return (currentVisitors * 100.0) / maxCapacity;
    }

    // ----------------------------------------------------------------
    // start and end of the day
    // ----------------------------------------------------------------

    public void startDay() {
        reportCounter++;
        String date = java.time.LocalDate.now().toString();
        String reportId = "RPT-" + String.format("%04d", reportCounter);
        dailyReport = new Report(reportId, date);
        System.out.println("Day started. Report " + reportId + " created.");
    }

    public Report closeDay() {
        if (dailyReport == null) {
            System.out.println("No active day to close.");
            return null;
        }
        LinkedList<Attraction> allAttractions = listAllAttractions();
        dailyReport.loadAttractionsSnapshot(allAttractions);
        Report result = dailyReport;
        System.out.println(result.export());
        dailyReport = null;
        System.out.println("Day closed.");
        return result;
    }
}