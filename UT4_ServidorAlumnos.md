# 📘 UT4 - API REST en Java Puro con HttpServer

Esta unidad explica cómo implementar una API REST completamente funcional sin frameworks, solo con Java y `HttpServer`. Perfecto para el examen si no puedes usar Spring Boot.

---

## 📌 Objetivos de UT4

* Comprender cómo funciona una API REST desde cero.
* Implementar un CRUD usando rutas HTTP.
* Enviar y recibir datos en formato JSON.
* Probar servicios con `curl` o Thunder Client.

---

## 🔧 ¿Qué es HttpServer?

`HttpServer` es una clase incluida en Java (desde Java 6) que permite:

* Crear un servidor embebido ligero.
* Escuchar peticiones HTTP.
* Responder con texto o JSON.

No necesitas librerías externas.

---

## 🌐 ¿Qué es una API REST?

Una API REST permite acceder a datos y funciones usando métodos HTTP estándar.

| Método | Descripción            | Ejemplo ruta             |
| ------ | ---------------------- | ------------------------ |
| GET    | Obtener información    | `/alumnos`, `/alumnos/1` |
| POST   | Crear un nuevo recurso | `/alumnos`               |
| PUT    | Modificar un recurso   | `/alumnos/1`             |
| DELETE | Borrar un recurso      | `/alumnos/1`             |

---

## 🧱 Estructura del proyecto (Java puro)

```java
public class UT4_ServidorAlumnos {
    static Map<Integer, Alumno> bd = new HashMap<>();
    static int idAuto = 1;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/alumnos", UT4_ServidorAlumnos::gestionarAlumnos);
        server.start();
    }

    // Métodos: listar(), crear(), obtener(), actualizar(), eliminar()
}
```

---

## 🧠 Clave para el examen: estructura de cada endpoint

### 1️⃣ GET /alumnos

* Devuelve la lista de todos los alumnos.

```bash
curl http://localhost:8080/alumnos
```

Thunder: GET - URL `http://localhost:8080/alumnos`

### 2️⃣ GET /alumnos/{id}

* Devuelve un alumno por ID.

```bash
curl http://localhost:8080/alumnos/1
```

Thunder: GET - URL `http://localhost:8080/alumnos/1`

### 3️⃣ POST /alumnos

* Crea un alumno.

```bash
curl -X POST http://localhost:8080/alumnos \
     -d '{"nombre":"Juan","edad":21}' \
     -H "Content-Type: application/json"
```

Thunder: POST - URL + Body JSON

### 4️⃣ PUT /alumnos/{id}

* Modifica un alumno existente.

```bash
curl -X PUT http://localhost:8080/alumnos/1 \
     -d '{"nombre":"Ana","edad":22}' \
     -H "Content-Type: application/json"
```

### 5️⃣ DELETE /alumnos/{id}

* Elimina un alumno por ID.

```bash
curl -X DELETE http://localhost:8080/alumnos/1
```

---

## 🧾 Clase Alumno

```java
class Alumno {
    private int id;
    private String nombre;
    private int edad;

    // Constructor, getters, setters
    // toJson() → devuelve String JSON
    // fromJson(String) → construye un objeto desde JSON
}
```

### Ejemplo JSON:

```json
{
  "id": 1,
  "nombre": "Juan",
  "edad": 20
}
```

---

## 🧠 Conceptos clave explicados

* **HttpExchange**: representa una petición y permite leer/escribir la respuesta.
* **RequestMethod**: define si la llamada es GET, POST, etc.
* **InputStream / OutputStream**: se usan para leer el body y responder.
* **Map como BD**: se guarda todo en un `HashMap<Integer, Alumno>`.
* **Id autoincremental**: `idAuto++` al crear nuevos alumnos.
* **Content-Type**: siempre debes usar `application/json` para enviar y recibir JSON.

---

## 🧪 Ejercicios recomendados para el examen

1. Añade validación: que edad > 0 y nombre no esté vacío.
2. Agrega campo `email` y valida su formato (opcional).
3. Crea una nueva ruta `/alumnos/menores` que filtre los menores de edad.
4. Implementa persistencia: guarda la lista en archivo `.json`.
5. Añade búsqueda por nombre: `/alumnos/buscar?nombre=Ana`
6. Simula errores 400 y 404 con mensajes adecuados.

---

## ✅ Conclusión

Con este enfoque puedes demostrar que entiendes cómo funciona una API REST, manejar JSON, controlar rutas, estados HTTP y simular una base de datos, todo sin frameworks.

Ideal para preguntas teóricas o para implementar durante el examen sin preocuparte por dependencias externas.

---

¿Listo para implementar una ruta extra con filtros? ¿O añadir seguridad con token en cabecera? ¡Puedes seguir mejorándolo! 💪
