package modelo;

import java.time.LocalDate;

public abstract class Transaccion {

    private int id;
    private int usuarioId;
    private Categoria categoria;
    private double monto;
    private LocalDate fecha;
    private String descripcion;

    public Transaccion() {
    }

    public Transaccion(int id, int usuarioId, Categoria categoria,
                       double monto, LocalDate fecha, String descripcion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.categoria = categoria;
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

    // Métodos abstractos: cada hija decide cómo los implementa.
    public abstract String getTipo();
    public abstract double obtenerEfectoSobreSaldo();

    // Validación común. Devuelve null si todo está bien, o el mensaje del error.
    public String validar() {
        if (monto <= 0) {
            return "El monto debe ser mayor que cero.";
        }
        if (fecha == null) {
            return "La fecha es obligatoria.";
        }
        if (categoria == null) {
            return "Seleccione una categoría.";
        }
        if (descripcion != null && descripcion.length() > 200) {
            return "La descripción no puede superar los 200 caracteres.";
        }
        return null;
    }

    // Método concreto que usa los abstractos (polimorfismo en acción).
    public String describir() {
        return getTipo() + ": " + categoria.getNombre() + " - $" + monto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}