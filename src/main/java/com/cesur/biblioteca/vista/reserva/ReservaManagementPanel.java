package com.cesur.biblioteca.vista.reserva;

import com.cesur.biblioteca.controlador.ReservaController;
import com.cesur.biblioteca.modelo.Reserva;
import com.cesur.biblioteca.modelo.EstadoReserva;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.util.List;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class ReservaManagementPanel extends JPanel {

    private final ReservaController reservaController;

    private JTextField txtUsuarioId, txtLibroId;
    private JTextField txtFechaReserva, txtFechaDevolucionPrevista, txtFechaDevolucionReal;
    private JComboBox<EstadoReserva> cmbEstado;
    
    // Campos para búsqueda
    private JTextField txtReservaIdBuscar;
    private JTextField txtLibroISBNBuscar;
    private JTextField txtUsuarioEmailBuscar;
    private JTextField txtUsuarioIdBuscar;    
    private JTextField txtLibroIdBuscar;    

    // Botones de acción
    private JButton btnAdd, btnUpdate, btnCancelarReserva, btnFinalizarReserva, btnProcesarDevolucion, btnRefresh, btnDelete; // Agregado btnDelete
    // Botones de búsqueda
    private JButton btnSearchById, btnSearchByISBN, btnSearchByEmail, btnSearchByUserId, btnSearchByBookId;

    private JTable reservaTable;
    private DefaultTableModel tableModel;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReservaManagementPanel(ReservaController reservaController) {
        this.reservaController = reservaController;
        setLayout(new BorderLayout(10, 10));

        initComponents();
        loadReservasIntoTable();
    }

    private void initComponents() {
        // --- Panel de Formulario para Añadir/Actualizar ---
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Detalles de la Reserva"));

        formPanel.add(new JLabel("ID Usuario:"));
        txtUsuarioId = new JTextField(10);
        formPanel.add(txtUsuarioId);

        formPanel.add(new JLabel("ID Libro:"));
        txtLibroId = new JTextField(10);
        formPanel.add(txtLibroId);

        formPanel.add(new JLabel("Fecha Reserva (dd/MM/yyyy):"));
        txtFechaReserva = new JTextField(10);
        txtFechaReserva.setEditable(true);
        txtFechaReserva.setToolTipText("Ingrese la fecha de reserva (dd/MM/yyyy)");
        formPanel.add(txtFechaReserva);

        formPanel.add(new JLabel("Fecha Devolución Prevista (dd/MM/yyyy):"));
        txtFechaDevolucionPrevista = new JTextField(10);
        txtFechaDevolucionPrevista.setEditable(true);
        txtFechaReserva.setToolTipText("Ingrese la fecha de devolución prevista (dd/MM/yyyy)");
        formPanel.add(txtFechaDevolucionPrevista);
        
        formPanel.add(new JLabel("Fecha Devolución Real (dd/MM/yyyy):"));
        txtFechaDevolucionReal = new JTextField(10);
        txtFechaDevolucionReal.setEditable(true);
        txtFechaDevolucionReal.setToolTipText("Ingrese la fecha de devolución real (dd/MM/yyyy) o deje en blanco");
        formPanel.add(txtFechaDevolucionReal);

        formPanel.add(new JLabel("Estado:"));
        cmbEstado = new JComboBox<>(EstadoReserva.values());
        formPanel.add(cmbEstado);

        // --- Panel de Botones de Acción (CRUD y Estado) ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("Procesar Préstamo");
        btnUpdate = new JButton("Actualizar Reserva");
        btnCancelarReserva = new JButton("Cancelar Reserva");
        btnFinalizarReserva = new JButton("Finalizar Reserva");
        btnProcesarDevolucion = new JButton("Procesar Devolución");
        btnRefresh = new JButton("Refrescar Lista");
        btnDelete = new JButton("Eliminar Reserva");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnCancelarReserva);
        buttonPanel.add(btnFinalizarReserva);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnProcesarDevolucion);
        buttonPanel.add(btnRefresh);

        // --- Panel de Búsqueda ---
        JPanel searchPanel = new JPanel(new GridLayout(6, 3, 5, 5)); 
        searchPanel.setBorder(BorderFactory.createTitledBorder("Buscar Reserva"));
        
        searchPanel.add(new JLabel("ID Reserva:"));
        txtReservaIdBuscar = new JTextField(10);
        searchPanel.add(txtReservaIdBuscar);
        btnSearchById = new JButton("Buscar por ID Reserva");
        searchPanel.add(btnSearchById);

        searchPanel.add(new JLabel("ID Usuario:"));
        txtUsuarioIdBuscar = new JTextField(10);
        searchPanel.add(txtUsuarioIdBuscar);
        btnSearchByUserId = new JButton("Buscar por ID Usuario");
        searchPanel.add(btnSearchByUserId);

        searchPanel.add(new JLabel("ID Libro:"));
        txtLibroIdBuscar = new JTextField(10);
        searchPanel.add(txtLibroIdBuscar);
        btnSearchByBookId = new JButton("Buscar por ID Libro");
        searchPanel.add(btnSearchByBookId);

        searchPanel.add(new JLabel("Email Usuario:"));
        txtUsuarioEmailBuscar = new JTextField(10);
        searchPanel.add(txtUsuarioEmailBuscar);
        btnSearchByEmail = new JButton("Buscar por Email Usuario");
        searchPanel.add(btnSearchByEmail);

        searchPanel.add(new JLabel("ISBN Libro:"));
        txtLibroISBNBuscar = new JTextField(10);
        searchPanel.add(txtLibroISBNBuscar);
        btnSearchByISBN = new JButton("Buscar por ISBN Libro");
        searchPanel.add(btnSearchByISBN);


        // --- Tabla de Reservas ---
        String[] columnNames = {"ID Reserva", "ID Usuario", "ID Libro", "Fecha Reserva", "Fecha Prevista", "Fecha Real", "Estado"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservaTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reservaTable);

        // Configuración del Layout principal (GridBagLayout)
        removeAll();
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Panel de Formulario
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(formPanel, gbc);

        // Panel de Botones (en una nueva fila)
        gbc.gridx = 0;
        gbc.gridy = 1; 
        gbc.gridwidth = 2; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Panel de la Tabla (ScrollPane)
        gbc.gridx = 0;
        gbc.gridy = 2; 
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(scrollPane, gbc);

        // Panel de Búsqueda
        gbc.gridx = 0;
        gbc.gridy = 3; 
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(searchPanel, gbc);

        addListeners();
    }

    private void addListeners() {
        btnAdd.addActionListener(e -> procesarPrestamo());
        btnUpdate.addActionListener(e -> actualizarReserva());
        btnCancelarReserva.addActionListener(e -> cancelarReserva()); 
        btnFinalizarReserva.addActionListener(e -> finalizarReserva()); 
        btnProcesarDevolucion.addActionListener(e -> procesarDevolucion());
        btnDelete.addActionListener(e -> eliminarReserva()); // Añadir listener para el nuevo botón

        btnSearchById.addActionListener(e -> buscarReservaPorId());
        btnSearchByISBN.addActionListener(e -> buscarReservasPorLibroISBN());
        btnSearchByEmail.addActionListener(e -> buscarReservasPorUsuarioEmail());
        btnSearchByUserId.addActionListener(e -> buscarReservasPorUsuarioId());
        btnSearchByBookId.addActionListener(e -> buscarReservasPorLibroId());

        btnRefresh.addActionListener(e -> loadReservasIntoTable());
        
        reservaTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && reservaTable.getSelectedRow() != -1) {
                cargarDatosReservaSeleccionada();
            }
        });
    }

    private void procesarPrestamo() {
        try {
            int usuarioId = Integer.parseInt(txtUsuarioId.getText());
            int libroId = Integer.parseInt(txtLibroId.getText());
            
            // La fecha de reserva se establece en el controlador de reservas al registrar el préstamo
            // Date fechaReserva = null; 

            LocalDate fechaDevolucionPrevistaLocal = null;
            String fechaPrevistaText = txtFechaDevolucionPrevista.getText().trim();
            if (!fechaPrevistaText.isEmpty()) {
                fechaDevolucionPrevistaLocal = LocalDate.parse(fechaPrevistaText, DATE_FORMATTER);
            } else {
                throw new IllegalArgumentException("La fecha de devolución prevista es obligatoria para procesar un préstamo.");
            }
            Date fechaDevolucionPrevista = Date.valueOf(fechaDevolucionPrevistaLocal);
            
            // Llamar al método del controlador de reservas
            reservaController.registrarPrestamo(libroId, usuarioId, fechaDevolucionPrevista); 

            JOptionPane.showMessageDialog(this, "Préstamo procesado con éxito y marcada como PENDIENTE.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadReservasIntoTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese IDs válidos y una fecha de devolución prevista válida.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha de devolución prevista inválido (dd/MM/yyyy).", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Procesar Préstamo", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarReserva() {
        int selectedRow = reservaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una reserva de la tabla para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int reservaId = (int) tableModel.getValueAt(selectedRow, 0);
            int usuarioId = Integer.parseInt(txtUsuarioId.getText());
            int libroId = Integer.parseInt(txtLibroId.getText());
            
            // Obtener fecha de reserva del campo de texto
            LocalDate fechaReservaLocal = null;
            String fechaReservaText = txtFechaReserva.getText().trim();
            if (!fechaReservaText.isEmpty()) {
                fechaReservaLocal = LocalDate.parse(fechaReservaText, DATE_FORMATTER);
            } else {
                throw new IllegalArgumentException("La fecha de reserva es obligatoria para la actualización.");
            }
            Date fechaReserva = Date.valueOf(fechaReservaLocal);
            
            // Obtener fecha de devolución prevista del campo de texto
            LocalDate fechaDevolucionPrevistaLocal = null;
            String fechaPrevistaText = txtFechaDevolucionPrevista.getText().trim();
            if (!fechaPrevistaText.isEmpty()) {
                fechaDevolucionPrevistaLocal = LocalDate.parse(fechaPrevistaText, DATE_FORMATTER);
            } else {
                throw new IllegalArgumentException("La fecha de devolución prevista es obligatoria para la actualización.");
            }
            Date fechaDevolucionPrevista = Date.valueOf(fechaDevolucionPrevistaLocal);

            // Obtener fecha de devolución real del campo de texto
            Date fechaDevolucionReal = null; 
            String fechaRealText = txtFechaDevolucionReal.getText().trim();
            if (!fechaRealText.isEmpty()) {
                fechaDevolucionReal = Date.valueOf(LocalDate.parse(fechaRealText, DATE_FORMATTER));
            }
            
            EstadoReserva estadoEnum = (EstadoReserva) cmbEstado.getSelectedItem();
            if (estadoEnum == null) {
                    throw new IllegalArgumentException("Debe seleccionar un estado para la reserva.");
            }
            
            // Llamar al método del controlador de reservas
            reservaController.actualizarReserva(reservaId, libroId, usuarioId, fechaReserva, fechaDevolucionPrevista, fechaDevolucionReal, estadoEnum.name());
            
            JOptionPane.showMessageDialog(this, "Reserva actualizada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadReservasIntoTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese IDs válidos y fechas en formato dd/MM/yyyy.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido (dd/MM/yyyy) en uno de los campos de fecha.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Actualizar Reserva", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cancelarReserva() {
        int selectedRow = reservaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una reserva de la tabla para cancelar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea CANCELAR esta reserva? Su estado cambiará a CANCELADO.", "Confirmar Cancelación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                
                reservaController.cancelarReserva(id); 
                
                JOptionPane.showMessageDialog(this, "Reserva CANCELADA con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadReservasIntoTable();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Cancelar Reserva", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void finalizarReserva() {
        int selectedRow = reservaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una reserva de la tabla para FINALIZAR.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int reservaId = (int) tableModel.getValueAt(selectedRow, 0);
            
            reservaController.finalizarReserva(reservaId); 
            
            JOptionPane.showMessageDialog(this, "Reserva FINALIZADA con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            loadReservasIntoTable();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Finalizar Reserva", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarReserva() {
        int selectedRow = reservaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una reserva de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea ELIMINAR esta reserva de forma permanente?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                
                // Llamar al método del controlador para eliminar la reserva
                boolean resultado = reservaController.eliminarReserva(id); 
                
                if (resultado) {
                    JOptionPane.showMessageDialog(this, "Reserva eliminada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadReservasIntoTable(); // Recargar la tabla para reflejar los cambios
                }
                else {
                    JOptionPane.showMessageDialog(this, "No se ha podido eliminar la reserva.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Eliminar Reserva", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al eliminar la reserva: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void procesarDevolucion() {
        int selectedRow = reservaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una reserva de la tabla para PROCESAR SU DEVOLUCIÓN.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int reservaId = (int) tableModel.getValueAt(selectedRow, 0);
            
            // Obtener la fecha de devolución real del campo de texto
            Date fechaDevolucionReal = null;
            String fechaRealText = txtFechaDevolucionReal.getText().trim();
            if (fechaRealText.isEmpty()) {
                // Si el campo está vacío, optamos por la fecha actual del sistema.
                fechaDevolucionReal = Date.valueOf(LocalDate.now());
                JOptionPane.showMessageDialog(this, "No se especificó la fecha de devolución real. Se usará la fecha actual del sistema.", "Información", JOptionPane.INFORMATION_MESSAGE);
            } else {
                fechaDevolucionReal = Date.valueOf(LocalDate.parse(fechaRealText, DATE_FORMATTER));
            }
            
            // Llamar al método del controlador de reservas
            reservaController.registrarDevolucion(reservaId, fechaDevolucionReal);
            
            JOptionPane.showMessageDialog(this, "Devolución procesada con éxito. Se ha actualizado el estado y el stock (y posibles penalizaciones).", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); // Limpiar el formulario después de una acción exitosa
            loadReservasIntoTable();
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha de devolución real inválido (dd/MM/yyyy).", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Procesar Devolución", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Imprimir la pila de errores para depuración
        }
    }

    private void buscarReservaPorId() {
        try {
            String idText = txtReservaIdBuscar.getText();
            if (idText.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo ID de reserva no puede estar vacío para la búsqueda.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = Integer.parseInt(idText);
            Reserva reserva = reservaController.buscarReservaPorId(id);
            if (reserva != null) {
                mostrarDetalleReserva(reserva);
            } else {
                JOptionPane.showMessageDialog(this, "Reserva con ID " + id + " no encontrada.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de reserva válido.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarReservasPorLibroISBN() {
        try {
            String isbn = txtLibroISBNBuscar.getText().trim();
            if (isbn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese el ISBN del libro para la búsqueda.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<Reserva> reservas = reservaController.obtenerReservasPorLibroIsbn(isbn);
            if (!reservas.isEmpty()) {
                loadReservasIntoTable(reservas);
                JOptionPane.showMessageDialog(this, "Se encontraron " + reservas.size() + " reservas para el ISBN: " + isbn, "Búsqueda Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontraron reservas para el ISBN: " + isbn, "Información", JOptionPane.INFORMATION_MESSAGE);
                loadReservasIntoTable();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarReservasPorUsuarioEmail() {
        try {
            String email = txtUsuarioEmailBuscar.getText().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese el Email del usuario para la búsqueda.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<Reserva> reservas = reservaController.obtenerReservasPorUsuarioEmail(email);
            if (!reservas.isEmpty()) {
                loadReservasIntoTable(reservas);
                JOptionPane.showMessageDialog(this, "Se encontraron " + reservas.size() + " reservas para el Email: " + email, "Búsqueda Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontraron reservas para el Email: " + email, "Información", JOptionPane.INFORMATION_MESSAGE);
                loadReservasIntoTable();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarReservasPorUsuarioId() {
        try {
            String idText = txtUsuarioIdBuscar.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese el ID del usuario para la búsqueda.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int userId = Integer.parseInt(idText);
            List<Reserva> reservas = reservaController.obtenerReservasPorUsuarioID(userId);
            if (!reservas.isEmpty()) {
                loadReservasIntoTable(reservas);
                JOptionPane.showMessageDialog(this, "Se encontraron " + reservas.size() + " reservas para el Usuario ID: " + userId, "Búsqueda Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontraron reservas para el Usuario ID: " + userId, "Información", JOptionPane.INFORMATION_MESSAGE);
                loadReservasIntoTable();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de usuario válido.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        }  catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarReservasPorLibroId() {
        try {
            String idText = txtLibroIdBuscar.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese el ID del libro para la búsqueda.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int bookId = Integer.parseInt(idText);
            List<Reserva> reservas = reservaController.obtenerReservasPorLibroID(bookId);
            if (!reservas.isEmpty()) {
                loadReservasIntoTable(reservas);
                JOptionPane.showMessageDialog(this, "Se encontraron " + reservas.size() + " reservas para el Libro ID: " + bookId, "Búsqueda Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontraron reservas para el Libro ID: " + bookId, "Información", JOptionPane.INFORMATION_MESSAGE);
                loadReservasIntoTable();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de libro válido.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarDetalleReserva(Reserva reserva) {
        ReservaDetailDialog dialog = new ReservaDetailDialog(SwingUtilities.getWindowAncestor(this), reserva);
        dialog.setVisible(true);
    }

    private void loadReservasIntoTable(List<Reserva> reservas) {
        tableModel.setRowCount(0); // Limpiar la tabla
        for (Reserva reserva : reservas) {
            String fechaReservaStr = (reserva.getFechaReserva() != null) ? 
                                     reserva.getFechaReserva().toLocalDate().format(DATE_FORMATTER) : ""; // Mostrar vacío en lugar de N/A para edición
            String fechaDevolucionPrevistaStr = (reserva.getFechaDevolucionPrevista() != null) ? 
                                       reserva.getFechaDevolucionPrevista().toLocalDate().format(DATE_FORMATTER) : ""; // Mostrar vacío en lugar de N/A para edición
            String fechaDevolucionRealStr = (reserva.getFechaDevolucionReal() != null) ? 
                                             reserva.getFechaDevolucionReal().toLocalDate().format(DATE_FORMATTER) : ""; // Mostrar vacío en lugar de N/A para edición
            tableModel.addRow(new Object[]{
                reserva.getId(),
                reserva.getUsuarioId(),
                reserva.getLibroId(),
                fechaReservaStr,
                fechaDevolucionPrevistaStr,
                fechaDevolucionRealStr, 
                reserva.getEstado()
            });
        }
    }

    private void loadReservasIntoTable() {
        loadReservasIntoTable(reservaController.obtenerTodasLasReservas());
    }

    private void clearForm() {
        txtUsuarioId.setText("");
        txtLibroId.setText("");
        txtFechaReserva.setText("");
        txtFechaDevolucionPrevista.setText("");
        txtFechaDevolucionReal.setText("");
        cmbEstado.setSelectedItem(EstadoReserva.PENDIENTE); 
        txtReservaIdBuscar.setText("");
        txtLibroISBNBuscar.setText("");
        txtUsuarioEmailBuscar.setText("");
        txtUsuarioIdBuscar.setText("");
        txtLibroIdBuscar.setText("");   
        reservaTable.clearSelection();
    }
    
    private void cargarDatosReservaSeleccionada() {
        int selectedRow = reservaTable.getSelectedRow();
        if (selectedRow != -1) {
            txtUsuarioId.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtLibroId.setText(tableModel.getValueAt(selectedRow, 2).toString());
            txtFechaReserva.setText((String) tableModel.getValueAt(selectedRow, 3));
            txtFechaDevolucionPrevista.setText((String) tableModel.getValueAt(selectedRow, 4));
            txtFechaDevolucionReal.setText((String) tableModel.getValueAt(selectedRow, 5)); 
            String estadoStr = (String) tableModel.getValueAt(selectedRow, 6); 
            cmbEstado.setSelectedItem(EstadoReserva.valueOf(estadoStr));
        }
    }
}