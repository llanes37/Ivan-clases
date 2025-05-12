# 📘 UT3 - API REST SIN SPRING (TEORÍA + ESTUDIO)

## 🌐 ¿Qué es una API REST?

Una **API REST** (Representational State Transfer) es una forma de construir servicios web que permiten la comunicación entre sistemas utilizando el protocolo HTTP. Cada recurso se representa con una URL y se manipula con métodos HTTP estándar:

| Método | Acción     | Descripción                    |
| ------ | ---------- | ------------------------------ |
| GET    | Leer       | Obtener datos de un recurso    |
| POST   | Crear      | Insertar nuevos datos          |
| PUT    | Actualizar | Modificar un recurso existente |
| DELETE | Eliminar   | Borrar un recurso              |

## 🧠 Fundamentos de REST sin Spring

Puedes crear un servicio REST en Java **sin usar frameworks** como Spring. Java ofrece:

* `com.sun.net.httpserver.HttpServer`: clase embebida para crear servidores HTTP.
* Métodos como `.createContext()`, `.sendResponseHeaders()`, etc., para manejar rutas.
* Manejo de JSON manual con `String` o con librerías como Gson (opcional).

## 🛠️ Conceptos clave que usamos

* **HttpServer**: servidor embebido incluido en Java.
* **HttpExchange**: clase que representa una solicitud/respuesta HTTP.
* **InputStream / OutputStream**: lectura y escritura de datos.
* **Map\<Long, Producto>**: almacenamiento en memoria.
* **String JSON manual**: sin librerías externas.

---

## 🧪 EJEMPLO EXPLICADO: `ApiRestProductos.java`

Este archivo `.java` crea una API completa con:

### 1️⃣ Servidor HTTP en el puerto 8000

```java
HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
```

### 2️⃣ Rutas soportadas

* `/productos` (GET, POST)
* `/productos/{id}` (GET, PUT, DELETE)

Usamos `.createContext("/productos", handler)` y en el handler inspeccionamos `exchange.getRequestMethod()` y la ruta para tomar decisiones.

### 3️⃣ Clase Producto (modelo)

```java
class Producto {
    private long id;
    private String nombre;
    private double precio;
    // Métodos getter, setter, toJson(), fromJson()
}
```

Se convierte manualmente desde/hacia JSON usando `String.format` y `split()`.

### 4️⃣ CRUD básico en memoria

```java
Map<Long, Producto> productos = new HashMap<>();
```

El `Map` actúa como una base de datos temporal. Cada producto se guarda con su `id`.

### 5️⃣ Métodos HTTP implementados

* `listar()`: devuelve todos los productos
* `obtener(id)`: devuelve uno por ID
* `crear()`: añade un producto con POST
* `actualizar(id)`: modifica uno con PUT
* `eliminar(id)`: borra con DELETE

---

## 🧪 Cómo probar desde terminal

```bash
# Crear un producto
curl -X POST http://localhost:8000/productos -d '{"nombre":"Camiseta","precio":19.99}' -H "Content-Type: application/json"

# Listar productos
curl http://localhost:8000/productos

# Ver un producto
curl http://localhost:8000/productos/1

# Actualizar
curl -X PUT http://localhost:8000/productos/1 -d '{"nombre":"Pantalón","precio":29.99}' -H "Content-Type: application/json"

# Eliminar
curl -X DELETE http://localhost:8000/productos/1
```

---

## 📚 Glosario rápido

* **HttpServer**: servidor web embebido (sin librerías).
* **Handler**: función que procesa la ruta.
* **JSON**: formato de intercambio de datos.
* **HashMap**: estructura clave/valor para almacenar objetos.
* **`Thread.sleep()`**: pausa de ejecución (si usamos tareas en background).

---

## 🎯 Ejercicios para practicar

1. ➕ Añade un campo `stock` a Producto.
2. ❗ Valida que `precio` no pueda ser negativo.
3. ❌ Si falta `nombre` o `precio`, devuelve error 400.
4. 🔍 Añade ruta `/productos/buscar?min=10` que filtre por precio mínimo.
5. 💾 Guarda los productos en un archivo `.json` y cárgalos al arrancar.

---

## ✅ Conclusión

Este enfoque es ideal para **aprender REST desde cero**, sin frameworks, usando sólo Java puro. Luego podrás migrar a frameworks como Spring Boot, comprendiendo cómo funcionan las peticiones y rutas por debajo.

---

¿Listo para pasar a hacer autenticación básica o añadir persistencia real con ficheros? ¡Puedes hacerlo desde aquí! 💪
