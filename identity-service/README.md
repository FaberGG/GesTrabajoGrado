# Microservicio de Identidad (Identity Service)

Este microservicio gestiona la autenticación y la identidad de los usuarios para la aplicación de gestión de trabajos de grado. Implementa un sistema completo de registro, login y verificación de tokens JWT.

## Tecnologías Utilizadas

- **Runtime:** Node.js 18+ LTS
- **Framework:** Express.js 4.x
- **Base de Datos:** PostgreSQL 15+
- **ORM:** Sequelize 6.x
- **Autenticación:** JWT (jsonwebtoken)
- **Hash de Passwords:** bcrypt (10 rounds)
- **Validaciones:** express-validator
- **Variables de Entorno:** dotenv
- **Containerización:** Docker + Docker Compose
- **Logging:** winston

## Requisitos Previos

- Node.js 18+ instalado
- Docker y Docker Compose instalados (recomendado)
- PostgreSQL 15+ instalado (si no se usa Docker)

## Instalación y Configuración

### Con Docker (Recomendado)

```bash
# Clonar repositorio
git clone <repo>
cd identity-service

# Configurar variables de entorno
cp .env.example .env
# Editar .env con tus valores

# Iniciar los contenedores
npm run docker:up

# Ejecutar migraciones
npm run migrate
```

### Sin Docker

```bash
# Clonar repositorio
git clone <repo>
cd identity-service

# Instalar dependencias
npm install

# Configurar variables de entorno
cp .env.example .env
# Editar .env con tus valores (importante configurar la conexión a PostgreSQL)

# Ejecutar migraciones
npm run migrate

# Iniciar en desarrollo
npm run dev

# O iniciar en producción
npm start
```

## Endpoints de la API

### 1. Registro de Usuario

**POST `/api/auth/register`**

Registra un nuevo usuario en el sistema.

**Request:**
```json
{
  "nombres": "Juan Carlos",
  "apellidos": "Pérez García",
  "celular": "3201234567",
  "programa": "INGENIERIA_DE_SISTEMAS",
  "rol": "ESTUDIANTE",
  "email": "jperez@unicauca.edu.co",
  "password": "Pass123!"
}
```

**Response (201):**
```json
{
  "success": true,
  "message": "Usuario registrado exitosamente",
  "data": {
    "id": 1,
    "nombres": "Juan Carlos",
    "apellidos": "Pérez García",
    "email": "jperez@unicauca.edu.co",
    "rol": "ESTUDIANTE",
    "programa": "INGENIERIA_DE_SISTEMAS",
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

### 2. Login

**POST `/api/auth/login`**

Autentica al usuario y devuelve un token JWT.

**Request:**
```json
{
  "email": "jperez@unicauca.edu.co",
  "password": "Pass123!"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "user": {
      "id": 1,
      "nombres": "Juan Carlos",
      "apellidos": "Pérez García",
      "email": "jperez@unicauca.edu.co",
      "rol": "ESTUDIANTE",
      "programa": "INGENIERIA_DE_SISTEMAS"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

### 3. Perfil de Usuario

**GET `/api/auth/profile`**

Obtiene el perfil del usuario autenticado.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "nombres": "Juan Carlos",
    "apellidos": "Pérez García",
    "celular": "3201234567",
    "email": "jperez@unicauca.edu.co",
    "rol": "ESTUDIANTE",
    "programa": "INGENIERIA_DE_SISTEMAS",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

### 4. Roles y Programas

**GET `/api/auth/roles`**

Obtiene la lista de roles y programas disponibles.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200):**
```json
{
  "success": true,
  "data": {
    "roles": ["ESTUDIANTE", "DOCENTE", "ADMIN"],
    "programas": [
      "INGENIERIA_DE_SISTEMAS",
      "INGENIERIA_ELECTRONICA_Y_TELECOMUNICACIONES",
      "AUTOMATICA_INDUSTRIAL",
      "TECNOLOGIA_EN_TELEMATICA"
    ]
  }
}
```

### 5. Verificación de Token

**POST `/api/auth/verify-token`**

Verifica la validez de un token JWT (uso interno entre servicios).

**Request:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200):**
```json
{
  "success": true,
  "valid": true,
  "data": {
    "userId": 1,
    "email": "jperez@unicauca.edu.co",
    "rol": "ESTUDIANTE",
    "programa": "INGENIERIA_DE_SISTEMAS"
  }
}
```

## Variables de Entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| NODE_ENV | Entorno de ejecución | development |
| PORT | Puerto para el servidor | 3000 |
| DATABASE_URL | URL de conexión a PostgreSQL | postgresql://identity_user:identity_pass@localhost:5432/identity_db |
| DB_HOST | Host de la base de datos | localhost |
| DB_PORT | Puerto de la base de datos | 5432 |
| DB_NAME | Nombre de la base de datos | identity_db |
| DB_USER | Usuario de la base de datos | identity_user |
| DB_PASSWORD | Contraseña de la base de datos | identity_pass |
| JWT_SECRET | Clave secreta para firmar JWT | (32+ caracteres aleatorios) |
| JWT_EXPIRATION | Tiempo de validez del JWT | 1h |
| CORS_ORIGIN | Orígenes permitidos para CORS | http://localhost:8080,http://localhost:3001 |
| LOG_LEVEL | Nivel de detalle para logs | debug |
| BCRYPT_ROUNDS | Rondas de encriptación bcrypt | 10 |
| RATE_LIMIT_WINDOW_MS | Ventana de tiempo para rate limit (ms) | 900000 |
| RATE_LIMIT_MAX_REQUESTS | Máx. requests por ventana de tiempo | 100 |

## Comandos Disponibles

```bash
# Iniciar en desarrollo con hot reload
npm run dev

# Iniciar en producción
npm start

# Ejecutar migraciones
npm run migrate

# Revertir última migración
npm run migrate:undo

# Cargar datos semilla
npm run seed

# Pruebas
npm test

# Verificar estilo de código
npm run lint

# Formatear código
npm run format

# Docker: Levantar contenedores
npm run docker:up

# Docker: Detener contenedores y eliminar volúmenes
npm run docker:down

# Docker: Ver logs del servicio
npm run docker:logs
```

## Estructura de Datos

### Tabla: usuarios

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER | Identificador único, clave primaria |
| nombres | VARCHAR(100) | Nombres del usuario |
| apellidos | VARCHAR(100) | Apellidos del usuario |
| celular | VARCHAR(20) | Número de celular (opcional) |
| programa | VARCHAR(100) | Programa académico |
| rol | VARCHAR(20) | Rol del usuario |
| email | VARCHAR(255) | Email institucional, único |
| password_hash | VARCHAR(255) | Hash de la contraseña con bcrypt |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Fecha de última actualización |

## Seguridad

- Contraseñas hasheadas con bcrypt (10 rondas)
- Autenticación stateless con JWT
- Protección contra ataques CSRF y XSS con Helmet
- Rate limiting para prevenir ataques de fuerza bruta
- Validación y sanitización de todas las entradas
- No se exponen datos sensibles en respuestas ni logs

## Deployment para Producción

Para un despliegue en producción:

1. Asegúrate de cambiar las siguientes variables:
   - `NODE_ENV=production`
   - `JWT_SECRET` (usar un valor seguro y aleatorio)
   - `DB_PASSWORD` (usar una contraseña fuerte)

2. Considera usar un proxy inverso como Nginx

3. Configura correctamente CORS_ORIGIN con los dominios permitidos

4. Habilita HTTPS/TLS para todas las comunicaciones

## Troubleshooting

### Problemas comunes

1. **Error de conexión a la base de datos**
   - Verifica que PostgreSQL esté ejecutándose
   - Revisa las credenciales en el archivo .env
   - Si usas Docker, asegúrate que el contenedor postgres esté activo

2. **Error "JWT malformed"**
   - El token JWT no tiene el formato correcto
   - Asegúrate de enviar el token con el prefijo "Bearer " en el header Authorization

3. **Error de validación de email o contraseña**
   - El email debe terminar en @unicauca.edu.co
   - La contraseña debe tener al menos 6 caracteres, una mayúscula, un número y un carácter especial

4. **Rate limit excedido**
   - Demasiadas solicitudes en poco tiempo, espera unos minutos antes de reintentar

## Contribuir

1. Haz fork del repositorio
2. Crea una rama para tu funcionalidad (`git checkout -b feature/amazing-feature`)
3. Haz commit de tus cambios (`git commit -m 'Add some amazing feature'`)
4. Push a la rama (`git push origin feature/amazing-feature`)
5. Abre un Pull Request
