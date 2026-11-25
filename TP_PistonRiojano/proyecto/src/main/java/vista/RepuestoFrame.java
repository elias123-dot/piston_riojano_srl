package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import dao.RepuestoDAO;
import modelo.Repuesto;

/**
 * RF3 - Listado de repuestos con stock, y actualizacion de precio y stock.
 */
public class RepuestoFrame extends JInternalFrame {

    private final RepuestoDAO dao = new RepuestoDAO();
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Codigo", "Descripcion", "Precio", "Stock"}, 0);
    private final JTable tabla = new JTable(modeloTabla);

    private final JTextField txtPrecio = new JTextField(8);
    private final JTextField txtStock = new JTextField(4);

    private final JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JButton bActualizar = new JButton("Actualizar seleccionado");
    private final JButton bRefrescar = new JButton("Listar");

    public RepuestoFrame() {
        super("Repuestos", true, true, true, true);
        setSize(640, 420);
        setLayout(new BorderLayout());

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtro.add(new JLabel("Nuevo precio:"));  filtro.add(txtPrecio);
        filtro.add(new JLabel("Nuevo stock:"));   filtro.add(txtStock);
        norte.add(filtro);

        botones.add(bActualizar); botones.add(bRefrescar);
        norte.add(botones);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        bActualizar.addActionListener(e -> actualizar());
        bRefrescar.addActionListener(e -> listar());
        listar();
    }

    private void listar() {
        try {
            modeloTabla.setRowCount(0);
            for (Repuesto r : dao.listar()) {
                modeloTabla.addRow(new Object[]{
                    r.getCodigo(), r.getDescripcion(),
                    r.getPrecioUnitario(), r.getStock()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al listar: " + ex.getMessage());
        }
    }

    /** Actualiza precio y stock del repuesto seleccionado en la tabla. */
    private void actualizar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un repuesto de la tabla.");
            return;
        }
        String codigo = (String) modeloTabla.getValueAt(fila, 0);
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim().replace(',', '.'));
            int stock = Integer.parseInt(txtStock.getText().trim());
            if (precio < 0 || stock < 0) throw new NumberFormatException();
            dao.actualizar(codigo, precio, stock);
            listar();
            JOptionPane.showMessageDialog(this, "Repuesto actualizado.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Precio y stock deben ser numeros validos (>= 0).",
                "Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
