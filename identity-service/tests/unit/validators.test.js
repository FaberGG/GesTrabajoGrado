const { validateRegister, validateLogin, validateVerifyToken, validate } = require('../../src/middlewares/validators');
const { validationResult } = require('express-validator');

// Mock de express-validator
jest.mock('express-validator', () => ({
  check: jest.fn().mockImplementation((field) => {
    return {
      trim: jest.fn().mockReturnThis(),
      not: jest.fn().mockReturnThis(),
      isEmpty: jest.fn().mockReturnThis(),
      isEmail: jest.fn().mockReturnThis(),
      isLength: jest.fn().mockReturnThis(),
      matches: jest.fn().mockReturnThis(),
      isIn: jest.fn().mockReturnThis(),
      optional: jest.fn().mockReturnThis(),
      withMessage: jest.fn().mockReturnThis()
    };
  }),
  validationResult: jest.fn()
}));

describe('Validators Middleware', () => {
  let req;
  let res;
  let next;

  beforeEach(() => {
    // Reiniciar mocks
    jest.clearAllMocks();

    // Preparar mocks para req, res y next
    req = {};

    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn()
    };

    next = jest.fn();
  });

  describe('validate', () => {
    it('debería llamar a next si no hay errores', () => {
      // Configurar mock de validationResult
      validationResult.mockReturnValue({
        isEmpty: jest.fn().mockReturnValue(true),
        array: jest.fn().mockReturnValue([])
      });

      // Ejecutar middleware
      validate(req, res, next);

      // Verificaciones
      expect(validationResult).toHaveBeenCalledWith(req);
      expect(next).toHaveBeenCalled();
      expect(res.status).not.toHaveBeenCalled();
    });

    it('debería responder con error 400 si hay errores de validación', () => {
      // Configurar mock de validationResult
      const mockErrors = [
        { msg: 'El email es requerido' },
        { msg: 'El formato del email es incorrecto' }
      ];

      validationResult.mockReturnValue({
        isEmpty: jest.fn().mockReturnValue(false),
        array: jest.fn().mockReturnValue(mockErrors)
      });

      // Ejecutar middleware
      validate(req, res, next);

      // Verificaciones
      expect(validationResult).toHaveBeenCalledWith(req);
      expect(res.status).toHaveBeenCalledWith(400);
      expect(res.json).toHaveBeenCalledWith({
        success: false,
        message: 'Error de validación',
        errors: ['El email es requerido', 'El formato del email es incorrecto']
      });
      expect(next).not.toHaveBeenCalled();
    });
  });

  // Verificar que los validadores estén definidos (no podemos probar su implementación interna debido a la estructura de express-validator)
  describe('validateRegister', () => {
    it('debería ser un array de validadores', () => {
      expect(Array.isArray(validateRegister)).toBe(true);
      expect(validateRegister.length).toBeGreaterThan(0);
    });
  });

  describe('validateLogin', () => {
    it('debería ser un array de validadores', () => {
      expect(Array.isArray(validateLogin)).toBe(true);
      expect(validateLogin.length).toBeGreaterThan(0);
    });
  });

  describe('validateVerifyToken', () => {
    it('debería ser un array de validadores', () => {
      expect(Array.isArray(validateVerifyToken)).toBe(true);
      expect(validateVerifyToken.length).toBeGreaterThan(0);
    });
  });
});
