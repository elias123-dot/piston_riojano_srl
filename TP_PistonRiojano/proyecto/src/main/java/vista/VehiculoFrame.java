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
import dao.ClienteDAO;
import dao.VehiculoDAO;
import modelo.Auto;
import modelo.Cliente;
import modelo.Moto;
import modelo.Vehiculo;

/**
 * RF2 - Alta y listado de vehiculos de un cliente elegido en un JComboBox.
 * Segun se elija Auto o Moto se habilitan los campos propios (RF2).
 */
public class VehiculoFrame extends JInternalFrame {

    private final VehiculoDAO dao = new VehiculoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    private final JComboBox<Cliente> cmbCliente = new JComboBox<>();
    private final JComboBox<String> cmbTipo =
            new JComboBox<>(new String[]{"AUTO", "MOTO"});
    private final JTextField txtPatente = new JTextField(7);
    private final JTextField txtMarca = new JTextField(10);
    private final JTextField txtModelo = new JTextField(10);
    private final JTextField txtAño = new JTextField(4);

    // campos de AUTO
    private final JTextField txtPuertas = new JTextField(3);
    private final JComboBox<String> cmbCombustible =
            new JComboBox<>(new String[]{"NAFTA", "DIESEL", "GNC"});

    // campo de MOTO
    private final JTextField txtCilindrada = new JTextField(5);

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Patente", "Marca", "Modelo", "Año", "Tipo", "Cliente"}, 0);
    private final JTable tabla = new JTable(modeloTabla);

    private final JPanel botones = new JPanel(new GridLayout(0, 1, 4, 4));
    private final JButton bGuardar = new JButton("Guardar vehiculo");
    private final JButton bListar = new JButton("Listar todos");

    public VehiculoFrame() {
        super("Vehiculos", true, true, true, true);
        setSize(780, 520);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("Cliente:"));    form.add(cmbCliente);
        form.add(new JLabel("Tipo:"));       form.add(cmbTipo);
        form.add(new JLabel("Patente:"));    form.add(txtPatente);
        form.add(new JLabel("Marca:"));      form.add(txtMarca);
        form.add(new JLabel("Modelo:"));     form.add(txtModelo);
        form.add(new JLabel("Año:"));       form.add(txtAño);
        form.add(new JLabel("Puertas (auto):")); form.add(txtPuertas);
        form.add(new JLabel("Combustible (auto):")); form.add(cmbCombustible);
        form.add(new JLabel("Cilindrada (moto):")); form.add(txtCilindrada);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(form);

        botones.add(bGuardar); botones.add(bListar);
        norte.add(botones);
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        cargarClientes();
        habilitarCampos();   // estado inicial segun tipo

        cmbTipo.addActionListener(e -> habilitarCampos());
        bGuardar.addActionListener(e -> guardar());
        bListar.addActionListener(e -> listar());
        listar();
    }

    /** Carga los clientes en el JComboBox. */
    private void cargarClientes() {
        try {
            for (Cliente c : clienteDAO.listar()) {
                cmbCliente.addItem(c);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + ex.getMessage());
        }
    }

    /**
     * [ETIQUETA: RF2 - CAMPOS DINAMICOS] Habilita/deshabilita los campos
     * propios de cada tipo: si es AUTO, se cargan puertas y combustible y
     * se deshabilita cilindrada; si es MOTO, al reves.
     */
    private void habilitarCampos() {
        boolean esAuto = "AUTO".equals(cmbTipo.getSelectedItem());
        txtPuertas.setEnabled(esAuto);
        cmbCombustible.setEnabled(esAuto);
        txtCilindrada.setEnabled(!esAuto);
    }

    /** Valida y guarda el vehiculo (auto o moto). */
    private void guardar() {
        if (cmbCliente.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "No hay clientes cargados.");
            return;
        }
        String patente = txtPatente.getText().trim().toUpperCase();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String año = txtAño.getText().trim();

        if (patente.isEmpty() || marca.isEmpty() || modelo.isEmpty()
                || !año.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this,
                "Complete patente, marca, modelo y un año de 4 dígitos.",
                "Validacion", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cliente cliente = (Cliente) cmbCliente.getSelectedItem();
        Vehiculo v;
        try {
            if ("AUTO".equals(cmbTipo.getSelectedItem())) {
                // validar puertas numericas
                int puertas = Integer.parseInt(txtPuertas.getText().trim());
                v = new Auto(patente, marca, modelo, Integer.parseInt(año),
                        puertas, (String) cmbCombustible.getSelectedItem());
            } else {
                // validar cilindrada numerica
                int cc = Integer.parseInt(txtCilindrada.getText().trim());
                if (cc <= 0) throw new NumberFormatException();
                v = new Moto(patente, marca, modelo, Integer.parseInt(año), cc);
            }
            v.setCliente(cliente);
            dao.insertar(v);
            listar();
            JOptionPane.showMessageDialog(this, "Vehiculo guardado.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Puertas y cilindrada deben ser numeros validos.",
                "Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            String msg = ex.getMessage().contains("vehiculo_pkey")
                    ? "Esa patente ya existe." : ex.getMessage();
            JOptionPane.showMessageDialog(this, "Error: " + msg);
        }
    }

    /** Lista todos los vehiculos en la tabla. */
    private void listar() {
        try {
            modeloTabla.setRowCount(0);
            for (Vehiculo v : dao.listarTodos()) {
                modeloTabla.addRow(new Object[]{
                    v.getPatente(), v.getMarca(), v.getModelo(), v.getAño(),
                    v.getTipo(), v.getCliente().getDni()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al listar: " + ex.getMessage());
        }
    }
}
