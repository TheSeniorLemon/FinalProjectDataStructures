package co.edu.uniquindio;

import co.edu.uniquindio.techpark.dataStructures.SimpleLinkedList;
import co.edu.uniquindio.techpark.model.entities.Attraction;
import co.edu.uniquindio.techpark.model.enums.AttractionType;
import co.edu.uniquindio.techpark.util.VisualizadorGrafos;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // 1. Crear la lista de atracciones
        SimpleLinkedList<Attraction> listaAtracciones = new SimpleLinkedList<>();

        // 2. Crear algunas atracciones usando el Builder
        Attraction montana = new Attraction.Builder("1", "Montaña Rusa", AttractionType.ROLLER_COASTER)
                .build();
        Attraction carrusel = new Attraction.Builder("2", "Carrusel", AttractionType.KIDS)
                .build();
        Attraction tronquitos = new Attraction.Builder("3", "Tronquitos", AttractionType.AQUATIC)
                .build();

        // 3. Crear las conexiones (Grafos)
        // Conectamos Montaña -> Carrusel (50 metros)
        montana.addNeighbor(carrusel, 50.0);
        // Conectamos Carrusel -> Tronquitos (30 metros)
        carrusel.addNeighbor(tronquitos, 30.0);
        // Conectamos Tronquitos -> Montaña (100 metros) para cerrar el ciclo
        tronquitos.addNeighbor(montana, 100.0);

        // 4. Agregarlas a la lista
        listaAtracciones.addLast(montana);
        listaAtracciones.addLast(carrusel);
        listaAtracciones.addLast(tronquitos);

        // 5. Lanzar el visualizador
        VisualizadorGrafos ventana = new VisualizadorGrafos(listaAtracciones);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setTitle("Mapa del TechPark - Vista de Grafo");
        ventana.setVisible(true);

        System.out.println("Visualizador iniciado con " + listaAtracciones.size() + " nodos.");
    }
}