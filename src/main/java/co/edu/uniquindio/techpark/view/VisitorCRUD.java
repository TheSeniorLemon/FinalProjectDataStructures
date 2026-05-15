package co.edu.uniquindio.techpark.view;

import co.edu.uniquindio.techpark.model.entities.*;
import co.edu.uniquindio.techpark.model.structures.LinkedList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class VisitorCRUD extends JPanel {

    private static final Color C_BG = new Color(10, 14, 20);
    private static final Color C_CARD = new Color(18, 24, 32);
    private static final Color C_BORDER = new Color(40, 50, 64);
    private static final Color C_PRIMARY = new Color(56, 130, 255);
    private static final Color C_TEXT = new Color(224, 232, 244);
    private static final Color C_TEXT2 = new Color(120, 134, 154);
    private static final Color C_SUCCESS = new Color(52, 199, 100);
    private static final Color C_WARNING = new Color(255, 180, 50);
    private static final Color C_DANGER = new Color(240, 80, 80);
    private static final Color C_FIELD = new Color(10, 14, 20);

    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font F_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BTN = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font F_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_FIELD = new Font("Segoe UI", Font.PLAIN, 13);

    private static final String[] COLUMNS = {
            "ID", "Name", "Document", "Email", "Age",
            "Height", "Balance", "Ticket", "Visits", "In Park"
    };

    private final Park park;
    private final UserStore store;
    private DefaultTableModel tableModel;
    private JTable table;

    public VisitorCRUD(Park park, UserStore store) {
        this.park = park;
        this.store = store;
        setOpaque(false);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        // top bar
        JPanel topBar = new JPanel(new BorderLayout(16, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 24, 14, 24));

        JLabel title = new JLabel("Visitor Management");
        title.setFont(F_TITLE); title.setForeground(C_TEXT);
        topBar.add(title, BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        topRight.setOpaque(false);
        JTextField searchField = searchField();
        JButton btnAdd = actionBtn("+ Register Visitor", C_PRIMARY);
        topRight.add(searchField); topRight.add(btnAdd);
        topBar.add(topRight, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = buildTable();
        JScrollPane scroll = darkScroll(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        add(scroll, BorderLayout.CENTER);

        // bottom bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(12, 24, 20, 24));
        JButton btnView = actionBtn("View Details", C_PRIMARY);
        JButton btnTicket = actionBtn("Assign Ticket", new Color(80, 160, 100));
        JButton btnBalance = actionBtn("Add Balance", C_WARNING);
        JButton btnEnter = actionBtn("Enter Park", C_SUCCESS);
        JButton btnExit = actionBtn("Exit Park", C_TEXT2);
        JButton btnRefresh = actionBtn("Refresh", C_BORDER);
        bottomBar.add(btnView); bottomBar.add(btnTicket); bottomBar.add(btnBalance);
        bottomBar.add(btnEnter); bottomBar.add(btnExit); bottomBar.add(btnRefresh);
        add(bottomBar, BorderLayout.SOUTH);

        refreshTable(null);

        // search
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { refreshTable(searchField.getText().trim()); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { refreshTable(searchField.getText().trim()); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { refreshTable(searchField.getText().trim()); }
        });

        btnAdd.addActionListener(e -> showAddDialog());
        btnView.addActionListener(e -> { int r = sel(); if (r < 0) return; showDetailsDialog(r); });
        btnTicket.addActionListener(e -> { int r = sel(); if (r < 0) return; showTicketDialog(r); });
        btnBalance.addActionListener(e -> { int r = sel(); if (r < 0) return; showBalanceDialog(r); });
        btnEnter.addActionListener(e -> { int r = sel(); if (r < 0) return; enterPark(r); });
        btnExit.addActionListener(e -> { int r = sel(); if (r < 0) return; exitPark(r); });
        btnRefresh.addActionListener(e -> refreshTable(searchField.getText().trim()));
    }

    // ================================================================
    // table
    // ================================================================
    private void refreshTable(String filter) {
        tableModel.setRowCount(0);
        LinkedList<Visitor> visitors = store.getVisitors();
        int n = visitors.getSize();

        for (int i = 0; i < n; i++) {
            Visitor v = visitors.get(i);
            if (v == null) continue;
            if (filter != null && !filter.isEmpty()) {
                boolean match = v.getName().toLowerCase().contains(filter.toLowerCase())
                        || v.getEmail().toLowerCase().contains(filter.toLowerCase())
                        || v.getDocument().contains(filter);
                if (!match) continue;
            }
            boolean inPark = park.findVisitor(v.getId()) != null;
            tableModel.addRow(new Object[]{
                    v.getId(),
                    v.getName(),
                    v.getDocument(),
                    v.getEmail(),
                    v.getAge(),
                    String.format("%.2f m", v.getHeight()),
                    String.format("$%.0f", v.getVirtualBalance()),
                    v.getTicket() != null ? v.getTicket().getType().name() : "None",
                    v.getVisitHistory().getSize(),
                    inPark ? "Yes" : "No"
            });
        }
    }

    private Visitor getVisitorAt(int row) {
        String id = (String) tableModel.getValueAt(row, 0);
        LinkedList<Visitor> visitors = store.getVisitors();
        int n = visitors.getSize();
        for (int i = 0; i < n; i++) {
            Visitor v = visitors.get(i);
            if (v != null && v.getId().equals(id)) return v;
        }
        return null;
    }

    // ================================================================
    // ADD
    // ================================================================
    private void showAddDialog() {
        JDialog dialog = dialog("Register Visitor", 440, 460);
        JPanel  form   = formPanel();

        JTextField nameField = field("Full name");
        JTextField docField = field("Document number");
        JTextField emailField = field("email@example.com");
        JPasswordField passField = passField();
        JTextField ageField = field("18");
        JTextField htField = field("170");

        addRow(form, "Full name *", nameField);
        addRow(form, "Document *", docField);
        addRow(form, "Email *", emailField);
        addRow(form, "Password *", passField);
        addRow(form, "Age *", ageField);
        addRow(form, "Height (cm) *", htField);

        JLabel errLbl = errLabel();
        form.add(errLbl);
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        JButton btnSave = primaryBtn("Register visitor");
        form.add(btnSave);

        dialog.add(darkScroll(form));

        btnSave.addActionListener(e -> {
            String name = nameField.getText().trim();
            String doc = docField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            String ageS = ageField.getText().trim();
            String htS = htField.getText().trim();

            if (name.isEmpty() || doc.isEmpty() || email.isEmpty()
                    || pass.isEmpty() || ageS.isEmpty() || htS.isEmpty()) {
                err(errLbl, "All fields are required."); return;
            }
            if (!validEmail(email)) { err(errLbl, "Invalid email address."); return; }
            if (pass.length() < 6)  { err(errLbl, "Password must be at least 6 characters."); return; }
            if (store.emailExists(email))  { err(errLbl, "Email already registered."); return; }
            if (store.documentExists(doc)) { err(errLbl, "Document already registered."); return; }

            int   age;
            float height;
            try {
                age    = Integer.parseInt(ageS);
                height = Float.parseFloat(htS.replace(",", "."));
                if (age <= 0 || height <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                err(errLbl, "Age and height must be valid positive numbers."); return;
            }

            String  id      = "VIS-" + String.format("%03d", System.currentTimeMillis() % 10000);
            Visitor visitor = new Visitor(id, name, doc, email, pass, age, height);
            store.register(visitor);
            DataManager.save(store, park);
            refreshTable(null);
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    // ================================================================
    // VIEW DETAILS
    // ================================================================
    private void showDetailsDialog(int row) {
        Visitor v = getVisitorAt(row);
        if (v == null) return;

        JDialog dialog = dialog("Visitor Details - " + v.getName(), 500, 480);
        JPanel  root   = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // info card
        JPanel info = infoCard();
        addInfoRow(info, "ID", v.getId());
        addInfoRow(info, "Name", v.getName());
        addInfoRow(info, "Document", v.getDocument());
        addInfoRow(info, "Email", v.getEmail());
        addInfoRow(info, "Age", v.getAge() + " years");
        addInfoRow(info, "Height", String.format("%.2f m", v.getHeight()));
        addInfoRow(info, "Balance", String.format("$%.0f", v.getVirtualBalance()));
        addInfoRow(info, "Ticket", v.getTicket() != null ? v.getTicket().getType().name() : "None");
        addInfoRow(info, "Favorites", v.getFavoriteIds().size() + " attractions");
        addInfoRow(info, "Total visits", v.getVisitHistory().getSize() + " attractions");
        root.add(info);
        root.add(Box.createRigidArea(new Dimension(0, 16)));

        // visit history
        JLabel histTitle = sectionLabel("Visit History");
        root.add(histTitle);
        root.add(Box.createRigidArea(new Dimension(0, 8)));

        LinkedList<VisitHistory> history = v.getVisitHistory();
        int hn = history.getSize();
        if (hn == 0) {
            JLabel empty = new JLabel("No visits recorded yet.");
            empty.setFont(F_BODY); empty.setForeground(C_TEXT2);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            root.add(empty);
        } else {
            for (int i = hn - 1; i >= 0; i--) {
                VisitHistory vh = history.get(i);
                if (vh == null) continue;
                JPanel row2 = new JPanel(new BorderLayout());
                row2.setOpaque(false);
                row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
                JLabel left = new JLabel(vh.getAttractionName() + "  |  " + vh.getDateTime());
                left.setFont(F_SMALL); left.setForeground(C_TEXT2);
                JLabel right2 = new JLabel("$" + String.format("%.0f", vh.getIncurredCost()), SwingConstants.RIGHT);
                right2.setFont(F_SMALL); right2.setForeground(C_WARNING);
                row2.add(left, BorderLayout.WEST); row2.add(right2, BorderLayout.EAST);
                root.add(row2);
            }
        }

        dialog.add(darkScroll(root));
        dialog.setVisible(true);
    }

    // ================================================================
    // ASSIGN TICKET
    // ================================================================
    private void showTicketDialog(int row) {
        Visitor v = getVisitorAt(row);
        if (v == null) return;

        String[] options = {"GENERAL  - $25,000", "FAMILY   - $80,000", "FAST-PASS - $50,000"};
        int choice = JOptionPane.showOptionDialog(this,
                "Select ticket type for " + v.getName() + ":",
                "Assign Ticket", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice < 0) return;

        String date = java.time.LocalDate.now().toString();
        String id = "TK-" + System.currentTimeMillis();
        Ticket ticket = switch (choice) {
            case 0 -> new GeneralTicket(id, 25000.0, date);
            case 1 -> new FamilyTicket(id, 80000.0, date, 15.0, 4);
            default -> new FastPassTicket(id, 50000.0, date);
        };
        v.setTicket(ticket);
        DataManager.save(store, park);
        refreshTable(null);
        JOptionPane.showMessageDialog(this,
                "Ticket " + ticket.getType() + " assigned to " + v.getName() + ".",
                "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    // ================================================================
    // ADD BALANCE
    // ================================================================
    private void showBalanceDialog(int row) {
        Visitor v = getVisitorAt(row);
        if (v == null) return;

        String input = JOptionPane.showInputDialog(this,
                "Current balance: $" + String.format("%.0f", v.getVirtualBalance())
                        + "\nAmount to add ($):",
                "Add Balance", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;

        double amount;
        try {
            amount = Double.parseDouble(input.trim().replace(",", ""));
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            info("Enter a valid positive amount."); return;
        }

        v.addBalance(amount);
        DataManager.save(store, park);
        refreshTable(null);
        JOptionPane.showMessageDialog(this,
                "Added $" + String.format("%.0f", amount) + " to " + v.getName()
                        + ".\nNew balance: $" + String.format("%.0f", v.getVirtualBalance()),
                "Balance updated", JOptionPane.INFORMATION_MESSAGE);
    }

    // ================================================================
    // ENTER / EXIT PARK
    // ================================================================
    private void enterPark(int row) {
        Visitor v = getVisitorAt(row);
        if (v == null) return;
        if (park.findVisitor(v.getId()) != null) {
            info(v.getName() + " is already inside the park."); return;
        }
        if (v.getTicket() == null) {
            info(v.getName() + " has no ticket. Assign one first."); return;
        }
        if (!park.hasAvailableCapacity()) {
            info("Park is at full capacity."); return;
        }
        park.registerVisitor(v);
        DataManager.save(store, park);
        refreshTable(null);
        JOptionPane.showMessageDialog(this,
                v.getName() + " has entered the park.",
                "Entry registered", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exitPark(int row) {
        Visitor v = getVisitorAt(row);
        if (v == null) return;
        if (park.findVisitor(v.getId()) == null) {
            info(v.getName() + " is not inside the park."); return;
        }
        park.removeVisitor(v.getId());
        DataManager.save(store, park);
        refreshTable(null);
        JOptionPane.showMessageDialog(this,
                v.getName() + " has exited the park.",
                "Exit registered", JOptionPane.INFORMATION_MESSAGE);
    }

    // ================================================================
    // helpers
    // ================================================================
    private boolean validEmail(String email) {
        if (email == null || email.isBlank()) return false;
        int at = email.indexOf('@');
        if (at < 1) return false;
        String domain = email.substring(at + 1);
        int dot = domain.lastIndexOf('.');
        return dot > 0 && dot < domain.length() - 1;
    }

    private JTable buildTable() {
        JTable t = new JTable(tableModel);
        t.setBackground(C_CARD); t.setForeground(C_TEXT); t.setFont(F_BODY);
        t.setSelectionBackground(new Color(30, 50, 90));
        t.setSelectionForeground(C_TEXT); t.setRowHeight(32);
        t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0, 1));
        t.setFillsViewportHeight(true);

        JTableHeader h = t.getTableHeader();
        h.setBackground(new Color(22, 30, 42));
        h.setForeground(C_TEXT2); h.setFont(F_SMALL);
        h.setPreferredSize(new Dimension(0, 34));

        // color ticket column
        t.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean focus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl,val,sel,focus,r,c);
                lbl.setForeground(switch (val != null ? val.toString() : "") {
                    case "FAST_PASS" -> new Color(180, 130, 255);
                    case "FAMILY" -> C_SUCCESS;
                    case "GENERAL" -> C_TEXT2;
                    default -> C_DANGER;
                });
                return lbl;
            }
        });

        // color in-park column
        t.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean focus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl,val,sel,focus,r,c);
                lbl.setForeground("Yes".equals(val) ? C_SUCCESS : C_TEXT2);
                return lbl;
            }
        });

        return t;
    }

    private JScrollPane darkScroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setOpaque(false); sp.getViewport().setOpaque(false);
        sp.getViewport().setBackground(C_BG);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(12);
        return sp;
    }

    private JDialog dialog(String title, int w, int h) {
        JDialog d = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), title, true);
        d.setSize(w, h); d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(C_BG); return d;
    }

    private JPanel formPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        return p;
    }

    private JPanel infoCard() {
        JPanel p = new JPanel();
        p.setBackground(C_CARD);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        return p;
    }

    private void addInfoRow(JPanel card, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_LABEL); lbl.setForeground(C_TEXT2);
        lbl.setPreferredSize(new Dimension(100, 0));
        JLabel val = new JLabel(value);
        val.setFont(F_BODY); val.setForeground(C_TEXT);
        row.add(lbl, BorderLayout.WEST); row.add(val, BorderLayout.CENTER);
        card.add(row);
    }

    private void addRow(JPanel form, String lbl, JComponent field) {
        JLabel l = new JLabel(lbl);
        l.setFont(F_LABEL); l.setForeground(C_TEXT2); l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        form.add(l); form.add(Box.createRigidArea(new Dimension(0, 4)));
        form.add(field); form.add(Box.createRigidArea(new Dimension(0, 12)));
    }

    private JTextField searchField() {
        JTextField f = new JTextField(18);
        f.setFont(F_FIELD); f.setForeground(C_TEXT); f.setBackground(C_FIELD);
        f.setCaretColor(C_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        f.setPreferredSize(new Dimension(180, 34));
        // hint
        f.setText("Search name, email, document...");
        f.setForeground(C_TEXT2);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (f.getText().startsWith("Search")) { f.setText(""); f.setForeground(C_TEXT); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText("Search name, email, document..."); f.setForeground(C_TEXT2); }
            }
        });
        return f;
    }

    private JTextField field(String initial) {
        JTextField f = new JTextField(initial);
        f.setFont(F_FIELD); f.setForeground(C_TEXT); f.setBackground(C_FIELD);
        f.setCaretColor(C_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); return f;
    }

    private JPasswordField passField() {
        JPasswordField f = new JPasswordField();
        f.setFont(F_FIELD); f.setForeground(C_TEXT); f.setBackground(C_FIELD);
        f.setCaretColor(C_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); return f;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l.setForeground(C_TEXT); l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }

    private JButton primaryBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? C_PRIMARY.brighter() : C_PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN); btn.setForeground(Color.WHITE);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); return btn;
    }

    private JButton actionBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(F_BTN); btn.setForeground(color);
        btn.setBackground(new Color(
                Math.max(0, color.getRed()/5),
                Math.max(0, color.getGreen()/5),
                Math.max(0, color.getBlue()/5)
        ));
        btn.setBorder(BorderFactory.createLineBorder(color));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return btn;
    }

    private JLabel errLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(F_LABEL); l.setForeground(C_DANGER);
        l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }

    private int sel() {
        int r = table.getSelectedRow();
        if (r < 0) info("Select a visitor first.");
        return r;
    }

    private void err(JLabel l, String msg) { l.setForeground(C_DANGER); l.setText(msg); }
    private void info(String msg) { JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE); }
}