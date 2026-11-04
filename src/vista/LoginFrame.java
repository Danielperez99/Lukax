package vista;

import javax.swing.JFrame;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("LUKAX - Iniciar sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new PanelLogin(this::abrirDashboard, this::abrirRegistro));
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    private void abrirDashboard() {
        new DashboardFrame().setVisible(true);
        dispose();
    }

    private void abrirRegistro() {
        new RegistroDialog(this).setVisible(true);
    }
}
