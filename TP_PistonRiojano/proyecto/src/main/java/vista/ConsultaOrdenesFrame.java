package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import dao.OrdenDAO;
import modelo.EstadoOrden;

/**
 * RF5 - Consulta de ordenes filtrando por estado y cambio de estado
 * de la orden seleccionada.
 */
public class ConsultaOrdenesFrame extends JInternalFrame {

    private final OrdenDAO dao = new OrdenDAO();
    private final JComboBox<EstadoOrden> cmbEstado = new JComboBox<>(EstadoOrden.values());
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Numero", "Fecha", "Patente", "Mecanico", "Total"}, 0);
    private final JTable tabla = new JTable(modeloTabla);

    private final JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JButton bBuscar = new JButton("Buscar");
    private final JButton bCambiar = new JButton("Cambiar estado de la seleccionada");

    public ConsultaOrdenesFrame() {
        super("Consulta de ordenes", true, true, true, true);
        setSize(700, 420);
        setLayout(new BorderLayout());

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtro.add(new JLabel("Estado:"));
        filtro.add(cmbEstado);
        norte.add(filtro);

        botones.add(bBuscar); botones.add(bCambiar);
        norte.add(botones);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        bBuscar.addActionListener(e -> buscar());
        bCambiar.addActionListener(e -> cambiarEstado());
        buscar();
    }

    private void buscar() {
        try {
            modeloTabla.setRowCount(0);
            for (Object[] fila : dao.listarPorEstado((EstadoOrden) cmbEstado.getSelectedItem())) {
                modeloTabla.addRow(fila);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar: " + ex.getMessage());
        }
    }

    /** Cambia el estado de la orden seleccionada (elige el nuevo en un combo). */
    private void cambiarEstado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una orden de la tabla.");
            return;
        }
        int numero = (int) modeloTabla.getValueAt(fila, 0);
        EstadoOrden nuevo = (EstadoOrden) javax.swing.JOptionPane.showInputDialog(
                this, "Nuevo estado para la orden " + numero + ":",
                "Cambiar estado", JOptionPane.QUESTION_MESSAGE,
                null, EstadoOrden.values(), EstadoOrden.PENDIENTE);
        if (nuevo == null) return;
        try {
            dao.cambiarEstado(numero, nuevo);
            buscar();
            JOptionPane.showMessageDialog(this, "Estado actualizado.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
