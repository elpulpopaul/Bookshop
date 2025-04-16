package dao;

/**
 * Lanzada cuando se intenta realizar una operacion sobre la base de datos
 * y se produce un error interrumpiendo la misma.
 */
public class BaseDatosException extends Exception {
    public BaseDatosException(String message) {
        super(message);
    }
}
