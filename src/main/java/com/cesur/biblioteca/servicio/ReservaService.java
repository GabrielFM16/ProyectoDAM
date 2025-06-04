package com.cesur.biblioteca.servicio;

import com.cesur.biblioteca.dao.ReservaDAO;
import com.cesur.biblioteca.modelo.Libro;
import com.cesur.biblioteca.modelo.Reserva;
import com.cesur.biblioteca.modelo.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit; // Para calcular días de sanción

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class ReservaService implements ReservaServiceInterface {
    private final ReservaDAO reservaDAO;
    private final LibroService libroService; // Dependencia de LibroService
    private final UsuarioService usuarioService; // Dependencia de UsuarioService

    // Constructor paramétrico para inyectar las dependencias
    public ReservaService(ReservaDAO reservaDAO, LibroService libroService, UsuarioService usuarioService) {
        this.reservaDAO = reservaDAO;
        this.libroService = libroService;
        this.usuarioService = usuarioService;
    }

    @Override
    public void crearReserva(Reserva reserva) {
        // 1. Validar que el libro exista y esté disponible
        Libro libro = libroService.buscarLibroPorId(reserva.getLibroId());
        if (libro == null) {
            throw new IllegalArgumentException("Error al crear reserva: El libro con ID " + reserva.getLibroId() + " no existe.");
        }
        if (!libro.isDisponible()) {
            throw new IllegalStateException("Error al crear reserva: El libro '" + libro.getTitulo() + "' (ISBN: " + libro.getIsbn() + ") no está disponible para ser reservado.");
        }
        if (libro.getNumEjemplares() <= 0) {
            throw new IllegalStateException("Error al crear reserva: El libro '" + libro.getTitulo() + "' (ISBN: " + libro.getIsbn() + ") no tiene unidades disponibles.");
        }

        // 2. Validar que el usuario exista y no esté sancionado o inactivo
        Usuario usuario = usuarioService.buscarUsuarioPorId(reserva.getUsuarioId());
        if (usuario == null) {
            throw new IllegalArgumentException("Error al crear reserva: El usuario con ID " + reserva.getUsuarioId() + " no existe.");
        }
        if (!usuario.isActivo()) {
            throw new IllegalStateException("Error al crear reserva: El usuario '" + usuario.getNombre() + "' no está activo en el sistema.");
        }
        if (usuario.isSancionado() && usuario.getFechaFinSancion() != null && usuario.getFechaFinSancion().after(Date.valueOf(LocalDate.now()))) {
            throw new IllegalStateException("Error al crear reserva: El usuario '" + usuario.getNombre() + "' está sancionado hasta " + usuario.getFechaFinSancion() + ".");
        } else if (usuario.isSancionado() && (usuario.getFechaFinSancion() == null || !usuario.getFechaFinSancion().after(Date.valueOf(LocalDate.now())))) {
            System.out.println("Servicio: Sanción expirada para usuario ID " + usuario.getId() + ". Desactivando sanción automáticamente.");
            usuarioService.eliminarSancionUsuario(usuario.getId());
        }

        // 3. Validar que el usuario no tenga ya una reserva PENDIENTE para el mismo libro
        List<Reserva> reservasActivasUsuario = reservaDAO.getReservasByUsuarioId(reserva.getUsuarioId());
        for (Reserva r : reservasActivasUsuario) {
            // Si la reserva está PENDIENTE y es para el mismo libro
            if ("PENDIENTE".equals(r.getEstado()) && r.getLibroId() == reserva.getLibroId()) {
                throw new IllegalStateException("El usuario '" + usuario.getNombre() + "' ya tiene una reserva activa para el libro '" + libro.getTitulo() + "'. No se puede reservar otro ejemplar.");
            }
        }

        // 4. Establecer la fecha de reserva si no está ya puesta
        if (reserva.getFechaReserva() == null) {
            reserva.setFechaReserva(Date.valueOf(LocalDate.now()));
        }
        // 5. Establecer la fecha de devolución prevista si no está ya puesta
        if (reserva.getFechaDevolucionPrevista() == null) {
            reserva.setFechaDevolucionPrevista(Date.valueOf(LocalDate.now().plusDays(15)));
        }
        // 6. Establecer estado inicial
        if (reserva.getEstado() == null || reserva.getEstado().trim().isEmpty()) {
            reserva.setEstado("PENDIENTE"); // Usamos "PENDIENTE" como estado inicial
        }

        // 7. Guardar la reserva usando el DAO
        reservaDAO.addReserva(reserva);

        // 8. Marcar el ejemplar/libro como no disponible (Lógica de negocio cruzada de entidades)
        libro.setNumEjemplares(libro.getNumEjemplares() - 1);
        // libroService.marcarLibroNoDisponible(libro.getId());
        if (libro.getNumEjemplares() == 0) {
            libro.setDisponible(false);
        }
            libroService.actualizarLibro(libro);
        System.out.println("Servicio: Reserva creada exitosamente para Libro ID: " + reserva.getLibroId() + " y Usuario ID: " + reserva.getUsuarioId());
    }

    @Override
    public Reserva buscarReservaPorId(int id) {
        return reservaDAO.getReservaById(id);
    }

    @Override
    public List<Reserva> obtenerReservasPorLibroID(int libroId) {
        return reservaDAO.getReservasByLibroId(libroId);
    }

    @Override
    public List<Reserva> obtenerReservasPorLibroIsbn(String libroIsbn) {
        Libro libro = libroService.buscarLibroPorIsbn(libroIsbn);
        if (libro != null) {
            return reservaDAO.getReservasByLibroId(libro.getId());
        }
        return List.of(); // Devuelve una lista vacía si el libro no existe
    }

    @Override
    public List<Reserva> obtenerReservasPorUsuarioID(int usuarioId) {
        return reservaDAO.getReservasByUsuarioId(usuarioId);
    }

    @Override
    public List<Reserva> obtenerReservasPorUsuarioEmail(String usuarioEmail) {
        Usuario usuario = usuarioService.buscarUsuarioPorEmail(usuarioEmail);
        if (usuario != null) {
            return reservaDAO.getReservasByUsuarioId(usuario.getId());
        }
        return List.of(); // Devuelve una lista vacía si el usuario no existe
    }

    @Override
    public List<Reserva> obtenerTodasLasReservas() {
        return reservaDAO.getAllReservas();
    }

    @Override
    public void actualizarReserva(Reserva reserva, String cambioEstado) {
        // Obtener la reserva existente para comparar y evitar auto-comparación
        Reserva existingReserva = reservaDAO.getReservaById(reserva.getId());
        if (reserva.getId() == 0 || existingReserva == null) {
            throw new IllegalArgumentException("La reserva a actualizar no existe o su ID es inválido.");
        }
        
        // Nos aseguramos de que los IDs de libro y usuario siguen siendo válidos si se actualizan
        Libro libro = libroService.buscarLibroPorId(reserva.getLibroId());
        if (libro == null) {
            throw new IllegalArgumentException("El libro asociado a la reserva no existe.");
        }
        Usuario usuario = usuarioService.buscarUsuarioPorId(reserva.getUsuarioId());
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario asociado a la reserva no existe.");
        }

        // Validar que el usuario no tenga ya una reserva PENDIENTE para el mismo libro (excluyendo la reserva actual)
        List<Reserva> reservasActivasUsuario = reservaDAO.getReservasByUsuarioId(reserva.getUsuarioId());
        for (Reserva r : reservasActivasUsuario) {
            // Si la reserva está PENDIENTE, es para el mismo libro Y NO ES LA RESERVA QUE ESTAMOS ACTUALIZANDO
            if ("PENDIENTE".equals(r.getEstado()) && r.getLibroId() == reserva.getLibroId() && r.getId() != reserva.getId()) {
                throw new IllegalStateException("El usuario '" + usuario.getNombre() + "' ya tiene otra reserva activa para el libro '" + libro.getTitulo() + "'. No se puede actualizar a este libro.");
            }
        }
        
        // Si vamos a cambiar de estado la reserva, debemos ajustar la cantidad de ejemplares disponibles del libro asociado.
        if (cambioEstado != null) {
            if ("INC".equals(cambioEstado)) { // Paso a no pendiente, incrementar ejemplares.
                libro.setNumEjemplares(libro.getNumEjemplares() + 1);
                libro.setDisponible(true);
            } else if ("DEC".equals(cambioEstado)) { // Paso a pendiente, comprobar unidades y decrementar ejemplares.
                if (libro.getNumEjemplares() <= 0) {
                    throw new IllegalStateException("Error al actualizar la reserva: El libro '" + libro.getTitulo() + "' (ISBN: " + libro.getIsbn() + ") no tiene unidades disponibles.");
                }
                libro.setNumEjemplares(libro.getNumEjemplares() - 1);
                if (libro.getNumEjemplares() == 0) {
                    libro.setDisponible(false);
                }
            }
            libroService.actualizarLibro(libro);
        } 

        reservaDAO.updateReserva(reserva);
        System.out.println("Servicio: Reserva con ID " + reserva.getId() + " actualizada exitosamente.");
    }

    @Override
    public void cancelarReserva(int id) {
        Reserva reserva = reservaDAO.getReservaById(id);
        if (reserva != null) {
            // Solo se puede cancelar si la reserva se encuentra en estado "PENDIENTE".
            if ("PENDIENTE".equals(reserva.getEstado())) {
                reserva.setEstado("CANCELADO");
                reservaDAO.updateReserva(reserva);
                // Obtenemos el libro del préstamo.
                Libro libro = libroService.buscarLibroPorId(reserva.getLibroId());
                libro.setNumEjemplares(libro.getNumEjemplares() + 1);
                libroService.marcarLibroDisponible(reserva.getLibroId()); // El libro vuelve a estar disponible
                System.out.println("Servicio: Reserva con ID " + id + " cancelada y libro marcado como disponible.");
            } else {
                System.err.println("Servicio: La reserva con ID " + id + " no puede ser cancelada en su estado actual: " + reserva.getEstado());
            }
        } else {
            System.err.println("Servicio: Reserva con ID " + id + " no encontrada para cancelar.");
        }
    }

    @Override
    public void finalizarReserva(int id) { // Se finaliza la reserva sin tener en cuenta penalizaciones por retraso.
        Reserva reserva = reservaDAO.getReservaById(id);
        if (reserva != null) {
            // Solo se puede finalizar si la reserva se encuentra en estado "PENDIENTE".
            if ("PENDIENTE".equals(reserva.getEstado())) {
                reserva.setFechaDevolucionReal(Date.valueOf(LocalDate.now())); // Establecer fecha de finalización
                reserva.setEstado("DEVUELTO");
                reservaDAO.updateReserva(reserva);
                // Obtenemos el libro del préstamo.
                Libro libro = libroService.buscarLibroPorId(reserva.getLibroId());
                libro.setNumEjemplares(libro.getNumEjemplares() + 1);
                libroService.marcarLibroDisponible(reserva.getLibroId()); // Marcar libro como disponible de nuevo
                System.out.println("Servicio: Reserva con ID " + id + " finalizada y libro marcado como disponible.");
            } else {
                System.err.println("Servicio: La reserva con ID " + id + " no puede ser finalizada en su estado actual: " + reserva.getEstado());
            }
        } else {
            System.err.println("Servicio: Reserva con ID " + id + " no encontrada para finalizar.");
        }
    }
    
    @Override
    public boolean eliminarReserva(int id) {
        boolean resultado = false;
        Reserva reserva = reservaDAO.getReservaById(id);
        if (reserva != null) {
            // Solo se puede eliminar si la reserva no se encuentra en estado "PENDIENTE".
            if (!"PENDIENTE".equals(reserva.getEstado())) {
                resultado = reservaDAO.deleteReserva(id);
                System.out.println("Servicio: Reserva con ID " + id + " eliminada.");
            } else {
                System.err.println("Servicio: La reserva con ID " + id + " no puede ser eliminada en su estado actual: " + reserva.getEstado());
            }
        } else {
            System.err.println("Servicio: Reserva con ID " + id + " no encontrada para eliminar.");
        }
        return resultado;
    }

    @Override
    public void registrarPrestamo(int libroId, int usuarioId, Date fechaDevolucionPrevista) {
        // Validación crucial: ¿Puede el usuario tomar un préstamo?
        Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario con ID " + usuarioId + " no encontrado para registrar préstamo.");
        }
        if (!usuario.isActivo()) {
            throw new IllegalStateException("El usuario " + usuario.getNombre() + " no está activo para registrar préstamos.");
        }
        if (usuario.isSancionado() && usuario.getFechaFinSancion() != null && usuario.getFechaFinSancion().after(Date.valueOf(LocalDate.now()))) {
            throw new IllegalStateException("El usuario " + usuario.getNombre() + " está sancionado y no puede registrar préstamos hasta " + usuario.getFechaFinSancion() + ".");
        } else if (usuario.isSancionado() && (usuario.getFechaFinSancion() == null || !usuario.getFechaFinSancion().after(Date.valueOf(LocalDate.now())))) {
            System.out.println("Servicio: Sanción expirada para usuario ID " + usuario.getId() + ". Desactivando sanción automáticamente.");
            usuarioService.eliminarSancionUsuario(usuario.getId());
        }

        // Lógica de negocio: Verificar el límite de reservas por usuario
        List<Reserva> reservasUsuario = reservaDAO.getReservasByUsuarioId(usuarioId); // Obtenemos todas las reservas del usuario
        int countActiveReservas = 0;
        for (Reserva r : reservasUsuario) {
            // Contamos solo las reservas que están "PENDIENTE".
            if ("PENDIENTE".equals(r.getEstado())) {
                countActiveReservas++;
            }
        }

        final int MAX_RESERVAS_PERMITIDAS = 5;
        if (countActiveReservas >= MAX_RESERVAS_PERMITIDAS) {
            throw new IllegalStateException("El usuario " + usuario.getNombre() +
                                            " " + usuario.getApellidos() + " ya tiene " + countActiveReservas +
                                            " reservas activas (pendientes) y ha alcanzado el límite de " +
                                            MAX_RESERVAS_PERMITIDAS + " préstamos.");
        }
        
        // Obtenemos el libro a prestar.
        Libro libro = libroService.buscarLibroPorId(libroId);
        if (libro == null) {
            throw new IllegalArgumentException("Error al registrar préstamo: El libro con ID " + libroId + " no existe.");
        }
        if (!libro.isDisponible()) {
            throw new IllegalStateException("Error al registrar préstamo: El libro '" + libro.getTitulo() + "' (ISBN: " + libro.getIsbn() + ") no está disponible.");
        }
        if (libro.getNumEjemplares() <= 0) {
            throw new IllegalStateException("Error al registrar préstamo: El libro '" + libro.getTitulo() + "' (ISBN: " + libro.getIsbn() + ") no tiene unidades disponibles.");
        }

        // Validar que el usuario no tenga ya una reserva PENDIENTE para el mismo libro
        for (Reserva r : reservasUsuario) { // Reutilizamos la lista de reservas del usuario
            // Si la reserva está PENDIENTE y es para el mismo libro
            if ("PENDIENTE".equals(r.getEstado()) && r.getLibroId() == libroId) {
                throw new IllegalStateException("El usuario '" + usuario.getNombre() + "' ya tiene una reserva activa para el libro '" + libro.getTitulo() + "'. No se puede registrar otro préstamo de este libro.");
            }
        }

        // Si el usuario pasa todas las validaciones, procedemos con el préstamo
        // El estado inicial de un préstamo es "PENDIENTE"
        Reserva nuevaReserva = new Reserva(libroId, usuarioId, Date.valueOf(LocalDate.now()), fechaDevolucionPrevista, null, "PENDIENTE");
        this.crearReserva(nuevaReserva); // Reutiliza la lógica de validación del libro y lo marca como no disponible
        System.out.println("Servicio: Préstamo registrado para Libro ID: " + libroId + ", Usuario ID: " + usuarioId + ".");
    }
    
    @Override
    public void registrarDevolucion(int reservaId, Date fechaDevolucionReal) {
        Reserva reserva = reservaDAO.getReservaById(reservaId);
        if (reserva == null) {
            System.err.println("Servicio: Reserva con ID " + reservaId + " no encontrada para registrar devolución.");
            return;
        }

        // Solo se puede devolver si la reserva se encuentra en estado "PENDIENTE".
        if (!("PENDIENTE".equals(reserva.getEstado()))) {
            System.err.println("Servicio: La reserva con ID " + reservaId + " no está en un estado válido para ser devuelta. Estado actual: " + reserva.getEstado());
            return;
        }

        // Si no se ha indicado fecha de devolución real o bien no es un valor válido, se usará la fecha de hoy.
        reserva.setFechaDevolucionReal(fechaDevolucionReal != null && fechaDevolucionReal.after(reserva.getFechaReserva()) ? fechaDevolucionReal : Date.valueOf(LocalDate.now()));
        // Lógica de negocio: Calcular sanción por retraso
        if (reserva.getFechaDevolucionPrevista() != null && reserva.getFechaDevolucionReal() != null && reserva.getFechaDevolucionReal().after(reserva.getFechaDevolucionPrevista())) {
            long diffInMillies = Math.abs(reserva.getFechaDevolucionReal().getTime() - reserva.getFechaDevolucionPrevista().getTime());
            long diffDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            System.out.println("¡Advertencia! La devolución de la reserva ID " + reservaId + " se retrasó por " + diffDays + " días. Se aplicará sanción correspondiente.");
            // Se sanciona al usuario con días correspondientes al retraso en la devolución.
            usuarioService.sancionarUsuario(reserva.getUsuarioId(), Date.valueOf(LocalDate.now().plusDays(diffDays)));
            reserva.setEstado("RETRASO"); // Cambiamos el estado a "RETRASO"
        } else {
            reserva.setEstado("DEVUELTO"); // Cambiamos el estado a "DEVUELTO"
        }
        
        // Actualizamos el estado de la reserva.
        reservaDAO.updateReserva(reserva);
        
        // Obtenemos el libro del préstamo.
        Libro libro = libroService.buscarLibroPorId(reserva.getLibroId());
        libro.setNumEjemplares(libro.getNumEjemplares() + 1);
        // El ejemplar/libro vuelve a estar disponible
        libro.setDisponible(true);
        libroService.actualizarLibro(libro);
        // libroService.marcarLibroDisponible(reserva.getLibroId());
        System.out.println("Servicio: Devolución registrada para la reserva ID " + reservaId + ".");
    }
}
