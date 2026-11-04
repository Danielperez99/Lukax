package vista;

import controlador.Sesion;
import java.awt.Dimension;
import javax.swing.JFrame;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("LUKAX - Control de gastos estudiantil");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        PanelDashboard panel = new PanelDashboard(
                Sesion.getUsuario().getNombre(), this::cerrarSesion);
        setContentPane(panel);
        setMinimumSize(new Dimension(1100, 700));
        pack();
        setLocationRelativeTo(null);
        panel.cargar();
    }

    private void cerrarSesion() {
        Sesion.cerrar();
        new LoginFrame().setVisible(true);
        dispose();
    }
}
