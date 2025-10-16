'use strict';

const AuthService = require('../services/authService');
const logger = require('../config/logger');

/**
 * Controlador para manejar operaciones de autenticación
 */
class AuthController {
  /**
   * Registra un nuevo usuario
   * @param {Object} req - Objeto de solicitud Express
   * @param {Object} res - Objeto de respuesta Express
   * @param {Function} next - Función de siguiente middleware
   */
  static async register(req, res, next) {
    try {
      const userData = {
        nombres: req.body.nombres,
        apellidos: req.body.apellidos,
        celular: req.body.celular,
        programa: req.body.programa,
        rol: req.body.rol,
        email: req.body.email
      };

      const plainPassword = req.body.password;

      const user = await AuthService.register(userData, plainPassword);

      res.status(201).json({
        success: true,
        message: 'Usuario registrado exitosamente',
        data: user
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Autentica un usuario y genera un token JWT
   * @param {Object} req - Objeto de solicitud Express
   * @param {Object} res - Objeto de respuesta Express
   * @param {Function} next - Función de siguiente middleware
   */
  static async login(req, res, next) {
    try {
      const { email, password } = req.body;

      const { user, token } = await AuthService.login(email, password);

      res.status(200).json({
        success: true,
        message: 'Login exitoso',
        data: { user, token }
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Obtiene el perfil del usuario autenticado
   * @param {Object} req - Objeto de solicitud Express
   * @param {Object} res - Objeto de respuesta Express
   * @param {Function} next - Función de siguiente middleware
   */
  static async getProfile(req, res, next) {
    try {
      const { userId } = req.user;

      const user = await AuthService.getProfile(userId);

      res.status(200).json({
        success: true,
        data: user
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Obtiene lista de roles y programas disponibles
   * @param {Object} req - Objeto de solicitud Express
   * @param {Object} res - Objeto de respuesta Express
   * @param {Function} next - Función de siguiente middleware
   */
  static async getRoles(req, res, next) {
    try {
      const data = await AuthService.getRolesAndPrograms();

      res.status(200).json({
        success: true,
        data
      });
    } catch (error) {
      next(error);
    }
  }

  /**
   * Verifica la validez de un token JWT
   * @param {Object} req - Objeto de solicitud Express
   * @param {Object} res - Objeto de respuesta Express
   * @param {Function} next - Función de siguiente middleware
   */
  static async verifyToken(req, res, next) {
    try {
      const { token } = req.body;

      const userData = await AuthService.verifyToken(token);

      res.status(200).json({
        success: true,
        valid: true,
        data: userData
      });
    } catch (error) {
      // Para este endpoint particular, no queremos lanzar un error HTTP,
      // sino devolver una respuesta que indique que el token no es válido
      logger.info('Token verificado como inválido', { error: error.message });

      res.status(401).json({
        success: false,
        valid: false,
        message: 'Token inválido o expirado'
      });
    }
  }
}

module.exports = AuthController;
