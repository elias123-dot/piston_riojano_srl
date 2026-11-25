package vista;

import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * [ETIQUETA: VENTANA MDI] Ventana principal con JDesktopPane.
 * Los formularios (clientes, vehiculos, ordenes...) se abren como
 * JInternalFrame DENTRO de esta ventana: eso es una interfaz MDI.
 */
public class VentanaPrincipal extends JFrame {

    private final JDesktopPane escritorio = new JDesktopPane();

    public VentanaPrincipal() {
        setTitle("Taller Mecanico Pistón Riojano SRL");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(escritorio);
        armarMenu();
    }

    /** Arma el menu que abre cada formulario. */
    private void armarMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu mOperaciones = new JMenu("Operaciones");

        JMenuItem mClientes = new JMenuItem("Clientes (ABM)");
        mClientes.addActionListener(e -> abrir(new ClienteFrame()));

        JMenuItem mVehiculos = new JMenuItem("Vehiculos");
        mVehiculos.addActionListener(e -> abrir(new VehiculoFrame()));

        JMenuItem mRepuestos = new JMenuItem("Repuestos");
        mRepuestos.addActionListener(e -> abrir(new RepuestoFrame()));

        JMenuItem mOrdenes = new JMenuItem("Nueva orden de trabajo");
        mOrdenes.addActionListener(e -> abrir(new OrdenFrame()));

        JMenuItem mConsulta = new JMenuItem("Consultar ordenes");
        mConsulta.addActionListener(e -> abrir(new ConsultaOrdenesFrame()));

        mOperaciones.add(mClientes);
        mOperaciones.add(mVehiculos);
        mOperaciones.add(mRepuestos);
        mOperaciones.add(mOrdenes);
        mOperaciones.add(mConsulta);

        JMenu mSalir = new JMenu("Ayuda");
        JMenuItem mSalirItem = new JMenuItem("Salir");
        mSalirItem.addActionListener(e -> System.exit(0));
        mSalir.add(mSalirItem);

        barra.add(mOperaciones);
        barra.add(mSalir);
        setJMenuBar(barra);
    }

    /** Centra el formulario y lo agrega al escritorio MDI. */
    private void abrir(javax.swing.JInternalFrame f) {
        escritorio.add(f);
        f.setLocation(
            (escritorio.getWidth() - f.getWidth()) / 2,
            (escritorio.getHeight() - f.getHeight()) / 2);
        f.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}