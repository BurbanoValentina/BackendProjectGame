# Despliegue en SeeNode

## Configuración para https://cloud.seenode.com

⚠️ **IMPORTANTE**: SeeNode no tiene runtime de Java directo. Usa Docker.

### Opción 1: Despliegue con Docker (RECOMENDADO)

**Runtime**: Cualquier opción con Docker support o déjalo vacío

**Build Command**: (dejar vacío, Docker lo maneja)

**Start Command**: (dejar vacío, Docker lo maneja)

El `Dockerfile` ya está configurado y construirá y ejecutará automáticamente la aplicación en el puerto 80.

### Opción 2: Si SeeNode requiere comandos explícitos

**Build Command**:
```bash
docker build -t game-backend .
```

**Start Command**:
```bash
docker run -p 80:80 game-backend
```

### Notas Importantes

1. **Puerto configurado**: La aplicación está configurada para escuchar en `0.0.0.0:80`
2. **Base de datos**: Ya está configurada la conexión a MySQL de SeeNode
3. **Variables de entorno**: No se requieren variables adicionales, todo está en `application.properties`

### Verificación Local

Para probar localmente antes de desplegar:

```bash
# Build
mvnw.cmd clean package -DskipTests

# Run
java -jar target/game-backend-1.0-SNAPSHOT.jar
```

La aplicación estará disponible en `http://localhost:80`

### Endpoints Disponibles

- `POST /api/game/start` - Iniciar un nuevo juego
- `GET /api/game/{id}` - Obtener información de un juego
- `PUT /api/game/{id}` - Actualizar un juego
- `DELETE /api/game/{id}` - Eliminar un juego
