package modelo;

/**
 * [ETIQUETA: SUBCLASE - POLIMORFISMO] Auto: coeficiente 1,00 (nafta o GNC)
 * y 1,20 (diesel). Redefine coeficiente() de la clase abstracta Vehiculo.
 */
public class Auto extends Vehiculo {

    private int cantidadPuertas;
    private String combustible;   // NAFTA, DIESEL o GNC

    public Auto(String patente, String marca, String modelo, int año,
                int cantidadPuertas, String combustible) {
        super(patente, marca, modelo, año);
        this.cantidadPuertas = cantidadPuertas;
        this.combustible = combustible;
    }

    @Override
    public double coeficiente() {
        // diesel paga 20% mas de maño de obra
        return "DIESEL".equals(combustible) ? 1.20 : 1.00;
    }

    @Override
    public String getTipo() { return "AUTO"; }

    public int getCantidadPuertas() { return cantidadPuertas; }
    public void setCantidadPuertas(int cantidadPuertas) { this.cantidadPuertas = cantidadPuertas; }

    public String getCombustible() { return combustible; }
    public void setCombustible(String combustible) { this.combustible = combustible; }
}
