package com.cesur.biblioteca.servicio;

import com.cesur.biblioteca.dao.UsuarioDAO;
import com.cesur.biblioteca.dao.ReservaDAO;
import com.cesur.biblioteca.modelo.Usuario;
import com.cesur.biblioteca.modelo.Reserva;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit; // Para calcular días de sanción

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class UsuarioService implements UsuarioServiceInterface {
    private final UsuarioDAO usuarioDAO;
    private final ReservaDAO reservaDAO;
    
    // Constructor paramétrico para inyectar las dependencias
    public UsuarioService(UsuarioDAO usuarioDAO, ReservaDAO reservaDAO) {
        this.usuarioDAO = usuarioDAO;
        this.reservaDAO = reservaDAO;
    }

    @Override
    public void añadirUsuario(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() ||
            usuario.getApellidos() == null || usuario.getApellidos().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre y apellidos del usuario no pueden estar vacíos.");
        }
        if (usuario.getEmail() == null || !usuario.getEmail().contains("@") || usuarioDAO.getUsuarioByEmail(usuario.getEmail()) != null) {
            throw new IllegalArgumentException("Email inválido o ya existente.");
        }
        if (usuario.isSancionado() && usuario.getFechaFinSancion() == null) {
            throw new IllegalArgumentException("Un usuario no puede ser sancionado sin fecha límite.");
        }
        // Establecer estado por defecto para un nuevo usuario si no viene preestablecido
        if (usuario.getId() == 0) { // Solo para nuevos usuarios
            usuario.setActivo(true);
            usuario.setSancionado(false);
            usuario.setFechaFinSancion(null);
        }

        usuarioDAO.addUsuario(usuario);
        System.out.println("Servicio: Usuario '" + usuario.getNombre() + " " + usuario.getApellidos() + "' añadido exitosamente.");
    }

    @Override
    public Usuario buscarUsuarioPorId(int id) {
        return usuarioDAO.getUsuarioById(id);
    }

    @Override
    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarioDAO.getUsuarioByEmail(email);
    }

    @Override
    public List<Usuario> obtenerTodosUsuarios() {
        return usuarioDAO.getAllUsuarios();
    }

    @Override
    public void actualizarUsuario(Usuario usuario) {
        if (usuario.getId() == 0 || usuarioDAO.getUsuarioById(usuario.getId()) == null) {
            throw new IllegalArgumentException("El usuario a actualizar no existe o su ID es inválido.");
        }
        // Validar si el email ya está en uso por otro usuario
        Usuario existingUserByEmail = usuarioDAO.getUsuarioByEmail(usuario.getEmail());
        if (existingUserByEmail != null && existingUserByEmail.getId() != usuario.getId()) {
            throw new IllegalArgumentException("El email '" + usuario.getEmail() + "' ya está asignado a otro usuario.");
        } else if (existingUserByEmail == null) {
            throw new IllegalArgumentException("No se encuentra el usuario con email '" + usuario.getEmail() + "'.");
        } else {
            usuarioDAO.updateUsuario(usuario);
            System.out.println("Servicio: Usuario con ID " + usuario.getId() + " actualizado exitosamente.");
        }
    }

    @Override
    public boolean eliminarUsuario(int id) {
        boolean resultado = false;
        Usuario usuario = usuarioDAO.getUsuarioById(id);
        if (usuario != null) {
            // Lógica de negocio: No se debería eliminar un usuario si tiene reservas activas.
            List<Reserva> reservasUsuario = reservaDAO.getReservasByUsuarioId(id); // Obtenemos todas las reservas del usuario
            for (Reserva r : reservasUsuario) {
                // Contamos solo las reservas que están "PENDIENTE".
                if ("PENDIENTE".equals(r.getEstado())) {
                    throw new IllegalStateException("No se puede desactivar al usuario con ID " + id + 
                                                    " porque tiene reservas pendientes con ID " + r.getId() + 
                                                    " en estado: " + r.getEstado() + ". Deben ser finalizadas o canceladas primero.");
                }
            }
        
            // Si no hay reservas pendientes, procede a eliminar al usuario
            resultado = usuarioDAO.deleteUsuario(id);
            System.out.println("Servicio: Usuario con ID " + id + " eliminado.");
        } else {
            System.err.println("Servicio: Usuario con ID " + id + " no encontrado para ser eliminado.");
        }
        return resultado;
    }

    @Override
    public void activarUsuario(int id) {
        Usuario usuario = usuarioDAO.getUsuarioById(id);
        if (usuario != null) {
            usuario.setActivo(true);
            usuario.setSancionado(false); // Al activar, eliminamos cualquier sanción activa
            usuario.setFechaFinSancion(null);
            usuarioDAO.updateUsuario(usuario);
            System.out.println("Servicio: Usuario con ID " + id + " activado.");
        } else {
            System.err.println("Servicio: Usuario con ID " + id + " no encontrado para ser activado.");
        }
    }
    
    @Override
    public void desactivarUsuario(int id) {
        Usuario usuario = usuarioDAO.getUsuarioById(id);
        if (usuario != null) {
            // Lógica de negocio: Verificar si el usuario tiene reservas activas.
            List<Reserva> reservasUsuario = reservaDAO.getReservasByUsuarioId(id);
            
            for (Reserva r : reservasUsuario) {
                // "PENDIENTE" es el estado que indica un préstamo o reserva no finalizado.
                // "DEVUELTO", "RETRASO", "CANCELADO" son los estados que indican un préstamo como finalizado.
                if ("PENDIENTE".equals(r.getEstado())) {
                    throw new IllegalStateException("No se puede desactivar al usuario con ID " + id + 
                                                    " porque tiene reservas pendientes con ID " + r.getId() + 
                                                    " en estado: " + r.getEstado() + ". Deben ser finalizadas o canceladas primero.");
                }
            }
            
            // Si no hay reservas pendientes, procede a desactivar al usuario
            usuario.setActivo(false);
            usuario.setSancionado(false); // Si se desactiva, la sanción pierde sentido
            usuario.setFechaFinSancion(null);
            usuarioDAO.updateUsuario(usuario);
            System.out.println("Servicio: Usuario con ID " + id + " desactivado exitosamente.");
        } else {
            System.err.println("Servicio: Usuario con ID " + id + " no encontrado para ser desactivado.");
        }
    }

    @Override
    public void sancionarUsuario(int id, Date fechaFinSancion) {
        Usuario usuario = usuarioDAO.getUsuarioById(id);
        if (usuario != null) {
            if (fechaFinSancion == null || !fechaFinSancion.after(Date.valueOf(LocalDate.now()))) {
                 throw new IllegalArgumentException("La fecha de fin de sanción debe ser mayor que la fecha de hoy.");
            }
            
            usuario.setSancionado(true);
            usuario.setFechaFinSancion(fechaFinSancion);
            // Un usuario sancionado puede seguir estando 'activo' (puede iniciar sesión, ver perfil, etc.)
            // También podrá finalizar sus reservas activas, pero no podrá obtener nuevos préstamos.
            usuario.setActivo(true); 
            usuarioDAO.updateUsuario(usuario);
            
            long diffInMillies = Math.abs(fechaFinSancion.getTime() - Date.valueOf(LocalDate.now()).getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            System.out.println("Servicio: Usuario con ID " + id + " sancionado por " + diff + " días hasta " + fechaFinSancion + ".");
        } else {
            System.err.println("Servicio: Usuario con ID " + id + " no encontrado para sancionar.");
        }
    }

    @Override
    public void eliminarSancionUsuario(int id) {
        Usuario usuario = usuarioDAO.getUsuarioById(id);
        if (usuario != null) {
            if (usuario.isSancionado()) {
                usuario.setSancionado(false);
                usuario.setFechaFinSancion(null);
                // Si la sanción era el único motivo de inactividad, lo reactivamos (si no lo estaba ya)
                if (!usuario.isActivo() && usuario.getFechaFinSancion() == null) { // Si no estaba activo y no hay otra sanción
                    usuario.setActivo(true);
                }
                usuarioDAO.updateUsuario(usuario);
                System.out.println("Servicio: Sanción eliminada para usuario con ID " + id + ".");
            } else {
                System.out.println("Servicio: El usuario con ID " + id + " no estaba sancionado.");
            }
        } else {
            System.err.println("Servicio: Usuario con ID " + id + " no encontrado para eliminar sanción.");
        }
    }

    @Override
    public List<Usuario> obtenerTodosUsuariosActivos() {
        List<Usuario> todosUsuarios = usuarioDAO.getAllUsuarios();
        List<Usuario> usuariosFiltrados = new ArrayList<>();
        // Un usuario activo es aquel que tiene activo = true y no está sancionado (o su sanción ha expirado)
        Date hoy = Date.valueOf(LocalDate.now());
        for (Usuario u : todosUsuarios) {
            if (u.isActivo()) {
                if (u.isSancionado() && u.getFechaFinSancion() != null && u.getFechaFinSancion().after(hoy)) {
                    // Está activo pero sancionado y su sanción no ha expirado
                    // No se añade a la lista de "activos" para propósitos de préstamos, etc.
                } else {
                    // Si está sancionado pero la sanción ha expirado, se desanciona automáticamente
                    if (u.isSancionado() && (u.getFechaFinSancion() == null || !u.getFechaFinSancion().after(hoy))) {
                         System.out.println("Servicio: Sanción expirada para usuario ID " + u.getId() + ". Desactivando sanción automáticamente.");
                         this.eliminarSancionUsuario(u.getId()); // Actualiza el usuario en la BBDD
                         u.setSancionado(false); // Actualiza el objeto en memoria para esta iteración
                         u.setFechaFinSancion(null);
                    }
                    usuariosFiltrados.add(u);
                }
            }
        }
        return usuariosFiltrados;
    }

    @Override
    public List<Usuario> obtenerTodosUsuariosSancionados() {
        List<Usuario> todosUsuarios = usuarioDAO.getAllUsuarios();
        List<Usuario> usuariosFiltrados = new ArrayList<>();
        Date hoy = Date.valueOf(LocalDate.now());
        for (Usuario u : todosUsuarios) {
            if (u.isSancionado()) {
                if (u.getFechaFinSancion() == null || !u.getFechaFinSancion().after(hoy)) {
                    // La sanción ha expirado, desancionar automáticamente
                    System.out.println("Servicio: Sanción expirada para usuario ID " + u.getId() + ". Desactivando sanción automáticamente.");
                    this.eliminarSancionUsuario(u.getId()); // Actualiza el usuario en la BBDD
                    u.setSancionado(false); // Actualiza el objeto en memoria para esta iteración
                    u.setFechaFinSancion(null);
                } else {
                    usuariosFiltrados.add(u);
                }
            }
        }
        return usuariosFiltrados;
    }
}