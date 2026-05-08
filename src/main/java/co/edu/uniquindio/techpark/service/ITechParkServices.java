package co.edu.uniquindio.techpark.service;

import co.edu.uniquindio.techpark.model.entities.*;
import co.edu.uniquindio.techpark.model.enums.*;
import co.edu.uniquindio.techpark.dataStructures.*;
import java.time.LocalDate;

public interface ITechParkServices {

    // --- GESTIÓN DE USUARIOS (VISITANTES Y EMPLEADOS) ---
    void registrarVisitante(String cedula, String nombre, int edad, double estatura, double saldoInicial);

    void eliminarVisitante(String cedula);

    void editarDatosVisitante(String cedula, String nombre, int edad, double estatura);

    Visitor iniciarSesionVisitante(String cedula); // O con contraseña si la implementas

    boolean iniciarSesionAdmin(String email, String contrasena);

    void crearOperador(String cedula, String nombre, String idZona);

    // --- GESTIÓN DE TICKETS Y SALDO ---
    Ticket comprarTicket(String cedulaVisitante, TicketType tipo, double precioBase, double descuento) throws Exception;

    void recargarSaldoVirtual(String cedulaVisitante, double monto);

    double consultarSaldo(String cedulaVisitante);

    // --- GESTIÓN DEL PARQUE (ZONAS Y ATRACCIONES) ---
    void crearZona(String id, String nombre, int capacidadMaxima);

    void crearAtraccion(String id, String nombre, AttractionType tipo, int capacidadCiclo,
                        double alturaMin, int edadMin, double costoAdicional, String idZona);

    void eliminarAtraccion(String idAtraccion);

    void cambiarEstadoAtraccion(String idAtraccion, AttractionStatus nuevoEstado, String motivo);

    // --- LÓGICA DE COLAS Y ACCESO ---
    /**
     * Valida restricciones y saldo. Si cumple, lo agrega a la PriorityQueue de la atracción.
     */
    void realizarRegistroEnCola(String cedulaVisitante, String idAtraccion) throws Exception;

    /**
     * El operador procesa al siguiente grupo según la capacidad de la atracción.
     */
    SimpleLinkedList<Visitor> procesarEntradaAtraccion(String idAtraccion);

    // --- MAPA Y RUTAS (GRAFOS) ---
    /**
     * Crea un sendero entre dos atracciones con una distancia/tiempo específico.
     */
    void conectarAtracciones(String idAtraccionA, String idAtraccionB, double peso);

    /**
     * Algoritmo de Dijkstra para mostrar al usuario cómo llegar.
     */
    SimpleLinkedList<Attraction> obtenerRutaOptima(String idOrigen, String idDestino);

    // --- MANTENIMIENTO Y CLIMA ---
    void registrarRevisionTecnica(String idAtraccion, String idOperador, String observaciones);

    /**
     * Cierre masivo de atracciones mecánicas y acuáticas.
     */
    void activarAlertaClimatica(boolean hayTormenta);

    // --- BÚSQUEDAS Y FAVORITOS ---
    /**
     * Búsqueda rápida usando tu Árbol Binario (ABB).
     */
    Attraction buscarAtraccionPorNombre(String nombre);

    void agregarAtraccionAFavoritos(String cedulaVisitante, String idAtraccion);

    // --- REPORTES Y CONSULTAS (USO DE LISTAS Y RECORRIDOS) ---
    SimpleLinkedList<Attraction> listarAtraccionesPorZona(String idZona);

    SimpleLinkedList<Attraction> obtenerAtraccionesMasVisitadas();

    SimpleLinkedList<String> obtenerAlertasMantenimiento(); // Atracciones >= 500 visitas

    double calcularIngresosDiarios();

    // --- PERSISTENCIA / CARGA ---
    void cargarDatosIniciales(String nombreArchivo) throws Exception;

    // Getters de estructuras completas para la GUI
    SimpleLinkedList<Attraction> listarTodasLasAtracciones();
    SimpleLinkedList<Zone> listarTodasLasZonas();
    SimpleLinkedList<Visitor> listarVisitantes();
}