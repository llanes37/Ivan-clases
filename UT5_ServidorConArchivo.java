/**
 * 📖 UT5 - API REST con persistencia en archivo JSON (Java puro)
 * ===============================================================
 * Este ejercicio amplía el servidor de UT4 para guardar los datos
 * en un archivo "alumnos.json" y cargarlos al iniciar.
 *
 * 🧠 Teoría básica sobre persistencia:
 * ----------------------------------
 * • Persistencia = mantener los datos después de cerrar el programa.
 * • Usamos archivo JSON como "base de datos" para guardar alumnos.
 * • Cada vez que creamos, editamos o borramos, actualizamos el archivo.
 * • Al iniciar, cargamos los datos del archivo para tenerlos disponibles.
 * • Usamos la clase Files para leer y escribir archivos.
 * • El archivo alumnos.json se guarda junto al .java si no se indica ruta.
 *
 * 📌 Ventajas:
 * - No necesitamos bases de datos externas.
 * - 100% compatible con examen o entornos sin frameworks.
 * - Se puede probar con curl y Thunder Client como UT3 y UT4.
 *
 * 🔗 Peticiones HTTP disponibles y ejemplos:
 * -----------------------------------------
 * ▶️ GET /alumnos
 *   curl http://localhost:8080/alumnos
 *   Thunder Client: Método GET, URL http://localhost:8080/alumnos
 *
 * ▶️ GET /alumnos/{id}
 *   curl http://localhost:8080/alumnos/1
 *   Thunder Client: Método GET, URL http://localhost:8080/alumnos/1
 *
 * ▶️ POST /alumnos
 *   curl -X POST http://localhost:8080/alumnos \
 *        -d '{"nombre":"Juan","edad":20}' \
 *        -H "Content-Type: application/json"
 *   Thunder Client: Método POST, URL + body JSON con nombre y edad
 *
 * ▶️ PUT /alumnos/{id}
 *   curl -X PUT http://localhost:8080/alumnos/1 \
 *        -d '{"nombre":"Pepe","edad":21}' \
 *        -H "Content-Type: application/json"
 *   Thunder Client: Método PUT, URL con id, body JSON modificado
 *
 * ▶️ DELETE /alumnos/{id}
 *   curl -X DELETE http://localhost:8080/alumnos/1
 *   Thunder Client: Método DELETE, URL con el id
 */

// ⚙️ Imports necesarios para servidor, ficheros y estructuras
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class UT5_ServidorConArchivo {

    static Map<Integer, Alumno> bd = new HashMap<>(); // Base de datos en memoria
    static int idAuto = 1;
    static final String ARCHIVO = "alumnos.json"; // 📁 Archivo de almacenamiento persistente

    public static void main(String[] args) throws IOException {
        cargarDesdeArchivo(); // 🔁 Carga inicial de datos del archivo

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/alumnos", UT5_ServidorConArchivo::gestionar);
        server.setExecutor(null); // Ejecutores por defecto
        server.start();
        System.out.println("Servidor iniciado en http://localhost:8080/alumnos");
    }

    // 🔀 Manejo general de rutas
    static void gestionar(HttpExchange ex) throws IOException {
        String[] partes = ex.getRequestURI().getPath().split("/");
        String metodo = ex.getRequestMethod();

        if (partes.length == 2) {
            if (metodo.equals("GET")) listar(ex);
            else if (metodo.equals("POST")) crear(ex);
            else noPermitido(ex);
        } else if (partes.length == 3) {
            int id = Integer.parseInt(partes[2]);
            switch (metodo) {
                case "GET" -> obtener(ex, id);
                case "PUT" -> actualizar(ex, id);
                case "DELETE" -> eliminar(ex, id);
                default -> noPermitido(ex);
            }
        } else {
            responder(ex, 404, "Ruta inválida");
        }
    }

    // 📥 GET /alumnos
    // curl http://localhost:8080/alumnos
    // Thunder Client: método GET, URL http://localhost:8080/alumnos
    static void listar(HttpExchange ex) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        for (Alumno a : bd.values()) sb.append(a.toJson()).append(",");
        if (sb.length() > 1) sb.setLength(sb.length() - 1);
        sb.append("]");
        responder(ex, 200, sb.toString());
    }

    // 📥 GET /alumnos/{id}
    // curl http://localhost:8080/alumnos/1
    // Thunder Client: método GET, URL http://localhost:8080/alumnos/1
    static void obtener(HttpExchange ex, int id) throws IOException {
        Alumno a = bd.get(id);
        if (a == null) responder(ex, 404, "No encontrado");
        else responder(ex, 200, a.toJson());
    }

    // 📤 POST /alumnos
    // curl -X POST http://localhost:8080/alumnos -d '{"nombre":"Juan","edad":20}' -H "Content-Type: application/json"
    // Thunder Client: POST, URL + body JSON con nombre y edad
    static void crear(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Alumno nuevo = Alumno.fromJson(body);
        nuevo.setId(idAuto++);
        bd.put(nuevo.getId(), nuevo);
        guardarEnArchivo();
        responder(ex, 201, nuevo.toJson());
    }

    // 🔁 PUT /alumnos/{id}
    // curl -X PUT http://localhost:8080/alumnos/1 -d '{"nombre":"Pepe","edad":21}' -H "Content-Type: application/json"
    // Thunder Client: PUT, URL con id, body JSON con datos nuevos
    static void actualizar(HttpExchange ex, int id) throws IOException {
        if (!bd.containsKey(id)) {
            responder(ex, 404, "No encontrado");
            return;
        }
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Alumno modificado = Alumno.fromJson(body);
        modificado.setId(id);
        bd.put(id, modificado);
        guardarEnArchivo();
        responder(ex, 200, modificado.toJson());
    }

    // ❌ DELETE /alumnos/{id}
    // curl -X DELETE http://localhost:8080/alumnos/1
    // Thunder Client: DELETE, URL con id del alumno
    static void eliminar(HttpExchange ex, int id) throws IOException {
        if (bd.remove(id) == null) responder(ex, 404, "No encontrado");
        else {
            guardarEnArchivo();
            responder(ex, 204, "");
        }
    }

    static void noPermitido(HttpExchange ex) throws IOException {
        responder(ex, 405, "Método no permitido");
    }

    static void responder(HttpExchange ex, int status, String body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    // 💾 Guardar la base de datos en archivo JSON
    static void guardarEnArchivo() throws IOException {
        StringBuilder sb = new StringBuilder("[");
        for (Alumno a : bd.values()) sb.append(a.toJson()).append(",");
        if (sb.length() > 1) sb.setLength(sb.length() - 1);
        sb.append("]");
        Files.writeString(Path.of(ARCHIVO), sb.toString(), StandardCharsets.UTF_8);
    }

    // 🔃 Leer la base de datos desde archivo JSON al arrancar
    static void cargarDesdeArchivo() {
        try {
            if (!Files.exists(Path.of(ARCHIVO))) return;
            String json = Files.readString(Path.of(ARCHIVO));
            json = json.replaceAll("[\\[\\]]", "");
            for (String entry : json.split("},")) {
                if (!entry.trim().isEmpty()) {
                    if (!entry.endsWith("}")) entry += "}";
                    Alumno a = Alumno.fromJson(entry);
                    a.setId(idAuto++);
                    bd.put(a.getId(), a);
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar el archivo: " + e.getMessage());
        }
    }

    // 📦 Clase Alumno (modelo)
    static class Alumno {
        private int id;
        private String nombre;
        private int edad;

        public Alumno() {}
        public Alumno(int id, String nombre, int edad) {
            this.id = id; this.nombre = nombre; this.edad = edad;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public int getEdad() { return edad; }
        public void setEdad(int edad) { this.edad = edad; }

        public String toJson() {
            return String.format("{\"id\":%d,\"nombre\":\"%s\",\"edad\":%d}", id, nombre, edad);
        }

        public static Alumno fromJson(String json) {
            Map<String, String> map = new HashMap<>();
            json = json.replaceAll("[{}\"]", "");
            for (String p : json.split(",")) {
                String[] kv = p.split(":");
                map.put(kv[0].trim(), kv[1].trim());
            }
            return new Alumno(0, map.get("nombre"), Integer.parseInt(map.get("edad")));
        }
    }
}

/*
 * 🎯 EJERCICIOS PARA EL ALUMNO:
 * -----------------------------
 * 1️⃣ Validar que nombre no esté vacío y edad > 0 antes de guardar.
 * 2️⃣ Añadir campo "email" al alumno y mostrarlo en JSON.
 * 3️⃣ Crear GET /alumnos/menores que devuelva solo los < 18 años.
 * 4️⃣ Añadir ordenación por nombre al listar().
 * 5️⃣ Hacer backup del archivo "alumnos.json" en cada modificación.
 * 6️⃣ Cargar desde .csv en lugar de JSON (extra).
 */