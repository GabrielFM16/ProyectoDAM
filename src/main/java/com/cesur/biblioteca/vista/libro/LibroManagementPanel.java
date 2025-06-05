package com.cesur.biblioteca.vista.libro;

import com.cesur.biblioteca.controlador.LibroController;
import com.cesur.biblioteca.modelo.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class LibroManagementPanel extends JPanel {

    private final LibroController libroController;

    // Componentes de la GUI
    private JTextField txtTitulo, txtAutor, txtIsbn, txtGenero, txtAnioPublicacion, txtNumEjemplares;
    private JTextField txtLibroIdBuscar; // Campo para buscar por ID
    private JTextField txtLibroIsbnBuscar; // Campo para buscar por ISBN
    private JCheckBox chkDisponible;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearchById, btnSearchByIsbn, btnRefresh;
    private JTable libroTable;
    private DefaultTableModel tableModel;

    public LibroManagementPanel(LibroController libroController) {
        this.libroController = libroController;
        initComponents();
        loadLibrosIntoTable();
    }

    private void initComponents() {
        // --- Panel de Formulario para Añadir/Actualizar ---
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Detalles del Libro"));

        formPanel.add(new JLabel("Título:"));
        txtTitulo = new JTextField(20);
        formPanel.add(txtTitulo);

        formPanel.add(new JLabel("Autor:"));
        txtAutor = new JTextField(20);
        formPanel.add(txtAutor);

        formPanel.add(new JLabel("ISBN:"));
        txtIsbn = new JTextField(20);
        formPanel.add(txtIsbn);

        formPanel.add(new JLabel("Género:"));
        txtGenero = new JTextField(20);
        formPanel.add(txtGenero);

        formPanel.add(new JLabel("Año Publicación:"));
        txtAnioPublicacion = new JTextField(20);
        formPanel.add(txtAnioPublicacion);

        formPanel.add(new JLabel("Nº Ejemplares:"));
        txtNumEjemplares = new JTextField(20);
        formPanel.add(txtNumEjemplares);

        formPanel.add(new JLabel("Disponible:"));
        chkDisponible = new JCheckBox();
        formPanel.add(chkDisponible);

        // --- Panel de Botones de Acción ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("Añadir Libro");
        btnUpdate = new JButton("Actualizar Libro");
        btnDelete = new JButton("Eliminar Libro");
        btnRefresh = new JButton("Refrescar Lista");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        // --- Panel de Búsqueda (Combinado para ID y ISBN) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Buscar Libro"));
        
        searchPanel.add(new JLabel("ID Libro:"));
        txtLibroIdBuscar = new JTextField(10);
        searchPanel.add(txtLibroIdBuscar);
        btnSearchById = new JButton("Buscar por ID");
        searchPanel.add(btnSearchById);

        searchPanel.add(Box.createHorizontalStrut(20)); // Espacio entre campos de búsqueda
        
        searchPanel.add(new JLabel("ISBN Libro:")); // Campo de búsqueda por ISBN
        txtLibroIsbnBuscar = new JTextField(15); // Un poco más grande para el ISBN
        searchPanel.add(txtLibroIsbnBuscar);
        btnSearchByIsbn = new JButton("Buscar por ISBN");
        searchPanel.add(btnSearchByIsbn);

        // --- Tabla de Libros ---
        String[] columnNames = {"ID Libro", "Título", "Autor", "ISBN", "Género", "Año Publicación", "Nº Ejemplares", "Disponible"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        libroTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(libroTable);

        // --- Layout del Panel Principal con GridBagLayout (Opción 2) ---
        removeAll();
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 1. Formulario (fila 0, ocupa ambas columnas)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        add(formPanel, gbc);

        // 2. Botones de Acción (fila 1, ocupa ambas columnas)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        add(buttonPanel, gbc);

        // 3. Tabla (fila 2, ocupa ambas columnas y el espacio restante)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        // 4. Panel de Búsqueda (fila 3, ocupa ambas columnas)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
        add(searchPanel, gbc);

        // --- Añadir Listeners ---
        addListeners();
    }

    private void addListeners() {
        btnAdd.addActionListener(e -> añadirLibro());
        btnUpdate.addActionListener(e -> actualizarLibro());
        btnDelete.addActionListener(e -> eliminarLibro());
        btnSearchById.addActionListener(e -> buscarLibroPorId());
        btnSearchByIsbn.addActionListener(e -> buscarLibroPorIsbn());
        btnRefresh.addActionListener(e -> loadLibrosIntoTable());
        
        libroTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && libroTable.getSelectedRow() != -1) {
                cargarDatosLibroSeleccionado();
            }
        });
    }

    private void añadirLibro() {
        try {
            String titulo = txtTitulo.getText();
            String autor = txtAutor.getText();
            String isbn = txtIsbn.getText();
            String genero = txtGenero.getText();
            int anioPublicacion = Integer.parseInt(txtAnioPublicacion.getText());
            int numEjemplares = Integer.parseInt(txtNumEjemplares.getText());

            // Se llama al controlador de libros
            libroController.añadirLibro(titulo, autor, isbn, genero, anioPublicacion, numEjemplares);
            JOptionPane.showMessageDialog(this, "Libro añadido con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadLibrosIntoTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El año de publicación y el número de ejemplares deben ser números válidos.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Añadir Libro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarLibro() {
        int selectedRow = libroTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un libro de la tabla para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String titulo = txtTitulo.getText();
            String autor = txtAutor.getText();
            String isbn = txtIsbn.getText();
            String genero = txtGenero.getText();
            
            int anioPublicacion = 0; // Se inicializa para evitar errores si el campo está vacío
            try {
                String anioText = txtAnioPublicacion.getText();
                if (!anioText.trim().isEmpty()) {
                    anioPublicacion = Integer.parseInt(anioText);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El año de publicación no es válido o está vacío. Se mantendrá el actual o se ignorará si se deja vacío.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

            int numEjemplares = 0; // Se inicializa para evitar errores si el campo está vacío
            try {
                String numEjemplaresText = txtNumEjemplares.getText();
                if (!numEjemplaresText.trim().isEmpty()) {
                    numEjemplares = Integer.parseInt(numEjemplaresText);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El número de ejemplares no es válido o está vacío. Se mantendrá el actual o se ignorará si se deja vacío.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
            
            boolean disponible = chkDisponible.isSelected();

            // Se llama al controlador de libros
            libroController.actualizarLibro(id, titulo, autor, isbn, genero, anioPublicacion, disponible, numEjemplares);
            JOptionPane.showMessageDialog(this, "Libro actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadLibrosIntoTable();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Actualizar Libro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void eliminarLibro() {
        int selectedRow = libroTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un libro de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar este libro?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                
                boolean resultado = libroController.eliminarLibro(id);
                
                if (resultado) {
                    JOptionPane.showMessageDialog(this, "Libro eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadLibrosIntoTable();
                } else {
                    JOptionPane.showMessageDialog(this, "No se ha podido eliminar el libro.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Eliminar Libro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void buscarLibroPorId() {
        try {
            String idText = txtLibroIdBuscar.getText();
            if (idText.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo ID de libro no puede estar vacío para la búsqueda.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = Integer.parseInt(idText);
            Libro libro = libroController.buscarLibroPorId(id);
            if (libro != null) {
                mostrarDetalleLibro(libro);
            } else {
                JOptionPane.showMessageDialog(this, "Libro con ID " + id + " no encontrado.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de libro válido.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al buscar libro: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void buscarLibroPorIsbn() {
        try {
            String isbn = txtLibroIsbnBuscar.getText();
            if (isbn.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo ISBN de libro no puede estar vacío para la búsqueda.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Libro libro = libroController.buscarLibroPorIsbn(isbn);
            if (libro != null) {
                mostrarDetalleLibro(libro);
            } else {
                JOptionPane.showMessageDialog(this, "Libro con ISBN '" + isbn + "' no encontrado.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al buscar libro por ISBN: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void mostrarDetalleLibro(Libro libro) {
        LibroDetailDialog dialog = new LibroDetailDialog(SwingUtilities.getWindowAncestor(this), libro);
        dialog.setVisible(true);
    }

    private void loadLibrosIntoTable() {
        tableModel.setRowCount(0);
        List<Libro> libros = libroController.obtenerTodosLosLibros();
        for (Libro libro : libros) {
            tableModel.addRow(new Object[]{
                libro.getId(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getIsbn(),
                libro.getGenero(),
                libro.getAnioPublicacion(),
                libro.getNumEjemplares(),
                libro.isDisponible()
            });
        }
    }

    private void clearForm() {
        txtTitulo.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtGenero.setText("");
        txtAnioPublicacion.setText("");
        txtNumEjemplares.setText("");
        chkDisponible.setSelected(false);
        txtLibroIdBuscar.setText("");
        txtLibroIsbnBuscar.setText("");
        libroTable.clearSelection();
    }
    
    private void cargarDatosLibroSeleccionado() {
        int selectedRow = libroTable.getSelectedRow();
        if (selectedRow != -1) {
            txtTitulo.setText((String) tableModel.getValueAt(selectedRow, 1));
            txtAutor.setText((String) tableModel.getValueAt(selectedRow, 2));
            txtIsbn.setText((String) tableModel.getValueAt(selectedRow, 3));
            txtGenero.setText((String) tableModel.getValueAt(selectedRow, 4));
            txtAnioPublicacion.setText(String.valueOf(tableModel.getValueAt(selectedRow, 5)));
            txtNumEjemplares.setText(String.valueOf(tableModel.getValueAt(selectedRow, 6)));
            chkDisponible.setSelected((Boolean) tableModel.getValueAt(selectedRow, 7));
        }
    }
}