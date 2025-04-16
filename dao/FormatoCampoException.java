package dao;

/**
 * Lanzada cuando se intenta convertir un string a un tipo
 * numerico incorrecto desde un campo de texto.
 */
public class FormatoCampoException extends Exception {
    public FormatoCampoException(String message) {
        super(message);
    }
}
