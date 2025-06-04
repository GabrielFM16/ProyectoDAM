package com.cesur.biblioteca.modelo;

/**
 *
 * @author Gabriel Fernandez Magan
 */

/**
 * Enumeración que define los posibles estados de una reserva de libro.
 */
public enum EstadoReserva {
    /**
     * La reserva ha sido creada y el libro ya ha sido recogido por el usuario.
     * Esta es la fase activa del préstamo.
     */
    PENDIENTE,

    /**
     * El libro ha sido devuelto por el usuario, pero después de la fecha de devolución prevista.
     * Implica que la fechaDevolucionReal > fechaDevolucionPrevista.
     */
    RETRASO,

    /**
     * El libro ha sido devuelto a tiempo por el usuario a la biblioteca.
     * Implica que la fechaDevolucionReal <= fechaDevolucionPrevista.
     */
    DEVUELTO,

    /**
     * La reserva ha sido cancelada por el usuario o por un administrador.
     * El libro no fue recogido o fue cancelado por otras razones.
     */
    CANCELADO
}