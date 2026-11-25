package modelo;

/**
 * [ETIQUETA: RELACION ORDEN-REPUESTO] Cada linea del detalle de una orden.
 * Guarda el PRECIO APLICADO (el precio del repuesto al momento de usarlo),
 * porque el precio actual del repuesto cambia con el tiempo.
 */
public class DetalleRepuesto {

    private Repuesto repuesto;
    private int cantidad;
    private double precioAplicado;

    public DetalleRepuesto(Repuesto repuesto, int cantidad, double precioAplicado) {
        this.repuesto = repuesto;
        this.cantidad = cantidad;
        this.precioAplicado = precioAplicado;
    }

    /** Subtotal de la linea = cantidad x precio aplicado. */
    public double subtotal() {
        return cantidad * precioAplicado;
    }

    public Repuesto getRepuesto() { return repuesto; }
    public void setRepuesto(Repuesto repuesto) { this.repuesto = repuesto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioAplicado() { return precioAplicado; }
    public void setPrecioAplicado(double precioAplicado) { this.precioAplicado = precioAplicado; }
}
