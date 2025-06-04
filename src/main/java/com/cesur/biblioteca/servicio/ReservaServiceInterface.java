package com.cesur.biblioteca.servicio;

import com.cesur.biblioteca.modelo.Reserva;
import java.util.List;
import java.sql.Date;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public interface ReservaServiceInterface {
    void crearReserva(Reserva reserva);
    Reserva buscarReservaPorId(int id);
    List<Reserva> obtenerReservasPorLibroID(int libroId);
    List<Reserva> obtenerReservasPorLibroIsbn(String libroIsbn);
    List<Reserva> obtenerReservasPorUsuarioID(int usuarioId);
    List<Reserva> obtenerReservasPorUsuarioEmail(String usuarioEmail);
    List<Reserva> obtenerTodasLasReservas();
    void actualizarReserva(Reserva reserva, String CambioEstado);
    // Métodos adicionales
    void cancelarReserva(int id);
    void finalizarReserva(int id); // Para marcar una reserva como completada/devuelta manualmente
    boolean eliminarReserva(int id);  // Para eliminar una reserva de la BBDD
    void registrarPrestamo(int libroId, int usuarioId, Date fechaDevolucionPrevista); // Simplifica el proceso de préstamo
    void registrarDevolucion(int reservaId, Date fechaDevolucionReal); // Registra la devolución de un libro
}