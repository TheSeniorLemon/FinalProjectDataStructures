package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.AttractionStatus;
import co.edu.uniquindio.techpark.model.enums.AttractionType;
import co.edu.uniquindio.techpark.model.enums.TicketType;
import co.edu.uniquindio.techpark.model.structures.LinkedList;
import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String USERS_FILE = "techpark_users.json";
    private static final String PARK_FILE = "techpark_park.json";

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    // ================================================================
    // SAVE
    // ================================================================
    public static void save(UserStore store) {
        saveUsers(store);
    }

    public static void save(UserStore store, Park park) {
        saveUsers(store);
        savePark(park);
    }

    // ----------------------------------------------------------------
    // users
    // ----------------------------------------------------------------
    private static void saveUsers(UserStore store) {
        try {
            UsersSnapshot snapshot = new UsersSnapshot();

            LinkedList<Visitor> visitors = store.getVisitors();
            LinkedList<Operator> operators = store.getOperators();
            LinkedList<Administrator> administrators = store.getAdministrators();

            for (int i = 0; i < visitors.getSize(); i++) {
                Visitor v = visitors.get(i);
                if (v != null) snapshot.visitors.add(new VisitorDTO(v));
            }
            for (int i = 0; i < operators.getSize(); i++) {
                Operator op = operators.get(i);
                if (op != null) snapshot.operators.add(new OperatorDTO(op));
            }
            for (int i = 0; i < administrators.getSize(); i++) {
                Administrator a = administrators.get(i);
                if (a != null) snapshot.administrators.add(new AdministratorDTO(a));
            }

            write(USERS_FILE, GSON.toJson(snapshot));
            System.out.println("[DataManager] Users saved -> " + USERS_FILE);

        } catch (Exception e) {
            System.err.println("[DataManager] Failed to save users: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // park
    // ----------------------------------------------------------------
    private static void savePark(Park park) {
        try {
            ParkSnapshot snapshot = new ParkSnapshot();
            snapshot.id = park.getId();
            snapshot.name = park.getName();
            snapshot.maxCapacity = park.getMaxCapacity();

            LinkedList<Zone> zones = park.getZones();
            for (int i = 0; i < zones.getSize(); i++) {
                Zone z = zones.get(i);
                if (z == null) continue;
                ZoneDTO zdto = new ZoneDTO(z);

                LinkedList<Attraction> attractions = z.getAttractions();
                for (int j = 0; j < attractions.getSize(); j++) {
                    Attraction a = attractions.get(j);
                    if (a != null) zdto.attractions.add(new AttractionDTO(a));
                }
                snapshot.zones.add(zdto);
            }

            write(PARK_FILE, GSON.toJson(snapshot));
            System.out.println("[DataManager] Park saved -> " + PARK_FILE);

        } catch (Exception e) {
            System.err.println("[DataManager] Failed to save park: " + e.getMessage());
        }
    }

    // ================================================================
    // LOAD
    // ================================================================
    public static void load(UserStore store, Park park) {
        loadUsers(store);
        loadPark(park);
    }

    public static void loadUsers(UserStore store) {
        String json = read(USERS_FILE);
        if (json == null) return;

        try {
            UsersSnapshot snapshot = GSON.fromJson(json, UsersSnapshot.class);
            if (snapshot == null) return;

            for (VisitorDTO dto : snapshot.visitors) {
                Visitor v = new Visitor(
                        dto.id, dto.name, dto.document,
                        dto.email, dto.password, dto.age, dto.height
                );
                v.setVirtualBalance(dto.virtualBalance);
                if (dto.profilePhoto != null) v.setProfilePhoto(dto.profilePhoto);

                // restore ticket
                if (dto.ticket != null) {
                    Ticket t = switch (dto.ticket.ticketType) {
                        case "GENERAL" ->
                                new GeneralTicket(dto.ticket.ticketId, dto.ticket.price, dto.ticket.purchaseDate);
                        case "FAST_PASS" ->
                                new FastPassTicket(dto.ticket.ticketId, dto.ticket.price, dto.ticket.purchaseDate);
                        case "FAMILY" ->
                                new FamilyTicket(dto.ticket.ticketId, dto.ticket.price, dto.ticket.purchaseDate, dto.ticket.familyDiscount, dto.ticket.familyMaxPeople);
                        default -> null;
                    };
                    if (t != null) v.setTicket(t);
                }

                // restore favorites
                if (dto.favoriteIds != null)
                    for (String fid : dto.favoriteIds) v.addFavorite(fid);

                // restore visit history
                if (dto.visitHistory != null) {
                    for (VisitHistoryDTO vhDto : dto.visitHistory) {
                        try {
                            VisitHistory vh = new VisitHistory(
                                    vhDto.attractionId,
                                    vhDto.attractionName,
                                    vhDto.dateTime,
                                    vhDto.incurredCost,
                                    TicketType.valueOf(vhDto.ticketTypeUsed),
                                    vhDto.estimatedDurationMinutes
                            );
                            v.registerVisit(vh);
                        } catch (Exception ignored) {
                        }
                    }
                }

                store.register(v);
            }
            for (OperatorDTO dto : snapshot.operators) {
                Operator op = new Operator(
                        dto.id, dto.name, dto.document, dto.email, dto.password
                );
                if (dto.assignedZoneId != null) op.setAssignedZoneId(dto.assignedZoneId);
                store.register(op);
            }
            for (AdministratorDTO dto : snapshot.administrators) {
                Administrator a = new Administrator(
                        dto.id, dto.name, dto.document, dto.email, dto.password
                );
                store.register(a);
            }
            System.out.println("[DataManager] Users loaded from " + USERS_FILE);

        } catch (Exception e) {
            System.err.println("[DataManager] Failed to load users: " + e.getMessage());
        }
    }

    public static void loadPark(Park park) {
        String json = read(PARK_FILE);
        if (json == null) return;

        try {
            ParkSnapshot snapshot = GSON.fromJson(json, ParkSnapshot.class);
            if (snapshot == null) return;

            for (ZoneDTO zdto : snapshot.zones) {
                Zone zone = new Zone(zdto.id, zdto.name, zdto.maxCapacity);
                park.addZone(zone);

                for (AttractionDTO adto : zdto.attractions) {
                    Attraction a = new Attraction(
                            adto.id, adto.name,
                            AttractionType.valueOf(adto.type),
                            adto.capacityPerCycle,
                            adto.minimumHeight,
                            adto.minimumAge,
                            adto.additionalCost,
                            adto.estimatedWaitTime
                    );
                    a.changeStatus(
                            AttractionStatus.valueOf(adto.status),
                            adto.closureReason
                    );
                    if (adto.inspections != null) {
                        for (TechnicalInspectionDTO tdto : adto.inspections) {
                            try {
                                a.addInspection(new TechnicalInspection(
                                        tdto.id, tdto.dateTime,
                                        tdto.operatorId, tdto.operatorName,
                                        tdto.result, tdto.observations,
                                        tdto.successful
                                ));
                            } catch (Exception ignored) {
                            }
                        }
                    }
                    zone.addAttraction(a);
                    park.registerAttractionInCatalog(a);
                }
            }
            System.out.println("[DataManager] Park loaded from " + PARK_FILE);

        } catch (Exception e) {
            System.err.println("[DataManager] Failed to load park: " + e.getMessage());
        }
    }

    public static boolean usersFileExists() {
        return Files.exists(Paths.get(USERS_FILE));
    }

    public static boolean parkFileExists() {
        return Files.exists(Paths.get(PARK_FILE));
    }

    // ================================================================
    // file I/O
    // ================================================================
    private static void write(String filename, String content) throws IOException {
        Files.writeString(Paths.get(filename), content);
    }

    private static String read(String filename) {
        try {
            Path path = Paths.get(filename);
            if (!Files.exists(path)) return null;
            return Files.readString(path);
        } catch (IOException e) {
            System.err.println("[DataManager] Cannot read " + filename + ": " + e.getMessage());
            return null;
        }
    }

    // ================================================================
    // DTOs (plain objects for Gson serialization)
    // ================================================================
    private static class UsersSnapshot {
        List<VisitorDTO> visitors = new ArrayList<>();
        List<OperatorDTO> operators = new ArrayList<>();
        List<AdministratorDTO> administrators = new ArrayList<>();
    }

    private static class VisitorDTO {
        String id;
        String name;
        String document;
        String email;
        String password;
        String profilePhoto;
        int age;
        float height;
        double virtualBalance;
        TicketDTO ticket;
        List<String> favoriteIds = new ArrayList<>();
        List<VisitHistoryDTO> visitHistory = new ArrayList<>();

        VisitorDTO(Visitor v) {
            this.id = v.getId();
            this.name = v.getName();
            this.document = v.getDocument();
            this.email = v.getEmail();
            this.password = v.getPassword();
            this.profilePhoto = v.getProfilePhoto();
            this.age = v.getAge();
            this.height = v.getHeight();
            this.virtualBalance = v.getVirtualBalance();

            // ticket
            if (v.getTicket() != null) this.ticket = new TicketDTO(v.getTicket());

            // favorites
            if (v.getFavoriteIds() != null)
                this.favoriteIds.addAll(v.getFavoriteIds());

            // visit history
            LinkedList<VisitHistory> hist = v.viewHistory();
            if (hist != null) {
                for (int i = 0; i < hist.getSize(); i++) {
                    VisitHistory vh = hist.get(i);
                    if (vh != null) this.visitHistory.add(new VisitHistoryDTO(vh));
                }
            }
        }
    }

    private static class TicketDTO {
        String ticketId, ticketType, purchaseDate;
        double price;
        double familyDiscount;
        int familyMaxPeople;

        TicketDTO(Ticket t) {
            this.ticketId = t.getId();
            this.ticketType = t.getType().name();
            this.purchaseDate = t.getPurchaseDate();
            this.price = t.getPrice();
            if (t instanceof FamilyTicket ft) {
                this.familyDiscount = ft.getDiscountPercentage();
                this.familyMaxPeople = ft.getMaxMembers();
            }
        }
    }

    private static class VisitHistoryDTO {
        String attractionId, attractionName, dateTime, ticketTypeUsed;
        double incurredCost;
        int estimatedDurationMinutes;

        VisitHistoryDTO(VisitHistory vh) {
            this.attractionId = vh.getAttractionId();
            this.attractionName = vh.getAttractionName();
            this.dateTime = vh.getDateTime();
            this.incurredCost = vh.getIncurredCost();
            this.ticketTypeUsed = vh.getTicketTypeUsed().name();
            this.estimatedDurationMinutes = vh.getEstimatedDurationMinutes();
        }
    }

    private static class OperatorDTO {
        String id, name, document, email, password, assignedZoneId;

        OperatorDTO(Operator op) {
            this.id = op.getId();
            this.name = op.getName();
            this.document = op.getDocument();
            this.email = op.getEmail();
            this.password = op.getPassword();
            this.assignedZoneId = op.getAssignedZoneId();
        }
    }

    private static class AdministratorDTO {
        String id, name, document, email, password;

        AdministratorDTO(Administrator a) {
            this.id = a.getId();
            this.name = a.getName();
            this.document = a.getDocument();
            this.email = a.getEmail();
            this.password = a.getPassword();
        }
    }

    private static class ParkSnapshot {
        String id;
        String name;
        int maxCapacity;
        List<ZoneDTO> zones = new ArrayList<>();
    }

    private static class ZoneDTO {
        String id;
        String name;
        int maxCapacity;
        List<AttractionDTO> attractions = new ArrayList<>();

        ZoneDTO(Zone z) {
            this.id = z.getId();
            this.name = z.getName();
            this.maxCapacity = z.getMaxCapacity();
        }
    }

    private static class AttractionDTO {
        String id;
        String name;
        String type;
        String status;
        String closureReason;
        int capacityPerCycle;
        int minimumAge;
        int estimatedWaitTime;
        float minimumHeight;
        double additionalCost;
        List<TechnicalInspectionDTO> inspections = new ArrayList<>();

        AttractionDTO(Attraction a) {
            this.id = a.getId();
            this.name = a.getName();
            this.type = a.getType().name();
            this.status = a.getStatus().name();
            this.closureReason = a.getClosureReason();
            this.capacityPerCycle = a.getCapacityPerCycle();
            this.minimumHeight = a.getMinimumHeight();
            this.minimumAge = a.getMinimumAge();
            this.additionalCost = a.getAdditionalCost();
            this.estimatedWaitTime = a.getEstimatedWaitTime();

            LinkedList<TechnicalInspection> insp = a.getInspections();
            if (insp != null) {
                for (int i = 0; i < insp.getSize(); i++) {
                    TechnicalInspection ti = insp.get(i);
                    if (ti != null) this.inspections.add(new TechnicalInspectionDTO(ti));
                }
            }
        }
    }

    private static class TechnicalInspectionDTO {
        String id, dateTime, operatorId, operatorName, result, observations;
        boolean successful;

        TechnicalInspectionDTO(TechnicalInspection ti) {
            this.id = ti.getId();
            this.dateTime = ti.getDateTime();
            this.operatorId = ti.getOperatorId();
            this.operatorName = ti.getOperatorName();
            this.result = ti.getResult();
            this.observations = ti.getObservations();
            this.successful = ti.isSuccessful();
        }
    }
}