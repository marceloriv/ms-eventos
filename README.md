# 🎟️ ms-eventos

Microservicio de gestión de eventos para **Ticketti**. Expone una API REST para crear, publicar, buscar y administrar eventos culturales, **Circuit Breaker** para resiliencia, y mensajería asíncrona vía **RabbitMQ** para notificar cambios de stock al resto del ecosistema.

## Qué hace

- Permite a organizadores crear, editar y publicar eventos (concierto, festival cultural, cine móvil).
- Gestiona stock de entradas con validaciones de negocio (stock ≤ aforo, sin valores negativos).- Filtra y busca eventos por género, nombre y ubicación.
- Valida autenticación y roles mediante JWT, compatible con tokens emitidos por el BFF.
- Expone documentación interactiva con Swagger UI.
- Corre en local o con Docker Compose.

## Tecnologías

- Java 17
- Spring Boot 4.0.7
- Spring Web MVC
- Spring Data JPA
- Spring Security + JJWT (0.13.0)
- Spring Validation
- Spring Cloud Config Client
- Eureka Client
- Spring AMQP (RabbitMQ)
- Resilience4j Circuit Breaker
- Springdoc OpenAPI / Swagger
- MySQL (producción) / H2 (tests)
- Lombok
- JUnit 5 + Mockito + JaCoCo

## Estructura

La lógica principal vive en `src/main/java/com/ticketti/ms_eventos/` y se organiza en:

- `controller` — endpoints REST
- `service` — lógica de negocio, productor/consumidor de RabbitMQ
- `repository` — acceso a datos (Repository Pattern)
- `model` — entidades JPA (`Evento`, `Recinto`, `Genero`, `Estado`)
- `dto` — objetos de transferencia (`EventoDTO`, `RecintoDTO`, `GeneroDTO`)
- `config` — RabbitMQ, Swagger, Security
- `security` — `JwtService` y `JwtAuthenticationFilter`
- `exception` — manejo centralizado de errores (`GlobalExceptionHandler`)

## Patrones de diseño implementados

- **Repository Pattern** — desacopla el acceso a datos de la lógica de negocio.
- **Circuit Breaker** (Resilience4j) — protege la consulta de stock con fallback ante fallos, evitando caídas en cascada.

## Requisitos

- JDK 17
- Maven Wrapper o Maven instalado
- MySQL 8.4 si ejecutas la app fuera de Docker
- Docker y Docker Compose si quieres levantar todo en contenedores
- RabbitMQ corriendo (local o en contenedor) para la mensajería asíncrona

## Configuración local

La configuración se obtiene desde el **Config Server** (`spring.config.import`). Localmente, sin Config Server, la app usa los valores por defecto definidos en cada propiedad `${VARIABLE:default}`.

- Host BD: `localhost`
- Puerto BD: `3306`
- Base de datos: `eventos_db`
- Usuario: `root`
- Contraseña: vacía
- Puerto del servicio: `8083`

## Ejecutar en local

```powershell
.\mvnw.cmd spring-boot:run
```

La API queda disponible en:

- Swagger UI: http://localhost:8083/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8083/v3/api-docs

## Ejecutar con Docker Compose

```powershell
docker-compose up --build
```

## Autenticación y seguridad

El microservicio valida JWT mediante `JwtAuthenticationFilter` y `JwtService`, con el **mismo secreto compartido** que el BFF y el API Gateway (`jwt.secret`, vía variable de entorno `JWT_SECRET`).

Los roles esperados en el token (claim `rol`):

- `ORGANIZADOR` — crear, editar y publicar/cancelar sus propios eventos.
- `ADMINISTRADOR` — eliminar eventos.
- Rutas de lectura (`listarEventos`, `buscarEvento`, `buscar`) son públicas.

## Endpoints

Base URL: `/api/v0/Eventos`

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/crear` | Crea un nuevo evento |
| `GET` | `/listarEventos` | Lista todos los eventos |
| `GET` | `/mis` | Lista los eventos del organizador autenticado |
| `GET` | `/buscarEvento/{id}` | Busca un evento por id |
| `GET` | `/buscar` | Filtra por género, nombre y/o ubicación |
| `GET` | `/stock/{check}` | Verifica stock (protegido con Circuit Breaker) |
| `PUT` | `/{id}` | Actualiza un evento existente |
| `PUT` | `/{id}/estado` | Cambia el estado del evento (publicado/cancelado) |
| `PUT` | `/actualizarStock/{id}/{cantidad}` | Descuenta stock al confirmarse una compra |
| `PUT` | `/restaurarStock/{id}/{cantidad}` | Restaura stock al liberar una reserva |
| `DELETE` | `/eliminarEvento/{id}` | Elimina un evento |

## Reglas de negocio

- El stock no puede superar el aforo del evento.
- El stock no puede quedar negativo al confirmar una compra.
- Solo el `ORGANIZADOR` puede crear, editar y cambiar el estado de sus propios eventos.
- Solo el `ADMINISTRADOR` puede eliminar eventos.
- Cada cambio de stock publica un evento a RabbitMQ para su consumo asíncrono.

## Mensajería (RabbitMQ)

- **Productor** (`EntradaProducer`): publica el evento actualizado cuando se confirma una compra.
- **Consumidor** (`EntradaConsumer`): escucha la cola configurada para reaccionar a eventos relacionados.
- Configuración: `auto-startup: false` para no bloquear el arranque si RabbitMQ no está disponible.

## Pruebas

El proyecto cuenta con **35 tests** distribuidos en:

- `EventoServiceTest` — pruebas unitarias de la lógica de negocio (Mockito).
- `EventoControllerTest` — pruebas de la capa HTTP con `MockMvc` (standalone setup).
- `EventoIntegrationTest` — pruebas de integración end-to-end contra base de datos H2 real.
- `MsEventosApplicationTests` — carga de contexto de la aplicación.

Cobertura medida con JaCoCo.

```powershell
.\mvnw.cmd test
```

## Docker

El proyecto incluye un `Dockerfile` multi-stage (build con Maven, ejecución con JRE) y un `docker-compose.yml` para levantar el servicio junto con sus dependencias.

## Estado del proyecto

En desarrollo activo. Implementado el flujo completo de gestión de eventos, Circuit Breaker, mensajería con RabbitMQ, autenticación JWT y suite de pruebas unitarias/integración. Pendiente como deuda técnica: endpoint de reportes combinados de asistencia e ingresos (requiere coordinación con `ms-carrito`).

## Siguiente paso

Validar el contrato JWT con BFF y API Gateway en el entorno de release, y definir junto con `ms-carrito` el contrato de datos para el reporte de asistencia e ingresos del organizador.

## Autor

Meritxell Nicole Arroyo Aravena