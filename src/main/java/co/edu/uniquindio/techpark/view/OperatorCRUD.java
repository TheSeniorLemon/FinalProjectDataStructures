package co.edu.uniquindio.techpark.view;

import co.edu.uniquindio.techpark.model.entities.Operator;
import co.edu.uniquindio.techpark.model.entities.Park;
import co.edu.uniquindio.techpark.model.entities.UserStore;
import co.edu.uniquindio.techpark.model.entities.Zone;
import co.edu.uniquindio.techpark.model.structures.LinkedList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class OperatorCRUD extends JPanel {

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
            "ID", "Name", "Document", "Email", "Assigned Zone", "Status"
    };

    private final Park park;
    private final UserStore store;
    private DefaultTableModel tableModel;
    private JTable table;

    public OperatorCRUD(Park park, UserStore store) {
        this.park  = park;
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
        JLabel title = new JLabel("Operator Management");
        title.setFont(F_TITLE); title.setForeground(C_TEXT);
        topBar.add(title, BorderLayout.WEST);
        JButton btnAdd = actionBtn("+ Add Operator", C_PRIMARY);
        topBar.add(btnAdd, BorderLayout.EAST);
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
        JButton btnEdit = actionBtn("Edit", C_WARNING);
        JButton btnDelete = actionBtn("Remove", C_DANGER);
        JButton btnUnassign = actionBtn("Unassign Zone", new Color(100, 80, 200));
        JButton btnRefresh = actionBtn("Refresh", C_TEXT2);
        bottomBar.add(btnEdit); bottomBar.add(btnDelete);
        bottomBar.add(btnUnassign); bottomBar.add(btnRefresh);
        add(bottomBar, BorderLayout.SOUTH);

        refreshTable();

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> { int r = sel(); if (r < 0) return; showEditDialog(r); });
        btnDelete.addActionListener(e -> { int r = sel(); if (r < 0) return; deleteRow(r); });
        btnUnassign.addActionListener(e -> { int r = sel(); if (r < 0) return; unassignZone(r); });
        btnRefresh.addActionListener(e -> refreshTable());
    }

    // ================================================================
    // table
    // ================================================================
    private void refreshTable() {
        tableModel.setRowCount(0);
        LinkedList<Operator> ops = store.getOperators();
        int n = ops.getSize();
        for (int i = 0; i < n; i++) {
            Operator op = ops.get(i);
            if (op == null) continue;
            String zone   = op.hasAssignedZone() ? op.getAssignedZoneId() : "Unassigned";
            String status = op.hasAssignedZone() ? "Active" : "Idle";
            tableModel.addRow(new Object[]{
                    op.getId(), op.getName(), op.getDocument(),
                    op.getEmail(), zone, status
            });
        }
    }

    private Operator getOperatorAt(int row) {
        String id  = (String) tableModel.getValueAt(row, 0);
        LinkedList<Operator> ops = store.getOperators();
        int n = ops.getSize();
        for (int i = 0; i < n; i++) {
            Operator op = ops.get(i);
            if (op != null && op.getId().equals(id)) return op;
        }
        return null;
    }

    // ================================================================
    // ADD
    // ================================================================
    private void showAddDialog() {
        JDialog dialog = dialog("Add Operator", 420, 360);
        JPanel  form   = formPanel();

        JTextField nameField = field("Full name");
        JTextField docField  = field("Document number");
        JTextField emailField = field("email@example.com");
        JPasswordField passField = passField("Password (min 6 chars)");

        addRow(form, "Full name *", nameField);
        addRow(form, "Document *", docField);
        addRow(form, "Email *", emailField);
        addRow(form, "Password *", passField);

        JLabel errLbl = errLabel();
        form.add(errLbl);
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        JButton btnSave = primaryBtn("Create operator account");
        form.add(btnSave);

        dialog.add(form);

        btnSave.addActionListener(e -> {
            String name = nameField.getText().trim();
            String doc = docField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword()).trim();

            if (name.isEmpty() || doc.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                err(errLbl, "All fields are required."); return;
            }
            if (!validEmail(email)) { err(errLbl, "Invalid email address."); return; }
            if (pass.length() < 6)  { err(errLbl, "Password must be at least 6 characters."); return; }
            if (store.emailExists(email))    { err(errLbl, "Email already registered."); return; }
            if (store.documentExists(doc))   { err(errLbl, "Document already registered."); return; }

            String id = "OP-" + String.format("%03d", System.currentTimeMillis() % 1000);
            Operator op = new Operator(id, name, doc, email, pass);
            store.register(op);
            park.registerOperator(op);
            DataManager.save(store, park);
            refreshTable();
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    // ================================================================
    // EDIT
    // ================================================================
    private void showEditDialog(int row) {
        Operator op = getOperatorAt(row);
        if (op == null) return;

        JDialog dialog = dialog("Edit - " + op.getName(), 420, 260);
        JPanel  form   = formPanel();

        JTextField nameField  = field(op.getName());
        JTextField emailField = field(op.getEmail());

        addRow(form, "Full name *",  nameField);
        addRow(form, "Email *", emailField);

        JLabel errLbl = errLabel();
        form.add(errLbl);
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        JButton btnSave = primaryBtn("Save changes");
        form.add(btnSave);

        dialog.add(form);

        btnSave.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String email = emailField.getText().trim();
            if (name.isEmpty()) { err(errLbl, "Name is required."); return; }
            if (!validEmail(email)) { err(errLbl, "Invalid email address."); return; }
            if (!email.equals(op.getEmail()) && store.emailExists(email)) {
                err(errLbl, "Email already in use."); return;
            }
            op.setName(name);
            op.setEmail(email);
            DataManager.save(store, park);
            refreshTable();
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    // ================================================================
    // DELETE
    // ================================================================
    private void deleteRow(int row) {
        Operator op = getOperatorAt(row);
        if (op == null) return;

        if (op.hasAssignedZone()) {
            Zone zone = park.findZone(op.getAssignedZoneId());
            if (zone != null && zone.getOperators().getSize() <= 1) {
                info("Cannot remove: zone \"" + zone.getName() +
                        "\" would be left without an operator.");
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove operator \"" + op.getName() + "\"?",
                "Confirm removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (op.hasAssignedZone()) {
            Zone zone = park.findZone(op.getAssignedZoneId());
            if (zone != null) zone.removeOperator(op.getId());
            op.setAssignedZoneId(null);
        }
        park.removeOperator(op.getId());
        // note: store.getOperators() keeps the user for login;
        // remove from park only (they keep their account)
        DataManager.save(store, park);
        refreshTable();
    }

    // ================================================================
    // UNASSIGN
    // ================================================================
    private void unassignZone(int row) {
        Operator op = getOperatorAt(row);
        if (op == null) return;
        if (!op.hasAssignedZone()) { info("Operator is not assigned to any zone."); return; }

        Zone zone = park.findZone(op.getAssignedZoneId());
        if (zone != null && zone.getOperators().getSize() <= 1) {
            info("Cannot unassign: zone \"" + zone.getName() +
                    "\" would be left without an operator.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Unassign " + op.getName() + " from zone " + op.getAssignedZoneId() + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (zone != null) zone.removeOperator(op.getId());
        op.setAssignedZoneId(null);
        DataManager.save(store, park);
        refreshTable();
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

        // color status column
        t.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean focus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl,val,sel,focus,r,c);
                lbl.setForeground("Active".equals(val) ? C_SUCCESS : C_WARNING);
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

    private void addRow(JPanel form, String lbl, JComponent field) {
        JLabel l = new JLabel(lbl);
        l.setFont(F_LABEL); l.setForeground(C_TEXT2); l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        form.add(l); form.add(Box.createRigidArea(new Dimension(0, 4)));
        form.add(field); form.add(Box.createRigidArea(new Dimension(0, 12)));
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

    private JPasswordField passField(String hint) {
        JPasswordField f = new JPasswordField();
        f.setFont(F_FIELD); f.setForeground(C_TEXT); f.setBackground(C_FIELD);
        f.setCaretColor(C_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); return f;
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
        btn.setBackground(new Color(color.getRed()/5, color.getGreen()/5, color.getBlue()/5));
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
        if (r < 0) info("Select an operator first.");
        return r;
    }

    private void err(JLabel l, String msg) { l.setForeground(C_DANGER); l.setText(msg); }
    private void info(String msg) { JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE); }
}