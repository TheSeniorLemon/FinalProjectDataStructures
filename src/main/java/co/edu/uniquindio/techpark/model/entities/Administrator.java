package co.edu.uniquindio.techpark.model.entities;

public class Administrator {
    private String id, name, password;

    private Administrator(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.password = builder.password;
    }

    public static class Builder {
        private String id, name, password;

        public Builder(String id, String name, String password) {
            this.id = id;
            this.name = name;
            this.password = password;
        }

        public Administrator build() {
            return new Administrator(this);
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}