package com.cesur.biblioteca.dao;

import com.cesur.biblioteca.modelo.Libro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class LibroDAO implements LibroDAOInterface {
    
    // Constructor por defecto
    public LibroDAO() {
    }
    
    @Override
    public void addLibro(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, isbn, genero, anio_publicacion, numero_ejemplares, disponible) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getAutor());
            pstmt.setString(3, libro.getIsbn());
            pstmt.setString(4, libro.getGenero());
            pstmt.setInt(5, libro.getAnioPublicacion());
            pstmt.setInt(6, libro.getNumEjemplares());
            pstmt.setBoolean(7, libro.isDisponible());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        libro.setId(rs.getInt(1)); // Asigna el ID generado al objeto Libro
                    }
                }
                System.out.println("Libro añadido exitosamente: " + libro.getTitulo());
            }

        } catch (SQLException e) {
            System.err.println("Error al añadir libro: " + e.getMessage());
        }
    }

    @Override
    public Libro getLibroById(int id) {
        String sql = "SELECT * FROM libros WHERE id = ?";
        Libro libro = null;
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getString("genero"),
                        rs.getInt("anio_publicacion"),
                        rs.getBoolean("disponible"),
                        rs.getInt("numero_ejemplares")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener libro por ID: " + e.getMessage());
        }
        return libro;
    }

    @Override
    public Libro getLibroByIsbn(String isbn) {
        String sql = "SELECT * FROM libros WHERE isbn = ?";
        Libro libro = null;
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("isbn"),
                        rs.getString("genero"),
                        rs.getInt("anio_publicacion"),
                        rs.getBoolean("disponible"),
                        rs.getInt("numero_ejemplares")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener libro por ISBN: " + e.getMessage());
        }
        return libro;
    }

    @Override
    public List<Libro> getAllLibros() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        try (Connection conn = ConexionBBDD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Libro libro = new Libro(
                    rs.getInt("id"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("isbn"),
                    rs.getString("genero"),
                    rs.getInt("anio_publicacion"),
                    rs.getBoolean("disponible"),
                    rs.getInt("numero_ejemplares")
                );
                libros.add(libro);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los libros: " + e.getMessage());
        }
        return libros;
    }

    @Override
    public void updateLibro(Libro libro) {
        String sql = "UPDATE libros SET titulo = ?, autor = ?, isbn = ?, genero = ?, anio_publicacion = ?, numero_ejemplares = ?, disponible = ? WHERE id = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, libro.getTitulo());
            pstmt.setString(2, libro.getAutor());
            pstmt.setString(3, libro.getIsbn());
            pstmt.setString(4, libro.getGenero());
            pstmt.setInt(5, libro.getAnioPublicacion());
            pstmt.setInt(6, libro.getNumEjemplares());
            pstmt.setBoolean(7, libro.isDisponible());
            pstmt.setInt(8, libro.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Libro actualizado exitosamente: " + libro.getTitulo());
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar libro: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteLibro(int id) {
        boolean resultado = false;
        String sql = "DELETE FROM libros WHERE id = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                resultado = true;
                System.out.println("Libro con ID " + id + " eliminado exitosamente.");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
        }
        return resultado;
    }
}