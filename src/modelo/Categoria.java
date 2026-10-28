package modelo;

public class Categoria {

    private int id;
    private int usuarioId;          // 0 = categoría predefinida (sin dueño)
    private String nombre;
    private String tipo;            // "INGRESO" o "GASTO"
    private boolean esPredefinida;

    public Categoria() {
    }

    public Categoria(int id, int usuarioId, String nombre, String tipo, boolean esPredefinida) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.tipo = tipo;
        this.esPredefinida = esPredefinida;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public boolean isEsPredefinida() { return esPredefinida; }
    public void setEsPredefinida(boolean esPredefinida) { this.esPredefinida = esPredefinida; }

    // Las categorías predefinidas no se pueden eliminar (RF-06).
    public boolean puedeEliminarse() {
        return !esPredefinida;
    }

    // Así un JComboBox mostrará el nombre en lugar de una dirección de memoria.
    @Override
    public String toString() {
        return nombre;
    }
}