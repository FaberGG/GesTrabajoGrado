'use strict';

const bcrypt = require('bcrypt');
const dotenv = require('dotenv');
const logger = require('../config/logger');

dotenv.config();

// Número de rondas para el hash de bcrypt (más rondas = más seguro pero más lento)
const saltRounds = parseInt(process.env.BCRYPT_ROUNDS) || 10;

/**
 * Servicio para manejar operaciones relacionadas con contraseñas
 */
class PasswordService {
  /**
   * Genera un hash seguro de la contraseña utilizando bcrypt
   * @param {string} plainPassword - Contraseña en texto plano
   * @returns {Promise<string>} - Hash de la contraseña
   */
  static async hash(plainPassword) {
    try {
      return await bcrypt.hash(plainPassword, saltRounds);
    } catch (error) {
      logger.error('Error al generar hash de contraseña', { error: error.message });
      throw new Error('Error al procesar la contraseña');
    }
  }

  /**
   * Verifica si una contraseña coincide con su hash almacenado
   * @param {string} plainPassword - Contraseña en texto plano para verificar
   * @param {string} passwordHash - Hash almacenado de la contraseña
   * @returns {Promise<boolean>} - true si coincide, false si no
   */
  static async verify(plainPassword, passwordHash) {
    try {
      return await bcrypt.compare(plainPassword, passwordHash);
    } catch (error) {
      logger.error('Error al verificar contraseña', { error: error.message });
      throw new Error('Error al verificar la contraseña');
    }
  }

  /**
   * Valida que la contraseña cumpla con la política de seguridad
   * @param {string} password - Contraseña en texto plano para validar
   * @returns {boolean} - true si la contraseña es válida según la política
   */
  static isValid(password) {
    if (!password) return false;

    // Requisitos: mínimo 6 caracteres, al menos 1 mayúscula, 1 dígito y 1 caracter especial
    const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{6,}$/;
    return passwordRegex.test(password);
  }
}

module.exports = PasswordService;
