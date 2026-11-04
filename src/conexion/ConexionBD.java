package conexion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton de la conexión a MySQL.
 * Los datos de conexión se leen del archivo config.properties (que está en la
 * carpeta del proyecto y NO se sube a GitHub). Así cada integrante usa su propia
 * contraseña sin tocar el código.
 */
public class ConexionBD {

    private static final String ARCHIVO_CONFIG = "config.properties";

    private static ConexionBD instancia;

    private final String url;
    private final String usuario;
    private final String clave;
    private Connection conexion;

    private ConexionBD() {
        Properties p = new Properties();
        // Valores por defecto, por si falta el archivo.
        p.setProperty("db.host", "localhost");
        p.setProperty("db.port", "3306");
        p.setProperty("db.name", "db_lukax");
        p.setProperty("db.user", "root");
        p.setProperty("db.password", "");

        File archivo = new File(ARCHIVO_CONFIG);
        if (archivo.exists()) {
            try (InputStreamReader lector = new InputStreamReader(
                    new FileInputStream(archivo), StandardCharsets.UTF_8)) {
                p.load(lector);
            } catch (IOException e) {
                System.err.println("No se pudo leer " + ARCHIVO_CONFIG + ": " + e.getMessage());
            }
        } else {
            System.err.println("No se encontró " + ARCHIVO_CONFIG
                    + " en " + archivo.getAbsolutePath() + ". Se usan valores por defecto.");
        }

        url = "jdbc:mysql://" + p.getProperty("db.host") + ":" + p.getProperty("db.port")
                + "/" + p.getProperty("db.name")
                + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        usuario = p.getProperty("db.user");
        clave = p.getProperty("db.password");
        conectar();
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    private void conectar() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(url, usuario, clave);
            System.out.println("Conexion exitosa con MySQL.");
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontro el driver de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar con MySQL: " + e.getMessage());
        }
    }

    public Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conectar();
        }
        if (conexion == null) {
            throw new SQLException("No fue posible establecer conexión con MySQL.");
        }
        return conexion;
    }

    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
