'use strict';

const { check, validationResult } = require('express-validator');

// Validación para el registro de usuario
const validateRegister = [
  check('nombres')
    .trim()
    .not().isEmpty().withMessage('El nombre es obligatorio')
    .matches(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,}$/)
    .withMessage('Nombres debe contener solo letras y tener al menos 2 caracteres'),

  check('apellidos')
    .trim()
    .not().isEmpty().withMessage('Los apellidos son obligatorios')
    .matches(/^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,}$/)
    .withMessage('Apellidos debe contener solo letras y tener al menos 2 caracteres'),

  check('celular')
    .optional()
    .matches(/^[0-9]{10}$/)
    .withMessage('Celular debe tener 10 dígitos numéricos'),

  check('programa')
    .trim()
    .not().isEmpty().withMessage('El programa es obligatorio')
    .isIn(['INGENIERIA_DE_SISTEMAS', 'INGENIERIA_ELECTRONICA_Y_TELECOMUNICACIONES',
          'AUTOMATICA_INDUSTRIAL', 'TECNOLOGIA_EN_TELEMATICA'])
    .withMessage('Programa no válido'),

  check('rol')
    .trim()
    .not().isEmpty().withMessage('El rol es obligatorio')
    .isIn(['ESTUDIANTE', 'DOCENTE', 'ADMIN'])
    .withMessage('Rol no válido'),

  check('email')
    .trim()
    .not().isEmpty().withMessage('El email es obligatorio')
    .isEmail().withMessage('Formato de email no válido')
    .matches(/^[^\s@]+@unicauca\.edu\.co$/)
    .withMessage('El email debe ser institucional (@unicauca.edu.co)'),

  check('password')
    .not().isEmpty().withMessage('La contraseña es obligatoria')
    .isLength({ min: 6 }).withMessage('La contraseña debe tener al menos 6 caracteres')
    .matches(/^(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{6,}$/)
    .withMessage('La contraseña debe contener al menos una letra mayúscula, un número y un carácter especial')
];

// Validación para el login
const validateLogin = [
  check('email')
    .trim()
    .not().isEmpty().withMessage('El email es obligatorio')
    .isEmail().withMessage('Formato de email no válido'),

  check('password')
    .not().isEmpty().withMessage('La contraseña es obligatoria')
];

// Validación para verificación de token
const validateVerifyToken = [
  check('token')
    .not().isEmpty().withMessage('El token es obligatorio')
];

// Middleware para verificar resultados de validación
const validate = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    const extractedErrors = errors.array().map(err => err.msg);
    return res.status(400).json({
      success: false,
      message: 'Error de validación',
      errors: extractedErrors
    });
  }
  next();
};

module.exports = {
  validateRegister,
  validateLogin,
  validateVerifyToken,
  validate
};
