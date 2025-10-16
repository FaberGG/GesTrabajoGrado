'use strict';

const dotenv = require('dotenv');

dotenv.config();

module.exports = {
  secret: process.env.JWT_SECRET || 'your-super-secret-jwt-key-change-in-production-MINIMUM-32-CHARACTERS',
  expiration: process.env.JWT_EXPIRATION || '1h',
  algorithm: 'HS256'
};
