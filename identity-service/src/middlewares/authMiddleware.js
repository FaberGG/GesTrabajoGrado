'use strict';

const JwtHelper = require('../utils/jwtHelper');
const logger = require('../config/logger');

/**
 * Middleware para verificar la autenticación mediante JWT
 * Se utiliza en rutas privadas que requieren autenticación
 */
const authMiddleware = async (req, res, next) => {
  try {
    // Extraer el token del header
    const token = JwtHelper.extractTokenFromHeader(req);

    // Si no hay token, denegar acceso
    if (!token) {
      return res.status(401).json({
        success: false,
        message: 'Acceso denegado. Token no proporcionado.'
      });
    }

    // Verificar el token
    const payload = JwtHelper.verifyToken(token);

    // Si el token no es válido, denegar acceso
    if (!payload) {
      return res.status(401).json({
        success: false,
        message: 'Token no válido o expirado'
      });
    }

    // Almacenar datos del usuario en el objeto request para uso posterior
    req.user = {
      userId: payload.userId,
      email: payload.email,
      rol: payload.rol,
      programa: payload.programa
    };

    // Continuar con la siguiente función/middleware
    next();
  } catch (error) {
    logger.error('Error en middleware de autenticación', { error: error.message });
    return res.status(401).json({
      success: false,
      message: 'Error en la autenticación'
    });
  }
};

module.exports = authMiddleware;
