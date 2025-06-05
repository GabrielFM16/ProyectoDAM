
package com.cesur.biblioteca.modelo;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private String genero;
    private int anioPublicacion;
    private boolean disponible; // Corresponde con la columna 'disponible' en la BBDD
    private int numEjemplares;

    // Constructor por defecto
    public Libro() {
        this.id = 0;
        this.titulo = "";
        this.autor = "";
        this.isbn = "";
        this.genero = "";
        this.anioPublicacion = 0;
        this.disponible = false;
        this.numEjemplares = 0;
    }

    // Constructor parametrico
    public Libro(int id, String titulo, String autor, String isbn, String genero, int anioPublicacion, boolean disponible, int numEjemplares) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.genero = genero;
        this.anioPublicacion = anioPublicacion;
        this.disponible = (disponible && numEjemplares > 0);
        this.numEjemplares = numEjemplares;
    }

    // Constructor sin 'id' (para cuando se crea un nuevo libro antes de guardarlo en la BBDD)
    public Libro(String titulo, String autor, String isbn, String genero, int anioPublicacion, boolean disponible, int numEjemplares) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.genero = genero;
        this.anioPublicacion = anioPublicacion;
        this.disponible = (disponible && numEjemplares > 0);
        this.numEjemplares = numEjemplares;
    }

    // --- Getters y Setters ---
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return this.autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenero() {
        return this.genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getAnioPublicacion() {
        return this.anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public boolean isDisponible() {
        return (this.disponible && this.numEjemplares > 0);
    }

    public void setDisponible(boolean disponible) {
        this.disponible = (disponible && this.numEjemplares > 0);
    }
    
    public int getNumEjemplares() {
        return numEjemplares;
    }

    public void setNumEjemplares(int numEjemplares) {
        this.numEjemplares = numEjemplares;
    }

    // --- Método toString() para una representación legible del objeto ---
    @Override
    public String toString() {
        return "Libro{" +
               "id=" + this.id +
               ", titulo='" + this.titulo + '\'' +
               ", autor='" + this.autor + '\'' +
               ", isbn='" + this.isbn + '\'' +
               ", genero='" + this.genero + '\'' +
               ", anioPublicacion=" + this.anioPublicacion +
               ", disponible=" + this.disponible +
               ", ejemplares=" + this.numEjemplares +
               '}';
    }
}