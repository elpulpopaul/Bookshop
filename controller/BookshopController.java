package controller;

import java.net.URL;
import java.util.ResourceBundle;

import dao.BaseDatosException;
import dao.BookshopSQLDAO;
import dao.FormatoCampoException;
import model.Libro;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class BookshopController implements Initializable {

    private static BookshopSQLDAO dao;

    @FXML private TableView<Libro> librosTableView;

    @FXML private TableColumn<Libro, Integer> columnaId;
    @FXML private TableColumn<Libro, String> columnaNombre;
    @FXML private TableColumn<Libro, String> columnaAutor;
    @FXML private TableColumn<Libro, Integer> columnaCantidad;
    @FXML private TableColumn<Libro, Double> columnaPrecio;

    @FXML private TitledPane panelAniadir;
    @FXML private TextField idAniadir;
    @FXML private TextField nombreAniadir;
    @FXML private TextField autorAniadir;
    @FXML private TextField cantidadAniadir;
    @FXML private TextField precioAniadir;

    @FXML private TitledPane panelFiltrar;
    @FXML private TextField idFiltro;
    @FXML private TextField nombreFiltro;
    @FXML private TextField autorFiltro;
    @FXML private TextField cantidadFiltro;
    @FXML private TextField precioMaxFiltro;

    @FXML private TitledPane panelCambio;
    @FXML private TextField idCambio;
    @FXML private TextField nombreCambio;
    @FXML private TextField autorCambio;
    @FXML private TextField cantidadCambio;
    @FXML private TextField precioCambio;

    @FXML
    void aniadirLibro(ActionEvent event) {
        try {
            String autor = autorAniadir.getText();
            String nombre = nombreAniadir.getText();
            double precio = Double.parseDouble(precioAniadir.getText());
            int cantidad = Integer.parseInt(cantidadAniadir.getText());
            int id = Integer.parseInt(idAniadir.getText());
            Libro libro = new Libro(nombre, autor, cantidad , precio, id);
            dao.aniadirLibro(libro);
            librosTableView.getItems().add(libro);
            mostrarExito("Libro añadido");
        } catch (BaseDatosException e) {
            mostrarError(e.getMessage());
        } catch (NumberFormatException e) {
            mostrarError("Error en el formato de alguno de los campos numericos");
        }
    }

    @FXML
    void buscarFiltros(ActionEvent event) {
        String autor = autorFiltro.getText();
        String nombre = nombreFiltro.getText();
        String precioMax = precioMaxFiltro.getText();
        String cantidad = cantidadFiltro.getText();
        String id = idFiltro.getText();
        String[] filtros = new String[] {autor,nombre,precioMax,cantidad,id};
        try {
            if (!precioMax.isEmpty() && Double.parseDouble(precioMax) <= 0) throw new FormatoCampoException("El precio maximo debe ser mayor que 0");
            if (!cantidad.isEmpty() && Integer.parseInt(cantidad) < 0) throw new FormatoCampoException("La cantidad debe ser mayor o igual que 0");
            librosTableView.getItems().setAll(dao.filtrarLibros(filtros));
            mostrarExito("Libros filtrados");
        } catch (BaseDatosException e) {
            mostrarError(e.getMessage());
        } catch (FormatoCampoException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    void cambiarDatos(ActionEvent event) {
        Libro seleccionado = librosTableView.getSelectionModel().getSelectedItem();
        try {
            String autor = autorCambio.getText();
            String nombre = nombreCambio.getText();
            String precio = precioCambio.getText();
            String cantidad = cantidadCambio.getText();
            String id = idCambio.getText();
            String[] cambios = new String[] {autor,nombre,precio,cantidad,id};
            if (Double.parseDouble(precio) <= 0) throw new FormatoCampoException("El precio maximo debe ser mayor que 0");
            if (Integer.parseInt(cantidad) < 0) throw new FormatoCampoException("La cantidad debe ser mayor o igual que 0");
            dao.modificarDatosLibro(seleccionado, cambios);
            librosTableView.getItems().setAll(dao.getLibros());
            librosTableView.getSelectionModel().select(seleccionado);
            mostrarExito("Libro modificado");
        } catch (FormatoCampoException e) {
            mostrarError(e.getMessage());
        } catch (BaseDatosException e) {
            mostrarError(e.getMessage());
        } catch (NumberFormatException e) {
            mostrarError("Error en el formato de alguno de los campos numericos");
        }
    }

    @FXML
    void eliminarLibro(ActionEvent event) {
        Libro seleccionado = librosTableView.getSelectionModel().getSelectedItem();
        try {
            dao.eliminarLibro(seleccionado);
            librosTableView.getItems().remove(seleccionado);
            mostrarExito("Libro eliminado");
        } catch (NullPointerException e) {
            mostrarError("No se ha seleccionado un libro");
        } catch (BaseDatosException e) {
            mostrarError(e.getMessage());
        }
    }

    private void mostrarError(String texto) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(texto);
        alerta.showAndWait();
    }

    private void mostrarExito(String texto) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Exito");
        alerta.setHeaderText(texto);
        alerta.showAndWait();
    }

    @FXML
    void verDetallesLibro(ActionEvent event) {
        try {
            Libro seleccionado = librosTableView.getSelectionModel().getSelectedItem();
            Alert alerta = new Alert(AlertType.INFORMATION);
            alerta.setTitle("Detalles del libro");
            alerta.setHeaderText("Informacion detallada del libro");
            String texto = String.format("Título: %s\nAutor: %s\nCantidad: %d\nprecio: %.2f",
                seleccionado.getNombre(), seleccionado.getAutor(), seleccionado.getCantidad(), seleccionado.getPrecio());
            alerta.setContentText(texto);
            alerta.showAndWait();
        } catch (NullPointerException e) {
            mostrarError("No se ha seleccionado un libro");
        }
    }



    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        dao = new BookshopSQLDAO();
        columnaId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        columnaNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        columnaAutor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAutor()));
        columnaCantidad.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCantidad()).asObject());
        columnaPrecio.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());
        //Libro l = new Libro("Libro1", "Autor1", 1, 10.00, 1);
        //librosTableView.getItems().add(l);
        try {
            librosTableView.getItems().setAll(dao.getLibros());
            librosTableView.getSelectionModel().selectFirst();
        } catch (BaseDatosException e) {
            mostrarError(e.getMessage());
        }
    }

}
