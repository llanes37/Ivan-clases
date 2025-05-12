# UT2 - Servicios de Red y Concurrencia Avanzada (PSP)

**Objetivos de la unidad:**

* Comprender la diferencia entre **procesos** e **hilos**.
* Aprender a comunicar procesos mediante **sockets** (TCP/UDP).
* Explorar protocolos de red básicos (Telnet, FTP, HTTP).
* Diseñar y desarrollar un **servicio REST** sin y con Spring Boot.
* Implementar un **chat en tiempo real** usando sockets y multihilo.
* Gestionar **objetos compartidos** y garantizar la **seguridad de la concurrencia**.

---

## 1. Procesos vs Hilos

* **Proceso:** instancia de un programa en ejecución, aislado en memoria.
* **Hilo:** unidad ligera dentro de un proceso, comparte espacio de memoria.

**Ejercicio 1:** Crea dos procesos simples desde consola que se comuniquen usando tuberías (pipes) o archivos temporales.

---

## 2. Programación con Sockets (TCP)

```java
// Esqueleto de servidor TCP que atiende múltiples clientes
public class ServidorTCP {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(5000);
        System.out.println("Servidor escuchando en puerto 5000...");
        while (true) {
            Socket cliente = server.accept();
            new Thread(() -> manejarCliente(cliente)).start();
        }
    }
    private static void manejarCliente(Socket s) { /* ... */ }
}
```

**Ejercicio 2:** Implementa `manejarCliente` para leer líneas desde el cliente y responder con un eco.

---

## 3. Protocolos de Red Básicos

* **Telnet:** cliente/servidor de texto, prueba manual de servicios.
* **FTP:** transferencia de archivos en modo activo/pasivo.
* **HTTP:** base de la Web, peticiones GET/POST.

**Ejercicio 3:** Conéctate por Telnet a `time.google.com` en el puerto 13 para obtener la hora.

---

## 4. API REST

### 4.1 Sin Spring Boot (usando `com.sun.net.httpserver`)

```java
HttpServer http = HttpServer.create(new InetSocketAddress(8080), 0);
http.createContext("/api/mensaje", exchange -> {
    String respuesta = "Hola REST sin Spring";
    exchange.sendResponseHeaders(200, respuesta.length());
    try (OutputStream os = exchange.getResponseBody()) {
        os.write(respuesta.getBytes());
    }
});
http.start();
```

**Ejercicio 4:** Añade rutas para GET, POST y maneja JSON manualmente.

### 4.2 Con Spring Boot

```java
@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/saludo")
    public String saludo() {
        return "Hola Spring Boot REST";
    }
}
```

**Ejercicio 5:** Crea un endpoint `/api/users` que devuelva una lista de objetos `User`.

---

## 5. Chat en Tiempo Real

* Usa **TCP** o **WebSockets**.
* Multihilo para atender cada conexión.

```java
// Cliente WebSocket con Java
WebSocketClient client = new WebSocketClient(...);
client.connect();
client.send("Mensaje de prueba");
```

**Ejercicio 6:** Implementa un chat básico donde varios clientes se comuniquen a través del servidor.

---

## 6. Objetos Compartidos y Concurrencia

* Uso de **bloqueos** (`synchronized`, `ReentrantLock`).
* **Colecciones concurrentes** (`ConcurrentHashMap`, `CopyOnWriteArrayList`).

**Ejercicio 7:** Crea un `Map<String, List<String>>` compartido que almacene mensajes de chat por sala, asegurando la seguridad de hilos.

---

## Ejercicio Final (Guiado)

1. Desarrolla un **servidor de chat** usando Spring Boot y WebSockets.
2. En el cliente HTML/JS, muestra la lista de usuarios conectados y los mensajes en tiempo real.
3. Asegura que los mensajes se persisten en memoria con una estructura concurrente adecuada.
4. Documenta cómo arrancar el servidor y probar con múltiples navegadores.

---

*¡Listo para empezar UT2!* Prueba cada ejercicio, compártelo en clase y resuelve las dudas en equipo.
