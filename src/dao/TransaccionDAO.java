package dao;

import conexion.ConexionBD;
import modelo.Categoria;
import modelo.Gasto;
import modelo.Ingreso;
import modelo.Transaccion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransaccionDAO {

    private final Connection conexion;

    public TransaccionDAO() {
        try {
            conexion = ConexionBD.getInstancia().getConexion();
        } catch (SQLException e) {
            throw new RuntimeException("No fue posible obtener la conexión.", e);
        }
    }

    public boolean registrar(Transaccion t) {
        String sql = "INSERT INTO transaccion "
                + "(usuario_id, categoria_id, tipo, monto, fecha, descripcion) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, t.getUsuarioId());
            ps.setInt(2, t.getCategoria().getId());
            ps.setString(3, t.getTipo());                 // polimorfismo
            ps.setDouble(4, t.getMonto());
            ps.setDate(5, Date.valueOf(t.getFecha()));
            ps.setString(6, t.getDescripcion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar transacción: " + e.getMessage());
            return false;
        }
    }

    // Solo devuelve los movimientos del usuario indicado (RNF-06).
    public List<Transaccion> listarPorUsuario(int usuarioId) {
        List<Transaccion> lista = new ArrayList<>();
        String sql = "SELECT t.id, t.usuario_id, t.tipo, t.monto, t.fecha, t.descripcion, "
                + "c.id AS c_id, c.usuario_id AS c_usuario_id, c.nombre AS c_nombre, "
                + "c.tipo AS c_tipo, c.es_predefinida AS c_predefinida "
                + "FROM transaccion t JOIN categoria c ON t.categoria_id = c.id "
                + "WHERE t.usuario_id = ? ORDER BY t.fecha DESC, t.id DESC";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Categoria categoria = new Categoria(
                            rs.getInt("c_id"),
                            rs.getInt("c_usuario_id"),
                            rs.getString("c_nombre"),
                            rs.getString("c_tipo"),
                            rs.getBoolean("c_predefinida")
                    );
                    int id = rs.getInt("id");
                    int idUsuario = rs.getInt("usuario_id");
                    double monto = rs.getDouble("monto");
                    LocalDate fecha = rs.getDate("fecha").toLocalDate();
                    String descripcion = rs.getString("descripcion");

                    if (rs.getString("tipo").equals("INGRESO")) {
                        lista.add(new Ingreso(id, idUsuario, categoria, monto, fecha, descripcion));
                    } else {
                        lista.add(new Gasto(id, idUsuario, categoria, monto, fecha, descripcion));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar transacciones: " + e.getMessage());
        }
        return lista;
    }

    // Exige también el usuario para que nadie borre datos ajenos (RF-05).
    public boolean eliminar(int id, int usuarioId) {
        String sql = "DELETE FROM transaccion WHERE id = ? AND usuario_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar transacción: " + e.getMessage());
            return false;
        }
    }

    public double calcularSaldo(int usuarioId) {
        String sql = "SELECT COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN monto ELSE -monto END), 0) "
                + "AS saldo FROM transaccion WHERE usuario_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular saldo: " + e.getMessage());
        }
        return 0;
    }
}