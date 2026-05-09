package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.enums.UserRole;

import java.util.Objects;

public abstract class User {
    public String id;
    public String name;
    public String document;
    public String email;
    public String password;
    public UserRole userRole;

    public User(String id, String name, String document, String email, String password, UserRole userRole) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
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

    public String getDocument() {
        return document;
    }
    public void setDocument(String document) {
        this.document = document;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }
    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(document, user.document) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && userRole == user.userRole;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, document, email, password, userRole);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", document='" + document + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userRole=" + userRole +
                '}';
    }

    public abstract boolean login(String email, String password);

    public void logout() {
        System.out.println("User " + name + " has logged out.");
    }
}