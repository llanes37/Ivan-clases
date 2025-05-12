# 📘 RESUMEN COMPLETO PARA EL EXAMEN (UT1 a UT6)

Este documento recopila toda la teoría y los fragmentos de código esenciales de todas las unidades del curso, para poder estudiar y escribir a mano si es necesario. Está organizado por unidad, con explicaciones teóricas extensas, definiciones clave, ejemplos de código comentado y estructura de los principales bloques funcionales en Java.

---

## ✅ UT1 - Hilos básicos en Java

### 📖 Teoría completa

* **Hilo (Thread)**: Es una unidad de ejecución dentro de un proceso. Java permite crear múltiples hilos para realizar tareas en paralelo.
* **Multitarea**: Capacidad de ejecutar más de una tarea a la vez. Java implementa esto con hilos.
* **run()**: Método que define lo que hará el hilo. No se llama directamente, se invoca automáticamente al llamar `start()`.
* **start()**: Método que inicia el hilo y ejecuta su `run()` en paralelo.
* **sleep(milisegundos)**: Suspende el hilo por un tiempo determinado.
* **join()**: Hace que el hilo actual espere hasta que otro hilo termine.
* **interrupted() e interrupt()**: Permiten interrumpir un hilo que esté esperando.
* **setPriority()**: Cambia la prioridad de un hilo (de 1 a 10). Influye, pero no garantiza orden.
* **Thread.currentThread()**: Devuelve una referencia al hilo que se está ejecutando actualmente.

### 🧩 Código esencial:

```java
public class MiHilo extends Thread {
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println("Paso " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Interrumpido");
            }
        }
    }
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MiHilo h = new MiHilo();
        h.start();
        h.join();
        System.out.println("Fin del programa");
    }
}
```

---

## ✅ UT2 - Servicios en red y sockets

### 📖 Teoría completa

* **Redes**: Comunicación entre computadoras. En Java se puede implementar con `Socket` y `ServerSocket`.
* **Socket**: Punto de conexión entre cliente y servidor.
* **ServerSocket**: Se usa para aceptar conexiones de clientes.
* **InputStreamReader + BufferedReader**: Se usa para leer datos del cliente.
* **PrintWriter**: Se usa para enviar respuestas al cliente.
* **Hilos por cliente**: Para atender varios clientes al mismo tiempo se usa un hilo por cada uno.

### 🧩 Código servidor TCP multicliente:

```java
ServerSocket server = new ServerSocket(5000);
while (true) {
    Socket cliente = server.accept();
    new Thread(() -> manejar(cliente)).start();
}

public static void manejar(Socket s) {
    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
    String msg = in.readLine();
    out.println("Echo: " + msg);
}
```

---

## ✅ UT3 - API REST sin Spring

### 📖 Teoría completa

* **REST (Representational State Transfer)**: Arquitectura web basada en recursos accesibles mediante URLs.
* **HTTPServer (com.sun.net.httpserver)**: Clase incluida en Java para crear un servidor HTTP básico.
* **HttpExchange**: Representa una petición y permite construir una respuesta.
* **CRUD**:

  * **GET**: Leer datos
  * **POST**: Crear nuevo
  * **PUT**: Actualizar existente
  * **DELETE**: Eliminar recurso
* **JSON manual**: Conversión básica con String.format y split.

### 🧩 Estructura básica:

```java
HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
server.createContext("/alumnos", this::gestionar);
server.start();

private void gestionar(HttpExchange ex) {
    String metodo = ex.getRequestMethod();
    if (metodo.equals("GET")) listar();
    else if (metodo.equals("POST")) crear();
    else responder(ex, 405, "Método no permitido");
}
```

---

## ✅ UT4 - Simulación de Spring Boot (manual con HttpServer)

### 📖 Teoría completa

* Se simula el comportamiento de `@RestController` y `@RequestMapping` usando condiciones en Java puro.
* Uso de rutas como `/api/alumnos` y operaciones REST.
* JSON se manipula sin librerías externas.
* Manualmente se analiza `ex.getRequestURI().getPath()` y `ex.getRequestMethod()` para decidir qué hacer.

### 🧩 Fragmento de rutas:

```java
if (ruta.equals("/alumnos") && metodo.equals("GET")) listar(ex);
else if (ruta.equals("/alumnos") && metodo.equals("POST")) crear(ex);
```

---

## ✅ UT5 - API REST con archivo JSON (persistencia)

### 📖 Teoría completa

* **Persistencia**: Guardar datos para que no se pierdan al cerrar el programa.
* **Files.readString / writeString**: Leer y escribir texto en archivos.
* **JSON simulado**: Construcción manual de cadenas tipo \[{...},{...}].
* **Carga inicial**: Se lee el archivo al iniciar el programa.
* **Actualización**: Cada vez que se crea/edita/borrar un alumno, se reescribe el archivo.

### 🧩 Guardar y cargar:

```java
// Guardar
Files.writeString(Path.of("alumnos.json"), json, StandardCharsets.UTF_8);

// Cargar
String json = Files.readString(Path.of("alumnos.json"));
```

---

## ✅ UT6 - Concurrencia avanzada y servidores multicliente

### 📖 Teoría completa

* **ExecutorService**: Gestión eficiente de múltiples hilos.
* **Runnable**: Tareas que no devuelven resultado.
* **Callable y Future**: Tareas que devuelven resultado y pueden esperar con `.get()`.
* **ScheduledExecutorService**: Tareas programadas periódicamente.
* **newFixedThreadPool(n)**: Pool con `n` hilos fijos.
* **newCachedThreadPool()**: Pool con hilos que crecen dinámicamente.
* **shutdown()**: Finaliza el pool de forma ordenada.
* **scheduleAtFixedRate()**: Ejecuta una tarea cada X tiempo.
* **Servidor TCP concurrente**: Un hilo por cliente para atención simultánea.

### 🧩 Código de todas las demos:

**Pool de hilos (Runnable):**

```java
ExecutorService pool = Executors.newFixedThreadPool(3);
for (int i = 1; i <= 5; i++) {
    final int t = i;
    pool.submit(() -> {
        Thread.sleep(1000);
        System.out.println("Tarea " + t + " finalizada");
    });
}
pool.shutdown();
```

**Callable + Future:**

```java
Callable<String> tarea = () -> {
    Thread.sleep(1500);
    return "Resultado listo";
};
Future<String> res = pool.submit(tarea);
System.out.println(res.get());
```

**Tarea programada cada 2s:**

```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
scheduler.scheduleAtFixedRate(() -> {
    System.out.println("Tarea ejecutada: " + new Date());
}, 0, 2, TimeUnit.SECONDS);
```

**Servidor TCP multicliente:**

```java
ServerSocket server = new ServerSocket(5000);
while (true) {
    Socket cliente = server.accept();
    new Thread(() -> atender(cliente)).start();
}
```

---

## 📂 Conclusión

Este resumen abarca todos los aspectos técnicos y prácticos desde UT1 hasta UT6. Incluye la teoría que puedes escribir a mano y fragmentos de código funcionales que se pueden copiar tal cual para practicar y repasar. Es ideal para repasar en papel y enfrentarse con seguridad al examen final.
