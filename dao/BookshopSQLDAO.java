package dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import model.Libro;

public class BookshopSQLDAO {

    private final String IP_PORT = "localhost:3306";
    private final String DB_NAME = "libros";
    private final String URL = "jdbc:mysql://" + IP_PORT + "/" + DB_NAME + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private final String USER = "pablo";
    private final String PASS = "1234";


    public Set<Libro> getLibros() throws BaseDatosException {
        Set<Libro> libros = new TreeSet<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {


            String selectQuery = "select * from libros";
            ResultSet rset = stmt.executeQuery(selectQuery);


            while (rset.next()) {
                int id = rset.getInt("id");
                String titulo = rset.getString("titulo");
                String autor = rset.getString("autor");
                double precio = rset.getDouble("precio");
                int cantidad = rset.getInt("cantidad");

                libros.add(new Libro(titulo, autor, cantidad, precio, id));
            }
        } catch (SQLException e) {
            throw new BaseDatosException("Error en la base de datos al obtener los libros");
        }
        return libros;
    }


    /**
     * Agrega un nuevo libro a la base de datos. El objeto Libro pasado como parametro
     * contiene el titulo, autor, precio,cantidad e id del nuevo libro.
     *
     * @param libro nuevo libro a agregar a la base de datos
     * @throws BaseDatosException si hubo un error al agregar el nuevo libro
     */
    public void aniadirLibro(Libro libro) throws BaseDatosException {
        try (   Connection conn = DriverManager.getConnection(URL, USER, PASS);
        Statement stmt = conn.createStatement();) {


            String strInsert = "INSERT INTO libros(titulo, autor, precio, cantidad, id) VALUES (";
            strInsert += "'" + libro.getNombre() + "',";
            strInsert += "'" + libro.getAutor() + "',";
            strInsert += libro.getPrecio() + ",";
            strInsert += libro.getCantidad() + ",";
            strInsert += libro.getId() + ")";
            stmt.executeUpdate(strInsert);


        } catch (SQLException e) {
            String error = e.getMessage();
            if (error.contains("Duplicate entry"))
                throw new BaseDatosException("El libro con id " + libro.getId() + " ya existe en la base de datos");
            else
                throw new BaseDatosException("Error en la base de datos al agregar libro");
        }
    }


    /**
     * Modifica los datos de un libro en la base de datos. El objeto Libro pasado
     * como parametro contiene el id del libro a modificar y el array de strings
     * contiene los cambios a aplicar.
     *
     * @param libro libro a modificar
     * @param cambios array con los cambios a aplicar. Los indices del array
     *                corresponden a los campos a modificar:
     *                0: autor
     *                1: titulo
     *                2: precio maximo
     *                3: cantidad minima
     *                4: id
     * @throws BaseDatosException si hubo un error en la base de datos al
     *                             intentar modificar el libro
     * @throws FormatoCampoException si el formato de alguno de los campos a
     *                               modificar es incorrecto
     */
    public void modificarDatosLibro(Libro libro, String[] cambios) throws BaseDatosException, FormatoCampoException {
        try (   Connection conn = DriverManager.getConnection(URL, USER, PASS);
        Statement stmt = conn.createStatement();) {

            String autorCambio = cambios[0];
            String tituloCambio = cambios[1];
            String precioCambio = cambios[2];
            String cantidadCambio = cambios[3];
            String idCambio = cambios[4];

            if (!Arrays.deepToString(cambios).equals("[, , , , ]")) {
                if (!precioCambio.isEmpty() && Double.parseDouble(precioCambio) <= 0)
                    throw new FormatoCampoException("El precio maximo debe ser mayor que 0");
                if (!cantidadCambio.isEmpty() && Integer.parseInt(cantidadCambio) < 0)
                    throw new FormatoCampoException("La cantidad debe ser mayor o igual que 0");
                if (!idCambio.isEmpty() && Integer.parseInt(idCambio) == 0);

                String strUpdate = "UPDATE libros SET autor=";
                strUpdate += "'" + (autorCambio.isEmpty() ? libro.getAutor() : autorCambio) + "',";
                strUpdate += "titulo=";
                strUpdate += "'" + (tituloCambio.isEmpty() ? libro.getNombre() : tituloCambio) + "',";
                strUpdate += "precio=";
                strUpdate += (precioCambio.isEmpty() ? libro.getPrecio() : precioCambio) + ",";
                strUpdate += "cantidad=";
                strUpdate += (cantidadCambio.isEmpty() ? libro.getCantidad() : cantidadCambio) + ",";
                strUpdate += "id=";
                strUpdate += (idCambio.isEmpty() ? libro.getId() : idCambio);
                strUpdate += " WHERE id=" + libro.getId();

                stmt.executeUpdate(strUpdate);
            } else {
                throw new FormatoCampoException("No se han introducido cambios");
            }
        } catch (SQLException e) {
            throw new BaseDatosException("Error en la base de datos al modificar el precio del libro");
        } catch (NumberFormatException e) {
            throw new FormatoCampoException("Error en el formato de alguno de los campos numericos");
        }
    }



    /**
     * Elimina un libro de la base de datos.
     * @param libro El libro a eliminar
     * @throws BaseDatosException Si hubo un error en la base de datos
     */
    public void eliminarLibro(Libro libro) throws BaseDatosException {
        String deleteStatement = "DELETE FROM libros WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
                PreparedStatement pstmtDelete = conn.prepareStatement(deleteStatement, 1)) {
            pstmtDelete.setInt(1, libro.getId());
            pstmtDelete.executeUpdate();
        } catch (SQLException e) {
            throw new BaseDatosException("Error en la base de datos al eliminar libro");
        }
    }


    /**
     * Filtra los libros en la base de datos segun los argumentos pasados
     * @param filtros Una lista de cadenas que contienen los filtros a aplicar:
     *      0: titulo
     *      1: precio maximo
     *      2: autor
     *      3: cantidad minima
     *      4: id
     * @return Un conjunto de libros que cumplen con los filtros
     * @throws FormatoCampoException
     * @throws BaseDatosException
     */
    public Set<Libro> filtrarLibros(String[] filtros) throws FormatoCampoException, BaseDatosException {
        String selectQuery = "";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
            Statement stmtQuery = conn.createStatement();) {

            String autorFiltro = filtros[0];
            String tituloFiltro = filtros[1];
            String precioFiltro = filtros[2];
            String cantidadFiltro = filtros[3];
            String idFiltro = filtros[4];

            // construir la sentencia SQL con los filtros
            selectQuery = "";
            if (!tituloFiltro.isEmpty()) selectQuery = addTituloFiltro(tituloFiltro);
            if (!autorFiltro.isEmpty()) selectQuery = addAutorFiltro(selectQuery, autorFiltro);
            if (!precioFiltro.isEmpty()) selectQuery = addPrecioFiltro(selectQuery, precioFiltro);
            if (!cantidadFiltro.isEmpty()) selectQuery = addCantidadFiltro(selectQuery, cantidadFiltro);
            if (!idFiltro.isEmpty()) selectQuery = addIdFiltro(selectQuery, idFiltro);


            // ejecutamos la sentencia SQL y obtenemos los resultados
            ResultSet rset = stmtQuery.executeQuery("SELECT * FROM libros" + selectQuery);
            Set<Libro> libros = new TreeSet<>();
            while (rset.next()) {
                String titulo = rset.getString("titulo");
                String autor = rset.getString("autor");
                double precio = rset.getDouble("precio");
                int cantidad = rset.getInt("cantidad");
                int id = rset.getInt("id");
                libros.add(new Libro(titulo, autor, cantidad, precio, id));
            }
            return libros;
        } catch (SQLException e) {
            throw new BaseDatosException("Error en la base de datos al filtrar libros");
        } catch (NumberFormatException e) {
            throw new FormatoCampoException("Error en el formato de alguno de los campos numericos de filtrado");
        }
    }


    private String addTituloFiltro(String tituloFiltro) {
        return " WHERE LOWER(titulo) LIKE LOWER('%" + tituloFiltro + "%')";
    }

    private String addAutorFiltro(String selectQuery, String autorFiltro) {
        return addAndOrWhere(selectQuery) + "LOWER(autor) LIKE LOWER('%" + autorFiltro + "%')";
    }

    private String addPrecioFiltro(String selectQuery, String precioFiltro) {
        return addAndOrWhere(selectQuery) + "precio <= " + Double.parseDouble(precioFiltro);
    }

    private String addCantidadFiltro(String selectQuery, String cantidadFiltro) {
        return addAndOrWhere(selectQuery) + "cantidad <= " + Integer.parseInt(cantidadFiltro);
    }

    private String addIdFiltro(String selectQuery, String idFiltro) {
        return addAndOrWhere(selectQuery) + "id = " + Integer.parseInt(idFiltro);
    }

    private String addAndOrWhere(String selectQuery) {
        return selectQuery + ((selectQuery.length() > 20) ? " AND " : " WHERE ");
    }

}
