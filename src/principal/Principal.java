package principal;

import controlador.Sesion;
import controlador.TransaccionController;
import controlador.UsuarioController;
import dao.CategoriaDAO;
import java.time.LocalDate;
import modelo.Categoria;
import modelo.Gasto;
import modelo.Transaccion;

public class Principal {
    public static void main(String[] args) {
        UsuarioController usuarios = new UsuarioController();

        System.out.println(usuarios.registrar("Ana", "ana@lukax.com", "clave1234", "clave1234"));
        System.out.println("Login con clave mala: " + usuarios.iniciarSesion("ana@lukax.com", "otra"));
        System.out.println("Login correcto: " + usuarios.iniciarSesion("ana@lukax.com", "clave1234"));

        TransaccionController movimientos = new TransaccionController();
        Categoria comida = new CategoriaDAO()
                .listarPorTipo(Sesion.getUsuario().getId(), "GASTO").get(0);

        System.out.println(movimientos.registrar(new Gasto(0, 0, comida, 12000, LocalDate.now(), "Almuerzo")));
        System.out.println(movimientos.registrar(new Gasto(0, 0, comida, -5, LocalDate.now(), "Monto malo")));

        for (Transaccion t : movimientos.listar()) {
            System.out.println(t.describir() + " | " + t.getFecha());
        }
        System.out.println("Saldo: " + movimientos.obtenerSaldo());
    }
}
