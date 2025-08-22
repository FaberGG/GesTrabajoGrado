package co.unicauca.gestiontrabajogrado.presentation.auth.register;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RegisterView extends JDialog { // Cambio de JFrame a JDialog para modal
    // Colores que coinciden con el login
    private static final Color HEADER_COLOR = new Color(52, 103, 170);
    private static final Color PRIMARY_COLOR = new Color(52, 103, 170);
    private static final Color BACKGROUND_COLOR = new Color(240, 242, 247);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(73, 80, 87);
    private static final Color BORDER_COLOR = new Color(206, 212, 218);
    private static final Color LINK_COLOR = new Color(52, 103, 170);

    // Componentes
    private JTextField txtNombres;
    private JTextField txtApellidos;
    private JTextField txtCelular;
    private JComboBox<String> cbPrograma;
    private JComboBox<String> cbRol;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnRegistrar;
    private JButton btnCancelar;
    private JLabel lblLogin;

    // Constructor que recibe el frame padre para hacer la ventana modal
    public RegisterView(JFrame parent) {
        super(parent, "Universidad del Cauca - Registro", true); // Modal
        initializeDialog();
        setupComponents();
        setupLayout();
    }

    private void initializeDialog() {
        // Configuración específica para ventana modal - TAMAÑO AUMENTADO
        setSize(700, 800); // Mucho más alto para mostrar todos los campos
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent()); // Centrar respecto al padre
        setResizable(true);
        setMinimumSize(new Dimension(650, 750)); // Mínimo más grande

        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void setupComponents() {
        // Configurar componentes
        txtNombres = createStyledTextField();
        txtApellidos = createStyledTextField();
        txtCelular = createStyledTextField();
        txtEmail = createStyledTextField();
        txtPassword = createStyledPasswordField();

        // ComboBoxes estilizados
        cbPrograma = createStyledComboBox(new String[]{
            "Seleccione un programa...",
            "INGENIERIA_SISTEMAS",
            "INGENIERIA_ELECTRONICA_TELECOMUNICACIONES",
            "AUTOMATICA_INDUSTRIAL",
            "TECNOLOGIA_TELEMATICA"
        });

        cbRol = createStyledComboBox(new String[]{
            "Seleccione un rol...",
            "ESTUDIANTE",
            "DOCENTE"
        });

        // Botones
        btnRegistrar = createPrimaryButton("Registrarse");
        btnCancelar = createSecondaryButton("Cancelar");

        // Enlace para cerrar y volver al login
        lblLogin = createLinkLabel("¿Ya tienes cuenta? Cerrar e Iniciar Sesión");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header compacto para modal
        JPanel headerPanel = createCompactHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Contenido principal con scroll mejorado
        JPanel mainContent = createScrollableMainContent();
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createCompactHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        // Título principal más compacto
        JLabel titleLabel = new JLabel("Registro de Usuario");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtítulo
        JLabel subtitleLabel = new JLabel("Sistema de Gestión de Trabajo de Grado");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        return headerPanel;
    }
    
    private JPanel createScrollableMainContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(new BorderLayout());
        
        // Panel contenedor con padding
        JPanel containerPanel = new JPanel();
        containerPanel.setBackground(BACKGROUND_COLOR);
        containerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Menos padding vertical
        
        // Crear el formulario
        JPanel formCard = createFormCard();
        containerPanel.add(formCard);
        
        mainPanel.add(containerPanel, BorderLayout.CENTER);
        
        // Panel inferior con espacio adicional para scroll
        JPanel bottomPadding = new JPanel();
        bottomPadding.setBackground(BACKGROUND_COLOR);
        bottomPadding.setPreferredSize(new Dimension(0, 50)); // Más espacio para scroll
        mainPanel.add(bottomPadding, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createFormCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(15),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        // Tamaño ajustado para mostrar todos los campos
        card.setPreferredSize(new Dimension(550, 750)); // Más alto
        card.setMaximumSize(new Dimension(550, Integer.MAX_VALUE));
        
        // Título del formulario
        JLabel formTitle = new JLabel("Crear Nueva Cuenta");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(TEXT_COLOR);
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel formSubtitle = new JLabel("Complete todos los campos obligatorios (*)");
        formSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formSubtitle.setForeground(new Color(108, 117, 125));
        formSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(formTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(formSubtitle);
        card.add(Box.createVerticalStrut(20)); // Reducido un poco
        
        // Campos del formulario - ESPACIADO REDUCIDO
        card.add(createFieldLabel("Nombres *"));
        card.add(Box.createVerticalStrut(5));
        card.add(txtNombres);
        card.add(Box.createVerticalStrut(12)); // Reducido de 15 a 12
        
        card.add(createFieldLabel("Apellidos *"));
        card.add(Box.createVerticalStrut(5));
        card.add(txtApellidos);
        card.add(Box.createVerticalStrut(12));
        
        card.add(createFieldLabel("Celular (opcional)"));
        card.add(Box.createVerticalStrut(5));
        card.add(txtCelular);
        card.add(Box.createVerticalStrut(12));
        
        card.add(createFieldLabel("Programa Académico *"));
        card.add(Box.createVerticalStrut(5));
        card.add(cbPrograma);
        card.add(Box.createVerticalStrut(12));
        
        card.add(createFieldLabel("Rol *"));
        card.add(Box.createVerticalStrut(5));
        card.add(cbRol);
        card.add(Box.createVerticalStrut(12));
        
        card.add(createFieldLabel("Correo electrónico *"));
        card.add(Box.createVerticalStrut(5));
        card.add(txtEmail);
        card.add(Box.createVerticalStrut(12));
        
        card.add(createFieldLabel("Contraseña *"));
        card.add(Box.createVerticalStrut(5));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(20)); // Espacio antes de botones
        
        // Panel de botones
        JPanel buttonPanel = createButtonPanel();
        card.add(buttonPanel);
        card.add(Box.createVerticalStrut(15)); // Reducido
        
        // Enlace al login
        card.add(lblLogin);
        
        return card;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnRegistrar);
        
        return buttonPanel;
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new RoundedBorder(8));
        field.setBackground(CARD_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setPreferredSize(new Dimension(0, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // Efecto de foco
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new RoundedBorder(8, PRIMARY_COLOR, 2));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new RoundedBorder(8));
            }
        });
        
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new RoundedBorder(8));
        field.setBackground(CARD_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setPreferredSize(new Dimension(0, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // Efecto de foco
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new RoundedBorder(8, PRIMARY_COLOR, 2));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new RoundedBorder(8));
            }
        });
        
        return field;
    }
    
    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(CARD_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBorder(new RoundedBorder(8));
        comboBox.setPreferredSize(new Dimension(0, 40));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // Personalizar el renderer
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (index == 0) {
                    setForeground(new Color(108, 117, 125));
                } else {
                    setForeground(TEXT_COLOR);
                }
                
                if (isSelected) {
                    setBackground(PRIMARY_COLOR);
                    setForeground(Color.WHITE);
                }
                
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                return this;
            }
        });
        
        return comboBox;
    }
    
    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(new RoundedBorder(8));
        button.setPreferredSize(new Dimension(0, 45));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efectos hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY_COLOR.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }
    
    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(TEXT_COLOR);
        button.setBackground(Color.WHITE);
        button.setBorder(new RoundedBorder(8, BORDER_COLOR, 1));
        button.setPreferredSize(new Dimension(0, 45));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efectos hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(248, 249, 250));
                button.setBorder(new RoundedBorder(8, PRIMARY_COLOR, 1));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(Color.WHITE);
                button.setBorder(new RoundedBorder(8, BORDER_COLOR, 1));
            }
        });
        
        return button;
    }
    
    private JLabel createLinkLabel(String text) {
        JLabel link = new JLabel("<html><u>" + text + "</u></html>");
        link.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        link.setForeground(LINK_COLOR);
        link.setAlignmentX(Component.CENTER_ALIGNMENT);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        link.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                link.setForeground(LINK_COLOR.darker());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                link.setForeground(LINK_COLOR);
            }
        });
        
        return link;
    }
    
    // Clase para bordes redondeados
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        private final int thickness;
        
        public RoundedBorder(int radius) {
            this(radius, BORDER_COLOR, 1);
        }
        
        public RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            int padding = Math.max(4, radius / 4);
            return new Insets(padding + 8, padding + 12, padding + 8, padding + 12);
        }
    }
    
    // Getters para obtener los valores de los campos
    public String getNombres() { 
        return txtNombres.getText().trim();
    }
    
    public String getApellidos() { 
        return txtApellidos.getText().trim();
    }
    
    public String getCelular() { 
        return txtCelular.getText().trim();
    }
    
    public String getPrograma() { 
        int selectedIndex = cbPrograma.getSelectedIndex();
        return selectedIndex > 0 ? cbPrograma.getSelectedItem().toString() : "";
    }
    
    public String getRol() { 
        int selectedIndex = cbRol.getSelectedIndex();
        return selectedIndex > 0 ? cbRol.getSelectedItem().toString() : "";
    }
    
    public String getEmail() { 
        return txtEmail.getText().trim();
    }
    
    public String getPassword() { 
        return new String(txtPassword.getPassword());
    }
    
    // Listeners
    public void addRegisterListener(ActionListener listener) {
        btnRegistrar.addActionListener(listener);
    }
    
    public void addCancelListener(ActionListener listener) {
        btnCancelar.addActionListener(listener);
    }
    
    public void addLoginLinkListener(ActionListener listener) {
        lblLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (listener != null) {
                    listener.actionPerformed(null);
                }
            }
        });
    }
    
    // Métodos para mostrar mensajes
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Universidad del Cauca", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Método para limpiar el formulario
    public void clearForm() {
        txtNombres.setText("");
        txtApellidos.setText("");
        txtCelular.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        cbPrograma.setSelectedIndex(0);
        cbRol.setSelectedIndex(0);
    }
    
    // Método para enfocar el primer campo
    public void focusFirstField() {
        SwingUtilities.invokeLater(() -> txtNombres.requestFocusInWindow());
    }
    
    // Método para cerrar la ventana modal
    public void closeDialog() {
        dispose();
    }
}