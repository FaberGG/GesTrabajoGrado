package co.unicauca.gestiontrabajogrado.presentation.auth.register;

import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.domain.model.enumProgram;
import co.unicauca.gestiontrabajogrado.domain.model.enumRol;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.UserRepository;
import co.unicauca.gestiontrabajogrado.util.EmailPolicy;
import co.unicauca.gestiontrabajogrado.util.PasswordHasher;
import co.unicauca.gestiontrabajogrado.util.PasswordPolicy;

import javax.swing.*;
import java.awt.*;

public class RegisterController {
    private final RegisterView view;
    private final UserRepository repo;
    private final PasswordHasher hasher = new PasswordHasher();
    private final JFrame parentFrame; // Referencia al frame padre (LoginView)
    
    // Constructor que recibe el frame padre para crear ventana modal
    public RegisterController(JFrame parentFrame, UserRepository repo) {
        this.parentFrame = parentFrame;
        this.view = new RegisterView(parentFrame);
        this.repo = repo;
        setupEventHandlers();
    }
    
    private void setupEventHandlers() {
        view.addRegisterListener(e -> registerUser());
        view.addCancelListener(e -> cancelRegistration());
        view.addLoginLinkListener(e -> goToLogin());
        
        // Listener para cerrar con ESC
        view.getRootPane().registerKeyboardAction(
            e -> cancelRegistration(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void goToLogin() {
        int option = JOptionPane.showConfirmDialog(
            view,
            "¿Deseas cerrar el registro y volver al login?\n\n" +
            "Se perderá la información ingresada en este formulario.",
            "Volver al Login",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            view.closeDialog();
            // El login ya está visible en el fondo, solo cerramos el registro
        }
    }
    
    private void cancelRegistration() {
        // Si hay datos ingresados, preguntar antes de cerrar
        if (hasDataEntered()) {
            int option = JOptionPane.showConfirmDialog(
                view,
                "¿Está seguro que desea cancelar el registro?\n\n" +
                "Se perderá toda la información ingresada.",
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (option == JOptionPane.YES_OPTION) {
                view.closeDialog();
            }
        } else {
            view.closeDialog();
        }
    }
    
    private boolean hasDataEntered() {
        return !view.getNombres().isEmpty() || 
               !view.getApellidos().isEmpty() || 
               !view.getCelular().isEmpty() || 
               !view.getEmail().isEmpty() || 
               !view.getPassword().isEmpty() ||
               !view.getPrograma().isEmpty() ||
               !view.getRol().isEmpty();
    }
    
    private void registerUser() {
        try {
            // Deshabilitar botón durante el procesamiento
            view.getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            // Obtener datos del formulario
            String nombres = view.getNombres();
            String apellidos = view.getApellidos();
            String celular = view.getCelular();
            String programa = view.getPrograma();
            String rol = view.getRol();
            String email = view.getEmail();
            String password = view.getPassword();
            
            // Validaciones
            if (!validateRequiredFields(nombres, apellidos, programa, rol, email, password)) {
                return;
            }
            
            if (!validateEmail(email)) {
                return;
            }
            
            if (!validatePassword(password)) {
                return;
            }
            
            if (!validateUniqueEmail(email)) {
                return;
            }
            
            // Crear y guardar usuario
            if (createAndSaveUser(nombres, apellidos, celular, programa, rol, email, password)) {
                view.showSuccessMessage("¡Usuario registrado exitosamente!\n\n" +
                                      "Bienvenido al Sistema de Gestión de Trabajo de Grado\n" +
                                      "de la Universidad del Cauca.\n\n" +
                                      "Ya puedes iniciar sesión con tus credenciales.");
                
                // Cerrar ventana de registro después del éxito
                view.closeDialog();
            }
            
        } catch (Exception ex) {
            view.showErrorMessage("Error inesperado durante el registro:\n\n" + 
                                ex.getMessage() + 
                                "\n\nPor favor, inténtelo nuevamente.");
        } finally {
            // Restaurar cursor normal
            view.getRootPane().setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private boolean validateRequiredFields(String nombres, String apellidos, String programa, 
                                         String rol, String email, String password) {
        if (nombres.isEmpty() || apellidos.isEmpty() || programa.isEmpty() || 
            rol.isEmpty() || email.isEmpty() || password.isEmpty()) {
            
            view.showErrorMessage("Campos Obligatorios\n\n" +
                                "Todos los campos marcados con (*) son obligatorios:\n\n" +
                                "• Nombres\n" +
                                "• Apellidos\n" +
                                "• Programa Académico\n" +
                                "• Rol\n" +
                                "• Correo electrónico\n" +
                                "• Contraseña\n\n" +
                                "Por favor, complete la información requerida.");
            return false;
        }
        return true;
    }
    
    private boolean validateEmail(String email) {
        if (!EmailPolicy.isInstitutional(email)) {
            view.showErrorMessage("Correo Institucional Requerido\n\n" +
                                "El correo electrónico debe ser institucional de la Universidad del Cauca.\n\n" +
                                "Formato requerido: usuario@unicauca.edu.co\n\n" +
                                "Ejemplo: juan.perez@unicauca.edu.co");
            view.focusFirstField(); // Enfocar para facilitar corrección
            return false;
        }
        return true;
    }
    
    private boolean validatePassword(String password) {
        if (!PasswordPolicy.isValid(password)) {
            view.showErrorMessage("Política de Contraseñas\n\n" +
                                "La contraseña debe cumplir con los siguientes requisitos:\n\n" +
                                "✓ Mínimo 6 caracteres\n" +
                                "✓ Al menos una letra mayúscula (A-Z)\n" +
                                "✓ Al menos un dígito (0-9)\n" +
                                "✓ Al menos un carácter especial (!@#$%^&*)\n\n" +
                                "Ejemplo de contraseña válida: MiClave123!");
            return false;
        }
        return true;
    }
    
    private boolean validateUniqueEmail(String email) {
        if (repo.emailExists(email)) {
            view.showErrorMessage("Correo ya Registrado\n\n" +
                                "El correo electrónico '" + email + "' ya está registrado en el sistema.\n\n" +
                                "Si ya tienes una cuenta, cierra esta ventana e inicia sesión.\n" +
                                "Si olvidaste tu contraseña, contacta al administrador del sistema.");
            return false;
        }
        return true;
    }
    
    private boolean createAndSaveUser(String nombres, String apellidos, String celular, 
                                    String programa, String rol, String email, String password) {
        try {
            // Hashear contraseña
            String hashedPassword = hasher.hash(password);
            
            // Crear usuario
            User usuario = new User(
                null,
                nombres,
                apellidos,
                celular.isEmpty() ? null : celular,
                enumProgram.valueOf(programa),
                enumRol.valueOf(rol),
                email,
                hashedPassword
            );
            
            // Guardar en el repositorio
            repo.save(usuario);
            return true;
            
        } catch (IllegalArgumentException ex) {
            view.showErrorMessage("Error de Validación\n\n" +
                                "Valor inválido detectado: " + ex.getMessage() + 
                                "\n\nVerifique que todos los campos tengan valores válidos.\n\n" +
                                "Si el problema persiste, contacte al administrador.");
            return false;
        } catch (Exception ex) {
            view.showErrorMessage("Error al Guardar\n\n" +
                                "No se pudo guardar el usuario en el sistema.\n\n" +
                                "Detalles del error: " + ex.getMessage() + "\n\n" +
                                "Por favor, inténtelo nuevamente o contacte al administrador.");
            return false;
        }
    }
    
    // Método público para mostrar la vista modal
    public void showView() {
        view.clearForm();
        view.focusFirstField();
        view.setVisible(true);
    }
    
    // Método para obtener la referencia de la vista (si es necesario)
    public RegisterView getView() {
        return view;
    }
    
    // Método para validar datos antes de mostrar (opcional)
    public boolean isRepositoryAvailable() {
        try {
            // Verificar que el repositorio esté disponible
            return repo != null;
        } catch (Exception e) {
            return false;
        }
    }
}