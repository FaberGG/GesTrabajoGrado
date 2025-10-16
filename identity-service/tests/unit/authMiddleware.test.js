const authMiddleware = require('../../src/middlewares/authMiddleware');
const JwtHelper = require('../../src/utils/jwtHelper');

// Mock para el helper JWT
jest.mock('../../src/utils/jwtHelper');

describe('authMiddleware', () => {
  let req;
  let res;
  let next;

  beforeEach(() => {
    // Reiniciar mocks
    jest.clearAllMocks();

    // Preparar mocks para req, res y next
    req = {
      headers: {}
    };

    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn()
    };

    next = jest.fn();
  });

  it('debería continuar si el token es válido', () => {
    // Preparar datos de prueba
    const token = 'valid.jwt.token';
    const payload = {
      userId: 1,
      email: 'test@unicauca.edu.co',
      rol: 'ESTUDIANTE',
      programa: 'INGENIERIA_DE_SISTEMAS'
    };

    // Configurar mocks
    req.headers.authorization = `Bearer ${token}`;
    JwtHelper.extractTokenFromHeader.mockReturnValue(token);
    JwtHelper.verifyToken.mockReturnValue(payload);

    // Ejecutar el middleware
    authMiddleware(req, res, next);

    // Verificaciones
    expect(JwtHelper.extractTokenFromHeader).toHaveBeenCalledWith(req);
    expect(JwtHelper.verifyToken).toHaveBeenCalledWith(token);
    expect(req.user).toEqual({
      userId: payload.userId,
      email: payload.email,
      rol: payload.rol,
      programa: payload.programa
    });
    expect(next).toHaveBeenCalled();
    expect(res.status).not.toHaveBeenCalled();
  });

  it('debería rechazar petición si no hay token', () => {
    // Configurar mocks
    JwtHelper.extractTokenFromHeader.mockReturnValue(null);

    // Ejecutar el middleware
    authMiddleware(req, res, next);

    // Verificaciones
    expect(JwtHelper.extractTokenFromHeader).toHaveBeenCalledWith(req);
    expect(JwtHelper.verifyToken).not.toHaveBeenCalled();
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({
      success: false,
      message: 'Acceso denegado. Token no proporcionado.'
    });
    expect(next).not.toHaveBeenCalled();
  });

  it('debería rechazar petición si el token es inválido', () => {
    // Preparar datos de prueba
    const token = 'invalid.jwt.token';

    // Configurar mocks
    req.headers.authorization = `Bearer ${token}`;
    JwtHelper.extractTokenFromHeader.mockReturnValue(token);
    JwtHelper.verifyToken.mockReturnValue(null);

    // Ejecutar el middleware
    authMiddleware(req, res, next);

    // Verificaciones
    expect(JwtHelper.extractTokenFromHeader).toHaveBeenCalledWith(req);
    expect(JwtHelper.verifyToken).toHaveBeenCalledWith(token);
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({
      success: false,
      message: 'Token no válido o expirado'
    });
    expect(next).not.toHaveBeenCalled();
  });

  it('debería manejar errores que ocurran durante la verificación', () => {
    // Preparar datos de prueba
    const token = 'error.jwt.token';

    // Configurar mocks
    req.headers.authorization = `Bearer ${token}`;
    JwtHelper.extractTokenFromHeader.mockReturnValue(token);
    JwtHelper.verifyToken.mockImplementation(() => {
      throw new Error('Error inesperado');
    });

    // Ejecutar el middleware
    authMiddleware(req, res, next);

    // Verificaciones
    expect(JwtHelper.extractTokenFromHeader).toHaveBeenCalledWith(req);
    expect(JwtHelper.verifyToken).toHaveBeenCalledWith(token);
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({
      success: false,
      message: 'Error en la autenticación'
    });
    expect(next).not.toHaveBeenCalled();
  });
});
