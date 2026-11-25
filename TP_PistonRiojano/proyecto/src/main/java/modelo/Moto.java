package modelo;

/**
 * [ETIQUETA: SUBCLASE - POLIMORFISMO] Moto: coeficiente 0,80 (hasta 250cc)
 * y 1,00 (mas de 250cc). Redefine coeficiente().
 */
public class Moto extends Vehiculo {

    private int cilindrada;   // cc del motor

    public Moto(String patente, String marca, String modelo, int año, int cilindrada) {
        super(patente, marca, modelo, año);
        this.cilindrada = cilindrada;
    }

    @Override
    public double coeficiente() {
        return cilindrada <= 250 ? 0.80 : 1.00;
    }

    @Override
    public String getTipo() { return "MOTO"; }

    public int getCilindrada() { return cilindrada; }
    public void setCilindrada(int cilindrada) { this.cilindrada = cilindrada; }
}
