package vista;

import controlador.UsuarioController;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/** Formulario de registro de usuario (RF-01). */
public class PanelRegistro extends JPanel {

    private final Runnable alTerminar;

    private final Tema.Campo txtNombre = new Tema.Campo("Tu nombre");
    private final Tema.Campo txtCorreo = new Tema.Campo("tucorreo@ejemplo.com");
    private final Tema.CampoClave txtClave = new Tema.CampoClave("Mínimo 8 caracteres");
    private final Tema.CampoClave txtConfirmar = new Tema.CampoClave("Repite la contraseña");
    private final JLabel lblMensaje = new JLabel(" ");

    public PanelRegistro(Runnable alTerminar) {
        this.alTerminar = alTerminar;

        setBackground(Tema.PANEL);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(36, 52, 32, 52));
        setPreferredSize(new Dimension(480, 640));

        JLabel marca = new JLabel("LUKAX");
        marca.setFont(Tema.fuente(Font.PLAIN, 14));
        marca.setForeground(Tema.DORADO);
        JLabel titulo = Tema.titulo("Crea tu cuenta", 26);
        JLabel subtitulo = Tema.etiqueta("Empieza a ordenar tu dinero en un minuto");

        lblMensaje.setFont(Tema.fuente(Font.PLAIN, 13));

        Tema.Boton btnCrear = new Tema.Boton("Crear cuenta", true);
        Tema.Boton btnCancelar = new Tema.Boton("Cancelar", false);

        agregar(marca, 4);
        agregar(titulo, 6);
        agregar(subtitulo, 26);
        agregarCampo("Nombre", txtNombre);
        agregarCampo("Correo electrónico", txtCorreo);
        agregarCampo("Contraseña", txtClave);
        agregarCampo("Confirmar contraseña", txtConfirmar);
        agregar(lblMensaje, 12);
        agregar(btnCrear, 12);
        agregar(btnCancelar, 0);
        add(Box.createVerticalGlue());

        for (JComponent c : new JComponent[]{btnCrear, btnCancelar}) {
            c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        }

        btnCrear.addActionListener(e -> registrar());
        btnCancelar.addActionListener(e -> alTerminar.run());
        txtConfirmar.addActionListener(e -> registrar());
    }

    private void agregar(JComponent c, int espacioDespues) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(c);
        add(Box.createVerticalStrut(espacioDespues));
    }

    private void agregarCampo(String titulo, JComponent campo) {
        agregar(Tema.etiqueta(titulo), 6);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        agregar(campo, 16);
    }

    private void registrar() {
        try {
            String mensaje = new UsuarioController().registrar(
                    txtNombre.getText(),
                    txtCorreo.getText(),
                    new String(txtClave.getPassword()),
                    new String(txtConfirmar.getPassword()));
            if (mensaje.equals("Usuario registrado correctamente.")) {
                JOptionPane.showMessageDialog(this,
                        "Tu cuenta quedó creada. Ya puedes iniciar sesión.",
                        "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
                alTerminar.run();
            } else {
                Tema.mensaje(lblMensaje, mensaje, true);
            }
        } catch (RuntimeException ex) {
            Tema.mensaje(lblMensaje, "No hay conexión con la base de datos. ¿Está MySQL encendido?", true);
        }
    }
}
