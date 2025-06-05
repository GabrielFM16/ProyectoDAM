package com.cesur.biblioteca.dao;

import com.cesur.biblioteca.modelo.Usuario;
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

public class UsuarioDAO implements UsuarioDAOInterface {
    
    // Constructor por defecto
    public UsuarioDAO() {
    }
    
    @Override
    public void addUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, apellidos, email, telefono, activo, sancionado, fecha_fin_sancion) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellidos());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getTelefono());
            pstmt.setBoolean(5, usuario.isActivo());
            pstmt.setBoolean(6, usuario.isSancionado());
            pstmt.setDate(7, usuario.getFechaFinSancion()); // Si es null, JDBC lo manejará bien.

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getInt(1)); // Asigna el ID generado al objeto Usuario
                    }
                }
                System.out.println("Usuario añadido exitosamente: " + usuario.getNombre() + " " + usuario.getApellidos());
            }

        } catch (SQLException e) {
            System.err.println("Error al añadir usuario: " + e.getMessage());
        }
    }

    @Override
    public Usuario getUsuarioById(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        Usuario usuario = null;
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("email"),
                        rs.getString("telefono"),
                        rs.getBoolean("activo"),
                        rs.getBoolean("sancionado"),
                        rs.getDate("fecha_fin_sancion") // Obtiene la fecha fin de sanción. Null en caso de no estar sancionado.
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
        }
        return usuario;
    }

    @Override
    public Usuario getUsuarioByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        Usuario usuario = null;
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("email"),
                        rs.getString("telefono"),
                        rs.getBoolean("activo"),
                        rs.getBoolean("sancionado"),
                        rs.getDate("fecha_fin_sancion")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por email: " + e.getMessage());
        }
        return usuario;
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection conn = ConexionBBDD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Usuario usuario = new Usuario(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("apellidos"),
                    rs.getString("email"),
                    rs.getString("telefono"),
                    rs.getBoolean("activo"),
                    rs.getBoolean("sancionado"),
                    rs.getDate("fecha_fin_sancion")
                );
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    @Override
    public void updateUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET nombre = ?, apellidos = ?, email = ?, telefono = ?, activo = ?, sancionado = ?, fecha_fin_sancion = ? WHERE id = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellidos());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, usuario.getTelefono());
            pstmt.setBoolean(5, usuario.isActivo());
            pstmt.setBoolean(6, usuario.isSancionado());
            pstmt.setDate(7, usuario.getFechaFinSancion());
            pstmt.setInt(8, usuario.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Usuario actualizado exitosamente: " + usuario.getNombre() + " " + usuario.getApellidos());
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteUsuario(int id) {
        boolean resultado = false;
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                resultado = true;
                System.out.println("Usuario con ID " + id + " eliminado exitosamente.");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
        return resultado;
    }
}