# Proyecto Demostración — Javalin 7

[![Gitpod Ready-to-Code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/vacax/javalin-demo)

Proyecto de demostración de conceptos de desarrollo web con **Javalin 7.2.2** y **Java 25**.
Orientado a estudiantes que están comenzando a desarrollar aplicaciones web,
aprendiendo el protocolo HTTP, HTML, CSS y JavaScript.

---

## Requisitos

| Herramienta | Versión |
|-------------|---------|
| Java        | 25      |
| Gradle      | 9.5.1 (usa el wrapper `./gradlew`) |
| Javalin     | 7.2.2   |

---

## Cómo ejecutar

```bash
# Compilar y ejecutar en modo desarrollo (puerto 7000)
./gradlew run

# Generar el fat-jar (para despliegue)
./gradlew shadowJar
java -jar build/libs/app.jar

# Con Docker
./gradlew shadowJar
docker build -t javalin-demo .
docker run -p 7000:7000 javalin-demo
```

El servidor queda disponible en **http://localhost:7000**

Para ver todas las rutas registradas visita: **http://localhost:7000/routes**

---

## Conceptos demostrados

### 1. Protocolo HTTP básico — `/isc415`

Demuestra los **verbos HTTP**, los manejadores `before`/`after` y cómo leer y escribir cabeceras.

| Acción | Ejemplo |
|--------|---------|
| Ver el ciclo before → handler → after | `GET http://localhost:7000/isc415` |
| Enviar cabecera personalizada | Agrega `-H "profesor: Juan"` a la petición |
| Probar otros verbos | POST, PUT, DELETE, PATCH sobre `/isc415` |

```bash
# GET — responde con el método y la variable de contexto inyectada por before
curl http://localhost:7000/isc415

# POST con cabecera personalizada
curl -X POST -H "profesor: Juan" http://localhost:7000/isc415

# Listar todos los headers que el cliente envía
curl http://localhost:7000/leerheaders
```

---

### 2. Recepción de datos

#### Query parameters
```bash
# Parámetros en la URL (?clave=valor)
curl "http://localhost:7000/parametros?matricula=20011136&nombre=Carlos"
```

#### Path parameters
```bash
# Parámetro dentro de la URL
curl http://localhost:7000/parametros/20011136/

# Varios parámetros combinados
curl http://localhost:7000/parametros/20011136/nombre/Carlos
```

#### Form data (body)
Abre en el navegador: **http://localhost:7000/formulario.html**
O envíalo con curl:
```bash
curl -X POST -d "matricula=20011136&nombre=Carlos&carrera=ISC" \
     http://localhost:7000/parametros
```

---

### 3. Cookies y Sesiones

Las **cookies** se almacenan en el navegador del cliente.
Las **sesiones** se almacenan en el servidor y se identifican por un ID en una cookie.

```bash
# Crear una cookie con tiempo de vida de 120 segundos
curl -c cookies.txt http://localhost:7000/crearCookie/nombre/Carlos

# Ver las cookies guardadas en el cliente
curl -b cookies.txt http://localhost:7000/listarCookies

# Contador de sesión (incrementa con cada visita — usar el navegador para ver el efecto)
curl -c session.txt -b session.txt http://localhost:7000/contadorSesion

# Invalidar la sesión
curl -c session.txt -b session.txt http://localhost:7000/invalidarSesion
```

**Formulario de login con cookies:** http://localhost:7000/formulario_cookie.html

---

### 4. Autenticación clásica con sesión — `/zona-admin-clasica/`

Muestra cómo proteger rutas verificando si existe un usuario en la sesión HTTP.
El filtro `before` redirige a `/401.html` si no hay sesión activa.

```
1. Abrir http://localhost:7000/login.html
2. Ingresar usuario: cualquiera / contraseña: cualquiera  (FakeServices siempre autentica)
3. Acceder a http://localhost:7000/zona-admin-clasica/
```

> **Concepto de seguridad:** Ver `PruebaRoboSesion.java` para entender
> cómo un atacante podría robar una sesión activa.

---

### 5. Excepciones y códigos de error HTTP

```bash
# 404 Not Found
curl -i http://localhost:7000/excepciones/ruta-no-encontrada

# 401 Unauthorized
curl -i http://localhost:7000/excepciones/ruta-sin-permisos

# Error 500 capturado por el handler de excepciones
curl http://localhost:7000/excepciones/provocando-error

# Ruta que no existe (manejada por el error 404 personalizado)
curl -H "Accept: text/html" http://localhost:7000/ruta-inexistente
```

---

### 6. Plantillas de servidor (Server-Side Rendering)

Tres motores de plantillas para generar HTML dinámicamente en el servidor.

| Motor      | Extensión | URL de ejemplo |
|------------|-----------|----------------|
| Thymeleaf  | `.html`   | http://localhost:7000/thymeleaf |
| FreeMarker | `.ftl`    | http://localhost:7000/freemarker/datosEstudiante/20011136 |
| Velocity   | `.vm`     | http://localhost:7000/velocity |

Las plantillas se ubican en `src/main/resources/templates/`.

---

### 7. CRUD Tradicional (Thymeleaf + sesión)

CRUD completo de estudiantes usando el patrón petición-respuesta clásico
(sin JavaScript, el formulario envía datos al servidor que responde con una nueva página HTML).

```
http://localhost:7000/crud-simple/listar
```

Operaciones disponibles: Listar → Crear → Visualizar → Editar → Eliminar.

> Los datos se almacenan en memoria (`FakeServices`); se pierden al reiniciar el servidor.

---

### 8. API REST — `/api/estudiante`

Servicio REST para el manejo de estudiantes. Usa JSON como formato de intercambio.

```bash
# Listar todos los estudiantes
curl http://localhost:7000/api/estudiante

# Obtener un estudiante por matrícula
curl http://localhost:7000/api/estudiante/20011136

# Crear un estudiante
curl -X POST -H "Content-Type: application/json" \
     -d '{"matricula":20011200,"nombre":"Ana López","carrera":"ISC"}' \
     http://localhost:7000/api/estudiante

# Actualizar un estudiante (enviar el objeto completo)
curl -X PUT -H "Content-Type: application/json" \
     -d '{"matricula":20011200,"nombre":"Ana María López","carrera":"ITT"}' \
     http://localhost:7000/api/estudiante

# Eliminar un estudiante
curl -X DELETE http://localhost:7000/api/estudiante/20011200
```

---

### 9. Control de acceso basado en roles — `/zona-admin-role`

Demuestra RBAC (Role-Based Access Control). Cada endpoint declara qué roles pueden accederlo.
El filtro `beforeMatched` verifica que el usuario en sesión tenga el rol requerido.

**Usuarios precargados** (en `FakeServices`):

| Usuario    | Contraseña | Roles                              |
|------------|------------|------------------------------------|
| `admin`    | `1234`      | ROLE_ADMIN, LOGUEADO, CUALQUIERA   |
| `logueado` | `logueado`  | CUALQUIERA                         |
| `usuario`  | `usuario`   | ROLE_USUARIO                       |

```
1. Autenticarse en http://localhost:7000/login.html  (usuario: admin, contraseña: 1234)
2. Probar los endpoints:
   http://localhost:7000/zona-admin-role           → requiere LOGUEADO
   http://localhost:7000/zona-admin-role/admin     → requiere ROLE_ADMIN
   http://localhost:7000/zona-admin-role/cliente   → requiere ROLE_USUARIO
   http://localhost:7000/zona-admin-role/otro-rol  → cualquier rol
```

---

### 10. Ejemplos HTML5 avanzados

Páginas de demostración de APIs del navegador (sin frameworks).

| Demo | URL |
|------|-----|
| Web Storage (localStorage/sessionStorage) | http://localhost:7000/html5/ejemploWebStorage.html |
| IndexedDB | http://localhost:7000/html5/ejemploIndexedDb.html |
| Geolocalización | http://localhost:7000/html5/ejemploGeoLocalizacion.html |
| File API | http://localhost:7000/html5/ejemploArchivosApi.html |
| Web Workers | http://localhost:7000/html5/ejemploWorker.html |
| Service Worker / Offline | http://localhost:7000/html5/EjemploSinConexion.html |
| Índice completo | http://localhost:7000/html5/index.html |

El endpoint `/fecha` devuelve la hora del servidor y es consumido por los ejemplos de
Web Workers con AJAX: **http://localhost:7000/fecha**

---

## Estructura del proyecto

```
src/main/java/edu/pucmm/eict/
├── Main.java                          ← Punto de entrada; configura Javalin
├── controladores/
│   ├── ApiControlador.java            ← Handlers estáticos para /api/estudiante
│   ├── ConceptoBasicosControlador.java← Verbos HTTP, before/after, cabeceras
│   ├── CookiesSesionesControlador.java← Cookies, sesiones, autenticación
│   ├── CrudTradicionalControlador.java← CRUD con plantillas Thymeleaf
│   ├── ExcepcionesControlador.java    ← Manejo de excepciones y errores HTTP
│   ├── PlantillasControlador.java     ← Thymeleaf, FreeMarker, Velocity
│   ├── ZonaAdminClasica.java          ← Auth clásica con sesión
│   └── ZonaAdminConRoles.java         ← RBAC con RouteRole
├── encapsulaciones/
│   ├── Estudiante.java                ← POJO
│   └── Usuario.java                   ← POJO con roles
├── servicios/
│   └── FakeServices.java              ← Singleton con datos en memoria
└── util/
    ├── BaseControlador.java           ← Clase base (recibe JavalinConfig)
    ├── NoExisteEstudianteException.java
    ├── PruebaRoboSesion.java          ← Demo de robo de sesión con Jsoup
    └── RolesApp.java                  ← Enum de roles (implementa RouteRole)

src/main/resources/
├── publico/                           ← Archivos estáticos (HTML, CSS, JS)
│   └── html5/                         ← Demos de APIs HTML5
└── templates/                         ← Plantillas de servidor
    ├── crud-tradicional/              ← Thymeleaf para el CRUD
    ├── freemarker/                    ← Plantillas .ftl
    ├── thymeleaf/                     ← Plantillas .html
    └── velocity/                      ← Plantillas .vm
```

---

## Diferencias clave frente a Javalin 6

| Javalin 6 | Javalin 7 |
|-----------|-----------|
| `app.get()`, `app.before()`, etc. en cualquier momento | Rutas solo dentro de `Javalin.create(config -> {...})` |
| `config.router.apiBuilder()` | `config.routes.apiBuilder()` |
| `config.registerPlugin(new RouteOverviewPlugin())` | `config.bundledPlugins.enableRouteOverview("/routes")` |
| `validator.get()` devuelve no-nulo | `validator.required().get()` para comportamiento equivalente |
| `javalin-rendering` (un solo artefacto) | `javalin-rendering-thymeleaf`, `-freemarker`, `-velocity` |
| `BaseControlador(Javalin app)` | `BaseControlador(JavalinConfig config)` |
| Jetty 11 (`javax.servlet.*`) | Jetty 12 (`jakarta.servlet.*`) |
