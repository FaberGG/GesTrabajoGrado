'use strict';

const winston = require('winston');
const { format } = winston;
const dotenv = require('dotenv');

dotenv.config();

// Nivel de log basado en el entorno
const level = process.env.LOG_LEVEL || (process.env.NODE_ENV === 'production' ? 'info' : 'debug');

// Formato personalizado para los logs
const customFormat = format.combine(
  format.timestamp({ format: 'YYYY-MM-DD HH:mm:ss' }),
  format.errors({ stack: true }),
  format.splat(),
  format.json()
);

// Instancia del logger
const logger = winston.createLogger({
  level,
  format: customFormat,
  defaultMeta: { service: 'identity-service' },
  transports: [
    // Log a la consola siempre
    new winston.transports.Console({
      format: format.combine(
        format.colorize(),
        format.printf(({ timestamp, level, message, ...meta }) => {
          // Asegúrate de no mostrar datos sensibles como contraseñas o tokens
          const cleanedMeta = { ...meta };
          if (cleanedMeta.password) cleanedMeta.password = '[REDACTED]';
          if (cleanedMeta.token) cleanedMeta.token = '[REDACTED]';

          return `${timestamp} ${level}: ${message} ${
            Object.keys(cleanedMeta).length ? JSON.stringify(cleanedMeta) : ''
          }`;
        })
      )
    }),

    // Log a archivo en producción
    ...(process.env.NODE_ENV === 'production'
      ? [
          new winston.transports.File({
            filename: 'logs/error.log',
            level: 'error'
          }),
          new winston.transports.File({
            filename: 'logs/combined.log'
          })
        ]
      : [])
  ]
});

// Método para loguear accesos HTTP (para usar con morgan)
logger.stream = {
  write: message => logger.http(message.trim())
};

module.exports = logger;
