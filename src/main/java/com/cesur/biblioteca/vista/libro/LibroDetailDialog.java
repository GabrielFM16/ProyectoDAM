package com.cesur.biblioteca.vista.libro;

import com.cesur.biblioteca.modelo.Libro;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File; // Para cargar la imagen desde el sistema de archivos
import javax.imageio.ImageIO; // Para leer la imagen

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class LibroDetailDialog extends JDialog {

    private final Libro libro;
    private JLabel lblTitulo, lblAutor, lblIsbn, lblGenero, lblAnio, lblNumEjemplares, lblDisponible, lblImagen;

    public LibroDetailDialog(Window parent, Libro libro) {
        super(parent, "Detalles del Libro: " + libro.getTitulo(), Dialog.ModalityType.APPLICATION_MODAL);
        this.libro = libro;
        setSize(550, 450);
        setLocationRelativeTo(parent);

        initComponents();
        loadLibroData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel de Datos
        JPanel dataPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        dataPanel.setBorder(BorderFactory.createTitledBorder("Información del Libro"));

        dataPanel.add(new JLabel("ID:"));
        dataPanel.add(new JLabel(String.valueOf(libro.getId()))); // ID no editable

        dataPanel.add(new JLabel("Título:"));
        lblTitulo = new JLabel();
        dataPanel.add(lblTitulo);

        dataPanel.add(new JLabel("Autor:"));
        lblAutor = new JLabel();
        dataPanel.add(lblAutor);

        dataPanel.add(new JLabel("ISBN:"));
        lblIsbn = new JLabel();
        dataPanel.add(lblIsbn);

        dataPanel.add(new JLabel("Género:"));
        lblGenero = new JLabel();
        dataPanel.add(lblGenero);

        dataPanel.add(new JLabel("Año Publicación:"));
        lblAnio = new JLabel();
        dataPanel.add(lblAnio);
        
        dataPanel.add(new JLabel("Nº Ejemplares:"));
        lblNumEjemplares = new JLabel();
        dataPanel.add(lblNumEjemplares);
        
        dataPanel.add(new JLabel("Disponible:"));
        lblDisponible = new JLabel();
        dataPanel.add(lblDisponible);

        add(dataPanel, BorderLayout.CENTER);

        // Panel de Imagen (Portada)
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createTitledBorder("Portada"));
        lblImagen = new JLabel();
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(lblImagen, BorderLayout.CENTER);
        
        add(imagePanel, BorderLayout.EAST); // O BorderLayout.SOUTH, dependiendo del diseño

        // Botón de cierre
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose()); // Cierra el diálogo
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.add(btnCerrar);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadLibroData() {
        lblTitulo.setText(libro.getTitulo());
        lblAutor.setText(libro.getAutor());
        lblIsbn.setText(libro.getIsbn());
        lblGenero.setText(libro.getGenero());
        lblAnio.setText(String.valueOf(libro.getAnioPublicacion()));
        lblNumEjemplares.setText(String.valueOf(libro.getNumEjemplares()));
        lblDisponible.setText(libro.isDisponible() ? "Sí" : "No");

        // Cargar imagen de la portada
        String imagePath = "src/main/resources/portadas/" + libro.getIsbn() + ".jpg"; // O .png
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = ImageIO.read(imageFile);
                // Escalar la imagen para que quepa en el JLabel. Ajustamos el tamaño si es necesario.
                Image scaledImage = image.getScaledInstance(180, -1, Image.SCALE_SMOOTH); // Aumentado ligeramente para el nuevo tamaño del diálogo
                lblImagen.setIcon(new ImageIcon(scaledImage));
            } else {
                lblImagen.setText("Portada no disponible");
            }
        } catch (Exception e) {
            lblImagen.setText("Error al cargar portada");
            System.err.println("Error al cargar la imagen para ISBN " + libro.getIsbn() + ": " + e.getMessage());
        }
    }
}
