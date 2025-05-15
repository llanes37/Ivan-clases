import java.io.*;                              // 📦 Entrada/Salida estándar de Java
import java.net.*;                             // 🌐 Clases para programación de red (Socket, ServerSocket, etc.)
import java.util.*;                            // 📚 Estructuras de datos como List, Scanner, Collections
import java.util.concurrent.*;                // 🔄 Manejo de hilos (ExecutorService)
import com.sun.net.httpserver.HttpServer;     // 📡 Servidor HTTP embebido propio de Java

/**
 * 📖 TEORÍA UT2: SERVICIOS DE RED Y CONCURRENCIA AVANZADA
 * ======================================================
 * Objetivos:
 *  - Entender comunicación cliente-servidor con TCP, HTTP y UDP.
 *  - Crear servidores concurrentes en Java.
 *  - Practicar I/O de sockets y servidores HTTP embebidos.
 *
 * 🔍 TCP vs UDP:
 *  ▪️ TCP: conexión fiable, ordenada; ideal para chats y APIs.
 *  ▪️ UDP: rápido, sin conexión; ideal para streaming y logs.
 */
public class UT2_ServiciosRed_Extendido {

    /**
     * 🔧 DEMO 1: Servidor TCP concurrente (Echo Server)
     * -----------------------------------------------
     * ✅ ¿QUÉ HACE?
     * - Escucha en el puerto indicado (5000).
     * - Acepta múltiples clientes simultáneamente.
     * - Por cada mensaje recibido, responde con "Echo: <mensaje>".
     *
     * 🧪 ¿CÓMO PROBARLO?
     * 1. Ejecutar esta demo (opción 1 en el menú).
     * 2. Conectar con telnet o netcat: telnet localhost 5000
     * 3. Escribir texto y verificar eco.
     *
     * 🎓 APRENDIZAJE:
     * - Clase ServerSocket y Socket.
     * - Flujo de texto con BufferedReader y PrintWriter.
     * - Concurrencia con hilos (Thread).
     *
     * 🎯 EJERCICIOS:
     * - Cambiar prefijo "Echo:" a otro personalizado.
     * - Limitar número máximo de clientes.
     */
    static class ServidorTCP extends Thread {
        private final int puerto;  // 🔢 Puerto donde escucha el servidor.
        public ServidorTCP(int puerto) { this.puerto = puerto; } // ⚙️ Constructor.
        @Override
        public void run() {
            try (ServerSocket server = new ServerSocket(puerto)) { // 🔒 Abre socket de servidor.
                System.out.println("[ServidorTCP] Escuchando en puerto " + puerto);
                while (true) { // 🔁 Bucle para aceptar clientes.
                    Socket cliente = server.accept(); // 🛎️ Espera conexión.
                    new Thread(() -> manejarCliente(cliente)).start(); // 🧵 Atiende cliente en hilo.
                }
            } catch (IOException e) {
                System.err.println("[ServidorTCP] Error: " + e.getMessage()); // 📛 Error.
            }
        }
        private static void manejarCliente(Socket socket) {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 📥 Lector.
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true) // 📤 Escritor.
            ) {
                String linea;
                while ((linea = in.readLine()) != null) { // 🔄 Lee hasta fin de stream.
                    System.out.println("[ServidorTCP] Recibido: " + linea);
                    out.println("Echo: " + linea); // 🔄 Envía eco.
                }
            } catch (IOException e) {
                System.out.println("[ServidorTCP] Cliente desconectado."); // ⚠️ Cliente terminó.
            }
        }
    }

    /**
     * 🔧 DEMO 2: Cliente TCP de prueba
     * --------------------------------
     * ✅ ¿QUÉ HACE?
     * - Conecta a localhost:5000.
     * - Envía 3 mensajes: "Mensaje 1", "Mensaje 2", "Mensaje 3".
     * - Pausa 1 segundo entre envíos.
     * - Muestra envío y respuesta.
     *
     * 🎓 APRENDIZAJE:
     * - Conexión con Socket.
     * - Uso de PrintWriter y BufferedReader.
     * - Control de flujo con Thread.sleep().
     *
     * 🎯 EJERCICIOS:
     * - Modificar a 10 mensajes.
     * - Leer mensajes desde teclado.
     */
    static class ClienteTCP extends Thread {
        private final int puerto;  // 🔢 Puerto destino.
        public ClienteTCP(int puerto) { this.puerto = puerto; } // ⚙️ Constructor.
        @Override
        public void run() {
            try (
                Socket socket = new Socket("localhost", puerto); // ⚡ Conecta al servidor.
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true); // 📤 Salida.
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())) // 📥 Entrada.
            ) {
                for (int i = 1; i <= 3; i++) { // 🔁 Envía 3 mensajes.
                    String msg = "Mensaje " + i; // 📝 Construye mensaje.
                    System.out.println("[ClienteTCP] Enviando: " + msg); // 🖨 Imprime.
                    out.println(msg); // ✉️ Envía.
                    String resp = in.readLine(); // 📥 Espera respuesta.
                    System.out.println("[ClienteTCP] Recibe: " + resp); // 🖨 Imprime.
                    Thread.sleep(1000); // 💤 Pausa 1s.
                }
            } catch (IOException | InterruptedException e) {
                System.err.println("[ClienteTCP] Error: " + e.getMessage()); // 📛 Error.
            }
        }
    }

    /**
     * 🔧 DEMO 3: Servidor HTTP embebido (Echo API)
     * --------------------------------------------
     * ✅ ¿QUÉ HACE?
     * - Levanta HTTP en puerto 8000.
     * - Endpoint /echo devuelve los parámetros de la URL.
     *
     * 🧪 PRUEBAS:
     * curl "http://localhost:8000/echo?msg=test"
     * Navegador: /echo?msg=hola
     *
     * 🎓 APRENDIZAJE:
     * - Uso de HttpServer sin frameworks.
     * - Manejo de query string.
     *
     * 🎯 EJERCICIOS:
     * - Añadir endpoint POST JSON.
     * - Validar ausencia de parámetros (400).
     */
    static class ServidorHttpSimple extends Thread {
        @Override
        public void run() {
            try {
                HttpServer http = HttpServer.create(new InetSocketAddress(8000), 0); // 🔌 Abre HTTP.
                http.createContext("/echo", exchange -> { // 🛠 Define ruta.
                    String query = exchange.getRequestURI().getQuery(); // 🧾 Lee query.
                    String resp = "Echo HTTP: " + (query == null ? "" : query); // 🪞 Prepara.
                    exchange.sendResponseHeaders(200, resp.getBytes().length); // ✅ Responde 200.
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(resp.getBytes()); // 📨 Envía cuerpo.
                    }
                });
                http.setExecutor(Executors.newFixedThreadPool(4)); // ♻️ Pool.
                http.start(); // 🚀 Arranca.
                System.out.println("[HTTP] Servidor iniciado en http://localhost:8000/echo?msg=hola");
            } catch (IOException e) {
                System.err.println("[HTTP] Error: " + e.getMessage()); // 📛 Error.
            }
        }
    }

    /**
     * 🔧 DEMO 4: Servidor de Chat TCP (Broadcast)
     * -------------------------------------------
     * ✅ ¿QUÉ HACE?
     * - Escucha en puerto.
     * - Cada mensaje de un cliente se reenvía a todos.
     *
     * 🎓 APRENDIZAJE:
     * - Lista sincronizada.
     * - Concurrencia en broadcast.
     *
     * 🎯 EJERCICIOS:
     * - Ignorar mensajes vacíos.
     * - Agregar timestamp a mensajes.
     */
    static class ChatServidor extends Thread {
        private final int puerto;
        private final List<PrintWriter> clientes = Collections.synchronizedList(new ArrayList<>());
        public ChatServidor(int puerto) { this.puerto = puerto; }
        @Override
        public void run() {
            try (ServerSocket server = new ServerSocket(puerto)) {
                System.out.println("[ChatServidor] Escuchando puerto " + puerto);
                while (true) {
                    Socket s = server.accept(); // 🛎 Cliente llega.
                    new Thread(() -> manejarCliente(s)).start(); // 🧵 Atiende.
                }
            } catch (IOException e) {
                System.err.println("[ChatServidor] Error: " + e.getMessage());
            }
        }
        private void manejarCliente(Socket socket) {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                clientes.add(out); // ➕ Agrega cliente.
                String msg;
                while ((msg = in.readLine()) != null) {
                    synchronized (clientes) {
                        for (PrintWriter pw : clientes) pw.println(msg); // 🔁 Broadcast.
                    }
                }
            } catch (IOException e) {
                System.out.println("[ChatServidor] Cliente desconectado.");
            }
        }
    }

    /**
     * 🔧 DEMO 5: Cliente de Chat (Consola)
     * ------------------------------------
     * ✅ ¿QUÉ HACE?
     * - Conecta a ChatServidor.
     * - Lee teclado y envía mensajes.
     * - Muestra mensajes de otros clientes.
     *
     * 🎓 APRENDIZAJE:
     * - Cliente TCP con I/O concurrente.
     * - Uso de hilos para leer socket y consola.
     *
     * 🎯 EJERCICIOS:
     * - Evitar mensajes vacíos.
     * - Mostrar solo mensajes de otros.
     * - Añadir fecha/hora a mensajes.
     */
    static class ChatCliente extends Thread {
        private final String nombre;
        public ChatCliente(String nombre) { this.nombre = nombre; }
        @Override
        public void run() {
            try (
                Socket s = new Socket("localhost", 9000);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                BufferedReader term = new BufferedReader(new InputStreamReader(System.in))
            ) {
                out.println(nombre + " se unió"); // 🚪 Anuncio.
                // 🧵 Hilo para leer del socket.
                new Thread(() -> {
                    try {
                        String line;
                        while ((line = in.readLine()) != null) {
                            System.out.println(line); // 🖨 Muestra mensaje.
                        }
                    } catch (IOException ignored) {}
                }).start();
                String input;
                while ((input = term.readLine()) != null) {
                    if (!input.isBlank()) out.println(nombre + ": " + input); // ✉️ Envia si no vacío.
                }
            } catch (IOException e) {
                System.err.println("[ChatCliente] Error: " + e.getMessage());
            }
        }
    }

    /**
     * 🧪 MENÚ PRINCIPAL: Ejecuta demos según elección
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;
        do {
            System.out.println("\n=== MENÚ UT2: Servicios de Red ===");
            System.out.println("1) Servidor TCP (Echo Server)");
            System.out.println("2) Cliente TCP de prueba");
            System.out.println("3) Servidor HTTP simple (Echo API)");
            System.out.println("4) ChatServidor (broadcast)");
            System.out.println("5) ChatCliente (consola)");
            System.out.println("6) Salir");
            System.out.print("Elige una opción: ");
            opcion = sc.nextInt();
            switch (opcion) {
                case 1 -> new ServidorTCP(5000).start();
                case 2 -> new ClienteTCP(5000).start();
                case 3 -> new ServidorHttpSimple().start();
                case 4 -> new ChatServidor(9000).start();
                case 5 -> {
                    sc.nextLine();
                    System.out.print("Nombre de usuario: ");
                    String nombre = sc.nextLine();
                    new ChatCliente(nombre).start();
                }
                case 6 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción no válida");
            }
        } while (opcion != 6);
        sc.close();
        System.out.println("Programa terminado");
    }
}
