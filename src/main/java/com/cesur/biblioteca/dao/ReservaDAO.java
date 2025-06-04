package com.cesur.biblioteca.dao;

import com.cesur.biblioteca.modelo.Reserva;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class ReservaDAO implements ReservaDAOInterface { // Asegúrate de que implementa tu nueva interfaz
    
    // Constructor por defecto
    public ReservaDAO() {
    }
    
    @Override
    public void addReserva(Reserva reserva) {
        String sql = "INSERT INTO reservas (libro_id, usuario_id, fecha_reserva, fecha_devolucion_prevista, fecha_devolucion_real, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, reserva.getLibroId());
            pstmt.setInt(2, reserva.getUsuarioId());
            pstmt.setDate(3, reserva.getFechaReserva());
            pstmt.setDate(4, reserva.getFechaDevolucionPrevista());
            pstmt.setDate(5, reserva.getFechaDevolucionReal()); // Puede ser null
            pstmt.setString(6, reserva.getEstado());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reserva.setId(rs.getInt(1)); // Asigna el ID generado al objeto Reserva
                    }
                }
                System.out.println("Reserva añadida exitosamente para Libro ID: " + reserva.getLibroId() + " y Usuario ID: " + reserva.getUsuarioId());
            }

        } catch (SQLException e) {
            System.err.println("Error al añadir reserva: " + e.getMessage());
        }
    }

    @Override
    public Reserva getReservaById(int id) {
        String sql = "SELECT * FROM reservas WHERE id = ?";
        Reserva reserva = null;
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    reserva = new Reserva(
                        rs.getInt("id"),
                        rs.getInt("libro_id"),
                        rs.getInt("usuario_id"),
                        rs.getDate("fecha_reserva"),
                        rs.getDate("fecha_devolucion_prevista"),
                        rs.getDate("fecha_devolucion_real"),
                        rs.getString("estado")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reserva por ID de reserva: " + e.getMessage());
        }
        return reserva;
    }

    @Override
    public List<Reserva> getReservasByLibroId(int libroId) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE libro_id = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, libroId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = new Reserva(
                        rs.getInt("id"),
                        rs.getInt("libro_id"),
                        rs.getInt("usuario_id"),
                        rs.getDate("fecha_reserva"),
                        rs.getDate("fecha_devolucion_prevista"),
                        rs.getDate("fecha_devolucion_real"),
                        rs.getString("estado")
                    );
                    reservas.add(reserva);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reservas por ID del libro: " + e.getMessage());
        }
        return reservas;
    }

    @Override
    public List<Reserva> getReservasByLibroIsbn(String isbn) {
        List<Reserva> reservas = new ArrayList<>();
        // Unimos con la tabla libros para obtener el libro_id a partir del ISBN
        String sql = "SELECT r.* FROM reservas r JOIN libros l ON r.libro_id = l.id WHERE l.isbn = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, isbn);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = new Reserva(
                        rs.getInt("id"),
                        rs.getInt("libro_id"),
                        rs.getInt("usuario_id"),
                        rs.getDate("fecha_reserva"),
                        rs.getDate("fecha_devolucion_prevista"),
                        rs.getDate("fecha_devolucion_real"),
                        rs.getString("estado")
                    );
                    reservas.add(reserva);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reservas por ISBN del libro: " + e.getMessage());
        }
        return reservas;
    }

    @Override
    public List<Reserva> getReservasByUsuarioId(int usuarioId) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE usuario_id = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, usuarioId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = new Reserva(
                        rs.getInt("id"),
                        rs.getInt("libro_id"),
                        rs.getInt("usuario_id"),
                        rs.getDate("fecha_reserva"),
                        rs.getDate("fecha_devolucion_prevista"),
                        rs.getDate("fecha_devolucion_real"),
                        rs.getString("estado")
                    );
                    reservas.add(reserva);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reservas por ID de usuario: " + e.getMessage());
        }
        return reservas;
    }

    @Override
    public List<Reserva> getReservasByUsuarioEmail(String email) {
        List<Reserva> reservas = new ArrayList<>();
        // Unimos con la tabla usuarios para obtener el usuario_id a partir del email
        String sql = "SELECT r.* FROM reservas r JOIN usuarios u ON r.usuario_id = u.id WHERE u.email = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reserva reserva = new Reserva(
                        rs.getInt("id"),
                        rs.getInt("libro_id"),
                        rs.getInt("usuario_id"),
                        rs.getDate("fecha_reserva"),
                        rs.getDate("fecha_devolucion_prevista"),
                        rs.getDate("fecha_devolucion_real"),
                        rs.getString("estado")
                    );
                    reservas.add(reserva);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener reservas por Email de usuario: " + e.getMessage());
        }
        return reservas;
    }

    @Override
    public List<Reserva> getAllReservas() {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas";
        try (Connection conn = ConexionBBDD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reserva reserva = new Reserva(
                    rs.getInt("id"),
                    rs.getInt("libro_id"),
                    rs.getInt("usuario_id"),
                    rs.getDate("fecha_reserva"),
                    rs.getDate("fecha_devolucion_prevista"),
                    rs.getDate("fecha_devolucion_real"),
                    rs.getString("estado")
                );
                reservas.add(reserva);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las reservas: " + e.getMessage());
        }
        return reservas;
    }

    @Override
    public void updateReserva(Reserva reserva) {
        String sql = "UPDATE reservas SET libro_id = ?, usuario_id = ?, fecha_reserva = ?, fecha_devolucion_prevista = ?, fecha_devolucion_real = ?, estado = ? WHERE id = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reserva.getLibroId());
            pstmt.setInt(2, reserva.getUsuarioId());
            pstmt.setDate(3, reserva.getFechaReserva());
            pstmt.setDate(4, reserva.getFechaDevolucionPrevista());
            pstmt.setDate(5, reserva.getFechaDevolucionReal());
            pstmt.setString(6, reserva.getEstado());
            pstmt.setInt(7, reserva.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Reserva actualizada exitosamente con ID: " + reserva.getId());
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar reserva: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteReserva(int id) {
        boolean resultado = false;
        String sql = "DELETE FROM reservas WHERE id = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                resultado = true;
                System.out.println("Reserva con ID " + id + " eliminada exitosamente.");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar reserva: " + e.getMessage());
        }
        return resultado;
    }
}