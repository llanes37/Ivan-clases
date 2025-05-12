/**
 * 📖 UT3 - API REST sin Spring (100% funcional en un .java)
 * ==========================================================
 * Este ejemplo completo implementa una API REST básica con:
 *  - CRUD de productos (crear, leer, actualizar, borrar)
 *  - Almacenamiento en memoria (HashMap)
 *  - Rutas REST: GET, POST, PUT, DELETE
 *  - Manejo manual de JSON (entrada/salida)
 *
 * ▶️ Ejecuta y prueba con curl o navegador.
 * 🎯 Ejercicios al final.
 */

 import com.sun.net.httpserver.*;
 import java.io.*;
 import java.net.InetSocketAddress;
 import java.nio.charset.StandardCharsets;
 import java.util.*;
 
 public class UT3_ApiRestProductos {
 
     // 🗃️ Repositorio en memoria
     static Map<Long, Producto> productos = new HashMap<>();
     static long contadorId = 1;
 
     public static void main(String[] args) throws IOException {
         // 🌐 Crea servidor en puerto 8000
         HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
 
         // 📍 Define las rutas
         server.createContext("/productos", UT3_ApiRestProductos::handleProductos);
 
         // ▶️ Inicia servidor
         server.setExecutor(null);
         server.start();
         System.out.println("Servidor REST en http://localhost:8000/productos");
     }
 
     // 🔀 Enrutador de métodos HTTP
     public static void handleProductos(HttpExchange exchange) throws IOException {
         String path = exchange.getRequestURI().getPath();
         String method = exchange.getRequestMethod();
         String[] partes = path.split("/");
 
         // /productos       --> partes.length == 2
         // /productos/2     --> partes.length == 3
 
         if (partes.length == 2) {
             if (method.equals("GET")) listar(exchange);
             else if (method.equals("POST")) crear(exchange);
             else metodoNoPermitido(exchange);
         } else if (partes.length == 3) {
             long id = Long.parseLong(partes[2]);
             switch (method) {
                 case "GET" -> obtener(exchange, id);
                 case "PUT" -> actualizar(exchange, id);
                 case "DELETE" -> eliminar(exchange, id);
                 default -> metodoNoPermitido(exchange);
             }
         } else {
             responder(exchange, 404, "Ruta no encontrada");
         }
     }
 
     // 📥 GET /productos
     private static void listar(HttpExchange exchange) throws IOException {
         StringBuilder sb = new StringBuilder("[");
         for (Producto p : productos.values()) {
             sb.append(p.toJson()).append(",");
         }
         if (sb.length() > 1) sb.setLength(sb.length() - 1);
         sb.append("]");
         responder(exchange, 200, sb.toString());
     }
 
     // 📥 GET /productos/{id}
     private static void obtener(HttpExchange exchange, long id) throws IOException {
         Producto p = productos.get(id);
         if (p == null) responder(exchange, 404, "Producto no encontrado");
         else responder(exchange, 200, p.toJson());
     }
 
     // 📤 POST /productos
     private static void crear(HttpExchange exchange) throws IOException {
         String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
         Producto nuevo = Producto.fromJson(body);
         nuevo.setId(contadorId++);
         productos.put(nuevo.getId(), nuevo);
         responder(exchange, 201, nuevo.toJson());
     }
 
     // 🔁 PUT /productos/{id}
     private static void actualizar(HttpExchange exchange, long id) throws IOException {
         Producto existente = productos.get(id);
         if (existente == null) {
             responder(exchange, 404, "Producto no encontrado");
             return;
         }
         String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
         Producto actualizado = Producto.fromJson(body);
         actualizado.setId(id);
         productos.put(id, actualizado);
         responder(exchange, 200, actualizado.toJson());
     }
 
     // ❌ DELETE /productos/{id}
     private static void eliminar(HttpExchange exchange, long id) throws IOException {
         if (productos.remove(id) == null) {
             responder(exchange, 404, "Producto no encontrado");
         } else {
             responder(exchange, 204, "");
         }
     }
 
     // ⚠️ Método no permitido
     private static void metodoNoPermitido(HttpExchange exchange) throws IOException {
         responder(exchange, 405, "Método no permitido");
     }
 
     // 📤 Enviar respuesta JSON
     private static void responder(HttpExchange exchange, int status, String body) throws IOException {
         exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
         byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
         exchange.sendResponseHeaders(status, bytes.length);
         OutputStream os = exchange.getResponseBody();
         os.write(bytes);
         os.close();
     }
 
     // 🧾 Clase Producto con JSON manual
     static class Producto {
         private long id;
         private String nombre;
         private double precio;
 
         public Producto() {}
         public Producto(long id, String nombre, double precio) {
             this.id = id; this.nombre = nombre; this.precio = precio;
         }
         public long getId() { return id; }
         public void setId(long id) { this.id = id; }
         public String getNombre() { return nombre; }
         public void setNombre(String nombre) { this.nombre = nombre; }
         public double getPrecio() { return precio; }
         public void setPrecio(double precio) { this.precio = precio; }
 
         public String toJson() {
             return String.format("{\"id\":%d,\"nombre\":\"%s\",\"precio\":%.2f}", id, nombre, precio);
         }
 
         public static Producto fromJson(String json) {
             Map<String, String> map = new HashMap<>();
             json = json.replaceAll("[{}\"]", "");
             for (String pair : json.split(",")) {
                 String[] kv = pair.split(":");
                 map.put(kv[0].trim(), kv[1].trim());
             }
             Producto p = new Producto();
             p.setNombre(map.get("nombre"));
             p.setPrecio(Double.parseDouble(map.get("precio")));
             return p;
         }
     }
 }
 
 /*
  * 🧪 CÓMO PROBAR:
  * --------------------------------
  * 1. Ejecuta con:
  *    javac ApiRestProductos.java && java ApiRestProductos
  *
  * 2. Abre otra terminal y prueba:
  *
  *  ▶️ Crear producto:
  *    curl -X POST http://localhost:8000/productos -d "{\"nombre\":\"Camiseta\",\"precio\":19.99}" -H "Content-Type: application/json"
  *
  *  ▶️ Listar:
  *    curl http://localhost:8000/productos
  *
  *  ▶️ Obtener:
  *    curl http://localhost:8000/productos/1
  *
  *  ▶️ Actualizar:
  *    curl -X PUT http://localhost:8000/productos/1 -d "{\"nombre\":\"Sudadera\",\"precio\":29.99}" -H "Content-Type: application/json"
  *
  *  ▶️ Eliminar:
  *    curl -X DELETE http://localhost:8000/productos/1
  *
  * 🎯 EJERCICIOS:
  * 1️⃣ Añade un campo nuevo: stock (int)
  * 2️⃣ Valida que el precio no sea negativo.
  * 3️⃣ Devuelve un error 400 si falta un campo.
  * 4️⃣ Agrega GET /productos/mascaros?precio=20 para filtrar.
  * 5️⃣ Guarda los productos en fichero JSON.
  */
 