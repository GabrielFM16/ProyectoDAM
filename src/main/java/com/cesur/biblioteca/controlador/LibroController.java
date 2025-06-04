package com.cesur.biblioteca.controlador;

import com.cesur.biblioteca.modelo.Libro;
import com.cesur.biblioteca.servicio.LibroService;

import java.util.List;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class LibroController {
    private final LibroService libroService;

    // Constructor para inyectar la dependencia del servicio
    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    /**
     * Añade un nuevo libro al sistema.
     * @param titulo El título del libro.
     * @param autor El autor del libro.
     * @param isbn El ISBN del libro.
     * @param genero El género del libro.
     * @param anioPublicacion El año de publicación del libro.
     * @param numEjemplares El número de ejemplares del libro.
     * @return El objeto Libro añadido, con su ID generado.
     * @throws IllegalArgumentException si los datos de entrada son inválidos.
     * @throws IllegalStateException si ya existe un libro con el mismo ISBN.
     */
    public Libro añadirLibro(String titulo, String autor, String isbn, String genero, int anioPublicacion, int numEjemplares) {
        // Validación básica de parámetros antes de pasar al servicio
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("El autor no puede estar vacío.");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("El ISBN no puede estar vacío.");
        }
        if (anioPublicacion <= 0) {
            throw new IllegalArgumentException("El año de publicación debe ser un número positivo.");
        }
        if (numEjemplares <= 0) {
            throw new IllegalArgumentException("El número de ejemplares debe ser mayor de 0.");
        }

        Libro nuevoLibro = new Libro(titulo, autor, isbn, genero, anioPublicacion, true, numEjemplares);
        libroService.añadirLibro(nuevoLibro);
        return nuevoLibro;
    }

    /**
     * Busca un libro por su ID.
     * @param id El ID del libro.
     * @return El objeto Libro encontrado, o null si no existe.
     */
    public Libro buscarLibroPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del libro debe ser un número positivo.");
        }
        return libroService.buscarLibroPorId(id);
    }

    /**
     * Busca un libro por su ISBN.
     * @param isbn El ISBN del libro.
     * @return El objeto Libro encontrado, o null si no existe.
     */
    public Libro buscarLibroPorIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("El ISBN no puede estar vacío para la búsqueda.");
        }
        return libroService.buscarLibroPorIsbn(isbn);
    }

    /**
     * Obtiene una lista de todos los libros en el sistema.
     * @return Una lista de objetos Libro.
     */
    public List<Libro> obtenerTodosLosLibros() {
        return libroService.obtenerTodosLosLibros();
    }

    /**
     * Obtiene una lista de todos los libros disponibles en el sistema.
     * @return Una lista de objetos Libro disponibles.
     */
    public List<Libro> obtenerLibrosDisponibles() {
        return libroService.obtenerLibrosDisponibles();
    }

    /**
     * Actualiza la información de un libro existente.
     * @param id El ID del libro a actualizar.
     * @param titulo Nuevo título (puede ser null para no cambiar).
     * @param autor Nuevo autor (puede ser null para no cambiar).
     * @param isbn Nuevo ISBN (puede ser null para no cambiar).
     * @param genero Nuevo género (puede ser null para no cambiar).
     * @param anioPublicacion Nuevo año de publicación (0 para no cambiar).
     * @param disponible Nueva disponibilidad (null para no cambiar).
     * @param numEjemplares El número de ejemplares del libro.
     * @throws IllegalArgumentException si los datos de entrada son inválidos o el libro no existe.
     * @throws IllegalStateException si la actualización viola alguna regla de negocio.
     */
    public void actualizarLibro(int id, String titulo, String autor, String isbn, String genero, int anioPublicacion, Boolean disponible, int numEjemplares) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del libro debe ser un número positivo para actualizarlo.");
        }
        Libro libroExistente = libroService.buscarLibroPorId(id);
        if (libroExistente == null) {
            throw new IllegalArgumentException("Libro con ID " + id + " no encontrado para actualizar.");
        }

        // Lógica de actualización de campos solo si se proporcionan
        if (titulo != null && !titulo.trim().isEmpty()) {
            libroExistente.setTitulo(titulo);
        }
        if (autor != null && !autor.trim().isEmpty()) {
            libroExistente.setAutor(autor);
        }
        if (isbn != null && !isbn.trim().isEmpty()) {
            libroExistente.setIsbn(isbn);
        }
        if (genero != null && !genero.trim().isEmpty()) {
            libroExistente.setGenero(genero);
        }
        if (anioPublicacion > 0) {
            libroExistente.setAnioPublicacion(anioPublicacion);
        }
        if (disponible != null) {
            libroExistente.setDisponible(disponible);
        }
        if (numEjemplares >= 0) {
            libroExistente.setNumEjemplares(numEjemplares);
        }

        libroService.actualizarLibro(libroExistente);
    }

    /**
     * Elimina un libro del sistema por su ID.
     * @param id El ID del libro a eliminar.
     * @return valor booleano true/false resultado de la operación.
     * @throws IllegalArgumentException si el ID es inválido o el libro no existe.
     * @throws IllegalStateException si el libro no puede ser eliminado (ej. tiene reservas activas).
     */
    public boolean eliminarLibro(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del libro debe ser un número positivo para eliminarlo.");
        }
        return libroService.eliminarLibro(id);
    }
    
    /**
     * Marca un libro como disponible en el sistema por su ID.
     * @param id El ID del libro a marcar.
     * @throws IllegalArgumentException si el ID es inválido o el libro no existe.
     */
    public void marcarLibroDisponible(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del libro debe ser un número positivo para actualizarlo.");
        }
        libroService.marcarLibroDisponible(id);
    }
    
    /**
     * Marca un libro como no disponible en el sistema por su ID.
     * @param id El ID del libro a marcar.
     * @throws IllegalArgumentException si el ID es inválido o el libro no existe.
     */
    public void marcarLibroNoDisponible(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del libro debe ser un número positivo para actualizarlo.");
        }
        libroService.marcarLibroNoDisponible(id);
    }
}