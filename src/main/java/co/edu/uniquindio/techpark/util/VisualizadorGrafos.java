package co.edu.uniquindio.techpark.util;

import co.edu.uniquindio.techpark.dataStructures.SimpleLinkedList;
import co.edu.uniquindio.techpark.model.entities.Attraction;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class VisualizadorGrafos extends JFrame {

    private static final double MAX_ZOOM = 2.5;
    private static final double MIN_ZOOM = 0.5;

    public VisualizadorGrafos(SimpleLinkedList<Attraction> listaAtracciones) {
        setTitle("Mapa Interactivo TechPark");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        HashMap<String, Object> nodosMap = new HashMap<>();

        graph.getModel().beginUpdate();
        try {
            for (Attraction a : listaAtracciones) {
                Object v = graph.insertVertex(parent, null, a.getName(), 0, 0, 110, 50);
                nodosMap.put(a.getName(), v);
            }

            for (Attraction a : listaAtracciones) {
                Object vOrigen = nodosMap.get(a.getName());
                for (Attraction.Edge edge : a.getAdjacentEdges()) {
                    Object vDestino = nodosMap.get(edge.getTarget().getName());
                    if (vDestino != null) {
                        graph.insertEdge(parent, null, edge.getWeight() + "m", vOrigen, vDestino);
                    }
                }
            }

            mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
            layout.setOrientation(SwingConstants.WEST);
            layout.execute(parent);
        } finally {
            graph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph);

        // --- CONFIGURACIÓN DE NAVEGACIÓN ---
        graphComponent.setEnabled(false); // Mantenemos el bloqueo que pediste
        graphComponent.setConnectable(false);
        graph.setCellsEditable(false);
        graph.setCellsMovable(false);
        graph.setCellsDisconnectable(false);

        // Zoom controlado con límites
        graphComponent.setWheelScrollingEnabled(false);
        graphComponent.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double currentScale = graphComponent.getGraph().getView().getScale();
                if (e.getWheelRotation() < 0) {
                    if (currentScale < MAX_ZOOM) graphComponent.zoomIn();
                } else {
                    if (currentScale > MIN_ZOOM) graphComponent.zoomOut();
                }
            }
        });

        // --- EL TRUCO DEL CENTRADO REAL ---
        SwingUtilities.invokeLater(() -> {
            // scrollToCenter intenta mover los scrollbars al medio del contenido
            graphComponent.scrollToCenter(true);

            // Si el scroll no es suficiente, forzamos la posición del viewport
            Dimension graphSize = graphComponent.getGraphControl().getSize();
            Dimension viewPortSize = graphComponent.getViewport().getSize();

            int x = (graphSize.width - viewPortSize.width) / 2;
            int y = (graphSize.height - viewPortSize.height) / 2;

            graphComponent.getViewport().setViewPosition(new Point(Math.max(x, 0), Math.max(y, 0)));
        });

        // Tal como en tu ejemplo: BorderLayout para contener el componente
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(graphComponent, BorderLayout.CENTER);
    }
}