# Despliegue en SeeNode

## Configuración para https://cloud.seenode.com

### Comandos de Despliegue

**Runtime**: Node 22

**Build Command**:
```bash
apt-get update && apt-get install -y default-jdk maven && mvn clean package -DskipTests
```

**Start Command**:
```bash
apt-get update && apt-get install -y default-jre && java -jar target/game-backend-1.0-SNAPSHOT.jar
```

### Variables de Entorno en SeeNode

Configura estas variables en el dashboard de SeeNode:

```
FRONTEND_URL=https://tu-app.vercel.app
DATABASE_URL=jdbc:mysql://up-de-fra1-mysql-1.db.run-on-seenode.com:11550/db_1pyy7warrpf2
DATABASE_USERNAME=db_1pyy7warrpf2
DATABASE_PASSWORD=W2K2v4xTxm5M1g2zomF0OhHF
PORT=80
```

**⚠️ IMPORTANTE**: Reemplaza `https://tu-app.vercel.app` con la URL real de tu frontend en Vercel.

### Configuración del Frontend en Vercel

En tu frontend (React/Vite), configura la variable de entorno:

```
VITE_API_URL=https://tu-backend.seenode.app
```

Usa esta variable en tu código:
```typescript
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:80';
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
