package co.unicauca.gestiontrabajogrado.presentation.auth;

import co.unicauca.gestiontrabajogrado.controller.LoginController;
import co.unicauca.gestiontrabajogrado.presentation.common.UIConstants;
import co.unicauca.gestiontrabajogrado.presentation.common.HeaderPanel;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Formulario de inicio de sesión para el sistema de gestión de trabajo de grado
 * @author Lyz
 */
public class LoginView extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registrarseLabel;
    private JCheckBox rememberMeCheckBox;
    private LoginController controller;

    public LoginView() {
        super("Iniciar Sesión - Gestión del Proceso de Trabajo de Grado");
        initializeFrame();
        createComponents();
        setupLayout();
        setupEventListeners();
        loadRememberedData();
    }

    /**
     * Constructor que recibe el controller (inyección de dependencia)
     * @param controller Controlador para manejar la lógica de login
     */
    public LoginView(LoginController controller) {
        this();
        this.controller = controller;
        loadRememberedData();
    }

    /**
     * Establece el controller después de la construcción
     * @param controller Controlador para manejar la lógica de login
     */
    public void setController(LoginController controller) {
        this.controller = controller;
        loadRememberedData();
    }

    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void createComponents() {
        // Campo de email con ancho consistente
        emailField = new JTextField();
        emailField.setFont(UIConstants.BODY);
        emailField.setBackground(UIConstants.CARD_BG);
        emailField.setForeground(UIConstants.TEXT_PRIMARY);
        emailField.setBorder(createRoundedBorder(new Color(0xCED4DA), false));
        emailField.setPreferredSize(new Dimension(350, 40));
        emailField.setMaximumSize(new Dimension(350, 40));

        // Campo de contraseña con ancho consistente
        passwordField = new JPasswordField();
        passwordField.setFont(UIConstants.BODY);
        passwordField.setBackground(UIConstants.CARD_BG);
        passwordField.setForeground(UIConstants.TEXT_PRIMARY);
        passwordField.setBorder(createRoundedBorder(new Color(0xCED4DA), false));
        passwordField.setPreferredSize(new Dimension(350, 40));
        passwordField.setMaximumSize(new Dimension(350, 40));

        // Botón de inicio de sesión con sombra y ancho consistente
        loginButton = new JButton("Iniciar Sesión") {
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
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setBackground(UIConstants.BLUE_MAIN);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(true);
        loginButton.setPreferredSize(new Dimension(350, 45));
        loginButton.setMaximumSize(new Dimension(350, 45));

        // Checkbox "Recordarme"
        rememberMeCheckBox = new JCheckBox("Recordarme");
        rememberMeCheckBox.setFont(UIConstants.SMALL);
        rememberMeCheckBox.setBackground(UIConstants.CARD_BG);
        rememberMeCheckBox.setForeground(UIConstants.TEXT_PRIMARY);

        // Enlace "Resgistrarse"
        registrarseLabel = new JLabel("<html><u>¿Registrarse?</u></html>");
        registrarseLabel.setFont(UIConstants.SMALL);
        registrarseLabel.setForeground(UIConstants.BLUE_MAIN);
        registrarseLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

        // Main content panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIConstants.BG_APP);
        mainPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        // Login card panel
        JPanel loginCard = createLoginCard();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(loginCard, gbc);

        root.add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createLoginCard() {
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

        card.setLayout(new GridBagLayout());
        card.setBackground(UIConstants.CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(45, 50, 45, 50));
        card.setPreferredSize(new Dimension(450, 500));
        card.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Panel contenedor para centrar el título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setBackground(UIConstants.CARD_BG);
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Iniciar Sesión");
        titleLabel.setFont(UIConstants.H1);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);
        titlePanel.add(titleLabel);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(titlePanel, gbc);

        // Panel contenedor para centrar el subtítulo
        JPanel subtitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        subtitlePanel.setBackground(UIConstants.CARD_BG);
        subtitlePanel.setOpaque(false);

        JLabel subtitleLabel = new JLabel("Ingresa tus credenciales para continuar");
        subtitleLabel.setFont(UIConstants.BODY);
        subtitleLabel.setForeground(UIConstants.TEXT_MUTED);
        subtitlePanel.add(subtitleLabel);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        card.add(subtitlePanel, gbc);

        // Label para email (alineado a la izquierda)
        JLabel emailLabel = new JLabel("Correo electrónico");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        emailLabel.setForeground(UIConstants.TEXT_PRIMARY);

        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(emailLabel, gbc);

        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 18, 0);
        card.add(emailField, gbc);

        // Label para contraseña (alineado a la izquierda)
        JLabel passwordLabel = new JLabel("Contraseña");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        passwordLabel.setForeground(UIConstants.TEXT_PRIMARY);

        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(passwordLabel, gbc);

        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 12, 0);
        card.add(passwordField, gbc);

        // Panel para recordarme y olvidar contraseña (mejor balanceado)
        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsPanel.setBackground(UIConstants.CARD_BG);
        optionsPanel.setOpaque(false);
        optionsPanel.setPreferredSize(new Dimension(350, 25));
        optionsPanel.add(rememberMeCheckBox, BorderLayout.WEST);
        optionsPanel.add(registrarseLabel, BorderLayout.EAST);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 20, 0);
        card.add(optionsPanel, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(loginButton, gbc);

        return card;
    }

    private void setupEventListeners() {
        // Acción del botón de login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Enter en los campos de texto
        ActionListener loginAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        };

        emailField.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);

        // Hover effect para el botón
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(UIConstants.BLUE_DARK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(UIConstants.BLUE_MAIN);
            }
        });

        // Focus listeners para los campos de texto con efecto hover
        emailField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                emailField.setBorder(createRoundedBorder(UIConstants.BLUE_MAIN, true));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                emailField.setBorder(createRoundedBorder(new Color(0xCED4DA), false));
            }
        });

        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(createRoundedBorder(UIConstants.BLUE_MAIN, true));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                passwordField.setBorder(createRoundedBorder(new Color(0xCED4DA), false));
            }
        });

        // Hover effect para los campos de texto
        emailField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!emailField.hasFocus()) {
                    emailField.setBorder(createRoundedBorder(new Color(0xADB5BD), false));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!emailField.hasFocus()) {
                    emailField.setBorder(createRoundedBorder(new Color(0xCED4DA), false));
                }
            }
        });

        passwordField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!passwordField.hasFocus()) {
                    passwordField.setBorder(createRoundedBorder(new Color(0xADB5BD), false));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!passwordField.hasFocus()) {
                    passwordField.setBorder(createRoundedBorder(new Color(0xCED4DA), false));
                }
            }
        });

        // Click en "Registrarse"
        registrarseLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleRegistrarse();
            }
        });
    }

    private void handleLogin() {
        if (controller == null) {
            showError("Error interno: Controlador no inicializado.");
            return;
        }
        // Delegar toda la lógica al controlador
        controller.handleLogin(getEmailText(), getPasswordText(), isRememberMeSelected());
    }

    private void handleRegistrarse() {
        if (controller != null) {
            controller.handleRegistrarse();
        } else {
            showError("Controlador no inicializado.");
        }
    }

    /**
     * Carga los datos recordados si existen
     */
    private void loadRememberedData() {
        if (controller != null) {
            String rememberedEmail = controller.getRememberedEmail();
            if (!rememberedEmail.isEmpty()) {
                emailField.setText(rememberedEmail);
                rememberMeCheckBox.setSelected(true);
                passwordField.requestFocus(); // Enfocar en contraseña si email ya está
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

    // Getters para que el controller pueda acceder a los datos si es necesario
    public String getEmailText() {
        return emailField.getText().trim();
    }

    public String getPasswordText() {
        return new String(passwordField.getPassword());
    }

    public boolean isRememberMeSelected() {
        return rememberMeCheckBox.isSelected();
    }

    public static void main(String[] args) {
        // Look and feel del sistema para que se vea moderno
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}