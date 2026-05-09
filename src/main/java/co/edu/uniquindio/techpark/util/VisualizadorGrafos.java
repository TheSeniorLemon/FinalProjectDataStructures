package co.edu.uniquindio.techpark.util;

import co.edu.uniquindio.techpark.dataStructures.SimpleLinkedList;
import co.edu.uniquindio.techpark.model.entities.Attraction;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.util.HashMap;
import javax.swing.JFrame;

public class VisualizadorGrafos extends JFrame {
    public VisualizadorGrafos(SimpleLinkedList<Attraction> listaAtracciones) {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        HashMap<String, Object> nodosMap = new HashMap<>();

        graph.getModel().beginUpdate();
        try {
            int x = 50, y = 50;

            for (Attraction a : listaAtracciones) {
                String nombre = a.getName();
                Object v = graph.insertVertex(parent, null, nombre, x, y, 90, 40);
                nodosMap.put(nombre, v);

                x += 120;
                if (x > 600) { x = 50; y += 80; }
            }

        } finally {
            graph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        add(graphComponent);
        setSize(800, 600);
    }
}
