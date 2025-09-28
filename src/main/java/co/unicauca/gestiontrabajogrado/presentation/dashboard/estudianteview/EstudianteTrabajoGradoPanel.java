package co.unicauca.gestiontrabajogrado.presentation.dashboard.estudianteview;

import co.unicauca.gestiontrabajogrado.dto.ProyectoGradoResponseDTO;
import co.unicauca.gestiontrabajogrado.presentation.common.UIConstants;
import co.unicauca.gestiontrabajogrado.controller.EstudianteController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EstudianteTrabajoGradoPanel extends JPanel {
    private EstudianteController controller;
    private EstudianteView parentView;
    private JPanel scrollableContent;

    public EstudianteTrabajoGradoPanel(EstudianteController controller, EstudianteView parentView) {
        this.controller = controller;
        this.parentView = parentView;
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG_APP);

        // Panel principal con padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        mainPanel.setBackground(UIConstants.BG_APP);
        add(mainPanel, BorderLayout.CENTER);

        // Panel superior que contendrá path + encabezado
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIConstants.BG_APP);

        // Path breadcrumb mejorado con tamaño aumentado
        JLabel pathLabel = new JLabel("Inicio > Mi Trabajo de Grado");
        pathLabel.setForeground(Color.decode("#3388D1"));
        pathLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18)); // Aumentado de 14 a 18
        pathLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        topPanel.add(pathLabel, BorderLayout.NORTH);

        // Encabezado con gradiente - ALTURA REDUCIDA
        JPanel headerPanel = createGradientHeader();
        topPanel.add(headerPanel, BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Área scrollable
        createScrollableContent();
        JScrollPane scrollPane = new JScrollPane(scrollableContent);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(UIConstants.BG_APP);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Botón volver
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createGradientHeader() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                float[] fractions = {0.0f, 0.25f, 0.5f, 0.75f, 1.0f};
                Color[] colors = {
                        Color.decode("#861111"),
                        Color.decode("#9E0A0A"),
                        Color.decode("#B11F1F"),
                        Color.decode("#D52E2E"),
                        Color.decode("#E13030")
                };

                LinearGradientPaint gradient = new LinearGradientPaint(
                        0, 0, getWidth(), 0, fractions, colors);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };

        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setPreferredSize(new Dimension(0, 90)); // Reducido de 120 a 90
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Reducido padding

        JLabel titleLabel = new JLabel("Mi trabajo de Grado");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28)); // Reducido de 32 a 28
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Seguimiento del proceso");
        subtitleLabel.setForeground(new Color(255, 255, 255, 220));
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14)); // Reducido de 16 a 14
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(Box.createVerticalGlue());
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8)); // Reducido de 10 a 8
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalGlue());

        return headerPanel;
    }

    private void createScrollableContent() {
        scrollableContent = new JPanel();
        scrollableContent.setLayout(new BoxLayout(scrollableContent, BoxLayout.Y_AXIS));
        scrollableContent.setBackground(UIConstants.BG_APP);
        scrollableContent.setBorder(new EmptyBorder(30, 0, 30, 0));

        if (controller.tieneProyecto()) {
            // Contenedor centrado para las tarjetas
            JPanel centerContainer = new JPanel(new GridBagLayout());
            centerContainer.setBackground(UIConstants.BG_APP);

            JPanel contentWrapper = new JPanel();
            contentWrapper.setLayout(new BoxLayout(contentWrapper, BoxLayout.Y_AXIS));
            contentWrapper.setBackground(UIConstants.BG_APP);

            // Tarjeta de información del proyecto compacta
            JPanel projectInfoCard = createCompactProjectInfoCard();
            contentWrapper.add(projectInfoCard);
            contentWrapper.add(Box.createVerticalStrut(35));

            // Sección de seguimiento
            JPanel followUpSection = createFollowUpSection();
            contentWrapper.add(followUpSection);

            centerContainer.add(contentWrapper);
            scrollableContent.add(centerContainer);
        } else {
            // Mensaje de no proyecto centrado
            JPanel centerContainer = new JPanel(new GridBagLayout());
            centerContainer.setBackground(UIConstants.BG_APP);

            JPanel noProjectPanel = createStylizedNoProjectPanel();
            centerContainer.add(noProjectPanel);
            scrollableContent.add(centerContainer);
        }

        scrollableContent.revalidate();
        scrollableContent.repaint();
    }

    private JPanel createCompactProjectInfoCard() {
        ProyectoGradoResponseDTO proyecto = controller.getProyectoActual();

        // Panel con sombra
        JPanel shadowPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sombra suave
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 25, 25);
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 25, 25);

                g2d.dispose();
            }
        };
        shadowPanel.setLayout(new BorderLayout());
        shadowPanel.setOpaque(false);

        // Tarjeta principal con bordes estilizados
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo blanco
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // Borde izquierdo grueso (6px)
                g2d.setColor(Color.decode("#C44B4B"));
                g2d.fillRoundRect(0, 0, 6, getHeight(), 20, 20);

                // Borde general de 3px
                g2d.setColor(Color.decode("#C44B4B"));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);

                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(750, 240)); // Reducido de 280 a 240
        card.setPreferredSize(new Dimension(750, 240));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 35, 20, 35)); // Reducido padding vertical
        contentPanel.setOpaque(false);

        // Título del proyecto con icono
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setOpaque(false);

        JLabel projectIcon = new JLabel("📋");
        projectIcon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16)); // Reducido de 18 a 16
        projectIcon.setBorder(new EmptyBorder(0, 0, 0, 8));

        JLabel titleLabel = new JLabel(proyecto.titulo);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16)); // Reducido de 18 a 16
        titleLabel.setForeground(Color.decode("#2C2C2C"));

        titlePanel.add(projectIcon);
        titlePanel.add(titleLabel);
        contentPanel.add(titlePanel, BorderLayout.NORTH);

        // Panel de información mejorado con distribución uniforme
        JPanel mainInfoPanel = new JPanel(new BorderLayout());
        mainInfoPanel.setBackground(Color.WHITE);
        mainInfoPanel.setOpaque(false);
        mainInfoPanel.setBorder(new EmptyBorder(15, 15, 10, 15));

        // Grid superior (2x2) para información básica
        JPanel topInfoPanel = new JPanel(new GridLayout(2, 2, 25, 10));
        topInfoPanel.setBackground(Color.WHITE);
        topInfoPanel.setOpaque(false);

        topInfoPanel.add(createCompactInfoItem("📚 Modalidad",
                proyecto.modalidad != null ? proyecto.modalidad.toString() : "N/A"));
        topInfoPanel.add(createCompactInfoItem("🎓 Programa",
                controller.getCurrentUser().getPrograma() != null ?
                        controller.getCurrentUser().getPrograma().toString() : "N/A"));
        topInfoPanel.add(createCompactInfoItem("👨‍🏫 Director", controller.obtenerNombreDirector()));
        topInfoPanel.add(createCompactInfoItem("👨‍💼 Codirector", controller.obtenerNombreCodirector()));

        mainInfoPanel.add(topInfoPanel, BorderLayout.NORTH);

        // Panel de estudiantes centrado
        String estudiantes = controller.getCurrentUser().getNombres() + " " +
                controller.getCurrentUser().getApellidos();
        String estudiante2 = controller.obtenerNombreEstudiante2();
        if (estudiante2 != null) {
            estudiantes += ", " + estudiante2;
        }

        JPanel estudiantesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        estudiantesPanel.setBackground(Color.WHITE);
        estudiantesPanel.setOpaque(false);
        estudiantesPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel estudianteInfo = createCompactInfoItem("👥 Estudiantes", estudiantes);
        estudiantesPanel.add(estudianteInfo);

        mainInfoPanel.add(estudiantesPanel, BorderLayout.CENTER);

        contentPanel.add(mainInfoPanel, BorderLayout.CENTER);
        card.add(contentPanel, BorderLayout.CENTER);
        shadowPanel.add(card, BorderLayout.CENTER);

        return shadowPanel;
    }

    private JPanel createCompactInfoItem(String label, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setOpaque(false);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11)); // Reducido de 12 a 11
        labelComponent.setForeground(Color.decode("#666666"));
        labelComponent.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueComponent = new JLabel(value != null ? value : "N/A");
        valueComponent.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13)); // Reducido de 14 a 13
        valueComponent.setForeground(Color.decode("#2C2C2C"));
        valueComponent.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(labelComponent);
        panel.add(Box.createVerticalStrut(2));
        panel.add(valueComponent);

        return panel;
    }

    private JPanel createFollowUpSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(UIConstants.BG_APP);
        section.setMaximumSize(new Dimension(750, Integer.MAX_VALUE));

        // Estado actual mejorado
        JPanel currentStatePanel = createStylizedCurrentStatePanel();
        section.add(currentStatePanel);

        section.add(Box.createVerticalStrut(25));

        // Tarjetas de seguimiento
        JPanel trackingPanel = createTrackingPanel();
        section.add(trackingPanel);

        return section;
    }

    private JPanel createStylizedCurrentStatePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(UIConstants.BG_APP);

        JLabel statusLabel = new JLabel("Estado actual:");
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        statusLabel.setForeground(Color.decode("#2C2C2C"));

        // Contenedor del estado con diseño moderno
        JPanel statusContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradiente suave para el fondo
                GradientPaint gradient = new GradientPaint(0, 0, Color.decode("#F7EDED"),
                        0, getHeight(), Color.decode("#FAFAFA"));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Borde sutil
                g2d.setColor(new Color(196, 75, 75, 80));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);

                g2d.dispose();
            }
        };

        statusContainer.setLayout(new BorderLayout());
        statusContainer.setOpaque(false);
        statusContainer.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel statusValue = new JLabel(controller.obtenerEstadoActualTexto());
        statusValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        statusValue.setForeground(Color.decode("#B11F1F"));
        statusValue.setHorizontalAlignment(SwingConstants.CENTER);

        statusContainer.add(statusValue, BorderLayout.CENTER);

        panel.add(statusLabel);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(statusContainer);

        return panel;
    }

    private JPanel createTrackingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.BG_APP);

        List<TrackingItem> trackingItems = getTrackingItems();

        for (int i = 0; i < trackingItems.size(); i++) {
            TrackingItem item = trackingItems.get(i);
            JPanel trackingCard = createModernTrackingCard(item);
            panel.add(trackingCard);

            // Línea conectora más estilizada
            if (i < trackingItems.size() - 1) {
                JPanel connectorPanel = createStylizedConnectorLine();
                panel.add(connectorPanel);
            }

            panel.add(Box.createVerticalStrut(8));
        }

        return panel;
    }

    private List<TrackingItem> getTrackingItems() {
        List<TrackingItem> items = new ArrayList<>();
        ProyectoGradoResponseDTO proyecto = controller.getProyectoActual();

        // 1. Propuesta enviada
        items.add(new TrackingItem(
                "✓", TrackingItem.EstadoIcono.OK,
                "Propuesta enviada",
                "El docente ha enviado tu formato A para evaluación",
                formatearFecha(proyecto.fechaCreacion)
        ));

        // 2. Evaluaciones según el número de intentos
        for (int intento = 1; intento <= Math.max(proyecto.numeroIntentos, 1); intento++) {
            TrackingItem.EstadoIcono estadoItem = getEstadoIcono(proyecto, intento);
            String icono = getIconoParaEstado(estadoItem, intento, proyecto);
            String titulo = getTextoEvaluacion(intento);
            String descripcion = getDescripcionEvaluacion(intento, estadoItem);
            String fecha = getFechaEvaluacion(intento, estadoItem);

            items.add(new TrackingItem(icono, estadoItem, titulo, descripcion, fecha));

            if (estadoItem == TrackingItem.EstadoIcono.BAD && intento < proyecto.numeroIntentos) {
                items.add(new TrackingItem(
                        "⚠", TrackingItem.EstadoIcono.BAD,
                        getTextoRechazo(intento),
                        "Su propuesta ha sido rechazada y requiere correcciones",
                        "Completado"
                ));
            }
        }

        // 3. Estado final
        agregarEstadoFinal(items, proyecto);

        return items;
    }

    private String getIconoParaEstado(TrackingItem.EstadoIcono estado, int intento, ProyectoGradoResponseDTO proyecto) {
        switch (estado) {
            case OK: return "✓";
            case BAD: return "✗";
            case IN_PROGRESS: return "⏳";
            case NOT_STARTED:
            default: return "○";
        }
    }

    private String getDescripcionEvaluacion(int intento, TrackingItem.EstadoIcono estado) {
        String base = "El coordinador del programa está revisando tu propuesta";
        if (intento > 1) {
            base = "El coordinador está revisando tu propuesta actualizada";
        }

        if (estado == TrackingItem.EstadoIcono.OK) {
            return "Tu propuesta ha sido revisada y continúa al siguiente paso";
        } else if (estado == TrackingItem.EstadoIcono.BAD) {
            return "La evaluación ha sido completada con observaciones";
        } else if (estado == TrackingItem.EstadoIcono.NOT_STARTED) {
            return "Esta evaluación se realizará en el futuro";
        }

        return base;
    }

    private String getFechaEvaluacion(int intento, TrackingItem.EstadoIcono estado) {
        switch (estado) {
            case OK:
            case BAD: return "Completado";
            case IN_PROGRESS: return "En proceso...";
            case NOT_STARTED:
            default: return "Pendiente";
        }
    }

    private String getTextoRechazo(int intento) {
        switch (intento) {
            case 1: return "Primer rechazo";
            case 2: return "Segundo rechazo";
            case 3: return "Tercer rechazo";
            default: return "Rechazo";
        }
    }

    private void agregarEstadoFinal(List<TrackingItem> items, ProyectoGradoResponseDTO proyecto) {
        switch (proyecto.estado.toString()) {
            case "APROBADO":
                items.add(new TrackingItem(
                        "🎉", TrackingItem.EstadoIcono.OK,
                        "Propuesta Aceptada",
                        "Su propuesta de trabajo de grado ha sido aceptada por el comité",
                        "Completado"
                ));
                break;
            case "RECHAZADO_DEFINITIVO":
                items.add(new TrackingItem(
                        "❌", TrackingItem.EstadoIcono.BAD,
                        "Rechazo definitivo",
                        "Su propuesta ha sido rechazada definitivamente después de " + proyecto.numeroIntentos + " intentos",
                        formatearFecha(java.time.LocalDateTime.now())
                ));
                break;
            case "EN_PROCESO":
                if (proyecto.numeroIntentos < 3) {
                    items.add(new TrackingItem(
                            "○", TrackingItem.EstadoIcono.NOT_STARTED,
                            "Resultado de la evaluación",
                            "Pronto recibirás el resultado de la evaluación por correo electrónico",
                            "Pendiente"
                    ));
                }
                break;
            case "RECHAZADO":
                items.add(new TrackingItem(
                        "○", TrackingItem.EstadoIcono.NOT_STARTED,
                        "Siguiente evaluación",
                        "Podrás subir una nueva versión corregida de tu propuesta",
                        "Pendiente"
                ));
                break;
        }
    }

    private TrackingItem.EstadoIcono getEstadoIcono(ProyectoGradoResponseDTO proyecto, int evaluacion) {
        String estadoProyecto = proyecto.estado.toString();

        if (estadoProyecto.equals("APROBADO")) {
            if (evaluacion <= proyecto.numeroIntentos) {
                return TrackingItem.EstadoIcono.OK;
            }
        } else if (estadoProyecto.equals("RECHAZADO") || estadoProyecto.equals("RECHAZADO_DEFINITIVO")) {
            if (evaluacion < proyecto.numeroIntentos) {
                return TrackingItem.EstadoIcono.BAD;
            } else if (evaluacion == proyecto.numeroIntentos) {
                return TrackingItem.EstadoIcono.IN_PROGRESS;
            }
        } else if (estadoProyecto.equals("EN_PROCESO")) {
            if (evaluacion < proyecto.numeroIntentos) {
                return TrackingItem.EstadoIcono.OK;
            } else if (evaluacion == proyecto.numeroIntentos) {
                return TrackingItem.EstadoIcono.IN_PROGRESS;
            }
        }

        return TrackingItem.EstadoIcono.NOT_STARTED;
    }

    private String getTextoEvaluacion(int numeroIntento) {
        switch (numeroIntento) {
            case 1: return "Primera evaluación del formato A";
            case 2: return "Segunda evaluación del formato A";
            case 3: return "Tercera evaluación del formato A";
            default: return "Evaluación del formato A";
        }
    }

    private JPanel createModernTrackingCard(TrackingItem item) {
        // Panel con sombra
        JPanel shadowPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sombra más sutil y moderna
                g2d.setColor(new Color(0, 0, 0, 12));
                g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 20, 20);
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);

                g2d.dispose();
            }
        };
        shadowPanel.setLayout(new BorderLayout());
        shadowPanel.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo con gradiente sutil
                Color backgroundColor = getBackgroundColorForEstado(item.estadoIcono);
                if (item.estadoIcono == TrackingItem.EstadoIcono.IN_PROGRESS) {
                    GradientPaint gradient = new GradientPaint(0, 0, backgroundColor,
                            0, getHeight(), Color.WHITE);
                    g2d.setPaint(gradient);
                } else {
                    g2d.setColor(backgroundColor);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                // Borde sutil
                g2d.setColor(getBorderColorForEstado(item.estadoIcono));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);

                g2d.dispose();
            }
        };

        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setPreferredSize(new Dimension(0, 100));

        // Icono más grande y centrado
        JLabel iconLabel = new JLabel(item.icono);
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
        iconLabel.setForeground(getColorForEstado(item.estadoIcono));
        iconLabel.setPreferredSize(new Dimension(60, 60));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        card.add(iconLabel, BorderLayout.WEST);

        // Panel de texto mejorado
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        Color titleColor = getTitleColorForEstado(item.estadoIcono);
        Color descColor = getDescriptionColorForEstado(item.estadoIcono);
        Color dateColor = getDateColorForEstado(item.estadoIcono);

        JLabel titleLabel = new JLabel(item.titulo);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        titleLabel.setForeground(titleColor);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><p style='width: 400px;'>" + item.descripcion + "</p></html>");
        descLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        descLabel.setForeground(descColor);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel(item.fecha);
        dateLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        dateLabel.setForeground(dateColor);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(dateLabel);

        card.add(textPanel, BorderLayout.CENTER);
        shadowPanel.add(card, BorderLayout.CENTER);

        return shadowPanel;
    }

    private Color getBackgroundColorForEstado(TrackingItem.EstadoIcono estado) {
        switch (estado) {
            case OK: return Color.decode("#F0F8F0");
            case IN_PROGRESS: return Color.decode("#E8F4FD");
            case BAD: return Color.decode("#FDF2F2");
            case NOT_STARTED:
            default: return Color.decode("#FAFAFA");
        }
    }

    private Color getBorderColorForEstado(TrackingItem.EstadoIcono estado) {
        switch (estado) {
            case OK: return new Color(76, 175, 80, 100);
            case IN_PROGRESS: return new Color(33, 150, 243, 100);
            case BAD: return new Color(244, 67, 54, 100);
            case NOT_STARTED:
            default: return new Color(158, 158, 158, 80);
        }
    }

    private Color getTitleColorForEstado(TrackingItem.EstadoIcono estado) {
        switch (estado) {
            case NOT_STARTED: return Color.decode("#8B8B8B");
            default: return Color.decode("#2C2C2C");
        }
    }

    private Color getDescriptionColorForEstado(TrackingItem.EstadoIcono estado) {
        switch (estado) {
            case NOT_STARTED: return Color.decode("#AAAAAA");
            default: return Color.decode("#555555");
        }
    }

    private Color getDateColorForEstado(TrackingItem.EstadoIcono estado) {
        switch (estado) {
            case NOT_STARTED: return Color.decode("#BBBBBB");
            default: return Color.decode("#777777");
        }
    }

    private JPanel createStylizedConnectorLine() {
        JPanel connectorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Línea con gradiente
                GradientPaint gradient = new GradientPaint(0, 0, Color.decode("#E0E0E0"),
                        0, getHeight(), Color.decode("#F0F0F0"));
                g2d.setPaint(gradient);
                g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int x = 30;
                g2d.drawLine(x, 0, x, getHeight());
            }
        };
        connectorPanel.setPreferredSize(new Dimension(60, 15));
        connectorPanel.setBackground(UIConstants.BG_APP);
        return connectorPanel;
    }

    private Color getColorForEstado(TrackingItem.EstadoIcono estado) {
        switch (estado) {
            case OK: return Color.decode("#4CAF50");
            case BAD: return Color.decode("#F44336");
            case IN_PROGRESS: return Color.decode("#2196F3");
            case NOT_STARTED:
            default: return Color.decode("#9E9E9E");
        }
    }

    private JPanel createStylizedNoProjectPanel() {
        // Panel con sombra
        JPanel shadowPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 25, 25);

                g2d.dispose();
            }
        };
        shadowPanel.setLayout(new BorderLayout());
        shadowPanel.setOpaque(false);

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setColor(new Color(196, 75, 75, 60));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(600, 180));
        panel.setPreferredSize(new Dimension(600, 180));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(40, 30, 40, 30));

        JLabel iconLabel = new JLabel("📄");
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel("No tienes un trabajo de grado asignado actualmente");
        messageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        messageLabel.setForeground(Color.decode("#666666"));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(messageLabel);

        panel.add(contentPanel, BorderLayout.CENTER);
        shadowPanel.add(panel, BorderLayout.CENTER);

        return shadowPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(UIConstants.BG_APP);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton backButton = new JButton("← Volver al Dashboard") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                super.paintComponent(g);
                g2.dispose();
            }
        };

        backButton.setBackground(Color.decode("#6C757D"));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        backButton.setPreferredSize(new Dimension(180, 40));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.volverAlDashboard();
            }
        });

        // Efecto hover
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(Color.decode("#5A6268"));
                backButton.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(Color.decode("#6C757D"));
                backButton.repaint();
            }
        });

        buttonPanel.add(backButton);
        return buttonPanel;
    }

    private String formatearFecha(java.time.LocalDateTime fecha) {
        if (fecha == null) return "Fecha no disponible";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fecha.format(formatter);
    }

    // Clase helper para items de seguimiento
    private static class TrackingItem {
        public enum EstadoIcono { OK, BAD, IN_PROGRESS, NOT_STARTED }

        String icono;
        EstadoIcono estadoIcono;
        String titulo;
        String descripcion;
        String fecha;

        public TrackingItem(String icono, EstadoIcono estadoIcono, String titulo, String descripcion, String fecha) {
            this.icono = icono;
            this.estadoIcono = estadoIcono;
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.fecha = fecha;
        }
    }
}