package modelo;

/** Repuesto con codigo alfanumerico, precio unitario actual y stock. */
public class Repuesto {

    private String codigo;
    private String descripcion;
    private double precioUnitario;
    private int stock;

    public Repuesto(String codigo, String descripcion, double precioUnitario, int stock) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.stock = stock;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        // asi se muestra en el JComboBox de repuestos
        return codigo + " - " + descripcion + " ($" + precioUnitario + ") stock: " + stock;
    }
}
