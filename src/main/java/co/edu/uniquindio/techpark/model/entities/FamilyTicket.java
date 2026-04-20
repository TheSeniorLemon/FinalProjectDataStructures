package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.TicketType;

public class FamilyTicket extends Ticket {
    private double discountPercentage;
    private int maxMembers;

    public FamilyTicket(String id, TicketType type, double price, String purchaseDate, double discountPercentage, int maxMembers) {
        super(id, type, price, purchaseDate);
        this.discountPercentage = discountPercentage;
        this.maxMembers = maxMembers;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }
    public void setDiscountPercentage(double discountPercentage) {
        if (discountPercentage >= 0 && discountPercentage <= 100) {
            this.discountPercentage = discountPercentage;
        } else {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100.");
        }
    }

    public int getMaxMembers() {
        return maxMembers;
    }
    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    @Override
    public String toString() {
        return "FamilyTicket{" +
                "discountPercentage=" + discountPercentage +
                ", maxMembers=" + maxMembers +
                ", id='" + id + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", purchaseDate=" + purchaseDate +
                ", active=" + active +
                '}';
    }

    @Override
    public int getPriority() {
        return 2;
    }

    public double applyDiscount(double originalPrice) {
        if (discountPercentage <= 0 || discountPercentage > 100) {
            return originalPrice;
        }
        double discount = originalPrice * (discountPercentage / 100.0);
        return originalPrice - discount;
    }
}