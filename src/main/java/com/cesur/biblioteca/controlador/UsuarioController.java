package com.cesur.biblioteca.controlador;

import com.cesur.biblioteca.modelo.Usuario;
import com.cesur.biblioteca.servicio.UsuarioService;

import java.sql.Date;
import java.util.List;

/**
 *
 * @author Gabriel Fernandez Magan
 */


public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Añade un nuevo usuario al sistema.
     * @param nombre El nombre del usuario.
     * @param apellidos Los apellidos del usuario.
     * @param email El email del usuario.
     * @param telefono Nuevo telefono (puede ser null para no cambiar).
     * @return El objeto Usuario añadido, con su ID generado.
     * @throws IllegalArgumentException si los datos de entrada son inválidos o el usuario no existe.
     * @throws IllegalStateException si la actualización viola alguna regla de negocio.
     */
    public Usuario añadirUsuario(String nombre, String apellidos, String email, String telefono) {
        // Validación básica de parámetros
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos no pueden estar vacíos.");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) { // Validación de email básica
            throw new IllegalArgumentException("El email no es válido.");
        }

        Usuario nuevoUsuario = new Usuario(nombre, apellidos, email, telefono, true, false, null);
        usuarioService.añadirUsuario(nuevoUsuario);
        return nuevoUsuario;
    }

    /**
     * Busca un usuario por su ID.
     * @param id El ID del usuario.
     * @return El objeto Usuario encontrado, o null si no existe.
     */
    public Usuario buscarUsuarioPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo.");
        }
        return usuarioService.buscarUsuarioPorId(id);
    }

    /**
     * Busca un usuario por su email.
     * @param email El email del usuario.
     * @return El objeto Usuario encontrado, o null si no existe.
     */
    public Usuario buscarUsuarioPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío para la búsqueda.");
        }
        return usuarioService.buscarUsuarioPorEmail(email);
    }

    /**
     * Obtiene una lista de todos los usuarios.
     * @return Una lista de objetos Usuario.
     */
    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioService.obtenerTodosUsuarios();
    }

    /**
     * Actualiza la información de un usuario existente.
     * @param id El ID del usuario a actualizar.
     * @param nombre Nuevo nombre (puede ser null para no cambiar).
     * @param apellidos Nuevos apellidos (puede ser null para no cambiar).
     * @param email Nuevo email (puede ser null para no cambiar).
     * @param telefono Nuevo telefono (puede ser null para no cambiar).
     * @param activo Nuevo campo usuario activo (puede ser null para no cambiar).
     * @param sancionado Nuevo campo usuario sancionado (puede ser null para no cambiar).
     * @param fechaFinSancion Nueva fecha fin sanción (puede ser null para no cambiar).
     * @throws IllegalArgumentException si los datos de entrada son inválidos o el usuario no existe.
     * @throws IllegalStateException si la actualización viola alguna regla de negocio.
     */
    public void actualizarUsuario(int id, String nombre, String apellidos, String email, String telefono, 
                                    Boolean activo, Boolean sancionado, Date fechaFinSancion) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo para actualizarlo.");
        }
        Usuario usuarioExistente = usuarioService.buscarUsuarioPorId(id);
        if (usuarioExistente == null) {
            throw new IllegalArgumentException("Usuario con ID " + id + " no encontrado para actualizar.");
        }

        if (nombre != null && !nombre.trim().isEmpty()) {
            usuarioExistente.setNombre(nombre);
        }
        if (apellidos != null && !apellidos.trim().isEmpty()) {
            usuarioExistente.setApellidos(apellidos);
        }
        if (email != null && !email.trim().isEmpty()) {
            if (!email.contains("@") || !email.contains(".")) {
                throw new IllegalArgumentException("El nuevo email no es válido.");
            }
            usuarioExistente.setEmail(email);
        }
        if (telefono != null && !telefono.trim().isEmpty()) {
            usuarioExistente.setTelefono(telefono);
        }
        if (activo != null) {
            usuarioExistente.setActivo(activo);
        }
        if (sancionado != null) {
            usuarioExistente.setSancionado(sancionado);
        }
        if (fechaFinSancion != null && !fechaFinSancion.after(Date.valueOf(java.time.LocalDate.now()))) {
            throw new IllegalArgumentException("La fecha de fin de sanción debe ser una fecha posterior a hoy.");
        } else if (fechaFinSancion != null) {
            usuarioExistente.setFechaFinSancion(fechaFinSancion);
        }

        usuarioService.actualizarUsuario(usuarioExistente);
    }

    /**
     * Activa un usuario en el sistema.
     * @param id El ID del usuario a activar.
     * @throws IllegalArgumentException si el ID es inválido o el usuario no existe.
     * @throws IllegalStateException si el usuario ya está activo.
     */
    public void activarUsuario(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo para activarlo.");
        }
        usuarioService.activarUsuario(id);
    }

    /**
     * Desactiva un usuario en el sistema.
     * @param id El ID del usuario a desactivar.
     * @throws IllegalArgumentException si el ID es inválido o el usuario no existe.
     * @throws IllegalStateException si el usuario no puede ser desactivado (ej. tiene reservas pendientes).
     */
    public void desactivarUsuario(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo para desactivarlo.");
        }
        usuarioService.desactivarUsuario(id);
    }

    /**
     * Sanciona a un usuario.
     * @param id El ID del usuario a sancionar.
     * @param fechaFinSancion La fecha hasta la que el usuario estará sancionado.
     * @throws IllegalArgumentException si el ID es inválido, el usuario no existe o la fecha de fin es inválida.
     * @throws IllegalStateException si el usuario ya está sancionado.
     */
    public void sancionarUsuario(int id, Date fechaFinSancion) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo para sancionarlo.");
        }
        if (fechaFinSancion == null || !fechaFinSancion.after(Date.valueOf(java.time.LocalDate.now()))) {
            throw new IllegalArgumentException("La fecha de fin de sanción debe ser una fecha posterior a hoy.");
        }
        usuarioService.sancionarUsuario(id, fechaFinSancion);
    }

    /**
     * Elimina la sanción de un usuario.
     * @param id El ID del usuario.
     * @throws IllegalArgumentException si el ID es inválido o el usuario no existe.
     * @throws IllegalStateException si el usuario no tiene una sanción activa.
     */
    public void eliminarSancionUsuario(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo para eliminar la sanción.");
        }
        usuarioService.eliminarSancionUsuario(id);
    }

    /**
     * Elimina un usuario del sistema.
     * @param id El ID del usuario a eliminar.
     * @return valor booleano true/false resultado de la operación.
     * @throws IllegalArgumentException si el ID es inválido o el usuario no existe.
     * @throws IllegalStateException si el usuario no puede ser eliminado (ej. tiene reservas pendientes).
     */
    public boolean eliminarUsuario(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo para eliminarlo.");
        }
        return usuarioService.eliminarUsuario(id);
    }
    
    /**
     * Obtiene una lista de todos los usuarios activos.
     * @return Una lista de objetos Usuario activos.
     */
    public List<Usuario> obtenerTodosUsuariosActivos() {
        return usuarioService.obtenerTodosUsuariosActivos();
    }

    /**
     * Obtiene una lista de todos los usuarios sancionados.
     * @return Una lista de objetos Usuario sancionados.
     */
    public List<Usuario> obtenerTodosUsuariosSancionados() {
        return usuarioService.obtenerTodosUsuariosSancionados();
    }
}