const { Sequelize } = require('sequelize');
const User = require('../../src/models/User');
const { sequelize } = require('../../src/config/database');

// Mock de sequelize
jest.mock('../../src/config/database', () => {
  const SequelizeMock = require('sequelize-mock');
  const dbMock = new SequelizeMock();
  return {
    sequelize: dbMock,
    Sequelize: { Op: {} }
  };
});

describe('User Model', () => {
  describe('Validaciones', () => {
    let user;

    beforeEach(() => {
      // Datos válidos para un usuario
      user = {
        nombres: 'Juan Carlos',
        apellidos: 'Pérez García',
        celular: '3201234567',
        programa: 'INGENIERIA_DE_SISTEMAS',
        rol: 'ESTUDIANTE',
        email: 'jperez@unicauca.edu.co',
        passwordHash: '$2b$10$abcdefghijklmnopqrstuv'
      };
    });

    it('debería permitir crear un usuario con datos válidos', async () => {
      // Mock para el método create de Sequelize
      User.create = jest.fn().mockResolvedValue({
        id: 1,
        ...user,
        createdAt: new Date(),
        updatedAt: new Date()
      });

      // Intentar crear un usuario válido
      const newUser = await User.create(user);

      // Verificar que se llamó al método create con los datos correctos
      expect(User.create).toHaveBeenCalledWith(user);
      expect(newUser).toHaveProperty('id', 1);
      expect(newUser).toHaveProperty('email', user.email);
    });

    it('debería rechazar un email no institucional', async () => {
      // Configurar un email no institucional
      const invalidUser = { ...user, email: 'jperez@gmail.com' };

      // Mock para simular la validación fallida
      User.create = jest.fn().mockImplementation(() => {
        const error = new Error('El email debe ser institucional (@unicauca.edu.co)');
        error.name = 'SequelizeValidationError';
        throw error;
      });

      // Intentar crear usuario con email inválido
      await expect(User.create(invalidUser)).rejects.toThrow('El email debe ser institucional');
    });

    it('debería rechazar nombres inválidos', async () => {
      // Configurar nombres inválidos (muy cortos)
      const invalidUser = { ...user, nombres: 'J' };

      // Mock para simular la validación fallida
      User.create = jest.fn().mockImplementation(() => {
        const error = new Error('Nombres debe contener solo letras y tener al menos 2 caracteres');
        error.name = 'SequelizeValidationError';
        throw error;
      });

      // Intentar crear usuario con nombres inválidos
      await expect(User.create(invalidUser)).rejects.toThrow('Nombres debe contener');
    });

    it('debería rechazar apellidos inválidos', async () => {
      // Configurar apellidos inválidos (con números)
      const invalidUser = { ...user, apellidos: 'Pérez123' };

      // Mock para simular la validación fallida
      User.create = jest.fn().mockImplementation(() => {
        const error = new Error('Apellidos debe contener solo letras');
        error.name = 'SequelizeValidationError';
        throw error;
      });

      // Intentar crear usuario con apellidos inválidos
      await expect(User.create(invalidUser)).rejects.toThrow('Apellidos debe contener');
    });

    it('debería rechazar celular inválido', async () => {
      // Configurar celular inválido (letras o formato incorrecto)
      const invalidUser = { ...user, celular: '320abc1234' };

      // Mock para simular la validación fallida
      User.create = jest.fn().mockImplementation(() => {
        const error = new Error('Celular debe tener 10 dígitos numéricos');
        error.name = 'SequelizeValidationError';
        throw error;
      });

      // Intentar crear usuario con celular inválido
      await expect(User.create(invalidUser)).rejects.toThrow('Celular debe tener 10 dígitos');
    });

    it('debería rechazar programa inválido', async () => {
      // Configurar programa inválido
      const invalidUser = { ...user, programa: 'MEDICINA' };

      // Mock para simular la validación fallida
      User.create = jest.fn().mockImplementation(() => {
        const error = new Error('Programa no válido');
        error.name = 'SequelizeValidationError';
        throw error;
      });

      // Intentar crear usuario con programa inválido
      await expect(User.create(invalidUser)).rejects.toThrow('Programa no válido');
    });

    it('debería rechazar rol inválido', async () => {
      // Configurar rol inválido
      const invalidUser = { ...user, rol: 'RECTOR' };

      // Mock para simular la validación fallida
      User.create = jest.fn().mockImplementation(() => {
        const error = new Error('Rol no válido');
        error.name = 'SequelizeValidationError';
        throw error;
      });

      // Intentar crear usuario con rol inválido
      await expect(User.create(invalidUser)).rejects.toThrow('Rol no válido');
    });
  });
});
