package controlador;

import modelo.Usuario;

public class Sesion {

    private static Usuario usuarioActual;

    private Sesion() {
    }

    public static void iniciar(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static void cerrar() {
        usuarioActual = null;
    }

    public static boolean haySesion() {
        return usuarioActual != null;
    }

    public static Usuario getUsuario() {
        return usuarioActual;
    }
}