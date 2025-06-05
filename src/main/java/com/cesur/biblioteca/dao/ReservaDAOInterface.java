package com.cesur.biblioteca.dao;

import com.cesur.biblioteca.modelo.Reserva;
import java.util.List;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public interface ReservaDAOInterface {
    void addReserva(Reserva reserva); // Crear reserva
    Reserva getReservaById(int id); // Obtener reserva por ID de reserva
    List<Reserva> getReservasByLibroId(int libroId); // Obtener reservas por ID del libro
    List<Reserva> getReservasByLibroIsbn(String isbn); // Obtener reservas por ISBN del libro
    List<Reserva> getReservasByUsuarioId(int usuarioId); // Obtener reservas por ID de usuario
    List<Reserva> getReservasByUsuarioEmail(String email); // Obtener reservas por email de usuario
    List<Reserva> getAllReservas(); // Obtener todas las reservas
    void updateReserva(Reserva reserva); // Actualizar reserva
    boolean deleteReserva(int id); // Eliminar reserva
}