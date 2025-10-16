'use strict';

const express = require('express');
const router = express.Router();
const AuthController = require('../controllers/authController');
const { validateRegister, validateLogin, validateVerifyToken, validate } = require('../middlewares/validators');
const authMiddleware = require('../middlewares/authMiddleware');

// Rutas públicas - no requieren autenticación
router.post('/register', validateRegister, validate, AuthController.register);
router.post('/login', validateLogin, validate, AuthController.login);

// Rutas privadas - requieren autenticación (token JWT)
router.get('/profile', authMiddleware, AuthController.getProfile);
router.get('/roles', authMiddleware, AuthController.getRoles);

// Ruta para verificación de token (uso interno entre servicios)
router.post('/verify-token', validateVerifyToken, validate, AuthController.verifyToken);

module.exports = router;
