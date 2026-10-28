package modelo;

public class Usuario {

    private int id;
    private String nombre;
    private String correo;
    private String hashContrasena;   // nunca se guarda la contraseña en texto plano

    public Usuario() {
    }

    public Usuario(int id, String nombre, String correo, String hashContrasena) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.hashContrasena = hashContrasena;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getHashContrasena() { return hashContrasena; }
    public void setHashContrasena(String hashContrasena) { this.hashContrasena = hashContrasena; }
}