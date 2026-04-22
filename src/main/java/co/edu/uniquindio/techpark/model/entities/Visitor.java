package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.TicketType;
import co.edu.uniquindio.techpark.model.enums.UserRole;
import co.edu.uniquindio.techpark.model.structures.LinkedList;

import java.util.Set;

public class Visitor extends User {
    private int age;
    private float height;
    private double virtualBalance;
    private String profilePhoto;
    private Ticket ticket;
    private LinkedList<VisitHistory> visitHistory;
    private Set<String> favoriteIds;

    public Visitor(String id, String name, String email, String password, UserRole userRole, int age, float height) {
        super(id, name, email, password, userRole);
        this.age = age;
        this.height = height;
        this.virtualBalance = 0.0;
        this.profilePhoto = null;
        this.ticket = null;
        this.visitHistory = new LinkedList<>();
        this.favoriteIds = new java.util.HashSet<>();
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public float getHeight() {
        return height;
    }
    public void setHeight(float height) {
        this.height = height;
    }

    public double getVirtualBalance() {
        return virtualBalance;
    }
    public void setVirtualBalance(double virtualBalance) {
        this.virtualBalance = virtualBalance;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }
    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Ticket getTicket() {
        return ticket;
    }
    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public LinkedList<VisitHistory> getVisitHistory() {
        return visitHistory;
    }
    public void setVisitHistory(LinkedList<VisitHistory> visitHistory) {
        this.visitHistory = visitHistory;
    }

    public Set<String> getFavoriteIds() {
        return favoriteIds;
    }
    public void setFavoriteIds(Set<String> favoriteIds) {
        this.favoriteIds = favoriteIds;
    }

    @Override
    public String toString() {
        return "Visitor{" +
                "age=" + age +
                ", height=" + height +
                ", virtualBalance=" + virtualBalance +
                ", profilePhoto='" + profilePhoto + '\'' +
                ", ticket=" + (ticket != null ? ticket.getType() : "none") +
                ", visitHistory=" + visitHistory +
                ", favoriteIds=" + favoriteIds +
                ", id='" + id + '\'' +
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

    public double checkBalance() {
        return virtualBalance;
    }

    public boolean addBalance(double amount) {
        if (amount <= 0) return false;
        virtualBalance += amount;
        return true;
    }

    public boolean deductBalance(double amount) {
        if (amount <= 0 || virtualBalance < amount) return false;
        virtualBalance -= amount;
        return true;
    }

    public boolean hasSufficientBalance(double cost) {
        return virtualBalance >= cost;
    }

    public void addFavorite(String attractionId) {
        favoriteIds.add(attractionId);
    }

    public void removeFavorite(String attractionId) {
        favoriteIds.remove(attractionId);
    }

    public boolean isFavorite(String attractionId) {
        return favoriteIds.contains(attractionId);
    }

    public void registerVisit(VisitHistory visit) {
        visitHistory.add(visit);
    }

    public LinkedList<VisitHistory> viewHistory() {
        return visitHistory;
    }

    public boolean hasFastPassTicket() {
        return ticket != null && ticket.getType() == TicketType.FAST_PASS;
    }

    public int getPriority() {
        if (ticket == null) return 2;
        return ticket.getPriority();
    }

    public boolean meetsRestrictions(Attraction attraction) {
        return this.age >= attraction.getMinimumAge()
                && this.height >= attraction.getMinimumHeight();
    }
}