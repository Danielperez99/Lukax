package controlador;

import dao.CategoriaDAO;
import java.util.ArrayList;
import java.util.List;
import modelo.Categoria;

public class CategoriaController {

    private final CategoriaDAO categoriaDAO;

    public CategoriaController() {
        categoriaDAO = new CategoriaDAO();
    }

    // Categorías predefinidas + las propias del usuario en sesión.
    public List<Categoria> listarPorTipo(String tipo) {
        if (!Sesion.haySesion()) {
            return new ArrayList<>();
        }
        return categoriaDAO.listarPorTipo(Sesion.getUsuario().getId(), tipo);
    }
}
