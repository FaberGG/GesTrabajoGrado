package co.unicauca.gestiontrabajogrado.presentation.dashboard.docenteview;

import java.awt.*;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import co.unicauca.gestiontrabajogrado.presentation.common.GradientePanel;
import co.unicauca.gestiontrabajogrado.presentation.common.DropFileField;
import co.unicauca.gestiontrabajogrado.dto.ProyectoGradoRequestDTO;
import co.unicauca.gestiontrabajogrado.domain.model.enumModalidad;

public class SubirPropuestaModal extends JPanel {
    private static final Color C_BORDE_SUAVE = new Color(220, 220, 220);
    private static final Color C_ROJO_1 = new Color(166, 15, 21);
    private static final Color C_ROJO_2 = new Color(204, 39, 29);
    private static final Font F_H2 = new Font("SansSerif", Font.BOLD, 22);
    private static final Font F_H3 = new Font("SansSerif", Font.BOLD, 16);
    private static final Font F_BODY = new Font("SansSerif", Font.PLAIN, 14);

    // Enum del combo (texto)
    private enum Modalidad {
        SELECCION("Seleccione modalidad"),
        PLAN_COTERMINAL("Plan Coterminal"),
        INVESTIGACION("Investigación"),
        PRACTICA("Práctica profesional");
        final String label;
        Modalidad(String l) { this.label = l; }
        @Override public String toString() { return label; }
    }

    // Campos del formulario
    final JTextField tfTitulo = text("Ingrese el título del proyecto de grado");
    final JComboBox<Modalidad> cbModalidad = createModalidadComboBox();
    final DatePickerField dpFecha = new DatePickerField();
    final JTextField tfDirId = text("ID del director (ej: 123456)");
    final JTextField tfCoDirId = text("ID del codirector (opcional)");
    final JTextField tfEstudiante1Id = text("ID del estudiante 1 *");
    final JTextField tfEstudiante2Id = text("ID del estudiante 2 (solo para Investigación)");
    final JTextArea taObjGeneral = area("Describe el objetivo general…");
    final JTextArea taObjEspecificos = area("Describe los objetivos específicos…");

    final DropFileField dfFormatoA = new DropFileField();
    final DropFileField dfCarta = new DropFileField();

    private Runnable onSubmitValid = () -> {};
    private Runnable onCancel = () -> {};

    private static JComboBox<Modalidad> createModalidadComboBox() {
        javax.swing.DefaultComboBoxModel<Modalidad> model =
                new javax.swing.DefaultComboBoxModel<>(Modalidad.values());
        JComboBox<Modalidad> combo = new JComboBox<>(model);
        combo.setSelectedItem(Modalidad.SELECCION);
        combo.setFont(F_BODY);
        combo.setBackground(Color.WHITE);
        combo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(C_BORDE_SUAVE, 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        combo.setLightWeightPopupEnabled(false);
        combo.setMaximumRowCount(6);
        return combo;
    }

    public SubirPropuestaModal() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(new javax.swing.border.LineBorder(C_BORDE_SUAVE, 1, true));

        // Header
        JPanel header = new GradientePanel(C_ROJO_1, C_ROJO_2, 16);
        header.setLayout(new BorderLayout());
        JLabel title = new JLabel("NUEVA PROPUESTA DE PROYECTO DE GRADO", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(F_H2.deriveFont(22f));
        title.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton btnX = new JButton("✕");
        btnX.setForeground(Color.WHITE);
        btnX.setOpaque(false);
        btnX.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10));
        btnX.setContentAreaFilled(false);
        btnX.setFont(F_H3);
        btnX.addActionListener(e -> onCancel.run());

        header.add(title, BorderLayout.CENTER);
        header.add(btnX, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Formulario
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(16, 18, 16, 18));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        int y = 0;

        addFull(form, c, y++, "Título del Proyecto *", tfTitulo);
        addPair(form, c, y++, "Modalidad *", cbModalidad, "Fecha Actual *", dpFecha);
        addPair(form, c, y++, "ID Director *", tfDirId, "ID Codirector (opcional)", tfCoDirId);
        addPair(form, c, y++, "ID Estudiante 1 *", tfEstudiante1Id, "ID Estudiante 2", tfEstudiante2Id);
        addFull(form, c, y++, "Objetivo General *", taScroll(taObjGeneral));
        addFull(form, c, y++, "Objetivos Específicos *", taScroll(taObjEspecificos));

        addFull(form, c, y++, "Formato A (PDF) *", dfFormatoA);
        dfFormatoA.setLine1("✎  Arrastre el archivo aquí o haga clic para seleccionar");
        dfFormatoA.setLine2("Solo un archivo PDF");

        addFull(form, c, y++, "Carta de Aceptación de la empresa (PDF)", dfCarta);
        dfCarta.setLine1("✎  Arrastre el archivo aquí o haga clic para seleccionar");
        dfCarta.setLine2("Solo un archivo PDF");

        JScrollPane sc = new JScrollPane(form);
        sc.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        sc.getViewport().setBackground(Color.WHITE);
        add(sc, BorderLayout.CENTER);

        // Botones de acción
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 12));
        actions.setOpaque(false);

        JButton btnCancelar = createButton("Cancelar", new Color(140, 140, 140), new Color(120, 120, 120));
        btnCancelar.putClientProperty("role", "cancel");
        btnCancelar.addActionListener(e -> onCancel.run());

        JButton btnEnviar = createButton("Enviar Propuesta", C_ROJO_1, C_ROJO_2);
        btnEnviar.addActionListener(e -> {
            if (validar()) onSubmitValid.run();
        });

        actions.add(btnCancelar);
        actions.add(btnEnviar);
        add(actions, BorderLayout.SOUTH);

        actualizarCamposSegunModalidad();
        cbModalidad.addActionListener(e -> actualizarCamposSegunModalidad());
    }

    // ========== MÉTODO PRINCIPAL: Construir DTO ==========
    public ProyectoGradoRequestDTO construirDTO() {
        ProyectoGradoRequestDTO dto = new ProyectoGradoRequestDTO();
        dto.titulo = tfTitulo.getText().trim();
        dto.modalidad = toEnumModalidad(getModalidad());
        dto.objetivoGeneral = taObjGeneral.getText().trim();
        dto.objetivosEspecificos = taObjEspecificos.getText().trim();

        // Helper para parsear IDs de forma segura
        dto.directorId = parseIdSafely(tfDirId);
        dto.codirectorId = parseIdSafely(tfCoDirId);
        dto.estudiante1Id = parseIdSafely(tfEstudiante1Id);
        dto.estudiante2Id = parseIdSafely(tfEstudiante2Id);

        return dto;
    }

    private Integer parseIdSafely(JTextField field) {
        if (field == null) return null;

        String text = field.getText();
        if (text == null || text.trim().isEmpty()) return null;

        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ========== Validación ==========
    private boolean validar() {
        StringBuilder sb = new StringBuilder();

        if (isEmpty(tfTitulo.getText()))
            sb.append("• Título del proyecto es obligatorio.\n");

        Modalidad m = (Modalidad) cbModalidad.getSelectedItem();
        if (m == null || m == Modalidad.SELECCION)
            sb.append("• Selecciona una modalidad.\n");

        if (isEmpty(dpFecha.getDateString()))
            sb.append("• Fecha actual es obligatoria.\n");

        if (isEmpty(tfDirId.getText()))
            sb.append("• ID del director es obligatorio.\n");
        else {
            try {
                Integer.parseInt(tfDirId.getText().trim());
            } catch (NumberFormatException e) {
                sb.append("• ID del director debe ser un número válido.\n");
            }
        }

        // Validar codirector si no está vacío
        String codirId = tfCoDirId.getText().trim();
        if (!codirId.isEmpty()) {
            try {
                Integer.parseInt(codirId);
            } catch (NumberFormatException e) {
                sb.append("• ID del codirector debe ser un número válido.\n");
            }
        }
        if (isEmpty(tfEstudiante1Id.getText()))
            sb.append("• ID del Estudiante 1 es obligatorio.\n");
        else {
            try {
                Integer.parseInt(tfEstudiante1Id.getText().trim());
            } catch (NumberFormatException e) {
                sb.append("• ID del Estudiante 1 debe ser un número válido.\n");
            }
        }

        // NUEVO: Validar estudiante 2 según modalidad
        String est2Id = tfEstudiante2Id.getText().trim();
        if (!est2Id.isEmpty()) {
            try {
                Integer.parseInt(est2Id);
            } catch (NumberFormatException e) {
                sb.append("• ID del Estudiante 2 debe ser un número válido.\n");
            }
        }
        if (m == Modalidad.INVESTIGACION) {
            // Investigación puede tener 1 o 2 estudiantes (está ok)
        } else {
            // Otras modalidades solo permiten 1 estudiante
            if (!est2Id.isEmpty()) {
                sb.append("• Solo la modalidad Investigación permite 2 estudiantes.\n");
            }
        }

        if (isEmpty(taObjGeneral.getText()))
            sb.append("• Objetivo General es obligatorio.\n");

        if (isEmpty(taObjEspecificos.getText()))
            sb.append("• Objetivos Específicos son obligatorios.\n");

        if (!dfFormatoA.hasFile())
            sb.append("• Debes adjuntar el Formato A (PDF).\n");

        if (m == Modalidad.PRACTICA && !dfCarta.hasFile())
            sb.append("• Debes adjuntar la Carta de Aceptación (PDF) para Práctica profesional.\n");

        if (sb.length() > 0) {
            JOptionPane.showMessageDialog(this, sb.toString(),
                    "Campos obligatorios", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    // ========== Mapeo a enum del dominio ==========
    private static enumModalidad toEnumModalidad(String etiqueta) {
        if (etiqueta == null) return null;
        String s = Normalizer.normalize(etiqueta, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase().trim();
        //if (s.startsWith("plan")) return enumModalidad.PLAN_COTERMINAL;
        if (s.startsWith("invest")) return enumModalidad.INVESTIGACION;
        if (s.startsWith("practica")) return enumModalidad.PRACTICA_PROFESIONAL;
        return null;
    }

    // ========== Helpers UI ==========
    private void actualizarCamposSegunModalidad() {
        Modalidad m = (Modalidad) cbModalidad.getSelectedItem();
        boolean req = (m == Modalidad.PRACTICA);
        dfCarta.setEnabled(req);
        if (req) {
            dfCarta.setLine1("✎  Arrastre el archivo aquí o haga clic para seleccionar (REQUERIDO)");
            dfCarta.setLine2("Solo un archivo PDF - Obligatorio para Práctica profesional");
        } else {
            dfCarta.setLine1("✎  Arrastre el archivo aquí o haga clic para seleccionar");
            dfCarta.setLine2("Solo un archivo PDF - Opcional para esta modalidad");
        }
        boolean permiteSegundoEstudiante = (m == Modalidad.INVESTIGACION);
        tfEstudiante2Id.setEnabled(permiteSegundoEstudiante);

        if (permiteSegundoEstudiante) {
            tfEstudiante2Id.putClientProperty("JTextField.placeholderText",
                    "ID del estudiante 2 (opcional)");
        } else {
            tfEstudiante2Id.setText(""); // Limpiar si cambia de modalidad
            tfEstudiante2Id.putClientProperty("JTextField.placeholderText",
                    "Solo disponible para Investigación");
        }
        revalidate();
        repaint();
    }

    private static JTextField text(String placeholder) {
        JTextField tf = new JTextField();
        tf.putClientProperty("JTextField.placeholderText", placeholder);
        tf.setFont(F_BODY);
        tf.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(C_BORDE_SUAVE, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        return tf;
    }

    private static JTextArea area(String ph) {
        JTextArea ta = new JTextArea(3, 20);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setFont(F_BODY);
        ta.setBorder(new EmptyBorder(6, 6, 6, 6));
        ta.putClientProperty("JTextArea.placeholderText", ph);
        return ta;
    }

    private static JScrollPane taScroll(JTextArea ta) {
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new javax.swing.border.LineBorder(C_BORDE_SUAVE, 1, true),
                new EmptyBorder(4, 6, 4, 6)));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    private static void label(JPanel form, GridBagConstraints c, int x, int y, String text) {
        GridBagConstraints l = (GridBagConstraints) c.clone();
        l.gridx = x;
        l.gridy = y;
        l.weightx = 0.0;
        JLabel jl = new JLabel(text);
        jl.setFont(F_H3);
        form.add(jl, l);
    }

    private static void field(JPanel form, GridBagConstraints c, int x, int y, Component comp) {
        GridBagConstraints f = (GridBagConstraints) c.clone();
        f.gridx = x;
        f.gridy = y;
        f.weightx = 1.0;
        form.add(comp, f);
    }

    private static void addPair(JPanel form, GridBagConstraints c, int y,
                                String l1, Component f1, String l2, Component f2) {
        label(form, c, 0, y, l1);
        field(form, c, 1, y, f1);
        label(form, c, 2, y, l2);
        field(form, c, 3, y, f2);
    }

    private static void addFull(JPanel form, GridBagConstraints c, int y, String l, Component comp) {
        label(form, c, 0, y, l);
        GridBagConstraints f = (GridBagConstraints) c.clone();
        f.gridx = 1;
        f.gridy = y;
        f.gridwidth = 3;
        f.weightx = 1.0;
        form.add(comp, f);
    }

    private JButton createButton(String txt, Color c, Color h) {
        JButton btn = new JButton(txt) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = isEnabled() ? (getModel().isRollover() ? h : c) : new Color(170, 170, 170);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(F_BODY.deriveFont(Font.BOLD));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ========== Getters públicos ==========
    public void setOnSubmitValid(Runnable r) {
        this.onSubmitValid = (r != null) ? r : () -> {};
    }

    public void setOnCancel(Runnable r) {
        this.onCancel = (r != null) ? r : () -> {};
    }

    public void reset() {
        tfTitulo.setText("");
        cbModalidad.setSelectedItem(Modalidad.SELECCION);
        dpFecha.clear();
        tfDirId.setText("");
        tfCoDirId.setText("");
        taObjGeneral.setText("");
        taObjEspecificos.setText("");
        dfFormatoA.clear();
        dfCarta.clear();
        actualizarCamposSegunModalidad();
    }

    public java.io.File getFormatoA() {
        return dfFormatoA.getFile();
    }

    public java.io.File getCartaEmpresa() {
        return dfCarta.getFile();
    }

    public String getModalidad() {
        Modalidad m = (Modalidad) cbModalidad.getSelectedItem();
        return m == null ? "" : m.label;
    }

    public String getFecha() {
        return dpFecha.getDateString();
    }

    // ========== Clase interna: DatePickerField ==========
    private static class DatePickerField extends JPanel {
        private final JTextField tf = new JTextField();
        private final JButton btn = new JButton("📅");
        private final SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");

        DatePickerField() {
            super(new BorderLayout(4, 0));
            setOpaque(false);

            tf.putClientProperty("JTextField.placeholderText", "MM/DD/YYYY");
            tf.setFont(F_BODY);
            tf.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    new javax.swing.border.LineBorder(C_BORDE_SUAVE, 1, true),
                    new EmptyBorder(8, 10, 8, 10)));

            btn.setBorder(new EmptyBorder(6, 8, 6, 8));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            add(tf, BorderLayout.CENTER);
            add(btn, BorderLayout.EAST);

            btn.addActionListener(e -> showPopup());
        }

        private void showPopup() {
            JPopupMenu pm = new JPopupMenu();
            pm.setBorder(new javax.swing.border.LineBorder(C_BORDE_SUAVE, 1, true));

            SpinnerDateModel model = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
            JSpinner spinner = new JSpinner(model);
            JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "MM/dd/yyyy");
            spinner.setEditor(editor);

            JButton btnHoy = new JButton("Hoy");
            btnHoy.addActionListener(e -> spinner.setValue(new Date()));

            JButton btnOk = new JButton("Aceptar");
            btnOk.addActionListener(e -> {
                Date d = (Date) spinner.getValue();
                tf.setText(fmt.format(d));
                pm.setVisible(false);
            });

            JPanel content = new JPanel(new BorderLayout(8, 8));
            content.setBorder(new EmptyBorder(8, 8, 8, 8));
            content.add(spinner, BorderLayout.CENTER);

            JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            south.add(btnHoy);
            south.add(btnOk);
            content.add(south, BorderLayout.SOUTH);

            pm.add(content);
            pm.show(this, getWidth() - 220, getHeight());
        }

        String getDateString() {
            return tf.getText();
        }

        void clear() {
            tf.setText("");
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            tf.setEnabled(enabled);
            btn.setEnabled(enabled);
        }
    }
}