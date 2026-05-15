package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.entities.*;
import co.edu.uniquindio.techpark.model.enums.*;
import co.edu.uniquindio.techpark.view.LoginGUI;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        EmailService.getInstance().configure(
                "your.email@gmail.com",
                "xxxx xxxx xxxx xxxx"
        );

        SwingUtilities.invokeLater(() -> {
            Park park = new Park("PARK-001", "Tech-Park UQ", 500);
            park.startDay();

            // load persisted data if available, otherwise load defaults
            if (DataManager.usersFileExists() || DataManager.parkFileExists()) {
                DataManager.load(UserStore.getInstance(), park);
                System.out.println("[Main] Data loaded from files.");
            } else {
                loadTestData(park);
                System.out.println("[Main] Default test data loaded.");
            }

            new LoginGUI(park);
        });
    }

    // ================================================================
    // test data
    // ================================================================
    private static void loadTestData(Park park) {
        loadZones(park);
        loadAttractions(park);
        loadPaths(park);
        loadOperators(park);
        loadAdmins(park);
        loadVisitors(park);
        DataManager.save(UserStore.getInstance(), park);
    }

    // ----------------------------------------------------------------
    // zones
    // ----------------------------------------------------------------
    private static void loadZones(Park park) {
        park.addZone(new Zone("ZONE-001", "Adventure World", 150));
        park.addZone(new Zone("ZONE-002", "Aqua Kingdom",    120));
        park.addZone(new Zone("ZONE-003", "Kids Land",       100));
        park.addZone(new Zone("ZONE-004", "Tech Arena",       80));
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
        Attraction a1 = new Attraction("ATT-001","Extreme Roller Coaster", AttractionType.MECHANICAL_HEIGHT, 24, 1.40f, 12, 15000.0, 20);
        Attraction a2 = new Attraction("ATT-002","Sky Drop Tower", AttractionType.MECHANICAL_HEIGHT, 16, 1.45f, 14, 12000.0, 15);
        Attraction a3 = new Attraction("ATT-003","Haunted Mine", AttractionType.SHOWS, 30, 1.20f, 10, 8000.0, 10);

        // Aqua Kingdom
        Attraction a4 = new Attraction("ATT-004","Titan Water Slide", AttractionType.WATER, 8, 1.30f, 8, 10000.0, 12);
        Attraction a5 = new Attraction("ATT-005","Lazy River", AttractionType.WATER, 40, 0.00f, 0, 5000.0, 5);
        Attraction a6 = new Attraction("ATT-006","Wave Pool", AttractionType.WATER, 60, 0.00f, 0, 0.0, 3);

        // Kids Land
        Attraction a7 = new Attraction("ATT-007","Mini Carousel", AttractionType.CHILDREN, 20, 0.60f, 2, 3000.0, 5);
        Attraction a8 = new Attraction("ATT-008","Kiddie Coaster", AttractionType.CHILDREN, 16, 0.80f, 4, 4000.0, 7);
        Attraction a9 = new Attraction("ATT-009","Puppet Theater", AttractionType.SHOWS, 50, 0.00f, 0, 0.0, 0);

        // Tech Arena
        Attraction a10 = new Attraction("ATT-010","VR Escape Room", AttractionType.ELECTRONIC_GAME, 10, 0.00f, 8, 20000.0, 30);
        Attraction a11 = new Attraction("ATT-011","Racing Simulators", AttractionType.ELECTRONIC_GAME, 12, 0.00f, 10, 15000.0, 15);
        Attraction a12 = new Attraction("ATT-012","Laser Tag Arena", AttractionType.ELECTRONIC_GAME, 20, 0.00f, 6, 10000.0, 20);

        for (Attraction a : new Attraction[]{a1, a2, a3}) { z1.addAttraction(a); park.registerAttractionInCatalog(a); }
        for (Attraction a : new Attraction[]{a4, a5, a6}) { z2.addAttraction(a); park.registerAttractionInCatalog(a); }
        for (Attraction a : new Attraction[]{a7, a8, a9}) { z3.addAttraction(a); park.registerAttractionInCatalog(a); }
        for (Attraction a : new Attraction[]{a10,a11,a12}){ z4.addAttraction(a); park.registerAttractionInCatalog(a); }

        // demo states
        a2.changeStatus(AttractionStatus.MAINTENANCE, "Scheduled preventive maintenance");
        a6.changeStatus(AttractionStatus.CLOSED,      "Closed for cleaning");

        // demo inspections
        Attraction rc = park.findAttractionByName("Extreme Roller Coaster");
        if (rc != null) rc.addInspection(new TechnicalInspection(
                "REV-001","2026-05-09T08:00","OP-001","Carlos Rivera",
                "All restraints and braking systems checked",
                "Minor wear on wheel assembly - scheduled for next cycle", true
        ));
        Attraction sky = park.findAttractionByName("Sky Drop Tower");
        if (sky != null) sky.addInspection(new TechnicalInspection(
                "REV-002","2026-05-09T07:30","OP-001","Carlos Rivera",
                "Hydraulic system inspection failed - pressure inconsistency",
                "Unit placed in maintenance mode pending technician visit", false
        ));
    }

    // ----------------------------------------------------------------
    // graph paths (weight = meters)
    // ----------------------------------------------------------------
    private static void loadPaths(Park park) {
        Attraction[] a = new Attraction[13];
        String[] names = {
                null,
                "Extreme Roller Coaster", "Sky Drop Tower", "Haunted Mine",
                "Titan Water Slide", "Lazy River", "Wave Pool",
                "Mini Carousel", "Kiddie Coaster", "Puppet Theater",
                "VR Escape Room", "Racing Simulators", "Laser Tag Arena"
        };
        for (int i = 1; i <= 12; i++) a[i] = park.findAttractionByName(names[i]);
        if (a[1] == null) return;

        // within Adventure World
        park.connectAttractions(a[1], a[2], 80);
        park.connectAttractions(a[2], a[3], 120);
        park.connectAttractions(a[1], a[3], 150);

        // Adventure World <-> Aqua Kingdom
        park.connectAttractions(a[3], a[4], 200);
        park.connectAttractions(a[1], a[5], 280);

        // within Aqua Kingdom
        park.connectAttractions(a[4], a[5], 60);
        park.connectAttractions(a[5], a[6], 90);

        // Aqua Kingdom <-> Kids Land
        park.connectAttractions(a[6], a[7], 180);
        park.connectAttractions(a[5], a[8], 160);

        // within Kids Land
        park.connectAttractions(a[7], a[8], 50);
        park.connectAttractions(a[8], a[9], 70);

        // Kids Land <-> Tech Arena
        park.connectAttractions(a[9],  a[10], 220);
        park.connectAttractions(a[7],  a[11], 250);

        // within Tech Arena
        park.connectAttractions(a[10], a[11], 40);
        park.connectAttractions(a[11], a[12], 55);
        park.connectAttractions(a[10], a[12], 80);

        // shortcut: Adventure World <-> Tech Arena
        park.connectAttractions(a[2], a[12], 350);
    }

    // ----------------------------------------------------------------
    // operators  (password: op1234)
    // ----------------------------------------------------------------
    private static void loadOperators(Park park) {
        UserStore store = UserStore.getInstance();

        Operator[] ops = {
                new Operator("OP-001","Carlos Rivera", "10111213","carlos@techpark.uq", "op1234"),
                new Operator("OP-002","Maria Gomez", "20212223","maria@techpark.uq", "op1234"),
                new Operator("OP-003","Luis Fernandez", "30313233","luis@techpark.uq", "op1234"),
                new Operator("OP-004","Ana Herrera", "40414243","ana@techpark.uq", "op1234"),
                new Operator("OP-005","James Torres", "50515253","james@techpark.uq", "op1234"),
        };

        String[] zoneIds = {"ZONE-001","ZONE-002","ZONE-003","ZONE-004","ZONE-001"};

        for (int i = 0; i < ops.length; i++) {
            store.register(ops[i]);
            park.registerOperator(ops[i]);
            Zone zone = park.findZone(zoneIds[i]);
            if (zone != null) {
                zone.assignOperator(ops[i]);
                ops[i].assignToZone(zone);
            }
        }
    }

    // ----------------------------------------------------------------
    // admins  (password: admin123)
    // must register in UserStore only - not in park
    // ----------------------------------------------------------------
    private static void loadAdmins(Park park) {
        UserStore store = UserStore.getInstance();
        store.register(new Administrator("ADM-001","Park Director","00000001","admin@techpark.uq","admin123"));
        store.register(new Administrator("ADM-002","Operations Manager","00000002","ops@techpark.uq","admin123"));
    }

    // ----------------------------------------------------------------
    // visitors  (password: demo123)
    // stored only in UserStore; park entry is separate
    // ----------------------------------------------------------------
    private static void loadVisitors(Park park) {
        UserStore store = UserStore.getInstance();

        Visitor[] visitors = {
                new Visitor("VIS-001","Alice Johnson", "11111111","alice@demo.com","demo123",25,165.0f),
                new Visitor("VIS-002","Bob Martinez", "22222222","bob@demo.com", "demo123",32,178.0f),
                new Visitor("VIS-003","Clara Diaz", "33333333","clara@demo.com","demo123",19,160.0f),
                new Visitor("VIS-004","Dylan Park", "44444444","dylan@demo.com","demo123",28,182.0f),
                new Visitor("VIS-005","Emma Wilson", "55555555","emma@demo.com", "demo123", 8,120.0f),
        };

        // assign tickets
        visitors[0].setTicket(new FastPassTicket("TK-0001",50000.0, today()));
        visitors[1].setTicket(new GeneralTicket( "TK-0002",25000.0, today()));
        visitors[2].setTicket(new FastPassTicket("TK-0003",50000.0, today()));
        visitors[3].setTicket(new FamilyTicket(  "TK-0004",80000.0, today(), 15.0, 4));
        visitors[4].setTicket(new GeneralTicket( "TK-0005",25000.0, today()));

        // load balances
        double[] balances = {100000, 50000, 80000, 60000, 20000};
        for (int i = 0; i < visitors.length; i++) {
            visitors[i].addBalance(balances[i]);
            store.register(visitors[i]);
        }

        // alice and bob enter the park (have tickets)
        park.registerVisitor(visitors[0]);
        park.registerVisitor(visitors[1]);

        // virtual queues
        Attraction rc  = park.findAttractionByName("Extreme Roller Coaster");
        Attraction vr  = park.findAttractionByName("VR Escape Room");
        if (rc  != null) { rc.addToVirtualQueue(visitors[1]); rc.addToVirtualQueue(visitors[0]); }
        if (vr  != null) { vr.addToVirtualQueue(visitors[2]); vr.addToVirtualQueue(visitors[1]); }

        // favorites and history for alice
        visitors[0].addFavorite("ATT-001");
        visitors[0].addFavorite("ATT-010");
        visitors[0].registerVisit(new VisitHistory("ATT-004","Titan Water Slide", today()+"T10:15", 10000.0, TicketType.FAST_PASS));
        visitors[0].registerVisit(new VisitHistory("ATT-010","VR Escape Room", today()+"T11:30", 20000.0, TicketType.FAST_PASS));

        // bob's history
        visitors[1].registerVisit(new VisitHistory("ATT-001","Extreme Roller Coaster", today()+"T09:45", 15000.0, TicketType.GENERAL));

        // register entries in daily report
        Report report = park.getDailyReport();
        if (report != null) {
            report.registerEntry(visitors[0], visitors[0].getTicket().getPrice());
            report.registerEntry(visitors[1], visitors[1].getTicket().getPrice());
        }

        printDemoAccounts();
    }

    // ----------------------------------------------------------------
    // helpers
    // ----------------------------------------------------------------
    private static String today() {
        return java.time.LocalDate.now().toString();
    }

    private static void printDemoAccounts() {
        System.out.println();
        System.out.println("========================================");
        System.out.println("  TECH-PARK UQ  -  DEMO ACCOUNTS");
        System.out.println("========================================");
        System.out.println("ROLE          EMAIL                     PASSWORD");
        System.out.println("--------      ----------------------    --------");
        System.out.println("VISITOR       alice@demo.com            demo123   (Fast-Pass, in park)");
        System.out.println("VISITOR       bob@demo.com              demo123   (General, in park)");
        System.out.println("VISITOR       clara@demo.com            demo123   (Fast-Pass)");
        System.out.println("VISITOR       dylan@demo.com            demo123   (Family)");
        System.out.println("VISITOR       emma@demo.com             demo123   (General, age 8)");
        System.out.println("OPERATOR      carlos@techpark.uq        op1234    (Adventure World)");
        System.out.println("OPERATOR      maria@techpark.uq         op1234    (Aqua Kingdom)");
        System.out.println("OPERATOR      luis@techpark.uq          op1234    (Kids Land)");
        System.out.println("OPERATOR      ana@techpark.uq           op1234    (Tech Arena)");
        System.out.println("ADMIN         admin@techpark.uq         admin123");
        System.out.println("ADMIN         ops@techpark.uq           admin123");
        System.out.println("========================================");
        System.out.println();
    }
}