package com.cesur.biblioteca.vista.usuario;

import com.cesur.biblioteca.controlador.UsuarioController;
import com.cesur.biblioteca.modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Date; // Usaremos java.sql.Date para la base de datos
import java.util.List;

/**
 *
 * @author Gabriel Fernandez Magan
 */

public class UsuarioManagementPanel extends JPanel {

    private final UsuarioController usuarioController;

    // Componentes de la UI
    private JTextField txtNombre, txtApellidos, txtEmail, txtTelefono;
    private JTextField txtUsuarioIdBuscar; // Campo para buscar por ID
    private JTextField txtUsuarioEmailBuscar; // Campo para buscar por Email
    private JTextField txtFechaFinSancion; // Para sancionar usuario
    private JCheckBox chkActivo;
    private JButton btnAdd, btnUpdate, btnDelete, btnActivate, btnDeactivate, btnSanction, btnRemoveSanction, btnSearchById, btnSearchByEmail, btnRefresh;
    private JTable usuarioTable;
    private DefaultTableModel tableModel;

    // Formateador de fechas
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public UsuarioManagementPanel(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
        // Eliminar o comentar esta línea, ya que el layout se establecerá en initComponents()
        // setLayout(new BorderLayout(10, 10)); 

        initComponents();
        loadUsuariosIntoTable();
    }

    private void initComponents() {
        // --- Panel de Formulario para Añadir/Actualizar ---
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5)); // 6 Filas (Nombre, Apellidos, Email, Telefono, Activo)
        formPanel.setBorder(BorderFactory.createTitledBorder("Detalles del Usuario"));

        formPanel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(20);
        formPanel.add(txtNombre);

        formPanel.add(new JLabel("Apellidos:"));
        txtApellidos = new JTextField(20);
        formPanel.add(txtApellidos);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField(20);
        formPanel.add(txtTelefono);

        formPanel.add(new JLabel("Activo:"));
        chkActivo = new JCheckBox();
        formPanel.add(chkActivo);

        // --- Panel de Botones de Acción CRUD y Estado ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("Añadir Usuario");
        btnUpdate = new JButton("Actualizar Usuario");
        btnDelete = new JButton("Eliminar Usuario");
        btnActivate = new JButton("Activar Usuario");
        btnDeactivate = new JButton("Desactivar Usuario");
        btnSanction = new JButton("Sancionar Usuario");
        btnRemoveSanction = new JButton("Quitar Sanción");
        btnRefresh = new JButton("Refrescar Lista");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnActivate);
        buttonPanel.add(btnDeactivate);
        buttonPanel.add(btnSanction);
        buttonPanel.add(btnRemoveSanction);
        buttonPanel.add(btnRefresh);

        // --- Panel para Sancionar con Fecha ---
        JPanel sanctionDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sanctionDatePanel.setBorder(BorderFactory.createTitledBorder("Sancionar (dd/MM/yyyy)"));
        sanctionDatePanel.add(new JLabel("Fecha Fin Sanción:"));
        txtFechaFinSancion = new JTextField(10);
        sanctionDatePanel.add(txtFechaFinSancion);

        // --- Panel de Búsqueda (Combinado para ID y Email) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Buscar Usuario"));
        
        searchPanel.add(new JLabel("ID Usuario:"));
        txtUsuarioIdBuscar = new JTextField(10);
        searchPanel.add(txtUsuarioIdBuscar);
        btnSearchById = new JButton("Buscar por ID");
        searchPanel.add(btnSearchById);

        searchPanel.add(Box.createHorizontalStrut(20)); // Espacio entre campos de búsqueda
        
        searchPanel.add(new JLabel("Email Usuario:"));
        txtUsuarioEmailBuscar = new JTextField(20);
        searchPanel.add(txtUsuarioEmailBuscar);
        btnSearchByEmail = new JButton("Buscar por Email");
        searchPanel.add(btnSearchByEmail);

        // --- Tabla de Usuarios ---
        String[] columnNames = {"ID Usuario", "Nombre", "Apellidos", "Email", "Teléfono", "Activo", "Sancionado", "Fin Sanción"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usuarioTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(usuarioTable);

        // --- Configuración del Layout principal (GridBagLayout) ---
        removeAll(); // Limpiar el layout anterior para reconfigurar
        setLayout(new GridBagLayout()); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Márgenes para los componentes

        // 1. Formulario (fila 0, ocupa ambas columnas)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa las dos columnas
        gbc.fill = GridBagConstraints.HORIZONTAL; // Permite que se expanda horizontalmente
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0; // Distribuye el espacio horizontal a este componente
        add(formPanel, gbc);

        // 2. Panel de Sanción con Fecha (fila 1, ocupa ambas columnas - ¡CAMBIO AQUÍ!)
        gbc.gridx = 0;
        gbc.gridy = 1; // Ahora está en la fila 1
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        add(sanctionDatePanel, gbc);
        
        // 3. Botones de Acción (fila 2, ocupa ambas columnas - ¡CAMBIO AQUÍ!)
        gbc.gridx = 0;
        gbc.gridy = 2; // Ahora está en la fila 2
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        add(buttonPanel, gbc);

        // 4. Tabla (fila 3, ocupa ambas columnas y el espacio restante vertical)
        gbc.gridx = 0;
        gbc.gridy = 3; 
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH; 
        add(scrollPane, gbc);

        // 5. Panel de Búsqueda (fila 4, ocupa ambas columnas)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
        add(searchPanel, gbc);

        addListeners();
    }

    private void addListeners() {
        btnAdd.addActionListener(e -> añadirUsuario());
        btnUpdate.addActionListener(e -> actualizarUsuario());
        btnDelete.addActionListener(e -> eliminarUsuario());
        btnActivate.addActionListener(e -> activarUsuario());
        btnDeactivate.addActionListener(e -> desactivarUsuario());
        btnSanction.addActionListener(e -> sancionarUsuario());
        btnRemoveSanction.addActionListener(e -> quitarSancionUsuario());
        btnSearchById.addActionListener(e -> buscarUsuarioPorId());
        btnSearchByEmail.addActionListener(e -> buscarUsuarioPorEmail());
        btnRefresh.addActionListener(e -> loadUsuariosIntoTable());
        
        usuarioTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && usuarioTable.getSelectedRow() != -1) {
                cargarDatosUsuarioSeleccionado();
            }
        });
    }

    private void añadirUsuario() {
        try {
            String nombre = txtNombre.getText();
            String apellidos = txtApellidos.getText();
            String email = txtEmail.getText();
            String telefono = txtTelefono.getText();

            usuarioController.añadirUsuario(nombre, apellidos, email, telefono);
            JOptionPane.showMessageDialog(this, "Usuario añadido con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadUsuariosIntoTable();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Añadir Usuario", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void actualizarUsuario() {
        int selectedRow = usuarioTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un usuario de la tabla para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nombre = txtNombre.getText();
            String apellidos = txtApellidos.getText();
            String email = txtEmail.getText();
            String telefono = txtTelefono.getText();
            
            usuarioController.actualizarUsuario(id, nombre, apellidos, email, telefono, null, null, null); 
            JOptionPane.showMessageDialog(this, "Usuario actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadUsuariosIntoTable();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Actualizar Usuario", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void eliminarUsuario() {
        int selectedRow = usuarioTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un usuario de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar este usuario?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                
                // Llamar al método del controlador para eliminar el usuario
                boolean resultado = usuarioController.eliminarUsuario(id);
                
                if (resultado) {
                    JOptionPane.showMessageDialog(this, "Usuario eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadUsuariosIntoTable(); // Recargar la tabla para reflejar los cambios
                } else {
                    JOptionPane.showMessageDialog(this, "No se ha podido eliminar el usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Eliminar Usuario", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    private void activarUsuario() {
        int selectedRow = usuarioTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un usuario de la tabla para activar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            usuarioController.activarUsuario(id);
            JOptionPane.showMessageDialog(this, "Usuario activado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            loadUsuariosIntoTable();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Activar Usuario", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void desactivarUsuario() {
        int selectedRow = usuarioTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un usuario de la tabla para desactivar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            usuarioController.desactivarUsuario(id);
            JOptionPane.showMessageDialog(this, "Usuario desactivado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            loadUsuariosIntoTable();
        }  catch (IllegalStateException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Desactivar Usuario", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void sancionarUsuario() {
        int selectedRow = usuarioTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un usuario de la tabla para sancionar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String fechaFinStr = txtFechaFinSancion.getText();
            if (fechaFinStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese la fecha de fin de sanción (dd/MM/yyyy).", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LocalDate localDate = LocalDate.parse(fechaFinStr, DATE_FORMATTER);
            Date fechaFinSancion = Date.valueOf(localDate);

            usuarioController.sancionarUsuario(id, fechaFinSancion);
            JOptionPane.showMessageDialog(this, "Usuario sancionado con éxito hasta el " + fechaFinStr + ".", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            loadUsuariosIntoTable();
            txtFechaFinSancion.setText("");
        } catch (java.time.format.DateTimeParseException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido (dd/MM/yyyy) o " + ex.getMessage(), "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Sancionar Usuario", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void quitarSancionUsuario() {
        int selectedRow = usuarioTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un usuario de la tabla para quitar la sanción.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            usuarioController.eliminarSancionUsuario(id);
            JOptionPane.showMessageDialog(this, "Sanción de usuario eliminada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            loadUsuariosIntoTable();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al Quitar Sanción", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void buscarUsuarioPorId() {
        try {
            String idText = txtUsuarioIdBuscar.getText();
            if (idText.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo ID de usuario no puede estar vacío para la búsqueda.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = Integer.parseInt(idText);
            Usuario usuario = usuarioController.buscarUsuarioPorId(id);
            if (usuario != null) {
                mostrarDetalleUsuario(usuario);
            } else {
                JOptionPane.showMessageDialog(this, "Usuario con ID " + id + " no encontrado.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de usuario válido.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al buscar usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void buscarUsuarioPorEmail() {
        try {
            String email = txtUsuarioEmailBuscar.getText();
            if (email.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Email de usuario no puede estar vacío para la búsqueda.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Usuario usuario = usuarioController.buscarUsuarioPorEmail(email);
            if (usuario != null) {
                mostrarDetalleUsuario(usuario);
            } else {
                JOptionPane.showMessageDialog(this, "Usuario con Email '" + email + "' no encontrado.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado al buscar usuario por Email: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void mostrarDetalleUsuario(Usuario usuario) {
        UsuarioDetailDialog dialog = new UsuarioDetailDialog(SwingUtilities.getWindowAncestor(this), usuario);
        dialog.setVisible(true);
    }

    private void loadUsuariosIntoTable() {
        tableModel.setRowCount(0); // Limpiar la tabla
        List<Usuario> usuarios = usuarioController.obtenerTodosUsuarios();
        for (Usuario usuario : usuarios) {
            String fechaFinSancionStr = (usuario.getFechaFinSancion() != null) ?
                                         usuario.getFechaFinSancion().toLocalDate().format(DATE_FORMATTER) : "N/A";
            tableModel.addRow(new Object[]{
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.isActivo() ? "Sí" : "No",
                usuario.isSancionado() ? "Sí" : "No",
                fechaFinSancionStr
            });
        }
    }

    private void clearForm() {
        txtNombre.setText("");
        txtApellidos.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        chkActivo.setSelected(false);
        txtFechaFinSancion.setText("");
        txtUsuarioIdBuscar.setText("");
        txtUsuarioEmailBuscar.setText("");
        usuarioTable.clearSelection();
    }
    
    private void cargarDatosUsuarioSeleccionado() {
        int selectedRow = usuarioTable.getSelectedRow();
        if (selectedRow != -1) {
            txtNombre.setText((String) tableModel.getValueAt(selectedRow, 1));
            txtApellidos.setText((String) tableModel.getValueAt(selectedRow, 2));
            txtEmail.setText((String) tableModel.getValueAt(selectedRow, 3));
            txtTelefono.setText((String) tableModel.getValueAt(selectedRow, 4));
            chkActivo.setSelected(((String) tableModel.getValueAt(selectedRow, 5)).equals("Sí"));
            // La fecha de fin de sanción no se carga en el campo de texto del formulario
            // ya que se usa para *establecer* una sanción, no para mostrar la existente.
            txtFechaFinSancion.setText(""); 
        }
    }
}