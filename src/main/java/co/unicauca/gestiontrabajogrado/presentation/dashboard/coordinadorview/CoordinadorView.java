package co.unicauca.gestiontrabajogrado.presentation.dashboard.coordinadorview;

import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoFormato;
import co.unicauca.gestiontrabajogrado.presentation.common.HeaderPanel;

import co.unicauca.gestiontrabajogrado.presentation.common.ServiceManager;

import co.unicauca.gestiontrabajogrado.infrastructure.repository.IProyectoGradoRepository;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.IFormatoARepository;
import co.unicauca.gestiontrabajogrado.domain.service.IProyectoGradoService;



import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Vista del panel del Coordinador.
 * Solo maneja la presentación visual, delegando toda la lógica al controlador.
 */
public class CoordinadorView extends JFrame {

    // ----------------- Controlador -----------------
    private final CoordinadorController controller;

    // ----------------- Navegación -------------------
    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);
    private static final String CARD_HOME = "HOME";
    private static final String CARD_EVALUAR = "EVALUAR";

    // ----------------- Componentes ------------------
    private JTable table;
    private JCheckBox cbSoloPendientes;
    private JLabel lblPendientes;
    private JPopupMenu menuPopup;

    public CoordinadorView(CoordinadorController controller) {
        super("Panel Coordinador - Gestión de Trabajos de Grado");
        this.controller = controller;
        this.controller.setView(this); // Asociar vista con controlador

        configurarVentana();
        construirUI();

        // Arranque en HOME
        mostrar(CARD_HOME);
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1400, 840));
        setLocationRelativeTo(null);
    }

    private void construirUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(0xF3, 0xF4, 0xF5));
        setContentPane(root);

        root.add(new HeaderPanel(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(18, 18, 18, 18));
        root.add(body, BorderLayout.CENTER);

        body.add(buildTopRightControls(), BorderLayout.NORTH);

        cardPanel.setOpaque(false);
        cardPanel.add(buildHome(), CARD_HOME);
        cardPanel.add(buildEvaluarFormatoA(), CARD_EVALUAR);
        body.add(cardPanel, BorderLayout.CENTER);
    }

    // ============================================================
    // CONSTRUCCIÓN DE VISTAS
    // ============================================================

    private JPanel buildHome() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        panel.add(buildRedBanner("Panel Coordinador",
                        "Gestión de Trabajos de Grado"),
                BorderLayout.NORTH);

        JPanel canvas = new JPanel();
        canvas.setBackground(Color.WHITE);
        canvas.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.add(canvas, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildEvaluarFormatoA() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        panel.add(buildBreadcrumbs(), BorderLayout.NORTH);

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        column.add(buildRedBanner("Panel Coordinador",
                "Evalúa y gestiona las propuestas de proyectos de grado del programa"));
        column.add(Box.createVerticalStrut(10));

        JPanel cardHolder = new JPanel(new BorderLayout());
        cardHolder.setOpaque(false);
        cardHolder.setBorder(new EmptyBorder(8, 0, 0, 0));
        cardHolder.add(new FigmaCard(buildTableArea()), BorderLayout.CENTER);
        column.add(cardHolder);
        column.add(Box.createVerticalStrut(10));

        column.add(buildPendingPill());

        panel.add(column, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildBreadcrumbs() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);

        JLabel lHome = new JLabel("Inicio");
        lHome.setForeground(new Color(0x2F, 0x6D, 0xC1));
        lHome.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lHome.setBorder(new EmptyBorder(0, 0, 0, 6));
        lHome.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { mostrar(CARD_HOME); }
        });

        JLabel sep = new JLabel(" > ");
        sep.setForeground(new Color(0x66, 0x66, 0x66));
        JLabel lActual = new JLabel("Evaluar Formato A");
        lActual.setForeground(new Color(0x66, 0x66, 0x66));

        p.add(lHome);
        p.add(sep);
        p.add(lActual);
        return p;
    }

    private JPanel buildRedBanner(String title, String subtitle) {
        JPanel banner = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(210, 33, 33),
                        getWidth(), 0, new Color(133, 12, 12));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        banner.setOpaque(false);
        banner.setLayout(new GridBagLayout());
        banner.setPreferredSize(new Dimension(0, 92));
        banner.setBorder(new EmptyBorder(0, 24, 12, 24));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));
        lblSubtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitle.setBorder(new EmptyBorder(6, 0, 0, 0));

        inner.add(lblTitle);
        inner.add(lblSubtitle);
        banner.add(inner);

        return banner;
    }

    private JComponent buildTableArea() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        cbSoloPendientes = new JCheckBox("Solo pendientes");
        cbSoloPendientes.setOpaque(false);
        cbSoloPendientes.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        cbSoloPendientes.addActionListener(e -> recargarTabla());
        actions.add(cbSoloPendientes, BorderLayout.EAST);
        container.add(actions, BorderLayout.NORTH);

        table = new JTable(new CoordinadorTableModel());
        setupTableLook(table);
        installActionsColumn();

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() >= 0) {
                    abrirDialogoEvaluar();
                }
            }
        });

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);
        container.add(sp, BorderLayout.CENTER);

        recargarTabla();
        return container;
    }

    private void setupTableLook(JTable t) {
        t.setRowHeight(48);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setSelectionBackground(new Color(232, 245, 255));
        t.setSelectionForeground(Color.BLACK);
        t.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        JTableHeader header = t.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 46));
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        header.setBackground(new Color(0xF8, 0xF9, 0xFA));
        header.setForeground(new Color(80, 80, 80));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDE, 0xE2, 0xE6)));

        t.setDefaultRenderer(Object.class, new AlternatingRowRenderer());

        int estadoCol = guessEstadoColumn(t);
        if (estadoCol >= 0) {
            t.getColumnModel().getColumn(estadoCol).setCellRenderer(new EstadoBadgeRenderer());
        }
    }

    private int guessEstadoColumn(JTable t) {
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            String name = t.getColumnModel().getColumn(i).getHeaderValue().toString().trim().toLowerCase();
            if (name.equals("estado")) return i;
        }
        return -1;
    }

    private JComponent buildTopRightControls() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);

        JButton btnBell = makeIconButton("\uD83D\uDD14");
        bar.add(btnBell);

        JButton btnMenu = makeIconButton("\u2630");
        btnMenu.addActionListener(e -> showMenuPopup(btnMenu));
        bar.add(btnMenu);

        return bar;
    }

    private JButton makeIconButton(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        b.setForeground(Color.DARK_GRAY);
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(6, 10, 6, 10)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void showMenuPopup(Component invoker) {
        if (menuPopup == null) {
            menuPopup = new JPopupMenu();
            menuPopup.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0x7E, 0xA7, 0xCC)),
                    new EmptyBorder(0, 0, 0, 0)
            ));
            menuPopup.setBackground(new Color(0x2C, 0x6A, 0xA5));

            int w = 280;
            menuPopup.add(menuItem("Evaluar Formato A", w, () -> {
                mostrar(CARD_EVALUAR);
                recargarTabla();
            }));
            menuPopup.add(menuItem("Asignar Evaluadores", w, () -> info("Acción: Asignar Evaluadores")));
            menuPopup.add(menuItem("Revisar Solicitud de Sustentación", w, () -> info("Acción: Revisar Solicitud de Sustentación")));
            menuPopup.add(menuItem("Asignar Jurados", w, () -> info("Acción: Asignar Jurados")));
            menuPopup.add(menuItem("Asignar Sustentación", w, () -> info("Acción: Asignar Sustentación")));
            menuPopup.add(menuItem("Consolidar Calificaciones de Jurados", w, () -> info("Acción: Consolidar Calificaciones de Jurados")));
        }
        int x = invoker.getWidth() - menuPopup.getPreferredSize().width;
        int y = invoker.getHeight() + 6;
        menuPopup.show(invoker, x, y);
    }

    private JComponent menuItem(String text, int width, Runnable action) {
        Color BASE = new Color(0x2C, 0x6A, 0xA5);
        Color HOVER = new Color(0x3B, 0x7D, 0xBD);
        Color BORDER = new Color(0x7E, 0xA7, 0xCC);

        JPanel item = new JPanel(new BorderLayout());
        item.setPreferredSize(new Dimension(width, 44));
        item.setBackground(BASE);
        item.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        lbl.setBorder(new EmptyBorder(0, 14, 0, 14));
        item.add(lbl, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { item.setBackground(HOVER); }
            @Override public void mouseExited(MouseEvent e)  { item.setBackground(BASE); }
            @Override public void mouseClicked(MouseEvent e)  {
                if (menuPopup != null) menuPopup.setVisible(false);
                if (action != null) action.run();
            }
        });
        return item;
    }

    private JComponent buildPendingPill() {
        JPanel pill = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();

                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(16, 20, w - 32, h - 32, 24, 24);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(8, 12, w - 32, h - 32, 24, 24);

                g2.setColor(new Color(48, 102, 160));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(8, 12, w - 32, h - 32, 24, 24);

                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setPreferredSize(new Dimension(300, 110));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(20, 28, 24, 28));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Propuestas Pendientes", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        lblPendientes = new JLabel("0", SwingConstants.CENTER);
        lblPendientes.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPendientes.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 34));
        lblPendientes.setForeground(new Color(36, 86, 140));

        content.add(title);
        content.add(Box.createVerticalGlue());
        content.add(lblPendientes);
        content.add(Box.createVerticalGlue());

        pill.add(content, BorderLayout.CENTER);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        wrap.setOpaque(false);
        wrap.add(pill);
        return wrap;
    }

    // ============================================================
    // LÓGICA DE LA VISTA (Delegando al controlador)
    // ============================================================

    public void recargarTabla() {
        boolean solo = cbSoloPendientes != null && cbSoloPendientes.isSelected();

        // Delegar al controlador la obtención de datos
        List<PropuestaRow> rows = controller.obtenerPropuestas(solo);

        // Actualizar el modelo de la tabla
        CoordinadorTableModel model = (CoordinadorTableModel) table.getModel();
        model.setRows(rows);

        // Actualizar contador de pendientes
        actualizarContadorPendientes();
    }

    private void actualizarContadorPendientes() {
        long pendientes = controller.contarPorEstado(enumEstadoFormato.PENDIENTE);
        if (lblPendientes != null) {
            lblPendientes.setText(String.valueOf(pendientes));
        }
    }

    /**
     * Método llamado por el controlador para notificar cambios de estado.
     */
    public void notificarCambioEstado(Integer formatoId, enumEstadoFormato nuevoEstado) {
        CoordinadorTableModel model = (CoordinadorTableModel) table.getModel();

        // Buscar la fila correspondiente y actualizarla
        for (int i = 0; i < model.getRowCount(); i++) {
            PropuestaRow row = model.getRow(i);
            if (row.formatoId() != null && row.formatoId().equals(formatoId)) {
                model.updateEstado(i, nuevoEstado);
                break;
            }
        }

        // Actualizar el contador
        actualizarContadorPendientes();
    }

    private void abrirDialogoEvaluar() {
        int row = table.getSelectedRow();
        if (row < 0) {
            info("Selecciona una propuesta de la lista.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);
        PropuestaRow propuesta = ((CoordinadorTableModel) table.getModel()).getRow(modelRow);

        if (propuesta.estadoFormato() != enumEstadoFormato.PENDIENTE) {
            info("Esta propuesta ya ha sido evaluada.");
            return;
        }

        new EvaluarFormatoADialog(
                this,
                propuesta.titulo(),
                propuesta.formatoId(),
                controller,
                nuevoEstado -> {
                    // El controlador ya notificó el cambio
                    // Solo necesitamos refrescar visualmente
                    table.repaint();
                }
        ).setVisible(true);
    }

    private void installActionsColumn() {
        int col = findColumn(table, "Acciones");
        if (col < 0) return;
        table.getColumnModel().getColumn(col).setCellRenderer(new PillActionRenderer());
        table.getColumnModel().getColumn(col).setCellEditor(new PillActionEditor());
        table.getColumnModel().getColumn(col).setPreferredWidth(150);
    }

    private int findColumn(JTable t, String name) {
        for (int i = 0; i < t.getColumnModel().getColumnCount(); i++) {
            if (name.equalsIgnoreCase(t.getColumnName(i))) return i;
        }
        return -1;
    }

    private void ejecutarAccion(int viewRow) {
        if (viewRow < 0) return;
        int modelRow = table.convertRowIndexToModel(viewRow);
        PropuestaRow propuesta = ((CoordinadorTableModel) table.getModel()).getRow(modelRow);

        if (propuesta.estadoFormato() == enumEstadoFormato.PENDIENTE) {
            new EvaluarFormatoADialog(
                    this,
                    propuesta.titulo(),
                    propuesta.formatoId(),
                    controller,
                    nuevoEstado -> table.repaint()
            ).setVisible(true);
        } else {
            // Mostrar detalles - delegar al controlador
            CoordinadorController.DetallePropuestaDTO detalle =
                    controller.obtenerDetallePropuesta(propuesta.proyectoId());

            new DetallePropuestaDialog(
                    this,
                    detalle.formatearParaVista(),
                    propuesta
            ).setVisible(true);
        }
    }

    private void mostrar(String card) {
        cards.show(cardPanel, card);
    }

    private void info(String msg) {
        UIManager.put("OptionPane.background", Color.WHITE);
        UIManager.put("Panel.background", Color.WHITE);
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    // ============================================================
    // RENDERERS Y EDITORES
    // ============================================================

    static class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean sel, boolean foc, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, sel, foc, row, col);
            if (!sel) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
            }
            setBorder(new EmptyBorder(8, 12, 8, 12));
            return c;
        }
    }

    static class EstadoBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean sel, boolean foc, int row, int col) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, sel, foc, row, col);
            String v = value == null ? "pendiente" : value.toString().toLowerCase();

            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setOpaque(true);
            l.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            l.setBorder(new EmptyBorder(6, 12, 6, 12));

            switch (v) {
                case "aprobado" -> {
                    l.setBackground(new Color(212, 237, 218));
                    l.setForeground(new Color(21, 87, 36));
                    l.setText("APROBADO");
                }
                case "rechazado" -> {
                    l.setBackground(new Color(248, 215, 218));
                    l.setForeground(new Color(114, 28, 36));
                    l.setText("RECHAZADO");
                }
                default -> {
                    l.setBackground(new Color(255, 243, 205));
                    l.setForeground(new Color(133, 100, 4));
                    l.setText("PENDIENTE");
                }
            }
            if (sel) l.setBackground(l.getBackground().darker());
            return l;
        }
    }

    static class FigmaCard extends JPanel {
        FigmaCard(Component content) {
            super(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(0, 0, 0, 0));
            add(content, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int arc = 14;

            g2.setColor(new Color(0, 0, 0, 25));
            g2.fillRoundRect(8, 10, w - 16, h - 16, arc, arc);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 4, w - 16, h - 16, arc, arc);

            g2.setColor(new Color(48, 102, 160));
            g2.fillRoundRect(20, h - 18, w - 56, 6, 8, 8);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class PillButton extends JButton {
        PillButton() {
            setOpaque(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(getFont().deriveFont(Font.BOLD, 13f));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = getHeight(), w = getWidth();
            g2.setColor(new Color(184, 35, 35));
            g2.fillRoundRect(0, Math.max(0, (h - 34) / 2), w, 34, 18, 18);

            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics(getFont());
            String txt = getText() == null ? "" : getText();
            int x = (w - fm.stringWidth(txt)) / 2;
            int y = (h + fm.getAscent()) / 2 - 2;
            g2.drawString(txt, x, y);
            g2.dispose();
        }
    }

    class PillActionRenderer extends PillButton implements javax.swing.table.TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            PropuestaRow r = ((CoordinadorTableModel) table.getModel())
                    .getRow(table.convertRowIndexToModel(row));
            setText(r.estadoFormato() == enumEstadoFormato.PENDIENTE ? "Evaluar" : "Detalles");
            return this;
        }
    }

    class PillActionEditor extends javax.swing.AbstractCellEditor
            implements javax.swing.table.TableCellEditor {
        private final PillButton btn = new PillButton();
        private int editingRow = -1;

        PillActionEditor() {
            btn.addActionListener(e -> {
                fireEditingStopped();
                ejecutarAccion(editingRow);
            });
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            editingRow = row;
            PropuestaRow r = ((CoordinadorTableModel) table.getModel())
                    .getRow(table.convertRowIndexToModel(row));
            btn.setText(r.estadoFormato() == enumEstadoFormato.PENDIENTE ? "Evaluar" : "Detalles");
            return btn;
        }
    }
    // ============================================================
// MAIN PARA PROBAR SOLO ESTA VISTA (CoordinadorView)
// ============================================================
    // ============================================================
// MAIN PARA PROBAR SOLO ESTA VISTA (CoordinadorView)
// ============================================================
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            try {
                // Buscar dependencias dentro de ServiceManager probando varios nombres comunes
                IProyectoGradoRepository proyectoRepo =
                        sm(IProyectoGradoRepository.class,
                                "getProyectoGradoRepository", "getProyectoRepository",
                                "getProyectoGradoRepo", "getProyectoRepo",
                                "proyectoGradoRepository");

                IFormatoARepository formatoRepo =
                        sm(IFormatoARepository.class,
                                "getFormatoARepository", "getFormatoRepository",
                                "getFormatoARepo", "getFormatoRepo",
                                "formatoARepository");

                IProyectoGradoService proyectoService =
                        sm(IProyectoGradoService.class,
                                "getProyectoGradoService", "getProyectoService",
                                "getProyectoGradoSrv", "getProyectoSrv",
                                "proyectoGradoService");

                CoordinadorController controller =
                        new CoordinadorController(proyectoRepo, formatoRepo, proyectoService);

                CoordinadorView view = new CoordinadorView(controller);
                view.setVisible(true);

            } catch (Throwable ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Error inicializando Coordinador:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                ex.printStackTrace();
            }
        });
    }

    /**
     * Obtiene una dependencia desde ServiceManager usando reflexión.
     * - Prueba métodos estáticos e instancia (si existe getInstance()/instance/constructor sin args).
     * - Acepta varios nombres candidatos hasta encontrar uno que devuelva el tipo esperado.
     */
    @SuppressWarnings("unchecked")
    private static <T> T sm(Class<T> type, String... methodCandidates) throws Exception {
        Class<?> smClass = co.unicauca.gestiontrabajogrado.presentation.common.ServiceManager.class;

        // Intentar conseguir una instancia por si los métodos no son estáticos
        Object smInstance = null;
        try {
            try {
                var m = smClass.getMethod("getInstance");
                smInstance = m.invoke(null);
            } catch (NoSuchMethodException ignore) {
                try {
                    var m = smClass.getMethod("instance");
                    smInstance = m.invoke(null);
                } catch (NoSuchMethodException ignore2) {
                    try {
                        smInstance = smClass.getDeclaredConstructor().newInstance();
                    } catch (Throwable ignore3) {
                        // Si no hay forma de instanciar, seguiremos probando métodos estáticos
                    }
                }
            }
        } catch (Throwable ignore) { /* seguimos */ }

        // Probar con cada nombre candidato
        for (String name : methodCandidates) {
            try {
                var m = smClass.getMethod(name);
                Object val = m.invoke(smInstance); // sirve para estático o instancia
                if (val != null && type.isAssignableFrom(val.getClass())) {
                    return (T) val;
                }
            } catch (NoSuchMethodException ignored) {
                // probar siguiente
            }
        }

        throw new NoSuchMethodException(
                "No encontré en ServiceManager un método que devuelva " + type.getSimpleName() +
                        ". Probé: " + String.join(", ", methodCandidates)
        );
    }


}
