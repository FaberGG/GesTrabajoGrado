// Datos de prueba para las pruebas de integración
module.exports = {
  // Usuario válido para pruebas de registro
  validUser: {
    nombres: 'Juan Carlos',
    apellidos: 'Pérez García',
    celular: '3201234567',
    programa: 'INGENIERIA_DE_SISTEMAS',
    rol: 'ESTUDIANTE',
    email: 'jperez@unicauca.edu.co',
    password: 'Password123!'
  },

  // Usuario con email inválido
  userWithInvalidEmail: {
    nombres: 'María',
    apellidos: 'López',
    celular: '3209876543',
    programa: 'INGENIERIA_ELECTRONICA_Y_TELECOMUNICACIONES',
    rol: 'ESTUDIANTE',
    email: 'mlopez@gmail.com', // No institucional
    password: 'Password123!'
  },

  // Usuario con contraseña débil
  userWithWeakPassword: {
    nombres: 'Pedro',
    apellidos: 'Ramírez',
    celular: '3201112233',
    programa: 'AUTOMATICA_INDUSTRIAL',
    rol: 'DOCENTE',
    email: 'pramirez@unicauca.edu.co',
    password: 'password' // Sin mayúsculas, números ni caracteres especiales
  },

  // Datos de login válidos
  validLogin: {
    email: 'jperez@unicauca.edu.co',
    password: 'Password123!'
  },

  // Datos de login inválidos
  invalidLogin: {
    email: 'jperez@unicauca.edu.co',
    password: 'WrongPassword123!'
  }
};

