package principal;

import java.awt.EventQueue;
import vista.LoginFrame;
import vista.Tema;

public class Principal {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Tema.instalar();
            new LoginFrame().setVisible(true);
        });
    }
}
