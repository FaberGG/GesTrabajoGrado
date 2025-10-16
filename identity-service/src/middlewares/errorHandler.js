'use strict';

const logger = require('../config/logger');

/**
 * Middleware centralizado para manejo de errores
 * Captura todos los errores lanzados en la aplicación y envía respuestas consistentes
 */
const errorHandler = (err, req, res, next) => {
  // Loguear el error
  logger.error('Error en la aplicación', {
    path: req.path,
    method: req.method,
    error: err.message,
    stack: process.env.NODE_ENV === 'development' ? err.stack : undefined
  });

  // Si es un error de Sequelize (validación de modelo)
  if (err.name === 'SequelizeValidationError' || err.name === 'SequelizeUniqueConstraintError') {
    const errors = err.errors.map(e => e.message);
    return res.status(400).json({
      success: false,
      message: 'Error de validación',
      errors
    });
  }

  // Si es un error de Express-Validator (validación de request)
  if (err.array && typeof err.array === 'function') {
    const errors = err.array().map(e => e.msg);
    return res.status(400).json({
      success: false,
      message: 'Error de validación',
      errors
    });
  }

  // Determinar el código HTTP basado en el tipo de error
  let statusCode = 500;
  if (err.statusCode) {
    statusCode = err.statusCode;
  } else if (err.message.includes('no encontrado') || err.message.includes('not found')) {
    statusCode = 404;
  } else if (
    err.message.includes('no válido') ||
    err.message.includes('inválido') ||
    err.message.includes('contraseña') ||
    err.message.includes('credenciales')
  ) {
    statusCode = 401;
  } else if (err.message.includes('ya está registrado') || err.message.includes('duplicado')) {
    statusCode = 409;
  } else if (err.message.includes('no permitido') || err.message.includes('no autorizado')) {
    statusCode = 403;
  }

  // Preparar la respuesta de error
  const errorResponse = {
    success: false,
    message: err.message || 'Error interno del servidor'
  };

  // Solo incluir stack trace en desarrollo
  if (process.env.NODE_ENV === 'development') {
    errorResponse.stack = err.stack;
  }

  return res.status(statusCode).json(errorResponse);
};

module.exports = errorHandler;
