package model;

public class Libro implements Comparable<Libro> {

    private String nombre;
    private String autor;
    private int cantidad;
    private double precio;
    private int id;

    public Libro(String nombre, String autor, int cantidad, double precio, int id) {
        this.nombre = nombre;
        this.autor = autor;
        this.cantidad = cantidad;
        this.precio = precio;
        this.id = id;
    }

    public String getNombre() { return nombre   ;}
    public String getAutor()  { return autor    ;}
    public int getCantidad()  { return cantidad ;}
    public double getPrecio() { return precio   ;}
    public int getId()        { return id       ;}


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return autor + " " + nombre + " " + precio + " " + cantidad;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
        result = prime * result + ((autor == null) ? 0 : autor.hashCode());
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Libro other = (Libro) obj;
        if (nombre == null) {
            if (other.nombre != null)
                return false;
        } else if (!nombre.equals(other.nombre))
            return false;
        if (autor == null) {
            if (other.autor != null)
                return false;
        } else if (!autor.equals(other.autor))
            return false;
        if (id != other.id)
            return false;
        return true;
    }

    public int compareTo(Libro l) {
        return this.id - l.id;
    }
}
