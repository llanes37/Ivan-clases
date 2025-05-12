/**
 * 📖 TEORÍA UT2: SERVICIOS DE RED Y CONCURRENCIA AVANZADA
 * ======================================================
 * En la práctica profesional, es habitual:
 *  • Atender múltiples clientes en un servidor web o de chat.
 *  • Exponer APIs REST para integración de sistemas.
 *  • Gestionar conexiones TCP/IP y HTTP.
 *  • Asegurar la coherencia de datos compartidos entre hilos.
 *
 * Conceptos clave:
 *  1️⃣ Socket: punto de comunicación entre cliente y servidor.
 *  2️⃣ ServerSocket/Socket: clases base de Java para TCP.
 *  3️⃣ HTTP Server: servidor web embebido (com.sun.net.httpserver).
 *  4️⃣ Concurrencia: atender conexiones en hilos o pools.
 *  5️⃣ Broadcast: enviar datos a múltiples clientes (chat).
 *  6️⃣ Sincronización: evitar race conditions con synchronized o colecciones concurrentes.
 *  7️⃣ ExecutorService: alternativa eficiente a crear hilos manualmente.
 *  8️⃣ UDP: comunicación sin conexión, útil para logs y streaming.
 *
 * La teoría se complementa con las demos y ejercicios abajo.
 */

 import java.io.*;                              // 📦 Para I/O de sockets
 import java.net.*;                             // 🌐 Para clases de red
 import java.util.*;                            // 📚 Colecciones y utilidades
 import java.util.concurrent.*;                // 🔄 Pools y concurrencia
 import com.sun.net.httpserver.HttpServer;     // 📡 Servidor HTTP embebido
 
 public class UT2_ServiciosRed_Extendido {
 
     /**
      * 🔧 DEMO 1: Servidor TCP concurrente (Echo Server)
      */
     static class ServidorTCP extends Thread {
         private final int puerto;               // 🏷️ Puerto de escucha
         public ServidorTCP(int puerto) {       // 🔨 Constructor con puerto
             this.puerto = puerto;             // 📥 Asigna puerto a la instancia
         }
         @Override
         public void run() {                   // ▶️ Método que arranca al llamar start()
             try (ServerSocket server = new ServerSocket(puerto)) {  // 🔒 Abre ServerSocket
                 System.out.println("[ServidorTCP] Escuchando en puerto " + puerto);
                 while (true) {              // 🔄 Bucle infinito para aceptar clientes
                     Socket cliente = server.accept();  // 🛎️ Espera y acepta conexión
                     new Thread(() -> manejarCliente(cliente)).start();  // 🚀 Arranca hilo para cliente
                 }
             } catch (IOException e) {         // 📛 Captura errores de E/S
                 System.out.println("[ServidorTCP] Error: " + e.getMessage());
             }
         }
         private static void manejarCliente(Socket socket) {  // 🔧 Manejo de un cliente
             try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                  PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                 String linea;                   // 📝 Línea recibida
                 while ((linea = in.readLine()) != null) {  // 🔄 Lee hasta fin de stream
                     System.out.println("[ServidorTCP] Recibido: " + linea);
                     out.println("Echo: " + linea);  // 🔄 Devuelve eco al cliente
                 }
             } catch (IOException e) {         // 📛 Cliente desconectado o error
                 System.out.println("[ServidorTCP] Cliente desconectado.");
             }
         }
     }
 
     /**
      * 🔧 DEMO 2: Cliente TCP de prueba
      */
     static class ClienteTCP extends Thread {
         private final int puerto;               // 🏷️ Puerto al que conectar
         public ClienteTCP(int puerto) {         // 🔨 Constructor con puerto
             this.puerto = puerto;               // 📥 Asigna puerto a la instancia
         }
         @Override
         public void run() {                     // ▶️ Ejecuta al llamar start()
             try (Socket socket = new Socket("localhost", puerto);  // ⚡ Conecta a servidor
                  PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                  BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                 for (int i = 1; i <= 3; i++) {   // 🔄 Bucles de 3 mensajes
                     String msg = "Mensaje " + i;  // 💬 Texto a enviar
                     System.out.println("[ClienteTCP] Enviando: " + msg);
                     out.println(msg);            // 🚀 Envía al servidor
                     String resp = in.readLine();  // 📥 Lee respuesta
                     System.out.println("[ClienteTCP] Recibe: " + resp);
                     Thread.sleep(1000);          // 💤 Pausa 1s entre mensajes
                 }
             } catch (IOException | InterruptedException e) {  // 📛 Errores de red o interrupción
                 System.out.println("[ClienteTCP] Error: " + e.getMessage());
             }
         }
     }
 
     /**
      * 🔧 DEMO 3: Servidor HTTP simple (Echo API)
      */
     static class ServidorHttpSimple extends Thread {
         @Override
         public void run() {                       // ▶️ Arranca con start()
             try {
                 HttpServer http = HttpServer.create(new InetSocketAddress(8000), 0);  // 🌐 Crea servidor en 8000
                 http.createContext("/echo", exchange -> {  // 📍 Contexto /echo
                     String query = exchange.getRequestURI().getQuery();  // 📝 Parámetros GET
                     String resp = "Echo HTTP: " + (query == null ? "" : query);
                     exchange.sendResponseHeaders(200, resp.length());  // 📤 Código 200 OK
                     try (OutputStream os = exchange.getResponseBody()) {
                         os.write(resp.getBytes());     // ✏️ Escribe respuesta
                     }
                 });
                 http.setExecutor(Executors.newFixedThreadPool(4));  // 🔄 Pool de 4 hilos
                 http.start();                          // 🚀 Inicia el servidor HTTP
                 System.out.println("[HTTP] http://localhost:8000/echo?msg=hola");
             } catch (IOException e) {                // 📛 Captura errores
                 System.out.println("[HTTP] Error: " + e.getMessage());
             }
         }
     }
 
     /**
      * 🔧 DEMO 4: Chat en tiempo real con broadcast
      */
     static class ChatServidor extends Thread {
         private final int puerto;               // 🏷️ Puerto del chat
         private final List<PrintWriter> clientes = Collections.synchronizedList(new ArrayList<>());
 
         public ChatServidor(int puerto) {       // 🔨 Constructor con puerto
             this.puerto = puerto;               // 📥 Asigna puerto
         }
         @Override
         public void run() {                     // ▶️ Ejecuta con start()
             try (ServerSocket server = new ServerSocket(puerto)) {  // 🌐 Crea socket de servidor
                 System.out.println("[ChatServidor] Escuchando en puerto " + puerto);
                 while (true) {                  // 🔄 Acepta clientes en bucle
                     Socket s = server.accept();  // 🛎️ Cliente se conecta
                     new Thread(() -> manejarCliente(s)).start();  // 🚀 Nueva hebra por cliente
                 }
             } catch (IOException e) {           // 📛 Error de E/S
                 System.out.println("[ChatServidor] Error: " + e.getMessage());
             }
         }
         private void manejarCliente(Socket socket) {  // 🔧 Manejo de un cliente
             try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                  PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                 clientes.add(out);                // ➕ Añade nuevo cliente a la lista
                 String msg;                       // 📝 Mensaje entrante
                 while ((msg = in.readLine()) != null) {  // 🔄 Lee hasta desconexión
                     synchronized (clientes) {      // 🔒 Sincroniza lista
                         for (PrintWriter pw : clientes) {
                             pw.println(msg);      // 🔄 Envía mensaje a todos
                         }
                     }
                 }
             } catch (IOException e) {             // 📛 Cliente desconectado
                 System.out.println("[ChatServidor] Cliente desconectado.");
             }
         }
     }
 
     /**
      * 🧪 MAIN: Menú interactivo para elegir demo
      */
     public static void main(String[] args) {
         Scanner sc = new Scanner(System.in);      // 🔍 Scanner para lectura de consola
         int opcion;
         do {
             // 📜 Muestra el menú
             System.out.println("\n=== MENÚ UT2: Servicios de Red ===");
             System.out.println("1) Servidor TCP (Echo Server)");
             System.out.println("2) Cliente TCP de prueba");
             System.out.println("3) Servidor HTTP simple (Echo API)");
             System.out.println("4) ChatServidor (broadcast)");
             System.out.println("5) ChatCliente (consola)");
             System.out.println("6) Salir");
             System.out.print("Elige una opción: ");
             opcion = sc.nextInt();               // 📥 Lee elección
 
             switch (opcion) {
                 case 1:
                     System.out.println("Iniciando ServidorTCP...");
                     new ServidorTCP(5000).start(); break;    // 🚀 Demo 1
                 case 2:
                     System.out.println("Iniciando ClienteTCP...");
                     new ClienteTCP(5000).start(); break;    // 🚀 Demo 2
                 case 3:
                     System.out.println("Iniciando ServidorHttpSimple...");
                     new ServidorHttpSimple().start(); break; // 🚀 Demo 3
                 case 4:
                     System.out.println("Iniciando ChatServidor...");
                     new ChatServidor(9000).start(); break;   // 🚀 Demo 4
                 case 5:
                     System.out.println("Iniciando ChatCliente...");
                     sc.nextLine();                        // 🧹 Limpia buffer
                     System.out.print("Nombre de usuario: ");
                     String nombre = sc.nextLine();
                     new ChatCliente(nombre).start(); break; // 🚀 Cliente chat
                 case 6:
                     System.out.println("Saliendo..."); break;
                 default:
                     System.out.println("Opción no válida");
             }
         } while (opcion != 6);
         sc.close();                               // 🔒 Cierra Scanner
         System.out.println("Programa terminado");
     }
 
     /**
      * 🔧 CLIENTE OPCIONAL para chat (consola interactiva)
      */
     static class ChatCliente extends Thread {
         private final String nombre;            // 🏷️ Nombre de usuario
         public ChatCliente(String nombre) {     // 🔨 Constructor con nombre
             this.nombre = nombre;               // 📥 Asigna nombre
         }
         @Override
         public void run() {                     // ▶️ Ejecuta con start()
             try (Socket s = new Socket("localhost", 9000);  // ⚡ Conecta al chat
                  BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                  PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                  BufferedReader term = new BufferedReader(new InputStreamReader(System.in))) {
                 out.println(nombre + " se unió");  // 🚪 Anuncio de unión
                 // 🧵 Hilo para imprimir mensajes recibidos
                 new Thread(() -> {
                     try {
                         String line;
                         while ((line = in.readLine()) != null) {
                             System.out.println(line);  // 🖨️ Muestra mensaje
                         }
                     } catch (IOException ignored) {}
                 }).start();
                 String input;
                 while ((input = term.readLine()) != null) {  // 🔄 Lee del teclado
                     out.println(nombre + ": " + input);    // 🚀 Envía al servidor
                 }
             } catch (IOException e) {              // 📛 Error de E/S
                 System.out.println("[ChatCliente] Error: " + e.getMessage());
             }
         }
     }
 
     /*
      * 🎯 EJERCICIOS UT2 (pequeñas tareas para reforzar):
      * --------------------------------------------------
      * 1️⃣ Cambia los puertos por variables de entorno y prueba.
      * 2️⃣ Ajusta el número de mensajes enviados en ClienteTCP.
      * 3️⃣ Extiende HTTP demo con POST y parseo JSON.
      * 4️⃣ Valida mensajes en ChatServidor (no vacíos).
      * 5️⃣ Añade elección de puerto en el menú para cada demo.
      * 6️⃣ Implementa ExecutorService en lugar de crear hilos manuales.
      * 7️⃣ Crea un cliente HTTP Java que consuma /echo.
      * 8️⃣ Añade un demo UDP echo en la opción 7.
      * 🔟 FINAL (sencillo): hilo que imprima "UT2 activo" cada 2s, 5 veces.
      */
 }