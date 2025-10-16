const PasswordService = require('../../src/services/passwordService');

describe('PasswordService', () => {
  describe('hash', () => {
    it('debería generar un hash diferente al password original', async () => {
      const plainPassword = 'Password123!';
      const hash = await PasswordService.hash(plainPassword);

      expect(hash).not.toBe(plainPassword);
      expect(hash).toMatch(/^\$2[aby]\$\d+\$/); // Formato de hash bcrypt
    });

    it('debería generar hashes diferentes para la misma contraseña', async () => {
      const plainPassword = 'Password123!';
      const hash1 = await PasswordService.hash(plainPassword);
      const hash2 = await PasswordService.hash(plainPassword);

      expect(hash1).not.toBe(hash2);
    });
  });

  describe('verify', () => {
    it('debería verificar correctamente una contraseña válida', async () => {
      const plainPassword = 'Password123!';
      const hash = await PasswordService.hash(plainPassword);

      const isValid = await PasswordService.verify(plainPassword, hash);
      expect(isValid).toBe(true);
    });

    it('debería rechazar una contraseña incorrecta', async () => {
      const plainPassword = 'Password123!';
      const wrongPassword = 'WrongPassword123!';
      const hash = await PasswordService.hash(plainPassword);

      const isValid = await PasswordService.verify(wrongPassword, hash);
      expect(isValid).toBe(false);
    });
  });

  describe('isValid', () => {
    it('debería validar una contraseña que cumple la política', () => {
      const validPasswords = [
        'Password123!',
        'SecretA1@',
        'Abcd12#$'
      ];

      validPasswords.forEach(password => {
        expect(PasswordService.isValid(password)).toBe(true);
      });
    });

    it('debería rechazar contraseñas que no cumplen la política', () => {
      const invalidPasswords = [
        'password', // sin mayúscula, sin número, sin especial
        'Password', // sin número, sin especial
        'password123', // sin mayúscula, sin especial
        'password!', // sin mayúscula, sin número
        'Pass1!', // menos de 6 caracteres
        null,
        ''
      ];

      invalidPasswords.forEach(password => {
        expect(PasswordService.isValid(password)).toBe(false);
      });
    });
  });
});
