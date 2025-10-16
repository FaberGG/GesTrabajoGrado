'use strict';

// Importaciones de paquetes
const express = require('express');
const dotenv = require('dotenv');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const rateLimit = require('express-rate-limit');

// Importaciones de archivos del proyecto
const { testConnection } = require('./config/database');
const logger = require('./config/logger');
const authRoutes = require('./routes/authRoutes');
const errorHandler = require('./middlewares/errorHandler');

// Cargar variables de entorno
dotenv.config();

// Crear aplicación Express
const app = express();

// Configuración de seguridad básica
app.use(helmet());

// Configuración de CORS
app.use(cors({
  origin: process.env.CORS_ORIGIN ? process.env.CORS_ORIGIN.split(',') : '*',
  credentials: true
}));

// Configuración de rate limit para prevenir ataques de fuerza bruta
const limiter = rateLimit({
  windowMs: process.env.RATE_LIMIT_WINDOW_MS || 15 * 60 * 1000, // 15 minutos por defecto
  max: process.env.RATE_LIMIT_MAX_REQUESTS || 100 // 100 solicitudes por ventana por defecto
});
app.use('/api/', limiter);

// Parseo de cuerpos JSON
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Logging HTTP con Morgan, salida a través de Winston
app.use(morgan('combined', { stream: logger.stream }));

// Ruta de salud para monitoreo
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'UP',
    timestamp: new Date().toISOString()
  });
});

// Rutas de la API
app.use('/api/auth', authRoutes);

// Ruta 404 para endpoints no encontrados
app.use('*', (req, res) => {
  res.status(404).json({
    success: false,
    message: 'Endpoint no encontrado'
  });
});

// Middleware de manejo de errores (siempre al final)
app.use(errorHandler);

// Puerto para el servidor
const PORT = process.env.PORT || 3000;

// Iniciar el servidor
const startServer = async () => {
  try {
    // Probar conexión a base de datos
    await testConnection();

    // Iniciar el servidor HTTP
    app.listen(PORT, () => {
      logger.info(`Servidor iniciado en el puerto ${PORT}`);
      logger.info(`Entorno: ${process.env.NODE_ENV || 'development'}`);
    });
  } catch (error) {
    logger.error('No se pudo iniciar el servidor', { error: error.message });
    process.exit(1);
  }
};

// Iniciar el servidor
startServer();

// Manejo de señales para finalización ordenada
process.on('SIGTERM', () => {
  logger.info('SIGTERM recibida, cerrando servidor...');
  process.exit(0);
});

process.on('SIGINT', () => {
  logger.info('SIGINT recibida, cerrando servidor...');
  process.exit(0);
});

// Exportar la app para pruebas
module.exports = app;
