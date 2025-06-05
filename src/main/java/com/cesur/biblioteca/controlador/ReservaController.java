package com.cesur.biblioteca.controlador;

import com.cesur.biblioteca.modelo.Reserva;
import com.cesur.biblioteca.servicio.ReservaService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /**
     * Crea una nueva reserva para un libro y usuario.
     * Esta reserva estará inicialmente en estado "PENDIENTE"á.
     *
     * @param libroId El ID del libro.
     * @param usuarioId El ID del usuario.
     * @param fechaReserva La fecha en que se realiza la reserva (puede ser null para usar la fecha actual).
     * @return El objeto Reserva añadido, con su ID generado.
     * @throws IllegalArgumentException si los IDs son inválidos.
     * @throws IllegalStateException si el libro no está disponible o el usuario no puede reservar.
     */
    public Reserva crearReserva(int libroId, int usuarioId, Date fechaReserva) {
        Date fechaReservaLocal = null;
        if (libroId <= 0) {
            throw new IllegalArgumentException("El ID del libro debe ser un número positivo.");
        }
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo.");
        }

        // Si fechaReserva es null, usaremos la fecha actual. También comprobamos que la fecha no sea posterior a hoy.
        if (fechaReserva != null && fechaReserva.after(Date.valueOf(LocalDate.now()))) {
            throw new IllegalArgumentException("La fecha de reserva no puede ser posterior a hoy.");
        } else if (fechaReserva == null) {
            fechaReservaLocal = Date.valueOf(LocalDate.now());
        } else {
            fechaReservaLocal = fechaReserva;
        }
        
        // Asignamos período de préstamo por defecto en 15 días.
        Date fechaDevolucionPrevista = Date.valueOf(LocalDate.now().plusDays(15));
        
        Reserva nuevaReserva = new Reserva(libroId, usuarioId, fechaReservaLocal, fechaDevolucionPrevista, null, "PENDIENTE");
        reservaService.crearReserva(nuevaReserva);
        return nuevaReserva;
    }

    /**
     * Busca una reserva por su ID.
     * @param id El ID de la reserva.
     * @return El objeto Reserva encontrado, o null si no existe.
     */
    public Reserva buscarReservaPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de la reserva debe ser un número positivo.");
        }
        return reservaService.buscarReservaPorId(id);
    }

    /**
     * Obtiene una lista de reservas para un ID de libro específico.
     * @param libroId El ID del libro.
     * @return Una lista de objetos Reserva.
     */
    public List<Reserva> obtenerReservasPorLibroID(int libroId) {
        if (libroId <= 0) {
            throw new IllegalArgumentException("El ID del libro debe ser un número positivo.");
        }
        return reservaService.obtenerReservasPorLibroID(libroId);
    }
    
    /**
     * Obtiene una lista de reservas para un ISBN de libro específico.
     * @param libroIsbn El ISBN del libro.
     * @return Una lista de objetos Reserva.
     */
    public List<Reserva> obtenerReservasPorLibroIsbn(String libroIsbn) {
        if (libroIsbn == null || libroIsbn.trim().isEmpty()) {
            throw new IllegalArgumentException("El ISBN del libro no puede estar vacío.");
        }
        return reservaService.obtenerReservasPorLibroIsbn(libroIsbn);
    }

    /**
     * Obtiene una lista de reservas para un ID de usuario específico.
     * @param usuarioId El ID del usuario.
     * @return Una lista de objetos Reserva.
     */
    public List<Reserva> obtenerReservasPorUsuarioID(int usuarioId) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo.");
        }
        return reservaService.obtenerReservasPorUsuarioID(usuarioId);
    }
    
    /**
     * Obtiene una lista de reservas para un Email de usuario específico.
     * @param usuarioEmail El Email del usuario.
     * @return Una lista de objetos Reserva.
     */
    public List<Reserva> obtenerReservasPorUsuarioEmail(String usuarioEmail) {
         if (usuarioEmail == null || usuarioEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("El Email del usuario no puede estar vacío.");
        }
        return reservaService.obtenerReservasPorUsuarioEmail(usuarioEmail);
    }
    
    /**
     * Obtiene una lista de todas las reservas en el sistema.
     * @return Una lista de objetos Reserva.
     */
    public List<Reserva> obtenerTodasLasReservas() {
        return reservaService.obtenerTodasLasReservas();
    }

    /**
     * Actualiza una reserva existente.
     * Este método podría ser útil para modificar la fecha de devolución prevista o el estado de una reserva
     * si se requiere una actualización manual sin pasar por 'registrarDevolucion'.
     *
     * @param reservaId El ID de la reserva a actualizar.
     * @param libroId El nuevo ID del libro (0 para no cambiar).
     * @param usuarioId El nuevo ID del usuario (0 para no cambiar).
     * @param fechaReserva La nueva fecha de reserva (null para no cambiar).
     * @param fechaDevolucionPrevista La nueva fecha de devolución prevista (null para no cambiar).
     * @param fechaDevolucionReal La nueva fecha de devolución real (null para no cambiar).
     * @param estado El nuevo estado (null o vacío para no cambiar).
     * @throws IllegalArgumentException si los datos de entrada son inválidos o la reserva no existe.
     * @throws IllegalStateException si la actualización viola alguna regla de negocio.
     */
    public void actualizarReserva(int reservaId, int libroId, int usuarioId, Date fechaReserva, 
                                    Date fechaDevolucionPrevista, Date fechaDevolucionReal, String estado) {
        String cambioEstado = null;
        if (reservaId <= 0) {
            throw new IllegalArgumentException("El ID de la reserva debe ser un número positivo para actualizarla.");
        }
        
        // Obtener la reserva existente para actualizar sus campos
        Reserva reservaExistente = reservaService.buscarReservaPorId(reservaId);
        if (reservaExistente == null) {
            throw new IllegalArgumentException("Reserva con ID " + reservaId + " no encontrada para actualizar.");
        }

        // Actualizar solo los campos que se proporcionan y son válidos
        if (libroId > 0) {
            reservaExistente.setLibroId(libroId);
        }
        if (usuarioId > 0) {
            reservaExistente.setUsuarioId(usuarioId);
        }
        // Comprobamos que la nueva fecha de reserva no sea posterior a la fecha de hoy.
        if (fechaReserva != null && !fechaReserva.after(Date.valueOf(LocalDate.now()))) {
            reservaExistente.setFechaReserva(fechaReserva);
        } else {
            throw new IllegalArgumentException("La fecha de reserva no debe ser posterior a hoy.");
        }
        // Comprobamos que la nueva fecha de devolución prevista sea posterior a la nueva fecha de reserva.
        if (fechaDevolucionPrevista != null && fechaDevolucionPrevista.after(fechaReserva)) {
            reservaExistente.setFechaDevolucionPrevista(fechaDevolucionPrevista);
        } else {
            throw new IllegalArgumentException("La fecha de devolución prevista debe ser posterior a la fecha de reserva.");
        }
        // Comprobamos que la nueva fecha de devolución real, si existe, sea posterior a la nueva fecha de reserva.
        if (fechaDevolucionReal != null && fechaDevolucionReal.after(fechaReserva)) {
            reservaExistente.setFechaDevolucionReal(fechaDevolucionReal);
        } else if (fechaDevolucionReal != null) {
            throw new IllegalArgumentException("La fecha de devolución real debe ser posterior a la fecha de reserva.");
        }
        if (estado != null && !estado.trim().isEmpty()) {
            reservaExistente.setEstado(estado);
        }
        if ("PENDIENTE".equals(estado) && !"PENDIENTE".equals(reservaExistente.getEstado())) {
            // Si vamos a cambiar el estado de la reserva a algún valor pendiente, tenemos que decrementar el ejemplar de libro asociado
            cambioEstado = "DEC";
        } else if (!"PENDIENTE".equals(estado) && "PENDIENTE".equals(reservaExistente.getEstado())) {
            // Si vamos a cambiar el estado de la reserva a valor no pendiente, tenemos que incrementar el ejemplar de libro asociado.
            cambioEstado = "INC";
        }

        reservaService.actualizarReserva(reservaExistente, cambioEstado);
    }

    /**
     * Cancela una reserva.
     * @param id El ID de la reserva a cancelar.
     * @throws IllegalArgumentException si el ID es inválido o la reserva no existe.
     * @throws IllegalStateException si la reserva no puede ser cancelada en su estado actual.
     */
    public void cancelarReserva(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de la reserva debe ser un número positivo para cancelarla.");
        }
        reservaService.cancelarReserva(id);
    }

    /**
     * Finaliza una reserva por motivos no estándar (ej. libro perdido).
     * @param id El ID de la reserva a finalizar.
     * @throws IllegalArgumentException si el ID es inválido o la reserva no existe.
     * @throws IllegalStateException si la reserva no puede ser finalizada en su estado actual.
     */
    public void finalizarReserva(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de la reserva debe ser un número positivo para finalizarla.");
        }
        reservaService.finalizarReserva(id);
    }
    
    /**
     * Elimina una reserva.
     * @param id El ID de la reserva a eliminar.
     * @return valor booleano true/false resultado de la operación.
     * @throws IllegalArgumentException si el ID es inválido o la reserva no existe.
     * @throws IllegalStateException si la reserva no puede ser eliminada en su estado actual.
     */
    public boolean eliminarReserva(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de la reserva debe ser un número positivo para eliminarla.");
        }
        return reservaService.eliminarReserva(id);
    }
    
    /**
     * Registra un nuevo préstamo para un libro y usuario, validando condiciones de préstamo.
     * Internamente, este método puede llamar a 'crearReserva' y luego ajustar el estado.
     *
     * @param libroId El ID del libro.
     * @param usuarioId El ID del usuario.
     * @param fechaDevolucionPrevista La fecha prevista de devolución.
     * @throws IllegalArgumentException si los IDs son inválidos o las fechas no son válidas.
     * @throws IllegalStateException si el préstamo viola las reglas de negocio (ej. libro no disponible, usuario sancionado/límite excedido).
     */
    public void registrarPrestamo(int libroId, int usuarioId, Date fechaDevolucionPrevista) {
        if (libroId <= 0) {
            throw new IllegalArgumentException("El ID del libro debe ser un número positivo.");
        }
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un número positivo.");
        }
        if (fechaDevolucionPrevista == null || !fechaDevolucionPrevista.after(Date.valueOf(LocalDate.now()))) {
            throw new IllegalArgumentException("La fecha de devolución prevista debe ser posterior a hoy.");
        }
        reservaService.registrarPrestamo(libroId, usuarioId, fechaDevolucionPrevista);
    }
    
    /**
     * Registra la devolución de un préstamo.
     * @param reservaId El ID de la reserva/préstamo a devolver.
     * @param fechaDevolucionReal La fecha real de devolución (puede ser null para usar la fecha actual).
     * @throws IllegalArgumentException si el ID de la reserva es inválido.
     * @throws IllegalStateException si la reserva no se puede devolver en su estado actual.
     */
    public void registrarDevolucion(int reservaId, Date fechaDevolucionReal) {
        if (reservaId <= 0) {
            throw new IllegalArgumentException("El ID de la reserva debe ser un número positivo.");
        }
        // La validación de fecha real se hace en el servicio (si es null, usa LocalDate.now())
        reservaService.registrarDevolucion(reservaId, fechaDevolucionReal);
    }
}
