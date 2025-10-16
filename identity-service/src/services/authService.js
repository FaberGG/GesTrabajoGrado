'use strict';

const User = require('../models/User');
const PasswordService = require('./passwordService');
const JwtHelper = require('../utils/jwtHelper');
const logger = require('../config/logger');

/**
 * Servicio que maneja la lógica de negocio para autenticación y registro
 */
class AuthService {
  /**
   * Registra un nuevo usuario en el sistema
   *
   * @param {Object} userData - Datos del usuario a registrar
   * @param {string} plainPassword - Contraseña en texto plano
   * @returns {Promise<Object>} - Usuario creado (sin password_hash)
   * @throws {Error} Si hay problemas en la validación o creación
   */
  static async register(userData, plainPassword) {
    try {
      // Validar email institucional
      if (!userData.email || !userData.email.toLowerCase().endsWith('@unicauca.edu.co')) {
        throw new Error('El email debe ser institucional (@unicauca.edu.co)');
      }

      // Validar política de contraseñas
      if (!PasswordService.isValid(plainPassword)) {
        throw new Error('La contraseña no cumple la política (min 6, 1 dígito, 1 mayúscula, 1 especial)');
      }

      // Verificar si el email ya existe
      const existingUser = await User.findOne({ where: { email: userData.email } });
      if (existingUser) {
        throw new Error('El email ya está registrado');
      }

      // Generar hash de la contraseña
      const passwordHash = await PasswordService.hash(plainPassword);

      // Crear el usuario en la base de datos
      const newUser = await User.create({
        ...userData,
        passwordHash
      });

      // Excluir el hash de la contraseña en la respuesta
      const userResponse = newUser.toJSON();
      delete userResponse.passwordHash;

      logger.info('Usuario registrado exitosamente', { userId: newUser.id, email: newUser.email });
      return userResponse;
    } catch (error) {
      logger.error('Error en el registro de usuario', { error: error.message, email: userData.email });
      throw error;
    }
  }

  /**
   * Autentica a un usuario y genera un token JWT
   *
   * @param {string} email - Email del usuario
   * @param {string} plainPassword - Contraseña en texto plano
   * @returns {Promise<Object>} - Usuario y token JWT
   * @throws {Error} Si las credenciales son inválidas
   */
  static async login(email, plainPassword) {
    try {
      // Buscar usuario por email
      const user = await User.findOne({ where: { email } });
      if (!user) {
        throw new Error('Credenciales inválidas');
      }

      // Verificar contraseña
      const isPasswordValid = await PasswordService.verify(plainPassword, user.passwordHash);
      if (!isPasswordValid) {
        throw new Error('Credenciales inválidas');
      }

      // Generar JWT
      const token = JwtHelper.generateToken(user);

      // Excluir el hash de la contraseña en la respuesta
      const userResponse = user.toJSON();
      delete userResponse.passwordHash;

      logger.info('Usuario autenticado exitosamente', { userId: user.id, email: user.email });
      return { user: userResponse, token };
    } catch (error) {
      logger.error('Error en la autenticación', { error: error.message, email });
      throw error;
    }
  }

  /**
   * Obtiene los datos del perfil de usuario
   *
   * @param {number} userId - ID del usuario
   * @returns {Promise<Object>} - Datos del usuario
   * @throws {Error} Si el usuario no existe
   */
  static async getProfile(userId) {
    try {
      const user = await User.findByPk(userId);
      if (!user) {
        throw new Error('Usuario no encontrado');
      }

      // Excluir el hash de la contraseña en la respuesta
      const userResponse = user.toJSON();
      delete userResponse.passwordHash;

      return userResponse;
    } catch (error) {
      logger.error('Error al obtener perfil de usuario', { error: error.message, userId });
      throw error;
    }
  }

  /**
   * Verifica la validez de un token JWT
   *
   * @param {string} token - Token JWT a verificar
   * @returns {Promise<Object>} - Información del token decodificado
   * @throws {Error} Si el token es inválido
   */
  static async verifyToken(token) {
    try {
      const payload = JwtHelper.verifyToken(token);
      if (!payload) {
        throw new Error('Token inválido o expirado');
      }

      // Verificar que el usuario siga existiendo en la base de datos
      const user = await User.findByPk(payload.userId);
      if (!user) {
        throw new Error('Usuario no encontrado');
      }

      return {
        userId: payload.userId,
        email: payload.email,
        rol: payload.rol,
        programa: payload.programa
      };
    } catch (error) {
      logger.error('Error al verificar token', { error: error.message });
      throw error;
    }
  }

  /**
   * Obtiene los roles y programas disponibles
   *
   * @returns {Object} - Lista de roles y programas
   */
  static async getRolesAndPrograms() {
    return {
      roles: User.getAttributes().rol.values,
      programas: User.getAttributes().programa.values
    };
  }
}

module.exports = AuthService;
