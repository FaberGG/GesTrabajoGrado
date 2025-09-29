package co.unicauca.gestiontrabajogrado.domain.service;


import java.util.logging.Logger;
import java.util.logging.Level;

public class NotificationService {

    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());

    /**
     * Simula el envío de notificación por correo electrónico cuando se evalúa un Formato A
     * @param proyectoId ID del proyecto evaluado
     * @param tituloProyecto Título del proyecto
     * @param estadoEvaluacion Estado de la evaluación (APROBADO/RECHAZADO)
     * @param observaciones Observaciones de la evaluación
     * @param docenteEmail Email del docente (simulado)
     * @param estudiante1Email Email del estudiante 1 (simulado)
     * @param estudiante2Email Email del estudiante 2 (simulado, puede ser null)
     */
    public void enviarNotificacionEvaluacionFormatoA(
            Integer proyectoId,
            String tituloProyecto,
            String estadoEvaluacion,
            String observaciones,
            String docenteEmail,
            String estudiante1Email,
            String estudiante2Email) {

        // Simular envío de email con logger
        String mensaje = construirMensajeNotificacion(
                proyectoId, tituloProyecto, estadoEvaluacion, observaciones);

        // Log para el docente
        logger.log(Level.INFO, "📧 EMAIL ENVIADO - Docente: {0}", docenteEmail);
        logger.log(Level.INFO, "Asunto: Evaluación de Formato A - {0}", tituloProyecto);
        logger.log(Level.INFO, "Contenido:\n{0}", mensaje);

        // Log para estudiante 1
        logger.log(Level.INFO, "📧 EMAIL ENVIADO - Estudiante: {0}", estudiante1Email);
        logger.log(Level.INFO, "Asunto: Evaluación de Formato A - {0}", tituloProyecto);
        logger.log(Level.INFO, "Contenido:\n{0}", mensaje);

        // Log para estudiante 2 si existe
        if (estudiante2Email != null && !estudiante2Email.trim().isEmpty()) {
            logger.log(Level.INFO, "📧 EMAIL ENVIADO - Estudiante: {0}", estudiante2Email);
            logger.log(Level.INFO, "Asunto: Evaluación de Formato A - {0}", tituloProyecto);
            logger.log(Level.INFO, "Contenido:\n{0}", mensaje);
        }

        logger.log(Level.INFO, "✅ Notificaciones enviadas exitosamente para el proyecto ID: {0}", proyectoId);
    }

    private String construirMensajeNotificacion(
            Integer proyectoId,
            String tituloProyecto,
            String estadoEvaluacion,
            String observaciones) {

        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Estimado/a,\n\n");
        mensaje.append("Le informamos que se ha completado la evaluación del Formato A para el siguiente proyecto:\n\n");
        mensaje.append("📋 Proyecto ID: ").append(proyectoId).append("\n");
        mensaje.append("📝 Título: ").append(tituloProyecto).append("\n");
        mensaje.append("📊 Estado de evaluación: ").append(estadoEvaluacion).append("\n\n");

        if (observaciones != null && !observaciones.trim().isEmpty()) {
            mensaje.append("💭 Observaciones del coordinador:\n");
            mensaje.append(observaciones).append("\n\n");
        }

        if ("APROBADO".equalsIgnoreCase(estadoEvaluacion)) {
            mensaje.append("🎉 ¡Felicitaciones! Su Formato A ha sido aprobado. ");
            mensaje.append("Puede proceder con los siguientes pasos del proceso.\n\n");
        } else {
            mensaje.append("⚠️ Su Formato A requiere modificaciones. ");
            mensaje.append("Por favor revise las observaciones y realice las correcciones necesarias.\n\n");
        }

        mensaje.append("Para más información, por favor ingrese al sistema de gestión de trabajos de grado.\n\n");
        mensaje.append("Atentamente,\n");
        mensaje.append("Coordinación de Programa\n");
        mensaje.append("Universidad del Cauca");

        return mensaje.toString();
    }

    /**
     * Simula confirmación al coordinador de que las notificaciones fueron enviadas
     * @param proyectoId ID del proyecto
     * @param coordinadorEmail Email del coordinador
     */
    public void confirmarEnvioNotificaciones(Integer proyectoId, String coordinadorEmail) {
        logger.log(Level.INFO, "📧 CONFIRMACIÓN ENVIADA - Coordinador: {0}", coordinadorEmail);
        logger.log(Level.INFO, "Asunto: Confirmación - Evaluación registrada exitosamente");
        logger.log(Level.INFO, "Contenido: Se ha completado la evaluación del proyecto ID: {0} y las notificaciones han sido enviadas a todos los involucrados.", proyectoId);
    }
}