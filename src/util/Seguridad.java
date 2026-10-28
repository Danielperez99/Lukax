package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Seguridad {

    private Seguridad() {
    }

    // Devuelve "sal:hash". La sal viaja dentro del mismo texto.
    public static String hashear(String contrasena) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return Base64.getEncoder().encodeToString(sal) + ":" + calcular(contrasena, sal);
    }

    public static boolean verificar(String contrasena, String almacenado) {
        if (almacenado == null || !almacenado.contains(":")) {
            return false;
        }
        try {
            String[] partes = almacenado.split(":");
            byte[] sal = Base64.getDecoder().decode(partes[0]);
            return calcular(contrasena, sal).equals(partes[1]);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static String calcular(String contrasena, byte[] sal) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(sal);
            byte[] hash = md.digest(contrasena.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible.", e);
        }
    }
}