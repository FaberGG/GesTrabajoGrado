package co.unicauca.gestiontrabajogrado.presentation.dashboard.coordinadorview;

import co.unicauca.gestiontrabajogrado.controller.CoordinadorController;
import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoFormato;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Diálogo de Evaluación de Formato A.
 * Solo maneja la presentación, delega la lógica al controlador.
 */
public class EvaluarFormatoADialog extends JDialog {

    private final Integer formatoId;
    private final CoordinadorController controller;
    private final Consumer<enumEstadoFormato> onSaved;

    private final JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Aprobado", "Rechazado"});
    private final JTextArea txtObs = new JTextArea(8, 50);

    public EvaluarFormatoADialog(
            JFrame parent,
            String tituloPropuesta,
            Integer formatoId,
            CoordinadorController controller,
            Consumer<enumEstadoFormato> onSaved) {
        super(parent, "Evaluar", true);
        this.formatoId = formatoId;
        this.controller = controller;
        this.onSaved = onSaved;

        setContentPane(buildUI(tituloPropuesta));
        setSize(920, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // ============================================================
    // CONSTRUCCIÓN DE UI
    // ============================================================

    private JComponent buildUI(String tituloPropuesta) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(246, 247, 249));

        root.add(buildHeader("EVALUAR"), BorderLayout.NORTH);

        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(16, 16, 16, 16));
        wrap.setLayout(new BorderLayout());

        JPanel card = new SoftCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(22, 24, 24, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST;

        // Estado
        JLabel lEstado = new JLabel("Estado");
        lEstado.setFont(lEstado.getFont().deriveFont(Font.BOLD, 15f));
        card.add(lEstado, gc);

        gc.gridx = 1;
        cbEstado.setFont(cbEstado.getFont().deriveFont(Font.PLAIN, 14f));
        cbEstado.setPreferredSize(new Dimension(220, 30));
        card.add(cbEstado, gc);

        // Descripción
        gc.gridx = 0;
        gc.gridy++;
        JLabel lDesc = new JLabel("Descripción de la evaluación");
        lDesc.setFont(lDesc.getFont().deriveFont(Font.BOLD, 15f));
        gc.gridwidth = 2;
        card.add(lDesc, gc);

        // Área de texto
        gc.gridy++;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);
        txtObs.setFont(txtObs.getFont().deriveFont(Font.PLAIN, 14f));
        JScrollPane sp = new JScrollPane(txtObs);
        sp.setPreferredSize(new Dimension(520, 180));
        sp.setBorder(BorderFactory.createLineBorder(new Color(205, 205, 205)));
        card.add(sp, gc);

        // Botón Guardar
        gc.gridy++;
        gc.gridwidth = 2;
        gc.fill = GridBagConstraints.NONE;
        gc.weightx = 0;

        JButton btnGuardar = new RedPillButton("Guardar");
        btnGuardar.addActionListener(e -> onGuardar());
        JPanel btnCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnCenter.setOpaque(false);
        btnCenter.add(btnGuardar);
        card.add(btnCenter, gc);

        wrap.add(card, BorderLayout.CENTER);
        root.add(wrap, BorderLayout.CENTER);

        getRootPane().setDefaultButton(btnGuardar);

        return root;
    }

    private JComponent buildHeader(String title) {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(210, 33, 33),
                        getWidth(), 0, new Color(133, 12, 12)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 76));
        header.setLayout(new GridBagLayout());

        JLabel lbl = new JLabel(title);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 28f));
        header.add(lbl);

        return header;
    }

    // ============================================================
    // LÓGICA DE GUARDAR (Delegando al controlador)
    // ============================================================

    private void onGuardar() {
        String estadoTxt = (String) cbEstado.getSelectedItem();
        String obs = txtObs.getText().trim();

        // Validación básica en la vista
        if (obs.isEmpty()) {
            mostrarError("Por favor ingrese las observaciones de la evaluación.");
            return;
        }

        enumEstadoFormato estadoSeleccionado = "Aprobado".equalsIgnoreCase(estadoTxt)
                ? enumEstadoFormato.APROBADO
                : enumEstadoFormato.RECHAZADO;

        try {
            // Delegar al controlador
            enumEstadoFormato estadoResultante;
            if (estadoSeleccionado == enumEstadoFormato.APROBADO) {
                estadoResultante = controller.aprobarFormato(formatoId, obs);
            } else {
                estadoResultante = controller.rechazarFormato(formatoId, obs);
            }

            // Notificar a la vista padre
            if (onSaved != null) {
                onSaved.accept(estadoResultante);
            }

            // Mostrar mensaje de éxito
            String mensaje = estadoResultante == enumEstadoFormato.APROBADO
                    ? "✅ Formato A APROBADO exitosamente"
                    : "⚠️ Formato A RECHAZADO";

            mostrarExito(mensaje);
            dispose();

        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (Exception ex) {
            mostrarError("Error al procesar la evaluación:\n" + ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Evaluación Guardada",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ============================================================
    // COMPONENTES VISUALES PERSONALIZADOS
    // ============================================================

    static class SoftCard extends JPanel {
        SoftCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight(), arc = 14;

            // Sombra
            g2.setColor(new Color(0, 0, 0, 28));
            g2.fillRoundRect(6, 8, w - 12, h - 12, arc, arc);

            // Cuerpo
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 2, w - 12, h - 12, arc, arc);

            // Borde sutil
            g2.setColor(new Color(120, 158, 196));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(0, 2, w - 12, h - 12, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RedPillButton extends JButton {
        private final Color normal = new Color(184, 35, 35);
        private final Color hover = new Color(204, 45, 45);
        private final Color press = new Color(160, 30, 30);

        RedPillButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setFont(getFont().deriveFont(Font.BOLD, 14f));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(170, 42));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = getModel().isPressed() ? press
                    : (getModel().isRollover() ? hover : normal);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);

            // Texto centrado
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics(getFont());
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 2;
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
}