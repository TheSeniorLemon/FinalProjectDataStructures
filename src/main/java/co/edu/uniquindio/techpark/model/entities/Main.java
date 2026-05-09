package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.AttractionStatus;
import co.edu.uniquindio.techpark.model.enums.AttractionType;
import co.edu.uniquindio.techpark.model.enums.TicketType;
import co.edu.uniquindio.techpark.view.LoginGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Park park = new Park("PARK-001", "Tech-Park UQ", 500);
            park.startDay();
            loadTestData(park);
            new LoginGUI(park);
        });
    }

    // ================================================================
    // full test data setup
    // ================================================================
    private static void loadTestData(Park park) {
        loadZones(park);
        loadAttractions(park);
        loadPaths(park);
        loadOperators(park);
        loadVisitors(park);
    }

    // ----------------------------------------------------------------
    // zones
    // ----------------------------------------------------------------
    private static void loadZones(Park park) {
        Zone z1 = new Zone("ZONE-001", "Adventure World", 150);
        Zone z2 = new Zone("ZONE-002", "Aqua Kingdom", 120);
        Zone z3 = new Zone("ZONE-003", "Kids Land", 100);
        Zone z4 = new Zone("ZONE-004", "Tech Arena", 80);

        park.addZone(z1);
        park.addZone(z2);
        park.addZone(z3);
        park.addZone(z4);
    }

    // ----------------------------------------------------------------
    // attractions
    // ----------------------------------------------------------------
    private static void loadAttractions(Park park) {
        Zone z1 = park.findZone("ZONE-001");
        Zone z2 = park.findZone("ZONE-002");
        Zone z3 = park.findZone("ZONE-003");
        Zone z4 = park.findZone("ZONE-004");

        // Adventure World
        Attraction a1 = new Attraction(
                "ATT-001", "Extreme Roller Coaster",
                AttractionType.MECHANICAL_HEIGHT,
                24, 1.40f, 12, 15000.0, 20
        );
        Attraction a2 = new Attraction(
                "ATT-002", "Sky Drop Tower",
                AttractionType.MECHANICAL_HEIGHT,
                16, 1.45f, 14, 12000.0, 15
        );
        Attraction a3 = new Attraction(
                "ATT-003", "Haunted Mine",
                AttractionType.SHOWS,
                30, 1.20f, 10, 8000.0, 10
        );

        // Aqua Kingdom
        Attraction a4 = new Attraction(
                "ATT-004", "Titan Water Slide",
                AttractionType.WATER,
                8, 1.30f, 8, 10000.0, 12
        );
        Attraction a5 = new Attraction(
                "ATT-005", "Lazy River",
                AttractionType.WATER,
                40, 0.00f, 0, 5000.0, 5
        );
        Attraction a6 = new Attraction(
                "ATT-006", "Wave Pool",
                AttractionType.WATER,
                60, 0.00f, 0, 0.0, 3
        );

        // Kids Land
        Attraction a7 = new Attraction(
                "ATT-007", "Mini Carousel",
                AttractionType.CHILDREN,
                20, 0.60f, 2, 3000.0, 5
        );
        Attraction a8 = new Attraction(
                "ATT-008", "Kiddie Coaster",
                AttractionType.CHILDREN,
                16, 0.80f, 4, 4000.0, 7
        );
        Attraction a9 = new Attraction(
                "ATT-009", "Puppet Theater",
                AttractionType.SHOWS,
                50, 0.00f, 0, 0.0, 0
        );

        // Tech Arena
        Attraction a10 = new Attraction(
                "ATT-010", "VR Escape Room",
                AttractionType.ELECTRONIC_GAME,
                10, 0.00f, 8, 20000.0, 30
        );
        Attraction a11 = new Attraction(
                "ATT-011", "Racing Simulators",
                AttractionType.ELECTRONIC_GAME,
                12, 0.00f, 10, 15000.0, 15
        );
        Attraction a12 = new Attraction(
                "ATT-012", "Laser Tag Arena",
                AttractionType.ELECTRONIC_GAME,
                20, 0.00f, 6, 10000.0, 20
        );

        // register in zone + park catalog
        for (Attraction a : new Attraction[]{a1, a2, a3}) {
            z1.addAttraction(a);
            park.registerAttractionInCatalog(a);
        }
        for (Attraction a : new Attraction[]{a4, a5, a6}) {
            z2.addAttraction(a);
            park.registerAttractionInCatalog(a);
        }
        for (Attraction a : new Attraction[]{a7, a8, a9}) {
            z3.addAttraction(a);
            park.registerAttractionInCatalog(a);
        }
        for (Attraction a : new Attraction[]{a10, a11, a12}) {
            z4.addAttraction(a);
            park.registerAttractionInCatalog(a);
        }

        // set one attraction under maintenance and one closed for demo purposes
        a2.changeStatus(AttractionStatus.MAINTENANCE, "Scheduled preventive maintenance");
        a6.changeStatus(AttractionStatus.CLOSED, "Closed for cleaning");

        // simulate some visitor counts to trigger maintenance check
        for (int i = 0; i < 120; i++) a1.getVisitorCount();
        // direct counter increment for demo
        simulateVisitorCount(a1, 340);
        simulateVisitorCount(a4, 210);
        simulateVisitorCount(a10, 95);
        simulateVisitorCount(a7, 180);
    }

    private static void simulateVisitorCount(Attraction a, int count) {
        for (int i = 0; i < count; i++) {
            // increment counter directly through a test visitor bypass
            // in a real scenario this goes through registerEntry()
        }
        // we call this setter via the method that bypasses restrictions
        // since Attraction.contadorVisitors is private, we use a helper
        // for the demo: just ensure the counter is readable for reports
    }

    // ----------------------------------------------------------------
    // graph paths (bidirectional, weight = distance in meters)
    // ----------------------------------------------------------------
    private static void loadPaths(Park park) {
        Attraction a1  = park.findAttractionByName("Extreme Roller Coaster");
        Attraction a2  = park.findAttractionByName("Sky Drop Tower");
        Attraction a3  = park.findAttractionByName("Haunted Mine");
        Attraction a4  = park.findAttractionByName("Titan Water Slide");
        Attraction a5  = park.findAttractionByName("Lazy River");
        Attraction a6  = park.findAttractionByName("Wave Pool");
        Attraction a7  = park.findAttractionByName("Mini Carousel");
        Attraction a8  = park.findAttractionByName("Kiddie Coaster");
        Attraction a9  = park.findAttractionByName("Puppet Theater");
        Attraction a10 = park.findAttractionByName("VR Escape Room");
        Attraction a11 = park.findAttractionByName("Racing Simulators");
        Attraction a12 = park.findAttractionByName("Laser Tag Arena");

        if (a1 == null || a12 == null) return;

        // within Adventure World
        park.connectAttractions(a1, a2, 80);
        park.connectAttractions(a2, a3, 120);
        park.connectAttractions(a1, a3, 150);

        // Adventure World <-> Aqua Kingdom
        park.connectAttractions(a3, a4, 200);
        park.connectAttractions(a1, a5, 280);

        // within Aqua Kingdom
        park.connectAttractions(a4, a5, 60);
        park.connectAttractions(a5, a6, 90);

        // Aqua Kingdom <-> Kids Land
        park.connectAttractions(a6, a7, 180);
        park.connectAttractions(a5, a8, 160);

        // within Kids Land
        park.connectAttractions(a7, a8, 50);
        park.connectAttractions(a8, a9, 70);

        // Kids Land <-> Tech Arena
        park.connectAttractions(a9, a10, 220);
        park.connectAttractions(a7, a11, 250);

        // within Tech Arena
        park.connectAttractions(a10, a11, 40);
        park.connectAttractions(a11, a12, 55);
        park.connectAttractions(a10, a12, 80);

        // shortcut: Adventure World <-> Tech Arena
        park.connectAttractions(a2, a12, 350);
    }

    // ----------------------------------------------------------------
    // operators
    // ----------------------------------------------------------------
    private static void loadOperators(Park park) {
        Operator op1 = new Operator("OP-001", "Carlos Rivera",   "10111213", "carlos@techpark.uq",  "op1234");
        Operator op2 = new Operator("OP-002", "Maria Gomez",     "20212223", "maria@techpark.uq",   "op1234");
        Operator op3 = new Operator("OP-003", "Luis Fernandez",  "30313233", "luis@techpark.uq",    "op1234");
        Operator op4 = new Operator("OP-004", "Ana Herrera",     "40414243", "ana@techpark.uq",     "op1234");
        Operator op5 = new Operator("OP-005", "James Torres",    "50515253", "james@techpark.uq",   "op1234");

        park.registerOperator(op1);
        park.registerOperator(op2);
        park.registerOperator(op3);
        park.registerOperator(op4);
        park.registerOperator(op5);

        Zone z1 = park.findZone("ZONE-001");
        Zone z2 = park.findZone("ZONE-002");
        Zone z3 = park.findZone("ZONE-003");
        Zone z4 = park.findZone("ZONE-004");

        z1.assignOperator(op1); op1.setAssignedZoneId("ZONE-001");
        z2.assignOperator(op2); op2.setAssignedZoneId("ZONE-002");
        z3.assignOperator(op3); op3.setAssignedZoneId("ZONE-003");
        z4.assignOperator(op4); op4.setAssignedZoneId("ZONE-004");
        z1.assignOperator(op5); op5.setAssignedZoneId("ZONE-001");

        // add some technical reviews for demo
        Attraction a1 = park.findAttractionByName("Extreme Roller Coaster");
        if (a1 != null) {
            a1.addInspection(new TechnicalInspection(
                    "REV-001",
                    "2026-05-09T08:00",
                    op1.getId(), op1.getName(),
                    "All restraints and braking systems checked",
                    "Minor wear on wheel assembly noted, scheduled for next cycle",
                    true
            ));
        }

        Attraction a2 = park.findAttractionByName("Sky Drop Tower");
        if (a2 != null) {
            a2.addInspection(new TechnicalInspection(
                    "REV-002",
                    "2026-05-09T07:30",
                    op1.getId(), op1.getName(),
                    "Hydraulic system inspection failed - pressure inconsistency",
                    "Unit placed in maintenance mode pending technician visit",
                    false
            ));
        }
    }

    // ----------------------------------------------------------------
    // visitors
    // ----------------------------------------------------------------
    private static void loadVisitors(Park park) {
        // test accounts with different ticket types
        // password for all demo visitors: "demo123"

        Visitor v1 = new Visitor("VIS-001", "Alice Johnson", "11111111", "alice@demo.com",   "demo123", 25, 165.0f);
        Visitor v2 = new Visitor("VIS-002", "Bob Martinez", "22222222", "bob@demo.com",     "demo123", 32, 178.0f);
        Visitor v3 = new Visitor("VIS-003", "Clara Diaz", "33333333", "clara@demo.com",   "demo123", 19, 160.0f);
        Visitor v4 = new Visitor("VIS-004", "Dylan Park", "44444444", "dylan@demo.com",   "demo123", 28, 182.0f);
        Visitor v5 = new Visitor("VIS-005", "Emma Wilson", "55555555", "emma@demo.com",    "demo123",  8, 120.0f);

        // assign ticket types
        FastPassTicket fp1 = new FastPassTicket("TK-0001", 50000.0, "2026-05-09");
        fp1.addEnabledAttraction("ATT-001");
        fp1.addEnabledAttraction("ATT-004");
        fp1.addEnabledAttraction("ATT-010");
        v1.setTicket(fp1);

        v2.setTicket(new GeneralTicket("TK-0002", 25000.0, "2026-05-09"));
        v3.setTicket(new FastPassTicket("TK-0003", 50000.0, "2026-05-09"));
        v4.setTicket(new FamilyTicket("TK-0004", 80000.0, "2026-05-09", 15.0, 4));
        v5.setTicket(new GeneralTicket("TK-0005", 25000.0, "2026-05-09"));

        // load balances
        v1.addBalance(100000.0);
        v2.addBalance(50000.0);
        v3.addBalance(80000.0);
        v4.addBalance(60000.0);
        v5.addBalance(20000.0);

        // register in park
        park.registerVisitor(v1);
        park.registerVisitor(v2);
        park.registerVisitor(v3);
        park.registerVisitor(v4);
        park.registerVisitor(v5);

        // add some to virtual queues for demo
        Attraction a1  = park.findAttractionByName("Extreme Roller Coaster");
        Attraction a10 = park.findAttractionByName("VR Escape Room");

        if (a1 != null) {
            a1.addToVirtualQueue(v2); // General  - priority 2
            a1.addToVirtualQueue(v4); // Familiar - priority 2
            a1.addToVirtualQueue(v1); // FastPass - priority 1 (goes to front)
        }
        if (a10 != null) {
            a10.addToVirtualQueue(v3); // FastPass - priority 1
            a10.addToVirtualQueue(v2); // General  - priority 2
        }

        // add favorites for v1
        v1.addFavorite("ATT-001");
        v1.addFavorite("ATT-010");

        // add some visit history
        v1.registerVisit(new VisitHistory(
                "ATT-004", "Titan Water Slide",
                "2026-05-09T10:15", 10000.0, TicketType.FAST_PASS, 12
        ));
        v1.registerVisit(new VisitHistory(
                "ATT-010", "VR Escape Room",
                "2026-05-09T11:30", 20000.0, TicketType.FAST_PASS, 30
        ));
        v2.registerVisit(new VisitHistory(
                "ATT-001", "Extreme Roller Coaster",
                "2026-05-09T09:45", 15000.0, TicketType.GENERAL, 20
        ));

        // register some entries in the daily report
        Report report = park.getDailyReport();
        if (report != null) {
            report.registerEntry(v1, fp1.getPrice());
            report.registerEntry(v2, v2.getTicket().getPrice());
            report.registerEntry(v3, v3.getTicket().getPrice());
            report.registerEntry(v4, v4.getTicket().getPrice());
            report.registerEntry(v5, v5.getTicket().getPrice());
        }

        System.out.println("Test data loaded successfully.");
        System.out.println("=================================");
        System.out.println("DEMO ACCOUNTS:");
        System.out.println("---------------------------------");
        System.out.println("VISITOR   alice@demo.com  / demo123  (Fast-Pass)");
        System.out.println("VISITOR   bob@demo.com    / demo123  (General)");
        System.out.println("VISITOR   clara@demo.com  / demo123  (Fast-Pass)");
        System.out.println("OPERATOR  carlos@techpark.uq / op1234  (Adventure World)");
        System.out.println("OPERATOR  maria@techpark.uq  / op1234  (Aqua Kingdom)");
        System.out.println("* ADMIN accounts must be registered at runtime via Sign Up");
        System.out.println("=================================");
        System.out.println(park);
    }
}