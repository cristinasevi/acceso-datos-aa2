# Movies API

API REST para la gestión de películas, directores, actores, productoras, usuarios y reseñas. Proyecto desarrollado con Spring Boot para la asignatura de Acceso a Datos (AA2).

## Tabla de Contenidos

- [Descripción](#descripción)
- [Tecnologías](#tecnologías)
- [Requisitos Previos](#requisitos-previos)
- [Instalación y Configuración](#instalación-y-configuración)
- [Ejecución](#ejecución)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Endpoints de la API](#endpoints-de-la-api)
- [Versionado de la API](#versionado-de-la-api)
- [Tests de Integración con Newman](#tests-de-integración-con-newman)
- [GitHub Actions](#github-actions)
- [Apiman API Gateway](#apiman-api-gateway)
- [Base de Datos](#base-de-datos)

## Descripción

Sistema completo de gestión de películas que permite:
- Gestionar estudios cinematográficos
- Administrar información de directores y actores
- Catalogar películas con sus relaciones (v1 y v2)
- Gestionar usuarios del sistema
- Crear y consultar reseñas de películas
- Acceder a la API a través de un gateway Apiman con autenticación por API Key

El proyecto implementa una arquitectura en capas (Domain, Repository, Service, Controller) con DTOs para optimizar las respuestas de la API, versionado de endpoints (v1/v2), configuración externa con perfiles Spring, tests de integración con Newman y despliegue con Docker Compose.

## Tecnologías

- **Java 21**
- **Spring Boot 3.5.6**
- **MariaDB 11.3.2**
- **Lombok**
- **ModelMapper**
- **Docker** y **Docker Compose**
- **Maven**
- **Newman** (tests de integración)
- **GitHub Actions** (CI/CD)
- **Apiman 3.1.3** (API Gateway)
- **Keycloak 21.1.2** (autenticación)

## Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java JDK 21** o superior
    - Verifica con: `java -version`
- **Maven 3.9+**
    - Verifica con: `mvn -version`
- **Docker Desktop**
    - Descarga desde: https://www.docker.com/products/docker-desktop/
- **Git**
    - Verifica con: `git --version`
- **Postman**
    - Descarga desde: https://www.postman.com/downloads/
- **Newman** (para tests de integración)
    - Instala con: `npm install -g newman`

## Instalación y Configuración

### 1. Clonar el repositorio
```bash
git clone https://github.com/cristinasevi/acceso-datos-aa2.git
cd acceso-datos-aa2/movies
```

### 2. Configurar variables de entorno

Crea un archivo `.env` en la carpeta `movies/`:
```env
MARIADB_USER=cristina
MARIADB_PASSWORD=password
MARIADB_DATABASE=movies_aa2
MARIADB_ROOT_PASSWORD=rootpassword
```

## Ejecución

### Opción A: Docker Compose (recomendado)

Levanta la API completa con base de datos en un solo comando:

```bash
cd movies
docker build -t movies-api .
docker compose up -d
```

La aplicación estará disponible en: **http://localhost:8080**

Verifica que los contenedores están corriendo:
```bash
docker ps
```
Deberías ver `movies-db` (MariaDB en puerto 3308) y `movies-api` (Spring Boot en puerto 8080).

### Opción B: Desarrollo local (solo base de datos en Docker)

```bash
# Levantar solo la base de datos
docker compose -f docker-compose.dev.yaml up -d

# Arrancar la API con perfil dev
mvn spring-boot:run
```

### Compilar el proyecto

```bash
mvn clean package -DskipTests
```

### Detener los contenedores

```bash
docker compose down
```

## Estructura del Proyecto

```
acceso-datos-aa2/
├── .github/
│   └── workflows/
│       └── newman.yaml          # Pipeline CI/CD con GitHub Actions
└── movies/
    ├── src/
    │   ├── main/
    │   │   ├── java/acceso/datos/aa2/movies/
    │   │   │   ├── config/          # Configuración (ModelMapper)
    │   │   │   ├── controller/      # Controladores REST (v1 y v2)
    │   │   │   ├── domain/          # Entidades JPA
    │   │   │   ├── dto/             # Data Transfer Objects
    │   │   │   ├── exception/       # Excepciones personalizadas
    │   │   │   ├── repository/      # Repositorios JPA
    │   │   │   ├── service/         # Lógica de negocio
    │   │   │   └── util/            # Utilidades (DateUtil)
    │   │   └── resources/
    │   │       ├── application.properties          # Config principal
    │   │       ├── application-prod.properties     # Config producción (MariaDB)
    │   │       ├── logback-spring.xml              # Configuración de logs
    │   │       └── wiremock/mappings/              # Mocks para tests
    │   └── test/                    # Tests unitarios (JUnit + Mockito)
    ├── postman/                     # Colección Postman parametrizada
    ├── Movies_Movies.postman_collection.json  # Tests Newman
    ├── local.postman_environment.json         # Entorno local Newman
    ├── MoviesAPI_Apiman.postman_collection.json    # Postman con APIMan
    ├── docker-compose.yaml          # Producción (API + BD)
    ├── docker-compose.dev.yaml      # Desarrollo (solo BD)
    ├── Dockerfile                   # Imagen Docker de la API
    ├── movies.yaml                  # OpenAPI 3.0
    ├── pom.xml                      # Dependencias Maven
    └── README.md
```

## Endpoints de la API

### Studios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/studios` | Obtener todas las productoras |
| GET | `/studios?country={country}` | Filtrar por país |
| GET | `/studios?foundationYear={year}` | Filtrar por año de fundación |
| GET | `/studios?active={active}` | Filtrar por estado activo |
| GET | `/studios/{id}` | Obtener productora por ID |
| POST | `/studios` | Crear nueva productora |
| PUT | `/studios/{id}` | Actualizar productora |
| DELETE | `/studios/{id}` | Eliminar productora |

### Directors

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/directors` | Obtener todos los directores |
| GET | `/directors?nationality={nationality}` | Filtrar por nacionalidad |
| GET | `/directors?active={active}` | Filtrar por estado activo |
| GET | `/directors?minAwards={minAwards}` | Filtrar por premios mínimos |
| GET | `/directors/{id}` | Obtener director por ID |
| POST | `/directors` | Crear nuevo director |
| PUT | `/directors/{id}` | Actualizar director |
| DELETE | `/directors/{id}` | Eliminar director |

### Actors

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/actors` | Obtener todos los actores |
| GET | `/actors?nationality={nationality}` | Filtrar por nacionalidad |
| GET | `/actors?active={active}` | Filtrar por estado activo |
| GET | `/actors?actorType={actorType}` | Filtrar por tipo de actor |
| GET | `/actors/{id}` | Obtener actor por ID |
| POST | `/actors` | Crear nuevo actor |
| PUT | `/actors/{id}` | Actualizar actor |
| DELETE | `/actors/{id}` | Eliminar actor |

### Movies (v1 y v2)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/movies` | Obtener todas las películas |
| GET | `/api/v1/movies?genre={genre}` | Filtrar por género |
| GET | `/api/v1/movies?releaseDateFrom={date}` | Filtrar por fecha desde |
| GET | `/api/v1/movies?releaseDateTo={date}` | Filtrar por fecha hasta |
| GET | `/api/v1/movies?minRating={rating}` | Filtrar por valoración mínima |
| GET | `/api/v1/movies/{id}` | Obtener película por ID |
| GET | `/api/v2/movies/{id}` | Obtener película con campos calculados (daysUntilRelease, directorName) |
| POST | `/api/v1/movies` | Crear película (deprecated, usa directorId/studioId) |
| POST | `/api/v2/movies` | Crear película (usa objetos anidados + campo rate obligatorio) |
| PUT | `/api/v1/movies/{id}` | Actualizar película completa |
| PUT | `/api/v2/movies/{id}` | Actualizar película parcialmente (solo campos enviados) |
| DELETE | `/api/v1/movies/{id}` | Eliminar película (hard delete) |
| DELETE | `/api/v2/movies/{id}` | Eliminar película (soft delete, marca active=false) |

**Géneros válidos:** `ACTION`, `DRAMA`, `COMEDY`, `HORROR`, `SCIENCE_FICTION`, `ROMANCE`, `THRILLER`, `ANIMATION`, `DOCUMENTARY`, `ADVENTURE`

### Users

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/users` | Obtener todos los usuarios |
| GET | `/users?premium={premium}` | Filtrar por premium |
| GET | `/users?active={active}` | Filtrar por estado activo |
| GET | `/users?registrationDateFrom={date}` | Filtrar por fecha de registro |
| GET | `/users/{id}` | Obtener usuario por ID |
| POST | `/users` | Crear nuevo usuario |
| PUT | `/users/{id}` | Actualizar usuario |
| DELETE | `/users/{id}` | Eliminar usuario |

### Reviews

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/reviews` | Obtener todas las reseñas |
| GET | `/reviews?minRating={rating}` | Filtrar por valoración mínima |
| GET | `/reviews?recommended={recommended}` | Filtrar por recomendación |
| GET | `/reviews?spoiler={spoiler}` | Filtrar por spoiler |
| GET | `/movies/{movieId}/reviews` | Obtener reseñas de una película |
| GET | `/users/{userId}/reviews` | Obtener reseñas de un usuario |
| GET | `/reviews/{id}` | Obtener reseña por ID |
| POST | `/movies/{movieId}/reviews` | Crear reseña para una película |
| PUT | `/reviews/{id}` | Actualizar reseña |
| DELETE | `/reviews/{id}` | Eliminar reseña |

## Versionado de la API

La API implementa versionado en los endpoints de películas:

- **v1**: Endpoints originales. El POST usa `directorId` y `studioId` como campos planos. Marcado como `@Deprecated`.
- **v2**: Versión mejorada. El POST usa objetos anidados `director` y `studio`, e incluye el campo obligatorio `rate` (1-10). El GET incluye campos calculados `daysUntilRelease` y `directorName`. El DELETE es un soft delete (marca `active=false`).

## Tests de Integración con Newman

Los tests de integración cubren el endpoint `/api/v1/movies` con 12 requests y 43 assertions.

### Ejecutar tests localmente

```bash
cd movies
newman run Movies_Movies.postman_collection.json -e local.postman_environment.json
```

### Variables de entorno

El fichero `local.postman_environment.json` define:
- `HOST`: `http://localhost:8080/api/v1`
- `HOST_V2`: `http://localhost:8080/api/v2`

### Casos de prueba incluidos

| Request | Assertions |
|---------|-----------|
| POST /movies - 201 Created | Status 201, id presente, título y género correctos |
| POST /movies - 400 Bad Request | Status 400, code 400, title bad-request |
| GET /movies - 200 OK | Status 200, array, al menos 1 item |
| GET /movies?genre=ACTION | Status 200, todos ACTION |
| GET /movies?genre=NOGENRE | Status 200, array vacío |
| GET /movies/{id} - 200 OK | Status 200, id coincide |
| GET /movies/{id} - 404 Not Found | Status 404, code 404 |
| GET /v2/movies/{id} - 200 OK | Status 200, daysUntilRelease, directorName |
| PUT /movies/{id} - 200 OK | Status 200, título actualizado |
| PUT /movies/{id} - 404 Not Found | Status 404, not-found |
| DELETE /movies/{id} - 204 No Content | Status 204 |
| DELETE /movies/{id} - 404 Not Found | Status 404, not-found |

## GitHub Actions

El pipeline CI/CD se ejecuta automáticamente en **pull requests** hacia `main` o `develop`.

### Pasos del pipeline

1. Checkout del código
2. Setup Java 21 (Temurin)
3. Build del proyecto con Maven
4. Build de la imagen Docker
5. Arranque de los contenedores con Docker Compose
6. Espera a que la API esté lista (hasta 30 intentos de 5 segundos)
7. Instalación de Newman
8. Ejecución de los tests de integración

## Apiman API Gateway

La API está publicada en un gateway Apiman con autenticación por API Key.

### Arrancar Apiman

```bash
cd apiman-docker-compose-3.1.3.Final
docker compose up -d
```

Espera ~60 segundos y accede a:
- **Apiman UI:** http://apiman.local.gd:8090/apimanui (admin / admin123!)
- **Gateway:** http://gateway.local.gd:8090/movies/MoviesAPI/2.0

### Usar la API a través del gateway

Añade el parámetro `apikey` a todas las requests:

```
GET http://gateway.local.gd:8090/movies/MoviesAPI/2.0/api/v1/movies?apikey=<tu-api-key>
```

### Políticas configuradas

- **Rate Limiting**: 100 requests por minuto por API
- **Caching Resources**: Caché de 60 segundos para GET con status 200

### Colección Postman para Apiman

Importa `MoviesAPI_Apiman_final.postman_collection.json`. Incluye todas las entidades con el parámetro `apikey` preconfigurado.

## Base de Datos

### Modelo de datos

El sistema utiliza 6 entidades principales con las siguientes relaciones:

- **Studio** ← (1:N) → **Movie**
- **Director** ← (1:N) → **Movie**
- **Actor** ← (N:M) → **Movie**
- **Movie** ← (1:N) → **Review**
- **User** ← (1:N) → **Review**

### Detener la aplicación

```bash
# Detener contenedores
docker compose down

# Detener y eliminar datos
docker compose down -v
```