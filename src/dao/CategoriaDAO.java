package dao;

import conexion.ConexionBD;
import modelo.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    private final Connection conexion;

    public CategoriaDAO() {
        try {
            conexion = ConexionBD.getInstancia().getConexion();
        } catch (SQLException e) {
            throw new RuntimeException("No fue posible obtener la conexión.", e);
        }
    }

    // Devuelve las predefinidas (usuario_id NULL) más las propias del usuario.
    public List<Categoria> listarPorTipo(int usuarioId, String tipo) {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id, usuario_id, nombre, tipo, es_predefinida FROM categoria "
                + "WHERE (usuario_id IS NULL OR usuario_id = ?) AND tipo = ? ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setString(2, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categorias.add(new Categoria(
                            rs.getInt("id"),
                            rs.getInt("usuario_id"),      // si es NULL devuelve 0
                            rs.getString("nombre"),
                            rs.getString("tipo"),
                            rs.getBoolean("es_predefinida")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar categorías: " + e.getMessage());
        }
        return categorias;
    }

    public boolean registrar(Categoria categoria) {
        String sql = "INSERT INTO categoria (usuario_id, nombre, tipo, es_predefinida) "
                + "VALUES (?, ?, ?, FALSE)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, categoria.getUsuarioId());
            ps.setString(2, categoria.getNombre());
            ps.setString(3, categoria.getTipo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar categoría: " + e.getMessage());
            return false;
        }
    }
}