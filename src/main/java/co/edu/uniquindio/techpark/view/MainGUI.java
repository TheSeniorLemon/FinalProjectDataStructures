package co.edu.uniquindio.techpark.view;

import co.edu.uniquindio.techpark.model.entities.*;
import co.edu.uniquindio.techpark.model.enums.AlertType;
import co.edu.uniquindio.techpark.model.enums.AttractionStatus;
import co.edu.uniquindio.techpark.model.enums.UserRole;
import co.edu.uniquindio.techpark.model.structures.GraphEdge;
import co.edu.uniquindio.techpark.model.structures.GraphNode;
import co.edu.uniquindio.techpark.model.structures.LinkedList;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class MainGUI extends JFrame {

    // ----------------------------------------------------------------
    // color theme (matches LoginGUI)
    // ----------------------------------------------------------------
    private static final Color C_BG = new Color(10, 14, 20);
    private static final Color C_SIDEBAR = new Color(15, 20, 28);
    private static final Color C_CARD = new Color(18, 24, 32);
    private static final Color C_CARD2 = new Color(22, 30, 42);
    private static final Color C_BORDER = new Color(40, 50, 64);
    private static final Color C_PRIMARY = new Color(56, 130, 255);
    private static final Color C_PRIMARY_H = new Color(84, 152, 255);
    private static final Color C_TEXT = new Color(224, 232, 244);
    private static final Color C_TEXT2 = new Color(120, 134, 154);
    private static final Color C_SUCCESS = new Color(52, 199, 100);
    private static final Color C_WARNING = new Color(255, 180, 50);
    private static final Color C_DANGER = new Color(240, 80, 80);
    private static final Color C_FIELD_BG = new Color(10, 14, 20);
    private static final Color C_NAV_ACTIVE = new Color(30, 50, 90);

    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font F_HEADING = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font F_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BTN = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font F_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_FIELD = new Font("Segoe UI", Font.PLAIN, 13);

    private Park park;
    private User currentUser;
    private LinkedList<Administrator> admins;

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout contentLayout;
    private JButton activeNavBtn;

    public MainGUI(Park park, User currentUser, LinkedList<Administrator> admins) {
        this.park = park;
        this.currentUser = currentUser;
        this.admins = admins;
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
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(900, 600));
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(C_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setOpaque(false);

        root.add(buildTopBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);

        setContentPane(root);

        // show first panel by default
        String firstPanel = firstPanelForRole();
        contentLayout.show(contentPanel, firstPanel);
        highlightNavButton(firstPanel);
    }

    // ================================================================
    // TOP BAR
    // ================================================================
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_SIDEBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(C_BORDER);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 56));
        bar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // left: logo
        JLabel logo = new JLabel("TECH-PARK UQ");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(C_PRIMARY);
        bar.add(logo, BorderLayout.WEST);

        // right: user info + logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JLabel userInfo = new JLabel(currentUser.getName() + "  |  " + currentUser.getUserRole());
        userInfo.setFont(F_SMALL);
        userInfo.setForeground(C_TEXT2);

        JButton btnLogout = new JButton("Sign out") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? C_DANGER : new Color(60, 30, 30);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogout.setFont(F_SMALL);
        btnLogout.setForeground(C_DANGER);
        btnLogout.setOpaque(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Are you sure you want to sign out?",
                    "Sign out", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginGUI(park);
            }
        });

        right.add(userInfo);
        right.add(btnLogout);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    // ================================================================
    // SIDEBAR
    // ================================================================
    private JPanel buildSidebar() {
        sidebarPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_SIDEBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(C_BORDER);
                g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
                g2.dispose();
            }
        };
        sidebarPanel.setOpaque(false);
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        UserRole role = currentUser.getUserRole();

        if (role == UserRole.VISITOR) {
            addNavButton("Overview", "OVERVIEW");
            addNavButton("My Queue", "QUEUE");
            addNavButton("Route Planner", "ROUTES");
            addNavButton("Favorites", "FAVORITES");
            addNavButton("Visit History", "HISTORY");
        } else if (role == UserRole.OPERATOR) {
            addNavButton("Overview", "OVERVIEW");
            addNavButton("Attractions", "ATTRACTIONS");
            addNavButton("Queue Control", "QUEUE_OP");
            addNavButton("Reviews", "REVIEWS");
        } else {
            addNavButton("Dashboard", "DASHBOARD");
            addNavButton("Attractions", "ATTRACTIONS");
            addNavButton("Staff", "STAFF");
            addNavButton("Weather Alert", "WEATHER");
            addNavButton("Reports", "REPORTS");
            addNavButton("Park Graph", "GRAPH");
        }

        sidebarPanel.add(Box.createVerticalGlue());

        return sidebarPanel;
    }

    private void addNavButton(String label, String key) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                boolean active = key.equals(getClientProperty("activeKey"));
                if (active || getModel().isRollover()) {
                    g2.setColor(active ? C_NAV_ACTIVE : new Color(25, 35, 52));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    if (active) {
                        g2.setColor(C_PRIMARY);
                        g2.fillRect(0, 0, 3, getHeight());
                    }
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.putClientProperty("navKey", key);
        btn.setFont(F_BODY);
        btn.setForeground(C_TEXT2);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        btn.addActionListener(e -> {
            contentLayout.show(contentPanel, key);
            highlightNavButton(key);
        });

        sidebarPanel.add(btn);
    }

    private void highlightNavButton(String key) {
        for (Component c : sidebarPanel.getComponents()) {
            if (c instanceof JButton btn) {
                boolean active = key.equals(btn.getClientProperty("navKey"));
                btn.putClientProperty("activeKey", active ? key : null);
                btn.setForeground(active ? C_TEXT : C_TEXT2);
                btn.repaint();
            }
        }
    }

    // ================================================================
    // CONTENT AREA
    // ================================================================
    private JPanel buildContent() {
        contentLayout = new CardLayout();
        contentPanel  = new JPanel(contentLayout);
        contentPanel.setOpaque(false);

        UserRole role = currentUser.getUserRole();

        if (role == UserRole.VISITOR) {
            contentPanel.add(buildOverviewPanel(), "OVERVIEW");
            contentPanel.add(buildQueuePanel(), "QUEUE");
            contentPanel.add(buildRoutesPanel(), "ROUTES");
            contentPanel.add(buildFavoritesPanel(), "FAVORITES");
            contentPanel.add(buildHistoryPanel(), "HISTORY");
        } else if (role == UserRole.OPERATOR) {
            contentPanel.add(buildOverviewPanel(), "OVERVIEW");
            contentPanel.add(buildAttractionsPanel(), "ATTRACTIONS");
            contentPanel.add(buildQueueControlPanel(), "QUEUE_OP");
            contentPanel.add(buildReviewsPanel(), "REVIEWS");
        } else {
            contentPanel.add(buildDashboardPanel(), "DASHBOARD");
            contentPanel.add(buildAttractionsPanel(), "ATTRACTIONS");
            contentPanel.add(buildStaffPanel(), "STAFF");
            contentPanel.add(buildWeatherPanel(), "WEATHER");
            contentPanel.add(buildReportsPanel(), "REPORTS");
            contentPanel.add(buildGraphPanel(), "GRAPH");
        }

        return contentPanel;
    }

    private String firstPanelForRole() {
        switch (currentUser.getUserRole()) {
            case ADMINISTRATOR: return "DASHBOARD";
            default:            return "OVERVIEW";
        }
    }

    // ================================================================
    // OVERVIEW PANEL  (visitor + operator)
    // ================================================================
    private JPanel buildOverviewPanel() {
        JPanel root = scrollableRoot();

        root.add(sectionTitle("Park Overview"));
        root.add(vgap(12));

        // stats row
        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        int capacity = park.getMaxCapacity();
        int current = park.getCurrentVisitors();
        int available = park.getAvailableCapacity();

        stats.add(statCard("Visitors today", String.valueOf(current), C_PRIMARY));
        stats.add(statCard("Capacity", capacity + " max", C_SUCCESS));
        stats.add(statCard("Slots available", String.valueOf(available), available < 50 ? C_DANGER : C_SUCCESS));
        root.add(stats);
        root.add(vgap(20));

        // attraction list
        root.add(sectionTitle("Attractions"));
        root.add(vgap(10));

        LinkedList<Attraction> all = park.listAllAttractions();
        if (all.isEmpty()) {
            root.add(emptyState("No attractions registered yet."));
        } else {
            JPanel grid = new JPanel(new GridLayout(0, 2, 12, 10));
            grid.setOpaque(false);
            grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            int n = all.getSize();
            for (int i = 0; i < n; i++) {
                Attraction a = all.get(i);
                if (a != null) grid.add(attractionCard(a));
            }
            root.add(grid);
        }

        return wrap(root);
    }

    // ================================================================
    // QUEUE PANEL  (visitor)
    // ================================================================
    private JPanel buildQueuePanel() {
        JPanel root = scrollableRoot();
        root.add(sectionTitle("My Queue Status"));
        root.add(vgap(12));

        if (!(currentUser instanceof Visitor visitor)) {
            root.add(emptyState("Access restricted."));
            return wrap(root);
        }

        LinkedList<Attraction> all = park.listAllAttractions();
        int n = all.getSize();
        boolean inAnyQueue = false;

        for (int i = 0; i < n; i++) {
            Attraction a = all.get(i);
            if (a == null) continue;
            int pos = a.getQueuePosition(visitor);
            if (pos < 0) continue;
            inAnyQueue = true;

            JPanel card = card();
            card.setLayout(new BorderLayout(12, 0));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

            JPanel left = new JPanel();
            left.setOpaque(false);
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            JLabel name = new JLabel(a.getName());
            name.setFont(F_HEADING); name.setForeground(C_TEXT);
            JLabel zone = new JLabel("Zone: " + a.getZoneId() + "  |  Est. wait: " +
                    a.calculateWaitTime() + " min");
            zone.setFont(F_SMALL); zone.setForeground(C_TEXT2);
            left.add(name); left.add(zone);

            JLabel posLabel = new JLabel("Position #" + (pos + 1), SwingConstants.RIGHT);
            posLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
            posLabel.setForeground(C_PRIMARY);

            card.add(left, BorderLayout.CENTER);
            card.add(posLabel, BorderLayout.EAST);
            root.add(card);
            root.add(vgap(10));
        }

        if (!inAnyQueue) {
            root.add(emptyState("You are not currently in any queue.\nGo to Route Planner to find attractions."));
        }

        return wrap(root);
    }

    // ================================================================
    // ROUTE PLANNER  (visitor)
    // ================================================================
    private JPanel buildRoutesPanel() {
        JPanel root = scrollableRoot();
        root.add(sectionTitle("Route Planner"));
        root.add(vgap(12));

        JPanel form = card();
        form.setLayout(new GridBagLayout());
        form.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        GridBagConstraints g = gbc();

        g.gridy = 0; g.insets = ins(12, 16, 4, 16);
        form.add(smallLabel("From (attraction name)"), g);

        JTextField originField = textField("e.g. Roller Coaster");
        g.gridy = 1; g.insets = ins(0, 16, 12, 16);
        form.add(originField, g);

        g.gridy = 2; g.insets = ins(0, 16, 4, 16);
        form.add(smallLabel("To (attraction name)"), g);

        JTextField destField = textField("e.g. Water Slide");
        g.gridy = 3; g.insets = ins(0, 16, 12, 16);
        form.add(destField, g);

        root.add(form);
        root.add(vgap(12));

        JButton btnCalc = primaryButton("Calculate optimal route");
        btnCalc.setMaximumSize(new Dimension(260, 42));
        root.add(btnCalc);
        root.add(vgap(16));

        JPanel resultPanel = new JPanel();
        resultPanel.setOpaque(false);
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        root.add(resultPanel);

        btnCalc.addActionListener(e -> {
            resultPanel.removeAll();
            String originName = originField.getText().trim();
            String destName   = destField.getText().trim();

            if (originName.isEmpty() || destName.isEmpty()) {
                resultPanel.add(errorLabel("Please fill in both fields."));
                resultPanel.revalidate(); resultPanel.repaint();
                return;
            }

            Attraction origin = park.findAttractionByName(originName);
            Attraction dest   = park.findAttractionByName(destName);

            if (origin == null) {
                resultPanel.add(errorLabel("Origin attraction not found: " + originName));
                resultPanel.revalidate(); resultPanel.repaint();
                return;
            }
            if (dest == null) {
                resultPanel.add(errorLabel("Destination attraction not found: " + destName));
                resultPanel.revalidate(); resultPanel.repaint();
                return;
            }

            LinkedList<Attraction> route = park.calculateOptimalRoute(origin, dest);

            if (route == null || route.isEmpty()) {
                resultPanel.add(errorLabel("No route found between these attractions."));
            } else {
                JLabel routeTitle = new JLabel("Suggested route (" + route.getSize() + " stops):");
                routeTitle.setFont(F_HEADING); routeTitle.setForeground(C_TEXT);
                routeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
                resultPanel.add(routeTitle);
                resultPanel.add(vgap(10));

                int rn = route.getSize();
                for (int i = 0; i < rn; i++) {
                    Attraction stop = route.get(i);
                    if (stop == null) continue;
                    JPanel stopCard = routeStop(i + 1, stop, i == rn - 1);
                    stopCard.setAlignmentX(Component.LEFT_ALIGNMENT);
                    resultPanel.add(stopCard);
                }
            }
            resultPanel.revalidate();
            resultPanel.repaint();
        });

        return wrap(root);
    }

    // ================================================================
    // FAVORITES  (visitor)
    // ================================================================
    private JPanel buildFavoritesPanel() {
        JPanel root = scrollableRoot();
        root.add(sectionTitle("My Favorites"));
        root.add(vgap(12));

        if (!(currentUser instanceof Visitor visitor)) {
            root.add(emptyState("Access restricted."));
            return wrap(root);
        }

        java.util.Set<String> favIds = visitor.getFavoriteIds();
        if (favIds.isEmpty()) {
            root.add(emptyState("No favorites saved yet.\nExplore the park and add attractions to your list."));
            return wrap(root);
        }

        for (String favId : favIds) {
            LinkedList<Attraction> all = park.listAllAttractions();
            int n = all.getSize();
            for (int i = 0; i < n; i++) {
                Attraction a = all.get(i);
                if (a != null && a.getId().equals(favId)) {
                    JPanel card = attractionCard(a);
                    card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
                    root.add(card);
                    root.add(vgap(8));
                }
            }
        }

        return wrap(root);
    }

    // ================================================================
    // VISIT HISTORY  (visitor)
    // ================================================================
    private JPanel buildHistoryPanel() {
        JPanel root = scrollableRoot();
        root.add(sectionTitle("Visit History"));
        root.add(vgap(12));

        if (!(currentUser instanceof Visitor visitor)) {
            root.add(emptyState("Access restricted."));
            return wrap(root);
        }

        LinkedList<VisitHistory> history = visitor.viewHistory();
        if (history.isEmpty()) {
            root.add(emptyState("No visits recorded yet."));
            return wrap(root);
        }

        int n = history.getSize();
        for (int i = n - 1; i >= 0; i--) {
            VisitHistory visit = history.get(i);
            if (visit == null) continue;

            JPanel card = card();
            card.setLayout(new BorderLayout(12, 0));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

            JPanel left = new JPanel();
            left.setOpaque(false);
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
            JLabel name = new JLabel(visit.getAttractionName());
            name.setFont(F_HEADING); name.setForeground(C_TEXT);
            JLabel date = new JLabel(visit.getDateTime() + "  |  Ticket: " + visit.getTicketTypeUsed());
            date.setFont(F_SMALL); date.setForeground(C_TEXT2);
            left.add(name); left.add(date);

            JLabel cost = new JLabel("$" + String.format("%.0f", visit.getIncurredCost()), SwingConstants.RIGHT);
            cost.setFont(F_HEADING);
            cost.setForeground(visit.getIncurredCost() > 0 ? C_WARNING : C_TEXT2);

            card.add(left, BorderLayout.CENTER);
            card.add(cost, BorderLayout.EAST);
            root.add(card);
            root.add(vgap(8));
        }

        return wrap(root);
    }

    // ================================================================
    // ATTRACTIONS PANEL  (operator + admin)
    // ================================================================
    private JPanel buildAttractionsPanel() {
        JPanel root = scrollableRoot();
        root.add(sectionTitle("Attraction Management"));
        root.add(vgap(12));

        LinkedList<Attraction> all = park.listAllAttractions();
        if (all.isEmpty()) {
            root.add(emptyState("No attractions registered yet."));
            return wrap(root);
        }

        int n = all.getSize();
        for (int i = 0; i < n; i++) {
            Attraction a = all.get(i);
            if (a == null) continue;

            JPanel card = card();
            card.setLayout(new BorderLayout(12, 6));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

            // left: info
            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            JLabel name = new JLabel(a.getName());
            name.setFont(F_HEADING); name.setForeground(C_TEXT);
            JLabel details = new JLabel("Type: " + a.getType() +
                    "  |  Visitors: " + a.getVisitorCount() +
                    "  |  Queue: " + a.getVirtualQueue().getSize() +
                    "  |  Wait: " + a.calculateWaitTime() + " min");
            details.setFont(F_SMALL); details.setForeground(C_TEXT2);
            info.add(name); info.add(vgap(4)); info.add(details);

            // right: status badge + change button
            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            right.setOpaque(false);
            right.add(statusBadge(a.getStatus()));

            if (currentUser.getUserRole() == UserRole.OPERATOR) {
                Operator op = (Operator) currentUser;
                if (a.getZoneId() != null && a.getZoneId().equals(op.getAssignedZoneId())) {
                    right.add(changeStateButton(a));
                }
            } else {
                right.add(changeStateButton(a));
            }

            card.add(info, BorderLayout.CENTER);
            card.add(right, BorderLayout.EAST);
            root.add(card);
            root.add(vgap(8));
        }

        return wrap(root);
    }

    // ================================================================
    // QUEUE CONTROL  (operator)
    // ================================================================
    private JPanel buildQueueControlPanel() {
        JPanel root = scrollableRoot();
        root.add(sectionTitle("Queue Control"));
        root.add(vgap(12));

        if (!(currentUser instanceof Operator op)) {
            root.add(emptyState("Access restricted."));
            return wrap(root);
        }

        String zoneId = op.getAssignedZoneId();
        if (zoneId == null) {
            root.add(emptyState("You are not assigned to any zone yet."));
            return wrap(root);
        }

        Zone zone = park.findZone(zoneId);
        if (zone == null) {
            root.add(emptyState("Zone not found."));
            return wrap(root);
        }

        LinkedList<Attraction> attractions = zone.getAttractions();
        int n = attractions.getSize();
        if (n == 0) {
            root.add(emptyState("No attractions in your zone."));
            return wrap(root);
        }

        for (int i = 0; i < n; i++) {
            Attraction a = attractions.get(i);
            if (a == null) continue;

            JPanel card = card();
            card.setLayout(new BorderLayout(12, 0));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            JLabel name = new JLabel(a.getName());
            name.setFont(F_HEADING); name.setForeground(C_TEXT);
            JLabel queueInfo = new JLabel("In queue: " + a.getVirtualQueue().getSize() +
                    " people  |  Next: " + (a.getVirtualQueue().isEmpty()
                    ? "empty" : ((Visitor) a.getVirtualQueue().peek()).getName()));
            queueInfo.setFont(F_SMALL); queueInfo.setForeground(C_TEXT2);
            info.add(name); info.add(vgap(4)); info.add(queueInfo);

            JButton btnProcess = new JButton("Process next");
            btnProcess.setFont(F_SMALL);
            btnProcess.setForeground(C_SUCCESS);
            btnProcess.setOpaque(false);
            btnProcess.setContentAreaFilled(false);
            btnProcess.setBorderPainted(true);
            btnProcess.setBorder(BorderFactory.createLineBorder(C_SUCCESS));
            btnProcess.setFocusPainted(false);
            btnProcess.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnProcess.addActionListener(e -> {
                Visitor next = op.processNextInQueue(a);
                if (next != null) {
                    JOptionPane.showMessageDialog(this,
                            "Processing: " + next.getName() +
                                    " | Priority: " + (next.getPriority() == 1 ? "Fast-Pass" : "General"),
                            "Next visitor", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Queue is empty for " + a.getName(),
                            "Queue empty", JOptionPane.INFORMATION_MESSAGE);
                }
                contentPanel.remove(contentPanel.getComponent(
                        getIndexOfCard("QUEUE_OP")));
                contentPanel.add(buildQueueControlPanel(), "QUEUE_OP");
                contentLayout.show(contentPanel, "QUEUE_OP");
            });

            card.add(info, BorderLayout.CENTER);
            card.add(btnProcess, BorderLayout.EAST);
            root.add(card);
            root.add(vgap(8));
        }

        return wrap(root);
    }

    // ================================================================
    // TECHNICAL REVIEWS  (operator)
    // ================================================================
    private JPanel buildReviewsPanel() {
        JPanel root = scrollableRoot();
        root.add(sectionTitle("Register Technical Review"));
        root.add(vgap(12));

        if (!(currentUser instanceof Operator op)) {
            root.add(emptyState("Access restricted."));
            return wrap(root);
        }

        JPanel form = card();
        form.setLayout(new GridBagLayout());
        form.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        GridBagConstraints g = gbc();

        g.gridy = 0; g.insets = ins(12, 16, 4, 16);
        form.add(smallLabel("Attraction name"), g);
        JTextField attractionField = textField("Attraction name");
        g.gridy = 1; g.insets = ins(0, 16, 10, 16);
        form.add(attractionField, g);

        g.gridy = 2; g.insets = ins(0, 16, 4, 16);
        form.add(smallLabel("Result"), g);
        JTextField resultField = textField("e.g. All systems operational");
        g.gridy = 3; g.insets = ins(0, 16, 10, 16);
        form.add(resultField, g);

        g.gridy = 4; g.insets = ins(0, 16, 4, 16);
        form.add(smallLabel("Notes (optional)"), g);
        JTextField notesField = textField("Additional notes...");
        g.gridy = 5; g.insets = ins(0, 16, 10, 16);
        form.add(notesField, g);

        JCheckBox chkSuccess = new JCheckBox("Inspection passed successfully");
        chkSuccess.setFont(F_LABEL);
        chkSuccess.setForeground(C_TEXT);
        chkSuccess.setOpaque(false);
        chkSuccess.setSelected(true);
        g.gridy = 6; g.insets = ins(0, 16, 12, 16);
        form.add(chkSuccess, g);

        root.add(form);
        root.add(vgap(12));

        JLabel msgLabel = new JLabel(" ", SwingConstants.LEFT);
        msgLabel.setFont(F_LABEL);
        msgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnSubmit = primaryButton("Submit review");
        btnSubmit.setMaximumSize(new Dimension(220, 42));
        root.add(btnSubmit);
        root.add(vgap(10));
        root.add(msgLabel);

        btnSubmit.addActionListener(e -> {
            String attractionName = attractionField.getText().trim();
            String result = resultField.getText().trim();
            if (attractionName.isEmpty() || result.isEmpty()) {
                msgLabel.setForeground(C_DANGER);
                msgLabel.setText("Please fill in attraction name and result.");
                return;
            }
            Attraction target = park.findAttractionByName(attractionName);
            if (target == null) {
                msgLabel.setForeground(C_DANGER);
                msgLabel.setText("Attraction not found: " + attractionName);
                return;
            }
            String id = "REV-" + System.currentTimeMillis();
            String dateTime = java.time.LocalDateTime.now().toString();
            TechnicalInspection review = new TechnicalInspection(
                    id, dateTime, op.getId(), op.getName(),
                    result, notesField.getText().trim(), chkSuccess.isSelected()
            );
            op.registerInspection(target, review);
            msgLabel.setForeground(C_SUCCESS);
            msgLabel.setText("Review submitted for " + target.getName() + ".");
            attractionField.setText("");
            resultField.setText("");
            notesField.setText("");
            chkSuccess.setSelected(true);
        });

        return wrap(root);
    }

    // ================================================================
    // DASHBOARD  (admin)
    // ================================================================
    private JPanel buildDashboardPanel() {
        JPanel root = scrollableRoot();
        root.add(sectionTitle("Dashboard"));
        root.add(vgap(12));

        // stat row
        LinkedList<Attraction> all = park.listAllAttractions();
        int total = all.getSize();
        int active = 0;
        int maintenance = 0;
        int closed = 0;
        for (int i = 0; i < total; i++) {
            Attraction a = all.get(i);
            if (a == null) continue;
            switch (a.getStatus()) {
                case ACTIVE -> active++;
                case MAINTENANCE -> maintenance++;
                case CLOSED -> closed++;
            }
        }

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 14, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        statsRow.add(statCard("Visitors", String.valueOf(park.getCurrentVisitors()), C_PRIMARY));
        statsRow.add(statCard("Active attractions", String.valueOf(active), C_SUCCESS));
        statsRow.add(statCard("In maintenance", String.valueOf(maintenance), C_WARNING));
        statsRow.add(statCard("Closed", String.valueOf(closed), C_DANGER));
        root.add(statsRow);
        root.add(vgap(20));

        // alert banner
        if (park.isWeatherAlertActive()) {
            JPanel alertBanner = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
            alertBanner.setBackground(new Color(60, 30, 10));
            alertBanner.setBorder(BorderFactory.createLineBorder(C_WARNING));
            alertBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            JLabel alertLabel = new JLabel("Weather alert active: " + park.getCurrentAlert());
            alertLabel.setFont(F_HEADING); alertLabel.setForeground(C_WARNING);
            alertBanner.add(alertLabel);
            root.add(alertBanner);
            root.add(vgap(16));
        }

        // zones overview
        root.add(sectionTitle("Zones"));
        root.add(vgap(10));
        LinkedList<Zone> zones = park.getZones();
        int nz = zones.getSize();
        if (nz == 0) {
            root.add(emptyState("No zones configured yet."));
        } else {
            JPanel zoneGrid = new JPanel(new GridLayout(0, 2, 12, 10));
            zoneGrid.setOpaque(false);
            zoneGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
            for (int i = 0; i < nz; i++) {
                Zone z = zones.get(i);
                if (z != null) zoneGrid.add(zoneCard(z));
            }
            root.add(zoneGrid);
        }

        return wrap(root);
    }

    // ================================================================
    // STAFF PANEL  (admin)
    // ================================================================
    private JPanel buildStaffPanel() {
        JPanel root = scrollableRoot();
        root.add(sectionTitle("Staff Management"));
        root.add(vgap(12));

        // assign operator form
        JPanel form = card();
        form.setLayout(new GridBagLayout());
        form.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        GridBagConstraints g = gbc();

        g.gridy = 0; g.insets = ins(12, 16, 4, 16);
        form.add(smallLabel("Operator email"), g);
        JTextField opEmailField = textField("operator@example.com");
        g.gridy = 1; g.insets = ins(0, 16, 10, 16);
        form.add(opEmailField, g);

        g.gridy = 2; g.insets = ins(0, 16, 4, 16);
        form.add(smallLabel("Zone ID"), g);
        JTextField zoneIdField = textField("ZONE-001");
        g.gridy = 3; g.insets = ins(0, 16, 12, 16);
        form.add(zoneIdField, g);

        root.add(form);
        root.add(vgap(10));

        JLabel msgLabel = new JLabel(" ");
        msgLabel.setFont(F_LABEL);
        msgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnAssign = primaryButton("Assign operator to zone");
        btnAssign.setMaximumSize(new Dimension(260, 42));
        root.add(btnAssign);
        root.add(vgap(8));
        root.add(msgLabel);
        root.add(vgap(20));

        btnAssign.addActionListener(e -> {
            String opEmail = opEmailField.getText().trim();
            String zoneId  = zoneIdField.getText().trim();
            if (opEmail.isEmpty() || zoneId.isEmpty()) {
                msgLabel.setForeground(C_DANGER);
                msgLabel.setText("Please fill in both fields.");
                return;
            }
            Operator op = findOperatorByEmail(opEmail);
            Zone zone   = park.findZone(zoneId);
            if (op == null) {
                msgLabel.setForeground(C_DANGER);
                msgLabel.setText("Operator not found: " + opEmail);
                return;
            }
            if (zone == null) {
                msgLabel.setForeground(C_DANGER);
                msgLabel.setText("Zone not found: " + zoneId);
                return;
            }
            Administrator adm = (Administrator) currentUser;
            boolean ok = adm.assignOperator(op, zone);
            if (ok) {
                msgLabel.setForeground(C_SUCCESS);
                msgLabel.setText("Operator " + op.getName() + " assigned to zone " + zone.getName() + ".");
                opEmailField.setText(""); zoneIdField.setText("");
            } else {
                msgLabel.setForeground(C_DANGER);
                msgLabel.setText("Assignment failed. Operator may already have a zone.");
            }
        });

        // operator list
        root.add(sectionTitle("Registered operators"));
        root.add(vgap(10));
        LinkedList<Operator> operators = park.getOperators();
        int n = operators.getSize();
        if (n == 0) {
            root.add(emptyState("No operators registered yet."));
        } else {
            for (int i = 0; i < n; i++) {
                Operator op = operators.get(i);
                if (op == null) continue;
                JPanel card = card();
                card.setLayout(new BorderLayout());
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                JLabel name = new JLabel(op.getName() + "  |  " + op.getEmail());
                name.setFont(F_BODY); name.setForeground(C_TEXT);
                JLabel zone = new JLabel(op.hasAssignedZone()
                        ? "Zone: " + op.getAssignedZoneId() : "Unassigned", SwingConstants.RIGHT);
                zone.setFont(F_SMALL);
                zone.setForeground(op.hasAssignedZone() ? C_SUCCESS : C_WARNING);
                card.add(name, BorderLayout.WEST);
                card.add(zone, BorderLayout.EAST);
                root.add(card);
                root.add(vgap(8));
            }
        }

        return wrap(root);
    }

    // ================================================================
    // WEATHER ALERT  (admin)
    // ================================================================
    private JPanel buildWeatherPanel() {
        JPanel root = scrollableRoot();
        root.add(sectionTitle("Weather Alert Control"));
        root.add(vgap(12));

        // current status
        JPanel statusCard = card();
        statusCard.setLayout(new BorderLayout(16, 0));
        statusCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel statusTitle = new JLabel("Current alert status");
        statusTitle.setFont(F_HEADING); statusTitle.setForeground(C_TEXT2);
        JLabel statusValue = new JLabel(
                park.isWeatherAlertActive()
                        ? "ACTIVE: " + park.getCurrentAlert()
                        : "No active alert"
        );
        statusValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusValue.setForeground(park.isWeatherAlertActive() ? C_WARNING : C_SUCCESS);
        JPanel statusInfo = new JPanel();
        statusInfo.setOpaque(false);
        statusInfo.setLayout(new BoxLayout(statusInfo, BoxLayout.Y_AXIS));
        statusInfo.add(statusTitle); statusInfo.add(vgap(4)); statusInfo.add(statusValue);
        statusCard.add(statusInfo, BorderLayout.CENTER);
        root.add(statusCard);
        root.add(vgap(16));

        // activate buttons
        root.add(sectionTitle("Activate alert"));
        root.add(vgap(10));

        JPanel alertButtons = new JPanel(new GridLayout(1, 2, 12, 0));
        alertButtons.setOpaque(false);
        alertButtons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton btnStorm = alertButton("Electrical Storm", C_DANGER);
        JButton btnRain  = alertButton("Heavy Rain", C_WARNING);

        btnStorm.addActionListener(e -> {
            park.activateWeatherAlert(AlertType.ELECTRICAL_STORM);
            JOptionPane.showMessageDialog(this,
                    "Electrical storm alert activated.\nAquatic and mechanical attractions have been closed.",
                    "Alert activated", JOptionPane.WARNING_MESSAGE);
            refreshWeatherPanel();
        });

        btnRain.addActionListener(e -> {
            park.activateWeatherAlert(AlertType.HEAVY_RAIN);
            JOptionPane.showMessageDialog(this,
                    "Heavy rain alert activated.\nAquatic and mechanical attractions have been closed.",
                    "Alert activated", JOptionPane.WARNING_MESSAGE);
            refreshWeatherPanel();
        });

        alertButtons.add(btnStorm);
        alertButtons.add(btnRain);
        root.add(alertButtons);
        root.add(vgap(14));

        // deactivate button
        if (park.isWeatherAlertActive()) {
            JButton btnDeactivate = new JButton("Deactivate current alert");
            styleDestructiveButton(btnDeactivate);
            btnDeactivate.setMaximumSize(new Dimension(260, 42));
            btnDeactivate.addActionListener(e -> {
                park.deactivateWeatherAlert();
                JOptionPane.showMessageDialog(this,
                        "Alert deactivated. Attractions can now be reopened.",
                        "Alert deactivated", JOptionPane.INFORMATION_MESSAGE);
                refreshWeatherPanel();
            });
            root.add(btnDeactivate);
        }

        return wrap(root);
    }

    // ================================================================
    // REPORTS  (admin)
    // ================================================================
    private JPanel buildReportsPanel() {
        JPanel root = scrollableRoot();
        root.add(sectionTitle("Reports"));
        root.add(vgap(12));

        Report report = park.getDailyReport();
        if (report == null) {
            root.add(emptyState("No active session report.\nStart the park session first."));

            JButton btnStart = primaryButton("Start new session");
            btnStart.setMaximumSize(new Dimension(220, 42));
            btnStart.addActionListener(e -> {
                park.startDay();
                refreshPanel("REPORTS", buildReportsPanel());
            });
            root.add(vgap(12));
            root.add(btnStart);
            return wrap(root);
        }

        // stats
        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setOpaque(false);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        stats.add(statCard("Visitors", String.valueOf(report.getTotalVisitors()), C_PRIMARY));
        stats.add(statCard("Daily revenue", "$" + String.format("%.0f", report.getDailyRevenue()), C_SUCCESS));
        stats.add(statCard("Incidents", String.valueOf(report.getTotalIncidents()), C_DANGER));
        root.add(stats);
        root.add(vgap(20));

        JPanel stats2 = new JPanel(new GridLayout(1, 3, 14, 0));
        stats2.setOpaque(false);
        stats2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        stats2.add(statCard("Climate closures", String.valueOf(report.getWeatherClosures()), C_WARNING));
        stats2.add(statCard("Maintenance alerts", String.valueOf(report.getMaintenanceAlerts()), C_WARNING));
        stats2.add(statCard("Avg wait time", String.format("%.1f", report.getAverageWaitTime()) + " min", C_PRIMARY));
        root.add(stats2);
        root.add(vgap(20));

        // top attractions
        root.add(sectionTitle("Most visited attractions"));
        root.add(vgap(10));
        LinkedList<Attraction> top = report.getMostVisitedAttractions();
        int limit = Math.min(5, top.getSize());
        for (int i = 0; i < limit; i++) {
            Attraction a = top.get(i);
            if (a == null) continue;
            JPanel row = card();
            row.setLayout(new BorderLayout());
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
            JLabel rank = new JLabel("#" + (i + 1) + "  " + a.getName());
            rank.setFont(F_BODY); rank.setForeground(C_TEXT);
            JLabel count = new JLabel(a.getVisitorCount() + " visitors", SwingConstants.RIGHT);
            count.setFont(F_HEADING); count.setForeground(C_PRIMARY);
            row.add(rank,  BorderLayout.WEST);
            row.add(count, BorderLayout.EAST);
            root.add(row); root.add(vgap(6));
        }

        root.add(vgap(16));

        JButton btnClose = primaryButton("Close session & export report");
        btnClose.setMaximumSize(new Dimension(280, 44));
        btnClose.addActionListener(e -> {
            Report closed = park.closeDay();
            JTextArea area = new JTextArea(closed.export());
            area.setEditable(false);
            area.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(500, 360));
            JOptionPane.showMessageDialog(this, scroll, "Session Report", JOptionPane.INFORMATION_MESSAGE);
            refreshPanel("REPORTS", buildReportsPanel());
        });
        root.add(btnClose);

        return wrap(root);
    }

    // ================================================================
    // GRAPH PANEL  (admin)
    // ================================================================
    private JPanel buildGraphPanel() {
        JPanel root = scrollableRoot();
        root.add(sectionTitle("Park Graph"));
        root.add(vgap(12));

        JPanel infoCard = card();
        infoCard.setLayout(new GridBagLayout());
        infoCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        GridBagConstraints g = gbc();

        g.gridy = 0; g.insets = ins(12, 16, 4, 16);
        JLabel nodesLabel = new JLabel("Total nodes (attractions): " + park.getMapGraph().getNumNodes());
        nodesLabel.setFont(F_BODY); nodesLabel.setForeground(C_TEXT);
        infoCard.add(nodesLabel, g);

        LinkedList<GraphNode<Attraction>> clusters = park.detectClusters();
        g.gridy = 1; g.insets = ins(0, 16, 12, 16);
        JLabel clustersLabel = new JLabel("Connected clusters: " + clusters.getSize());
        clustersLabel.setFont(F_BODY); clustersLabel.setForeground(C_TEXT);
        infoCard.add(clustersLabel, g);

        root.add(infoCard);
        root.add(vgap(16));

        // adjacency list
        root.add(sectionTitle("Adjacency list"));
        root.add(vgap(10));

        LinkedList<GraphNode<Attraction>> nodes = park.getMapGraph().getNodes();
        int n = nodes.getSize();
        if (n == 0) {
            root.add(emptyState("No connections in the park graph yet."));
        } else {
            for (int i = 0; i < n; i++) {
                GraphNode<Attraction> node = nodes.get(i);
                if (node == null) continue;

                JPanel card = card();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

                JLabel nodeName = new JLabel(node.getData().getName());
                nodeName.setFont(F_HEADING); nodeName.setForeground(C_TEXT);
                card.add(nodeName);

                LinkedList<GraphEdge<Attraction>> edges = node.getEdges();
                int ne = edges.getSize();
                if (ne == 0) {
                    JLabel noEdges = new JLabel("  No connections");
                    noEdges.setFont(F_SMALL); noEdges.setForeground(C_TEXT2);
                    card.add(noEdges);
                } else {
                    for (int j = 0; j < ne; j++) {
                        GraphEdge<Attraction> edge = edges.get(j);
                        if (edge == null) continue;
                        JLabel edgeLabel = new JLabel("  -> " + edge.getDestination().getData().getName() +
                                "  (" + edge.getWeight() + " m)");
                        edgeLabel.setFont(F_SMALL); edgeLabel.setForeground(C_TEXT2);
                        card.add(edgeLabel);
                    }
                }
                root.add(card);
                root.add(vgap(8));
            }
        }

        return wrap(root);
    }

    // ================================================================
    // small reusable components
    // ================================================================
    private JPanel attractionCard(Attraction a) {
        JPanel card = card();
        card.setLayout(new BorderLayout(8, 4));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(a.getName());
        name.setFont(F_HEADING); name.setForeground(C_TEXT);
        JLabel details = new JLabel(a.getType() + "  |  Wait: " + a.calculateWaitTime() + " min" +
                "  |  Queue: " + a.getVirtualQueue().getSize());
        details.setFont(F_SMALL); details.setForeground(C_TEXT2);
        info.add(name); info.add(vgap(3)); info.add(details);

        card.add(info, BorderLayout.CENTER);
        card.add(statusBadge(a.getStatus()), BorderLayout.EAST);
        return card;
    }

    private JPanel zoneCard(Zone z) {
        JPanel card = card();
        card.setLayout(new BorderLayout(8, 4));

        JLabel name = new JLabel(z.getName());
        name.setFont(F_HEADING); name.setForeground(C_TEXT);

        JLabel stats = new JLabel(
                "Visitors: " + z.getCurrentVisitors() + "/" + z.getMaxCapacity() +
                        "  |  Attractions: " + z.getAttractions().getSize() +
                        "  |  Operators: " + z.getOperatorCount()
        );
        stats.setFont(F_SMALL); stats.setForeground(C_TEXT2);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(name); info.add(vgap(3)); info.add(stats);

        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private JPanel routeStop(int num, Attraction a, boolean isLast) {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel numLabel = new JLabel(String.valueOf(num));
        numLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        numLabel.setForeground(isLast ? C_SUCCESS : C_PRIMARY);
        numLabel.setPreferredSize(new Dimension(30, 0));
        numLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(a.getName() + (isLast ? "  (destination)" : ""));
        name.setFont(F_BODY); name.setForeground(C_TEXT);
        JLabel sub = new JLabel(a.getType() + "  |  Status: " + a.getStatus());
        sub.setFont(F_SMALL); sub.setForeground(C_TEXT2);
        info.add(name); info.add(sub);

        p.add(numLabel, BorderLayout.WEST);
        p.add(info,     BorderLayout.CENTER);
        return p;
    }

    private JLabel statusBadge(AttractionStatus state) {
        String text  = switch (state) {
            case ACTIVE -> "ACTIVE";
            case MAINTENANCE -> "MAINTENANCE";
            case CLOSED -> "CLOSED";
        };
        Color color = switch (state) {
            case ACTIVE -> C_SUCCESS;
            case MAINTENANCE -> C_WARNING;
            case CLOSED -> C_DANGER;
        };
        JLabel badge = new JLabel(text);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(color);
        badge.setBorder(BorderFactory.createLineBorder(color));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setPreferredSize(new Dimension(100, 24));
        return badge;
    }

    private JButton changeStateButton(Attraction a) {
        JButton btn = new JButton("Change state");
        btn.setFont(F_SMALL);
        btn.setForeground(C_TEXT2);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createLineBorder(C_BORDER));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            String[] options = {"ACTIVE", "MAINTENANCE", "CLOSED"};
            int choice = JOptionPane.showOptionDialog(this,
                    "Select new state for: " + a.getName(),
                    "Change state", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (choice < 0) return;
            String motivo = null;
            if (choice == 2) {
                motivo = JOptionPane.showInputDialog(this, "Reason for closure:");
                if (motivo == null) return;
            }
            AttractionStatus newState = switch (choice) {
                case 0 -> AttractionStatus.ACTIVE;
                case 1 -> AttractionStatus.MAINTENANCE;
                default -> AttractionStatus.CLOSED;
            };
            a.changeStatus(newState, motivo);
            refreshPanel("ATTRACTIONS", buildAttractionsPanel());
        });
        return btn;
    }

    private JPanel statCard(String label, String value, Color color) {
        JPanel p = card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        val.setForeground(color);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_SMALL); lbl.setForeground(C_TEXT2);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(vgap(14)); p.add(val); p.add(vgap(4)); p.add(lbl); p.add(vgap(14));
        p.setBorder(BorderFactory.createCompoundBorder(
                p.getBorder(), BorderFactory.createEmptyBorder(0, 16, 0, 16)
        ));
        return p;
    }

    private JButton alertButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? color.darker()
                        : new Color(color.getRed()/4, color.getGreen()/4, color.getBlue()/4);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN); btn.setForeground(color);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 44));
        return btn;
    }

    private void styleDestructiveButton(JButton btn) {
        btn.setFont(F_BTN);
        btn.setForeground(C_DANGER);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createLineBorder(C_DANGER));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ================================================================
    // layout helpers
    // ================================================================
    private JPanel card() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(C_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(C_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 10, 10));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private JPanel scrollableRoot() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        return p;
    }

    private JPanel wrap(JPanel content) {
        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setOpaque(false);
        outer.add(scroll, BorderLayout.CENTER);
        return outer;
    }

    private JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_TITLE); lbl.setForeground(C_TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel smallLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_LABEL); lbl.setForeground(C_TEXT2);
        return lbl;
    }

    private JLabel emptyState(String text) {
        JLabel lbl = new JLabel("<html><div style='text-align:center'>" +
                text.replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
        lbl.setFont(F_BODY); lbl.setForeground(C_TEXT2);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JLabel errorLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(F_LABEL); lbl.setForeground(C_DANGER);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private Component vgap(int size) {
        return Box.createRigidArea(new Dimension(0, size));
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
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN); btn.setForeground(Color.WHITE);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private JTextField textField(String hint) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(C_FIELD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g3 = (Graphics2D) g.create();
                    g3.setColor(C_TEXT2); g3.setFont(F_FIELD);
                    FontMetrics fm = g3.getFontMetrics();
                    g3.drawString(hint, 10, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                    g3.dispose();
                }
            }
        };
        f.setOpaque(false); f.setFont(F_FIELD);
        f.setForeground(C_TEXT); f.setCaretColor(C_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        f.setPreferredSize(new Dimension(0, 38));
        return f;
    }

    private GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        return g;
    }

    private Insets ins(int t, int l, int b, int r) { return new Insets(t, l, b, r); }

    // ================================================================
    // utility methods
    // ================================================================
    private Operator findOperatorByEmail(String email) {
        LinkedList<Operator> ops = park.getOperators();
        int n = ops.getSize();
        for (int i = 0; i < n; i++) {
            Operator op = ops.get(i);
            if (op != null && op.getEmail().equalsIgnoreCase(email)) return op;
        }
        return null;
    }

    private void refreshPanel(String key, JPanel newPanel) {
        contentPanel.add(newPanel, key);
        contentLayout.show(contentPanel, key);
        highlightNavButton(key);
    }

    private void refreshWeatherPanel() {
        refreshPanel("WEATHER", buildWeatherPanel());
    }

    private int getIndexOfCard(String key) {
        for (int i = 0; i < contentPanel.getComponentCount(); i++) {
            Component c = contentPanel.getComponent(i);
            if (key.equals(((JPanel) c).getName())) return i;
        }
        return -1;
    }
}