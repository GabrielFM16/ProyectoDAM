
package com.cesur.biblioteca.modelo;

/**
 *
 * @author Gabriel Fernandez Magan
 */

import java.sql.Date; // Usaremos java.sql.Date para mapear a los tipos DATE de la base de datos

public class Usuario {
    private int id;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private boolean activo;
    private boolean sancionado;
    private Date fechaFinSancion;

    // Constructor por defecto
    public Usuario() {
        this.id = 0;
        this.nombre = "";
        this.apellidos = "";
        this.email = "";
        this.telefono = "";
        this.activo = false;
        this.sancionado = false;
        this.fechaFinSancion = null;
    }

    // Constructor parametrico
    public Usuario(int id, String nombre, String apellidos, String email, String telefono, boolean activo, boolean sancionado, Date fechaFinSancion) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.activo = activo;
        this.sancionado = sancionado;
        this.fechaFinSancion = fechaFinSancion;
    }

    // Constructor sin 'id' (para cuando se crea un nuevo usuario antes de guardarlo en DB)
    public Usuario(String nombre, String apellidos, String email, String telefono, boolean activo, boolean sancionado, Date fechaFinSancion) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
        this.activo = activo;
        this.sancionado = sancionado;
        this.fechaFinSancion = fechaFinSancion;
    }

    // --- Getters y Setters ---
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return this.apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // Nuevos getters y setters para los campos añadidos
    public boolean isActivo() {
        return this.activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isSancionado() {
        return this.sancionado;
    }

    public void setSancionado(boolean sancionado) {
        this.sancionado = sancionado;
    }

    public Date getFechaFinSancion() {
        return this.fechaFinSancion;
    }

    public void setFechaFinSancion(Date fechaFinSancion) {
        this.fechaFinSancion = fechaFinSancion;
    }

    // --- Método toString() ---
    @Override
    public String toString() {
        return "Usuario{" +
               "id=" + this.id +
               ", nombre='" + this.nombre + '\'' +
               ", apellidos='" + this.apellidos + '\'' +
               ", email='" + this.email + '\'' +
               ", telefono='" + this.telefono + '\'' +
               ", activo=" + this.activo +
               ", sancionado=" + this.sancionado +
               ", fechaFinSancion=" + (this.fechaFinSancion != null ? this.fechaFinSancion.toString() : "Sin fecha de sanción") +
               '}';
    }
}