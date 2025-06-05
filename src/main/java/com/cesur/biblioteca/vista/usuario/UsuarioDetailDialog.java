package com.cesur.biblioteca.vista.usuario;

import com.cesur.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class UsuarioDetailDialog extends JDialog {

    private final Usuario usuario;
    private JLabel lblNombre, lblApellidos, lblEmail, lblTelefono, lblActivo, lblSancionado, lblFinSancion, lblFoto;

    // Formateador de fechas para mostrar en la GUI
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public UsuarioDetailDialog(Window parent, Usuario usuario) {
        super(parent, "Detalles del Usuario: " + usuario.getNombre() + " " + usuario.getApellidos(), Dialog.ModalityType.APPLICATION_MODAL);
        this.usuario = usuario;
        setSize(550, 450); // Tamaño inicial
        setLocationRelativeTo(parent); // Centrar respecto a la ventana padre

        initComponents();
        loadUsuarioData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel de Datos
        // Ajustado para 8 filas: ID, Nombre, Apellidos, Email, Teléfono, Activo, Sancionado, Fin Sanción
        JPanel dataPanel = new JPanel(new GridLayout(8, 2, 5, 5)); 
        dataPanel.setBorder(BorderFactory.createTitledBorder("Información del Usuario"));

        dataPanel.add(new JLabel("ID:"));
        dataPanel.add(new JLabel(String.valueOf(usuario.getId())));

        dataPanel.add(new JLabel("Nombre:"));
        lblNombre = new JLabel();
        dataPanel.add(lblNombre);

        dataPanel.add(new JLabel("Apellidos:"));
        lblApellidos = new JLabel();
        dataPanel.add(lblApellidos);

        dataPanel.add(new JLabel("Email:"));
        lblEmail = new JLabel();
        dataPanel.add(lblEmail);

        dataPanel.add(new JLabel("Teléfono:"));
        lblTelefono = new JLabel();
        dataPanel.add(lblTelefono);

        dataPanel.add(new JLabel("Activo:"));
        lblActivo = new JLabel();
        dataPanel.add(lblActivo);
        
        dataPanel.add(new JLabel("Sancionado:"));
        lblSancionado = new JLabel();
        dataPanel.add(lblSancionado);

        dataPanel.add(new JLabel("Fin Sanción:"));
        lblFinSancion = new JLabel();
        dataPanel.add(lblFinSancion);

        add(dataPanel, BorderLayout.CENTER);

        // Panel de Imagen (Foto del Usuario)
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createTitledBorder("Foto"));
        lblFoto = new JLabel();
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(lblFoto, BorderLayout.CENTER);
        
        add(imagePanel, BorderLayout.EAST);

        // Botón de cierre
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(btnCerrar);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadUsuarioData() {
        lblNombre.setText(usuario.getNombre());
        lblApellidos.setText(usuario.getApellidos());
        lblEmail.setText(usuario.getEmail());
        lblTelefono.setText(usuario.getTelefono());
        lblActivo.setText(usuario.isActivo() ? "Sí" : "No");
        lblSancionado.setText(usuario.isSancionado() ? "Sí" : "No");
        
        String fechaFinSancionStr = "N/A";
        if (usuario.getFechaFinSancion() != null) {
            fechaFinSancionStr = usuario.getFechaFinSancion().toLocalDate().format(DATE_FORMATTER);
        }
        lblFinSancion.setText(fechaFinSancionStr);

        // Cargar imagen del usuario
        String imagePath = "src/main/resources/fotos_usuarios/usuario_" + usuario.getEmail() + ".jpg"; 
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = ImageIO.read(imageFile);
                Image scaledImage = image.getScaledInstance(180, -1, Image.SCALE_SMOOTH); // Ajusta el tamaño de la imagen si es necesario
                lblFoto.setIcon(new ImageIcon(scaledImage));
            } else {
                lblFoto.setText("Foto no disponible");
            }
        } catch (Exception e) {
            lblFoto.setText("Error al cargar foto");
            System.err.println("Error al cargar la imagen para usuario ID " + usuario.getId() + ": " + e.getMessage());
        }
    }
}