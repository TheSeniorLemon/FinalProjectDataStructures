package co.edu.uniquindio.techpark.view;

import co.edu.uniquindio.techpark.model.entities.*;
import co.edu.uniquindio.techpark.model.structures.LinkedList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class ZoneCRUD extends JPanel {

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
            "ID", "Name", "Max Capacity", "Visitors", "Attractions", "Operators", "Availability"
    };

    private final Park park;
    private DefaultTableModel tableModel;
    private JTable table;

    public ZoneCRUD(Park park) {
        this.park = park;
        setOpaque(false);
        setLayout(new BorderLayout());
        build();
    }

    private void build() {
        // top bar
        JPanel topBar = new JPanel(new BorderLayout(16, 0));
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(20, 24, 14, 24));
        JLabel title = new JLabel("Zone Management");
        title.setFont(F_TITLE); title.setForeground(C_TEXT);
        topBar.add(title, BorderLayout.WEST);
        JButton btnAdd = actionBtn("+ Add Zone", C_PRIMARY);
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
        JButton btnDelete = actionBtn("Delete", C_DANGER);
        JButton btnAssign = actionBtn("Assign Operator", new Color(80, 160, 100));
        JButton btnRefresh = actionBtn("Refresh", C_TEXT2);
        bottomBar.add(btnEdit); bottomBar.add(btnDelete);
        bottomBar.add(btnAssign); bottomBar.add(btnRefresh);
        add(bottomBar, BorderLayout.SOUTH);

        refreshTable();

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> { int r = table.getSelectedRow(); if (r < 0) { info("Select a zone first."); return; } showEditDialog(r); });
        btnDelete.addActionListener(e -> { int r = table.getSelectedRow(); if (r < 0) { info("Select a zone first."); return; } deleteRow(r); });
        btnAssign.addActionListener(e -> { int r = table.getSelectedRow(); if (r < 0) { info("Select a zone first."); return; } showAssignDialog(r); });
        btnRefresh.addActionListener(e -> refreshTable());
    }

    // ================================================================
    // table
    // ================================================================
    private void refreshTable() {
        tableModel.setRowCount(0);
        LinkedList<Zone> zones = park.getZones();
        int n = zones.getSize();
        for (int i = 0; i < n; i++) {
            Zone z = zones.get(i);
            if (z == null) continue;
            int slots = z.getMaxCapacity() - z.getCurrentVisitors();
            tableModel.addRow(new Object[]{
                    z.getId(),
                    z.getName(),
                    z.getMaxCapacity(),
                    z.getCurrentVisitors(),
                    z.getAttractions().getSize(),
                    z.getOperators().getSize(),
                    slots > 0 ? slots + " slots free" : "FULL"
            });
        }
    }

    private Zone getZoneAt(int row) {
        String id = (String) tableModel.getValueAt(row, 0);
        return park.findZone(id);
    }

    // ================================================================
    // ADD
    // ================================================================
    private void showAddDialog() {
        JDialog dialog = dialog("Add Zone", 420, 280);
        JPanel form = formPanel();

        JTextField idField   = field("ZONE-00" + (park.getZones().getSize() + 1));
        JTextField nameField = field("e.g. Fantasy Land");
        JTextField capField  = field("100");

        addRow(form, "Zone ID *", idField);
        addRow(form, "Name *", nameField);
        addRow(form, "Max capacity *", capField);

        JLabel errLbl = errLabel();
        form.add(errLbl);
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        JButton btnSave = primaryBtn("Create zone");
        form.add(btnSave);

        dialog.add(form);

        btnSave.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            if (id.isEmpty() || name.isEmpty()) { err(errLbl, "ID and name are required."); return; }

            if (park.findZone(id) != null) { err(errLbl, "A zone with that ID already exists."); return; }

            int cap;
            try {
                cap = Integer.parseInt(capField.getText().trim());
                if (cap <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) { err(errLbl, "Capacity must be a positive number."); return; }

            Zone zone = new Zone(id, name, cap);
            park.addZone(zone);
            DataManager.save(UserStore.getInstance(), park);
            refreshTable();
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    // ================================================================
    // EDIT
    // ================================================================
    private void showEditDialog(int row) {
        Zone z = getZoneAt(row);
        if (z == null) return;

        JDialog dialog = dialog("Edit - " + z.getName(), 420, 240);
        JPanel  form   = formPanel();

        JTextField nameField = field(z.getName());
        JTextField capField  = field(String.valueOf(z.getMaxCapacity()));

        addRow(form, "Name *", nameField);
        addRow(form, "Max capacity *", capField);

        JLabel errLbl = errLabel();
        form.add(errLbl);
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        JButton btnSave = primaryBtn("Save changes");
        form.add(btnSave);

        dialog.add(form);

        btnSave.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { err(errLbl, "Name is required."); return; }
            int cap;
            try {
                cap = Integer.parseInt(capField.getText().trim());
                if (cap <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) { err(errLbl, "Capacity must be a positive number."); return; }

            z.setName(name);
            z.setMaxCapacity(cap);
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
        Zone z = getZoneAt(row);
        if (z == null) return;

        if (z.getAttractions().getSize() > 0) {
            info("Cannot delete: zone still has attractions. Remove them first.");
            return;
        }
        if (z.getCurrentVisitors() > 0) {
            info("Cannot delete: zone has visitors inside.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete zone \"" + z.getName() + "\"? This cannot be undone.",
                "Confirm deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        park.removeZone(z.getId());
        DataManager.save(UserStore.getInstance(), park);
        refreshTable();
    }

    // ================================================================
    // ASSIGN OPERATOR
    // ================================================================
    private void showAssignDialog(int row) {
        Zone z = getZoneAt(row);
        if (z == null) return;

        // build list of unassigned operators
        LinkedList<Operator> ops = park.getOperators();
        int n = ops.getSize();

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (int i = 0; i < n; i++) {
            Operator op = ops.get(i);
            if (op != null && !op.hasAssignedZone()) {
                listModel.addElement(op.getName() + " <" + op.getEmail() + "> [" + op.getId() + "]");
            }
        }

        if (listModel.isEmpty()) {
            info("No unassigned operators available.");
            return;
        }

        JList<String> opList = new JList<>(listModel);
        opList.setFont(F_BODY);
        opList.setBackground(C_CARD);
        opList.setForeground(C_TEXT);
        opList.setSelectionBackground(new Color(30, 50, 90));
        opList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int res = JOptionPane.showConfirmDialog(
                this,
                new JScrollPane(opList),
                "Assign operator to " + z.getName(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (res != JOptionPane.OK_OPTION || opList.getSelectedIndex() < 0) return;

        String selected = opList.getSelectedValue();
        String opId     = selected.substring(selected.lastIndexOf('[') + 1, selected.lastIndexOf(']'));

        Operator target = null;
        for (int i = 0; i < n; i++) {
            Operator op = ops.get(i);
            if (op != null && op.getId().equals(opId)) { target = op; break; }
        }

        if (target == null) { info("Operator not found."); return; }

        z.assignOperator(target);
        target.setAssignedZoneId(z.getId());
        DataManager.save(UserStore.getInstance(), park);
        refreshTable();

        JOptionPane.showMessageDialog(this,
                target.getName() + " assigned to " + z.getName() + ".",
                "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    // ================================================================
    // helpers
    // ================================================================
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

        // color availability column
        t.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean focus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl,val,sel,focus,r,c);
                String s = val != null ? val.toString() : "";
                lbl.setForeground(s.equals("FULL") ? C_DANGER : C_SUCCESS);
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

    private void addRow(JPanel form, String labelText, JComponent field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(F_LABEL); lbl.setForeground(C_TEXT2);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        form.add(lbl);
        form.add(Box.createRigidArea(new Dimension(0, 4)));
        form.add(field);
        form.add(Box.createRigidArea(new Dimension(0, 12)));
    }

    private JTextField field(String initial) {
        JTextField f = new JTextField(initial);
        f.setFont(F_FIELD); f.setForeground(C_TEXT); f.setBackground(C_FIELD);
        f.setCaretColor(C_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
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

    private void err(JLabel l, String msg) { l.setForeground(C_DANGER); l.setText(msg); }
    private void info(String msg) { JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE); }
}