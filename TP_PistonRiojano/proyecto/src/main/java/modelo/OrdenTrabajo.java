package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * [ETIQUETA: CLASE CENTRAL] Orden de trabajo del taller.
 * Contiene la logica de negocio: agregar repuestos (valida stock) y
 * calcular el total (maño de obra x coeficiente del vehiculo + repuestos).
 */
public class OrdenTrabajo {

    private int numero;
    private LocalDate fechaIngreso;
    private String descripcion;
    private EstadoOrden estado;
    private double horas;

    private Vehiculo vehiculo;      // la orden es de un unico vehiculo
    private Mecanico mecanico;      // un unico mecanico responsable
    private List<DetalleRepuesto> detalles;  // repuestos usados

    public OrdenTrabajo() {
        this.detalles = new ArrayList<>();
        this.estado = EstadoOrden.PENDIENTE;
        this.fechaIngreso = LocalDate.now();
    }

    /**
     * [ETIQUETA: METODO DIFICIL - VALIDACION DE STOCK]
     * Agrega un repuesto a la orden:
     *  1) verifica que el stock alcance para la cantidad pedida,
     *  2) si no alcanza lanza la excepcion propia StockInsuficienteException,
     *  3) si alcanza, registra el detalle con el PRECIO APLICADO (el precio
     *     vigente al momento de usarlo, no el precio actual que puede cambiar).
     * El descuento de stock real se hace en la base de datos (DAO, transaccion).
     */
    public void agregarRepuesto(Repuesto repuesto, int cantidad) throws StockInsuficienteException {
        if (repuesto == null) {
            throw new IllegalArgumentException("Repuesto inexistente.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        if (cantidad > repuesto.getStock()) {
            throw new StockInsuficienteException(
                "Stock insuficiente de '" + repuesto.getDescripcion()
                + "': hay " + repuesto.getStock() + " y se piden " + cantidad + ".");
        }
        // no repetir el mismo repuesto en la misma orden
        for (DetalleRepuesto d : detalles) {
            if (d.getRepuesto().getCodigo().equals(repuesto.getCodigo())) {
                throw new IllegalArgumentException(
                    "El repuesto " + repuesto.getCodigo() + " ya fue agregado a esta orden.");
            }
        }
        detalles.add(new DetalleRepuesto(repuesto, cantidad, repuesto.getPrecioUnitario()));
    }

    /**
     * [ETIQUETA: METODO DIFICIL - TOTAL CON POLIMORFISMO]
     * Total = horas x valor hora del mecanico x coeficiente del vehiculo + suma de repuestos.
     * El coeficiente NO se calcula aca: cada tipo de vehiculo (Auto/Moto)
     * "sabe" calcular el suyo con su metodo coeficiente() redefinido.
     * Eso es polimorfismo y evita usar instanceof.
     */
    public double total() {
        double mañoDeObra = horas * mecanico.getValorHora() * vehiculo.coeficiente();
        double repuestos = 0;
        for (DetalleRepuesto d : detalles) {
            repuestos += d.subtotal();
        }
        return mañoDeObra + repuestos;
    }

    // ---------- getters y setters (encapsulamiento) ----------
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }

    public double getHoras() { return horas; }
    public void setHoras(double horas) { this.horas = horas; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

    public Mecanico getMecanico() { return mecanico; }
    public void setMecanico(Mecanico mecanico) { this.mecanico = mecanico; }

    public List<DetalleRepuesto> getDetalles() { return detalles; }
}
