/* 
 * 📘 ¿Qué se estudia en UT4?
 * -------------------------------------------------------
 * En esta unidad implementamos una API REST completa utilizando
 * únicamente Java puro (sin frameworks como Spring Boot) mediante
 * la clase HttpServer incluida en el JDK. Esto permite entender
 * los fundamentos del protocolo HTTP y cómo funciona REST por debajo.
 *
 * 🔍 Conceptos clave:
 * -------------------------------------------------------
 * ✅ API REST: conjunto de rutas que responden a peticiones HTTP.
 * ✅ CRUD: operaciones básicas Create, Read, Update, Delete.
 * ✅ HttpExchange: clase para manejar cada solicitud/respuesta HTTP.
 * ✅ HttpServer: servidor HTTP embebido nativo de Java.
 * ✅ JSON manual: simulamos JSON usando String.format y split.
 * ✅ Body de petición: lectura de datos enviados con POST o PUT.
 * ✅ Rutas dinámicas: se interpreta el path para determinar acciones.
 * ✅ Métodos HTTP: GET (leer), POST (crear), PUT (modificar), DELETE (eliminar).
 * ✅ Cabeceras y códigos de estado HTTP: 200, 201, 204, 404, 405, etc.
 *
 * 🧠 ¿Por qué es importante?
 * -------------------------------------------------------
 * - Permite entender REST sin depender de frameworks externos.
 * - Sienta las bases para proyectos más avanzados (Spring Boot, Node.js).
 * - Entrena la lógica de backend y el tratamiento de rutas y datos.
 * - Ayuda a desarrollar pruebas con herramientas como curl o Thunder Client.
 *
 * Este archivo contiene:
 * - 🌐 Un servidor HTTP funcional en el puerto 8080.
 * - ✏️ Un CRUD completo para una entidad "Alumno".
 * - 🧪 Pruebas posibles con curl y Thunder Client.
 * - 🧠 Comentarios y teoría útil para el examen.
 * - 🎯 Ejercicios propuestos para ampliar y practicar.
 
 * =======================================================
 * Este ejercicio implementa un servidor HTTP funcional en Java
 * que expone una API REST para gestionar una lista de alumnos.
 * 
 * Incluye teoría, explicaciones línea por línea y ejercicios finales.
 *
 * Se aprende a:
 * - Crear un servidor REST con Java sin frameworks.
 * - Implementar CRUD (GET, POST, PUT, DELETE).
 * - Simular JSON de entrada/salida sin librerías externas.
 * - Usar colecciones como almacenamiento en memoria. */
 
import com.sun.net.httpserver.*; // 📦 Librería nativa de Java para crear servidores HTTP
import java.io.*;                // 📦 Para leer/escribir datos
import java.net.InetSocketAddress; // 🌐 Para definir puerto de escucha
import java.nio.charset.StandardCharsets; // 🧵 Para codificación de caracteres
import java.util.*;             // 📚 Para usar HashMap, List, etc.

public class UT4_ServidorAlumnos {

    // 🧠 Base de datos en memoria (clave = ID, valor = Alumno)
    static Map<Integer, Alumno> bd = new HashMap<>();
    static int idAuto = 1; // 🔢 Autoincremento de IDs

    public static void main(String[] args) throws IOException {
        // 🌐 Creamos el servidor en el puerto 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 📍 Ruta principal para alumnos
        server.createContext("/alumnos", UT4_ServidorAlumnos::gestionarAlumnos);

        server.setExecutor(null); // 🔄 Usa el executor por defecto
        server.start(); // ▶️ Arranca el servidor

        System.out.println("Servidor escuchando en http://localhost:8080/alumnos");
    }

    // 🔀 Gestiona todas las rutas: GET/POST/PUT/DELETE
// ------------------------------------------------------------
// 📌 Este método se encarga de identificar la ruta accedida y el tipo de método HTTP recibido
//    y redirigir la ejecución al método correspondiente (listar, crear, obtener, actualizar o eliminar).
//
// 📍 Ejemplos de rutas posibles:
// - GET /alumnos → lista todos los alumnos
// - POST /alumnos → crea uno nuevo
// - GET /alumnos/1 → obtiene el alumno con ID 1
// - PUT /alumnos/1 → actualiza los datos del alumno con ID 1
// - DELETE /alumnos/1 → elimina el alumno con ID 1
//
// 🔍 Este método extrae el path y lo divide con split("/") para saber cuántos elementos tiene la ruta:
// - Si tiene 2 → es la ruta base /alumnos
// - Si tiene 3 → incluye un ID, por ejemplo /alumnos/2
//
// 🔁 Según el método HTTP (GET, POST, etc.) llama a la función correspondiente.
    public static void gestionarAlumnos(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath(); // 📄 /alumnos o /alumnos/1
        String method = ex.getRequestMethod();      // 📥 GET, POST, etc.
        String[] partes = path.split("/");

        if (partes.length == 2) { // 📍 /alumnos
            if (method.equals("GET")) listar(ex);
            else if (method.equals("POST")) crear(ex);
            else noPermitido(ex);
        } else if (partes.length == 3) { // 📍 /alumnos/{id}
            int id = Integer.parseInt(partes[2]);
            switch (method) {
                case "GET" -> obtener(ex, id);
                case "PUT" -> actualizar(ex, id);
                case "DELETE" -> eliminar(ex, id);
                default -> noPermitido(ex);
            }
        } else {
            responder(ex, 404, "Ruta no válida");
        }
    }

    // 📤 GET /alumnos
    // curl http://localhost:8080/alumnos
    // Thunder: método GET, URL http://localhost:8080/alumnos
    // 📤 GET /alumnos
// ------------------------------------------------------------
// ▶️ ¿Qué hace este método?
// Este método devuelve una lista de todos los alumnos registrados en formato JSON.
// Recorre el mapa en memoria, convierte cada alumno a JSON y construye una lista tipo [ {...}, {...} ]
// Luego la envía como respuesta con código HTTP 200.
static void listar(HttpExchange ex) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        for (Alumno a : bd.values()) {
            sb.append(a.toJson()).append(",");
        }
        if (sb.length() > 1) sb.setLength(sb.length() - 1); // 🧹 Elimina coma final
        sb.append("]");
        responder(ex, 200, sb.toString());
    }

    // 📥 GET /alumnos/{id}
    // curl http://localhost:8080/alumnos/1
    // Thunder: método GET, URL http://localhost:8080/alumnos/1
    // 📥 GET /alumnos/{id}
// ------------------------------------------------------------
// ▶️ ¿Qué hace este método?
// Este método busca un alumno por su ID en el mapa de datos.
// Si lo encuentra, lo convierte a JSON y lo devuelve con código 200.
// Si no lo encuentra, devuelve un mensaje con código 404 (no encontrado).
static void obtener(HttpExchange ex, int id) throws IOException {
        Alumno a = bd.get(id);
        if (a == null) responder(ex, 404, "No encontrado");
        else responder(ex, 200, a.toJson());
    }

    // 📤 POST /alumnos
    // curl -X POST http://localhost:8080/alumnos -d '{"nombre":"Juan","edad":20}' -H "Content-Type: application/json"
    // Thunder: método POST, URL http://localhost:8080/alumnos, body JSON con nombre y edad
    // 📤 POST /alumnos
// ------------------------------------------------------------
// ▶️ ¿Qué hace este método?
// Este método recibe un JSON en el cuerpo de la petición,
// lo convierte a un objeto Alumno, le asigna un ID autoincrementado,
// lo guarda en el mapa y lo devuelve como respuesta con código 201 (creado).
static void crear(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Alumno nuevo = Alumno.fromJson(body);
        nuevo.setId(idAuto++); // 🆔 Asigna ID automáticamente
        bd.put(nuevo.getId(), nuevo);
        responder(ex, 201, nuevo.toJson());
    }

    // 🔁 PUT /alumnos/{id}
    // curl -X PUT http://localhost:8080/alumnos/1 -d '{"nombre":"Ana","edad":22}' -H "Content-Type: application/json"
    // Thunder: método PUT, URL http://localhost:8080/alumnos/1, body JSON con los datos nuevos
    // 🔁 PUT /alumnos/{id}
// ------------------------------------------------------------
// ▶️ ¿Qué hace este método?
// Este método busca un alumno existente por su ID.
// Si lo encuentra, reemplaza su contenido con los nuevos datos recibidos en el body.
// Si no lo encuentra, devuelve error 404.
static void actualizar(HttpExchange ex, int id) throws IOException {
        Alumno antiguo = bd.get(id);
        if (antiguo == null) {
            responder(ex, 404, "No encontrado");
            return;
        }
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Alumno nuevo = Alumno.fromJson(body);
        nuevo.setId(id);
        bd.put(id, nuevo);
        responder(ex, 200, nuevo.toJson());
    }

    // ❌ DELETE /alumnos/{id}
    // curl -X DELETE http://localhost:8080/alumnos/1
    // Thunder: método DELETE, URL http://localhost:8080/alumnos/1
    // ❌ DELETE /alumnos/{id}
// ------------------------------------------------------------
// ▶️ ¿Qué hace este método?
// Elimina un alumno si existe, basándose en su ID.
// Devuelve 204 si fue eliminado, o 404 si no se encontró.
static void eliminar(HttpExchange ex, int id) throws IOException {
        if (bd.remove(id) == null) responder(ex, 404, "No encontrado");
        else responder(ex, 204, "");
    }

    // ⚠️ Método no permitido
    static void noPermitido(HttpExchange ex) throws IOException {
        responder(ex, 405, "Método no permitido");
    }

    // 📤 Envía la respuesta al cliente
    // 📤 Envía la respuesta al cliente
// ------------------------------------------------------------
// ▶️ ¿Qué hace este método?
// Construye y envía la respuesta HTTP al cliente:
// - Incluye código de estado (200, 404, etc.)
// - Añade cabecera de tipo Content-Type
// - Escribe el cuerpo con el contenido en bytes UTF-8
static void responder(HttpExchange ex, int status, String body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, bytes.length);
        OutputStream os = ex.getResponseBody();
        os.write(bytes);
        os.close();
    }

    // 🧾 Clase interna Alumno (modelo)
    static class Alumno {
        private int id;
        private String nombre;
        private int edad;

        public Alumno() {}

        public Alumno(int id, String nombre, int edad) {
            this.id = id;
            this.nombre = nombre;
            this.edad = edad;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public int getEdad() { return edad; }
        public void setEdad(int edad) { this.edad = edad; }

        // 🔁 Convertir a JSON manualmente
        public String toJson() {
            return String.format("{\"id\":%d,\"nombre\":\"%s\",\"edad\":%d}", id, nombre, edad);
        }

        // 🔁 Crear objeto desde un JSON plano
        public static Alumno fromJson(String json) {
            Map<String, String> map = new HashMap<>();
            json = json.replaceAll("[{}\"]", "");
            for (String pair : json.split(",")) {
                String[] kv = pair.split(":");
                map.put(kv[0].trim(), kv[1].trim());
            }
            return new Alumno(0, map.get("nombre"), Integer.parseInt(map.get("edad")));
        }
    }
}

/*
 * 🧪 PRÁCTICA COMPLETA CON THUNDER CLIENT
 * ----------------------------------------
 * Puedes usar Visual Studio Code con la extensión Thunder Client
 * para probar todos los endpoints de esta API REST.

 * BASE URL: http://localhost:8080/alumnos

 * 1️⃣ ✅ GET → Listar todos los alumnos
 * - Método: GET
 * - URL: http://localhost:8080/alumnos
 * - Sin body

 * 2️⃣ ✅ POST → Crear nuevo alumno
 * - Método: POST
 * - URL: http://localhost:8080/alumnos
 * - Headers:
 *     Content-Type: application/json
 * - Body (JSON):
   {
     "nombre": "Lucía",
     "edad": 21
   }

 * 3️⃣ ✅ GET → Obtener alumno por ID
 * - Método: GET
 * - URL: http://localhost:8080/alumnos/1

 * 4️⃣ ✅ PUT → Modificar alumno por ID
 * - Método: PUT
 * - URL: http://localhost:8080/alumnos/1
 * - Headers:
 *     Content-Type: application/json
 * - Body (JSON):
   {
     "nombre": "Lucía Gómez",
     "edad": 22
   }

 * 5️⃣ ✅ DELETE → Eliminar alumno por ID
 * - Método: DELETE
 * - URL: http://localhost:8080/alumnos/1

 * 📌 IMPORTANTE: Cada prueba se puede hacer desde Thunder Client o cURL.
 * También puedes probar errores:
 * - Obtener un ID que no existe → debe devolver 404.
 * - Crear sin body → debe fallar con error 500 o 400.
 * - Modificar con datos mal formateados → debe dar error.

 * -------------------------------------------------------------
 * 🎯 EJERCICIOS DE EXTENSIÓN CON CÓDIGO PARA PRACTICAR
 * -------------------------------------------------------------

 * ✏️ EJERCICIO 1: Agrega validación de edad y nombre
 * - Dentro del método `crear()` y `actualizar()`, antes de guardar:
   if (nuevo.getEdad() <= 0 || nuevo.getNombre().isEmpty()) {
       responder(ex, 400, "Datos inválidos: nombre vacío o edad incorrecta");
       return;
   }

 * ✏️ EJERCICIO 2: Añadir campo nuevo "email" en la clase Alumno
 * - En la clase Alumno:
   private String email;
   // En los métodos toJson y fromJson, incluir también el email.
 * - En el JSON:
   {
     "nombre": "Pedro",
     "edad": 20,
     "email": "pedro@email.com"
   }

 * ✏️ EJERCICIO 3: Crear nueva ruta GET /alumnos/menores
 * - En el método main():
   server.createContext("/alumnos/menores", UT4_ServidorAlumnos::listarMenores);

 * - Luego añade el método:
   static void listarMenores(HttpExchange ex) throws IOException {
       StringBuilder sb = new StringBuilder("[");
       for (Alumno a : bd.values()) {
           if (a.getEdad() < 18) sb.append(a.toJson()).append(",");
       }
       if (sb.length() > 1) sb.setLength(sb.length() - 1);
       sb.append("]");
       responder(ex, 200, sb.toString());
   }

 * ✏️ EJERCICIO 4: Añadir persistencia con fichero JSON
 * - Cada vez que se crea, modifica o elimina, escribir el mapa en archivo "alumnos.json":
   Files.writeString(Path.of("alumnos.json"), json, StandardCharsets.UTF_8);

 * ✏️ EJERCICIO 5: Buscar alumno por nombre
 * - Crear nueva ruta `/alumnos/buscar?nombre=Juan`
 * - Extraer parámetro de la query con:
   String query = ex.getRequestURI().getQuery();
 * - Filtrar el mapa por coincidencias parciales del nombre.

 * 🧠 Con estas prácticas puedes repasar toda la lógica REST, validaciones, manejo de rutas y JSON sin usar frameworks externos.
 * Aporta una base sólida para Spring Boot, Node.js o cualquier backend profesional.
 */
