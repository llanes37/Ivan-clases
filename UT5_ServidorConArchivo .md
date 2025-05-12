# 📘 UT5 - API REST con persistencia en archivos (Java puro)

Esta unidad trata sobre cómo guardar los datos de nuestra API REST en un archivo `.json`, sin usar frameworks ni bases de datos externas. Es ideal para el examen o proyectos en entorno controlado.

---

## 🎯 Objetivos

* Aprender a **persistir datos** en un archivo plano.
* Usar **HttpServer** con CRUD completo.
* Usar `java.nio.file.Files` para leer y escribir archivos.
* Validar y transformar datos entre objetos y JSON.

---

## 🧠 Teoría Clave

### ¿Qué es persistencia?

Es la capacidad de **mantener datos guardados** aunque el programa se cierre. Aquí usamos un archivo `alumnos.json` para simular una base de datos.

### ¿Qué es JSON?

Es un formato de intercambio de datos. Usamos JSON manualmente:

```json
{ "id": 1, "nombre": "Ana", "edad": 20 }
```

### ¿Qué usamos?

* `Files.readString()` para leer archivos.
* `Files.writeString()` para guardar cambios.
* Un `HashMap<Integer, Alumno>` como base temporal.
* Clases internas para manejar objetos y JSON.

---

## 🔧 Estructura del servidor

```java
HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
server.createContext("/alumnos", UT5_ServidorConArchivo::gestionar);
```

La clase `gestionar()` decide qué función ejecutar según la ruta y el método:

| Ruta          | Método | Acción         |
| ------------- | ------ | -------------- |
| /alumnos      | GET    | Listar alumnos |
| /alumnos/{id} | GET    | Ver uno        |
| /alumnos      | POST   | Crear          |
| /alumnos/{id} | PUT    | Modificar      |
| /alumnos/{id} | DELETE | Eliminar       |

---

## 💬 Ejemplos de uso con cURL

### 1️⃣ GET todos

```bash
curl http://localhost:8080/alumnos
```

### 2️⃣ GET por ID

```bash
curl http://localhost:8080/alumnos/1
```

### 3️⃣ POST nuevo alumno

```bash
curl -X POST http://localhost:8080/alumnos \
     -d '{"nombre":"Juan","edad":22}' \
     -H "Content-Type: application/json"
```

### 4️⃣ PUT actualizar alumno

```bash
curl -X PUT http://localhost:8080/alumnos/1 \
     -d '{"nombre":"Pedro","edad":25}' \
     -H "Content-Type: application/json"
```

### 5️⃣ DELETE eliminar

```bash
curl -X DELETE http://localhost:8080/alumnos/1
```

---

## ⚡ Ejemplos con Thunder Client (Visual Studio Code)

1. Abrir la extensión Thunder Client.
2. Crear una nueva petición.
3. Seleccionar el método (GET, POST...).
4. Escribir la URL (ej. `http://localhost:8080/alumnos`).
5. En POST/PUT, ir a la pestaña **Body → JSON** y escribir:

```json
{
  "nombre": "Lucía",
  "edad": 19
}
```

6. Hacer clic en **Send**.

---

## 💾 Cómo funciona la persistencia

### Al crear/modificar/borrar:

```java
Files.writeString(Path.of("alumnos.json"), contenido, StandardCharsets.UTF_8);
```

### Al arrancar:

```java
if (Files.exists(Path.of("alumnos.json"))) {
  String json = Files.readString(...);
  // Parsear manualmente
}
```

---

## 📦 Clase Alumno

```java
class Alumno {
  int id;
  String nombre;
  int edad;

  String toJson() {...}
  static Alumno fromJson(String json) {...}
}
```

---

## 🧪 Ejercicios para practicar

1. ✅ Añadir validación: que edad > 0 y nombre no esté vacío.
2. ✅ Añadir campo `email` al JSON.
3. ✅ Crear ruta `/alumnos/menores` que filtre los menores de edad.
4. ✅ Guardar una copia `alumnos_backup.json` en cada modificación.
5. ✅ Leer desde `.csv` en lugar de JSON (opcional).
6. ✅ Añadir ordenación al hacer GET por nombre o edad.

---

## ✅ Conclusión

Con este enfoque puedes:

* Simular una base de datos sin instalar nada.
* Hacer peticiones con herramientas sencillas.
* Practicar para el examen sin necesidad de Maven o Spring Boot.

Es una solución didáctica y totalmente funcional que refuerza conceptos de API REST, JSON y persistencia real.
