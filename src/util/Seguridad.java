package util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Protege las contraseñas con PBKDF2 (viene incluido en Java, no necesita
 * librerías externas). Se guarda: PBKDF2$iteraciones$sal$hash
 */
public class Seguridad {

    private static final int ITERACIONES = 120000;
    private static final int BYTES_SAL = 16;
    private static final int BYTES_HASH = 32;
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";

    private Seguridad() {
    }

    public static String hashear(String contrasena) {
        byte[] sal = new byte[BYTES_SAL];
        new SecureRandom().nextBytes(sal);
        byte[] hash = calcular(contrasena, sal, ITERACIONES);
        return "PBKDF2$" + ITERACIONES + "$"
                + Base64.getEncoder().encodeToString(sal) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String contrasena, String almacenado) {
        if (contrasena == null || almacenado == null) {
            return false;
        }
        try {
            String[] partes = almacenado.split("\\$");
            if (partes.length != 4 || !partes[0].equals("PBKDF2")) {
                return false;
            }
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] guardado = Base64.getDecoder().decode(partes[3]);
            byte[] ingresado = calcular(contrasena, sal, iteraciones);
            return MessageDigest.isEqual(guardado, ingresado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] calcular(String contrasena, byte[] sal, int iteraciones) {
        PBEKeySpec especificacion =
                new PBEKeySpec(contrasena.toCharArray(), sal, iteraciones, BYTES_HASH * 8);
        try {
            SecretKeyFactory fabrica = SecretKeyFactory.getInstance(ALGORITMO);
            return fabrica.generateSecret(especificacion).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo procesar la contraseña.", e);
        } finally {
            especificacion.clearPassword();
        }
    }
}
