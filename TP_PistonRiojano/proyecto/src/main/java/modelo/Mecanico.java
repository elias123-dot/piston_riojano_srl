package modelo;

/** Mecanico del taller, con especialidad y valor por hora. */
public class Mecanico {

    private int legajo;
    private String nombre;
    private String apellido;
    private String especialidad;   // MOTOR, ELECTRICIDAD, FRENOS o GENERAL
    private double valorHora;

    public Mecanico(int legajo, String nombre, String apellido, String especialidad, double valorHora) {
        this.legajo = legajo;
        this.nombre = nombre;
        this.apellido = apellido;
        this.especialidad = especialidad;
        this.valorHora = valorHora;
    }

    public int getLegajo() { return legajo; }
    public void setLegajo(int legajo) { this.legajo = legajo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public double getValorHora() { return valorHora; }
    public void setValorHora(double valorHora) { this.valorHora = valorHora; }

    @Override
    public String toString() {
        // asi se muestra el mecanico en el JComboBox
        return legajo + " - " + apellido + ", " + nombre + " (" + especialidad + ")";
    }
}
