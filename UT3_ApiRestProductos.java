/**
 * 📖 UT3 - API REST sin Spring (100% funcional en un .java)
 * ==========================================================
 * Esta versión mantiene todas las rutas originales y añade:
 *  - Campo `stock` en Producto.
 *  - Endpoint GET /productos/mascaros?precio=XX para filtrar por precio mínimo.
 *  - Soporte CORS (OPTIONS y cabecera Access-Control-Allow-Origin).
 *
 * ▶️ Ejecuta y prueba con Thunder Client (VSCode).
 * 🎯 Ejercicios al final.
 */

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UT3_ApiRestProductos {

    // 🗃️ Repositorio en memoria
    static Map<Long, Producto> productos = new HashMap<>();
    static long contadorId = 1;

    public static void main(String[] args) throws IOException {
        // 📦 Puerto configurable
        int puerto = 8000;
        String env = System.getenv("PORT");
        if (env != null) try { puerto = Integer.parseInt(env); } catch (NumberFormatException ignored) {}
        else if (args.length > 0) try { puerto = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}

        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(puerto), 0);
        } catch (BindException e) {
            System.err.println("Error: puerto " + puerto + " en uso. Cambia PORT o usa otro args[0].");
            return;
        }

        server.createContext("/productos", UT3_ApiRestProductos::handleProductos);
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor REST iniciado en http://localhost:" + puerto + "/productos");
    }

    // 🔀 Enrutador con CORS y nuevo filtro
    public static void handleProductos(HttpExchange ex) throws IOException {
        // CORS
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        String path = ex.getRequestURI().getPath();
        String method = ex.getRequestMethod();
        String[] partes = path.split("/");

        if (partes.length == 2) {
            switch (method) {
                case "GET": listar(ex); break;
                case "POST": crear(ex); break;
                default: metodoNoPermitido(ex);
            }
        } else if (partes.length == 3) {
            String param = partes[2];
            if ("mascaros".equalsIgnoreCase(param) && "GET".equalsIgnoreCase(method)) {
                filtrarPorPrecio(ex);
                return;
            }
            // ID CRUD
            long id;
            try {
                id = Long.parseLong(param);
            } catch (NumberFormatException e) {
                responder(ex, 400, jsonError("ID inválido o ruta desconocida"));
                return;
            }
            switch (method) {
                case "GET": obtener(ex, id); break;
                case "PUT": actualizar(ex, id); break;
                case "DELETE": eliminar(ex, id); break;
                default: metodoNoPermitido(ex);
            }
        } else {
            responder(ex, 404, jsonError("Ruta no encontrada"));
        }
    }

    // 📥 GET /productos
    private static void listar(HttpExchange ex) throws IOException {
        List<String> arr = new ArrayList<>();
        for (Producto p : productos.values()) arr.add(p.toJson());
        String body = "[" + String.join(",", arr) + "]";
        responder(ex, 200, body);
    }
    // 📥 GET /productos/{id}
    private static void obtener(HttpExchange ex, long id) throws IOException {
        Producto p = productos.get(id);
        if (p == null) responder(ex, 404, jsonError("Producto no encontrado"));
        else responder(ex, 200, p.toJson());
    }
    // 🧹 POST /productos
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
    // 🔁 PUT /productos/{id}
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
    // ❌ DELETE /productos/{id}
    private static void eliminar(HttpExchange ex, long id) throws IOException {
        if (productos.remove(id) == null) responder(ex, 404, jsonError("Producto no encontrado"));
        else responder(ex, 204, "");
    }

    // 🔍 GET /productos/mascaros?precio=XX
    private static void filtrarPorPrecio(HttpExchange ex) throws IOException {
        String q = ex.getRequestURI().getQuery();
        double umbral;
        try {
            Map<String,String> params = queryToMap(q);
            umbral = Double.parseDouble(params.getOrDefault("precio","0"));
        } catch (Exception e) {
            responder(ex, 400, jsonError("Parámetro precio inválido")); return;
        }
        List<String> arr = new ArrayList<>();
        for (Producto p : productos.values()) if (p.getPrecio() >= umbral) arr.add(p.toJson());
        String body = "[" + String.join(",", arr) + "]";
        responder(ex, 200, body);
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

    // 🔧 Parseo de query
    private static Map<String,String> queryToMap(String q) throws UnsupportedEncodingException {
        Map<String,String> m = new HashMap<>();
        if (q == null) return m;
        for (String param : q.split("&")) {
            String[] kv = param.split("=");
            if (kv.length>1) m.put(URLDecoder.decode(kv[0],"UTF-8"), URLDecoder.decode(kv[1],"UTF-8"));
        }
        return m;
    }

    // 🧾 Clase Producto con nuevo campo stock
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
 * ▶️ CÓMO PROBAR EN THUNDER CLIENT:
 * ----------------------------------
 * Base URL: http://localhost:{PORT}
 *
 * 1. POST /productos
 *    Body (JSON):
 *    {
 *      "nombre": "Camiseta",
 *      "precio": 19.99,
 *      "stock": 50
 *    }
 *
 * 2. GET /productos
 * 3. GET /productos/{id}
 * 4. PUT /productos/{id}
 * 5. DELETE /productos/{id}
 * 6. GET /productos/mascaros?precio=20   ← Filtra productos con precio >=20
 *
 * 🎯 EJERCICIOS:
 * 1️⃣ Guarda productos en fichero JSON.
 * 2️⃣ Añade validación de campo length máximo en nombre.
 * 3️⃣ Implementa un endpoint PATCH para stock.
 */