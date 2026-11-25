package modelo;

/**
 * [ETIQUETA: HERENCIA - CLASE ABSTRACTA] La enunciado pide que cada tipo de
 * vehiculo "sepa" calcular su coeficiente sin usar instanceof: eso se logra
 * con una clase abstracta y un metodo abstracto coeficiente() que Auto y Moto
 * redefinen (polimorfismo).
 */
public abstract class Vehiculo {

    private String patente;   // unica
    private String marca;
    private String modelo;
    private int año;

    private Cliente cliente;  // cada vehiculo pertenece a UN cliente

    protected Vehiculo(String patente, String marca, String modelo, int año) {
        this.patente = patente;
        this.marca = marca;
        this.modelo = modelo;
        this.año = año;
    }

    /** Coeficiente para el calculo del total (cada subclase lo define). */
    public abstract double coeficiente();

    /** Tipo para la base de datos: AUTO o MOTO (discriminador). */
    public abstract String getTipo();

    // ---------- getters y setters ----------
    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAño() { return año; }
    public void setAño(int año) { this.año = año; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}
