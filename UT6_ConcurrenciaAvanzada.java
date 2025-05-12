/**
 * 📖 UT6 - Concurrencia avanzada y servicios multicliente (Java puro)
 * ====================================================================
 *
 * 📘 ¿Qué se estudia en UT6?
 * ----------------------------------------------------
 * Esta unidad se centra en el control avanzado de hilos y tareas concurrentes.
 * Aprenderás a gestionar múltiples tareas simultáneas, recibir resultados,
 * programar ejecuciones periódicas y crear servidores capaces de atender
 * muchos clientes a la vez.
 *
 * 🔍 Conceptos clave:
 * ----------------------------------------------------
 * ✅ ExecutorService: gestiona grupos de hilos para ejecutar tareas.
 * ✅ Runnable: tareas sin valor de retorno.
 * ✅ Callable + Future: tareas con valor de retorno.
 * ✅ ScheduledExecutorService: tareas programadas (tipo cron).
 * ✅ Servidor TCP multicliente: cada cliente atendido en un hilo distinto.
 * ✅ Thread pools: optimizan el uso de hilos sin crearlos uno a uno.
 * ✅ shutdown(): cierra de forma controlada los servicios de ejecución.
 * ✅ newFixedThreadPool(n): ejecuta hasta n tareas en paralelo.
 * ✅ newCachedThreadPool(): adapta el número de hilos según la carga.
 * ✅ scheduleAtFixedRate(): ejecuta una tarea periódicamente.
 * ✅ ServerSocket / Socket: clases base para redes en Java.
 * ✅ Comunicación cliente-servidor por consola (echo).
 *
 * 🧠 ¿Por qué es importante?
 * ----------------------------------------------------
 * - Permite crear aplicaciones más eficientes y escalables.
 * - Fundamental en servidores, videojuegos, webs, robots, etc.
 * - Simula entornos reales con múltiples usuarios conectados a la vez.
 * - Base para entender frameworks modernos que usan múltiples hilos.
 *
 * Este archivo muestra ejemplos prácticos y comentados línea por línea.
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class UT6_ConcurrenciaAvanzada {

    public static void main(String[] args) throws Exception {
        System.out.println("\n📘 UT6 - Concurrencia avanzada (elige demo):");
        System.out.println("1. Pool de hilos con Runnable");
        System.out.println("2. Callable + Future con retorno");
        System.out.println("3. Tarea programada cada 2s");
        System.out.println("4. Servidor TCP multicliente\n");

        Scanner sc = new Scanner(System.in);
        System.out.print("Elige opción: ");
        int opcion = sc.nextInt();
        sc.close();

        switch (opcion) {
            case 1 -> demoPoolRunnable();
            case 2 -> demoCallable();
            case 3 -> demoTareaProgramada();
            case 4 -> new ServidorTCPConcurrente().start();
            default -> System.out.println("❌ Opción inválida");
        }
    }

    // 🔧 DEMO 1 - Pool de hilos con Runnable
    // ------------------------------------------------------------
    // ▶️ ¿Qué hace esta demo?
    // Esta demo muestra cómo se pueden ejecutar múltiples tareas
    // de forma concurrente utilizando un ThreadPool (grupo de hilos).
    // Las 5 tareas son muy simples: solo imprimen un mensaje,
    // esperan 1 segundo (simulando trabajo) y terminan.
    //
    // ⚙️ ¿Qué aprendemos?
    // - A limitar el número de tareas en ejecución simultánea (3 hilos).
    // - A enviar tareas mediante submit() al ExecutorService.
    // - A simular trabajo con Thread.sleep.
    // - A observar la ejecución no secuencial gracias al pool.
    //
    // 🧠 Este patrón se usa mucho en servidores y procesamiento paralelo.
    public static void demoPoolRunnable() {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        for (int i = 1; i <= 5; i++) {
            final int tarea = i;
            pool.submit(() -> {
                System.out.println("🔨 Ejecutando tarea " + tarea);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                System.out.println("✅ Tarea " + tarea + " finalizada");
            });
        }
        pool.shutdown();
    }

    // 🔧 DEMO 2 - Callable + Future (con valor de retorno)
    // ------------------------------------------------------------
    // ▶️ ¿Qué hace esta demo?
    // Esta demo muestra cómo ejecutar una tarea en un hilo y recuperar
    // un resultado usando Callable y Future. La tarea simula un proceso
    // que tarda 1,5 segundos y luego devuelve un texto como resultado.
    //
    // ⚙️ ¿Qué aprendemos?
    // - A usar Callable para tareas con retorno.
    // - A recuperar el valor con Future.get() (bloqueante).
    // - A ejecutar lógica de negocio en segundo plano y esperar resultados.
    // - A usar un pool dinámico con newCachedThreadPool().
    //
    // 🧠 Este patrón se usa en apps que necesitan obtener respuestas de
    // tareas asincrónicas (consultas, cálculos, operaciones remotas).
    public static void demoCallable() throws Exception {
        ExecutorService pool = Executors.newCachedThreadPool();
        Callable<String> tarea = () -> {
            Thread.sleep(1500);
            return "🎯 Resultado devuelto por Callable";
        };
        Future<String> resultado = pool.submit(tarea);
        System.out.println("⌛ Esperando resultado...");
        System.out.println("📤 Resultado recibido: " + resultado.get());
        pool.shutdown();
    }

    // 🔧 DEMO 3 - Tarea programada cada 2 segundos
    // ------------------------------------------------------------
    // ▶️ ¿Qué hace esta demo?
    // Crea una tarea que se ejecuta automáticamente cada 2 segundos.
    // La tarea imprime la hora de ejecución para simular un servicio periódico.
    //
    // ⚙️ ¿Qué aprendemos?
    // - A usar ScheduledExecutorService.
    // - A programar tareas con scheduleAtFixedRate().
    // - A ejecutar tareas automáticas sin intervención manual.
    // - A controlar cuándo inicia y cada cuánto se repite.
    //
    // 🧠 Se usa en monitoreo, alertas, backups, bots, cron jobs, etc.
    public static void demoTareaProgramada() throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable tarea = () -> System.out.println("⏱️ Ejecutada: " + new Date());
        scheduler.scheduleAtFixedRate(tarea, 0, 2, TimeUnit.SECONDS);
        Thread.sleep(8000);
        scheduler.shutdown();
    }

    // 🔧 DEMO 4 - Servidor TCP que atiende múltiples clientes
    // ------------------------------------------------------------
    // ▶️ ¿Qué hace esta demo?
    // Crea un servidor TCP que escucha en el puerto 5000. Cada vez que
    // un cliente se conecta, se lanza un nuevo hilo que lo atiende por separado.
    // El cliente puede enviar mensajes por consola, y el servidor los devuelve (Echo).
    //
    // ⚙️ ¿Qué aprendemos?
    // - A usar ServerSocket y Socket para conexiones de red.
    // - A crear un hilo por cliente para atenderlos simultáneamente.
    // - A leer y escribir usando PrintWriter y BufferedReader.
    // - A manejar conexiones sin bloquear el servidor principal.
    //
    // 🧠 Este patrón se usa en servidores de chat, juegos en red, servicios TCP reales.
    static class ServidorTCPConcurrente extends Thread {
        public void run() {
            try (ServerSocket server = new ServerSocket(5000)) {
                System.out.println("🖥️ Servidor TCP esperando clientes en puerto 5000...");
                while (true) {
                    Socket cliente = server.accept();
                    new Thread(() -> atender(cliente)).start();
                }
            } catch (IOException e) {
                System.out.println("❗ Error en servidor: " + e.getMessage());
            }
        }

        private void atender(Socket socket) {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                out.println("👋 Bienvenido. Escribe algo:");
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println("📥 [Cliente] " + msg);
                    out.println("🔁 Echo: " + msg);
                }
            } catch (IOException e) {
                System.out.println("🚫 Cliente desconectado");
            }
        }
    }
}

/*
 * 🎯 EJERCICIOS PARA EL ALUMNO:
 * -----------------------------
 * 1️⃣ Cambia el número de hilos del pool a 1 y mide diferencia.
 * 2️⃣ Crea Callable que calcule factorial o cuadrado de un número.
 * 3️⃣ Programa tarea que se ejecute cada minuto exacto.
 * 4️⃣ Simula 20 clientes en paralelo con Thread.
 * 5️⃣ Modifica servidor para cerrar tras 5 clientes atendidos.
 */
