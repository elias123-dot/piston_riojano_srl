package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dao.MecanicoDAO;
import dao.OrdenDAO;
import dao.RepuestoDAO;
import dao.VehiculoDAO;
import modelo.Mecanico;
import modelo.OrdenTrabajo;
import modelo.Repuesto;
import modelo.StockInsuficienteException;
import modelo.Vehiculo;

/**
 * RF4 - Nueva orden de trabajo: vehiculo + mecanico + descripcion + horas
 * + detalle de repuestos con subtotales y total. Al guardar se ejecuta
 * UNA transaccion en OrdenDAO (orden + detalle + descuento de stock).
 */
public class OrdenFrame extends JInternalFrame {

    private final OrdenDAO dao = new OrdenDAO();
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final MecanicoDAO mecanicoDAO = new MecanicoDAO();
    private final RepuestoDAO repuestoDAO = new RepuestoDAO();

    private final JComboBox<Vehiculo> cmbVehiculo = new JComboBox<>();
    private final JComboBox<Mecanico> cmbMecanico = new JComboBox<>();
    private final JComboBox<Repuesto> cmbRepuesto = new JComboBox<>();
    private final JTextField txtDescripcion = new JTextField(20);
    private final JTextField txtHoras = new JTextField(4);
    private final JTextField txtCantidad = new JTextField(3);

    private final OrdenTrabajo orden = new OrdenTrabajo();   // orden "en construccion"
    private final DefaultTableModel modeloDetalle = new DefaultTableModel(
            new Object[]{"Repuesto", "Cant.", "Precio aplicado", "Subtotal"}, 0);
    private final JTable tablaDetalle = new JTable(modeloDetalle);
    private final JLabel lblTotal = new JLabel("Total: $0.00");

    private final JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JButton bAgregarRep = new JButton("Agregar repuesto");
    private final JButton bGuardar = new JButton("Guardar orden");

    public OrdenFrame() {
        super("Nueva orden de trabajo", true, true, true, true);
        setSize(820, 560);
        setLayout(new BorderLayout());

        // formulario superior
        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("Vehiculo:"));    form.add(cmbVehiculo);
        form.add(new JLabel("Mecanico:"));    form.add(cmbMecanico);
        form.add(new JLabel("Descripcion:")); form.add(txtDescripcion);
        form.add(new JLabel("Horas:"));       form.add(txtHoras);
        form.add(new JLabel("Repuesto:"));    form.add(cmbRepuesto);
        form.add(new JLabel("Cantidad:"));    form.add(txtCantidad);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(form);

        botones.add(bAgregarRep); botones.add(bGuardar);
        norte.add(botones);
        add(norte, BorderLayout.NORTH);

        // detalle y total
        add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);
        JPanel sur = new JPanel(new FlowLayout());
        sur.add(lblTotal);
        add(sur, BorderLayout.SOUTH);

        cargarCombos();
        bAgregarRep.addActionListener(e -> agregarRepuesto());
        bGuardar.addActionListener(e -> guardar());
    }

    private void cargarCombos() {
        try {
            for (Vehiculo v : vehiculoDAO.listarTodos()) cmbVehiculo.addItem(v);
            for (Mecanico m : mecanicoDAO.listar()) cmbMecanico.addItem(m);
            for (Repuesto r : repuestoDAO.listar()) cmbRepuesto.addItem(r);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar combos: " + ex.getMessage());
        }
    }

    /**
     * [ETIQUETA: EXCEPCION PROPIA EN ACCION] Agrega el repuesto a la orden
     * "en construccion". Si el stock no alcanza, OrdenTrabajo.agregarRepuesto
     * lanza StockInsuficienteException y aca se informa con JOptionPane.
     */
    private void agregarRepuesto() {
        Repuesto r = (Repuesto) cmbRepuesto.getSelectedItem();
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            orden.agregarRepuesto(r, cantidad);
            modeloDetalle.addRow(new Object[]{
                r.getDescripcion(), cantidad, r.getPrecioUnitario(),
                cantidad * r.getPrecioUnitario()});
            lblTotal.setText(String.format("Total: $%.2f", orden.total()));
            txtCantidad.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un numero.",
                    "Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (StockInsuficienteException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo agregar", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void guardar() {
        Vehiculo v = (Vehiculo) cmbVehiculo.getSelectedItem();
        Mecanico m = (Mecanico) cmbMecanico.getSelectedItem();
        if (v == null || m == null) {
            JOptionPane.showMessageDialog(this, "Debe elegir vehiculo y mecanico.");
            return;
        }
        try {
            double horas = Double.parseDouble(txtHoras.getText().trim());
            if (txtDescripcion.getText().trim().isEmpty() || horas < 0) {
                throw new NumberFormatException();
            }
            orden.setVehiculo(v);
            orden.setMecanico(m);
            orden.setDescripcion(txtDescripcion.getText().trim());
            orden.setHoras(horas);

            // guarda orden + detalle + descuento de stock en una sola transaccion
            dao.insertar(orden);
            JOptionPane.showMessageDialog(this,
                String.format("Orden %d guardada. Total: $%.2f", orden.getNumero(), orden.total()));
            orden.getDetalles().clear();          // lista para la proxima
            modeloDetalle.setRowCount(0);
            lblTotal.setText("Total: $0.00");
            txtDescripcion.setText("");
            txtHoras.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Horas debe ser un numero (con punto decimal si tiene decimales).",
                "Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (StockInsuficienteException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }
}
