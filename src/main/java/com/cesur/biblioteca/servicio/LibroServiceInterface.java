package com.cesur.biblioteca.servicio;

import com.cesur.biblioteca.modelo.Libro;
import java.util.List;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public interface LibroServiceInterface {
    void añadirLibro(Libro libro);
    Libro buscarLibroPorId(int id);
    Libro buscarLibroPorIsbn(String isbn);
    List<Libro> obtenerTodosLosLibros();
    List<Libro> obtenerLibrosDisponibles(); // Lógica de negocio: Filtrar por disponibilidad
    void actualizarLibro(Libro libro);
    boolean eliminarLibro(int id);
    // Métodos adicionales
    void marcarLibroDisponible(int libroId);
    void marcarLibroNoDisponible(int libroId);
}