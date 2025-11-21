# Game Challenge Platform (Backend + Frontend)

Documentación completa de los dos proyectos que conviven en este repositorio: un backend Spring Boot que orquesta sesiones individuales y multijugador, y un frontend React + TypeScript que exhibe estructuras de datos personalizadas, chat asistido por IA y deep links.

## 🧭 Resumen rápido
- Arquitectura en capas: Controllers → Patterns (facade/factory/builder) → Services → Repositories → MongoDB.
- Gestión real de sesiones: tokens persistidos en `user_sessions`, limpieza automática cada 15 min.
- Modo multijugador coordinado por IA: una única sala activa, preguntas matemáticas generadas al vuelo y bots con builder específico.
- Frontend educativo: todas las pantallas usan estructuras de datos implementadas a mano (Queue, Stack, Linked Lists, Circular Doubly Linked List, Graph, Tree, Map managers, etc.).
- Dev experience unificada: Maven Wrapper + Dockerfile para backend; Vite + TypeScript para frontend.
- Migrador propio (`migration/SqliteToMongoMigrator`) para llevar históricos desde el viejo SQLite hacia MongoDB.

## 📂 Estructura del repositorio
```
GameProject/
├─ game_backend_project/
│  ├─ src/main/java/com/example/gamebackend/
│  │  ├─ controller/ (AuthController, GameController, MultiplayerController)
│  │  ├─ service/ (UserService, UserSessionService, GameService, MultiplayerRoomService)
│  │  ├─ patterns/ (GameFacade, AbstractGameFactory, GameBuilder, PrototypeGame, SingletonDatabaseConnection, UserBuilder)
│  │  ├─ model/ (Game, User, UserSession, MultiplayerRoom, MultiplayerPlayer, MultiplayerQuestion)
│  │  ├─ repository/ (MongoRepository interfaces)
│  │  ├─ config/ (AppProperties, MongoAuditingConfig, MongoConfig)
│  │  └─ migration/ (SqliteToMongoMigrator, SqliteToMongoMigrationApp)
│  ├─ src/main/resources/application.properties (prod / Atlas)
│  ├─ src/main/resources/application-dev.properties (perfil local)
│  ├─ Dockerfile, pom.xml, mvnw, mvnw.cmd
│  └─ target/ (artefactos generados)
└─ my-game-app-with-structures1/
   ├─ src/App.tsx, main.tsx
   ├─ src/components/ (WelcomeScreen, Login, Register, ForgotPassword, GameModeSelection,
   │  GameScreen, MultiplayerScreen, CountdownOverlay, GameSummaryCard, Timer, ChatbotBubble, etc.)
   ├─ src/services/ (AuthService, MultiplayerService, AITournamentService)
   ├─ src/lib/ (Queue, Stack, LinkedList, DoublyLinkedList, CircularDoublyLinkedList,
   │  LayoutManager, PanelStateManager, Graph, Tree, ArrayStructure, UserDataStructures…)
   ├─ src/styles/*.css
   └─ package.json, tsconfig*.json, vite.config.*
```

## 🛠️ Stack principal
| Capa | Tecnologías |
|------|-------------|
| Backend | Java 17, Spring Boot 3.5, Spring Data MongoDB, Spring Validation, Spring Scheduler, Maven Wrapper, Docker |
| Persistencia | MongoDB Atlas/local (auditoría `@CreatedDate`, índices `@Indexed`) |
| Frontend | React 18.3, TypeScript 5.4, Vite 5, Framer Motion 11, canvas-confetti, CSS modular |
| Herramientas | Git, npm, Docker (backend), VS Code, netlify/host estático opcional |

---

## 🧩 Backend · Spring Boot + MongoDB

### Arquitectura
- **Controllers** (`controller/`)
  - `AuthController` (`/api/auth`): register/login/logout, cambio de contraseña y actualización de high score.
  - `GameController` (`/api/games`): leaderboard, inicio de sesiones individuales y actualización de resultados a través de `GameFacade`.
  - `MultiplayerController` (`/api/multiplayer`): CRUD de salas, inicio de partida, envío de respuestas, ranking, leave y listado de salas.
- **Patrones** (`patterns/`)
  - `GameFacade` simplifica la interacción con `GameService`.
  - `AbstractGameFactory` decide la dificultad y `GameBuilder` garantiza objetos consistentes.
  - `PrototypeGame` clona partidas para pruebas; `SingletonDatabaseConnection` mantiene conexión SQLite legada.
  - `UserBuilder` valida longitud/regex antes de persistir credenciales.
- **Servicios** (`service/`)
  - `UserService` usa `UserBuilder` + `MD5Util`, verifica unicidad y convierte entidades a DTOs.
  - `UserSessionService` genera tokens UUID, fija expiración 24 h y ejecuta `@Scheduled(fixedDelayString = app.session.cleanup-interval-ms)` para cerrar sesiones vencidas.
  - `GameService` ordena partidas por `createdAt`, crea sesiones con `GameFactory` y actualiza métricas.
  - `MultiplayerRoomService` restringe a una sala activa, genera room codes, añade bots (`MultiplayerPlayer.Builder`), fabrica preguntas matemáticas y calcula ranking por score y tiempo promedio.
- **Persistencia** (`repository/`)
  - `UserRepository`, `GameRepository`, `UserSessionRepository`, `MultiplayerRoomRepository` heredan de `MongoRepository`.
  - Documentos anotados con `@Document` y campos `@Indexed` (username, nickname, sessionToken, expiresAt) para consultas eficientes.
- **Configuración** (`config/`)
  - `AppProperties` expone `app.frontend.url` (CORS) y `app.session.cleanup-interval-ms`.
  - `MongoAuditingConfig` habilita `@CreatedDate`/`@LastModifiedDate`.
  - `MongoConfig` listo para converters cuando se requieran.

### Modelos principales
- `Game`: nombre del jugador, dificultad, score, correctAnswers, totalQuestions, durationSeconds, `createdAt`.
- `User`: username + nickname únicos, password MD5 (legado) y `highScore` opcional.
- `UserSession`: `sessionToken`, `expiresAt`, flags `active`, `closedAt`, `closedReason`.
- `MultiplayerRoom`: `roomCode`, lista de `MultiplayerPlayer`, preguntas (`MultiplayerQuestion`), `RoomStatus`, `startedAt`/`finishedAt`, host y límites.
- `MultiplayerPlayer`: score, respuestas contestadas, tiempo promedio, flags `isBot`/`isReady`.
- `MultiplayerQuestion`: prompt, respuesta y timestamp (VO).

### API REST disponible
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/register` | Crea usuario, valida username/nickname y genera sesión persistida |
| POST | `/api/auth/login` | Verifica password MD5, limpia sesiones expiradas y crea token nuevo |
| POST | `/api/auth/logout?token=UUID` | Marca sesión como cerrada (motivo "Manual logout") |
| PUT | `/api/auth/user/{userId}/highscore?score=n` | Actualiza récord si el nuevo puntaje es mayor |
| PUT | `/api/auth/password/change` | Cambia contraseña buscando por username o nickname |
| GET | `/api/games` | Leaderboard completo ordenado por fecha |
| GET | `/api/games/{id}` | Recupera partida individual |
| POST | `/api/games/start` | Fabrica partida nueva vía `GameFacade` |
| PUT | `/api/games/{id}` | Actualiza score/correctAnswers/totalQuestions/duration |
| POST | `/api/games` | Inserta partida manual (útil para seeds/demos) |
| POST | `/api/multiplayer/rooms/create` | Crea sala (host + bot) y devuelve código |
| POST | `/api/multiplayer/rooms/join` | Une jugador humano (máx. 5 en backend) |
| POST | `/api/multiplayer/rooms/{roomCode}/start` | Solo host; genera 5 preguntas y marca estado PLAYING |
| POST | `/api/multiplayer/rooms/answer` | Procesa respuesta, tiempos y avanza ronda |
| GET | `/api/multiplayer/rooms/{roomCode}` | Snapshot completo de sala |
| GET | `/api/multiplayer/rooms/{roomCode}/ranking` | Ranking ordenado por score/avg time |
| POST | `/api/multiplayer/rooms/{roomCode}/leave/{playerId}` | Remueve jugador y elimina sala si no quedan humanos |
| GET | `/api/multiplayer/rooms` | Helper para listar todas las salas guardadas |

### Configuración y perfiles
- **`application.properties` (deploy):** apunta a MongoDB Atlas (`spring.data.mongodb.uri`), habilita override por `FRONTEND_URL`, `SESSION_CLEANUP_INTERVAL_MS`, `PORT`.
- **`application-dev.properties`:** usa `mongodb://localhost:27017`, DB `gameproject_dev`, CORS `http://localhost:5173`.
- **Variables clave**
  | Variable | Descripción | Default |
  |----------|-------------|---------|
  | `SPRING_PROFILES_ACTIVE` | Perfil (`dev` para local) | _(vacío)_ |
  | `SPRING_DATA_MONGODB_URI` | Cadena de conexión | `mongodb+srv://…` (prod) |
  | `SPRING_DATA_MONGODB_DATABASE` | Base de datos | `gamedb` / `gameproject_dev` |
  | `FRONTEND_URL` | Origen permitido CORS | `http://localhost:5173` |
  | `SESSION_CLEANUP_INTERVAL_MS` | Frecuencia del scheduler | `900000` (15 min) |
  | `PORT` | Puerto HTTP | `8080` |

### Migración SQLite → Mongo
- `SqliteToMongoMigrator` copia todas las tablas a colecciones homónimas en lotes de 1 000 documentos.
- `SqliteToMongoMigrationApp` lee system properties/env vars: `SQLITE_PATH`, `MONGO_URI`, `MONGO_DATABASE`, `MONGO_DROP_COLLECTIONS`.
- Permite portar el archivo `game.db` legado sin scripts externos.

### Cómo ejecutar el backend
```bash
cd game_backend_project

# Desarrollo (Windows/Mac/Linux)
./mvnw spring-boot:run --spring.profiles.active=dev

# Pruebas
./mvnw test

# Empaquetar JAR
./mvnw clean package -DskipTests

# Docker (image + run)
docker build -t game-backend .
docker run -p 8080:8080 -e FRONTEND_URL=http://localhost:5173 game-backend
```

---

## 🎮 Frontend · React + TypeScript + Vite

### Flujo de pantallas (App.tsx)
1. **WelcomeScreen:** CTA para iniciar sesión.
2. **Login/Register/ForgotPassword:** Formularios conectados a `/api/auth` vía `AuthService`.
3. **GameModeSelection:** Permite elegir "Chatbot" o "Multiplayer", mostrando el nickname actual.
4. **GameScreen:** Juego individual cronometrado con countdown, bot que provoca, paneles colapsables y confetti.
5. **MultiplayerScreen:** Lobby, auto-join por `?roomCode=XYZ`, chat asistido por IA (`AITournamentService`) y ranking en vivo.

### Componentes clave
- **UI reusable:** `Button`, `Card`, `Input`, `Icon`, `Timer`, `Background`, `CountdownOverlay`, `GameSummaryCard`, `ChatbotBubble`.
- **Animaciones:** `framer-motion` para transiciones y overlays; `canvas-confetti` celebra resultados altos.
- **Timers y audio:** `Timer` + `CountdownOverlay` sincronizan estado visual; `GameScreen` manipula `AudioContext` para tensión.

### Servicios (`src/services/`)
- `AuthService` (Singleton)
  - Cachea usuarios con `UserHashMap`, registra historial con `SessionQueue`, maneja navegación con `NavigationStack`.
  - Persiste sesión en `localStorage`, detecta expiración (`enforceSessionTtl`) y comunica logout al backend.
- `MultiplayerService` (Singleton)
  - Encapsula `fetch` para `/api/multiplayer`, maneja errores y colas de solicitudes (`Queue`).
- `AITournamentService`
  - Genera `AIRoomBlueprint` determinista por código, set de preguntas fáciles, links de invitación con `roomCode` y respuestas contextuales (`AIChatMessage`).

### Estructuras de datos personalizadas
| Estructura | Archivo | Uso |
|------------|--------|-----|
| `Queue<T>` | `lib/Queue.ts` | Buffer FIFO de preguntas en `GameScreen` y cola de requests en `MultiplayerService`. |
| `Stack<T>` | `lib/Stack.ts` | Historial reciente de intentos mostrado en panel "📜 Intentos (Stack)". |
| `LinkedList<T>` | `lib/LinkedList.ts` | Leaderboard/historial de partidas (convierte a array para UI). |
| `DoublyLinkedList<T>` | `lib/DoublyLinkedList.ts` | Base para demos y debug de navegación bidireccional. |
| `CircularDoublyLinkedList<T>` | `lib/CircularDoublyLinkedList.ts` | Rotación infinita de burlas del bot y mensajes motivacionales. |
| `ArrayStructure<T>` | `lib/Array.ts` | Ejemplo de lista dinámica para paneles configurables. |
| `LayoutManager` | `lib/LayoutManager.ts` | Controla orden, visibilidad y prioridad de paneles (game/stats/history/leaderboard). |
| `PanelStateManager` | `lib/PanelStateManager.ts` | HashMap de estados (expandido/colapsado) con helpers `toggle`, `expandAll`, `collapseAll`. |
| `Graph` | `lib/Graph.ts` | Representa relaciones entre pantallas, usado para depuración de flujos. |
| `Tree<T>` | `lib/Tree.ts` | Inserta puntajes y permite recorridos `inOrder` para analizar rangos. |
| `UserHashMap`, `SessionQueue`, `NavigationStack` | `lib/UserDataStructures.ts` | Cache y trazabilidad en `AuthService`. |

### GameScreen (componentes & lógica)
- Dificultades (`basic`, `advanced`, `expert`) controlan cronómetro (`DIFFICULTY_TIME`) y velocidad del bot.
- Pre-carga 8 preguntas con `Queue` para minimizar latencia.
- `historyStack` almacena intentos (LIFO) y se proyecta al panel horizontal.
- `tauntListRef` (Circular Doubly Linked List) rota frases del bot sin reiniciar la lista.
- `LayoutManager` + `PanelStateManager` permiten reordenar/ocultar paneles sin perder estado.
- Guarda resultados en backend (`/api/games`) y actualiza high score (`/api/auth/user/.../highscore`).

### MultiplayerScreen (componentes & lógica)
- Estados `menu`, `join`, `lobby`, `game`, `results`.
- Auto-join cuando la URL contiene `roomCode`; `App.tsx` detecta query params y deriva a la vista correspondiente.
- Chat asistente: `AITournamentService.getAssistantReply` responde sobre pistas, tiempo restante o comparte link directo.
- Timers independientes: `questionTimeLeft` y `roundTimeLeft`.
- Ranking ordenado por score y tiempo promedio, feedback instantáneo por respuesta y copia de link mediante `navigator.clipboard`.

### Variables y comandos
| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `VITE_API_URL` | Base URL del backend | `https://localhost:8080` (cámbialo a `http://localhost:8080` en local) |

```bash
cd my-game-app-with-structures1
npm install
npm run dev      # http://localhost:5173
npm run build    # Genera dist/
npm run preview  # Sirve dist/ localmente
```

---

## 🔗 Integración full-stack
- **CORS:** `app.frontend.url` y `VITE_API_URL` deben apuntar al mismo origen para evitar bloqueos de navegador.
- **Sesiones:** el backend devuelve `sessionToken` + `expiresAt`; el frontend los guarda y ejecuta `logout` si la TTL expira o el usuario cierra sesión manualmente.
- **Multiplayer:** el backend solo permite una sala activa (diseño intencional para torneos sincronizados). Ajusta `MultiplayerRoomService` si necesitas varias salas.
- **Deep links:** `AITournamentService.buildJoinUrl(roomCode)` genera URLs con `?roomCode=XXXX&autoJoin=true`; `App.tsx` consume ese parámetro y redirige directamente a `MultiplayerScreen`.
- **Actualización de récords:** `GameScreen` llama `AuthService.updateHighScore` cuando supera el score guardado.

---

## 🧪 Próximos pasos recomendados
1. **Tests backend:** no existe `src/test/java`; crear suites para `UserService`, `MultiplayerRoomService` y controllers.
2. **Seguridad:** migrar de MD5 a BCrypt/Spring Security si se requiere endurecer credenciales.
3. **Observabilidad:** integrar `spring-boot-starter-actuator` para health checks (`/actuator/health`).
4. **CI/CD:** los comandos `./mvnw` y `npm run build` están listos para pipelines de GitHub Actions o Azure Pipelines.

---

**Repositorio:** BackendProjectGame · **Branch:** `main` · **Autora:** Valentina Burbano · **Última actualización:** 21/11/2025

