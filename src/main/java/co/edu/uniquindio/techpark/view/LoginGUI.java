package co.edu.uniquindio.techpark.view;

import co.edu.uniquindio.techpark.model.entities.*;
import co.edu.uniquindio.techpark.service.EmailService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.concurrent.atomic.AtomicReference;

public class LoginGUI extends JFrame {

    private static final Color C_BG = new Color(10, 14, 20);
    private static final Color C_CARD = new Color(18, 24, 32);
    private static final Color C_BORDER = new Color(40, 50, 64);
    private static final Color C_PRIMARY = new Color(56, 130, 255);
    private static final Color C_PRIMARY_H = new Color(84, 152, 255);
    private static final Color C_TEXT = new Color(224, 232, 244);
    private static final Color C_TEXT2 = new Color(120, 134, 154);
    private static final Color C_FIELD_BG = new Color(10, 14, 20);
    private static final Color C_ERROR = new Color(240, 80, 80);
    private static final Color C_SUCCESS = new Color(52, 199, 100);

    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font F_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_FIELD = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font F_BTN = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_LINK = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SUB = new Font("Segoe UI", Font.PLAIN, 13);

    private CardLayout cardLayout;
    private JPanel cardContainer;
    private final Park park;

    public LoginGUI(Park park) {
        this.park = park;
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
    }

    private void buildUI() {
        cardLayout    = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);
        cardContainer.add(buildLoginPanel(), "LOGIN");
        cardContainer.add(buildRegisterPanel(), "REGISTER");
        cardContainer.add(buildForgotPanel(), "FORGOT");

        JPanel bg = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(C_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setOpaque(false);
        bg.add(cardContainer);
        setContentPane(bg);
    }

    // ================================================================
    // LOGIN
    // ================================================================
    private JPanel buildLoginPanel() {
        JPanel card = card(400, 490);
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
        JPasswordField passField = passField();
        g.gridy = 5; g.insets = ins(0, 36, 8, 36);
        card.add(passField, g);

        JButton btnForgot = linkBtn("Forgot my password");
        g.gridy = 6; g.insets = ins(0, 36, 18, 36);
        card.add(wrapBtn(btnForgot, FlowLayout.RIGHT), g);

        JLabel errLabel = errLabel();
        g.gridy = 7; g.insets = ins(0, 36, 8, 36);
        card.add(errLabel, g);

        JButton btnSignIn = primaryBtn("Sign in");
        g.gridy = 8; g.insets = ins(0, 36, 20, 36);
        card.add(btnSignIn, g);

        g.gridy = 9; g.insets = ins(0, 36, 16, 36);
        card.add(sep(), g);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        row.setOpaque(false);
        JLabel noAcc = new JLabel("Don't have an account?");
        noAcc.setFont(F_LINK); noAcc.setForeground(C_TEXT2);
        JButton btnReg = linkBtn("Sign up");
        row.add(noAcc); row.add(btnReg);
        g.gridy = 10; g.insets = ins(0, 36, 28, 36);
        card.add(row, g);

        btnSignIn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass  = new String(passField.getPassword()).trim();
            if (email.isEmpty() || pass.isEmpty()) {
                err(errLabel, "Please fill in all fields."); return;
            }
            User user = UserStore.getInstance().authenticate(email, pass);
            if (user == null) {
                err(errLabel, "Incorrect email or password."); return;
            }
            errLabel.setText("");
            openMain(user);
        });

        btnForgot.addActionListener(e -> cardLayout.show(cardContainer, "FORGOT"));
        btnReg.addActionListener(e -> cardLayout.show(cardContainer, "REGISTER"));
        return card;
    }

    // ================================================================
    // REGISTER
    // ================================================================
    private JPanel buildRegisterPanel() {
        JPanel card = card(400, 640);
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        g.gridy = 0; g.insets = ins(28, 36, 20, 36);
        card.add(centeredLabel("Create account", F_TITLE, C_TEXT), g);

        g.gridy = 1; g.insets = ins(0, 36, 4, 36); card.add(label("Full name"), g);
        JTextField nameField = textField("John Doe");
        g.gridy = 2; g.insets = ins(0, 36, 10, 36); card.add(nameField, g);

        g.gridy = 3; g.insets = ins(0, 36, 4, 36); card.add(label("Document number"), g);
        JTextField docField  = textField("1234567890");
        g.gridy = 4; g.insets = ins(0, 36, 10, 36); card.add(docField, g);

        g.gridy = 5; g.insets = ins(0, 36, 4, 36); card.add(label("Email address"), g);
        JTextField emailField = textField("you@example.com");
        g.gridy = 6; g.insets = ins(0, 36, 10, 36); card.add(emailField, g);

        g.gridy = 7; g.insets = ins(0, 36, 4, 36); card.add(label("Password"), g);
        JPasswordField passField = passField();
        g.gridy = 8; g.insets = ins(0, 36, 10, 36); card.add(passField, g);

        g.gridy = 9; g.insets = ins(0, 36, 4, 36); card.add(label("User role"), g);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"VISITOR","OPERATOR","ADMIN"});
        styleCombo(roleCombo);
        g.gridy = 10; g.insets = ins(0, 36, 10, 36); card.add(roleCombo, g);

        // visitor-only fields
        JPanel visitorFields = new JPanel(new GridLayout(1, 2, 12, 0));
        visitorFields.setOpaque(false);
        JPanel ageCol = col("Age", "18");
        JPanel htCol  = col("Height (cm)", "170");
        JTextField ageField    = (JTextField) ((BorderLayout) ageCol.getLayout() == null
                ? null : ageCol.getComponent(1));
        JTextField heightField = (JTextField) ((BorderLayout) htCol.getLayout() == null
                ? null : htCol.getComponent(1));

        // rebuild cols properly
        visitorFields.removeAll();
        JPanel agePanel = new JPanel(new BorderLayout(0, 4)); agePanel.setOpaque(false);
        agePanel.add(label("Age"), BorderLayout.NORTH);
        JTextField ageTF = textField("18"); agePanel.add(ageTF, BorderLayout.CENTER);

        JPanel htPanel = new JPanel(new BorderLayout(0, 4)); htPanel.setOpaque(false);
        htPanel.add(label("Height (cm)"), BorderLayout.NORTH);
        JTextField htTF = textField("170"); htPanel.add(htTF, BorderLayout.CENTER);

        visitorFields.add(agePanel); visitorFields.add(htPanel);
        g.gridy = 11; g.insets = ins(0, 36, 10, 36); card.add(visitorFields, g);

        JLabel errLabel = errLabel();
        g.gridy = 12; g.insets = ins(0, 36, 8, 36); card.add(errLabel, g);

        JButton btnCreate = primaryBtn("Create account");
        g.gridy = 13; g.insets = ins(0, 36, 14, 36); card.add(btnCreate, g);

        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        backRow.setOpaque(false);
        JLabel hasAcc = new JLabel("Already have an account?");
        hasAcc.setFont(F_LINK); hasAcc.setForeground(C_TEXT2);
        JButton btnBack = linkBtn("Sign in");
        backRow.add(hasAcc); backRow.add(btnBack);
        g.gridy = 14; g.insets = ins(0, 36, 24, 36); card.add(backRow, g);

        roleCombo.addActionListener(e -> {
            visitorFields.setVisible("VISITOR".equals(roleCombo.getSelectedItem()));
            card.revalidate(); card.repaint();
        });

        btnCreate.addActionListener(e -> {
            // read all values inside lambda - avoids any capture issue
            final String name = nameField.getText().trim();
            final String document = docField.getText().trim();
            final String email = emailField.getText().trim();
            final String password = new String(passField.getPassword()).trim();
            final String role = (String) roleCombo.getSelectedItem();

            if (name.isEmpty() || document.isEmpty() || email.isEmpty() || password.isEmpty()) {
                err(errLabel, "Please fill in all required fields."); return;
            }
            // FIX: robust email check that does not rely on placeholder text
            if (!validEmail(email)) {
                err(errLabel, "Please enter a valid email address."); return;
            }
            if (password.length() < 6) {
                err(errLabel, "Password must be at least 6 characters."); return;
            }

            UserStore store = UserStore.getInstance();

            // duplicate checks
            if (store.emailExists(email)) {
                err(errLabel, "Email address is already registered."); return;
            }
            if (store.documentExists(document)) {
                err(errLabel, "Document number is already registered."); return;
            }

            final String id = "USR-" + System.currentTimeMillis();
            User newUser;

            switch (role) {
                case "VISITOR" -> {
                    final String ageStr = ageTF.getText().trim();
                    final String htStr  = htTF.getText().trim();
                    if (ageStr.isEmpty() || htStr.isEmpty()) {
                        err(errLabel, "Please enter age and height."); return;
                    }
                    int   age;
                    float height;
                    try {
                        age    = Integer.parseInt(ageStr);
                        height = Float.parseFloat(htStr);
                        if (age <= 0 || height <= 0) throw new NumberFormatException();
                    } catch (NumberFormatException ex) {
                        err(errLabel, "Age and height must be valid positive numbers."); return;
                    }
                    // KEY FIX: do NOT call park.registerVisitor() here.
                    // Visitors are stored in UserStore only.
                    // Park registration happens when they buy a ticket and enter.
                    newUser = new Visitor(id, name, document, email, password, age, height);
                }
                case "OPERATOR" -> newUser = new Operator(id, name, document, email, password);
                default -> newUser = new Administrator(id, name, document, email, password);
            }

            if (!store.register(newUser)) {
                err(errLabel, "Registration failed. Please try again."); return;
            }

            // welcome email (non-blocking)
            final User registered = newUser;
            new Thread(() -> EmailService.getInstance()
                    .sendWelcomeEmail(registered.getEmail(), registered.getName())
            ).start();

            errLabel.setForeground(C_SUCCESS);
            errLabel.setText("Account created! Sign in to continue.");
            clearFields(nameField, docField, emailField, ageTF, htTF);
            passField.setText("");
            roleCombo.setSelectedIndex(0);

            Timer t = new Timer(1800, ev -> {
                cardLayout.show(cardContainer, "LOGIN");
                errLabel.setText("");
            });
            t.setRepeats(false); t.start();
        });

        btnBack.addActionListener(e -> cardLayout.show(cardContainer, "LOGIN"));
        return card;
    }

    // ================================================================
    // FORGOT PASSWORD - real email verification
    // ================================================================
    private JPanel buildForgotPanel() {
        JPanel card = card(400, 380);
        card.setLayout(new GridBagLayout());
        GridBagConstraints g = gbc();

        g.gridy = 0; g.insets = ins(36, 36, 8, 36);
        card.add(centeredLabel("Reset password", F_TITLE, C_TEXT), g);

        JLabel desc = new JLabel(
                "<html><div style='text-align:center;'>Enter your email and we will send<br>" +
                        "you a 6-digit verification code.</div></html>", SwingConstants.CENTER);
        desc.setFont(F_SUB); desc.setForeground(C_TEXT2);
        g.gridy = 1; g.insets = ins(0, 36, 22, 36); card.add(desc, g);

        g.gridy = 2; g.insets = ins(0, 36, 4, 36);  card.add(label("Email address"), g);
        JTextField emailField = textField("you@example.com");
        g.gridy = 3; g.insets = ins(0, 36, 10, 36); card.add(emailField, g);

        JLabel msgLabel = errLabel();
        g.gridy = 4; g.insets = ins(0, 36, 10, 36); card.add(msgLabel, g);

        JButton btnSend = primaryBtn("Send verification code");
        g.gridy = 5; g.insets = ins(0, 36, 14, 36); card.add(btnSend, g);

        JButton btnBack = linkBtn("Back to sign in");
        g.gridy = 6; g.insets = ins(0, 36, 28, 36);
        card.add(wrapBtn(btnBack, FlowLayout.CENTER), g);

        AtomicReference<String> pendingCode = new AtomicReference<>(null);

        btnSend.addActionListener(e -> {
            final String email = emailField.getText().trim();

            if (!validEmail(email)) {
                err(msgLabel, "Please enter a valid email address."); return;
            }
            if (UserStore.getInstance().findByEmail(email) == null) {
                err(msgLabel, "No account found with that email address."); return;
            }

            String code = EmailService.getInstance().generateCode();
            pendingCode.set(code);

            if (!EmailService.getInstance().isConfigured()) {
                // dev mode: show code in console and dialog
                System.out.println("[DEV] Reset code for " + email + ": " + code);
                showCodeDialog(email, code, msgLabel);
                return;
            }

            btnSend.setEnabled(false);
            msgLabel.setForeground(C_TEXT2);
            msgLabel.setText("Sending code...");

            new Thread(() -> {
                boolean sent = EmailService.getInstance().sendVerificationCode(email, code);
                SwingUtilities.invokeLater(() -> {
                    btnSend.setEnabled(true);
                    if (sent) {
                        msgLabel.setForeground(C_SUCCESS);
                        msgLabel.setText("Code sent! Check your inbox.");
                        Timer t = new Timer(600, ev -> showCodeDialog(email, pendingCode.get(), msgLabel));
                        t.setRepeats(false); t.start();
                    } else {
                        err(msgLabel, "Could not send email. Check EmailService configuration.");
                    }
                });
            }).start();
        });

        btnBack.addActionListener(e -> {
            pendingCode.set(null);
            emailField.setText("");
            msgLabel.setText("");
            cardLayout.show(cardContainer, "LOGIN");
        });

        return card;
    }

    private void showCodeDialog(String email, String correct, JLabel msgLabel) {
        String entered = JOptionPane.showInputDialog(
                this,
                "Enter the 6-digit code sent to:\n" + email,
                "Verification code",
                JOptionPane.PLAIN_MESSAGE
        );
        if (entered == null) return;
        if (!entered.trim().equals(correct)) {
            err(msgLabel, "Incorrect code. Please try again."); return;
        }
        showNewPasswordDialog(email, msgLabel);
    }

    private void showNewPasswordDialog(String email, JLabel msgLabel) {
        JPasswordField f1 = new JPasswordField(20);
        JPasswordField f2 = new JPasswordField(20);
        JPanel panel = new JPanel(new GridLayout(4, 1, 4, 4));
        panel.add(new JLabel("New password (min 6 characters):"));
        panel.add(f1);
        panel.add(new JLabel("Confirm new password:"));
        panel.add(f2);

        int res = JOptionPane.showConfirmDialog(this, panel,
                "Set new password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String p1 = new String(f1.getPassword()).trim();
        String p2 = new String(f2.getPassword()).trim();

        if (p1.length() < 6) { err(msgLabel, "Password must be at least 6 characters."); return; }
        if (!p1.equals(p2)) { err(msgLabel, "Passwords do not match."); return; }

        UserStore.getInstance().updatePassword(email, p1);
        msgLabel.setForeground(C_SUCCESS);
        msgLabel.setText("Password updated! Please sign in.");
        Timer t = new Timer(2000, ev -> cardLayout.show(cardContainer, "LOGIN"));
        t.setRepeats(false); t.start();
    }

    // ================================================================
    // navigation
    // ================================================================
    private void openMain(User user) {
        dispose();
        new MainGUI(park, user, UserStore.getInstance());
    }

    // ================================================================
    // validation
    // ================================================================
    private boolean validEmail(String email) {
        if (email == null || email.isBlank()) return false;
        int at = email.indexOf('@');
        if (at < 1) return false;
        String domain = email.substring(at + 1);
        int dot = domain.lastIndexOf('.');
        return dot > 0 && dot < domain.length() - 1;
    }

    // ================================================================
    // components
    // ================================================================
    private JPanel card(int w, int h) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),18,18));
                g2.setColor(C_BORDER); g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(.5f,.5f,getWidth()-1,getHeight()-1,18,18));
                g2.dispose();
            }
        };
        p.setOpaque(false); p.setPreferredSize(new Dimension(w, h)); return p;
    }

    private JTextField textField(String hint) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_FIELD_BG);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.dispose(); super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setColor(C_TEXT2); g3.setFont(F_FIELD);
                    FontMetrics fm = g3.getFontMetrics();
                    g3.drawString(hint, 12, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                    g3.dispose();
                }
            }
        };
        f.setOpaque(false); f.setFont(F_FIELD); f.setForeground(C_TEXT);
        f.setCaretColor(C_PRIMARY); f.setBorder(rBorder(C_BORDER));
        f.setPreferredSize(new Dimension(0, 42));
        f.addFocusListener(focusHL(f)); return f;
    }

    private JPasswordField passField() {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_FIELD_BG);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.dispose(); super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setColor(C_TEXT2); g3.setFont(F_FIELD.deriveFont(Font.PLAIN));
                    FontMetrics fm = g3.getFontMetrics();
                    g3.drawString("At least 6 characters", 12,
                            (getHeight()+fm.getAscent()-fm.getDescent())/2);
                    g3.dispose();
                }
            }
        };
        f.setOpaque(false); f.setFont(F_FIELD); f.setForeground(C_TEXT);
        f.setCaretColor(C_PRIMARY); f.setEchoChar('\u25CF');
        f.setBorder(rBorder(C_BORDER)); f.setPreferredSize(new Dimension(0, 42));
        f.addFocusListener(focusHL(f)); return f;
    }

    private void styleCombo(JComboBox<String> c) {
        c.setFont(F_FIELD); c.setBackground(C_FIELD_BG); c.setForeground(C_TEXT);
        c.setPreferredSize(new Dimension(0, 42)); c.setBorder(rBorder(C_BORDER));
    }

    private JButton primaryBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = !isEnabled() ? C_BORDER
                        : getModel().isPressed()  ? C_PRIMARY.darker()
                          : getModel().isRollover() ? C_PRIMARY_H : C_PRIMARY;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),10,10));
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN); btn.setForeground(Color.WHITE); btn.setOpaque(false);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 44)); return btn;
    }

    private JButton linkBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(F_LINK); btn.setForeground(C_PRIMARY); btn.setOpaque(false);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(C_PRIMARY_H); }
            @Override public void mouseExited(MouseEvent  e) { btn.setForeground(C_PRIMARY); }
        }); return btn;
    }

    private JLabel label(String t) { JLabel l=new JLabel(t); l.setFont(F_LABEL); l.setForeground(C_TEXT2); return l; }
    private JLabel centeredLabel(String t, Font f, Color c) { JLabel l=new JLabel(t,SwingConstants.CENTER); l.setFont(f); l.setForeground(c); return l; }
    private JLabel errLabel() { JLabel l=new JLabel(" ",SwingConstants.CENTER); l.setFont(F_LABEL); l.setForeground(C_ERROR); return l; }
    private JSeparator sep() { JSeparator s=new JSeparator(); s.setForeground(C_BORDER); s.setBackground(C_BORDER); return s; }
    private JPanel col(String lbl, String hint) { JPanel p=new JPanel(new BorderLayout(0,4)); p.setOpaque(false); p.add(label(lbl),BorderLayout.NORTH); p.add(textField(hint),BorderLayout.CENTER); return p; }

    private GridBagConstraints gbc() { GridBagConstraints g=new GridBagConstraints(); g.gridx=0; g.fill=GridBagConstraints.HORIZONTAL; return g; }
    private Insets ins(int t,int l,int b,int r) { return new Insets(t,l,b,r); }
    private JPanel wrapBtn(JButton btn, int align) { JPanel p=new JPanel(new FlowLayout(align,0,0)); p.setOpaque(false); p.add(btn); return p; }
    private Border rBorder(Color c) { return BorderFactory.createCompoundBorder(new RndBorder(8,c),BorderFactory.createEmptyBorder(4,12,4,12)); }
    private FocusAdapter focusHL(JComponent c) { return new FocusAdapter() { @Override public void focusGained(FocusEvent e){c.setBorder(rBorder(C_PRIMARY));} @Override public void focusLost(FocusEvent e){c.setBorder(rBorder(C_BORDER));} }; }
    private void err(JLabel l, String msg) { l.setForeground(C_ERROR); l.setText(msg); }
    private void clearFields(JTextField... fs) { for(JTextField f:fs) f.setText(""); }

    private static class RndBorder extends AbstractBorder {
        final int r; final Color c;
        RndBorder(int r,Color c){this.r=r;this.c=c;}
        @Override public void paintBorder(Component comp,Graphics g,int x,int y,int w,int h){
            Graphics2D g2=(Graphics2D)g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c); g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Float(x+.5f,y+.5f,w-1,h-1,r,r)); g2.dispose();
        }
    }
}