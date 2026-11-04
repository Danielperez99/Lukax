package vista;

import java.awt.Frame;
import javax.swing.JDialog;

public class RegistroDialog extends JDialog {

    public RegistroDialog(Frame dueno) {
        super(dueno, "LUKAX - Crear cuenta", true);
        setContentPane(new PanelRegistro(this::dispose));
        setResizable(false);
        pack();
        setLocationRelativeTo(dueno);
    }
}
