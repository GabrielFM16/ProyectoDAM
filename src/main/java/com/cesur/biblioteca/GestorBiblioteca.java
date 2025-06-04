package com.cesur.biblioteca;

import com.cesur.biblioteca.controlador.LibroController;
import com.cesur.biblioteca.controlador.ReservaController;
import com.cesur.biblioteca.controlador.UsuarioController;
import com.cesur.biblioteca.dao.LibroDAO;
import com.cesur.biblioteca.dao.ReservaDAO;
import com.cesur.biblioteca.dao.UsuarioDAO;
import com.cesur.biblioteca.servicio.LibroService;
import com.cesur.biblioteca.servicio.ReservaService;
import com.cesur.biblioteca.servicio.UsuarioService;
import com.cesur.biblioteca.vista.MainFrame;

import javax.swing.SwingUtilities; // Para ejecutar la GUI en el Event Dispatch Thread

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class GestorBiblioteca {

    public static void main(String[] args) {
        // 1. Inicializar DAOs
        LibroDAO libroDAO = new LibroDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ReservaDAO reservaDAO = new ReservaDAO();

        // 2. Inicializar Servicios
        LibroService libroService = new LibroService(libroDAO);
        UsuarioService usuarioService = new UsuarioService(usuarioDAO, reservaDAO); // Pasa reservaDAO directamente
        ReservaService reservaService = new ReservaService(reservaDAO, libroService, usuarioService);

        // 3. Inicializar Controladores
        LibroController libroController = new LibroController(libroService);
        UsuarioController usuarioController = new UsuarioController(usuarioService);
        ReservaController reservaController = new ReservaController(reservaService);

        // 4. Lanzar la Interfaz Gráfica de Usuario en el Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(libroController, usuarioController, reservaController);
            mainFrame.setVisible(true);
        });
    }
}