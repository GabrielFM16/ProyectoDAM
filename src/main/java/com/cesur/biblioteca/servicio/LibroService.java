package com.cesur.biblioteca.servicio;

import com.cesur.biblioteca.dao.LibroDAO;
import com.cesur.biblioteca.modelo.Libro;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class LibroService implements LibroServiceInterface {
    private final LibroDAO libroDAO; // Dependencia del DAO

    // Constructor paramétrico para inyectar el DAO
    public LibroService(LibroDAO libroDAO) {
        this.libroDAO = libroDAO;
    }

    @Override
    public void añadirLibro(Libro libro) {
        // Lógica de negocio: Validaciones antes de añadir
        if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del libro no puede estar vacío.");
        }
        if (libro.getAutor() == null || libro.getAutor().trim().isEmpty()) {
            throw new IllegalArgumentException("El autor del libro no puede estar vacío.");
        }
        if (libro.getIsbn() == null || libro.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("El ISBN del libro no puede estar vacío.");
        }
        if (libroDAO.getLibroByIsbn(libro.getIsbn()) != null) {
            throw new IllegalArgumentException("Ya existe un libro con el ISBN: " + libro.getIsbn());
        }

        libroDAO.addLibro(libro);
        System.out.println("Servicio: Libro '" + libro.getTitulo() + "' añadido exitosamente.");
    }

    @Override
    public Libro buscarLibroPorId(int id) {
        return libroDAO.getLibroById(id);
    }

    @Override
    public Libro buscarLibroPorIsbn(String isbn) {
        return libroDAO.getLibroByIsbn(isbn);
    }

    @Override
    public List<Libro> obtenerTodosLosLibros() {
        return libroDAO.getAllLibros();
    }

    @Override
    public List<Libro> obtenerLibrosDisponibles() {
        List<Libro> todosLosLibros = libroDAO.getAllLibros();
        List<Libro> librosDisponibles = new ArrayList<>();
        for (Libro l : todosLosLibros) {
            if (l.isDisponible()) {
                librosDisponibles.add(l);
            }
        }
        return librosDisponibles;
    }

    @Override
    public void actualizarLibro(Libro libro) {
        if (libro.getId() == 0 || libroDAO.getLibroById(libro.getId()) == null) {
            throw new IllegalArgumentException("El libro a actualizar no existe o su ID es inválido.");
        }
        // Validar ISBN único al actualizar si cambia
        Libro existingLibroByIsbn = libroDAO.getLibroByIsbn(libro.getIsbn());
        if (existingLibroByIsbn != null && existingLibroByIsbn.getId() != libro.getId()) {
            throw new IllegalArgumentException("El ISBN '" + libro.getIsbn() + "' ya pertenece a otro libro.");
        } else if (existingLibroByIsbn == null) {
            throw new IllegalArgumentException("No se encuentra el libro con ISBN '" + libro.getIsbn() + "'.");
        } else {
            libroDAO.updateLibro(libro);
            System.out.println("Servicio: Libro con ID " + libro.getId() + " actualizado exitosamente.");
        }
    }

    @Override
    public boolean eliminarLibro(int id) {
    boolean resultado = false;
    Libro target = null;
    List<Libro> librosDisponibles = this.obtenerLibrosDisponibles();
    for (Libro l : librosDisponibles) {
        if (l.getId() == id) {
            target = l;
            break; // Si se encuentra el libro buscado, salimos del bucle.
        }
    }

    if (target != null) {
        if (target.getNumEjemplares() > 1) { //Si hay varios ejemplares del mismo libro, restamos un ejemplar.
            target.setNumEjemplares(target.getNumEjemplares() - 1);
            libroDAO.updateLibro(target); // Actualizamos el registro de la base de datos con la nueva cantidad.
            resultado = true;
        } else { // Si únicamente queda un ejemplar del libro, eliminamos el registro de la base de datos.
            resultado = libroDAO.deleteLibro(id);
        }
        System.out.println("Servicio: Libro con ID " + id + " eliminado.");
    } else {
        throw new IllegalArgumentException("El Libro con ID " + id +
                " no está disponible. Verifique que exista y que no tenga reservas activas.");
    }
    return resultado;
}

    @Override
    public void marcarLibroDisponible(int libroId) {
        Libro libro = libroDAO.getLibroById(libroId);
        if (libro != null) {
            libro.setDisponible(true);
            libroDAO.updateLibro(libro);
            System.out.println("Servicio: Libro ID " + libroId + " marcado como disponible.");
        } else {
            System.err.println("Servicio: No se encontró el libro con ID " + libroId + " para marcarlo como disponible.");
        }
    }

    @Override
    public void marcarLibroNoDisponible(int libroId) {
        Libro libro = libroDAO.getLibroById(libroId);
        if (libro != null) {
            libro.setDisponible(false);
            libroDAO.updateLibro(libro);
            System.out.println("Servicio: Libro ID " + libroId + " marcado como no disponible.");
        } else {
            System.err.println("Servicio: No se encontró el libro con ID " + libroId + " para marcarlo como no disponible.");
        }
    }
}