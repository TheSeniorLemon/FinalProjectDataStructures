package co.edu.uniquindio.techpark.view;

import co.edu.uniquindio.techpark.model.entities.*;
import co.edu.uniquindio.techpark.model.enums.AttractionStatus;
import co.edu.uniquindio.techpark.model.structures.GraphEdge;
import co.edu.uniquindio.techpark.model.structures.GraphNode;
import co.edu.uniquindio.techpark.model.structures.LinkedList;
import co.edu.uniquindio.techpark.model.enums.TicketType;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

public class MapPanel extends JPanel {

    private static final Color C_BG = new Color(10, 14, 20);
    private static final Color C_CTRL_BAR = new Color(15, 20, 28);
    private static final Color C_CARD = new Color(18, 24, 32);
    private static final Color C_BORDER = new Color(40, 50, 64);
    private static final Color C_PRIMARY = new Color(56, 130, 255);
    private static final Color C_TEXT = new Color(224, 232, 244);
    private static final Color C_TEXT2 = new Color(120, 134, 154);
    private static final Color C_SUCCESS = new Color(52, 199, 100);
    private static final Color C_WARNING = new Color(255, 180, 50);
    private static final Color C_DANGER = new Color(240, 80,  80);
    private static final Color C_MAP_BG = new Color(20, 34, 22);
    private static final Color C_DET_BG = new Color(13, 18, 26);

    private static final Color NODE_ACTIVE = new Color(52, 199, 100);
    private static final Color NODE_MAINT = new Color(255, 180, 50);
    private static final Color NODE_CLOSED = new Color(240, 80,  80);
    private static final Color NODE_BORDER = new Color(200, 220, 255);
    private static final Color EDGE_NORMAL = new Color(80, 100, 120);
    private static final Color EDGE_ROUTE = new Color(255, 220, 50);

    private static final Object[][] ZONE_STYLES = {
            {"ZONE-001","Adventure World", 45, 40,265,240,new Color(255,100,50,35), new Color(255,140,60)},
            {"ZONE-002","Aqua Kingdom", 380, 40,270,250,new Color( 50,150,255,35),new Color( 60,170,255)},
            {"ZONE-003","Kids Land", 40,355,305,210,new Color(100,220,100,35),new Color( 80,200,100)},
            {"ZONE-004","Tech Arena", 410,355,300,200,new Color(180, 80,255,35),new Color(180, 90,255)},
    };

    private static final Font F_ZONE = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font F_NODE = new Font("Segoe UI", Font.BOLD, 10);
    private static final Font F_EDGE = new Font("Segoe UI", Font.PLAIN, 9);
    private static final Font F_CTRL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_BTN = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_ICON = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font F_DET_H = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font F_DET_B = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_DET_S = new Font("Segoe UI", Font.PLAIN, 11);

    private static final int NODE_R = 24;

    // ---- data ----
    private final Park park;
    private final User currentUser;
    private final Map<String, int[]> positions = new HashMap<>();

    // ---- view ----
    private double scale = 1.0, panX = 40.0, panY = 20.0;
    private boolean dragging;
    private int dragMouseX, dragMouseY;
    private double dragPanX, dragPanY;

    // ---- route ----
    private LinkedList<Attraction> currentRoute = null;
    private final Set<String> routeEdgeKeys = new HashSet<>();

    // ---- selection ----
    private String hoveredId  = null;
    private String selectedId = null;

    // ---- controls ----
    private JComboBox<String> fromCombo, toCombo;
    private JLabel routeInfoLabel;
    private JPanel canvas;
    private JPanel detailPanel;

    // ================================================================
    public MapPanel(Park park)             { this(park, null); }
    public MapPanel(Park park, User user)  { this.park=park; this.currentUser=user; loadDefaultPositions(); buildLayout(); }

    public void setPosition(String id, int x, int y) { positions.put(id, new int[]{x,y}); if(canvas!=null) canvas.repaint(); }

    // ----------------------------------------------------------------
    private void loadDefaultPositions() {
        positions.put("ATT-001", new int[]{120,140}); positions.put("ATT-002", new int[]{240, 82});
        positions.put("ATT-003", new int[]{172,228}); positions.put("ATT-004", new int[]{452,100});
        positions.put("ATT-005", new int[]{566,160}); positions.put("ATT-006", new int[]{506,255});
        positions.put("ATT-007", new int[]{148,412}); positions.put("ATT-008", new int[]{282,446});
        positions.put("ATT-009", new int[]{100,510}); positions.put("ATT-010", new int[]{536,395});
        positions.put("ATT-011", new int[]{644,460}); positions.put("ATT-012", new int[]{492,500});
    }

    // ================================================================
    // UI BUILD
    // ================================================================
    private void buildLayout() {
        setLayout(new BorderLayout()); setBackground(C_BG);
        add(buildControlBar(), BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(buildCanvas(), BorderLayout.CENTER);
        center.add(buildDetailPanel(), BorderLayout.EAST);
        add(center, BorderLayout.CENTER);
        add(buildLegend(), BorderLayout.SOUTH);
    }

    // ---- control bar ----
    private JPanel buildControlBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        bar.setBackground(C_CTRL_BAR);
        bar.setBorder(BorderFactory.createMatteBorder(0,0,1,0,C_BORDER));
        bar.add(ctrlLbl("From:")); fromCombo=styledCombo(175); bar.add(fromCombo);
        bar.add(ctrlLbl("To:"));   toCombo=styledCombo(175);   bar.add(toCombo);
        JButton bFind  = colorBtn("Find route", C_PRIMARY);
        JButton bClear = colorBtn("Clear", new Color(80,90,110));
        JButton bReset = colorBtn("Reset view", new Color(60,75,95));
        bar.add(bFind); bar.add(bClear); bar.add(bReset);
        routeInfoLabel = new JLabel("  Click a node to see details");
        routeInfoLabel.setFont(F_SMALL); routeInfoLabel.setForeground(C_TEXT2);
        bar.add(routeInfoLabel);
        refreshCombos();
        bFind.addActionListener(e  -> calculateRoute());
        bClear.addActionListener(e -> { currentRoute=null; routeEdgeKeys.clear(); routeInfoLabel.setForeground(C_TEXT2); routeInfoLabel.setText("Route cleared."); canvas.repaint(); });
        bReset.addActionListener(e -> { scale=1.0; panX=40; panY=20; canvas.repaint(); });
        return bar;
    }

    // ---- canvas ----
    private JPanel buildCanvas() {
        canvas = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                paintMap(g2); g2.dispose();
            }
        };
        canvas.setBackground(C_MAP_BG);
        canvas.addMouseWheelListener(e -> {
            double f=e.getWheelRotation()<0?1.12:0.893, ns=Math.max(0.35,Math.min(4.0,scale*f));
            panX=e.getX()-(e.getX()-panX)*(ns/scale); panY=e.getY()-(e.getY()-panY)*(ns/scale); scale=ns; canvas.repaint();
        });
        canvas.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { if(!SwingUtilities.isLeftMouseButton(e))return; dragging=true; dragMouseX=e.getX(); dragMouseY=e.getY(); dragPanX=panX; dragPanY=panY; canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)); }
            @Override public void mouseReleased(MouseEvent e) { dragging=false; canvas.setCursor(hoveredId!=null?Cursor.getPredefinedCursor(Cursor.HAND_CURSOR):Cursor.getDefaultCursor()); }
            @Override public void mouseClicked(MouseEvent e)  { if(!SwingUtilities.isLeftMouseButton(e))return; String hit=hitTest(e.getX(),e.getY()); if(hit!=null) onNodeClicked(hit); else { selectedId=null; showEmptyDetail(); canvas.repaint(); } }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) { if(!dragging)return; panX=dragPanX+(e.getX()-dragMouseX); panY=dragPanY+(e.getY()-dragMouseY); canvas.repaint(); }
            @Override public void mouseMoved(MouseEvent e)   { String p=hoveredId; hoveredId=hitTest(e.getX(),e.getY()); canvas.setCursor(hoveredId!=null?Cursor.getPredefinedCursor(Cursor.HAND_CURSOR):Cursor.getDefaultCursor()); if(!Objects.equals(p,hoveredId)) canvas.repaint(); }
        });
        return canvas;
    }

    // ---- detail panel (right side) ----
    private JPanel buildDetailPanel() {
        detailPanel = new JPanel();
        detailPanel.setPreferredSize(new Dimension(236, 0));
        detailPanel.setBackground(C_DET_BG);
        detailPanel.setBorder(BorderFactory.createMatteBorder(0,1,0,0,C_BORDER));
        detailPanel.setLayout(new BorderLayout());
        showEmptyDetail();
        return detailPanel;
    }

    private void showEmptyDetail() {
        detailPanel.removeAll();
        JPanel center = new JPanel(); center.setOpaque(false); center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        center.add(Box.createVerticalGlue());
        JLabel ico = new JLabel("◎",SwingConstants.CENTER); ico.setFont(new Font("Segoe UI",Font.PLAIN,34)); ico.setForeground(C_BORDER); ico.setAlignmentX(0.5f);
        JLabel msg = new JLabel("<html><div style='text-align:center'>Click on an<br>attraction to<br>see details</div></html>",SwingConstants.CENTER);
        msg.setFont(F_DET_S); msg.setForeground(C_TEXT2); msg.setAlignmentX(0.5f);
        center.add(ico); center.add(Box.createRigidArea(new Dimension(0,8))); center.add(msg); center.add(Box.createVerticalGlue());
        detailPanel.add(center,BorderLayout.CENTER); detailPanel.revalidate(); detailPanel.repaint();
    }

    private void showAttractionDetail(Attraction a) {
        detailPanel.removeAll();
        JPanel form = new JPanel(); form.setOpaque(false); form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(14,12,14,12));

        // name
        JLabel nameL = new JLabel("<html><b>"+a.getName()+"</b></html>"); nameL.setFont(F_DET_H); nameL.setForeground(C_TEXT); nameL.setAlignmentX(0f);
        form.add(nameL); form.add(vgap(6));

        // status badge
        Color sc = switch(a.getStatus()){case ACTIVE->C_SUCCESS;case MAINTENANCE->C_WARNING;default->C_DANGER;};
        String st = switch(a.getStatus()){case ACTIVE->"ACTIVE";case MAINTENANCE->"MAINTENANCE";default->"CLOSED";};
        JLabel badge = new JLabel(st); badge.setFont(new Font("Segoe UI",Font.BOLD,10)); badge.setForeground(sc); badge.setBorder(BorderFactory.createLineBorder(sc)); badge.setAlignmentX(0f);
        form.add(badge); form.add(vgap(10)); form.add(hsep()); form.add(vgap(8));

        // queue info
        int qSz = a.getVirtualQueue().getSize();
        form.add(dRow("In queue:", qSz+" people")); form.add(vgap(3));
        form.add(dRow("Wait time:", a.calculateWaitTime()+" min"));

        if (currentUser instanceof Visitor visitor) {
            int pos = a.getVirtualQueue().getPosition(visitor);
            if (pos >= 0) { form.add(vgap(3)); JLabel posL=new JLabel("Your position: #"+(pos+1)); posL.setFont(F_DET_B); posL.setForeground(C_PRIMARY); posL.setAlignmentX(0f); form.add(posL); }
            form.add(vgap(10)); form.add(hsep()); form.add(vgap(10));

            // queue actions
            boolean inQueue = pos >= 0;
            if (inQueue) {
                JButton btnLeave = wideBtn("Leave Queue", C_DANGER);
                btnLeave.addActionListener(e -> { a.removeFromVirtualQueue(visitor); DataManager.save(UserStore.getInstance(),park); showAttractionDetail(a); canvas.repaint(); setMsg(C_WARNING,"Left queue: "+a.getName()); });
                form.add(btnLeave);
            } else {
                boolean active = a.getStatus()==AttractionStatus.ACTIVE;
                boolean hasTicket= visitor.getTicket()!=null;
                boolean meetsReq = visitor.getHeight()>=a.getMinimumHeight() && visitor.getAge()>=a.getMinimumAge();
                JButton btnJoin  = wideBtn("Join Queue", active&&hasTicket&&meetsReq ? C_SUCCESS : C_TEXT2);
                btnJoin.setEnabled(active&&hasTicket&&meetsReq);
                btnJoin.addActionListener(e -> {
                    int prio = (visitor.getTicket()!=null && visitor.getTicket().getType()==TicketType.FAST_PASS) ? 1 : 2;
                    a.addToVirtualQueue(visitor);
                    DataManager.save(UserStore.getInstance(),park);
                    showAttractionDetail(a); canvas.repaint();
                    setMsg(C_SUCCESS,"Joined queue for "+a.getName()+" (priority "+(prio==1?"Fast-Pass":"General")+")");
                });
                form.add(btnJoin);
                if (!active) { form.add(vgap(3)); form.add(warnLbl("Attraction not active")); }
                if (!hasTicket) { form.add(vgap(3)); form.add(warnLbl("No ticket assigned")); }
                if (hasTicket&&!meetsReq) { form.add(vgap(3)); form.add(warnLbl("Height or age requirement not met")); }
            }

            form.add(vgap(10)); form.add(hsep()); form.add(vgap(10));

            // favorites
            boolean fav = visitor.isFavorite(a.getId());
            JButton btnFav = wideBtn(fav ? "♥  Remove Favorite" : "♡  Add to Favorites", fav ? C_DANGER : new Color(190,150,255));
            btnFav.addActionListener(e -> {
                if (visitor.isFavorite(a.getId())) visitor.removeFavorite(a.getId());
                else                                visitor.addFavorite(a.getId());
                DataManager.save(UserStore.getInstance(),park);
                showAttractionDetail(a);
            });
            form.add(btnFav);
        }

        form.add(vgap(10)); form.add(hsep()); form.add(vgap(8));

        // details section
        form.add(dLabel("Details")); form.add(vgap(4));
        form.add(dRow("Type:", a.getType().name().replace("_"," ").toLowerCase()));
        form.add(vgap(2)); form.add(dRow("Zone:", a.getZoneId()));
        form.add(vgap(2)); form.add(dRow("Capacity:", a.getCapacityPerCycle()+" / cycle"));
        if (a.getMinimumHeight()>0) { form.add(vgap(2)); form.add(dRow("Min height:", a.getMinimumHeight()+" m")); }
        if (a.getMinimumAge()>0)    { form.add(vgap(2)); form.add(dRow("Min age:",    a.getMinimumAge()+" yrs")); }
        if (a.getAdditionalCost()>0){ form.add(vgap(2)); form.add(dRow("Add. cost:",  "$"+String.format("%,.0f",a.getAdditionalCost()))); }

        form.add(vgap(10)); form.add(hsep()); form.add(vgap(8));

        // route shortcuts
        form.add(dLabel("Set as route point")); form.add(vgap(4));
        JPanel shortcuts = new JPanel(new GridLayout(1,2,6,0)); shortcuts.setOpaque(false); shortcuts.setMaximumSize(new Dimension(Integer.MAX_VALUE,32));
        JButton bFrom=shortBtn("From", C_PRIMARY), bTo=shortBtn("To", new Color(100,180,120));
        bFrom.addActionListener(e -> { selectCombo(fromCombo,a.getName()); setMsg(C_TEXT2,a.getName()+" set as origin."); });
        bTo.addActionListener(e -> { selectCombo(toCombo,a.getName());   setMsg(C_TEXT2,a.getName()+" set as destination."); });
        shortcuts.add(bFrom); shortcuts.add(bTo); form.add(shortcuts);

        JScrollPane sp = new JScrollPane(form); sp.setOpaque(false); sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder()); sp.getVerticalScrollBar().setUnitIncrement(10);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        detailPanel.add(sp, BorderLayout.CENTER); detailPanel.revalidate(); detailPanel.repaint();
    }

    // ---- legend ----
    private JPanel buildLegend() {
        JPanel leg = new JPanel(new FlowLayout(FlowLayout.LEFT,14,6));
        leg.setBackground(C_CTRL_BAR); leg.setBorder(BorderFactory.createMatteBorder(1,0,0,0,C_BORDER));
        leg.add(dotLgd(NODE_ACTIVE,"Active")); leg.add(dotLgd(NODE_MAINT,"Maintenance")); leg.add(dotLgd(NODE_CLOSED,"Closed"));
        leg.add(vSep()); leg.add(lineLgd(EDGE_NORMAL,2f,"Path")); leg.add(lineLgd(EDGE_ROUTE,3f,"Shortest route"));
        leg.add(vSep());
        if (currentUser instanceof Visitor) { JPanel qDot=new JPanel(){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(C_SUCCESS);g2.fillOval(1,1,11,11);g2.dispose();}}; qDot.setOpaque(false); qDot.setPreferredSize(new Dimension(13,13)); JPanel qp=new JPanel(new FlowLayout(FlowLayout.LEFT,4,0)); qp.setOpaque(false); JLabel ql=new JLabel("You in queue"); ql.setFont(F_SMALL); ql.setForeground(C_TEXT2); qp.add(qDot); qp.add(ql); leg.add(vSep()); leg.add(qp); }
        JLabel hint=new JLabel("Scroll=zoom  |  Drag=pan  |  Click=select"); hint.setFont(F_SMALL); hint.setForeground(new Color(70,80,100)); leg.add(hint);
        return leg;
    }

    // ================================================================
    // PAINTING
    // ================================================================
    private void paintMap(Graphics2D g2) {
        g2.translate(panX,panY); g2.scale(scale,scale);
        drawGrid(g2); drawZoneAreas(g2); drawEdges(g2,false); drawEdges(g2,true); drawNodes(g2);
    }

    private void drawGrid(Graphics2D g2) { g2.setColor(new Color(255,255,255,10)); for(int x=0;x<=760;x+=40) for(int y=0;y<=600;y+=40) g2.fillOval(x-1,y-1,2,2); }

    private void drawZoneAreas(Graphics2D g2) {
        for (Object[] z : ZONE_STYLES) {
            int zx=(int)z[2],zy=(int)z[3],zw=(int)z[4],zh=(int)z[5]; Color fill=(Color)z[6],accent=(Color)z[7];
            g2.setColor(fill); g2.fillRoundRect(zx,zy,zw,zh,20,20);
            g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),65)); g2.setStroke(new BasicStroke(1.5f)); g2.drawRoundRect(zx,zy,zw,zh,20,20);
            g2.setFont(F_ZONE); g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),190)); g2.drawString((String)z[1],zx+10,zy+20);
        }
    }

    private void drawEdges(Graphics2D g2, boolean routeOnly) {
        LinkedList<GraphNode<Attraction>> nodes=park.getMapGraph().getNodes(); int n=nodes.getSize(); Set<String> drawn=new HashSet<>();
        for (int i=0;i<n;i++) {
            GraphNode<Attraction> fn=nodes.get(i); if(fn==null) continue;
            Attraction from=fn.getData(); int[] pF=positions.get(from.getId()); if(pF==null) continue;
            LinkedList<GraphEdge<Attraction>> edges=fn.getEdges(); int ne=edges.getSize();
            for (int j=0;j<ne;j++) {
                GraphEdge<Attraction> edge=edges.get(j); if(edge==null) continue;
                Attraction to=edge.getDestination().getData(); int[] pT=positions.get(to.getId()); if(pT==null) continue;
                String[] ids={from.getId(),to.getId()}; Arrays.sort(ids); String key=ids[0]+"|"+ids[1]; if(drawn.contains(key)) continue; drawn.add(key);
                boolean isRoute=routeEdgeKeys.contains(key); if(routeOnly&&!isRoute) continue; if(!routeOnly&&isRoute) continue;
                int x1=pF[0],y1=pF[1],x2=pT[0],y2=pT[1];
                if (isRoute) {
                    g2.setStroke(new BasicStroke(7f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND)); g2.setColor(new Color(255,220,50,70)); g2.drawLine(x1,y1,x2,y2);
                    g2.setStroke(new BasicStroke(3f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND)); g2.setColor(EDGE_ROUTE); g2.drawLine(x1,y1,x2,y2);
                } else {
                    g2.setStroke(new BasicStroke(1.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND)); g2.setColor(EDGE_NORMAL); g2.drawLine(x1,y1,x2,y2);
                    int mx=(x1+x2)/2,my=(y1+y2)/2; String wStr=(int)edge.getWeight()+"m"; g2.setFont(F_EDGE); FontMetrics fm=g2.getFontMetrics(); int tw=fm.stringWidth(wStr);
                    g2.setColor(new Color(10,14,20,180)); g2.fillRoundRect(mx-tw/2-3,my-fm.getAscent()+1,tw+6,fm.getHeight(),3,3);
                    g2.setColor(new Color(130,150,170)); g2.drawString(wStr,mx-tw/2,my+1);
                }
            }
        }
    }

    private void drawNodes(Graphics2D g2) {
        LinkedList<Attraction> all=park.listAllAttractions(); int n=all.getSize();
        for (int i=0;i<n;i++) {
            Attraction a=all.get(i); if(a==null) continue; int[] pos=positions.get(a.getId()); if(pos==null) continue;
            int cx=pos[0],cy=pos[1];
            boolean hovered=a.getId().equals(hoveredId), selected=a.getId().equals(selectedId), onRoute=isOnRoute(a);
            Color base=switch(a.getStatus()){case ACTIVE->NODE_ACTIVE;case MAINTENANCE->NODE_MAINT;default->NODE_CLOSED;};
            // glow
            if(hovered||onRoute||selected){ Color glow=selected?new Color(255,255,255,85):onRoute?new Color(255,220,50,80):new Color(255,255,255,55); g2.setColor(glow); g2.fillOval(cx-NODE_R-7,cy-NODE_R-7,(NODE_R+7)*2,(NODE_R+7)*2); }
            // shadow + fill
            g2.setColor(new Color(0,0,0,90)); g2.fillOval(cx-NODE_R+2,cy-NODE_R+3,NODE_R*2,NODE_R*2);
            g2.setColor(base.darker().darker()); g2.fillOval(cx-NODE_R,cy-NODE_R,NODE_R*2,NODE_R*2);
            int inner=NODE_R-4; g2.setColor(base); g2.fillOval(cx-inner,cy-inner,inner*2,inner*2);
            // border
            Color bc=selected?Color.WHITE:onRoute?EDGE_ROUTE:new Color(NODE_BORDER.getRed(),NODE_BORDER.getGreen(),NODE_BORDER.getBlue(),hovered?255:160);
            g2.setStroke(new BasicStroke(selected?2.5f:onRoute?3f:1.8f)); g2.setColor(bc); g2.drawOval(cx-NODE_R,cy-NODE_R,NODE_R*2,NODE_R*2);
            // status icon
            String icon=switch(a.getStatus()){case ACTIVE->"A";case MAINTENANCE->"M";default->"X";}; g2.setFont(F_ICON); FontMetrics fm=g2.getFontMetrics(); g2.setColor(new Color(10,14,20,210)); g2.drawString(icon,cx-fm.stringWidth(icon)/2,cy+fm.getAscent()/2-1);
            // name
            g2.setFont(F_NODE); fm=g2.getFontMetrics(); String lbl=a.getName(); int tw=fm.stringWidth(lbl); int lx=cx-tw/2,ly=cy+NODE_R+4;
            g2.setColor(new Color(10,14,20,200)); g2.fillRoundRect(lx-4,ly,tw+8,fm.getHeight()+2,4,4);
            g2.setColor((hovered||onRoute||selected)?Color.WHITE:C_TEXT); g2.drawString(lbl,lx,ly+fm.getAscent());
            // queue badge (top-right)
            int qSz=a.getVirtualQueue().getSize();
            if(qSz>0){ int bx=cx+NODE_R-5,by=cy-NODE_R+3; g2.setColor(C_PRIMARY); g2.fillOval(bx-9,by-9,18,18); g2.setFont(new Font("Segoe UI",Font.BOLD,9)); fm=g2.getFontMetrics(); String qs=String.valueOf(qSz); g2.setColor(Color.WHITE); g2.drawString(qs,bx-fm.stringWidth(qs)/2,by+fm.getAscent()/2-1); }
            // visitor-in-queue badge (top-left) - green Q
            if(currentUser instanceof Visitor visitor && a.getVirtualQueue().getPosition(visitor)>=0){ int bx=cx-NODE_R+5,by=cy-NODE_R+3; g2.setColor(C_SUCCESS); g2.fillOval(bx-8,by-8,16,16); g2.setFont(new Font("Segoe UI",Font.BOLD,9)); fm=g2.getFontMetrics(); g2.setColor(Color.WHITE); g2.drawString("Q",bx-fm.stringWidth("Q")/2,by+fm.getAscent()/2-1); }
            // route order badge
            if(onRoute&&currentRoute!=null){ int ord=getRouteOrder(a); if(ord>=0){ int bx=cx+NODE_R-5,by=cy+NODE_R-5; g2.setColor(EDGE_ROUTE); g2.fillOval(bx-9,by-9,18,18); g2.setFont(new Font("Segoe UI",Font.BOLD,9)); fm=g2.getFontMetrics(); String os=String.valueOf(ord+1); g2.setColor(new Color(10,14,20)); g2.drawString(os,bx-fm.stringWidth(os)/2,by+fm.getAscent()/2-1); } }
        }
    }

    // ================================================================
    // INTERACTIONS
    // ================================================================
    private void onNodeClicked(String id) {
        selectedId=id; Attraction a=findById(id);
        if(a==null){ showEmptyDetail(); canvas.repaint(); return; }
        showAttractionDetail(a); canvas.repaint();
        setMsg(C_TEXT2, a.getName()+"  |  "+a.getStatus()+"  |  Queue: "+a.getVirtualQueue().getSize()+"  |  Wait: "+a.calculateWaitTime()+" min");
    }

    private void calculateRoute() {
        String fn=(String)fromCombo.getSelectedItem(), tn=(String)toCombo.getSelectedItem();
        if(fn==null||fn.isEmpty()||tn==null||tn.isEmpty()){ setMsg(C_DANGER,"Select both origin and destination."); return; }
        if(fn.equals(tn)){ setMsg(C_DANGER,"Origin and destination must be different."); return; }
        Attraction orig=park.findAttractionByName(fn), dest=park.findAttractionByName(tn);
        if(orig==null){ setMsg(C_DANGER,"Origin not found: "+fn); return; }
        if(dest==null){ setMsg(C_DANGER,"Destination not found: "+tn); return; }
        LinkedList<Attraction> route=park.calculateOptimalRoute(orig,dest);
        if(route==null||route.getSize()<2){ currentRoute=null; routeEdgeKeys.clear(); setMsg(C_DANGER,"No path found."); canvas.repaint(); return; }
        currentRoute=route; routeEdgeKeys.clear(); double dist=0; int rn=route.getSize();
        for(int i=0;i<rn-1;i++){ Attraction a=route.get(i),b=route.get(i+1); if(a==null||b==null)continue; String[] ids={a.getId(),b.getId()}; Arrays.sort(ids); routeEdgeKeys.add(ids[0]+"|"+ids[1]); dist+=edgeWeight(a,b); }
        setMsg(EDGE_ROUTE,"Route: "+rn+" stops  |  "+((int)dist)+" m  |  ~"+Math.max(1,(int)(dist/80))+" min walk");
        scrollTo(orig); canvas.repaint();
    }

    // ================================================================
    // HELPERS
    // ================================================================
    private String hitTest(int sx,int sy){ LinkedList<Attraction> all=park.listAllAttractions(); int n=all.getSize(); int hitR=(int)(NODE_R*scale)+5; for(int i=0;i<n;i++){ Attraction a=all.get(i); if(a==null)continue; int[] p=positions.get(a.getId()); if(p==null)continue; if(Math.hypot(sx-(p[0]*scale+panX),sy-(p[1]*scale+panY))<=hitR) return a.getId(); } return null; }
    private boolean isOnRoute(Attraction a) { if(currentRoute==null)return false; for(int i=0;i<currentRoute.getSize();i++){ Attraction r=currentRoute.get(i); if(r!=null&&r.getId().equals(a.getId()))return true; } return false; }
    private int getRouteOrder(Attraction a) { if(currentRoute==null)return -1; for(int i=0;i<currentRoute.getSize();i++){ Attraction r=currentRoute.get(i); if(r!=null&&r.getId().equals(a.getId()))return i; } return -1; }
    private double edgeWeight(Attraction a,Attraction b){ LinkedList<GraphNode<Attraction>> ns=park.getMapGraph().getNodes(); for(int i=0;i<ns.getSize();i++){ GraphNode<Attraction> nd=ns.get(i); if(nd==null||!nd.getData().getId().equals(a.getId()))continue; LinkedList<GraphEdge<Attraction>> es=nd.getEdges(); for(int j=0;j<es.getSize();j++){ GraphEdge<Attraction> e=es.get(j); if(e!=null&&e.getDestination().getData().getId().equals(b.getId()))return e.getWeight(); } } return 0; }
    private Attraction findById(String id){ LinkedList<Attraction> all=park.listAllAttractions(); for(int i=0;i<all.getSize();i++){ Attraction a=all.get(i); if(a!=null&&a.getId().equals(id))return a; } return null; }
    private void scrollTo(Attraction a){ int[] p=positions.get(a.getId()); if(p==null||canvas==null)return; panX=canvas.getWidth()/2.0-p[0]*scale; panY=canvas.getHeight()/2.0-p[1]*scale; }
    private void refreshCombos(){ String pf=(String)fromCombo.getSelectedItem(),pt=(String)toCombo.getSelectedItem(); fromCombo.removeAllItems(); toCombo.removeAllItems(); fromCombo.addItem(""); toCombo.addItem(""); LinkedList<Attraction> all=park.listAllAttractions(); for(int i=0;i<all.getSize();i++){ Attraction a=all.get(i); if(a!=null){ fromCombo.addItem(a.getName()); toCombo.addItem(a.getName()); } } if(pf!=null) selectCombo(fromCombo,pf); if(pt!=null) selectCombo(toCombo,pt); if(canvas!=null) canvas.repaint(); }
    private void selectCombo(JComboBox<String> c,String name){ for(int i=0;i<c.getItemCount();i++) if(name.equals(c.getItemAt(i))){ c.setSelectedIndex(i); return; } }
    private void setMsg(Color col,String msg){ routeInfoLabel.setForeground(col); routeInfoLabel.setText("  "+msg); }
    public  void refresh(){ currentRoute=null; routeEdgeKeys.clear(); refreshCombos(); selectedId=null; showEmptyDetail(); setMsg(C_TEXT2,"Click a node to see attraction details"); }

    // ---- detail panel widgets ----
    private Component vgap(int n) { return Box.createRigidArea(new Dimension(0,n)); }
    private JPanel hsep() { JPanel p=new JPanel(); p.setBackground(C_BORDER); p.setMaximumSize(new Dimension(Integer.MAX_VALUE,1)); p.setPreferredSize(new Dimension(0,1)); return p; }
    private JLabel dLabel(String t) { JLabel l=new JLabel(t); l.setFont(new Font("Segoe UI",Font.BOLD,11)); l.setForeground(C_TEXT2); l.setAlignmentX(0f); return l; }
    private JLabel warnLbl(String t) { JLabel l=new JLabel(t); l.setFont(new Font("Segoe UI",Font.PLAIN,10)); l.setForeground(C_WARNING); l.setAlignmentX(0f); return l; }
    private JPanel dRow(String k,String v){ JPanel p=new JPanel(new BorderLayout(6,0)); p.setOpaque(false); p.setMaximumSize(new Dimension(Integer.MAX_VALUE,18)); JLabel lk=new JLabel(k); lk.setFont(F_DET_S); lk.setForeground(C_TEXT2); lk.setPreferredSize(new Dimension(74,0)); JLabel lv=new JLabel(v); lv.setFont(F_DET_S); lv.setForeground(C_TEXT); p.add(lk,BorderLayout.WEST); p.add(lv,BorderLayout.CENTER); return p; }
    private JButton wideBtn(String t,Color c){ JButton b=new JButton(t){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(new Color(c.getRed()/5,c.getGreen()/5,c.getBlue()/5));g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),8,8));g2.setColor(c);g2.setStroke(new BasicStroke(1.2f));g2.draw(new RoundRectangle2D.Float(.5f,.5f,getWidth()-1,getHeight()-1,8,8));g2.dispose();super.paintComponent(g);}}; b.setFont(F_BTN); b.setForeground(c); b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setMaximumSize(new Dimension(Integer.MAX_VALUE,36)); b.setAlignmentX(0f); return b; }
    private JButton shortBtn(String t,Color c){ JButton b=new JButton(t); b.setFont(F_SMALL); b.setForeground(c); b.setBackground(new Color(c.getRed()/6,c.getGreen()/6,c.getBlue()/6)); b.setBorder(BorderFactory.createLineBorder(c)); b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b; }

    // ---- legend widgets ----
    private JLabel ctrlLbl(String t) { JLabel l=new JLabel(t); l.setFont(F_CTRL); l.setForeground(C_TEXT2); return l; }
    private JComboBox<String> styledCombo(int w) { JComboBox<String> c=new JComboBox<>(); c.setFont(F_CTRL); c.setBackground(new Color(22,30,42)); c.setForeground(C_TEXT); c.setPreferredSize(new Dimension(w,30)); return c; }
    private JButton colorBtn(String t,Color c) { JButton b=new JButton(t){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(getModel().isRollover()?c.brighter():c.darker());g2.fill(new RoundRectangle2D.Float(0,0,getWidth(),getHeight(),6,6));g2.dispose();super.paintComponent(g);}}; b.setFont(F_BTN); b.setForeground(Color.WHITE); b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setPreferredSize(new Dimension(100,30)); return b; }
    private JPanel dotLgd(Color c,String t) { JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,4,0)); p.setOpaque(false); JPanel d=new JPanel(){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);g2.setColor(c);g2.fillOval(1,1,11,11);g2.dispose();}}; d.setOpaque(false); d.setPreferredSize(new Dimension(13,13)); JLabel l=new JLabel(t); l.setFont(F_SMALL); l.setForeground(C_TEXT2); p.add(d); p.add(l); return p; }
    private JPanel lineLgd(Color c,float w,String t){ JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,4,0)); p.setOpaque(false); JPanel li=new JPanel(){@Override protected void paintComponent(Graphics g){Graphics2D g2=(Graphics2D)g.create();g2.setColor(c);g2.setStroke(new BasicStroke(w,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));g2.drawLine(0,6,22,6);g2.dispose();}}; li.setOpaque(false); li.setPreferredSize(new Dimension(22,13)); JLabel l=new JLabel(t); l.setFont(F_SMALL); l.setForeground(C_TEXT2); p.add(li); p.add(l); return p; }
    private JSeparator vSep() { JSeparator s=new JSeparator(SwingConstants.VERTICAL); s.setForeground(C_BORDER); s.setPreferredSize(new Dimension(1,16)); return s; }
}