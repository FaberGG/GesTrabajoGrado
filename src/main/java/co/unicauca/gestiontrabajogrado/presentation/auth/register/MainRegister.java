package co.unicauca.gestiontrabajogrado.presentation.auth.register;

import co.unicauca.gestiontrabajogrado.infrastructure.repository.UserRepository;

import javax.swing.*;
import java.awt.*;

public class MainRegister {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear un JFrame temporal para simular el login (padre de la ventana modal)
                JFrame parentFrame = createMockLoginFrame();
                
                // Inicializar repositorio
                UserRepository repo = new UserRepository();
                
                // Crear y mostrar el controlador de registro
                RegisterController registerController = new RegisterController(parentFrame, repo);
                registerController.showView();
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error al inicializar la aplicación: " + e.getMessage(),
                    "Error Fatal",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
    
    /**
     * Crea un JFrame temporal que simula la ventana de login
     * para poder probar la ventana modal de registro
     */
    private static JFrame createMockLoginFrame() {
        JFrame mockFrame = new JFrame("Login - Universidad del Cauca");
        mockFrame.setSize(600, 500); // Más grande
        mockFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mockFrame.setLocationRelativeTo(null);
        
        // Configurar el contenido del mock frame
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(240, 242, 247));
        
        // Header simulado
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(52, 103, 170));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel titleLabel = new JLabel("Sistema de Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Panel central con información
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(240, 242, 247));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>" +
                                     "<h2>Ventana de Login Simulada</h2>" +
                                     "<p>Esta ventana simula el login principal.</p>" +
                                     "<p>La ventana de registro se abrirá como modal.</p>" +
                                     "</div></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(73, 80, 87));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton btnOpenRegister = new JButton("Abrir Registro (Prueba)");
        btnOpenRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnOpenRegister.setBackground(new Color(52, 103, 170));
        btnOpenRegister.setForeground(Color.WHITE);
        btnOpenRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOpenRegister.setMaximumSize(new Dimension(200, 40));
        
        // Listener para abrir otra ventana de registro si se desea
        btnOpenRegister.addActionListener(e -> {
            try {
                UserRepository repo = new UserRepository();
                RegisterController controller = new RegisterController(mockFrame, repo);
                controller.showView();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mockFrame, 
                    "Error: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        centerPanel.add(infoLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(btnOpenRegister);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        mockFrame.setContentPane(contentPanel);
        mockFrame.setVisible(true);
        
        return mockFrame;
    }
}