package vista;

import controlador.UsuarioController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/** Pantalla de inicio de sesión: marca a la izquierda, formulario a la derecha. */
public class PanelLogin extends JPanel {

    private final Runnable alIngresar;
    private final Runnable alPedirRegistro;

    private final Tema.Campo txtCorreo = new Tema.Campo("tucorreo@ejemplo.com");
    private final Tema.CampoClave txtClave = new Tema.CampoClave("Tu contraseña");
    private final JLabel lblMensaje = new JLabel(" ");

    public PanelLogin(Runnable alIngresar, Runnable alPedirRegistro) {
        this.alIngresar = alIngresar;
        this.alPedirRegistro = alPedirRegistro;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 560));
        add(new PanelMarca(), BorderLayout.WEST);
        add(crearFormulario(), BorderLayout.CENTER);
    }

    private JPanel crearFormulario() {
        JPanel lado = new JPanel();
        lado.setBackground(Tema.PANEL);
        lado.setLayout(new BoxLayout(lado, BoxLayout.Y_AXIS));
        lado.setBorder(new EmptyBorder(60, 64, 40, 64));

        JLabel titulo = Tema.titulo("Bienvenido de nuevo", 28);
        JLabel subtitulo = Tema.etiqueta("Ingresa para ver el estado de tus finanzas");

        JLabel lblCorreo = Tema.etiqueta("Correo electrónico");
        JLabel lblClave = Tema.etiqueta("Contraseña");

        lblMensaje.setFont(Tema.fuente(Font.PLAIN, 13));
        lblMensaje.setForeground(Tema.NEGATIVO);

        Tema.Boton btnIngresar = new Tema.Boton("Ingresar", true);
        Tema.Boton btnRegistro = new Tema.Boton("Crear una cuenta", false);

        for (Component c : new Component[]{titulo, subtitulo, lblCorreo, txtCorreo,
            lblClave, txtClave, lblMensaje, btnIngresar, btnRegistro}) {
            ((javax.swing.JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        txtCorreo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        txtClave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnIngresar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnRegistro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        lado.add(titulo);
        lado.add(Box.createVerticalStrut(6));
        lado.add(subtitulo);
        lado.add(Box.createVerticalStrut(34));
        lado.add(lblCorreo);
        lado.add(Box.createVerticalStrut(6));
        lado.add(txtCorreo);
        lado.add(Box.createVerticalStrut(18));
        lado.add(lblClave);
        lado.add(Box.createVerticalStrut(6));
        lado.add(txtClave);
        lado.add(Box.createVerticalStrut(10));
        lado.add(lblMensaje);
        lado.add(Box.createVerticalStrut(14));
        lado.add(btnIngresar);
        lado.add(Box.createVerticalStrut(12));
        lado.add(btnRegistro);
        lado.add(Box.createVerticalGlue());

        btnIngresar.addActionListener(e -> ingresar());
        btnRegistro.addActionListener(e -> alPedirRegistro.run());
        txtCorreo.addActionListener(e -> txtClave.requestFocusInWindow());
        txtClave.addActionListener(e -> ingresar());
        return lado;
    }

    private void ingresar() {
        String correo = txtCorreo.getText();
        String clave = new String(txtClave.getPassword());
        try {
            // El controlador se crea aquí (y no al abrir la ventana) para que la
            // pantalla abra aunque MySQL esté apagado y avise con un mensaje.
            String error = new UsuarioController().iniciarSesion(correo, clave);
            if (error != null) {
                Tema.mensaje(lblMensaje, error, true);
                return;
            }
            alIngresar.run();
        } catch (RuntimeException ex) {
            Tema.mensaje(lblMensaje, "No hay conexión con la base de datos. ¿Está MySQL encendido?", true);
        }
    }

    /** Lado izquierdo: fondo negro con el logo dibujado en dorado. */
    private static class PanelMarca extends JPanel {

        PanelMarca() {
            setPreferredSize(new Dimension(420, 560));
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.suave(g);
            g2.setPaint(new GradientPaint(0, 0, new Color(8, 8, 8),
                    0, getHeight(), new Color(34, 28, 14)));
            g2.fillRect(0, 0, getWidth(), getHeight());

            int cx = getWidth() / 2;
            Tema.pintarOrnamento(g2, cx, 200, 112);
            Tema.textoEspaciado(g2, "LUKAX", cx, 392,
                    Tema.fuente(Font.PLAIN, 44), Tema.DORADO_CLARO, 0.22f);
            g2.setColor(new Color(201, 162, 77, 120));
            g2.drawLine(cx - 120, 414, cx + 120, 414);
            Tema.textoEspaciado(g2, "TU DINERO EN ORDEN", cx, 442,
                    Tema.fuente(Font.PLAIN, 13), Tema.DORADO, 0.3f);
            Tema.textoEspaciado(g2, "Control de gastos estudiantil", cx, 520,
                    Tema.fuente(Font.PLAIN, 12), Tema.TEXTO_SUAVE, 0.05f);
            g2.dispose();
        }
    }
}
