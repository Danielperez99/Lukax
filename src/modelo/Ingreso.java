package modelo;

import java.time.LocalDate;

public class Ingreso extends Transaccion {

    public Ingreso() {
        super();
    }

    public Ingreso(int id, int usuarioId, Categoria categoria,
                   double monto, LocalDate fecha, String descripcion) {
        super(id, usuarioId, categoria, monto, fecha, descripcion);
    }

    @Override
    public String getTipo() {
        return "INGRESO";
    }

    // Un ingreso SUMA al saldo.
    @Override
    public double obtenerEfectoSobreSaldo() {
        return getMonto();
    }

    @Override
    public String validar() {
        String error = super.validar();   // primero las reglas comunes
        if (error != null) {
            return error;
        }
        if (!getCategoria().getTipo().equals("INGRESO")) {
            return "La categoría debe ser de tipo ingreso.";
        }
        return null;
    }
}