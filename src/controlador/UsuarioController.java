package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;
import util.Seguridad;

public class UsuarioController {

    private static final int LONGITUD_MINIMA = 8;   // a confirmar con el grupo

    private final UsuarioDAO usuarioDAO;

    public UsuarioController() {
        usuarioDAO = new UsuarioDAO();
    }

    // RF-01
    public String registrar(String nombre, String correo, String contrasena, String confirmacion) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "El nombre es obligatorio.";
        }
        if (correo == null || !correo.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "Ingrese un correo válido.";
        }
        if (contrasena == null || contrasena.length() < LONGITUD_MINIMA) {
            return "La contraseña debe tener mínimo " + LONGITUD_MINIMA + " caracteres.";
        }
        if (!contrasena.equals(confirmacion)) {
            return "Las contraseñas no coinciden.";
        }
        String correoLimpio = correo.trim().toLowerCase();
        if (usuarioDAO.buscarPorCorreo(correoLimpio) != null) {
            return "Ese correo ya está registrado.";
        }
        Usuario usuario = new Usuario(0, nombre.trim(), correoLimpio, Seguridad.hashear(contrasena));
        return usuarioDAO.registrar(usuario)
                ? "Usuario registrado correctamente."
                : "No fue posible registrar el usuario.";
    }

    // RF-02. Devuelve null si inició sesión bien, o el mensaje de error.
    public String iniciarSesion(String correo, String contrasena) {
        if (correo == null || correo.trim().isEmpty() || contrasena == null || contrasena.isEmpty()) {
            return "Ingrese correo y contraseña.";
        }
        Usuario usuario = usuarioDAO.buscarPorCorreo(correo.trim().toLowerCase());
        // Mensaje genérico: no dice si falló el correo o la contraseña.
        if (usuario == null || !Seguridad.verificar(contrasena, usuario.getHashContrasena())) {
            return "Correo o contraseña incorrectos.";
        }
        Sesion.iniciar(usuario);
        return null;
    }
}