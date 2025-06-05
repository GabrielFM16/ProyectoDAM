package com.cesur.biblioteca.dao;

import com.cesur.biblioteca.modelo.Usuario;
import java.util.List;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public interface UsuarioDAOInterface {
    void addUsuario(Usuario usuario); // Crear usuario
    Usuario getUsuarioById(int id); // Obtener usuario por ID de usuario
    Usuario getUsuarioByEmail(String email); // Obtener usuario por email de usuario
    List<Usuario> getAllUsuarios(); // Obtener todos los usuarios
    void updateUsuario(Usuario usuario); // Actualizar usuario
    boolean deleteUsuario(int id); // Eliminar usuario
}