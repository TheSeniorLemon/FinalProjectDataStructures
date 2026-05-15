package co.edu.uniquindio.techpark.model.entities;

import co.edu.uniquindio.techpark.model.structures.LinkedList;

public class UserStore {
    private static final UserStore INSTANCE = new UserStore();

    private final LinkedList<Visitor> visitors = new LinkedList<>();
    private final LinkedList<Operator> operators = new LinkedList<>();
    private final LinkedList<Administrator> administrators = new LinkedList<>();

    private UserStore() {}

    public static UserStore getInstance() { return INSTANCE; }

    // ----------------------------------------------------------------
    // registration
    // ----------------------------------------------------------------
    public boolean register(User user) {
        if (user == null) return false;
        if (emailExists(user.getEmail())) return false;
        if (documentExists(user.getDocument())) return false;

        switch (user.getUserRole()) {
            case VISITOR -> visitors.add((Visitor) user);
            case OPERATOR -> operators.add((Operator) user);
            case ADMINISTRATOR -> administrators.add((Administrator) user);
        }
        DataManager.save(this);
        return true;
    }

    // ----------------------------------------------------------------
    // lookup
    // ----------------------------------------------------------------
    public User authenticate(String email, String password) {
        User u = findByEmail(email);
        if (u != null && u.getPassword().equals(password)) return u;
        return null;
    }

    public User findByEmail(String email) {
        if (email == null) return null;
        for (int i = 0; i < visitors.getSize(); i++) {
            Visitor v = visitors.get(i);
            if (v != null && v.getEmail().equalsIgnoreCase(email)) return v;
        }
        for (int i = 0; i < operators.getSize(); i++) {
            Operator op = operators.get(i);
            if (op != null && op.getEmail().equalsIgnoreCase(email)) return op;
        }
        for (int i = 0; i < administrators.getSize(); i++) {
            Administrator a = administrators.get(i);
            if (a != null && a.getEmail().equalsIgnoreCase(email)) return a;
        }
        return null;
    }

    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    public boolean documentExists(String document) {
        if (document == null) return false;
        for (int i = 0; i < visitors.getSize(); i++) {
            Visitor v = visitors.get(i);
            if (v != null && v.getDocument().equals(document)) return true;
        }
        for (int i = 0; i < operators.getSize(); i++) {
            Operator op = operators.get(i);
            if (op != null && op.getDocument().equals(document)) return true;
        }
        for (int i = 0; i < administrators.getSize(); i++) {
            Administrator a = administrators.get(i);
            if (a != null && a.getDocument().equals(document)) return true;
        }
        return false;
    }

    // ----------------------------------------------------------------
    // password reset
    // ----------------------------------------------------------------
    public boolean updatePassword(String email, String newPassword) {
        User user = findByEmail(email);
        if (user == null) return false;
        user.setPassword(newPassword);
        DataManager.save(this);
        return true;
    }

    // ----------------------------------------------------------------
    // getters
    // ----------------------------------------------------------------
    public LinkedList<Visitor> getVisitors() {
        return visitors;
    }

    public LinkedList<Operator> getOperators() {
        return operators;
    }

    public LinkedList<Administrator> getAdministrators() {
        return administrators;
    }

    public int getTotalUsers() {
        return visitors.getSize() + operators.getSize() + administrators.getSize();
    }
}