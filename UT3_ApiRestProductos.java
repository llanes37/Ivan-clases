/**
 * 📘 UT3 - API REST sin Spring totalmente funcional en un único archivo Java
 * ========================================================================
 * ✔️ CRUD completo de productos: Crear, Leer, Actualizar, Eliminar
 * ✔️ Almacenamiento en memoria (HashMap con ID autoincremental)
 * ✔️ Manejo manual de JSON (entrada y salida)
 * ✔️ Soporte CORS para pruebas desde frontend
 * ✔️ Filtro por precio mínimo vía query param
 *
 * ✅ Este archivo está pensado para aprender REST de forma práctica y didáctica.
 * Puedes probar todo desde Thunder Client (VS Code) o Postman.
 */

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UT3_ApiRestProductos {

    // 🗂 Repositorio en memoria (clave: ID, valor: Producto)
    static Map<Long, Producto> productos = new HashMap<>();
    static long contadorId = 1; // 🧮 Contador autoincremental para ID de productos

    /**
     * 🚀 Método principal: inicia servidor HTTP embebido en puerto 8000 (o PORT/env/args)
     */
    public static void main(String[] args) throws IOException {
        int puerto = 8000;
        String env = System.getenv("PORT");
        if (env != null) try { puerto = Integer.parseInt(env); } catch (NumberFormatException ignored) {}
        else if (args.length > 0) try { puerto = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}

        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(puerto), 0);
        } catch (BindException e) {
            System.err.println("❌ Error: puerto " + puerto + " en uso. Usa otro puerto o cambia PORT");
            return;
        }

        server.createContext("/productos", UT3_ApiRestProductos::handleProductos); // Rutas base
        server.setExecutor(null); // Usa el executor por defecto
        server.start();
        System.out.println("✅ Servidor iniciado en http://localhost:" + puerto + "/productos");
    }

    /**
     * 📍 Enrutador de todas las operaciones de /productos
     */
    public static void handleProductos(HttpExchange ex) throws IOException {
        // 🔐 CORS para permitir peticiones externas desde frontend (por ejemplo con fetch)
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1); return;
        }

        String path = ex.getRequestURI().getPath();        // 🛣 /productos o /productos/{id}
        String method = ex.getRequestMethod();             // 🔁 GET, POST, PUT, DELETE
        String[] partes = path.split("/");

        if (partes.length == 2) {
            switch (method) {
                case "GET" -> listar(ex);
                case "POST" -> crear(ex);
                default -> metodoNoPermitido(ex);
            }
        } else if (partes.length == 3) {
            String recurso = partes[2];
            if (recurso.equals("mascaros") && method.equals("GET")) {
                filtrarPorPrecio(ex);
                return;
            }
            long id;
            try { id = Long.parseLong(recurso); } catch (NumberFormatException e) {
                responder(ex, 400, jsonError("ID inválido o recurso no encontrado")); return;
            }
            switch (method) {
                case "GET" -> obtener(ex, id);
                case "PUT" -> actualizar(ex, id);
                case "DELETE" -> eliminar(ex, id);
                default -> metodoNoPermitido(ex);
            }
        } else responder(ex, 404, jsonError("Ruta no válida"));
    }

    // 🔍 GET /productos
    private static void listar(HttpExchange ex) throws IOException {
        List<String> lista = new ArrayList<>();
        for (Producto p : productos.values()) lista.add(p.toJson());
        responder(ex, 200, "[" + String.join(",", lista) + "]");
    }

    // 🔍 GET /productos/{id}
    private static void obtener(HttpExchange ex, long id) throws IOException {
        Producto p = productos.get(id);
        if (p == null) responder(ex, 404, jsonError("Producto no encontrado"));
        else responder(ex, 200, p.toJson());
    }

    // 🧾 POST /productos (crear nuevo)
    private static void crear(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            Producto p = Producto.fromJson(body);
            p.setId(contadorId++);
            productos.put(p.getId(), p);
            responder(ex, 201, p.toJson());
        } catch (IllegalArgumentException e) {
            responder(ex, 400, jsonError(e.getMessage()));
        }
    }

    // ♻️ PUT /productos/{id} (actualizar)
    private static void actualizar(HttpExchange ex, long id) throws IOException {
        if (!productos.containsKey(id)) {
            responder(ex, 404, jsonError("Producto no encontrado")); return;
        }
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        try {
            Producto p = Producto.fromJson(body);
            p.setId(id);
            productos.put(id, p);
            responder(ex, 200, p.toJson());
        } catch (IllegalArgumentException e) {
            responder(ex, 400, jsonError(e.getMessage()));
        }
    }

    // ❌ DELETE /productos/{id} (eliminar)
    private static void eliminar(HttpExchange ex, long id) throws IOException {
        if (productos.remove(id) == null)
            responder(ex, 404, jsonError("Producto no encontrado"));
        else responder(ex, 204, "");
    }

    // 🎯 GET /productos/mascaros?precio=XX
    private static void filtrarPorPrecio(HttpExchange ex) throws IOException {
        String query = ex.getRequestURI().getQuery();
        double min;
        try {
            Map<String,String> params = queryToMap(query);
            min = Double.parseDouble(params.getOrDefault("precio","0"));
        } catch (Exception e) {
            responder(ex, 400, jsonError("Parámetro precio inválido")); return;
        }
        List<String> resultado = new ArrayList<>();
        for (Producto p : productos.values())
            if (p.getPrecio() >= min) resultado.add(p.toJson());
        responder(ex, 200, "[" + String.join(",", resultado) + "]");
    }

    private static void metodoNoPermitido(HttpExchange ex) throws IOException {
        responder(ex, 405, jsonError("Método no permitido"));
    }

    private static void responder(HttpExchange ex, int status, String body) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    private static String jsonError(String msg) {
        return String.format("{\"error\":\"%s\"}", msg);
    }

    private static Map<String,String> queryToMap(String q) throws UnsupportedEncodingException {
        Map<String,String> m = new HashMap<>();
        if (q == null) return m;
        for (String param : q.split("&")) {
            String[] kv = param.split("=");
            if (kv.length>1) m.put(URLDecoder.decode(kv[0],"UTF-8"), URLDecoder.decode(kv[1],"UTF-8"));
        }
        return m;
    }

    /**
     * 📦 Clase Producto con validaciones y conversión JSON manual
     */
    static class Producto {
        private long id;
        private String nombre;
        private double precio;
        private int stock;

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) {
            if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre es obligatorio");
            if (nombre.length() > 50) throw new IllegalArgumentException("Nombre muy largo (máx 50)");
            this.nombre = nombre;
        }

        public double getPrecio() { return precio; }
        public void setPrecio(double precio) {
            if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo");
            this.precio = precio;
        }

        public int getStock() { return stock; }
        public void setStock(int stock) {
            if (stock < 0) throw new IllegalArgumentException("El stock no puede ser negativo");
            this.stock = stock;
        }

        public String toJson() {
            return String.format("{\"id\":%d,\"nombre\":\"%s\",\"precio\":%.2f,\"stock\":%d}",
                                  id, nombre, precio, stock);
        }

        public static Producto fromJson(String json) {
            Map<String,String> map = new HashMap<>();
            json = json.replaceAll("[{}\"]","");
            for (String pair : json.split(",")) {
                String[] kv = pair.split(":");
                map.put(kv[0].trim(), kv[1].trim());
            }
            Producto p = new Producto();
            p.setNombre(map.get("nombre"));
            p.setPrecio(Double.parseDouble(map.getOrDefault("precio","0")));
            p.setStock(Integer.parseInt(map.getOrDefault("stock","0")));
            return p;
        }
    }
}

/*
 * 🧪 PRUEBAS COMPLETAS EN THUNDER CLIENT (VS CODE):
 * ==================================================
 * ✅ BASE URL → http://localhost:8000
 * Asegúrate de que el servidor esté corriendo antes de enviar peticiones.
 * Usa el método adecuado (GET, POST, PUT, DELETE) y el encabezado:
 *     Content-Type: application/json
 *
 * 🔽 PRUEBAS CRUD BÁSICAS:
 * ------------------------
 * 1. ▶️ POST /productos
 *    Crea un nuevo producto. Body (JSON):
 *    {
 *      "nombre": "Sudadera",
 *      "precio": 29.99,
 *      "stock": 10
 *    }
 *    ✔️ Esperado: código 201 y JSON del producto con ID.
 *
 * 2. 📄 GET /productos
 *    Lista todos los productos existentes.
 *    ✔️ Esperado: array de objetos JSON.
 *
 * 3. 🔍 GET /productos/1
 *    Consulta un producto concreto por ID (ej. ID 1).
 *    ✔️ Esperado: objeto JSON del producto.
 *    ❌ Si no existe: error 404.
 *
 * 4. 🔁 PUT /productos/1
 *    Actualiza un producto existente. Body:
 *    {
 *      "nombre": "Sudadera Premium",
 *      "precio": 39.99,
 *      "stock": 20
 *    }
 *    ✔️ Esperado: código 200 y producto actualizado.
 *    ❌ Si el ID no existe: error 404.
 *
 * 5. 🗑 DELETE /productos/1
 *    Elimina un producto existente.
 *    ✔️ Esperado: código 204 (sin contenido).
 *    ❌ Si el ID no existe: error 404.
 *
    * 🔎 PRUEBAS AVANZADAS DE FILTRADO:
    * ----------------------------------
    * 6. 🔍 GET /productos/mascaros?precio=20
    *    Filtra productos con precio >= 20
    *    ✔️ Esperado: array con productos filtrados.
    *    ❌ Si query mal formada: error 400.
 *
 * ⚠️ PRUEBAS DE VALIDACIONES Y ERRORES:
 * --------------------------------------
 * 7. ❌ POST sin campo "nombre"
 *    {
 *      "precio": 19.99,
 *      "stock": 5
 *    }
 *    ❌ Esperado: error 400 con mensaje "El nombre es obligatorio"
 *
 * 8. ❌ POST con precio negativo
 *    {
 *      "nombre": "Mochila",
 *      "precio": -10.0,
 *      "stock": 4
 *    }
 *    ❌ Esperado: error 400 con mensaje "El precio no puede ser negativo"
 *
 * 9. ❌ POST con nombre muy largo (más de 50 caracteres)
 *    {
 *      "nombre": "X" * 60,
 *      "precio": 10.0,
 *      "stock": 2
 *    }
 *    ❌ Esperado: error 400 con mensaje "Nombre muy largo"
 *
 * 🔄 CORS Y FETCH DESDE HTML:
 * ----------------------------
 * 10. Desde un archivo HTML local, crea un botón que haga fetch:
 *     fetch("http://localhost:8000/productos")
 *       .then(res => res.json()).then(console.log)
 *     ✔️ Esperado: la consola muestra la lista de productos sin error CORS
 *
 * ✏️ EJERCICIOS SUGERIDOS (manuales):
 * -----------------------------------
 * 11. Crear varios productos con distintos precios y stocks.
 * 12. Hacer una tabla en HTML que muestre los productos usando fetch.
 * 13. Crear un formulario que permita añadir productos usando POST.
 * 14. Implementar buscador en frontend por ID.
 * 15. Probar simultáneamente varias peticiones para ver concurrencia.
 * 16. Adaptar a cliente móvil con Postman o Insomnia.
 *
 * ✅ Recomendación: Exporta tus pruebas en Thunder Client como colección para reutilizarlas.
 */