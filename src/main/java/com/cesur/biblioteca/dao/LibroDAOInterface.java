package com.cesur.biblioteca.dao;

import com.cesur.biblioteca.modelo.Libro;
import java.util.List;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public interface LibroDAOInterface {
    void addLibro(Libro libro); // Crear libro
    Libro getLibroById(int id); // Obtener libro por ID del libro
    Libro getLibroByIsbn(String isbn); // Obtener libro por ISBN del libro
    List<Libro> getAllLibros(); // Obtener todos los libros
    void updateLibro(Libro libro); // Actualizar libro
    boolean deleteLibro(int id); // Eliminar libro
}