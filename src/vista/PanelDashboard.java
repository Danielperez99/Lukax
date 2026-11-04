package vista;

import controlador.CategoriaController;
import controlador.TransaccionController;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.font.TextAttribute;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import modelo.Categoria;
import modelo.Gasto;
import modelo.Ingreso;
import modelo.Transaccion;

/** Panel principal: resumen del mes, movimientos y formulario de registro. */
public class PanelDashboard extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale COLOMBIA = Locale.forLanguageTag("es-CO");

    private final String nombreUsuario;
    private final Runnable alCerrarSesion;

    // Los controladores se crean cuando se necesitan (si MySQL está apagado, la ventana abre igual).
    private TransaccionController transacciones;
    private CategoriaController categorias;
    private List<Transaccion> movimientos = new ArrayList<>();

    private final JLabel lblSaldo = new JLabel("$ 0");
    private final JLabel lblIngresos = new JLabel("$ 0");
    private final JLabel lblGastos = new JLabel("$ 0");
    private final JLabel lblConteo = Tema.etiqueta(" ");
    private final JLabel lblMensaje = new JLabel(" ");
    private final JLabel lblEstado = Tema.etiqueta("● Iniciando...");

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Fecha", "Tipo", "Categoría", "Descripción", "Monto"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    private final Tema.Interruptor btnGasto = new Tema.Interruptor("Gasto");
    private final Tema.Interruptor btnIngreso = new Tema.Interruptor("Ingreso");
    private final JComboBox<Categoria> cboCategoria = new JComboBox<>();
    private final Tema.Campo txtMonto = new Tema.Campo("Ej: 12000");
    private final Tema.Campo txtFecha = new Tema.Campo("dd/mm/aaaa");
    private final Tema.Campo txtDescripcion = new Tema.Campo("Opcional, máximo 200 caracteres");

    public PanelDashboard(String nombreUsuario, Runnable alCerrarSesion) {
        this.nombreUsuario = nombreUsuario;
        this.alCerrarSesion = alCerrarSesion;

        setBackground(Tema.FONDO);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1200, 760));

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);

        btnGasto.setSelected(true);
        txtFecha.setText(LocalDate.now().format(FORMATO_FECHA));
        Tema.estilizarCombo(cboCategoria);
    }

    // ============================================================ construcción
    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Tema.PANEL);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDE),
                new EmptyBorder(14, 28, 14, 28)));

        JLabel marca = new JLabel("LUKAX");
        Map<TextAttribute, Object> atributos = new HashMap<>();
        atributos.put(TextAttribute.TRACKING, 0.22f);
        marca.setFont(Tema.fuente(Font.PLAIN, 24).deriveFont(atributos));
        marca.setForeground(Tema.DORADO_CLARO);

        JLabel saludo = new JLabel("Hola, " + nombreUsuario);
        saludo.setFont(Tema.fuente(Font.PLAIN, 14));
        saludo.setForeground(Tema.TEXTO);

        Tema.Boton btnSalir = new Tema.Boton("Cerrar sesión", false);
        btnSalir.setBorder(new EmptyBorder(8, 16, 8, 16));
        btnSalir.addActionListener(e -> alCerrarSesion.run());

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        derecha.setOpaque(false);
        derecha.add(saludo);
        derecha.add(btnSalir);

        barra.add(marca, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);
        return barra;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(0, 18));
        contenido.setOpaque(false);
        contenido.setBorder(new EmptyBorder(22, 28, 22, 28));

        String mes = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, COLOMBIA);
        JPanel resumen = new JPanel(new GridLayout(1, 3, 18, 0));
        resumen.setOpaque(false);
        resumen.add(tarjetaResumen("SALDO DISPONIBLE", lblSaldo, "Todo lo que has registrado"));
        resumen.add(tarjetaResumen("INGRESOS DE " + mes.toUpperCase(COLOMBIA), lblIngresos,
                "Dinero que entró este mes"));
        resumen.add(tarjetaResumen("GASTOS DE " + mes.toUpperCase(COLOMBIA), lblGastos,
                "Dinero que salió este mes"));
        contenido.add(resumen, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.gridx = 0;
        c.weightx = 0.64;
        c.insets = new Insets(0, 0, 0, 18);
        centro.add(crearTarjetaMovimientos(), c);
        c.gridx = 1;
        c.weightx = 0.36;
        c.insets = new Insets(0, 0, 0, 0);
        centro.add(crearTarjetaFormulario(), c);
        contenido.add(centro, BorderLayout.CENTER);
        return contenido;
    }

    private JPanel tarjetaResumen(String titulo, JLabel valor, String detalle) {
        Tema.PanelRedondeado tarjeta = new Tema.PanelRedondeado(18, Tema.TARJETA, Tema.BORDE);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(18, 22, 18, 22));

        JLabel lblTitulo = Tema.etiqueta(titulo);
        lblTitulo.setFont(Tema.fuente(Font.BOLD, 11));
        valor.setFont(Tema.fuente(Font.BOLD, 30));
        valor.setForeground(Tema.TEXTO);
        JLabel lblDetalle = Tema.etiqueta(detalle);
        lblDetalle.setFont(Tema.fuente(Font.PLAIN, 12));

        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(valor);
        tarjeta.add(Box.createVerticalStrut(4));
        tarjeta.add(lblDetalle);
        return tarjeta;
    }

    private JPanel crearTarjetaMovimientos() {
        Tema.PanelRedondeado tarjeta = new Tema.PanelRedondeado(18, Tema.TARJETA, Tema.BORDE);
        tarjeta.setLayout(new BorderLayout(0, 14));
        tarjeta.setBorder(new EmptyBorder(20, 22, 18, 22));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(Tema.titulo("Movimientos", 18), BorderLayout.WEST);
        encabezado.add(lblConteo, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(tabla);
        Tema.estilizarTabla(tabla, scroll);
        int[] anchos = {120, 90, 175, 190, 130};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }

        Tema.Boton btnEliminar = new Tema.Boton("Eliminar seleccionado", false, Tema.NEGATIVO);
        btnEliminar.setBorder(new EmptyBorder(9, 18, 9, 18));
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pie.setOpaque(false);
        pie.add(btnEliminar);

        tarjeta.add(encabezado, BorderLayout.NORTH);
        tarjeta.add(scroll, BorderLayout.CENTER);
        tarjeta.add(pie, BorderLayout.SOUTH);
        tarjeta.setPreferredSize(new Dimension(720, 400));
        return tarjeta;
    }

    private JPanel crearTarjetaFormulario() {
        Tema.PanelRedondeado tarjeta = new Tema.PanelRedondeado(18, Tema.TARJETA, Tema.BORDE);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(20, 22, 20, 22));

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(btnGasto);
        grupo.add(btnIngreso);
        btnGasto.addActionListener(e -> cargarCategorias());
        btnIngreso.addActionListener(e -> cargarCategorias());
        JPanel tipo = new JPanel(new GridLayout(1, 2, 8, 0));
        tipo.setOpaque(false);
        tipo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        tipo.add(btnGasto);
        tipo.add(btnIngreso);

        lblMensaje.setFont(Tema.fuente(Font.PLAIN, 13));

        Tema.Boton btnRegistrar = new Tema.Boton("Registrar movimiento", true);
        Tema.Boton btnLimpiar = new Tema.Boton("Limpiar", false);
        btnRegistrar.addActionListener(e -> registrar());
        btnLimpiar.addActionListener(e -> {
            limpiarFormulario();
            lblMensaje.setText(" ");
        });

        // Monto y fecha comparten fila para que el formulario quepa sin scroll.
        JPanel filaMontoFecha = new JPanel(new GridLayout(1, 2, 12, 0));
        filaMontoFecha.setOpaque(false);
        filaMontoFecha.add(campoConEtiqueta("Monto (COP)", txtMonto));
        filaMontoFecha.add(campoConEtiqueta("Fecha", txtFecha));

        JPanel filaBotones = new JPanel(new GridBagLayout());
        filaBotones.setOpaque(false);
        GridBagConstraints gb = new GridBagConstraints();
        gb.fill = GridBagConstraints.HORIZONTAL;
        gb.weightx = 0.68;
        gb.insets = new Insets(0, 0, 0, 10);
        filaBotones.add(btnRegistrar, gb);
        gb.weightx = 0.32;
        gb.insets = new Insets(0, 0, 0, 0);
        filaBotones.add(btnLimpiar, gb);
        filaBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        izquierda(Tema.titulo("Nuevo movimiento", 18), 14, tarjeta);
        izquierda(tipo, 14, tarjeta);
        izquierda(Tema.etiqueta("Categoría"), 6, tarjeta);
        cboCategoria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        izquierda(cboCategoria, 14, tarjeta);
        filaMontoFecha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        izquierda(filaMontoFecha, 14, tarjeta);
        izquierda(campoConEtiqueta("Descripción", txtDescripcion), 8, tarjeta);
        izquierda(lblMensaje, 10, tarjeta);
        izquierda(filaBotones, 0, tarjeta);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.setPreferredSize(new Dimension(400, 400));
        return tarjeta;
    }

    private JPanel campoConEtiqueta(String titulo, JComponent campo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = Tema.etiqueta(titulo);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        p.add(l);
        p.add(Box.createVerticalStrut(6));
        p.add(campo);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        return p;
    }

    private void izquierda(JComponent c, int espacioDespues, JPanel destino) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        destino.add(c);
        destino.add(Box.createVerticalStrut(espacioDespues));
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Tema.PANEL);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.BORDE),
                new EmptyBorder(8, 28, 8, 28)));
        barra.add(lblEstado, BorderLayout.WEST);
        return barra;
    }

    // ============================================================ datos
    private TransaccionController transacciones() {
        if (transacciones == null) {
            transacciones = new TransaccionController();
        }
        return transacciones;
    }

    private CategoriaController categorias() {
        if (categorias == null) {
            categorias = new CategoriaController();
        }
        return categorias;
    }

    /** Lee la base de datos y refresca toda la pantalla. */
    public void cargar() {
        try {
            List<Transaccion> lista = transacciones().listar();
            double saldo = transacciones().obtenerSaldo();
            mostrarDatos(lista, saldo);
            cargarCategorias();
            lblEstado.setText("● Conectado a la base de datos");
        } catch (RuntimeException ex) {
            lblEstado.setText("● Sin conexión con la base de datos. Revisa que MySQL esté encendido.");
        }
    }

    void mostrarDatos(List<Transaccion> lista, double saldo) {
        movimientos = lista;
        modeloTabla.setRowCount(0);
        LocalDate hoy = LocalDate.now();
        double ingresosMes = 0;
        double gastosMes = 0;
        for (Transaccion t : lista) {
            boolean esGasto = t.getTipo().equals("GASTO");
            String monto = (esGasto ? "- " : "+ ") + Tema.dinero(t.getMonto());
            String descripcion = t.getDescripcion() == null || t.getDescripcion().isEmpty()
                    ? "—" : t.getDescripcion();
            modeloTabla.addRow(new Object[]{
                t.getFecha().format(FORMATO_FECHA),
                esGasto ? "Gasto" : "Ingreso",
                t.getCategoria().getNombre(),
                descripcion,
                monto});
            boolean esDelMes = t.getFecha().getMonth() == hoy.getMonth()
                    && t.getFecha().getYear() == hoy.getYear();
            if (esDelMes) {
                if (esGasto) {
                    gastosMes += t.getMonto();
                } else {
                    ingresosMes += t.getMonto();
                }
            }
        }
        lblSaldo.setText(Tema.dinero(saldo));
        lblSaldo.setForeground(saldo >= 0 ? Tema.DORADO_CLARO : Tema.NEGATIVO);
        lblIngresos.setText(Tema.dinero(ingresosMes));
        lblIngresos.setForeground(Tema.POSITIVO);
        lblGastos.setText(Tema.dinero(gastosMes));
        lblGastos.setForeground(Tema.NEGATIVO);
        lblConteo.setText(lista.size() == 1 ? "1 movimiento" : lista.size() + " movimientos");
    }

    private void cargarCategorias() {
        String tipo = btnGasto.isSelected() ? "GASTO" : "INGRESO";
        try {
            establecerCategorias(categorias().listarPorTipo(tipo));
        } catch (RuntimeException ex) {
            establecerCategorias(new ArrayList<>());
        }
    }

    void establecerCategorias(List<Categoria> lista) {
        cboCategoria.removeAllItems();
        for (Categoria c : lista) {
            cboCategoria.addItem(c);
        }
    }

    // ============================================================ acciones
    private void registrar() {
        Categoria categoria = (Categoria) cboCategoria.getSelectedItem();
        if (categoria == null) {
            Tema.mensaje(lblMensaje, "No hay categorías disponibles.", true);
            return;
        }
        double monto;
        try {
            monto = Tema.leerMonto(txtMonto.getText());
        } catch (NumberFormatException ex) {
            Tema.mensaje(lblMensaje, "Escribe un monto válido, por ejemplo 12000.", true);
            return;
        }
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(txtFecha.getText().trim(), FORMATO_FECHA);
        } catch (DateTimeParseException ex) {
            Tema.mensaje(lblMensaje, "Fecha inválida. Usa el formato dd/mm/aaaa.", true);
            return;
        }
        String descripcion = txtDescripcion.getText().trim();

        // Polimorfismo: las dos son Transaccion y el controlador las trata igual.
        Transaccion nueva = btnGasto.isSelected()
                ? new Gasto(0, 0, categoria, monto, fecha, descripcion)
                : new Ingreso(0, 0, categoria, monto, fecha, descripcion);
        try {
            String mensaje = transacciones().registrar(nueva);
            boolean exito = mensaje.contains("registrado correctamente");
            if (exito) {
                limpiarFormulario();
                cargar();
            }
            Tema.mensaje(lblMensaje, mensaje, !exito);
        } catch (RuntimeException ex) {
            Tema.mensaje(lblMensaje, "No hay conexión con la base de datos.", true);
        }
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= movimientos.size()) {
            Tema.mensaje(lblMensaje, "Selecciona un movimiento de la tabla.", true);
            return;
        }
        Transaccion t = movimientos.get(fila);
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Eliminar este movimiento?\n" + t.getCategoria().getNombre()
                + " · " + Tema.dinero(t.getMonto()),
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            String mensaje = transacciones().eliminar(t.getId());
            cargar();
            Tema.mensaje(lblMensaje, mensaje, !mensaje.contains("correctamente"));
        } catch (RuntimeException ex) {
            Tema.mensaje(lblMensaje, "No hay conexión con la base de datos.", true);
        }
    }

    private void limpiarFormulario() {
        txtMonto.setText("");
        txtDescripcion.setText("");
        txtFecha.setText(LocalDate.now().format(FORMATO_FECHA));
        if (cboCategoria.getItemCount() > 0) {
            cboCategoria.setSelectedIndex(0);
        }
    }
}
