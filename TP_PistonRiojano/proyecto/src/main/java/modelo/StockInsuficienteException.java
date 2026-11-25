package modelo;

/**
 * [ETIQUETA: EXCEPCION PROPIA] El enunciado exige esta excepcion con este nombre exacto.
 * Se lanza cuando se quiere agregar un repuesto y el stock no alcanza.
 * Hereda de Exception (excepcion chequeada) para obligar a manejarla.
 */
public class StockInsuficienteException extends Exception {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
