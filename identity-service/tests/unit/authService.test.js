const AuthService = require('../../src/services/authService');
const User = require('../../src/models/User');
const PasswordService = require('../../src/services/passwordService');
const JwtHelper = require('../../src/utils/jwtHelper');

// Mocks para los módulos dependientes
jest.mock('../../src/models/User');
jest.mock('../../src/services/passwordService');
jest.mock('../../src/utils/jwtHelper');

describe('AuthService', () => {
  // Datos de prueba
  const mockUserData = {
    nombres: 'Juan Carlos',
    apellidos: 'Pérez García',
    celular: '3201234567',
    programa: 'INGENIERIA_DE_SISTEMAS',
    rol: 'ESTUDIANTE',
    email: 'jperez@unicauca.edu.co'
  };

  const mockPlainPassword = 'Pass123!';
  const mockPasswordHash = '$2b$10$abcdefghijklmnopqrstuv';

  const mockSavedUser = {
    id: 1,
    ...mockUserData,
    passwordHash: mockPasswordHash,
    toJSON: () => ({
      id: 1,
      ...mockUserData,
      passwordHash: mockPasswordHash
    })
  };

  const mockToken = 'mock.jwt.token';

  // Limpiar mocks antes de cada test
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('register', () => {
    it('debería registrar un usuario exitosamente', async () => {
      // Configurar mocks
      User.findOne.mockResolvedValue(null);
      PasswordService.isValid.mockReturnValue(true);
      PasswordService.hash.mockResolvedValue(mockPasswordHash);
      User.create.mockResolvedValue(mockSavedUser);

      // Ejecutar el método a probar
      const result = await AuthService.register(mockUserData, mockPlainPassword);

      // Verificaciones
      expect(User.findOne).toHaveBeenCalledWith({ where: { email: mockUserData.email } });
      expect(PasswordService.isValid).toHaveBeenCalledWith(mockPlainPassword);
      expect(PasswordService.hash).toHaveBeenCalledWith(mockPlainPassword);
      expect(User.create).toHaveBeenCalledWith({
        ...mockUserData,
        passwordHash: mockPasswordHash
      });

      // Verificar que el resultado no contiene passwordHash
      expect(result).not.toHaveProperty('passwordHash');
      expect(result).toHaveProperty('id', 1);
      expect(result).toHaveProperty('email', mockUserData.email);
    });

    it('debería rechazar un email no institucional', async () => {
      const invalidUserData = {
        ...mockUserData,
        email: 'jperez@gmail.com' // Email no institucional
      };

      await expect(AuthService.register(invalidUserData, mockPlainPassword))
        .rejects
        .toThrow('El email debe ser institucional (@unicauca.edu.co)');

      expect(User.findOne).not.toHaveBeenCalled();
      expect(User.create).not.toHaveBeenCalled();
    });

    it('debería rechazar una contraseña que no cumple la política', async () => {
      PasswordService.isValid.mockReturnValue(false);

      await expect(AuthService.register(mockUserData, 'weak'))
        .rejects
        .toThrow('La contraseña no cumple la política');

      expect(User.create).not.toHaveBeenCalled();
    });

    it('debería rechazar un email ya registrado', async () => {
      User.findOne.mockResolvedValue({ id: 2, email: mockUserData.email });
      PasswordService.isValid.mockReturnValue(true);

      await expect(AuthService.register(mockUserData, mockPlainPassword))
        .rejects
        .toThrow('El email ya está registrado');

      expect(User.create).not.toHaveBeenCalled();
    });
  });

  describe('login', () => {
    it('debería autenticar a un usuario correctamente', async () => {
      // Configurar mocks
      User.findOne.mockResolvedValue(mockSavedUser);
      PasswordService.verify.mockResolvedValue(true);
      JwtHelper.generateToken.mockReturnValue(mockToken);

      // Ejecutar el método a probar
      const result = await AuthService.login(mockUserData.email, mockPlainPassword);

      // Verificaciones
      expect(User.findOne).toHaveBeenCalledWith({ where: { email: mockUserData.email } });
      expect(PasswordService.verify).toHaveBeenCalledWith(mockPlainPassword, mockPasswordHash);
      expect(JwtHelper.generateToken).toHaveBeenCalledWith(mockSavedUser);

      // Verificar que la respuesta tiene la estructura correcta
      expect(result).toHaveProperty('user');
      expect(result).toHaveProperty('token', mockToken);
      expect(result.user).not.toHaveProperty('passwordHash');
    });

    it('debería rechazar un email inexistente', async () => {
      User.findOne.mockResolvedValue(null);

      await expect(AuthService.login('noexiste@unicauca.edu.co', mockPlainPassword))
        .rejects
        .toThrow('Credenciales inválidas');

      expect(PasswordService.verify).not.toHaveBeenCalled();
      expect(JwtHelper.generateToken).not.toHaveBeenCalled();
    });

    it('debería rechazar una contraseña incorrecta', async () => {
      User.findOne.mockResolvedValue(mockSavedUser);
      PasswordService.verify.mockResolvedValue(false);

      await expect(AuthService.login(mockUserData.email, 'wrongpassword'))
        .rejects
        .toThrow('Credenciales inválidas');

      expect(JwtHelper.generateToken).not.toHaveBeenCalled();
    });
  });

  describe('getProfile', () => {
    it('debería obtener el perfil de usuario correctamente', async () => {
      User.findByPk.mockResolvedValue(mockSavedUser);

      const result = await AuthService.getProfile(1);

      expect(User.findByPk).toHaveBeenCalledWith(1);
      expect(result).not.toHaveProperty('passwordHash');
      expect(result).toHaveProperty('id', 1);
      expect(result).toHaveProperty('email', mockUserData.email);
    });

    it('debería lanzar error si el usuario no existe', async () => {
      User.findByPk.mockResolvedValue(null);

      await expect(AuthService.getProfile(999))
        .rejects
        .toThrow('Usuario no encontrado');
    });
  });

  describe('verifyToken', () => {
    it('debería verificar un token válido', async () => {
      const mockPayload = {
        userId: 1,
        email: mockUserData.email,
        rol: mockUserData.rol,
        programa: mockUserData.programa
      };

      JwtHelper.verifyToken.mockReturnValue(mockPayload);
      User.findByPk.mockResolvedValue(mockSavedUser);

      const result = await AuthService.verifyToken(mockToken);

      expect(JwtHelper.verifyToken).toHaveBeenCalledWith(mockToken);
      expect(User.findByPk).toHaveBeenCalledWith(1);
      expect(result).toEqual(mockPayload);
    });

    it('debería rechazar un token inválido', async () => {
      JwtHelper.verifyToken.mockReturnValue(null);

      await expect(AuthService.verifyToken('invalid.token'))
        .rejects
        .toThrow('Token inválido o expirado');

      expect(User.findByPk).not.toHaveBeenCalled();
    });

    it('debería rechazar un token de usuario inexistente', async () => {
      const mockPayload = {
        userId: 999,
        email: 'deleted@unicauca.edu.co',
        rol: 'ESTUDIANTE',
        programa: 'INGENIERIA_DE_SISTEMAS'
      };

      JwtHelper.verifyToken.mockReturnValue(mockPayload);
      User.findByPk.mockResolvedValue(null);

      await expect(AuthService.verifyToken(mockToken))
        .rejects
        .toThrow('Usuario no encontrado');
    });
  });
});
