package modelo;

import java.time.LocalDate;

public class Gasto extends Transaccion {

    public Gasto() {
        super();
    }

    public Gasto(int id, int usuarioId, Categoria categoria,
                 double monto, LocalDate fecha, String descripcion) {
        super(id, usuarioId, categoria, monto, fecha, descripcion);
    }

    @Override
    public String getTipo() {
        return "GASTO";
    }

    // Un gasto RESTA del saldo.
    @Override
    public double obtenerEfectoSobreSaldo() {
        return -getMonto();
    }

    @Override
    public String validar() {
        String error = super.validar();
        if (error != null) {
            return error;
        }
        if (!getCategoria().getTipo().equals("GASTO")) {
            return "La categoría debe ser de tipo gasto.";
        }
        return null;
    }
}