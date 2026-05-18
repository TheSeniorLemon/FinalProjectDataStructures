package co.edu.uniquindio.techpark.view;

import co.edu.uniquindio.techpark.model.entities.*;
import co.edu.uniquindio.techpark.model.enums.AttractionStatus;
import co.edu.uniquindio.techpark.model.structures.GraphEdge;
import co.edu.uniquindio.techpark.model.structures.GraphNode;
import co.edu.uniquindio.techpark.model.structures.LinkedList;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

public class MapPanel extends JPanel {

    // ----------------------------------------------------------------
    // theme
    // ----------------------------------------------------------------
    private static final Color C_BG = new Color(10, 14, 20);
    private static final Color C_CTRL_BAR = new Color(15, 20, 28);
    private static final Color C_BORDER = new Color(40, 50, 64);
    private static final Color C_TEXT = new Color(224, 232, 244);
    private static final Color C_TEXT2 = new Color(120, 134, 154);
    private static final Color C_MAP_BG = new Color(20, 34, 22);

    // node status colors
    private static final Color NODE_ACTIVE = new Color(52, 199, 100);
    private static final Color NODE_MAINT = new Color(255, 180, 50);
    private static final Color NODE_CLOSED = new Color(240, 80,  80);
    private static final Color NODE_BORDER = new Color(200, 220, 255);

    // edge colors
    private static final Color EDGE_NORMAL = new Color(80, 100, 120);
    private static final Color EDGE_ROUTE = new Color(255, 220, 50);

    // zone area fill + border colors
    private static final Object[][] ZONE_STYLES = {
            {"ZONE-001", "Adventure World", 45, 40, 265, 240, new Color(255,100, 50, 35), new Color(255,140, 60)},
            {"ZONE-002", "Aqua Kingdom", 380,  40, 270, 250, new Color( 50,150,255, 35), new Color( 60,170,255)},
            {"ZONE-003", "Kids Land", 40, 355, 305, 210, new Color(100,220,100, 35), new Color( 80,200,100)},
            {"ZONE-004", "Tech Arena", 410, 355, 300, 200, new Color(180, 80,255, 35), new Color(180, 90,255)},
    };

    // fonts
    private static final Font F_ZONE = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font F_NODE = new Font("Segoe UI", Font.BOLD, 10);
    private static final Font F_EDGE = new Font("Segoe UI", Font.PLAIN, 9);
    private static final Font F_CTRL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_BTN = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_ICON = new Font("Segoe UI", Font.BOLD, 11);

    private static final int NODE_R = 24; // node radius in world coords

    // ----------------------------------------------------------------
    // state
    // ----------------------------------------------------------------
    private final Park park;
    private final Map<String, int[]> positions = new HashMap<>();

    private double scale = 1.0;
    private double panX = 40.0;
    private double panY = 20.0;
    private boolean dragging;
    private int dragMouseX, dragMouseY;
    private double dragPanX, dragPanY;

    private LinkedList<Attraction> currentRoute = null;
    private final Set<String>      routeEdgeKeys = new HashSet<>();
    private String hoveredId = null;

    private JComboBox<String> fromCombo, toCombo;
    private JLabel routeInfoLabel;
    private JPanel canvas;

    // ----------------------------------------------------------------
    // constructor
    // ----------------------------------------------------------------
    public MapPanel(Park park) {
        this.park = park;
        loadDefaultPositions();
        buildLayout();
    }

    // ----------------------------------------------------------------
    // public API - set or override a node position
    // ----------------------------------------------------------------
    public void setPosition(String attractionId, int x, int y) {
        positions.put(attractionId, new int[]{x, y});
        if (canvas != null) canvas.repaint();
    }

    // ----------------------------------------------------------------
    // default world-coordinate layout for the 12 demo attractions
    // ----------------------------------------------------------------
    private void loadDefaultPositions() {
        // Adventure World
        positions.put("ATT-001", new int[]{120, 140}); // Extreme Roller Coaster
        positions.put("ATT-002", new int[]{240,  82}); // Sky Drop Tower
        positions.put("ATT-003", new int[]{172, 228}); // Haunted Mine
        // Aqua Kingdom
        positions.put("ATT-004", new int[]{452, 100}); // Titan Water Slide
        positions.put("ATT-005", new int[]{566, 160}); // Lazy River
        positions.put("ATT-006", new int[]{506, 255}); // Wave Pool
        // Kids Land
        positions.put("ATT-007", new int[]{148, 412}); // Mini Carousel
        positions.put("ATT-008", new int[]{282, 446}); // Kiddie Coaster
        positions.put("ATT-009", new int[]{100, 510}); // Puppet Theater
        // Tech Arena
        positions.put("ATT-010", new int[]{536, 395}); // VR Escape Room
        positions.put("ATT-011", new int[]{644, 460}); // Racing Simulators
        positions.put("ATT-012", new int[]{492, 500}); // Laser Tag Arena
    }

    // ================================================================
    // UI construction
    // ================================================================
    private void buildLayout() {
        setLayout(new BorderLayout(0, 0));
        setBackground(C_BG);
        add(buildControlBar(), BorderLayout.NORTH);
        add(buildCanvas(), BorderLayout.CENTER);
        add(buildLegend(), BorderLayout.SOUTH);
    }

    // ----------------------------------------------------------------
    // control bar
    // ----------------------------------------------------------------
    private JPanel buildControlBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        bar.setBackground(C_CTRL_BAR);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, C_BORDER));

        bar.add(ctrlLabel("From:"));
        fromCombo = styledCombo(200);
        bar.add(fromCombo);

        bar.add(ctrlLabel("To:"));
        toCombo = styledCombo(200);
        bar.add(toCombo);

        JButton btnFind = colorBtn("Find route", new Color(56, 130, 255));
        JButton btnClear = colorBtn("Clear route", new Color(80,  90, 110));
        JButton btnResetV = colorBtn("Reset view", new Color(60,  75,  95));
        JButton btnRefresh = colorBtn("Refresh", new Color(60,  75,  95));

        bar.add(btnFind); bar.add(btnClear); bar.add(btnResetV); bar.add(btnRefresh);

        routeInfoLabel = new JLabel("  Click a node to select it as origin / destination");
        routeInfoLabel.setFont(F_SMALL); routeInfoLabel.setForeground(C_TEXT2);
        bar.add(routeInfoLabel);

        refreshCombos();

        btnFind.addActionListener(e -> calculateRoute());
        btnClear.addActionListener(e -> { currentRoute = null; routeEdgeKeys.clear(); routeInfoLabel.setForeground(C_TEXT2); routeInfoLabel.setText("Route cleared."); canvas.repaint(); });
        btnResetV.addActionListener(e -> { scale = 1.0; panX = 40; panY = 20; canvas.repaint(); });
        btnRefresh.addActionListener(e -> refresh());

        return bar;
    }

    // ----------------------------------------------------------------
    // canvas (main drawing surface)
    // ----------------------------------------------------------------
    private JPanel buildCanvas() {
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                paintMap(g2);
                g2.dispose();
            }
        };
        canvas.setBackground(C_MAP_BG);

        // scroll to zoom
        canvas.addMouseWheelListener(e -> {
            double factor   = e.getWheelRotation() < 0 ? 1.12 : 0.893;
            double newScale = Math.max(0.35, Math.min(4.0, scale * factor));
            double mx = e.getX(), my = e.getY();
            panX  = mx - (mx - panX) * (newScale / scale);
            panY  = my - (my - panY) * (newScale / scale);
            scale = newScale;
            canvas.repaint();
        });

        // drag to pan + click to select
        canvas.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) return;
                dragging = true;
                dragMouseX = e.getX(); dragMouseY = e.getY();
                dragPanX = panX; dragPanY = panY;
                canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
            @Override public void mouseReleased(MouseEvent e) {
                dragging = false;
                canvas.setCursor(hoveredId != null
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) return;
                String hit = hitTest(e.getX(), e.getY());
                if (hit != null) onNodeClicked(hit);
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (!dragging) return;
                panX = dragPanX + (e.getX() - dragMouseX);
                panY = dragPanY + (e.getY() - dragMouseY);
                canvas.repaint();
            }
            @Override public void mouseMoved(MouseEvent e) {
                String prev = hoveredId;
                hoveredId = hitTest(e.getX(), e.getY());
                canvas.setCursor(hoveredId != null
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
                if (!Objects.equals(prev, hoveredId)) canvas.repaint();
            }
        });

        return canvas;
    }

    // ----------------------------------------------------------------
    // legend bar
    // ----------------------------------------------------------------
    private JPanel buildLegend() {
        JPanel leg = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 6));
        leg.setBackground(C_CTRL_BAR);
        leg.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER));
        leg.add(dotLegend(NODE_ACTIVE, "Active"));
        leg.add(dotLegend(NODE_MAINT, "Maintenance"));
        leg.add(dotLegend(NODE_CLOSED, "Closed"));
        leg.add(vSep());
        leg.add(lineLegend(EDGE_NORMAL, 2f,  "Path"));
        leg.add(lineLegend(EDGE_ROUTE, 3f,  "Shortest route (Dijkstra)"));
        leg.add(vSep());
        leg.add(dotLegend(new Color(56, 130, 255), "Queue count"));
        leg.add(vSep());
        JLabel hint = new JLabel("Scroll = zoom  |  Drag = pan  |  Click node = select");
        hint.setFont(F_SMALL); hint.setForeground(new Color(70, 80, 100));
        leg.add(hint);
        return leg;
    }

    // ================================================================
    // painting
    // ================================================================
    private void paintMap(Graphics2D g2) {
        // apply view transform
        g2.translate(panX, panY);
        g2.scale(scale, scale);

        drawGrid(g2);
        drawZoneAreas(g2);
        drawEdges(g2, false);    // normal edges
        drawEdges(g2, true);     // highlighted route on top
        drawNodes(g2);
    }

    // subtle dot grid
    private void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(255, 255, 255, 10));
        for (int x = 0; x <= 760; x += 40)
            for (int y = 0; y <= 600; y += 40)
                g2.fillOval(x - 1, y - 1, 2, 2);
    }

    // zone background rectangles with labels
    private void drawZoneAreas(Graphics2D g2) {
        for (Object[] z : ZONE_STYLES) {
            int zx = (int) z[2];
            int zy = (int) z[3];
            int zw = (int) z[4];
            int zh = (int) z[5];
            Color fill = (Color) z[6];
            Color accent = (Color) z[7];

            g2.setColor(fill);
            g2.fillRoundRect(zx, zy, zw, zh, 20, 20);

            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 65));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(zx, zy, zw, zh, 20, 20);

            g2.setFont(F_ZONE);
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 190));
            g2.drawString((String) z[1], zx + 10, zy + 20);
        }
    }

    // draw all edges; routeOnly=true draws only highlighted route edges
    private void drawEdges(Graphics2D g2, boolean routeOnly) {
        LinkedList<GraphNode<Attraction>> nodes = park.getMapGraph().getNodes();
        int n = nodes.getSize();
        Set<String> drawn = new HashSet<>();

        for (int i = 0; i < n; i++) {
            GraphNode<Attraction> fromNode = nodes.get(i);
            if (fromNode == null) continue;
            Attraction from = fromNode.getData();
            int[] pF = positions.get(from.getId());
            if (pF == null) continue;

            LinkedList<GraphEdge<Attraction>> edges = fromNode.getEdges();
            int ne = edges.getSize();

            for (int j = 0; j < ne; j++) {
                GraphEdge<Attraction> edge = edges.get(j);
                if (edge == null) continue;
                Attraction to = edge.getDestination().getData();
                int[] pT = positions.get(to.getId());
                if (pT == null) continue;

                // dedup undirected
                String[] ids = {from.getId(), to.getId()};
                Arrays.sort(ids);
                String key = ids[0] + "|" + ids[1];
                if (drawn.contains(key)) continue;
                drawn.add(key);

                boolean isRoute = routeEdgeKeys.contains(key);
                if (routeOnly && !isRoute) continue;
                if (!routeOnly && isRoute) continue;

                int x1 = pF[0], y1 = pF[1];
                int x2 = pT[0], y2 = pT[1];

                if (isRoute) {
                    // glow
                    g2.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(new Color(255, 220, 50, 70));
                    g2.drawLine(x1, y1, x2, y2);
                    // line
                    g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(EDGE_ROUTE);
                    g2.drawLine(x1, y1, x2, y2);
                } else {
                    g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(EDGE_NORMAL);
                    g2.drawLine(x1, y1, x2, y2);

                    // distance label at midpoint
                    int mx = (x1 + x2) / 2;
                    int my = (y1 + y2) / 2;
                    String wStr = (int) edge.getWeight() + "m";
                    g2.setFont(F_EDGE);
                    FontMetrics fm = g2.getFontMetrics();
                    int tw = fm.stringWidth(wStr);
                    g2.setColor(new Color(10, 14, 20, 180));
                    g2.fillRoundRect(mx - tw / 2 - 3, my - fm.getAscent() + 1, tw + 6, fm.getHeight(), 3, 3);
                    g2.setColor(new Color(130, 150, 170));
                    g2.drawString(wStr, mx - tw / 2, my + 1);
                }
            }
        }
    }

    // draw all attraction nodes
    private void drawNodes(Graphics2D g2) {
        LinkedList<Attraction> all = park.listAllAttractions();
        int n = all.getSize();

        for (int i = 0; i < n; i++) {
            Attraction a = all.get(i);
            if (a == null) continue;
            int[] pos = positions.get(a.getId());
            if (pos == null) continue;

            int cx = pos[0], cy = pos[1];
            boolean hovered = a.getId().equals(hoveredId);
            boolean onRoute = isOnRoute(a);

            Color baseColor = switch (a.getStatus()) {
                case ACTIVE -> NODE_ACTIVE;
                case MAINTENANCE -> NODE_MAINT;
                case CLOSED -> NODE_CLOSED;
            };

            // outer glow
            if (hovered || onRoute) {
                Color glow = onRoute
                        ? new Color(255, 220, 50,  90)
                        : new Color(255, 255, 255, 55);
                g2.setColor(glow);
                g2.fillOval(cx - NODE_R - 7, cy - NODE_R - 7, (NODE_R + 7) * 2, (NODE_R + 7) * 2);
            }

            // drop shadow
            g2.setColor(new Color(0, 0, 0, 90));
            g2.fillOval(cx - NODE_R + 2, cy - NODE_R + 3, NODE_R * 2, NODE_R * 2);

            // outer circle (dark version of color)
            g2.setColor(baseColor.darker().darker());
            g2.fillOval(cx - NODE_R, cy - NODE_R, NODE_R * 2, NODE_R * 2);

            // inner filled circle
            int inner = NODE_R - 4;
            g2.setColor(baseColor);
            g2.fillOval(cx - inner, cy - inner, inner * 2, inner * 2);

            // border ring
            g2.setStroke(new BasicStroke(onRoute ? 3f : 1.8f));
            g2.setColor(onRoute ? EDGE_ROUTE : new Color(NODE_BORDER.getRed(), NODE_BORDER.getGreen(), NODE_BORDER.getBlue(), hovered ? 255 : 160));
            g2.drawOval(cx - NODE_R, cy - NODE_R, NODE_R * 2, NODE_R * 2);

            // status letter inside
            String icon = switch (a.getStatus()) {
                case ACTIVE -> "A";
                case MAINTENANCE -> "M";
                case CLOSED -> "X";
            };
            g2.setFont(F_ICON);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(new Color(10, 14, 20, 210));
            g2.drawString(icon, cx - fm.stringWidth(icon) / 2, cy + fm.getAscent() / 2 - 1);

            // name label below node
            g2.setFont(F_NODE);
            fm = g2.getFontMetrics();
            String label = a.getName();
            int tw = fm.stringWidth(label);
            int lx = cx - tw / 2;
            int ly = cy + NODE_R + 4;

            g2.setColor(new Color(10, 14, 20, 200));
            g2.fillRoundRect(lx - 4, ly, tw + 8, fm.getHeight() + 2, 4, 4);
            g2.setColor((hovered || onRoute) ? Color.WHITE : C_TEXT);
            g2.drawString(label, lx, ly + fm.getAscent());

            // queue badge (top-right corner of node)
            int qSize = a.getVirtualQueue().getSize();
            if (qSize > 0) {
                int bx = cx + NODE_R - 5;
                int by = cy - NODE_R + 3;
                g2.setColor(new Color(56, 130, 255));
                g2.fillOval(bx - 9, by - 9, 18, 18);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                fm = g2.getFontMetrics();
                String qs = String.valueOf(qSize);
                g2.setColor(Color.WHITE);
                g2.drawString(qs, bx - fm.stringWidth(qs) / 2, by + fm.getAscent() / 2 - 1);
            }

            // route order badge (top-left, when on highlighted route)
            if (onRoute && currentRoute != null) {
                int order = getRouteOrder(a);
                if (order >= 0) {
                    int bx = cx - NODE_R + 5;
                    int by = cy - NODE_R + 3;
                    g2.setColor(EDGE_ROUTE);
                    g2.fillOval(bx - 9, by - 9, 18, 18);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    fm = g2.getFontMetrics();
                    String os = String.valueOf(order + 1);
                    g2.setColor(new Color(10, 14, 20));
                    g2.drawString(os, bx - fm.stringWidth(os) / 2, by + fm.getAscent() / 2 - 1);
                }
            }
        }
    }

    // ================================================================
    // route calculation (Dijkstra via park)
    // ================================================================
    private void calculateRoute() {
        String fromName = (String) fromCombo.getSelectedItem();
        String toName   = (String) toCombo.getSelectedItem();

        if (fromName == null || fromName.isEmpty() || toName == null || toName.isEmpty()) {
            setRouteMsg(new Color(240, 80, 80), "Select both origin and destination."); return;
        }
        if (fromName.equals(toName)) {
            setRouteMsg(new Color(240, 80, 80), "Origin and destination must be different."); return;
        }

        Attraction origin = park.findAttractionByName(fromName);
        Attraction dest   = park.findAttractionByName(toName);
        if (origin == null) { setRouteMsg(new Color(240, 80, 80), "Origin not found: " + fromName); return; }
        if (dest   == null) { setRouteMsg(new Color(240, 80, 80), "Destination not found: " + toName); return; }

        LinkedList<Attraction> route = park.calculateOptimalRoute(origin, dest);

        if (route == null || route.getSize() < 2) {
            currentRoute = null; routeEdgeKeys.clear();
            setRouteMsg(new Color(240, 80, 80), "No path found between those attractions."); canvas.repaint(); return;
        }

        currentRoute = route;
        routeEdgeKeys.clear();

        double totalDist = 0;
        int rn = route.getSize();
        for (int i = 0; i < rn - 1; i++) {
            Attraction a = route.get(i), b = route.get(i + 1);
            if (a == null || b == null) continue;
            String[] ids = {a.getId(), b.getId()};
            Arrays.sort(ids);
            routeEdgeKeys.add(ids[0] + "|" + ids[1]);
            totalDist += edgeWeight(a, b);
        }

        setRouteMsg(new Color(255, 220, 50),
                "Dijkstra route: " + rn + " stops  |  Total: " + (int) totalDist + " m  |  "
                        + "Est. walk: ~" + Math.max(1, (int)(totalDist / 80)) + " min");

        // scroll to origin node
        scrollToAttraction(origin);
        canvas.repaint();
    }

    private void scrollToAttraction(Attraction a) {
        int[] pos = positions.get(a.getId());
        if (pos == null || canvas == null) return;
        double cx = canvas.getWidth()  / 2.0;
        double cy = canvas.getHeight() / 2.0;
        panX = cx - pos[0] * scale;
        panY = cy - pos[1] * scale;
    }

    // ================================================================
    // node click - auto-fill combos
    // ================================================================
    private void onNodeClicked(String id) {
        Attraction a = findById(id);
        if (a == null) return;
        String name = a.getName();

        // first click -> from, second -> to, further -> replace to
        if (fromCombo.getSelectedItem() == null || ((String) fromCombo.getSelectedItem()).isEmpty()) {
            selectCombo(fromCombo, name);
        } else if (toCombo.getSelectedItem() == null || ((String) toCombo.getSelectedItem()).isEmpty()) {
            selectCombo(toCombo, name);
        } else {
            selectCombo(toCombo, name);
        }

        setRouteMsg(C_TEXT2, a.getName() + "  |  " + a.getStatus()
                + "  |  Queue: " + a.getVirtualQueue().getSize()
                + "  |  Wait: " + a.calculateWaitTime() + " min");
    }

    // ================================================================
    // helpers
    // ================================================================
    private String hitTest(int sx, int sy) {
        LinkedList<Attraction> all = park.listAllAttractions();
        int n = all.getSize();
        int hitR = (int)(NODE_R * scale) + 5;
        for (int i = 0; i < n; i++) {
            Attraction a = all.get(i); if (a == null) continue;
            int[] pos = positions.get(a.getId()); if (pos == null) continue;
            int wx = (int)(pos[0] * scale + panX);
            int wy = (int)(pos[1] * scale + panY);
            if (Math.hypot(sx - wx, sy - wy) <= hitR) return a.getId();
        }
        return null;
    }

    private boolean isOnRoute(Attraction a) {
        if (currentRoute == null) return false;
        int n = currentRoute.getSize();
        for (int i = 0; i < n; i++) {
            Attraction r = currentRoute.get(i);
            if (r != null && r.getId().equals(a.getId())) return true;
        }
        return false;
    }

    private int getRouteOrder(Attraction a) {
        if (currentRoute == null) return -1;
        int n = currentRoute.getSize();
        for (int i = 0; i < n; i++) {
            Attraction r = currentRoute.get(i);
            if (r != null && r.getId().equals(a.getId())) return i;
        }
        return -1;
    }

    private double edgeWeight(Attraction from, Attraction to) {
        LinkedList<GraphNode<Attraction>> nodes = park.getMapGraph().getNodes();
        int n = nodes.getSize();
        for (int i = 0; i < n; i++) {
            GraphNode<Attraction> node = nodes.get(i);
            if (node == null || !node.getData().getId().equals(from.getId())) continue;
            LinkedList<GraphEdge<Attraction>> edges = node.getEdges();
            int ne = edges.getSize();
            for (int j = 0; j < ne; j++) {
                GraphEdge<Attraction> edge = edges.get(j);
                if (edge != null && edge.getDestination().getData().getId().equals(to.getId()))
                    return edge.getWeight();
            }
        }
        return 0;
    }

    private Attraction findById(String id) {
        LinkedList<Attraction> all = park.listAllAttractions();
        int n = all.getSize();
        for (int i = 0; i < n; i++) {
            Attraction a = all.get(i);
            if (a != null && a.getId().equals(id)) return a;
        }
        return null;
    }

    private void refreshCombos() {
        String prevFrom = (String) fromCombo.getSelectedItem();
        String prevTo = (String) toCombo.getSelectedItem();
        fromCombo.removeAllItems(); toCombo.removeAllItems();
        fromCombo.addItem(""); toCombo.addItem("");
        LinkedList<Attraction> all = park.listAllAttractions();
        int n = all.getSize();
        for (int i = 0; i < n; i++) {
            Attraction a = all.get(i);
            if (a != null) { fromCombo.addItem(a.getName()); toCombo.addItem(a.getName()); }
        }
        if (prevFrom != null) selectCombo(fromCombo, prevFrom);
        if (prevTo != null) selectCombo(toCombo, prevTo);
        if (canvas != null) canvas.repaint();
    }

    private void selectCombo(JComboBox<String> combo, String name) {
        for (int i = 0; i < combo.getItemCount(); i++)
            if (name.equals(combo.getItemAt(i))) { combo.setSelectedIndex(i); return; }
    }

    private void setRouteMsg(Color color, String msg) {
        routeInfoLabel.setForeground(color); routeInfoLabel.setText("  " + msg);
    }

    public void refresh() {
        currentRoute = null; routeEdgeKeys.clear();
        refreshCombos();
        setRouteMsg(C_TEXT2, "Click a node to select it as origin / destination");
    }

    // ================================================================
    // legend widget helpers
    // ================================================================
    private JLabel ctrlLabel(String text) {
        JLabel l = new JLabel(text); l.setFont(F_CTRL); l.setForeground(C_TEXT2); return l;
    }

    private JComboBox<String> styledCombo(int width) {
        JComboBox<String> c = new JComboBox<>();
        c.setFont(F_CTRL); c.setBackground(new Color(22, 30, 42)); c.setForeground(C_TEXT);
        c.setPreferredSize(new Dimension(width, 30)); return c;
    }

    private JButton colorBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? color.brighter() : color.darker());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose(); super.paintComponent(g);
            }
        };
        btn.setFont(F_BTN); btn.setForeground(Color.WHITE);
        btn.setOpaque(false); btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(105, 30)); return btn;
    }

    private JPanel dotLegend(Color color, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)); p.setOpaque(false);
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color); g2.fillOval(1, 1, 11, 11); g2.dispose();
            }
        };
        dot.setOpaque(false); dot.setPreferredSize(new Dimension(13, 13));
        JLabel lbl = new JLabel(text); lbl.setFont(F_SMALL); lbl.setForeground(C_TEXT2);
        p.add(dot); p.add(lbl); return p;
    }

    private JPanel lineLegend(Color color, float width, String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)); p.setOpaque(false);
        JPanel line = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color); g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(0, 6, 22, 6); g2.dispose();
            }
        };
        line.setOpaque(false); line.setPreferredSize(new Dimension(22, 13));
        JLabel lbl = new JLabel(text); lbl.setFont(F_SMALL); lbl.setForeground(C_TEXT2);
        p.add(line); p.add(lbl); return p;
    }

    private JSeparator vSep() {
        JSeparator s = new JSeparator(SwingConstants.VERTICAL);
        s.setForeground(C_BORDER); s.setPreferredSize(new Dimension(1, 16)); return s;
    }
}