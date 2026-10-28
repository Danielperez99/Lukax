package controlador;

import dao.TransaccionDAO;
import java.util.ArrayList;
import java.util.List;
import modelo.Transaccion;

public class TransaccionController {

    private final TransaccionDAO transaccionDAO;

    public TransaccionController() {
        transaccionDAO = new TransaccionDAO();
    }

    // RF-03 y RF-04: sirve para Ingreso y Gasto (polimorfismo).
    public String registrar(Transaccion t) {
        if (!Sesion.haySesion()) {
            return "Debe iniciar sesión.";
        }
        t.setUsuarioId(Sesion.getUsuario().getId());
        String error = t.validar();
        if (error != null) {
            return error;
        }
        String nombre = t.getTipo().equals("INGRESO") ? "Ingreso" : "Gasto";
        return transaccionDAO.registrar(t)
                ? nombre + " registrado correctamente."
                : "No fue posible registrar el movimiento.";
    }

    public List<Transaccion> listar() {
        if (!Sesion.haySesion()) {
            return new ArrayList<>();
        }
        return transaccionDAO.listarPorUsuario(Sesion.getUsuario().getId());
    }

    // RF-05
    public String eliminar(int id) {
        if (!Sesion.haySesion()) {
            return "Debe iniciar sesión.";
        }
        return transaccionDAO.eliminar(id, Sesion.getUsuario().getId())
                ? "Movimiento eliminado correctamente."
                : "No fue posible eliminar el movimiento.";
    }

    public double obtenerSaldo() {
        if (!Sesion.haySesion()) {
            return 0;
        }
        return transaccionDAO.calcularSaldo(Sesion.getUsuario().getId());
    }
}