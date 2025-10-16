'use strict';

const jwt = require('jsonwebtoken');
const jwtConfig = require('../config/jwt');
const logger = require('../config/logger');

/**
 * Utilidad para manejar operaciones con JSON Web Tokens (JWT)
 */
class JwtHelper {
  /**
   * Genera un token JWT con los datos del usuario
   *
   * @param {Object} userData - Datos del usuario para incluir en el token
   * @returns {string} Token JWT generado
   */
  static generateToken(userData) {
    try {
      const payload = {
        userId: userData.id,
        email: userData.email,
        rol: userData.rol,
        programa: userData.programa
      };

      return jwt.sign(payload, jwtConfig.secret, {
        expiresIn: jwtConfig.expiration,
        algorithm: jwtConfig.algorithm
      });
    } catch (error) {
      logger.error('Error al generar JWT', { error: error.message });
      throw new Error('Error al generar el token de autenticación');
    }
  }

  /**
   * Verifica la validez de un token JWT
   *
   * @param {string} token - Token JWT a verificar
   * @returns {Object|null} Payload del token si es válido, null si no lo es
   */
  static verifyToken(token) {
    try {
      const payload = jwt.verify(token, jwtConfig.secret);
      return payload;
    } catch (error) {
      logger.error('Error al verificar JWT', { error: error.message });
      return null;
    }
  }

  /**
   * Extrae el token JWT del header de autorización
   *
   * @param {Object} req - Objeto de solicitud HTTP de Express
   * @returns {string|null} Token JWT si existe, null si no
   */
  static extractTokenFromHeader(req) {
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return null;
    }

    return authHeader.split(' ')[1];
  }
}

module.exports = JwtHelper;
