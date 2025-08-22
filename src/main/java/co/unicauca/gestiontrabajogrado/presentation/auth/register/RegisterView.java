package co.unicauca.gestiontrabajogrado.presentation.auth.register;


import co.unicauca.gestiontrabajogrado.presentation.common.UIConstants;
import co.unicauca.gestiontrabajogrado.presentation.common.HeaderPanel;
import co.unicauca.gestiontrabajogrado.domain.model.enumProgram;
import co.unicauca.gestiontrabajogrado.domain.model.enumRol;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Formulario de registro para el sistema de gestión de trabajo de grado
 * @author Lyz
 */
public class RegisterView extends JFrame {

    private JTextField nombresField;
    private JTextField apellidosField;
    private JTextField celularField;
    private JComboBox<ProgramItem> programaComboBox;
    private JComboBox<RolItem> rolComboBox;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JLabel volverLoginLabel;
    private RegisterController controller;

    public RegisterView() {
        super("Registrarse - Gestión del Proceso de Trabajo de Grado");
        initializeFrame();
        createComponents();
        setupLayout();
        setupEventListeners();
    }

    /**
     * Constructor que recibe el controller (inyección de dependencia)
     * @param controller Controlador para manejar la lógica de registro
     */
    public RegisterView(RegisterController controller) {
        this();
        this.controller = controller;
    }

    /**
     * Establece el controller después de la construcción
     * @param controller Controlador para manejar la lógica de registro
     */
    public void setController(RegisterController controller) {
        this.controller = controller;
    }

    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 900));
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createComponents() {
        // Campo de nombres
        nombresField = createStyledTextField();

        // Campo de apellidos
        apellidosField = createStyledTextField();

        // Campo de celular (opcional)
        celularField = createStyledTextField();

        // ComboBox para programa
        programaComboBox = createProgramComboBox();

        // ComboBox para rol
        rolComboBox = createRolComboBox();

        // Campo de email institucional
        emailField = createStyledTextField();

        // Campo de contraseña
        passwordField = createStyledPasswordField();

        // Campo de confirmar contraseña
        confirmPasswordField = createStyledPasswordField();

        // Botón de registro con sombra y ancho consistente
        registerButton = new JButton("Registrarse") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sombra del botón
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(1, 2, getWidth() - 1, getHeight() - 1, 8, 8);

                // Fondo del botón
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        registerButton.setBackground(UIConstants.BLUE_MAIN);
        registerButton.setForeground(Color.WHITE);
        registerButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setContentAreaFilled(false);
        registerButton.setOpaque(true);
        registerButton.setPreferredSize(new Dimension(640, 45));
        registerButton.setMaximumSize(new Dimension(640, 45));

        // Enlace "Volver al Login"
        volverLoginLabel = new JLabel("<html><u>¿Ya tienes cuenta? Iniciar Sesión</u></html>");
        volverLoginLabel.setFont(UIConstants.SMALL);
        volverLoginLabel.setForeground(UIConstants.BLUE_MAIN);
        volverLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(UIConstants.BODY);
        field.setBackground(UIConstants.CARD_BG);
        field.setForeground(UIConstants.TEXT_PRIMARY);
        field.setBorder(createRoundedBorder(new Color(0xCED4DA), false));
        field.setPreferredSize(new Dimension(310, 40));
        field.setMaximumSize(new Dimension(310, 40));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(UIConstants.BODY);
        field.setBackground(UIConstants.CARD_BG);
        field.setForeground(UIConstants.TEXT_PRIMARY);
        field.setBorder(createRoundedBorder(new Color(0xCED4DA), false));
        field.setPreferredSize(new Dimension(310, 40));
        field.setMaximumSize(new Dimension(310, 40));
        return field;
    }

    private JComboBox<ProgramItem> createProgramComboBox() {
        ProgramItem[] programItems = {
                new ProgramItem(null, "Selecciona un programa"),
                new ProgramItem(enumProgram.INGENIERIA_DE_SISTEMAS, "Ingeniería de Sistemas"),
                new ProgramItem(enumProgram.INGENIERIA_ELECTRONICA_Y_TELECOMUNICACIONES, "Ingeniería Electrónica y Telecomunicaciones"),
                new ProgramItem(enumProgram.AUTOMATICA_INDUSTRIAL, "Automática Industrial"),
                new ProgramItem(enumProgram.TECNOLOGIA_EN_TELEMATICA, "Tecnología en Telemática")
        };

        JComboBox<ProgramItem> comboBox = new JComboBox<>(programItems);
        comboBox.setFont(UIConstants.BODY);
        comboBox.setBackground(UIConstants.CARD_BG);
        comboBox.setForeground(UIConstants.TEXT_PRIMARY);
        comboBox.setBorder(createRoundedBorder(new Color(0xCED4DA), false));
        comboBox.setPreferredSize(new Dimension(310, 40));
        comboBox.setMaximumSize(new Dimension(310, 40));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ProgramItem) {
                    setText(((ProgramItem) value).getDisplayName());
                }
                return this;
            }
        });
        return comboBox;
    }

    private JComboBox<RolItem> createRolComboBox() {
        RolItem[] rolItems = {
                new RolItem(null, "Selecciona un rol"),
                new RolItem(enumRol.ESTUDIANTE, "Estudiante"),
                new RolItem(enumRol.DOCENTE, "Docente")
        };

        JComboBox<RolItem> comboBox = new JComboBox<>(rolItems);
        comboBox.setFont(UIConstants.BODY);
        comboBox.setBackground(UIConstants.CARD_BG);
        comboBox.setForeground(UIConstants.TEXT_PRIMARY);
        comboBox.setBorder(createRoundedBorder(new Color(0xCED4DA), false));
        comboBox.setPreferredSize(new Dimension(310, 40));
        comboBox.setMaximumSize(new Dimension(310, 40));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof RolItem) {
                    setText(((RolItem) value).getDisplayName());
                }
                return this;
            }
        });
        return comboBox;
    }

    // Método para crear bordes redondeados con sombra interna sutil
    private javax.swing.border.Border createRoundedBorder(Color borderColor, boolean focused) {
        return new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sombra interna muy sutil
                if (!focused) {
                    g2.setColor(new Color(0, 0, 0, 3));
                    g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, 7, 7);
                }

                // Borde principal
                g2.setColor(focused ? UIConstants.BLUE_MAIN : borderColor);
                g2.setStroke(new BasicStroke(focused ? 2.0f : 1.0f));
                g2.drawRoundRect(x, y, width - 1, height - 1, 8, 8);
                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(10, 15, 10, 15);
            }
        };
    }

    private void setupLayout() {
        // Root panel
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.BG_APP);
        setContentPane(root);

        // Header
        HeaderPanel header = new HeaderPanel();
        root.add(header, BorderLayout.NORTH);

        // Main content panel con scroll
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIConstants.BG_APP);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Register card panel
        JPanel registerCard = createRegisterCard();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(registerCard, gbc);

        // Scroll pane para el contenido
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        root.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createRegisterCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sombra sutil para el card
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 12, 12);

                // Fondo del card
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(45, 50, 45, 50));
        card.setPreferredSize(new Dimension(800, 600));
        card.setOpaque(false);

        // Título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setBackground(UIConstants.CARD_BG);
        titlePanel.setOpaque(false);
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Crear Cuenta");
        titleLabel.setFont(UIConstants.H1);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titlePanel.add(titleLabel);

        card.add(titlePanel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));

        // Subtítulo
        JPanel subtitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        subtitlePanel.setBackground(UIConstants.CARD_BG);
        subtitlePanel.setOpaque(false);
        subtitlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Completa la información para registrarte");
        subtitleLabel.setFont(UIConstants.BODY);
        subtitleLabel.setForeground(UIConstants.TEXT_MUTED);
        subtitlePanel.add(subtitleLabel);

        card.add(subtitlePanel);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        // Panel principal para las dos columnas
        JPanel formPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        formPanel.setBackground(UIConstants.CARD_BG);
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Columna izquierda
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(UIConstants.CARD_BG);
        leftColumn.setOpaque(false);

        addFormFieldToColumn(leftColumn, "Nombres *", nombresField);
        addFormFieldToColumn(leftColumn, "Apellidos *", apellidosField);
        addFormFieldToColumn(leftColumn, "Número de Celular", celularField);
        addFormFieldToColumn(leftColumn, "Programa Académico *", programaComboBox);

        // Columna derecha
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(UIConstants.CARD_BG);
        rightColumn.setOpaque(false);

        addFormFieldToColumn(rightColumn, "Rol *", rolComboBox);
        addFormFieldToColumn(rightColumn, "Email Institucional *", emailField);
        addFormFieldToColumn(rightColumn, "Contraseña *", passwordField);
        addFormFieldToColumn(rightColumn, "Confirmar Contraseña *", confirmPasswordField);

        // Agregar columnas al panel del formulario
        formPanel.add(leftColumn);
        formPanel.add(rightColumn);

        card.add(formPanel);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        // Panel para el botón centrado
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBackground(UIConstants.CARD_BG);
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(registerButton);

        card.add(buttonPanel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        // Panel para el enlace centrado
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        linkPanel.setBackground(UIConstants.CARD_BG);
        linkPanel.setOpaque(false);
        linkPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        linkPanel.add(volverLoginLabel);

        card.add(linkPanel);

        return card;
    }

    /**
     * Método para agregar campos del formulario a una columna específica
     */
    private void addFormFieldToColumn(JPanel column, String labelText, Component field) {
        // Panel contenedor para el campo
        JPanel fieldContainer = new JPanel();
        fieldContainer.setLayout(new BoxLayout(fieldContainer, BoxLayout.Y_AXIS));
        fieldContainer.setBackground(UIConstants.CARD_BG);
        fieldContainer.setOpaque(false);
        fieldContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(UIConstants.TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel para el label con alineación correcta
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setBackground(UIConstants.CARD_BG);
        labelPanel.setOpaque(false);
        labelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelPanel.setMaximumSize(new Dimension(310, 25));
        labelPanel.setPreferredSize(new Dimension(310, 25));
        labelPanel.add(label);

        // Ajustar el tamaño de los campos para las columnas
        field.setPreferredSize(new Dimension(310, 40));
        field.setMaximumSize(new Dimension(310, 40));

        // Panel para el campo centrado
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        fieldPanel.setBackground(UIConstants.CARD_BG);
        fieldPanel.setOpaque(false);
        fieldPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldPanel.add(field);

        // Agregar al contenedor
        fieldContainer.add(labelPanel);
        fieldContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        fieldContainer.add(fieldPanel);

        // Agregar al panel de la columna
        column.add(fieldContainer);
        column.add(Box.createRigidArea(new Dimension(0, 18)));
    }

    private void setupEventListeners() {
        // Acción del botón de registro
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        // Hover effect para el botón
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(UIConstants.BLUE_DARK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(UIConstants.BLUE_MAIN);
            }
        });

        // Focus listeners para los campos de texto
        setupFieldFocusListeners(nombresField);
        setupFieldFocusListeners(apellidosField);
        setupFieldFocusListeners(celularField);
        setupFieldFocusListeners(emailField);
        setupFieldFocusListeners(passwordField);
        setupFieldFocusListeners(confirmPasswordField);

        // Click en "Volver al Login"
        volverLoginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleVolverLogin();
            }
        });
    }

    private void setupFieldFocusListeners(JComponent field) {
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(createRoundedBorder(UIConstants.BLUE_MAIN, true));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(createRoundedBorder(new Color(0xCED4DA), false));
            }
        });

        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!field.hasFocus()) {
                    field.setBorder(createRoundedBorder(new Color(0xADB5BD), false));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!field.hasFocus()) {
                    field.setBorder(createRoundedBorder(new Color(0xCED4DA), false));
                }
            }
        });
    }

    private void handleRegister() {
        if (controller == null) {
            showError("Error interno: Controlador no inicializado.");
            return;
        }
        // Delegar toda la lógica al controlador
        controller.handleRegister(
            getNombres(),
            getApellidos(),
            getCelular(),
            getSelectedProgram(),
            getSelectedRol(),
            getEmail(),
            getPassword(),
            getConfirmPassword()
        );
    }

    private void handleVolverLogin() {
        if (controller != null) {
            controller.handleVolverLogin();
        } else {
            // Confirmación antes de cerrar si no hay controller
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "¿Estás seguro de que quieres salir?\n" +
                            "Se perderá la información ingresada.",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (option == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    try {
                        Class<?> loginViewClass = Class.forName("co.unicauca.gestiontrabajogrado.presentation.auth.login.LoginView");
                        JFrame loginView = (JFrame) loginViewClass.getDeclaredConstructor().newInstance();
                        loginView.setVisible(true);
                    } catch (Exception e) {
                        System.err.println("Error al abrir LoginView: " + e.getMessage());
                        System.exit(0); // Salir de la aplicación si no puede volver al login
                    }
                });
            }
        }
    }

    // Métodos públicos para que el controller pueda mostrar mensajes
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    // Getters para que el controller pueda acceder a los datos
    public String getNombres() {
        return nombresField.getText().trim();
    }

    public String getApellidos() {
        return apellidosField.getText().trim();
    }

    public String getCelular() {
        return celularField.getText().trim();
    }

    public enumProgram getSelectedProgram() {
        ProgramItem selected = (ProgramItem) programaComboBox.getSelectedItem();
        return selected != null ? selected.getEnumValue() : null;
    }

    public enumRol getSelectedRol() {
        RolItem selected = (RolItem) rolComboBox.getSelectedItem();
        return selected != null ? selected.getEnumValue() : null;
    }

    public String getEmail() {
        return emailField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getConfirmPassword() {
        return new String(confirmPasswordField.getPassword());
    }

    // Clases internas para los items de los ComboBox
    private static class ProgramItem {
        private final enumProgram enumValue;
        private final String displayName;

        public ProgramItem(enumProgram enumValue, String displayName) {
            this.enumValue = enumValue;
            this.displayName = displayName;
        }

        public enumProgram getEnumValue() {
            return enumValue;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private static class RolItem {
        private final enumRol enumValue;
        private final String displayName;

        public RolItem(enumRol enumValue, String displayName) {
            this.enumValue = enumValue;
            this.displayName = displayName;
        }

        public enumRol getEnumValue() {
            return enumValue;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public static void main(String[] args) {
        // Look and feel del sistema para que se vea moderno
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> new RegisterView().setVisible(true));
    }
}