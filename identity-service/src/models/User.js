'use strict';

const { Model, DataTypes } = require('sequelize');
const { sequelize } = require('../config/database');

class User extends Model {}

User.init({
  id: {
    type: DataTypes.INTEGER,
    primaryKey: true,
    autoIncrement: true,
    allowNull: false
  },
  nombres: {
    type: DataTypes.STRING(100),
    allowNull: false,
    validate: {
      is: {
        args: /^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,}$/,
        msg: 'Nombres debe contener solo letras y tener al menos 2 caracteres'
      }
    }
  },
  apellidos: {
    type: DataTypes.STRING(100),
    allowNull: false,
    validate: {
      is: {
        args: /^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,}$/,
        msg: 'Apellidos debe contener solo letras y tener al menos 2 caracteres'
      }
    }
  },
  celular: {
    type: DataTypes.STRING(20),
    allowNull: true,
    validate: {
      is: {
        args: /^[0-9]{10}$/,
        msg: 'Celular debe tener 10 dígitos numéricos'
      }
    }
  },
  programa: {
    type: DataTypes.ENUM('INGENIERIA_DE_SISTEMAS', 'INGENIERIA_ELECTRONICA_Y_TELECOMUNICACIONES',
                        'AUTOMATICA_INDUSTRIAL', 'TECNOLOGIA_EN_TELEMATICA'),
    allowNull: false
  },
  rol: {
    type: DataTypes.ENUM('ESTUDIANTE', 'DOCENTE', 'ADMIN'),
    allowNull: false
  },
  email: {
    type: DataTypes.STRING(255),
    allowNull: false,
    unique: true,
    validate: {
      isEmail: {
        msg: 'El formato del email no es válido'
      },
      isInstitutional(value) {
        if (!value.toLowerCase().endsWith('@unicauca.edu.co')) {
          throw new Error('El email debe ser institucional (@unicauca.edu.co)');
        }
      }
    }
  },
  passwordHash: {
    type: DataTypes.STRING(255),
    allowNull: false,
    field: 'password_hash'
  }
}, {
  sequelize,
  modelName: 'User',
  tableName: 'usuarios',
  timestamps: true,
  underscored: true
});

module.exports = User;
