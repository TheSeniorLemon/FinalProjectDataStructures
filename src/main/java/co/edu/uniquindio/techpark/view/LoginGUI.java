package co.edu.uniquindio.techpark.view;

import co.edu.uniquindio.techpark.model.entities.*;
import co.edu.uniquindio.techpark.model.structures.LinkedList;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LoginGUI extends JFrame {

    // ----------------------------------------------------------------
    // color theme
    // ----------------------------------------------------------------
    private static final Color C_BACKGROUND  = new Color(10, 14, 20);
    private static final Color C_CARD        = new Color(18, 24, 32);
    private static final Color C_BORDER      = new Color(40, 50, 64);
    private static final Color C_PRIMARY     = new Color(56, 130, 255);
    private static final Color C_PRIMARY_H   = new Color(84, 152, 255);
    private static final Color C_TEXT        = new Color(224, 232, 244);
    private static final Color C_TEXT2       = new Color(120, 134, 154);
    private static final Color C_FIELD_BG    = new Color(10, 14, 20);
    private static final Color C_ERROR       = new Color(240, 80, 80);
    private static final Color C_SUCCESS     = new Color(52, 199, 100);

    private static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font F_LABEL  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_FIELD  = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font F_BTN    = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_LINK   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SUB    = new Font("Segoe UI", Font.PLAIN, 13);

    private CardLayout cardLayout;
    private JPanel cardContainer;
    private final Park park;
    private LinkedList<Administrator> admins;

    public LoginGUI(Park park) {
        this.park = park;
        this.admins = new LinkedList<>();
        setupWindow();
        buildUI();
        setVisible(true);
    }

    // ----------------------------------------------------------------
    // window setup
    // ----------------------------------------------------------------
    private void setupWindow() {
        setTitle("Tech-Park UQ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 580);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(C_BACKGROUND);
    }

    private void buildUI() {
        cardLayout    = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);

        cardContainer.add(buildLoginPanel(), "LOGIN");
        cardContainer.add(buildRegisterPanel(), "REGISTER");
        cardContainer.add(buildForgotPanel(), "FORGOT");

        JPanel background = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_BACKGROUND);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        background.setOpaque(false);
        background.add(cardContainer);
        setContentPane(background);
    }

    // ================================================================
    // LOGIN PANEL
    // ================================================================
    private JPanel buildLoginPanel() {
        JPanel card = createCard(400, 490);
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        // header
        g.gridy = 0; g.insets = ins(32, 36, 4, 36);
        card.add(centeredLabel("TECH-PARK UQ", F_TITLE, C_PRIMARY), g);

        g.gridy = 1; g.insets = ins(0, 36, 28, 36);
        card.add(centeredLabel("Park management system", F_SUB, C_TEXT2), g);

        // email
        g.gridy = 2; g.insets = ins(0, 36, 4, 36);
        card.add(label("Email address"), g);

        JTextField emailField = textField("you@example.com");
        g.gridy = 3; g.insets = ins(0, 36, 14, 36);
        card.add(emailField, g);

        // password
        g.gridy = 4; g.insets = ins(0, 36, 4, 36);
        card.add(label("Password"), g);

        JPasswordField passField = passwordField();
        g.gridy = 5; g.insets = ins(0, 36, 8, 36);
        card.add(passField, g);

        // forgot password link
        JButton btnForgot = linkButton("Forgot my password");
        g.gridy = 6; g.insets = ins(0, 36, 18, 36);
        card.add(wrap(btnForgot, FlowLayout.RIGHT), g);

        // error label
        JLabel errorLabel = errorLabel();
        g.gridy = 7; g.insets = ins(0, 36, 8, 36);
        card.add(errorLabel, g);

        // sign in button
        JButton btnSignIn = primaryButton("Sign in");
        g.gridy = 8; g.insets = ins(0, 36, 20, 36);
        card.add(btnSignIn, g);

        // separator
        g.gridy = 9; g.insets = ins(0, 36, 16, 36);
        card.add(separator(), g);

        // register link
        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        registerRow.setOpaque(false);
        JLabel noAccount = new JLabel("Don't have an account?");
        noAccount.setFont(F_LINK); noAccount.setForeground(C_TEXT2);
        JButton btnGoRegister = linkButton("Sign up");
        registerRow.add(noAccount); registerRow.add(btnGoRegister);
        g.gridy = 10; g.insets = ins(0, 36, 28, 36);
        card.add(registerRow, g);

        // actions
        btnSignIn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            if (email.isEmpty() || password.isEmpty()) {
                showError(errorLabel, "Please fill in all fields.");
                return;
            }
            User user = authenticate(email, password);
            if (user == null) {
                showError(errorLabel, "Incorrect email or password.");
                return;
            }
            errorLabel.setText("");
            openMainPanel(user);
        });

        btnForgot.addActionListener(e     -> cardLayout.show(cardContainer, "FORGOT"));
        btnGoRegister.addActionListener(e -> cardLayout.show(cardContainer, "REGISTER"));

        return card;
    }

    // ================================================================
    // REGISTER PANEL
    // ================================================================
    private JPanel buildRegisterPanel() {
        JPanel card = createCard(400, 620);
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        g.gridy = 0; g.insets = ins(28, 36, 20, 36);
        card.add(centeredLabel("Create account", F_TITLE, C_TEXT), g);

        // full name
        g.gridy = 1; g.insets = ins(0, 36, 4, 36);
        card.add(label("Full name"), g);
        JTextField nameField = textField("John Doe");
        g.gridy = 2; g.insets = ins(0, 36, 10, 36);
        card.add(nameField, g);

        // document number
        g.gridy = 3; g.insets = ins(0, 36, 4, 36);
        card.add(label("Document number"), g);
        JTextField docField = textField("1234567890");
        g.gridy = 4; g.insets = ins(0, 36, 10, 36);
        card.add(docField, g);

        // email
        g.gridy = 5; g.insets = ins(0, 36, 4, 36);
        card.add(label("Email address"), g);
        JTextField emailField = textField("you@example.com");
        g.gridy = 6; g.insets = ins(0, 36, 10, 36);
        card.add(emailField, g);

        // password
        g.gridy = 7; g.insets = ins(0, 36, 4, 36);
        card.add(label("Password"), g);
        JPasswordField passField = passwordField();
        g.gridy = 8; g.insets = ins(0, 36, 10, 36);
        card.add(passField, g);

        // user role
        g.gridy = 9; g.insets = ins(0, 36, 4, 36);
        card.add(label("User role"), g);
        JComboBox<String> roleCombo = new JComboBox<>(
                new String[]{"VISITOR", "OPERATOR", "ADMIN"}
        );
        styleCombo(roleCombo);
        g.gridy = 10; g.insets = ins(0, 36, 10, 36);
        card.add(roleCombo, g);

        // visitor-only fields (age + height)
        JPanel visitorFields = new JPanel(new GridLayout(1, 2, 12, 0));
        visitorFields.setOpaque(false);

        JPanel ageCol = new JPanel(new BorderLayout(0, 4));
        ageCol.setOpaque(false);
        ageCol.add(label("Age"), BorderLayout.NORTH);
        JTextField ageField = textField("18");
        ageCol.add(ageField, BorderLayout.CENTER);

        JPanel heightCol = new JPanel(new BorderLayout(0, 4));
        heightCol.setOpaque(false);
        heightCol.add(label("Height (cm)"), BorderLayout.NORTH);
        JTextField heightField = textField("170");
        heightCol.add(heightField, BorderLayout.CENTER);

        visitorFields.add(ageCol);
        visitorFields.add(heightCol);

        g.gridy = 11; g.insets = ins(0, 36, 10, 36);
        card.add(visitorFields, g);

        // error label
        JLabel errorLabel = errorLabel();
        g.gridy = 12; g.insets = ins(0, 36, 8, 36);
        card.add(errorLabel, g);

        // create account button
        JButton btnCreate = primaryButton("Create account");
        g.gridy = 13; g.insets = ins(0, 36, 14, 36);
        card.add(btnCreate, g);

        // back to login link
        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        loginRow.setOpaque(false);
        JLabel hasAccount = new JLabel("Already have an account?");
        hasAccount.setFont(F_LINK); hasAccount.setForeground(C_TEXT2);
        JButton btnBack = linkButton("Sign in");
        loginRow.add(hasAccount); loginRow.add(btnBack);
        g.gridy = 14; g.insets = ins(0, 36, 24, 36);
        card.add(loginRow, g);

        // show or hide visitor fields based on role
        roleCombo.addActionListener(e -> {
            visitorFields.setVisible(roleCombo.getSelectedItem().equals("VISITOR"));
            card.revalidate();
            card.repaint();
        });

        btnCreate.addActionListener(e -> {
            String name = nameField.getText().trim();
            String document = docField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String role = (String) roleCombo.getSelectedItem();

            if (name.isEmpty() || document.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError(errorLabel, "Please fill in all required fields.");
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                showError(errorLabel, "Please enter a valid email address.");
                return;
            }
            if (password.length() < 6) {
                showError(errorLabel, "Password must be at least 6 characters.");
                return;
            }

            String id  = "USR-" + System.currentTimeMillis();
            boolean ok = false;

            switch (role) {
                case "VISITOR": {
                    String ageStr = ageField.getText().trim();
                    String heightStr = heightField.getText().trim();
                    if (ageStr.isEmpty() || heightStr.isEmpty()) {
                        showError(errorLabel, "Please enter age and height.");
                        return;
                    }
                    try {
                        int   age    = Integer.parseInt(ageStr);
                        float height = Float.parseFloat(heightStr);
                        if (age <= 0 || height <= 0) throw new NumberFormatException();
                        Visitor visitor = new Visitor(id, name, document, email, password, age, height);
                        park.registerVisitor(visitor);
                        ok = true;
                    } catch (NumberFormatException ex) {
                        showError(errorLabel, "Age and height must be valid numbers.");
                        return;
                    }
                    break;
                }
                case "OPERATOR": {
                    Operator operator = new Operator(id, name, document, email, password);
                    park.registerOperator(operator);
                    ok = true;
                    break;
                }
                case "ADMIN": {
                    Administrator admin = new Administrator(id, name, document, email, password);
                    admins.add(admin);
                    ok = true;
                    break;
                }
            }

            if (ok) {
                errorLabel.setForeground(C_SUCCESS);
                errorLabel.setText("Account created. Please sign in.");
                clearFields(nameField, docField, emailField, ageField, heightField);
                passField.setText("");
                Timer t = new Timer(1800, ev -> {
                    cardLayout.show(cardContainer, "LOGIN");
                    errorLabel.setText("");
                });
                t.setRepeats(false);
                t.start();
            }
        });

        btnBack.addActionListener(e -> cardLayout.show(cardContainer, "LOGIN"));

        return card;
    }

    // ================================================================
    // FORGOT PASSWORD PANEL
    // ================================================================
    private JPanel buildForgotPanel() {
        JPanel card = createCard(400, 360);
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        g.gridy = 0; g.insets = ins(36, 36, 8, 36);
        card.add(centeredLabel("Reset password", F_TITLE, C_TEXT), g);

        JLabel description = new JLabel(
                "<html><div style='text-align:center;'>Enter your email and we will send you<br>" +
                        "a link to reset your password.</div></html>",
                SwingConstants.CENTER
        );
        description.setFont(F_SUB); description.setForeground(C_TEXT2);
        g.gridy = 1; g.insets = ins(0, 36, 24, 36);
        card.add(description, g);

        g.gridy = 2; g.insets = ins(0, 36, 4, 36);
        card.add(label("Email address"), g);

        JTextField emailField = textField("you@example.com");
        g.gridy = 3; g.insets = ins(0, 36, 10, 36);
        card.add(emailField, g);

        JLabel msgLabel = errorLabel();
        g.gridy = 4; g.insets = ins(0, 36, 10, 36);
        card.add(msgLabel, g);

        JButton btnSend = primaryButton("Send reset link");
        g.gridy = 5; g.insets = ins(0, 36, 16, 36);
        card.add(btnSend, g);

        JButton btnBack = linkButton("Back to sign in");
        g.gridy = 6; g.insets = ins(0, 36, 28, 36);
        card.add(wrap(btnBack, FlowLayout.CENTER), g);

        btnSend.addActionListener(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty() || !email.contains("@")) {
                showError(msgLabel, "Please enter a valid email address.");
                return;
            }
            if (findByEmail(email) == null) {
                showError(msgLabel, "No account found with that email address.");
                return;
            }
            msgLabel.setForeground(C_SUCCESS);
            msgLabel.setText("Reset link sent. Check your inbox.");
            btnSend.setEnabled(false);
            Timer t = new Timer(2500, ev -> {
                cardLayout.show(cardContainer, "LOGIN");
                emailField.setText("");
                msgLabel.setText("");
                btnSend.setEnabled(true);
            });
            t.setRepeats(false);
            t.start();
        });

        btnBack.addActionListener(e -> cardLayout.show(cardContainer, "LOGIN"));

        return card;
    }

    // ================================================================
    // authentication logic
    // ================================================================
    private User authenticate(String email, String password) {
        LinkedList<Visitor> visitors = park.getRegisteredVisitors();
        for (int i = 0; i < visitors.getSize(); i++) {
            Visitor v = visitors.get(i);
            if (v != null && v.login(email, password)) return v;
        }
        LinkedList<Operator> operators = park.getOperators();
        for (int i = 0; i < operators.getSize(); i++) {
            Operator op = operators.get(i);
            if (op != null && op.login(email, password)) return op;
        }
        for (int i = 0; i < admins.getSize(); i++) {
            Administrator adm = admins.get(i);
            if (adm != null && adm.login(email, password)) return adm;
        }
        return null;
    }

    private User findByEmail(String email) {
        LinkedList<Visitor> visitors = park.getRegisteredVisitors();
        for (int i = 0; i < visitors.getSize(); i++) {
            Visitor v = visitors.get(i);
            if (v != null && v.getEmail().equalsIgnoreCase(email)) return v;
        }
        LinkedList<Operator> operators = park.getOperators();
        for (int i = 0; i < operators.getSize(); i++) {
            Operator op = operators.get(i);
            if (op != null && op.getEmail().equalsIgnoreCase(email)) return op;
        }
        for (int i = 0; i < admins.getSize(); i++) {
            Administrator adm = admins.get(i);
            if (adm != null && adm.getEmail().equalsIgnoreCase(email)) return adm;
        }
        return null;
    }

    private void openMainPanel(User user) {
        // uncomment once MainGUI is ready:
        // MainGUI main = new MainGUI(park, user, admins);
        // main.setVisible(true);
        // dispose();
        JOptionPane.showMessageDialog(
                this,
                "Welcome, " + user.getName() + "\nRole: " + user.getUserRole(),
                "Access granted",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ================================================================
    // custom components
    // ================================================================
    private JPanel createCard(int width, int height) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 18, 18));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(width, height));
        return p;
    }

    private JTextField textField(String hint) {
        JTextField field = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_FIELD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setColor(C_TEXT2);
                    g3.setFont(F_FIELD);
                    FontMetrics fm = g3.getFontMetrics();
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g3.drawString(hint, 12, y);
                    g3.dispose();
                }
            }
        };
        field.setOpaque(false);
        field.setFont(F_FIELD);
        field.setForeground(C_TEXT);
        field.setCaretColor(C_PRIMARY);
        field.setBorder(roundedBorder(C_BORDER));
        field.setPreferredSize(new Dimension(0, 42));
        field.addFocusListener(focusHighlight(field));
        return field;
    }

    private JPasswordField passwordField() {
        JPasswordField field = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_FIELD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setColor(C_TEXT2);
                    g3.setFont(F_FIELD.deriveFont(Font.PLAIN));
                    FontMetrics fm = g3.getFontMetrics();
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g3.drawString("At least 6 characters", 12, y);
                    g3.dispose();
                }
            }
        };
        field.setOpaque(false);
        field.setFont(F_FIELD);
        field.setForeground(C_TEXT);
        field.setCaretColor(C_PRIMARY);
        field.setEchoChar('\u25CF');
        field.setBorder(roundedBorder(C_BORDER));
        field.setPreferredSize(new Dimension(0, 42));
        field.addFocusListener(focusHighlight(field));
        return field;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(F_FIELD);
        combo.setBackground(C_FIELD_BG);
        combo.setForeground(C_TEXT);
        combo.setPreferredSize(new Dimension(0, 42));
        combo.setBorder(roundedBorder(C_BORDER));
    }

    private JButton primaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = !isEnabled()            ? C_BORDER
                        : getModel().isPressed()  ? C_PRIMARY.darker()
                          : getModel().isRollover() ? C_PRIMARY_H
                            : C_PRIMARY;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 44));
        return btn;
    }

    private JButton linkButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(F_LINK);
        btn.setForeground(C_PRIMARY);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(C_PRIMARY_H); }
            @Override public void mouseExited(MouseEvent  e) { btn.setForeground(C_PRIMARY); }
        });
        return btn;
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_LABEL); lbl.setForeground(C_TEXT2);
        return lbl;
    }

    private JLabel centeredLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(font); lbl.setForeground(color);
        return lbl;
    }

    private JLabel errorLabel() {
        JLabel lbl = new JLabel(" ", SwingConstants.CENTER);
        lbl.setFont(F_LABEL); lbl.setForeground(C_ERROR);
        return lbl;
    }

    private JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(C_BORDER); sep.setBackground(C_BORDER);
        return sep;
    }

    // ----------------------------------------------------------------
    // layout and border helpers
    // ----------------------------------------------------------------
    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL;
        return g;
    }

    private Insets ins(int top, int left, int bottom, int right) {
        return new Insets(top, left, bottom, right);
    }

    private JPanel wrap(JButton btn, int alignment) {
        JPanel p = new JPanel(new FlowLayout(alignment, 0, 0));
        p.setOpaque(false); p.add(btn);
        return p;
    }

    private Border roundedBorder(Color color) {
        return BorderFactory.createCompoundBorder(
                new RoundedBorder(8, color),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        );
    }

    private FocusAdapter focusHighlight(JComponent field) {
        return new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { field.setBorder(roundedBorder(C_PRIMARY)); }
            @Override public void focusLost(FocusEvent   e) { field.setBorder(roundedBorder(C_BORDER)); }
        };
    }

    private void showError(JLabel lbl, String message) {
        lbl.setForeground(C_ERROR); lbl.setText(message);
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }

    // ================================================================
    // custom rounded border
    // ================================================================
    private static class RoundedBorder extends AbstractBorder {
        private final int   radius;
        private final Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color  = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, w - 1, h - 1, radius, radius));
            g2.dispose();
        }
    }
}