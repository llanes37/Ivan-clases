import java.io.*;                              // 📦 Para I/O de sockets
import java.net.*;                             // 🌐 Para clases de red
import java.util.*;                            // 📚 Colecciones y utilidades
import java.util.concurrent.*;                // 🔄 Pools y concurrencia
import com.sun.net.httpserver.HttpServer;     // 📡 Servidor HTTP embebido

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
public class UT2_ServiciosRed_Extendido {

    /**
     * 🔧 DEMO 1: Servidor TCP concurrente (Echo Server)
     *    • Tipo de servidor: TCP concurrente
     *    • Descripción: atiende conexiones en puerto definido y devuelve eco de cada línea recibida.
     *    • Pruebas: se puede probar con cliente Telnet o Netcat:
     *      - telnet localhost 5000
     *      - nc localhost 5000
     *      Luego enviar texto y ver la respuesta "Echo: <texto>".
     */
    static class ServidorTCP extends Thread {
        private final int puerto;
        public ServidorTCP(int puerto) {
            this.puerto = puerto;
        }
        @Override
        public void run() {
            try (ServerSocket server = new ServerSocket(puerto)) {
                System.out.println("[ServidorTCP] Escuchando en puerto " + puerto);
                while (true) {
                    Socket cliente = server.accept();
                    new Thread(() -> manejarCliente(cliente)).start();
                }
            } catch (IOException e) {
                System.out.println("[ServidorTCP] Error: " + e.getMessage());
            }
        }
        private static void manejarCliente(Socket socket) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                String linea;
                while ((linea = in.readLine()) != null) {
                    System.out.println("[ServidorTCP] Recibido: " + linea);
                    out.println("Echo: " + linea);
                }
            } catch (IOException e) {
                System.out.println("[ServidorTCP] Cliente desconectado.");
            }
        }
    }

    /**
     * 🔧 DEMO 2: Cliente TCP de prueba
     *    • Tipo de cliente: TCP simple
     *    • Descripción: conecta al servidor TCP en el puerto indicado y envía tres mensajes de prueba.
     *    • Pruebas: ejecutar este hilo tras iniciar ServidorTCP para ver intercambio de mensajes.
     */
    static class ClienteTCP extends Thread {
        private final int puerto;
        public ClienteTCP(int puerto) {
            this.puerto = puerto;
        }
        @Override
        public void run() {
            try (Socket socket = new Socket("localhost", puerto);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                for (int i = 1; i <= 3; i++) {
                    String msg = "Mensaje " + i;
                    System.out.println("[ClienteTCP] Enviando: " + msg);
                    out.println(msg);
                    String resp = in.readLine();
                    System.out.println("[ClienteTCP] Recibe: " + resp);
                    Thread.sleep(1000);
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("[ClienteTCP] Error: " + e.getMessage());
            }
        }
    }

    /**
     * 🔧 DEMO 3: Servidor HTTP simple (Echo API)
     *    • Tipo de servidor: HTTP embebido
     *    • Descripción: expone un endpoint /echo que devuelve los parámetros de la query.
     *    • Pruebas: usar navegador, curl o Thunder Client:
     *      - http://localhost:8000/echo?msg=hola
     *      - curl "http://localhost:8000/echo?msg=hola"
     */
    static class ServidorHttpSimple extends Thread {
        @Override
        public void run() {
            try {
                HttpServer http = HttpServer.create(new InetSocketAddress(8000), 0);
                http.createContext("/echo", exchange -> {
                    String query = exchange.getRequestURI().getQuery();
                    String resp = "Echo HTTP: " + (query == null ? "" : query);
                    exchange.sendResponseHeaders(200, resp.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(resp.getBytes());
                    }
                });
                http.setExecutor(Executors.newFixedThreadPool(4));
                http.start();
                System.out.println("[HTTP] Servidor HTTP iniciado: http://localhost:8000/echo?msg=hola");
            } catch (IOException e) {
                System.out.println("[HTTP] Error: " + e.getMessage());
            }
        }
    }

    /**
     * 🔧 DEMO 4: Chat en tiempo real con broadcast
     *    • Tipo de servidor: TCP chat broadcast
     *    • Descripción: acepta múltiples clientes y reenvía cada mensaje a todos.
     *    • Pruebas: conectar varios ChatCliente o usar telnet/netcat a localhost:9000.
     */
    static class ChatServidor extends Thread {
        private final int puerto;
        private final List<PrintWriter> clientes = Collections.synchronizedList(new ArrayList<>());

        public ChatServidor(int puerto) {
            this.puerto = puerto;
        }
        @Override
        public void run() {
            try (ServerSocket server = new ServerSocket(puerto)) {
                System.out.println("[ChatServidor] Escuchando en puerto " + puerto);
                while (true) {
                    Socket s = server.accept();
                    new Thread(() -> manejarCliente(s)).start();
                }
            } catch (IOException e) {
                System.out.println("[ChatServidor] Error: " + e.getMessage());
            }
        }
        private void manejarCliente(Socket socket) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                clientes.add(out);
                String msg;
                while ((msg = in.readLine()) != null) {
                    synchronized (clientes) {
                        for (PrintWriter pw : clientes) {
                            pw.println(msg);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("[ChatServidor] Cliente desconectado.");
            }
        }
    }

    /**
     * 🧪 MAIN: Menú interactivo para elegir demo
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
                case 1:
                    System.out.println("Iniciando ServidorTCP...");
                    new ServidorTCP(5000).start(); break;
                case 2:
                    System.out.println("Iniciando ClienteTCP...");
                    new ClienteTCP(5000).start(); break;
                case 3:
                    System.out.println("Iniciando ServidorHttpSimple...");
                    new ServidorHttpSimple().start(); break;
                case 4:
                    System.out.println("Iniciando ChatServidor...");
                    new ChatServidor(9000).start(); break;
                case 5:
                    System.out.println("Iniciando ChatCliente...");
                    sc.nextLine();
                    System.out.print("Nombre de usuario: ");
                    String nombre = sc.nextLine();
                    new ChatCliente(nombre).start(); break;
                case 6:
                    System.out.println("Saliendo..."); break;
                default:
                    System.out.println("Opción no válida");
            }
        } while (opcion != 6);
        sc.close();
        System.out.println("Programa terminado");
    }

    /**
     * 🔧 CLIENTE OPCIONAL para chat (consola interactiva)
     */
    static class ChatCliente extends Thread {
        private final String nombre;
        public ChatCliente(String nombre) {
            this.nombre = nombre;
        }
        @Override
        public void run() {
            try (Socket s = new Socket("localhost", 9000);
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                 BufferedReader term = new BufferedReader(new InputStreamReader(System.in))) {
                out.println(nombre + " se unió");
                new Thread(() -> {
                    try {
                        String line;
                        while ((line = in.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException ignored) {}
                }).start();
                String input;
                while ((input = term.readLine()) != null) {
                    out.println(nombre + ": " + input);
                }
            } catch (IOException e) {
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
