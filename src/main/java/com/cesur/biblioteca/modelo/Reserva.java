
package com.cesur.biblioteca.modelo;

/**
 *
 * @author Gabriel Fernandez Magan
 */

import java.sql.Date; // Usaremos java.sql.Date para mapear a los tipos DATE de la base de datos

public class Reserva {
    private int id;
    private int libroId; // ID del libro relacionado
    private int usuarioId; // ID del usuario relacionado
    private Date fechaReserva;
    private Date fechaDevolucionPrevista;
    private Date fechaDevolucionReal; // Puede ser null
    private String estado; // "PENDIENTE", "RETRASO", "DEVUELTO", "CANCELADO"

    // Constructor por defecto
    public Reserva() {
        this.id = 0;
        this.libroId = 0;
        this.usuarioId = 0;
        this.fechaReserva = null;
        this.fechaDevolucionPrevista = null;
        this.fechaDevolucionReal = null;
        this.estado = "PENDIENTE";
    }

    // Constructor parametrico
    public Reserva(int id, int libroId, int usuarioId, Date fechaReserva, Date fechaDevolucionPrevista, Date fechaDevolucionReal, String estado) {
        this.id = id;
        this.libroId = libroId;
        this.usuarioId = usuarioId;
        this.fechaReserva = fechaReserva;
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.estado = estado;
    }

    // Constructor sin 'id' (para cuando se crea una nueva reserva)
    public Reserva(int libroId, int usuarioId, Date fechaReserva, Date fechaDevolucionPrevista, Date fechaDevolucionReal, String estado) {
        this.libroId = libroId;
        this.usuarioId = usuarioId;
        this.fechaReserva = fechaReserva;
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
        this.fechaDevolucionReal = fechaDevolucionReal;
        this.estado = estado;
    }

    // --- Getters y Setters ---
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLibroId() {
        return this.libroId;
    }

    public void setLibroId(int libroId) {
        this.libroId = libroId;
    }

    public int getUsuarioId() {
        return this.usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Date getFechaReserva() {
        return this.fechaReserva;
    }

    public void setFechaReserva(Date fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public Date getFechaDevolucionPrevista() {
        return this.fechaDevolucionPrevista;
    }

    public void setFechaDevolucionPrevista(Date fechaDevolucionPrevista) {
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
    }

    public Date getFechaDevolucionReal() {
        return this.fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(Date fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // --- Método toString() ---
    @Override
    public String toString() {
        return "Reserva{" +
               "id=" + this.id +
               ", libroId=" + this.libroId +
               ", usuarioId=" + this.usuarioId +
               ", fechaReserva=" + this.fechaReserva +
               ", fechaDevolucionPrevista=" + this.fechaDevolucionPrevista +
               ", fechaDevolucionReal=" + this.fechaDevolucionReal +
               ", estado='" + this.estado + '\'' +
               '}';
    }
}
