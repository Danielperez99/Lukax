package modelo;

public class Presupuesto {

    // Porcentaje desde el cual se avisa (a confirmar con el grupo, RF-10).
    private static final double PORCENTAJE_ALERTA = 80.0;

    private int id;
    private int usuarioId;
    private Categoria categoria;
    private double limiteMensual;

    public Presupuesto() {
    }

    public Presupuesto(int id, int usuarioId, Categoria categoria, double limiteMensual) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.categoria = categoria;
        this.limiteMensual = limiteMensual;
    }

    public double calcularPorcentajeUsado(double gastadoEnElMes) {
        if (limiteMensual <= 0) {
            return 0;
        }
        return gastadoEnElMes / limiteMensual * 100;
    }

    public boolean debeAlertar(double gastadoEnElMes) {
        return calcularPorcentajeUsado(gastadoEnElMes) >= PORCENTAJE_ALERTA;
    }

    public boolean superaLimite(double gastadoEnElMes) {
        return gastadoEnElMes > limiteMensual;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public double getLimiteMensual() { return limiteMensual; }
    public void setLimiteMensual(double limiteMensual) { this.limiteMensual = limiteMensual; }
}