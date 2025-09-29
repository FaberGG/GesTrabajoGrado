package co.unicauca.gestiontrabajogrado.presentation.dashboard.coordinadorview;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Diálogo de Detalles de Propuesta (estilo Figma).
 * - Encabezado rojo con "DETALLES" (sin botón X interno)
 * - Card con labels a la izquierda y valores a la derecha
 * - Rellena campos faltantes desde PropuestaRow por reflexión (tolerante a nombres de getters)
 */
public class DetallePropuestaDialog extends JDialog {

    public DetallePropuestaDialog(JFrame parent, String detalleTexto, PropuestaRow fallbackRow) {
        super(parent, "Detalles", true);
        setContentPane(buildUI(detalleTexto, fallbackRow));
        setSize(980, 560);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    // ==================== UI ====================

    private JComponent buildUI(String detalleTexto, PropuestaRow fallbackRow) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(246, 247, 249));

        // Header rojo con gradiente y título
        root.add(buildRedHeader("DETALLES"), BorderLayout.NORTH);

        // Card central
        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(16, 16, 16, 16));

        SoftCard card = new SoftCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(20, 24, 24, 24));

        // Parse del texto y fallback con la fila
        Parsed p = parse(detalleTexto);
        applyFallbackFromRow(p, fallbackRow);

        // Grid
        GridBagConstraints gL = new GridBagConstraints();
        gL.gridx = 0; gL.gridy = 0; gL.anchor = GridBagConstraints.NORTHEAST; gL.insets = new Insets(6, 6, 6, 18);
        GridBagConstraints gR = new GridBagConstraints();
        gR.gridx = 1; gR.gridy = 0; gR.anchor = GridBagConstraints.NORTHWEST; gR.insets = new Insets(6, 6, 6, 6);
        gR.fill = GridBagConstraints.HORIZONTAL; gR.weightx = 1;

        // Propuesta
        addLabel(card, gL, "Propuesta");
        addValue(card, gR, p.titulo);

        // Estudiante(s)
        gL.gridy++; gR.gridy++;
        addLabel(card, gL, "Estudiante(s)");
        addValue(card, gR, p.estudiantes);

        // Docente encargado
        gL.gridy++; gR.gridy++;
        addLabel(card, gL, "Docente encargado");
        addValue(card, gR, p.docente);

        // Descripción (usa text-area no editable para permitir multi-línea)
        gL.gridy++; gR.gridy++;
        addLabel(card, gL, "Descripción");
        JTextArea ta = new JTextArea(emptyToDash(p.descripcion));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setFont(new JLabel().getFont());
        ta.setBorder(new EmptyBorder(0,0,0,0));
        card.add(ta, gR);

        center.add(card, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        return root;
    }

    private JPanel buildRedHeader(String title) {
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(210, 33, 33),
                        getWidth(), 0, new Color(133, 12, 12));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 72));
        header.setLayout(new GridBagLayout());
        header.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lbl = new JLabel(title);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 28f));
        header.add(lbl);
        return header;
    }

    private void addLabel(JPanel parent, GridBagConstraints gc, String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 16f));
        parent.add(l, (GridBagConstraints) gc.clone());
    }

    private void addValue(JPanel parent, GridBagConstraints gc, String value) {
        JLabel l = new JLabel(emptyToDash(value));
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 15f));
        parent.add(l, (GridBagConstraints) gc.clone());
    }

    private String emptyToDash(String s) {
        return (s == null || s.trim().isEmpty()) ? "—" : s;
    }

    // ==================== Modelo interno ====================

    private static class Parsed {
        String titulo;
        String estudiantes;
        String docente;
        String descripcion;
    }

    /** Parser básico: conserva el texto recibido como descripción. */
    private Parsed parse(String txt) {
        Parsed p = new Parsed();
        if (txt != null) {
            String t = txt.strip();
            p.descripcion = t;
            // Si viene en formato "Propuesta: xxx\nEstudiantes: yyy\nDocente: zzz\nDescripción: ..." puedes
            // enriquecer este parser. Por ahora, lo más robusto es tratar el texto como la descripción.
        }
        return p;
    }

    /** Completa campos faltantes desde la fila usando reflexión tolerante a nombres. */
    private void applyFallbackFromRow(Parsed p, PropuestaRow row) {
        if (row == null) return;

        if (isBlank(p.titulo)) {
            String t = safeCallString(row, "titulo", "getTitulo", "proyecto", "getProyecto", "nombre", "getNombre");
            if (!isBlank(t)) p.titulo = t;
        }

        if (isBlank(p.estudiantes)) {
            // Intenta List<String>
            List<String> lista = safeCallList(row, "estudiantes", "getEstudiantes", "getListaEstudiantes");
            if (lista != null && !lista.isEmpty()) {
                p.estudiantes = String.join(", ", lista);
            } else {
                // Intenta un String ya unido
                String joined = safeCallString(
                        row,
                        "estudiantesStr", "getEstudiantesStr",
                        "nombresEstudiantes", "getNombresEstudiantes",
                        "estudiantesCadena", "getEstudiantesCadena"
                );
                if (!isBlank(joined)) p.estudiantes = joined;
            }
        }

        if (isBlank(p.docente)) {
            String dir = safeCallString(
                    row,
                    "director", "getDirector",
                    "docente", "getDocente",
                    "docenteEncargado", "getDocenteEncargado",
                    "asesor", "getAsesor"
            );
            if (!isBlank(dir)) p.docente = dir;
        }
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    // ==================== Helpers de reflexión ====================

    private static String safeCallString(Object target, String... methodNames) {
        for (String name : methodNames) {
            try {
                var m = target.getClass().getMethod(name);
                m.setAccessible(true);
                Object val = m.invoke(target);
                if (val instanceof String s) return s;
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                // Ignora y prueba el siguiente nombre
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<String> safeCallList(Object target, String... methodNames) {
        for (String name : methodNames) {
            try {
                var m = target.getClass().getMethod(name);
                m.setAccessible(true);
                Object val = m.invoke(target);
                if (val instanceof List<?> list) {
                    List<String> out = new ArrayList<>();
                    for (Object o : list) if (o != null) out.add(String.valueOf(o));
                    return out;
                }
            } catch (NoSuchMethodException ignored) {
            } catch (Exception e) {
                // Ignora y prueba el siguiente nombre
            }
        }
        return null;
    }

    // ==================== Card con sombra/borde azul ====================

    /** Card blanca con sombra suave y borde azul sutil */
    static class SoftCard extends JPanel {
        SoftCard() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int arc = 14;

            // sombra suave
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(6, 8, w - 12, h - 12, arc, arc);

            // cuerpo
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 2, w - 12, h - 12, arc, arc);

            // borde azul sutil
            g2.setColor(new Color(120, 158, 196));
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawRoundRect(0, 2, w - 12, h - 12, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
