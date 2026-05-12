package co.edu.uniquindio.techpark.view;

import co.edu.uniquindio.techpark.model.entities.Attraction;
import co.edu.uniquindio.techpark.model.entities.Park;
import co.edu.uniquindio.techpark.model.entities.UserStore;
import co.edu.uniquindio.techpark.model.entities.Zone;
import co.edu.uniquindio.techpark.model.enums.AttractionStatus;
import co.edu.uniquindio.techpark.model.enums.AttractionType;
import co.edu.uniquindio.techpark.model.structures.LinkedList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class AttractionCRUD extends JPanel {

    private static final Color C_BG      = new Color(10, 14, 20);
    private static final Color C_CARD    = new Color(18, 24, 32);
    private static final Color C_BORDER  = new Color(40, 50, 64);
    private static final Color C_PRIMARY = new Color(56, 130, 255);
    private static final Color C_TEXT    = new Color(224, 232, 244);
    private static final Color C_TEXT2   = new Color(120, 134, 154);
    private static final Color C_SUCCESS = new Color(52, 199, 100);
    private static final Color C_WARNING = new Color(255, 180, 50);
    private static final Color C_DANGER  = new Color(240, 80, 80);
    private static final Color C_FIELD   = new Color(10, 14, 20);

    private static final Font F_TITLE   = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font F_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BTN     = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font F_LABEL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_FIELD   = new Font("Segoe UI", Font.PLAIN, 13);

    private final Park  park;
    private DefaultTableModel tableModel;
    private JTable table;

    private static final String[] COLUMNS = {
            "ID", "Name", "Zone", "Type", "Status",
            "Capacity", "Min. Height", "Min. Age", "Add. Cost", "Wait (min)"
    };

    public AttractionCRUD(Park park) {
        this.park = park;
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        build();
    }

    private void build() {
        // top bar
        JPanel topBar = new JPanel(new BorderLayout(16, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 24, 14, 24));

        JLabel title = new JLabel("Attraction Management");
        title.setFont(F_TITLE); title.setForeground(C_TEXT);
        topBar.add(title, BorderLayout.WEST);

        JButton btnAdd = actionBtn("+ Add Attraction", C_PRIMARY);
        topBar.add(btnAdd, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = buildTable(tableModel);
        JScrollPane scroll = darkScroll(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        add(scroll, BorderLayout.CENTER);

        // bottom action bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bottomBar.setOpaque(false);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(12, 24, 20, 24));

        JButton btnEdit   = actionBtn("Edit",          C_WARNING);
        JButton btnDelete = actionBtn("Delete",        C_DANGER);
        JButton btnStatus = actionBtn("Change Status", new Color(100, 80, 200));
        JButton btnRefresh = actionBtn("Refresh",      C_TEXT2);

        bottomBar.add(btnEdit);
        bottomBar.add(btnDelete);
        bottomBar.add(btnStatus);
        bottomBar.add(btnRefresh);
        add(bottomBar, BorderLayout.SOUTH);

        // load data
        refreshTable();

        // listeners
        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { info("Select an attraction first."); return; }
            showEditDialog(row);
        });
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { info("Select an attraction first."); return; }
            deleteRow(row);
        });
        btnStatus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { info("Select an attraction first."); return; }
            showStatusDialog(row);
        });
        btnRefresh.addActionListener(e -> refreshTable());
    }

    // ================================================================
    // table data
    // ================================================================
    private void refreshTable() {
        tableModel.setRowCount(0);
        LinkedList<Attraction> all = park.getAttractionCatalog().inorder();
        int n = all.getSize();
        for (int i = 0; i < n; i++) {
            Attraction a = all.get(i);
            if (a == null) continue;
            tableModel.addRow(new Object[]{
                    a.getId(),
                    a.getName(),
                    a.getZoneId() != null ? a.getZoneId() : "-",
                    a.getType().name(),
                    a.getStatus().name(),
                    a.getCapacityPerCycle(),
                    a.getMinimumHeight(),
                    a.getMinimumAge(),
                    String.format("$%.0f", a.getAdditionalCost()),
                    a.getEstimatedWaitTime()
            });
        }
    }

    private Attraction getAttractionAt(int row) {
        String id = (String) tableModel.getValueAt(row, 0);
        LinkedList<Attraction> all = park.getAttractionCatalog().inorder();
        int n = all.getSize();
        for (int i = 0; i < n; i++) {
            Attraction a = all.get(i);
            if (a != null && a.getId().equals(id)) return a;
        }
        return null;
    }

    // ================================================================
    // ADD dialog
    // ================================================================
    private void showAddDialog() {
        JDialog dialog = dialog("Add Attraction", 480, 540);
        JPanel form    = formPanel();

        JTextField  nameField    = field("e.g. Space Coaster");
        JComboBox<String> typeCombo  = combo(AttractionType.values());
        JComboBox<String> zoneCombo  = buildZoneCombo();
        JTextField  capField     = field("24");
        JTextField  heightField  = field("1.40");
        JTextField  ageField     = field("12");
        JTextField  costField    = field("15000");
        JTextField  waitField    = field("20");

        addRow(form, "Name *",           nameField);
        addRow(form, "Type *",           typeCombo);
        addRow(form, "Zone *",           zoneCombo);
        addRow(form, "Capacity/cycle *", capField);
        addRow(form, "Min. height (m)",  heightField);
        addRow(form, "Min. age",         ageField);
        addRow(form, "Additional cost",  costField);
        addRow(form, "Est. wait (min)",  waitField);

        JLabel errLbl = errLabel();
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        form.add(errLbl);

        JButton btnSave = primaryBtn("Save attraction");
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(btnSave);

        dialog.add(darkScroll(form));

        btnSave.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { err(errLbl, "Name is required."); return; }

            String zoneId = zoneIdFromCombo(zoneCombo);
            if (zoneId == null) { err(errLbl, "Select a zone."); return; }

            int    cap;
            float  height;
            int    age;
            double cost;
            int    wait;

            try {
                cap    = Integer.parseInt(capField.getText().trim());
                height = Float.parseFloat(heightField.getText().trim().replace(",","."));
                age    = Integer.parseInt(ageField.getText().trim());
                cost   = Double.parseDouble(costField.getText().trim());
                wait   = Integer.parseInt(waitField.getText().trim());
            } catch (NumberFormatException ex) {
                err(errLbl, "Check numeric fields."); return;
            }

            AttractionType type = AttractionType.valueOf(
                    (String) typeCombo.getSelectedItem()
            );
            String id = "ATT-" + String.format("%03d", System.currentTimeMillis() % 1000);

            Attraction a = new Attraction(id, name, type, cap, height, age, cost, wait);
            Zone zone = park.findZone(zoneId);
            if (zone == null) { err(errLbl, "Zone not found."); return; }

            zone.addAttraction(a);
            park.registerAttractionInCatalog(a);
            DataManager.save(UserStore.getInstance(), park);
            refreshTable();
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    // ================================================================
    // EDIT dialog
    // ================================================================
    private void showEditDialog(int row) {
        Attraction a = getAttractionAt(row);
        if (a == null) return;

        JDialog dialog = dialog("Edit - " + a.getName(), 480, 420);
        JPanel  form   = formPanel();

        JTextField nameField   = field(a.getName());
        JTextField capField    = field(String.valueOf(a.getCapacityPerCycle()));
        JTextField heightField = field(String.valueOf(a.getMinimumHeight()));
        JTextField ageField    = field(String.valueOf(a.getMinimumAge()));
        JTextField costField   = field(String.valueOf(a.getAdditionalCost()));
        JTextField waitField   = field(String.valueOf(a.getEstimatedWaitTime()));

        addRow(form, "Name *",           nameField);
        addRow(form, "Capacity/cycle *", capField);
        addRow(form, "Min. height (m)",  heightField);
        addRow(form, "Min. age",         ageField);
        addRow(form, "Additional cost",  costField);
        addRow(form, "Est. wait (min)",  waitField);

        JLabel errLbl = errLabel();
        form.add(Box.createRigidArea(new Dimension(0, 6)));
        form.add(errLbl);

        JButton btnSave = primaryBtn("Save changes");
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(btnSave);

        dialog.add(darkScroll(form));

        btnSave.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { err(errLbl, "Name is required."); return; }
            try {
                int    cap    = Integer.parseInt(capField.getText().trim());
                float  height = Float.parseFloat(heightField.getText().trim().replace(",","."));
                int    age    = Integer.parseInt(ageField.getText().trim());
                double cost   = Double.parseDouble(costField.getText().trim());
                int    wait   = Integer.parseInt(waitField.getText().trim());

                a.setName(name);
                a.setCapacityPerCycle(cap);
                a.setMinimumHeight(height);
                a.setMinimumAge(age);
                a.setAdditionalCost(cost);
                a.setEstimatedWaitTime(wait);

            } catch (NumberFormatException ex) {
                err(errLbl, "Check numeric fields."); return;
            }
            DataManager.save(UserStore.getInstance(), park);
            refreshTable();
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    // ================================================================
    // DELETE
    // ================================================================
    private void deleteRow(int row) {
        Attraction a = getAttractionAt(row);
        if (a == null) return;
        if (!a.getVirtualQueue().isEmpty()) {
            info("Cannot delete: attraction has visitors in the virtual queue.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete \"" + a.getName() + "\"? This cannot be undone.",
                "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        Zone zone = park.findZone(a.getZoneId());
        if (zone != null) zone.removeAttraction(a.getId());

        DataManager.save(UserStore.getInstance(), park);
        refreshTable();
    }

    // ================================================================
    // STATUS dialog
    // ================================================================
    private void showStatusDialog(int row) {
        Attraction a = getAttractionAt(row);
        if (a == null) return;

        String[] options = {"ACTIVE", "MAINTENANCE", "CLOSED"};
        int choice = JOptionPane.showOptionDialog(this,
                "Select new status for: " + a.getName(),
                "Change status", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, a.getStatus().name());

        if (choice < 0) return;

        String reason = null;
        if (choice == 2) {
            reason = JOptionPane.showInputDialog(this, "Reason for closure:");
            if (reason == null) return;
        }

        AttractionStatus newStatus = switch (choice) {
            case 0  -> AttractionStatus.ACTIVE;
            case 1  -> AttractionStatus.MAINTENANCE;
            default -> AttractionStatus.CLOSED;
        };

        a.changeStatus(newStatus, reason);
        DataManager.save(UserStore.getInstance(), park);
        refreshTable();
    }

    // ================================================================
    // helpers
    // ================================================================
    private JComboBox<String> buildZoneCombo() {
        JComboBox<String> combo = new JComboBox<>();
        LinkedList<Zone> zones = park.getZones();
        int n = zones.getSize();
        for (int i = 0; i < n; i++) {
            Zone z = zones.get(i);
            if (z != null) combo.addItem(z.getName() + " [" + z.getId() + "]");
        }
        styleCombo(combo); return combo;
    }

    private String zoneIdFromCombo(JComboBox<String> combo) {
        String item = (String) combo.getSelectedItem();
        if (item == null) return null;
        int s = item.lastIndexOf('[');
        int e = item.lastIndexOf(']');
        if (s < 0 || e < 0) return null;
        return item.substring(s + 1, e);
    }

    private JComboBox<String> combo(Object[] values) {
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) names[i] = values[i].toString();
        JComboBox<String> c = new JComboBox<>(names);
        styleCombo(c); return c;
    }

    private void styleCombo(JComboBox<String> c) {
        c.setFont(F_FIELD); c.setBackground(C_FIELD); c.setForeground(C_TEXT);
        c.setPreferredSize(new Dimension(0, 36));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private JTable buildTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(C_CARD); t.setForeground(C_TEXT); t.setFont(F_BODY);
        t.setSelectionBackground(new Color(30, 50, 90));
        t.setSelectionForeground(C_TEXT); t.setRowHeight(32);
        t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0, 1));
        t.setFillsViewportHeight(true);

        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(22, 30, 42));
        header.setForeground(C_TEXT2); header.setFont(F_SMALL);
        header.setPreferredSize(new Dimension(0, 34));

        // color status column
        t.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean focus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl,val,sel,focus,r,c);
                String s = val != null ? val.toString() : "";
                lbl.setForeground(switch (s) {
                    case "ACTIVE"      -> C_SUCCESS;
                    case "MAINTENANCE" -> C_WARNING;
                    case "CLOSED"      -> C_DANGER;
                    default            -> C_TEXT;
                });
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
        d.getContentPane().setBackground(C_BG);
        return d;
    }

    private JPanel formPanel() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        return p;
    }

    private void addRow(JPanel form, String labelText, JComponent field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(F_LABEL); lbl.setForeground(C_TEXT2);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                field instanceof JComboBox ? 36 : 38));
        form.add(lbl);
        form.add(Box.createRigidArea(new Dimension(0, 4)));
        form.add(field);
        form.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JTextField field(String initial) {
        JTextField f = new JTextField(initial);
        f.setFont(F_FIELD); f.setForeground(C_TEXT); f.setBackground(C_FIELD);
        f.setCaretColor(C_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return f;
    }

    private JButton primaryBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? C_PRIMARY.brighter() : C_PRIMARY;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN); btn.setForeground(Color.WHITE);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
    }

    private JButton actionBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(F_BTN); btn.setForeground(color);
        btn.setBackground(new Color(color.getRed()/5, color.getGreen()/5, color.getBlue()/5));
        btn.setBorder(BorderFactory.createLineBorder(color));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel errLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(F_LABEL); l.setForeground(C_DANGER);
        l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }

    private void err(JLabel l, String msg)  { l.setForeground(C_DANGER);  l.setText(msg); }
    private void info(String msg)           { JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE); }
}