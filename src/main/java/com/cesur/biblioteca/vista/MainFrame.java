package com.cesur.biblioteca.vista;

import com.cesur.biblioteca.controlador.LibroController;
import com.cesur.biblioteca.controlador.ReservaController;
import com.cesur.biblioteca.controlador.UsuarioController;
import com.cesur.biblioteca.vista.libro.LibroManagementPanel;
import com.cesur.biblioteca.vista.usuario.UsuarioManagementPanel;
import com.cesur.biblioteca.vista.reserva.ReservaManagementPanel;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent; // Para atajos de teclado
import java.net.URL; // Necesario para cargar recursos desde el JAR

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class MainFrame extends JFrame {

    private final LibroController libroController;
    private final UsuarioController usuarioController;
    private final ReservaController reservaController;

    private JPanel cardPanel; // Panel que contendrá los diferentes paneles de gestión
    private CardLayout cardLayout; // Para cambiar entre paneles

    public MainFrame(LibroController libroController, UsuarioController usuarioController, ReservaController reservaController) {
        this.libroController = libroController;
        this.usuarioController = usuarioController;
        this.reservaController = reservaController;

        setTitle("Sistema de Gestión de Biblioteca - CESUR");
        // setSize(970, 600); // Usaremos pack() para darle un tamaño por defecto
        setExtendedState(JFrame.MAXIMIZED_BOTH); //Inicia la ventana maximizada
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana en la pantalla

        initComponents(); // Inicializa los componentes de la interfaz
        // Aquí se debe llamar a pack(), después de que todos los componentes se hayan añadido
        pack(); // Ajusta el tamaño de la ventana para que todos los componentes sean visibles
        setMinimumSize(new Dimension(1180, 730)); // Opcional: Define un tamaño mínimo para la ventana
    }

    private void initComponents() {
        // Configuración del JMenuBar (Barra de Menú superior)
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu gestionMenu = new JMenu("Gestión");
        gestionMenu.setMnemonic(KeyEvent.VK_G); // Atajo de teclado Alt+G
        menuBar.add(gestionMenu);

        JMenuItem libroMenuItem = new JMenuItem("Gestión de Libros");
        libroMenuItem.addActionListener(e -> showPanel("Libros"));
        gestionMenu.add(libroMenuItem);

        JMenuItem usuarioMenuItem = new JMenuItem("Gestión de Usuarios");
        usuarioMenuItem.addActionListener(e -> showPanel("Usuarios"));
        gestionMenu.add(usuarioMenuItem);

        JMenuItem reservaMenuItem = new JMenuItem("Gestión de Préstamos/Reservas");
        reservaMenuItem.addActionListener(e -> showPanel("Reservas"));
        gestionMenu.add(reservaMenuItem);

        // --- INICIO: Nuevo menú "Acerca de" ---
        JMenu aboutMenu = new JMenu("Acerca de");
        aboutMenu.setMnemonic(KeyEvent.VK_A); // Atajo de teclado Alt+A
        menuBar.add(aboutMenu);

        JMenuItem aboutMenuItem = new JMenuItem("Información");
        aboutMenuItem.addActionListener(e -> showAboutDialog());
        aboutMenu.add(aboutMenuItem);
        // --- FIN: Nuevo menú "Acerca de" ---

        // Configuración del cardPanel para cambiar entre vistas
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        add(cardPanel, BorderLayout.CENTER);

        // Añadir los paneles de gestión al cardPanel
        LibroManagementPanel libroPanel = new LibroManagementPanel(libroController);
        cardPanel.add(libroPanel, "Libros");

        UsuarioManagementPanel usuarioPanel = new UsuarioManagementPanel(usuarioController);
        cardPanel.add(usuarioPanel, "Usuarios");

        ReservaManagementPanel reservaPanel = new ReservaManagementPanel(reservaController);
        cardPanel.add(reservaPanel, "Reservas");

        // Mensaje de bienvenida inicial
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        JLabel welcomeLabel = new JLabel("<html><h1 style='color: #336699;'>¡Bienvenido al Sistema de Gestión de Biblioteca!</h1>" +
                                             "<p style='font-size: 14px; text-align: center;'>Seleccione una opción del menú 'Gestión' para empezar.</p></html>");
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(welcomeLabel);
        cardPanel.add(welcomePanel, "Inicio");

        // Mostrar el panel de inicio por defecto
        cardLayout.show(cardPanel, "Inicio");
    }

    /**
     * Muestra el panel correspondiente según el nombre.
     * @param panelName El nombre del panel a mostrar (ej. "Libros", "Usuarios", "Reservas").
     */
    private void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }

    /**
     * Muestra un diálogo con información "Acerca de" el programa, incluyendo un logo.
     */
    private void showAboutDialog() {
        String message = "<html><center>"
                        + "<h2>CESUR - CFGS Desarrollo de Aplicaciones Multiplataforma</h2>"
                        + "<h2>Proyecto de Fin de Ciclo</h2>"
                        + "<h3>Gabriel Fernández Magán - Curso Académico 2024/25</h3>"
                        + "</center></html>";

        // Cargar el icono del logo
        ImageIcon originalIcon = null;
        // La ruta es relativa al classpath. Si 'cesur_logo.png' está en 'src/main/resources/',
        // entonces la ruta en el classpath es 'cesur_logo.png'.
        URL imageUrl = getClass().getClassLoader().getResource("cesur_logo.png");
        if (imageUrl != null) {
            originalIcon = new ImageIcon(imageUrl);
        } else {
            System.err.println("Advertencia: No se pudo cargar la imagen 'logo.png'. Asegúrate de que esté en la carpeta 'resources'.");
        }

        ImageIcon scaledIcon = null;
        if (originalIcon != null) {
            // Define el tamaño deseado para el logo
            int desiredWidth = 128;  // Ancho en píxeles
            int desiredHeight = 128; // Alto en píxeles

            // Obtener la imagen original del ImageIcon
            Image originalImage = originalIcon.getImage();

            // Reescalar la imagen
            // Image.SCALE_SMOOTH es un algoritmo de reescalado de alta calidad
            Image scaledImage = originalImage.getScaledInstance(desiredWidth, desiredHeight, Image.SCALE_SMOOTH);

            // Crear un nuevo ImageIcon con la imagen reescalada
            scaledIcon = new ImageIcon(scaledImage);
        }

        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, scaledIcon, new Object[]{"Aceptar"}, null);
        JDialog dialog = optionPane.createDialog(this, "Acerca del Sistema de Biblioteca");
        dialog.setVisible(true);
    }
}