package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
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
import dao.ClienteDAO;
import modelo.Cliente;

/**
 * RF1 - ABM de clientes con JTable. No se puede dar de baja un cliente que
 * tenga vehiculos (regla RF1).
 */
public class ClienteFrame extends JInternalFrame {

    private final ClienteDAO dao = new ClienteDAO();
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"DNI", "Nombre", "Apellido", "Telefono", "Email"}, 0);
    private final JTable tabla = new JTable(modeloTabla);

    private final JTextField txtDni = new JTextField(8);
    private final JTextField txtNombre = new JTextField(10);
    private final JTextField txtApellido = new JTextField(10);
    private final JTextField txtTelefono = new JTextField(10);
    private final JTextField txtEmail = new JTextField(10);

    private final JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JButton bAgregar = new JButton("Agregar");
    private final JButton bModificar = new JButton("Modificar seleccionado");
    private final JButton bEliminar = new JButton("Eliminar seleccionado");
    private final JButton bRefrescar = new JButton("Listar");

    public ClienteFrame() {
        super("Clientes (ABM)", true, true, true, true);
        setSize(760, 480);
        setLayout(new BorderLayout());

        // formulario arriba
        JPanel form = new JPanel(new GridLayout(0, 2, 4, 4));
        form.add(new JLabel("DNI:"));
        form.add(txtDni);
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Apellido:"));
        form.add(txtApellido);
        form.add(new JLabel("Tel:"));
        form.add(txtTelefono);
        form.add(new JLabel("Email:"));
        form.add(txtEmail);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(form);

        botones.add(bAgregar);
        botones.add(bModificar);
        botones.add(bEliminar);
        botones.add(bRefrescar);
        norte.add(botones);
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        bAgregar.addActionListener(e -> agregar());
        bModificar.addActionListener(e -> modificar());
        bEliminar.addActionListener(e -> eliminar());
        bRefrescar.addActionListener(e -> listar());

        listar();
    }

    /**
     * Valida los campos obligatorios y numerales (el enunciado lo exige).
     */
    private Cliente leerFormulario() {
        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String tel = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();

        if (dni.isEmpty() || !dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this,
                    "El DNI debe tener 8 digitos numericos.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (nombre.isEmpty() || apellido.isEmpty() || tel.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nombre, apellido y telefono son obligatorios.", "Validacion", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        // email opcional: si esta vacio va null
        return new Cliente(dni, nombre, apellido, tel,
                email.isEmpty() ? null : email);
    }

    private void listar() {
        try {
            modeloTabla.setRowCount(0);
            for (Cliente c : dao.listar()) {
                modeloTabla.addRow(new Object[]{
                    c.getDni(), c.getNombre(), c.getApellido(), c.getTelefono(),
                    c.getEmail() == null ? "" : c.getEmail()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al listar: " + ex.getMessage());
        }
    }

    private void agregar() {
        Cliente c = leerFormulario();
        if (c == null) {
            return;
        }
        try {
            dao.insertar(c);
            listar();
            JOptionPane.showMessageDialog(this, "Cliente agregado.");
        } catch (SQLException ex) {
            // la PK de DNI unico tira error si repite DNI
            String msg = ex.getMessage().contains("cliente_pkey")
                    ? "Ese DNI ya esta registrado." : ex.getMessage();
            JOptionPane.showMessageDialog(this, "Error: " + msg);
        }
    }

    private void modificar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla.");
            return;
        }
        Cliente c = leerFormulario();
        if (c == null) {
            return;
        }
        try {
            dao.modificar(c);
            listar();
            JOptionPane.showMessageDialog(this, "Cliente modificado.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    /**
     * [ETIQUETA: REGLA RF1] Antes de eliminar pregunta a la base si el cliente
     * tiene vehiculos; si tiene, NO se permite la baja.
     */
    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla.");
            return;
        }
        String dni = (String) modeloTabla.getValueAt(fila, 0);
        try {
            if (dao.tieneVehiculos(dni)) {
                JOptionPane.showMessageDialog(this,
                        "No se puede eliminar: el cliente tiene vehiculos registrados.",
                        "Regla de negocio", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirmar = JOptionPane.showConfirmDialog(this,
                    "Eliminar el cliente " + dni + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirmar == JOptionPane.YES_OPTION) {
                dao.eliminar(dni);
                listar();
                JOptionPane.showMessageDialog(this, "Cliente eliminado.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
