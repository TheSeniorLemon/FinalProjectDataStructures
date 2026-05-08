package co.edu.uniquindio.techpark.model.entities;

public class Visitor {
    private String name, id, pathImage;
    private int age;
    private double virtualBalance, height;

    private Visitor(Builder builder) {
        this.name = builder.name;
        this.id = builder.id;
        this.age = builder.age;
        this.height = builder.height;
        this.virtualBalance = builder.virtualBalance;
        this.pathImage = builder.pathImage;
    }

    public static class Builder {
        private String name, id, pathImage;
        private int age;
        private double virtualBalance, height;

        public Builder(String name, String id) {
            this.name = name;
            this.id = id;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder height(double height) {
            this.height = height;
            return this;
        }

        public Builder virtualBalance(double virtualBalance) {
            this.virtualBalance = virtualBalance;
            return this;
        }

        public Builder pathImage(String pathImage) {
            this.pathImage = pathImage;
            return this;
        }

        public Visitor build() {
            return new Visitor(this);
        }
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPathImage() { return pathImage; }
    public void setPathImage(String pathImage) { this.pathImage = pathImage; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public double getVirtualBalance() { return virtualBalance; }
    public void setVirtualBalance(double virtualBalance) { this.virtualBalance = virtualBalance; }
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
}