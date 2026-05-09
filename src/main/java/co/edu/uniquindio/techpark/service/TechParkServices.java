package co.edu.uniquindio.techpark.service;

import co.edu.uniquindio.techpark.dataStructures.SimpleLinkedList;
import co.edu.uniquindio.techpark.model.entities.Attraction;
import co.edu.uniquindio.techpark.model.entities.Ticket;
import co.edu.uniquindio.techpark.model.entities.Visitor;
import co.edu.uniquindio.techpark.model.entities.Zone;
import co.edu.uniquindio.techpark.model.enums.AttractionStatus;
import co.edu.uniquindio.techpark.model.enums.AttractionType;
import co.edu.uniquindio.techpark.model.enums.TicketType;

public class TechParkServices implements ITechParkServices {

    @Override
    public void registrarVisitante(String cedula, String nombre, int edad, double estatura, double saldoInicial) {

    }

    @Override
    public void eliminarVisitante(String cedula) {

    }

    @Override
    public void editarDatosVisitante(String cedula, String nombre, int edad, double estatura) {

    }

    @Override
    public Visitor iniciarSesionVisitante(String cedula) {
        return null;
    }

    @Override
    public boolean iniciarSesionAdmin(String email, String contrasena) {
        return false;
    }

    @Override
    public void crearOperador(String cedula, String nombre, String idZona) {

    }

    @Override
    public Ticket comprarTicket(String cedulaVisitante, TicketType tipo, double precioBase, double descuento) throws Exception {
        return null;
    }

    @Override
    public void recargarSaldoVirtual(String cedulaVisitante, double monto) {

    }

    @Override
    public double consultarSaldo(String cedulaVisitante) {
        return 0;
    }

    @Override
    public void crearZona(String id, String nombre, int capacidadMaxima) {

    }

    @Override
    public void crearAtraccion(String id, String nombre, AttractionType tipo, int capacidadCiclo, double alturaMin, int edadMin, double costoAdicional, String idZona) {

    }

    @Override
    public void eliminarAtraccion(String idAtraccion) {

    }

    @Override
    public void cambiarEstadoAtraccion(String idAtraccion, AttractionStatus nuevoEstado, String motivo) {

    }

    @Override
    public void realizarRegistroEnCola(String cedulaVisitante, String idAtraccion) throws Exception {

    }

    @Override
    public SimpleLinkedList<Visitor> procesarEntradaAtraccion(String idAtraccion) {
        return null;
    }

    @Override
    public void conectarAtracciones(String idAtraccionA, String idAtraccionB, double peso) {

    }

    @Override
    public SimpleLinkedList<Attraction> obtenerRutaOptima(String idOrigen, String idDestino) {
        return null;
    }

    @Override
    public void registrarRevisionTecnica(String idAtraccion, String idOperador, String observaciones) {

    }

    @Override
    public void activarAlertaClimatica(boolean hayTormenta) {

    }

    @Override
    public Attraction buscarAtraccionPorNombre(String nombre) {
        return null;
    }

    @Override
    public void agregarAtraccionAFavoritos(String cedulaVisitante, String idAtraccion) {

    }

    @Override
    public SimpleLinkedList<Attraction> listarAtraccionesPorZona(String idZona) {
        return null;
    }

    @Override
    public SimpleLinkedList<Attraction> obtenerAtraccionesMasVisitadas() {
        return null;
    }

    @Override
    public SimpleLinkedList<String> obtenerAlertasMantenimiento() {
        return null;
    }

    @Override
    public double calcularIngresosDiarios() {
        return 0;
    }

    @Override
    public void cargarDatosIniciales(String nombreArchivo) throws Exception {

    }

    @Override
    public SimpleLinkedList<Attraction> listarTodasLasAtracciones() {
        return null;
    }

    @Override
    public SimpleLinkedList<Zone> listarTodasLasZonas() {
        return null;
    }

    @Override
    public SimpleLinkedList<Visitor> listarVisitantes() {
        return null;
    }
}