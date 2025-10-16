const JwtHelper = require('../../src/utils/jwtHelper');
const jwt = require('jsonwebtoken');
const jwtConfig = require('../../src/config/jwt');

// Mock para jwt.verify y jwt.sign
jest.mock('jsonwebtoken');

describe('JwtHelper', () => {
  const mockUser = {
    id: 1,
    email: 'test@unicauca.edu.co',
    rol: 'ESTUDIANTE',
    programa: 'INGENIERIA_DE_SISTEMAS'
  };

  const mockToken = 'mock.jwt.token';
  const mockPayload = {
    userId: mockUser.id,
    email: mockUser.email,
    rol: mockUser.rol,
    programa: mockUser.programa
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('generateToken', () => {
    it('debería generar un token JWT con el payload correcto', () => {
      jwt.sign.mockReturnValue(mockToken);

      const token = JwtHelper.generateToken(mockUser);

      expect(token).toBe(mockToken);
      expect(jwt.sign).toHaveBeenCalledWith(
        {
          userId: mockUser.id,
          email: mockUser.email,
          rol: mockUser.rol,
          programa: mockUser.programa
        },
        jwtConfig.secret,
        {
          expiresIn: jwtConfig.expiration,
          algorithm: jwtConfig.algorithm
        }
      );
    });

    it('debería manejar errores al generar tokens', () => {
      jwt.sign.mockImplementation(() => {
        throw new Error('JWT error');
      });

      expect(() => {
        JwtHelper.generateToken(mockUser);
      }).toThrow('Error al generar el token de autenticación');
    });
  });

  describe('verifyToken', () => {
    it('debería verificar un token JWT válido', () => {
      jwt.verify.mockReturnValue(mockPayload);

      const result = JwtHelper.verifyToken(mockToken);

      expect(result).toEqual(mockPayload);
      expect(jwt.verify).toHaveBeenCalledWith(mockToken, jwtConfig.secret);
    });

    it('debería devolver null para un token inválido', () => {
      jwt.verify.mockImplementation(() => {
        throw new Error('Token inválido');
      });

      const result = JwtHelper.verifyToken('invalid.token');

      expect(result).toBeNull();
      expect(jwt.verify).toHaveBeenCalledWith('invalid.token', jwtConfig.secret);
    });
  });

  describe('extractTokenFromHeader', () => {
    it('debería extraer el token del header de autorización', () => {
      const req = {
        headers: {
          authorization: `Bearer ${mockToken}`
        }
      };

      const token = JwtHelper.extractTokenFromHeader(req);

      expect(token).toBe(mockToken);
    });

    it('debería devolver null si no hay header de autorización', () => {
      const req = { headers: {} };

      const token = JwtHelper.extractTokenFromHeader(req);

      expect(token).toBeNull();
    });

    it('debería devolver null si el formato del header es incorrecto', () => {
      const req = {
        headers: {
          authorization: `Basic ${mockToken}`
        }
      };

      const token = JwtHelper.extractTokenFromHeader(req);

      expect(token).toBeNull();
    });
  });
});
