package com.cesur.biblioteca.servicio;

import com.cesur.biblioteca.modelo.Usuario;
import java.util.List;
import java.sql.Date;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public interface UsuarioServiceInterface {
    void añadirUsuario(Usuario usuario);
    Usuario buscarUsuarioPorId(int id);
    Usuario buscarUsuarioPorEmail(String email);
    List<Usuario> obtenerTodosUsuarios();
    void actualizarUsuario(Usuario usuario);
    boolean eliminarUsuario(int id);
    // Métodos adicionales
    void activarUsuario(int id);
    void desactivarUsuario(int id);
    void sancionarUsuario(int id, Date fechaFinSancion);
    void eliminarSancionUsuario(int id);
    List<Usuario> obtenerTodosUsuariosActivos();
    List<Usuario> obtenerTodosUsuariosSancionados();
}