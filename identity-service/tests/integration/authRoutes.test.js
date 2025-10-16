const request = require('supertest');
const app = require('../../src/index');
const userData = require('../fixtures/userData');
const User = require('../../src/models/User');
const { sequelize } = require('../../src/config/database');
const JwtHelper = require('../../src/utils/jwtHelper');

// Mocks
jest.mock('../../src/models/User');
jest.mock('../../src/utils/jwtHelper');

describe('Endpoints de Autenticación', () => {
  let authToken;

  beforeAll(() => {
    // Configurar mocks globales
    JwtHelper.generateToken.mockReturnValue('mock.jwt.token');
  });

  beforeEach(() => {
    jest.clearAllMocks();

    // Configurar token de autenticación para pruebas
    authToken = 'mock.jwt.token';
  });

  describe('POST /api/auth/register', () => {
    it('debería registrar un nuevo usuario exitosamente', async () => {
      // Configurar mocks
      User.findOne.mockResolvedValue(null);
      User.create.mockResolvedValue({
        id: 1,
        ...userData.validUser,
        passwordHash: '$2b$10$hashedpassword',
        toJSON: () => ({
          id: 1,
          nombres: userData.validUser.nombres,
          apellidos: userData.validUser.apellidos,
          email: userData.validUser.email,
          rol: userData.validUser.rol,
          programa: userData.validUser.programa,
          celular: userData.validUser.celular,
          createdAt: '2025-10-15T12:00:00Z'
        })
      });

      // Realizar petición POST al endpoint
      const response = await request(app)
        .post('/api/auth/register')
        .send(userData.validUser);

      // Verificaciones
      expect(response.status).toBe(201);
      expect(response.body).toHaveProperty('success', true);
      expect(response.body).toHaveProperty('message', 'Usuario registrado exitosamente');
      expect(response.body).toHaveProperty('data');
      expect(response.body.data).toHaveProperty('id', 1);
      expect(response.body.data).toHaveProperty('email', userData.validUser.email);
      expect(response.body.data).not.toHaveProperty('passwordHash');
      expect(response.body.data).not.toHaveProperty('password');

      // Verificar llamadas a funciones mock
      expect(User.findOne).toHaveBeenCalled();
      expect(User.create).toHaveBeenCalled();
    });

    it('debería rechazar registro con email no institucional', async () => {
      // Realizar petición POST al endpoint con datos inválidos
      const response = await request(app)
        .post('/api/auth/register')
        .send(userData.userWithInvalidEmail);

      // Verificaciones
      expect(response.status).toBe(400);
      expect(response.body).toHaveProperty('success', false);
      expect(response.body).toHaveProperty('errors');
      expect(Array.isArray(response.body.errors)).toBe(true);
      expect(response.body.errors.some(e => e.includes('email') && e.includes('institucional'))).toBe(true);
    });

    it('debería rechazar registro con contraseña débil', async () => {
      // Realizar petición POST al endpoint con datos inválidos
      const response = await request(app)
        .post('/api/auth/register')
        .send(userData.userWithWeakPassword);

      // Verificaciones
      expect(response.status).toBe(400);
      expect(response.body).toHaveProperty('success', false);
      expect(response.body).toHaveProperty('errors');
      expect(Array.isArray(response.body.errors)).toBe(true);
      expect(response.body.errors.some(e => e.includes('contraseña'))).toBe(true);
    });

    it('debería rechazar registro con email ya existente', async () => {
      // Configurar mocks
      User.findOne.mockResolvedValue({ id: 1, email: userData.validUser.email });

      // Realizar petición POST al endpoint
      const response = await request(app)
        .post('/api/auth/register')
        .send(userData.validUser);

      // Verificaciones
      expect(response.status).toBe(409); // Conflict
      expect(response.body).toHaveProperty('success', false);
      expect(response.body).toHaveProperty('message');
      expect(response.body.message).toContain('ya está registrado');
    });
  });

  describe('POST /api/auth/login', () => {
    it('debería autenticar exitosamente con credenciales válidas', async () => {
      // Configurar mocks
      User.findOne.mockResolvedValue({
        id: 1,
        ...userData.validUser,
        passwordHash: '$2b$10$hashedpassword',
        toJSON: () => ({
          id: 1,
          nombres: userData.validUser.nombres,
          apellidos: userData.validUser.apellidos,
          email: userData.validUser.email,
          rol: userData.validUser.rol,
          programa: userData.validUser.programa
        })
      });

      // Mock para verificación de contraseña
      const PasswordService = require('../../src/services/passwordService');
      PasswordService.verify = jest.fn().mockResolvedValue(true);

      // Realizar petición POST al endpoint
      const response = await request(app)
        .post('/api/auth/login')
        .send(userData.validLogin);

      // Verificaciones
      expect(response.status).toBe(200);
      expect(response.body).toHaveProperty('success', true);
      expect(response.body).toHaveProperty('message', 'Login exitoso');
      expect(response.body).toHaveProperty('data');
      expect(response.body.data).toHaveProperty('user');
      expect(response.body.data).toHaveProperty('token');
      expect(response.body.data.user).toHaveProperty('id', 1);
      expect(response.body.data.user).toHaveProperty('email', userData.validLogin.email);
      expect(response.body.data.user).not.toHaveProperty('passwordHash');
    });

    it('debería rechazar login con credenciales incorrectas', async () => {
      // Configurar mocks
      User.findOne.mockResolvedValue({
        id: 1,
        ...userData.validUser,
        passwordHash: '$2b$10$hashedpassword'
      });

      // Mock para verificación de contraseña fallida
      const PasswordService = require('../../src/services/passwordService');
      PasswordService.verify = jest.fn().mockResolvedValue(false);

      // Realizar petición POST al endpoint
      const response = await request(app)
        .post('/api/auth/login')
        .send(userData.invalidLogin);

      // Verificaciones
      expect(response.status).toBe(401);
      expect(response.body).toHaveProperty('success', false);
      expect(response.body).toHaveProperty('message', 'Credenciales inválidas');
    });

    it('debería rechazar login con email no existente', async () => {
      // Configurar mocks
      User.findOne.mockResolvedValue(null);

      // Realizar petición POST al endpoint
      const response = await request(app)
        .post('/api/auth/login')
        .send({
          email: 'noexiste@unicauca.edu.co',
          password: 'Password123!'
        });

      // Verificaciones
      expect(response.status).toBe(401);
      expect(response.body).toHaveProperty('success', false);
      expect(response.body).toHaveProperty('message', 'Credenciales inválidas');
    });
  });

  describe('GET /api/auth/profile', () => {
    it('debería obtener el perfil del usuario autenticado', async () => {
      // Configurar mocks
      const mockUser = {
        id: 1,
        nombres: 'Juan Carlos',
        apellidos: 'Pérez García',
        celular: '3201234567',
        email: 'jperez@unicauca.edu.co',
        rol: 'ESTUDIANTE',
        programa: 'INGENIERIA_DE_SISTEMAS',
        createdAt: '2025-10-15T12:00:00Z',
        updatedAt: '2025-10-15T12:00:00Z',
        toJSON: function() {
          return {
            id: this.id,
            nombres: this.nombres,
            apellidos: this.apellidos,
            celular: this.celular,
            email: this.email,
            rol: this.rol,
            programa: this.programa,
            createdAt: this.createdAt,
            updatedAt: this.updatedAt
          };
        }
      };

      User.findByPk.mockResolvedValue(mockUser);

      // Mock para el middleware de autenticación
      const authMiddleware = require('../../src/middlewares/authMiddleware');
      jest.mock('../../src/middlewares/authMiddleware', () => {
        return (req, res, next) => {
          req.user = {
            userId: 1,
            email: 'jperez@unicauca.edu.co'
          };
          next();
        };
      });

      // Realizar petición GET al endpoint
      const response = await request(app)
        .get('/api/auth/profile')
        .set('Authorization', `Bearer ${authToken}`);

      // Verificaciones
      expect(response.status).toBe(200);
      expect(response.body).toHaveProperty('success', true);
      expect(response.body).toHaveProperty('data');
      expect(response.body.data).toHaveProperty('id', 1);
      expect(response.body.data).toHaveProperty('email', 'jperez@unicauca.edu.co');
      expect(response.body.data).not.toHaveProperty('passwordHash');
    });

    it('debería rechazar acceso sin token de autenticación', async () => {
      // Realizar petición GET al endpoint sin token
      const response = await request(app)
        .get('/api/auth/profile');

      // Verificaciones
      expect(response.status).toBe(401);
      expect(response.body).toHaveProperty('success', false);
    });
  });

  describe('GET /api/auth/roles', () => {
    it('debería obtener la lista de roles y programas', async () => {
      // Configurar mocks
      User.getAttributes = jest.fn().mockReturnValue({
        rol: {
          values: ['ESTUDIANTE', 'DOCENTE', 'ADMIN']
        },
        programa: {
          values: ['INGENIERIA_DE_SISTEMAS', 'INGENIERIA_ELECTRONICA_Y_TELECOMUNICACIONES', 'AUTOMATICA_INDUSTRIAL', 'TECNOLOGIA_EN_TELEMATICA']
        }
      });

      // Mock para el middleware de autenticación
      const authMiddleware = require('../../src/middlewares/authMiddleware');
      jest.mock('../../src/middlewares/authMiddleware', () => {
        return (req, res, next) => {
          req.user = {
            userId: 1,
            email: 'jperez@unicauca.edu.co'
          };
          next();
        };
      });

      // Realizar petición GET al endpoint
      const response = await request(app)
        .get('/api/auth/roles')
        .set('Authorization', `Bearer ${authToken}`);

      // Verificaciones
      expect(response.status).toBe(200);
      expect(response.body).toHaveProperty('success', true);
      expect(response.body).toHaveProperty('data');
      expect(response.body.data).toHaveProperty('roles');
      expect(response.body.data).toHaveProperty('programas');
      expect(Array.isArray(response.body.data.roles)).toBe(true);
      expect(Array.isArray(response.body.data.programas)).toBe(true);
    });
  });

  describe('POST /api/auth/verify-token', () => {
    it('debería verificar un token válido', async () => {
      // Configurar mocks
      const mockPayload = {
        userId: 1,
        email: 'jperez@unicauca.edu.co',
        rol: 'ESTUDIANTE',
        programa: 'INGENIERIA_DE_SISTEMAS'
      };

      JwtHelper.verifyToken.mockReturnValue(mockPayload);
      User.findByPk.mockResolvedValue({
        id: 1,
        email: 'jperez@unicauca.edu.co'
      });

      // Realizar petición POST al endpoint
      const response = await request(app)
        .post('/api/auth/verify-token')
        .send({ token: authToken });

      // Verificaciones
      expect(response.status).toBe(200);
      expect(response.body).toHaveProperty('success', true);
      expect(response.body).toHaveProperty('valid', true);
      expect(response.body).toHaveProperty('data');
      expect(response.body.data).toEqual(mockPayload);
    });

    it('debería indicar cuando un token es inválido', async () => {
      // Configurar mocks
      JwtHelper.verifyToken.mockReturnValue(null);

      // Realizar petición POST al endpoint
      const response = await request(app)
        .post('/api/auth/verify-token')
        .send({ token: 'invalid.token' });

      // Verificaciones
      expect(response.status).toBe(401);
      expect(response.body).toHaveProperty('success', false);
      expect(response.body).toHaveProperty('valid', false);
      expect(response.body).toHaveProperty('message');
      expect(response.body.message).toContain('inválido');
    });
  });
});
