package com.cesur.biblioteca.vista.reserva;

import com.cesur.biblioteca.modelo.Reserva;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Diálogo para mostrar los detalles de una reserva específica.
 * Proporciona una vista de solo lectura de la información de la reserva.
 */
public class ReservaDetailDialog extends JDialog {

    private final Reserva reserva;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReservaDetailDialog(Window owner, Reserva reserva) {
        super(owner, "Detalles de la Reserva", ModalityType.APPLICATION_MODAL);
        this.reserva = reserva;
        initComponents();
        pack(); // Ajusta el tamaño del diálogo a sus componentes
        setLocationRelativeTo(owner); // Centra el diálogo respecto a la ventana padre
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel detailPanel = new JPanel(new GridLayout(0, 2, 5, 5)); // 0 filas, 2 columnas

        detailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Añadir los detalles de la reserva
        addDetailRow(detailPanel, "ID Reserva:", String.valueOf(reserva.getId()));
        addDetailRow(detailPanel, "ID Usuario:", String.valueOf(reserva.getUsuarioId()));
        addDetailRow(detailPanel, "ID Libro:", String.valueOf(reserva.getLibroId()));

        // --- INICIO DE LAS CORRECCIONES EN LAS FECHAS ---
        String fechaReservaStr = Optional.ofNullable(reserva.getFechaReserva())
                                         .map(Date::toLocalDate) // Convertir java.sql.Date a LocalDate
                                         .map(d -> d.format(DATE_FORMATTER)) // Formatear LocalDate a String
                                         .orElse("N/A");
        addDetailRow(detailPanel, "Fecha Reserva:", fechaReservaStr);

        String fechaDevolucionPrevistaStr = Optional.ofNullable(reserva.getFechaDevolucionPrevista())
                                                    .map(Date::toLocalDate) // Convertir java.sql.Date a LocalDate
                                                    .map(d -> d.format(DATE_FORMATTER)) // Formatear LocalDate a String
                                                    .orElse("N/A");
        addDetailRow(detailPanel, "Fecha Prevista:", fechaDevolucionPrevistaStr);

        String fechaDevolucionRealStr = Optional.ofNullable(reserva.getFechaDevolucionReal())
                                                .map(Date::toLocalDate) // Convertir java.sql.Date a LocalDate
                                                .map(d -> d.format(DATE_FORMATTER)) // Formatear LocalDate a String
                                                .orElse("N/A");
        addDetailRow(detailPanel, "Fecha Real:", fechaDevolucionRealStr);
        // --- FIN DE LAS CORRECCIONES EN LAS FECHAS ---

        String estadoStr = Optional.ofNullable(reserva.getEstado()).orElse("N/A");
        addDetailRow(detailPanel, "Estado:", estadoStr);

        add(detailPanel, BorderLayout.CENTER);

        // Botón para cerrar el diálogo
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dispose()); // Cierra el diálogo al hacer clic
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Método auxiliar para agregar un par etiqueta-valor al panel.
     */
    private void addDetailRow(JPanel panel, String labelText, String valueText) {
        panel.add(new JLabel(labelText));
        panel.add(new JLabel("<html><b>" + valueText + "</b></html>")); // Usar HTML para negrita
    }
}