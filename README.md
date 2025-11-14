# 📚 Estructuras de Datos Utilizadas en Game Backend

Este documento describe todas las estructuras de datos y patrones de diseño implementados en el backend del juego.

---

## 🎯 Estructuras de Datos Implementadas

### 1. ArrayList (Lista Dinámica) 📋

**Ubicación:** Repositorios JPA y gestión de entidades

**Uso:** Almacenamiento y gestión de colecciones de entidades

**Principio:** Lista dinámica con acceso indexado
- **Ubicación en código:** 
  - `List<User>` en `UserRepository`
  - `List<GameSession>` en `GameSessionRepository`
  - `List<GameAttempt>` en `GameAttemptRepository`

**Propósito:** Almacenar y manipular colecciones de entidades de forma eficiente

**Operaciones principales:**
- `add()` - Agregar nueva entidad
- `get(index)` - Acceso por índice O(1)
- `findAll()` - Obtener todas las entidades
- `stream()` - Operaciones funcionales sobre la colección

**Ejemplo de uso:**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findAll(); // Retorna ArrayList
}
```

---

### 2. HashMap (Mapa de Hash) 🗺️

**Ubicación:** `src/lib/HashMap.java` (implementación personalizada)

**Uso:** Caché de sesiones activas y datos temporales

**Principio:** Estructura clave-valor con acceso O(1)
- **Ubicación en código:** Sistema de caché interno
- **Propósito:** Almacenar sesiones activas y tokens JWT para validación rápida

**Operaciones principales:**
- `put(key, value)` - Almacenar par clave-valor O(1)
- `get(key)` - Recuperar valor O(1)
- `containsKey(key)` - Verificar existencia O(1)
- `remove(key)` - Eliminar entrada O(1)

**Ventajas:**
- ✅ Acceso ultra-rápido a sesiones activas
- ✅ Validación eficiente de tokens
- ✅ Reducción de consultas a base de datos
- ✅ Gestión de estado en memoria

---

### 3. LinkedList (Lista Enlazada) 🔗

**Ubicación:** `src/lib/LinkedList.java` (implementación personalizada)

**Uso:** Historial de intentos de juego en orden cronológico

**Principio:** Lista enlazada doble
- **Ubicación en código:** `GameAttemptService`
- **Propósito:** Mantener historial ordenado de intentos con inserción eficiente

**Operaciones principales:**
- `addFirst()` - Agregar al inicio O(1)
- `addLast()` - Agregar al final O(1)
- `remove()` - Eliminar elemento O(n)
- `iterator()` - Recorrer elementos

**Uso en el sistema:**
```java
// Historial de intentos de un jugador
LinkedList<GameAttempt> playerAttempts = new LinkedList<>();
playerAttempts.addFirst(newAttempt); // Más reciente primero
```

---

### 4. Stack (Pila - LIFO) 📚

**Ubicación:** `src/lib/Stack.java` (implementación personalizada)

**Uso:** Gestión de operaciones de rollback y undo

**Principio:** Last In, First Out (LIFO)
- **Ubicación en código:** Sistema de transacciones
- **Propósito:** Mantener historial de operaciones para rollback

**Operaciones principales:**
- `push(element)` - Agregar elemento O(1)
- `pop()` - Remover y retornar tope O(1)
- `peek()` - Ver tope sin remover O(1)
- `isEmpty()` - Verificar si está vacía O(1)

**Aplicaciones:**
- 🔄 Rollback de transacciones fallidas
- ⏮️ Undo de operaciones
- 📝 Historial de estados

---

### 5. Queue (Cola - FIFO) 📥

**Ubicación:** `src/lib/Queue.java` (implementación personalizada)

**Uso:** Procesamiento de solicitudes y generación de preguntas

**Principio:** First In, First Out (FIFO)
- **Ubicación en código:** `QuestionGeneratorService`
- **Propósito:** Buffer de preguntas generadas y cola de solicitudes

**Operaciones principales:**
- `enqueue(element)` - Agregar al final O(1)
- `dequeue()` - Remover del frente O(1)
- `peek()` - Ver el siguiente sin remover O(1)
- `size()` - Tamaño de la cola O(1)

**Uso en el sistema:**
```java
// Buffer de preguntas pregeneradas
Queue<Question> questionBuffer = new Queue<>();
questionBuffer.enqueue(generatedQuestion);

// Procesamiento FIFO
Question nextQuestion = questionBuffer.dequeue();
```

---

### 6. Tree (Árbol Binario de Búsqueda) 🌳

**Ubicación:** `src/lib/Tree.java` (implementación personalizada)

**Uso:** Clasificación de jugadores por puntaje

**Principio:** Árbol binario ordenado
- **Ubicación en código:** `LeaderboardService`
- **Propósito:** Mantener ranking de jugadores ordenado por puntaje

**Operaciones principales:**
- `insert(player)` - Insertar jugador O(log n) promedio
- `search(score)` - Buscar por puntaje O(log n)
- `inOrderTraversal()` - Obtener ranking ordenado O(n)
- `getTopN(n)` - Obtener top N jugadores O(n)

**Ventajas:**
- ✅ Inserción eficiente manteniendo orden
- ✅ Búsqueda rápida de posiciones
- ✅ Obtención ordenada del leaderboard
- ✅ Balanceo automático (AVL opcional)

---

### 7. Graph (Grafo) 🕸️

**Ubicación:** `src/lib/Graph.java` (implementación personalizada)

**Uso:** Sistema de dependencias entre preguntas y progresión

**Principio:** Grafo dirigido con nodos y aristas
- **Ubicación en código:** `ProgressionService`
- **Propósito:** Modelar relaciones entre temas y dependencias de preguntas

**Operaciones principales:**
- `addVertex(topic)` - Agregar tema O(1)
- `addEdge(from, to)` - Crear dependencia O(1)
- `getAdjacent(topic)` - Obtener temas relacionados O(1)
- `topologicalSort()` - Orden de progresión O(V+E)
- `shortestPath(start, end)` - Camino más corto (BFS) O(V+E)

**Aplicaciones:**
- 📊 Sistema de progresión de dificultad
- 🔗 Dependencias entre temas
- 🎯 Recomendación de siguiente pregunta
- 📈 Análisis de caminos de aprendizaje

---

## 🏗️ Patrones de Diseño Implementados

### 1. Singleton Pattern 🔐

**Archivo:** `src/patterns/Singleton.java`

**Uso:** Instancia única de configuración y conexiones

**Componentes que lo usan:**
- `DatabaseConnection` - Conexión única a la base de datos
- `ConfigurationManager` - Gestión centralizada de configuración
- `CacheManager` - Instancia única de caché

**Implementación:**
```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    private DatabaseConnection() {}
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
```

**Ventajas:**
- ✅ Control de instancia única
- ✅ Acceso global controlado
- ✅ Inicialización lazy (bajo demanda)
- ✅ Thread-safe con synchronized

---

### 2. Factory Pattern 🏭

**Archivo:** `src/patterns/Factory.java`

**Uso:** Creación de diferentes tipos de preguntas

**Componentes:**
- `QuestionFactory` - Fábrica de preguntas
- `DifficultyFactory` - Configuración según dificultad
- `ResponseFactory` - Generación de respuestas estandarizadas

**Implementación:**
```java
public class QuestionFactory {
    public static Question createQuestion(String type, String difficulty) {
        switch (type) {
            case "MULTIPLE_CHOICE":
                return new MultipleChoiceQuestion(difficulty);
            case "TRUE_FALSE":
                return new TrueFalseQuestion(difficulty);
            case "OPEN_ENDED":
                return new OpenEndedQuestion(difficulty);
            default:
                throw new IllegalArgumentException("Unknown question type");
        }
    }
}
```

**Ventajas:**
- ✅ Encapsulación de creación de objetos
- ✅ Fácil extensión con nuevos tipos
- ✅ Código limpio y mantenible
- ✅ Principio Open/Closed

---

### 3. Observer Pattern 👀

**Archivo:** `src/patterns/Observer.java`

**Uso:** Notificaciones de eventos del juego

**Componentes:**
- `GameEventPublisher` - Publica eventos
- `ScoreUpdateListener` - Observa cambios de puntaje
- `AchievementListener` - Detecta logros desbloqueados
- `LeaderboardListener` - Actualiza ranking

**Implementación:**
```java
public interface GameEventListener {
    void onEvent(GameEvent event);
}

public class GameEventPublisher {
    private List<GameEventListener> listeners = new ArrayList<>();
    
    public void subscribe(GameEventListener listener) {
        listeners.add(listener);
    }
    
    public void notifyListeners(GameEvent event) {
        listeners.forEach(l -> l.onEvent(event));
    }
}
```

**Eventos soportados:**
- 📊 Actualización de puntaje
- 🏆 Logro desbloqueado
- ⏱️ Tiempo agotado
- ✅ Respuesta correcta/incorrecta
- 🎮 Fin de partida

---

### 4. Strategy Pattern 🎯

**Archivo:** `src/patterns/Strategy.java`

**Uso:** Algoritmos de cálculo de puntaje según dificultad

**Componentes:**
- `ScoreStrategy` (interfaz) - Estrategia de puntuación
- `EasyScoreStrategy` - Puntuación para nivel fácil
- `MediumScoreStrategy` - Puntuación para nivel medio
- `HardScoreStrategy` - Puntuación para nivel difícil

**Implementación:**
```java
public interface ScoreStrategy {
    int calculateScore(boolean correct, long timeSpent);
}

public class HardScoreStrategy implements ScoreStrategy {
    @Override
    public int calculateScore(boolean correct, long timeSpent) {
        if (!correct) return 0;
        int baseScore = 100;
        int timeBonus = Math.max(0, 50 - (int)(timeSpent / 1000));
        return baseScore + timeBonus;
    }
}
```

**Ventajas:**
- ✅ Fácil cambio de algoritmo en runtime
- ✅ Código desacoplado y testeable
- ✅ Cumple principio Open/Closed
- ✅ Extensible para nuevas dificultades

---

### 5. Builder Pattern 🔨

**Archivo:** `src/patterns/Builder.java`

**Uso:** Construcción de objetos complejos paso a paso

**Componentes:**
- `GameSessionBuilder` - Constructor de sesiones
- `QuestionBuilder` - Constructor de preguntas
- `UserBuilder` - Constructor de usuarios

**Implementación:**
```java
public class GameSessionBuilder {
    private String username;
    private String difficulty;
    private int totalQuestions;
    private LocalDateTime startTime;
    
    public GameSessionBuilder username(String username) {
        this.username = username;
        return this;
    }
    
    public GameSessionBuilder difficulty(String difficulty) {
        this.difficulty = difficulty;
        return this;
    }
    
    public GameSession build() {
        return new GameSession(username, difficulty, totalQuestions, startTime);
    }
}

// Uso
GameSession session = new GameSessionBuilder()
    .username("player1")
    .difficulty("HARD")
    .totalQuestions(10)
    .build();
```

**Ventajas:**
- ✅ Construcción fluida y legible
- ✅ Validación paso a paso
- ✅ Inmutabilidad de objetos
- ✅ Parámetros opcionales claros

---

## 🔄 Flujo de Datos en el Backend

```
┌─────────────────────────┐
│   Cliente (Frontend)    │
└───────────┬─────────────┘
            │ HTTP Request
            ▼
┌─────────────────────────┐
│   AuthController /      │
│   GameController        │  ← REST Controllers
└───────────┬─────────────┘
            │
            ▼
┌─────────────────────────┐
│   Service Layer         │  ← Lógica de negocio
│  - AuthService          │
│  - GameService          │
│  - LeaderboardService   │
└───────────┬─────────────┘
            │
            ├─────────────┬──────────────┬─────────────┐
            ▼             ▼              ▼             ▼
      ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐
      │  Queue  │   │  Stack  │   │  Tree   │   │  Graph  │
      │(Pregun.)│   │(Undo)   │   │(Ranking)│   │(Progr.) │
      └─────────┘   └─────────┘   └─────────┘   └─────────┘
            │             │              │             │
            └─────────────┴──────────────┴─────────────┘
                          │
                          ▼
            ┌─────────────────────────┐
            │   Repository Layer      │  ← JPA Repositories
            │  - UserRepository       │     (ArrayList)
            │  - GameSessionRepo      │
            │  - GameAttemptRepo      │
            └───────────┬─────────────┘
                        │
                        ▼
            ┌─────────────────────────┐
            │   PostgreSQL Database   │  ← Persistencia
            └─────────────────────────┘
```

---

## 📊 Complejidad de Operaciones

| Estructura | Inserción | Búsqueda | Eliminación | Ordenar | Espacio |
|------------|-----------|----------|-------------|---------|---------|
| **ArrayList** | O(1)* | O(n) | O(n) | O(n log n) | O(n) |
| **HashMap** | O(1) | O(1) | O(1) | N/A | O(n) |
| **LinkedList** | O(1) | O(n) | O(1)** | N/A | O(n) |
| **Stack** | O(1) | O(n) | O(1) | N/A | O(n) |
| **Queue** | O(1) | O(n) | O(1) | N/A | O(n) |
| **Tree (BST)** | O(log n) | O(log n) | O(log n) | O(n) | O(n) |
| **Graph** | O(1) | O(V+E) | O(V+E) | O(V log V) | O(V+E) |

*Amortizado al final del array  
**Si se tiene referencia directa al nodo

---

## 🗄️ Modelo de Base de Datos

### Entidades Principales

#### User (Usuario)
```java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue
    private Long id;
    
    private String username;
    private String password; // BCrypt hash
    private String email;
    
    @OneToMany(mappedBy = "user")
    private List<GameSession> sessions; // ArrayList
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

#### GameSession (Sesión de Juego)
```java
@Entity
@Table(name = "game_sessions")
public class GameSession {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne
    private User user;
    
    private String difficulty;
    private int totalQuestions;
    private int correctAnswers;
    private int score;
    private long durationSeconds;
    
    @OneToMany(mappedBy = "session")
    private List<GameAttempt> attempts; // ArrayList
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
```

#### GameAttempt (Intento Individual)
```java
@Entity
@Table(name = "game_attempts")
public class GameAttempt {
    @Id @GeneratedValue
    private Long id;
    
    @ManyToOne
    private GameSession session;
    
    private String question;
    private String userAnswer;
    private String correctAnswer;
    private boolean correct;
    private long timeSpent;
    
    @CreationTimestamp
    private LocalDateTime timestamp;
}
```

---

## 🛡️ Seguridad y Autenticación

### JWT Token Management (HashMap)

```java
// Caché en memoria de tokens activos
private Map<String, TokenData> activeTokens = new HashMap<>();

public boolean validateToken(String token) {
    if (activeTokens.containsKey(token)) {
        TokenData data = activeTokens.get(token);
        return !data.isExpired();
    }
    return false;
}
```

### Estrategia de Seguridad
- 🔐 **BCrypt** para hash de contraseñas
- 🎫 **JWT** para autenticación stateless
- ⏰ **Token expiration** (24 horas)
- 🛡️ **Spring Security** para protección de endpoints
- 🚫 **CORS** configurado para frontend específico

---

## 🚀 Endpoints REST API

### Autenticación
```
POST   /api/auth/register     - Registrar nuevo usuario
POST   /api/auth/login        - Iniciar sesión (retorna JWT)
POST   /api/auth/logout       - Cerrar sesión
GET    /api/auth/validate     - Validar token JWT
```

### Juego
```
POST   /api/game/start        - Iniciar nueva partida
POST   /api/game/answer       - Enviar respuesta
GET    /api/game/question     - Obtener siguiente pregunta
POST   /api/game/finish       - Finalizar partida
GET    /api/game/session/:id  - Obtener detalles de sesión
```

### Leaderboard
```
GET    /api/leaderboard/top/:n      - Top N jugadores (Tree)
GET    /api/leaderboard/user/:id    - Posición de usuario
GET    /api/leaderboard/history/:id - Historial de partidas (LinkedList)
```

### Estadísticas
```
GET    /api/stats/user/:id    - Estadísticas del jugador
GET    /api/stats/global      - Estadísticas globales
```

---

## 📦 Tecnologías y Dependencias

### Core Framework
- ☕ **Java 21** (LTS)
- 🍃 **Spring Boot 3.2** 
- 🗄️ **Spring Data JPA**
- 🔒 **Spring Security 6**
- 🌐 **Spring Web (REST)**

### Base de Datos
- 🐘 **PostgreSQL 15+**
- 💾 **HikariCP** (Connection pooling)
- 🔄 **Flyway** (Migraciones)

### Seguridad
- 🎫 **JWT (jjwt 0.12.3)**
- 🔐 **BCrypt** (Spring Security)

### Herramientas
- 🔨 **Maven** (Gestión de dependencias)
- 🐋 **Docker** (Containerización)
- 📝 **Lombok** (Reducción de boilerplate)
- ✅ **JUnit 5** (Testing)

---

## 🧪 Testing

### Cobertura de Tests
```
src/test/java/
├── controller/
│   ├── AuthControllerTest.java
│   └── GameControllerTest.java
├── service/
│   ├── GameServiceTest.java
│   └── LeaderboardServiceTest.java
├── patterns/
│   ├── FactoryPatternTest.java
│   └── StrategyPatternTest.java
└── lib/
    ├── QueueTest.java
    ├── StackTest.java
    ├── TreeTest.java
    └── GraphTest.java
```

### Comandos de Testing
```bash
# Ejecutar todos los tests
mvn test

# Tests con cobertura
mvn test jacoco:report

# Tests de integración
mvn verify
```

---

## 🔧 Configuración y Despliegue

### Variables de Entorno
```properties
# Base de datos
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/gamedb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password

# JWT
JWT_SECRET=your_secret_key_here
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
```

### Docker Deployment
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/game-backend-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Comandos de Ejecución
```bash
# Desarrollo
mvn spring-boot:run

# Producción
mvn clean package
java -jar target/game-backend-1.0-SNAPSHOT.jar

# Docker
docker build -t game-backend .
docker run -p 8080:8080 game-backend
```

---

## 📈 Métricas y Monitoreo

### Actuator Endpoints
```
GET /actuator/health      - Estado de salud
GET /actuator/metrics     - Métricas de aplicación
GET /actuator/info        - Información del build
```

### Logs
```java
// Configuración de logging (logback-spring.xml)
- INFO: Operaciones normales
- WARN: Situaciones inusuales
- ERROR: Errores y excepciones
- DEBUG: Debugging detallado (solo desarrollo)
```

---

## 🚀 Extensiones Futuras

### Estructuras Adicionales Planificadas
- 🔄 **Circular Queue** - Rotación de preguntas
- 📊 **Priority Queue** - Priorización de solicitudes
- 🎲 **Bloom Filter** - Detección rápida de usuarios duplicados
- 🌐 **Trie** - Autocompletado de búsquedas
- 🔗 **Disjoint Set (Union-Find)** - Agrupación de jugadores

### Funcionalidades Futuras
- 🏆 Sistema de logros y badges
- 👥 Modo multijugador en tiempo real
- 📊 Dashboard de analytics avanzado
- 🤖 IA para generación dinámica de preguntas
- 🌍 Soporte multiidioma
- 📱 API GraphQL alternativa

---

## 📝 Notas de Implementación

✅ **Buenas Prácticas:**
- Clean Architecture (Capas bien definidas)
- SOLID Principles
- RESTful API design
- Exception handling centralizado
- Logging estructurado
- Validación de entrada robusta
- Documentación con Javadoc

🔒 **Seguridad:**
- Nunca loguear información sensible
- Validación de entrada en todos los endpoints
- Rate limiting implementado
- HTTPS requerido en producción
- SQL Injection prevention (JPA)
- XSS protection (Spring Security)

🚀 **Performance:**
- Connection pooling configurado
- Índices en columnas frecuentemente consultadas
- Caché de consultas comunes (HashMap)
- Lazy loading de relaciones
- Paginación en listados grandes

---

## 👥 Información del Proyecto

**Repositorio:** BackendProjectGame  
**Propietario:** BurbanoValentina  
**Branch Actual:** main

**Stack Tecnológico:**
- Java 21 LTS
- Spring Boot 3.2+
- PostgreSQL 15+
- Maven 3.9+
- Docker

**API Base URL:** `http://localhost:8080/api`

---

## 📚 Referencias y Documentación

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Reference](https://spring.io/projects/spring-security)
- [JWT Introduction](https://jwt.io/introduction)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Design Patterns (Gang of Four)](https://refactoring.guru/design-patterns)

---

**Autor:** Valentina Burbano (Valen Team)  
**Última actualización:** 14 de noviembre de 2025  
**Versión:** 1.0.0

